<p align="center">
  <img src="app/src/main/res/mipmap-xxxhdpi/ic_launcher.png" width="120" alt="BuzzBuster Logo"/>
</p>

<h1 align="center">BuzzBuster</h1>

<p align="center">
  <b>Intelligent spam notification blocker for Android</b><br/>
  <i>Take back control of your notification shade.</i>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Platform-Android-brightgreen?logo=android" alt="Platform"/>
  <img src="https://img.shields.io/badge/Min%20SDK-26-blue" alt="Min SDK"/>
  <img src="https://img.shields.io/badge/Language-Kotlin-purple?logo=kotlin" alt="Kotlin"/>
  <img src="https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4?logo=jetpackcompose" alt="Jetpack Compose"/>
  <img src="https://img.shields.io/badge/AI-Gemini%20API-FF6F00?logo=google" alt="Gemini"/>
</p>

---

## ✨ Features

### 🛡️ Multi-Tier Notification Filtering
BuzzBuster uses a layered approach to catch spam notifications before they reach you:

| Tier | Method | Description |
|------|--------|-------------|
| **Tier 1** | String Match | Simple keyword-based filtering (e.g., "limited offer", "flash sale") |
| **Tier 2** | Regex | Powerful pattern matching for complex spam patterns |
| **Tier 3** | AI-Generated | Describe what to block in plain English — Gemini AI generates the regex for you |

### 🤖 AI-Powered Rule Creation
Don't know regex? No problem. Just describe what you want to block:
> *"Block all loan and credit card promotional SMS"*

BuzzBuster uses the **Gemini API** to automatically generate the optimal regex pattern.

### 📱 App-Specific Rules
- Target rules to **specific apps** or apply them **globally**
- **Multi-app selection** — apply a single rule across multiple apps
- Searchable app picker with all installed apps

### 📊 Blocking History
- Full log of every blocked notification with timestamps
- Grouped by date (Today, Yesterday, Last Monday, etc.)
- Search through blocked notification history
- Select and manage blocked entries

### 🎨 Beautiful UI
- Modern **Material 3** design with dynamic theming
- **Dark mode** support (System / Light / Dark)
- Custom animated toggle switch with haptic feedback
- Smooth transitions and micro-animations
- Pill-shaped bottom navigation bar

---

## 🏗️ Architecture

```
com.tom.buzzbuster/
├── data/
│   ├── model/
│   │   ├── FilterRule.kt          # Rule entity (name, type, pattern, target apps)
│   │   └── BlockedNotification.kt # Blocked notification log entity
│   ├── dao/
│   │   ├── FilterRuleDao.kt       # Room DAO for filter rules
│   │   └── BlockedNotificationDao.kt
│   ├── AppDatabase.kt             # Room database with seed data
│   ├── BuzzBusterRepository.kt    # Single source of truth
│   └── PreferencesManager.kt      # DataStore preferences
├── service/
│   ├── NotificationInterceptorService.kt  # NotificationListenerService
│   ├── FilterEngine.kt            # Multi-tier filtering logic
│   └── GeminiApiClient.kt         # Gemini API integration
├── ui/
│   ├── screens/
│   │   ├── HomeScreen.kt          # Dashboard with stats & quick actions
│   │   ├── RulesScreen.kt         # Rule management list
│   │   ├── RuleEditorSheet.kt     # Create/edit rules bottom sheet
│   │   ├── HistoryScreen.kt       # Blocked notification history
│   │   └── SettingsScreen.kt      # App settings & preferences
│   ├── viewmodel/                 # ViewModels for each screen
│   ├── components/                # Reusable UI components
│   ├── theme/                     # Material 3 theming
│   └── BuzzBusterApp.kt           # Navigation & app shell
└── MainActivity.kt
```

---

## 🛠️ Tech Stack

| Component | Technology |
|-----------|-----------|
| **Language** | Kotlin |
| **UI Framework** | Jetpack Compose + Material 3 |
| **Navigation** | Navigation Compose |
| **Database** | Room (SQLite) |
| **Preferences** | DataStore |
| **Networking** | OkHttp |
| **JSON** | Gson |
| **AI** | Google Gemini API |
| **Async** | Kotlin Coroutines + Flow |
| **Architecture** | MVVM |

---

## 🚀 Getting Started

### Prerequisites
- Android Studio Ladybug or later
- Android SDK 26+ (Android 8.0 Oreo)
- A Gemini API key (optional, for AI rule generation)

### Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/tomxposed/buzz-buster.git
   cd buzz-buster
   ```

2. **Open in Android Studio**
   - Open the project and let Gradle sync

3. **Build & Run**
   - Connect an Android device or start an emulator
   - Click **Run** ▶️

4. **Grant Notification Access**
   - On first launch, BuzzBuster will prompt you to enable **Notification Listener** access in Settings
   - This is required for the app to intercept and filter notifications

5. **Set up Gemini API** *(optional)*
   - Go to **Settings → AI Configuration → API Key**
   - Enter your [Gemini API key](https://aistudio.google.com/apikey)
   - This enables the AI-powered rule generation feature

---

## 📱 How It Works

1. **BuzzBuster runs as a Notification Listener Service** — it sees all incoming notifications
2. When a notification arrives, the **FilterEngine** evaluates it against your enabled rules
3. If a rule matches, the notification is **automatically dismissed** and logged to history
4. You can review blocked notifications anytime and restore false positives

---

## 🤝 Contributing

Contributions are welcome! Feel free to:

- 🐛 Report bugs via [Issues](https://github.com/tomxposed/buzz-buster/issues)
- 💡 Suggest features
- 🔧 Submit pull requests

---

## 📄 License

This project is open source and available under the [MIT License](LICENSE).

---

<p align="center">
  Made with ❤️ by <a href="https://github.com/tomxposed">tomxposed</a>
</p>

