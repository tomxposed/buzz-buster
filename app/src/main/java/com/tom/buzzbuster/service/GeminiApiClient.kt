package com.tom.buzzbuster.service

import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

sealed class GeminiResult {
    data class Success(val regex: String) : GeminiResult()
    data class Error(val message: String) : GeminiResult()
}

object GeminiApiClient {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    private val gson = Gson()

    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent"

    suspend fun generateRegex(apiKey: String, userIntent: String): GeminiResult {
        return withContext(Dispatchers.IO) {
            try {
                val prompt = buildPrompt(userIntent)
                val requestBody = buildRequestBody(prompt)

                val request = Request.Builder()
                    .url("$BASE_URL?key=$apiKey")
                    .post(requestBody.toRequestBody("application/json".toMediaType()))
                    .build()

                val response = client.newCall(request).execute()
                val body = response.body?.string()

                if (!response.isSuccessful || body == null) {
                    return@withContext GeminiResult.Error(
                        "API error: ${response.code} — ${response.message}"
                    )
                }

                parseRegexFromResponse(body)
            } catch (e: Exception) {
                GeminiResult.Error("Network error: ${e.localizedMessage}")
            }
        }
    }

    private fun buildPrompt(userIntent: String): String {
        return """
You are a regex generator for a notification filtering system. 
The user describes which notifications they want to BLOCK. 
Generate a single regex pattern that matches notification text (title + content) for the described intent.

Rules:
- Output ONLY the regex pattern, nothing else.
- Use case-insensitive matching (the system applies (?i) flag separately).
- Make the regex practical and not overly broad.
- Use alternation (|) for multiple concepts.
- Keep it concise but effective.

User intent: "$userIntent"

Regex pattern:
        """.trimIndent()
    }

    private fun buildRequestBody(prompt: String): String {
        val json = JsonObject().apply {
            add("contents", gson.toJsonTree(listOf(
                mapOf("parts" to listOf(mapOf("text" to prompt)))
            )))
            add("generationConfig", gson.toJsonTree(mapOf(
                "temperature" to 0.2,
                "maxOutputTokens" to 256
            )))
        }
        return gson.toJson(json)
    }

    private fun parseRegexFromResponse(responseBody: String): GeminiResult {
        return try {
            val json = gson.fromJson(responseBody, JsonObject::class.java)
            val candidates = json.getAsJsonArray("candidates")
            if (candidates == null || candidates.size() == 0) {
                return GeminiResult.Error("No response from AI")
            }
            val content = candidates[0].asJsonObject
                .getAsJsonObject("content")
                .getAsJsonArray("parts")[0].asJsonObject
                .get("text").asString
                .trim()
                .removeSurrounding("```")
                .removeSurrounding("`")
                .trim()

            // Validate it's a valid regex
            try {
                Regex(content)
                GeminiResult.Success(content)
            } catch (_: Exception) {
                // Try to clean common issues
                val cleaned = content.lines().first().trim()
                try {
                    Regex(cleaned)
                    GeminiResult.Success(cleaned)
                } catch (_: Exception) {
                    GeminiResult.Error("AI returned invalid regex: $content")
                }
            }
        } catch (e: Exception) {
            GeminiResult.Error("Failed to parse AI response: ${e.localizedMessage}")
        }
    }
}
