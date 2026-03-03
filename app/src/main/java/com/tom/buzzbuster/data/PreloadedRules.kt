package com.tom.buzzbuster.data

import com.tom.buzzbuster.data.model.FilterType

/**
 * Preloaded spam filter rules for popular Indian apps.
 * Each entry maps a package name to a list of rule definitions.
 * Rules are inserted only if the app is installed on the user's device.
 */
data class PreloadedRuleDefinition(
    val name: String,
    val filterType: FilterType,
    val pattern: String
)

object PreloadedRules {

    /** packageName → list of rule definitions */
    val APP_RULES: Map<String, List<PreloadedRuleDefinition>> = mapOf(

        // ═══════════════════════════════════════════════════════════════
        //  E-COMMERCE
        // ═══════════════════════════════════════════════════════════════

        // ── Myntra ────────────────────────────────────────────────────
        "com.myntra.android" to listOf(
            PreloadedRuleDefinition(
                name = "Myntra — Promotions & Sales",
                filterType = FilterType.REGEX,
                pattern = "(?i)(?:sale|offer|discount|deal|clearance|flash|mega|end of reason|eors|coupon|voucher|cashback|reward|% ?off|₹\\d+\\s*off|flat \\d+|buy \\d+ get|extra \\d+%|limited.?time|hurry|don'?t miss|last chance|exclusive|special price|price drop|lowest price|steal|grab|shop now|check.?out now|trending|bestseller|style.?alert)"
            ),
            PreloadedRuleDefinition(
                name = "Myntra — Wishlist & Cart Nudges",
                filterType = FilterType.REGEX,
                pattern = "(?i)(?:wishlist.?(?:item|alert|price)|back in stock|almost sold out|selling fast|cart.?(?:reminder|expir|miss)|price.?(?:dropped|alert|reduced)|running low|few left|last few)"
            )
        ),

        // ── Ajio ──────────────────────────────────────────────────────
        "com.ril.ajio" to listOf(
            PreloadedRuleDefinition(
                name = "Ajio — Promotions & Sales",
                filterType = FilterType.REGEX,
                pattern = "(?i)(?:sale|offer|discount|deal|clearance|coupon|cashback|reward|% ?off|₹\\d+\\s*off|flat \\d+|buy \\d+ get|extra \\d+%|limited.?time|hurry|don'?t miss|last chance|big.?bold|all.?stars|price crash|mega|steal|shop now|check it out|trending|explore now|brand.?day)"
            ),
            PreloadedRuleDefinition(
                name = "Ajio — Cart & Wishlist Nudges",
                filterType = FilterType.REGEX,
                pattern = "(?i)(?:wishlist|back in stock|selling fast|almost gone|cart.?(?:reminder|expir|miss)|price.?drop|few left|low stock)"
            )
        ),

        // ── Nykaa (Beauty) ────────────────────────────────────────────
        "com.fsn.nykaa" to listOf(
            PreloadedRuleDefinition(
                name = "Nykaa — Promotions & Sales",
                filterType = FilterType.REGEX,
                pattern = "(?i)(?:sale|offer|discount|deal|coupon|cashback|reward|% ?off|₹\\d+\\s*off|flat \\d+|buy \\d+ get|extra \\d+%|limited.?time|hurry|pink friday|hot pink|nykaa.?sale|bestseller|trending|new launch|just dropped|must.?have|shop now|grab now|beauty.?(?:fest|bonanza|steal))"
            )
        ),

        // ── Nykaa Fashion ─────────────────────────────────────────────
        "com.nykaa.nykaafashion" to listOf(
            PreloadedRuleDefinition(
                name = "Nykaa Fashion — Promotions",
                filterType = FilterType.REGEX,
                pattern = "(?i)(?:sale|offer|discount|deal|coupon|cashback|% ?off|₹\\d+\\s*off|flat \\d+|buy \\d+ get|extra \\d+%|limited.?time|hurry|style.?(?:fest|sale|steal)|new arrival|trending|shop now|grab|don'?t miss|price drop)"
            )
        ),

        // ── Meesho ────────────────────────────────────────────────────
        "com.meesho.supply" to listOf(
            PreloadedRuleDefinition(
                name = "Meesho — Promotions & Sales",
                filterType = FilterType.REGEX,
                pattern = "(?i)(?:sale|offer|discount|deal|coupon|cashback|% ?off|₹\\d+\\s*off|flat \\d+|buy \\d+ get|extra \\d+%|lowest price|mega.?sale|hurry|shop now|order now|free delivery|limited.?time|don'?t miss|price drop|flash)"
            ),
            PreloadedRuleDefinition(
                name = "Meesho — Reseller Nudges",
                filterType = FilterType.REGEX,
                pattern = "(?i)(?:share.?(?:and|&)?.?earn|resell|margin|commission|supplier|catalogue|new product|start selling)"
            )
        ),

        // ── Shopsy ────────────────────────────────────────────────────
        "com.shopsy.android" to listOf(
            PreloadedRuleDefinition(
                name = "Shopsy — Promotions & Sales",
                filterType = FilterType.REGEX,
                pattern = "(?i)(?:sale|offer|discount|deal|coupon|cashback|% ?off|₹\\d+\\s*off|flat \\d+|buy \\d+ get|extra \\d+%|lowest price|hurry|shop now|order now|free delivery|limited.?time|don'?t miss|price crash|mega|starting.?₹)"
            )
        ),

        // ── Amazon India ──────────────────────────────────────────────
        "in.amazon.mShop.android.shopping" to listOf(
            PreloadedRuleDefinition(
                name = "Amazon — Promotions & Sales",
                filterType = FilterType.REGEX,
                pattern = "(?i)(?:sale|offer|discount|deal|lightning|prime.?day|great indian|coupon|cashback|reward|% ?off|₹\\d+\\s*off|flat \\d+|buy \\d+ get|extra \\d+%|limited.?time|hurry|don'?t miss|last chance|price drop|lowest price|shop now|check.?out|save \\d+%|today'?s deal|special offer)"
            ),
            PreloadedRuleDefinition(
                name = "Amazon — Cart & Wishlist Nudges",
                filterType = FilterType.REGEX,
                pattern = "(?i)(?:wishlist|back in stock|price.?(?:drop|alert|reduced)|cart.?(?:reminder|expir)|almost sold|only \\d+ left|selling fast|few left|saved item)"
            )
        ),

        // ── Flipkart ──────────────────────────────────────────────────
        "com.flipkart.android" to listOf(
            PreloadedRuleDefinition(
                name = "Flipkart — Promotions & Sales",
                filterType = FilterType.REGEX,
                pattern = "(?i)(?:sale|offer|discount|deal|big.?billion|big.?saving|big.?bachat|coupon|cashback|reward|% ?off|₹\\d+\\s*off|flat \\d+|buy \\d+ get|extra \\d+%|limited.?time|hurry|don'?t miss|last chance|price drop|lowest price|shop now|grab|steal|supercoins|coin)"
            ),
            PreloadedRuleDefinition(
                name = "Flipkart — Cart & Wishlist Nudges",
                filterType = FilterType.REGEX,
                pattern = "(?i)(?:wishlist|back in stock|price.?(?:drop|alert|reduced)|cart.?(?:reminder|expir)|almost sold|only \\d+ left|selling fast|few left|saved item)"
            )
        ),

        // ── Bewakoof ──────────────────────────────────────────────────
        "com.bewakoof.bewakoof" to listOf(
            PreloadedRuleDefinition(
                name = "Bewakoof — Promotions & Sales",
                filterType = FilterType.REGEX,
                pattern = "(?i)(?:sale|offer|discount|deal|coupon|cashback|% ?off|₹\\d+\\s*off|flat \\d+|buy \\d+ get|extra \\d+%|limited.?time|hurry|don'?t miss|new drop|trending|bestseller|shop now|grab|steal|free shipping|tribe.?(?:member|offer))"
            )
        ),

        // ═══════════════════════════════════════════════════════════════
        //  FOOD DELIVERY & GROCERY
        // ═══════════════════════════════════════════════════════════════

        // ── Zomato ────────────────────────────────────────────────────
        "com.application.zomato" to listOf(
            PreloadedRuleDefinition(
                name = "Zomato — Promotions & Offers",
                filterType = FilterType.REGEX,
                pattern = "(?i)(?:% ?off|₹\\d+\\s*off|flat \\d+|coupon|code|cashback|free delivery|buy \\d+ get|extra \\d+%|limited.?time|hurry|don'?t miss|order now|last chance|use code|apply|craving|hungry|exclusive|try now|feast|bogo)"
            ),
            PreloadedRuleDefinition(
                name = "Zomato — Engagement Nudges",
                filterType = FilterType.REGEX,
                pattern = "(?i)(?:miss.?(?:us|you)|been a while|come back|it'?s been|we miss|top pick|recommended|popular near|trending near|your favorite|re-?order)"
            )
        ),

        // ── Swiggy ────────────────────────────────────────────────────
        "in.swiggy.android" to listOf(
            PreloadedRuleDefinition(
                name = "Swiggy — Promotions & Offers",
                filterType = FilterType.REGEX,
                pattern = "(?i)(?:% ?off|₹\\d+\\s*off|flat \\d+|coupon|code|cashback|free delivery|buy \\d+ get|extra \\d+%|limited.?time|hurry|don'?t miss|order now|last chance|use code|apply|craving|exclusive|super.?(?:deal|saver)|instamart|bogo)"
            ),
            PreloadedRuleDefinition(
                name = "Swiggy — Engagement Nudges",
                filterType = FilterType.REGEX,
                pattern = "(?i)(?:miss.?(?:us|you)|been a while|come back|it'?s been|we miss|recommended|popular near|trending|your favorite|re-?order)"
            )
        ),

        // ── Blinkit ───────────────────────────────────────────────────
        "com.grofers.customerapp" to listOf(
            PreloadedRuleDefinition(
                name = "Blinkit — Promotions & Offers",
                filterType = FilterType.REGEX,
                pattern = "(?i)(?:% ?off|₹\\d+\\s*off|flat \\d+|coupon|cashback|free delivery|buy \\d+ get|extra \\d+%|hurry|don'?t miss|order now|limited.?time|use code|super.?saver|deal|lowest price|stock up|grab now|minutes)"
            )
        ),

        // ── Zepto ─────────────────────────────────────────────────────
        "com.zeptoconsumerapp" to listOf(
            PreloadedRuleDefinition(
                name = "Zepto — Promotions & Offers",
                filterType = FilterType.REGEX,
                pattern = "(?i)(?:% ?off|₹\\d+\\s*off|flat \\d+|coupon|cashback|free delivery|buy \\d+ get|extra \\d+%|hurry|don'?t miss|order now|limited.?time|use code|deal|lowest price|stock up|grab now|minutes|super.?saver)"
            )
        ),

        // ── EatSure ───────────────────────────────────────────────────
        "com.done.faasos" to listOf(
            PreloadedRuleDefinition(
                name = "EatSure — Promotions & Offers",
                filterType = FilterType.REGEX,
                pattern = "(?i)(?:% ?off|₹\\d+\\s*off|flat \\d+|coupon|cashback|free delivery|buy \\d+ get|extra \\d+%|hurry|don'?t miss|order now|limited.?time|use code|deal|craving|try now|combo|bogo)"
            )
        ),

        // ── BigBasket ─────────────────────────────────────────────────
        "com.bigbasket.mobileapp" to listOf(
            PreloadedRuleDefinition(
                name = "BigBasket — Promotions & Offers",
                filterType = FilterType.REGEX,
                pattern = "(?i)(?:% ?off|₹\\d+\\s*off|flat \\d+|coupon|cashback|free delivery|buy \\d+ get|extra \\d+%|limited.?time|hurry|don'?t miss|order now|use code|deal|lowest price|stock up|grab now|mega.?save|bb.?(?:star|plus|special))"
            )
        ),

        // ═══════════════════════════════════════════════════════════════
        //  FINTECH & PAYMENTS
        // ═══════════════════════════════════════════════════════════════

        // ── CRED ──────────────────────────────────────────────────────
        "com.dreamplug.androidapp" to listOf(
            PreloadedRuleDefinition(
                name = "CRED — Promotions & Rewards",
                filterType = FilterType.REGEX,
                pattern = "(?i)(?:reward|cashback|coins|voucher|offer|deal|win|earn|jackpot|spin|scratch|exclusive|privilege|cred.?(?:mint|store|cash)|claim|redeem|unlock|limited.?time|don'?t miss|hurry|% ?off|₹\\d+\\s*off)"
            )
        ),

        // ── Paytm ─────────────────────────────────────────────────────
        "net.one97.paytm" to listOf(
            PreloadedRuleDefinition(
                name = "Paytm — Promotions & Offers",
                filterType = FilterType.REGEX,
                pattern = "(?i)(?:cashback|reward|offer|deal|coupon|voucher|% ?off|₹\\d+\\s*off|flat \\d+|buy \\d+ get|extra \\d+%|limited.?time|hurry|don'?t miss|promo|use code|apply|scratch|win|spin|lucky|mall|shop|postpaid|loan|gold|assured)"
            ),
            PreloadedRuleDefinition(
                name = "Paytm — Lending & Insurance Spam",
                filterType = FilterType.REGEX,
                pattern = "(?i)(?:pre.?approved|loan|credit.?(?:line|limit|score)|emi|insurance|mutual fund|invest|fixed deposit|fd |sip |gold (?:offer|invest)|apply now|eligible|congratulations.*(?:loan|credit|limit))"
            )
        ),

        // ── PhonePe ───────────────────────────────────────────────────
        "com.phonepe.app" to listOf(
            PreloadedRuleDefinition(
                name = "PhonePe — Promotions & Offers",
                filterType = FilterType.REGEX,
                pattern = "(?i)(?:cashback|reward|offer|deal|coupon|voucher|% ?off|₹\\d+\\s*off|flat \\d+|extra \\d+%|limited.?time|hurry|don'?t miss|scratch|win|spin|lucky|use code|switch.?(?:to|&)|recharge offer|bill.?pay offer)"
            ),
            PreloadedRuleDefinition(
                name = "PhonePe — Lending & Insurance Spam",
                filterType = FilterType.REGEX,
                pattern = "(?i)(?:pre.?approved|loan|credit.?(?:line|limit)|emi|insurance|mutual fund|invest|tax.?sav|sip |apply now|eligible|congratulations.*(?:loan|credit|limit))"
            )
        ),

        // ── Slice ─────────────────────────────────────────────────────
        "com.slicepay" to listOf(
            PreloadedRuleDefinition(
                name = "Slice — Promotions & Offers",
                filterType = FilterType.REGEX,
                pattern = "(?i)(?:cashback|reward|offer|deal|% ?off|₹\\d+\\s*off|flat \\d+|extra \\d+%|limited.?time|hurry|don'?t miss|sparkle|spend|earn|win|shop|pay.?later|boost|upgrade|limit.?(?:increase|enhanced))"
            )
        ),

        // ── Jupiter ───────────────────────────────────────────────────
        "com.jupiter.money" to listOf(
            PreloadedRuleDefinition(
                name = "Jupiter — Promotions & Nudges",
                filterType = FilterType.REGEX,
                pattern = "(?i)(?:cashback|reward|offer|deal|% ?off|₹\\d+\\s*off|flat \\d+|extra \\d+%|limited.?time|hurry|don'?t miss|earn|save|pot|edge.?(?:upi|account)|pro.?(?:plan|account)|upgrade|refer)"
            )
        ),

        // ── Freecharge ────────────────────────────────────────────────
        "com.freecharge.android" to listOf(
            PreloadedRuleDefinition(
                name = "Freecharge — Promotions & Offers",
                filterType = FilterType.REGEX,
                pattern = "(?i)(?:cashback|reward|offer|deal|coupon|% ?off|₹\\d+\\s*off|flat \\d+|extra \\d+%|limited.?time|hurry|don'?t miss|promo|use code|recharge|bill.?pay|win|lucky|exclusive)"
            )
        ),

        // ── MobiKwik ──────────────────────────────────────────────────
        "com.mobikwik_new" to listOf(
            PreloadedRuleDefinition(
                name = "MobiKwik — Promotions & Offers",
                filterType = FilterType.REGEX,
                pattern = "(?i)(?:cashback|reward|offer|deal|coupon|% ?off|₹\\d+\\s*off|flat \\d+|extra \\d+%|limited.?time|hurry|don'?t miss|supercash|zip|boost|recharge|bill.?pay|use code|promo|loan|credit|emi)"
            )
        ),

        // ═══════════════════════════════════════════════════════════════
        //  TRAVEL & ENTERTAINMENT
        // ═══════════════════════════════════════════════════════════════

        // ── MakeMyTrip ────────────────────────────────────────────────
        "com.makemytrip" to listOf(
            PreloadedRuleDefinition(
                name = "MakeMyTrip — Promotions & Offers",
                filterType = FilterType.REGEX,
                pattern = "(?i)(?:sale|offer|discount|deal|coupon|cashback|% ?off|₹\\d+\\s*off|flat \\d+|extra \\d+%|limited.?time|hurry|don'?t miss|last chance|price drop|lowest fare|use code|promo|getaway|trip|travel.?(?:sale|deal|fest)|grab|exclusive|instant discount)"
            ),
            PreloadedRuleDefinition(
                name = "MakeMyTrip — Engagement Nudges",
                filterType = FilterType.REGEX,
                pattern = "(?i)(?:plan.?(?:your|a|next)|explore|destination|weekend|holiday|vacation|trending.?dest|popular.?route|price alert|fare drop|been a while|wanderlust)"
            )
        ),

        // ── Goibibo ───────────────────────────────────────────────────
        "com.goibibo" to listOf(
            PreloadedRuleDefinition(
                name = "Goibibo — Promotions & Offers",
                filterType = FilterType.REGEX,
                pattern = "(?i)(?:sale|offer|discount|deal|coupon|cashback|% ?off|₹\\d+\\s*off|flat \\d+|extra \\d+%|limited.?time|hurry|don'?t miss|last chance|lowest fare|price drop|use code|promo|go.?(?:cash|tribe|deal)|grab|exclusive|instant discount)"
            ),
            PreloadedRuleDefinition(
                name = "Goibibo — Engagement Nudges",
                filterType = FilterType.REGEX,
                pattern = "(?i)(?:plan.?(?:your|a|next)|explore|destination|weekend|holiday|vacation|trending|popular.?route|price alert|fare drop|been a while)"
            )
        ),

        // ── BookMyShow ────────────────────────────────────────────────
        "com.bt.bms" to listOf(
            PreloadedRuleDefinition(
                name = "BookMyShow — Promotions & Offers",
                filterType = FilterType.REGEX,
                pattern = "(?i)(?:offer|discount|deal|coupon|cashback|% ?off|₹\\d+\\s*off|flat \\d+|extra \\d+%|limited.?time|hurry|don'?t miss|last chance|use code|promo|grab|exclusive|bogo|buy \\d+ get)"
            ),
            PreloadedRuleDefinition(
                name = "BookMyShow — Event Recommendations",
                filterType = FilterType.REGEX,
                pattern = "(?i)(?:trending|now showing|new release|just launched|coming soon|recommended|popular near|book now|seats filling|almost full|last few seats|watch now|don'?t miss this)"
            )
        ),

        // ── EaseMyTrip ────────────────────────────────────────────────
        "com.emt.android" to listOf(
            PreloadedRuleDefinition(
                name = "EaseMyTrip — Promotions & Offers",
                filterType = FilterType.REGEX,
                pattern = "(?i)(?:sale|offer|discount|deal|coupon|cashback|% ?off|₹\\d+\\s*off|flat \\d+|extra \\d+%|limited.?time|hurry|don'?t miss|last chance|lowest fare|price drop|use code|promo|grab|exclusive|instant discount|no.?(?:fee|charge|markup))"
            )
        )
    )
}
