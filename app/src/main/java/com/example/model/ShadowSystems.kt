package com.example.model

data class GkQuestion(
    val id: Int,
    val questionBangla: String,
    val questionEnglish: String,
    val options: List<String>,
    val correctIndex: Int,
    val trivia: String
)

object BanglaGkBank {
    val SAMPLE_QUESTIONS = listOf(
        GkQuestion(
            id = 1,
            questionBangla = "বাংলাদেশের জাতীয় স্মৃতিসৌধ কোন জেলায় অবস্থিত?",
            questionEnglish = "In which district is the National Martyrs' Memorial located?",
            options = listOf("ঢাকা (সাভার)", "গাজীপুর", "নারায়ণগঞ্জ", "মুন্সিগঞ্জ"),
            correctIndex = 0,
            trivia = "সাভারের জাতীয় স্মৃতিসৌধ সৈয়দ মাইনুল হোসেন কর্তৃক নকশাকৃত।"
        ),
        GkQuestion(
            id = 2,
            questionBangla = "পদ্মা সেতুর মোট দৈর্ঘ্য কত কিলোমিটার?",
            questionEnglish = "What is the total length of the Padma Bridge in kilometers?",
            options = listOf("৫.৮ কিমি", "৬.১৫ কিমি", "৭.২ কিমি", "৪.৯ কিমি"),
            correctIndex = 1,
            trivia = "পদ্মা সেতু ৬.১৫ কিলোমিটার দীর্ঘ দ্বিতল বিশিষ্ট বহুমুখী সেতু।"
        ),
        GkQuestion(
            id = 3,
            questionBangla = "সুন্দরবনের মোট আয়তনের কত শতাংশ বাংলাদেশে অবস্থিত?",
            questionEnglish = "What percentage of the Sundarbans forest is situated in Bangladesh?",
            options = listOf("৪০%", "৫০%", "৬০%", "৭০%"),
            correctIndex = 2,
            trivia = "সুন্দরবনের প্রায় ৬০% বাংলাদেশে এবং ৪০% ভারতে অবস্থিত।"
        ),
        GkQuestion(
            id = 4,
            questionBangla = "জাতীয় সংসদ ভবনের স্থপতি কে?",
            questionEnglish = "Who is the chief architect of the National Parliament House?",
            options = listOf("লুই আই কান", "মাজহারুল ইসলাম", "ফজলুর রহমান খান", "হামিদুর রহমান"),
            correctIndex = 0,
            trivia = "আমেরিকান বিশ্বখ্যাত স্থপতি লুই আই কান শের-ই-বাংলা নগরের সংসদ ভবন ডিজাইন করেন।"
        ),
        GkQuestion(
            id = 5,
            questionBangla = "বিশ্বের দীর্ঘতম প্রাকৃতিক সমুদ্র সৈকত কোনটি?",
            questionEnglish = "Which is the longest natural sea beach in the world?",
            options = listOf("কুয়াকাটা", "কক্সবাজার (১২০ কিমি)", "গোয়া", "মায়ামি"),
            correctIndex = 1,
            trivia = "কক্সবাজার সমুদ্র সৈকত প্রায় ১২০ কিমি নিরবচ্ছিন্ন বালুকাময় সৈকত।"
        ),
        GkQuestion(
            id = 6,
            questionBangla = "বাংলাদেশের মুক্তিযুদ্ধের প্রধান সেনাপতি কে ছিলেন?",
            questionEnglish = "Who was the Commander-in-Chief of Bangladesh Armed Forces during Liberation War 1971?",
            options = listOf("জেনারেল এম এ জি ওসমানী", "মেজর জিয়াউর রহমান", "খালেদ মোশাররফ", "কে এম শফিউল্লাহ"),
            correctIndex = 0,
            trivia = "জেনারেল আতাউল গনি ওসমানী মুক্তিযুদ্ধের সর্বাধিনায়ক ছিলেন।"
        ),
        GkQuestion(
            id = 7,
            questionBangla = "ঢাকা বিশ্ববিদ্যালয় কত সালে প্রতিষ্ঠিত হয়?",
            questionEnglish = "In which year was the University of Dhaka established?",
            options = listOf("১৯০৫", "১৯১১", "১৯২১", "১৯৪৭"),
            correctIndex = 2,
            trivia = "১৯২১ সালের ১ জুলাই ঢাকা বিশ্ববিদ্যালয় শিক্ষা কার্যক্রম শুরু করে।"
        )
    )

    fun getRandomQuestion(): GkQuestion = SAMPLE_QUESTIONS.random()
}

data class DailyReward(
    val day: Int,
    val takaAmount: Int,
    val itemName: String,
    val isClaimed: Boolean = false,
    val isCurrent: Boolean = false
)

object DailyRewardSystem {
    val DEFAULT_WEEK = listOf(
        DailyReward(1, 200, "Kunai Darts × 10", isClaimed = true),
        DailyReward(2, 400, "Smoke Bomb × 3", isClaimed = true),
        DailyReward(3, 600, "Armor Plate Tier-2", isCurrent = true),
        DailyReward(4, 800, "AK-BD Ammo × 60"),
        DailyReward(5, 1000, "Gamcha Mask Accessory"),
        DailyReward(6, 1500, "Tiger Pet Food Feast"),
        DailyReward(7, 3000, "Gold Katana Trial Voucher")
    )
}

data class PassTier(
    val level: Int,
    val freeReward: String,
    val goldReward: String,
    val xpRequired: Int = 500,
    val isUnlocked: Boolean = false
)

object ShadowPassSystem {
    val TIERS = (1..50).map { level ->
        PassTier(
            level = level,
            freeReward = when {
                level % 10 == 0 -> "৳ 1,500 Taka + Weapon Skin"
                level % 5 == 0 -> "৳ 500 Taka"
                else -> "Supply Crate × 1"
            },
            goldReward = when (level) {
                1 -> "Golden Ninja Headband"
                10 -> "Gold Katana Neon FX"
                20 -> "Tiger Cub Armor Harness"
                30 -> "Dhaka Skyline Parachute"
                40 -> "Shadow Dash Violet Trail"
                50 -> "MYTHIC: Shadow Master Aura & Finisher"
                else -> "৳ 1,000 Taka + Gold Crate"
            },
            isUnlocked = level <= 6
        )
    }
}

data class ClanInfo(
    val id: String,
    val name: String,
    val leaderName: String,
    val memberCount: Int,
    val maxMembers: Int = 50,
    val totalTrophyPoints: Int,
    val district: String
)

object MohollaClanRegistry {
    val TOP_CLANS = listOf(
        ClanInfo("c1", "Dhanmondi Shadow Blades", "ShadowKing_BD", 48, 50, 18450, "Dhanmondi"),
        ClanInfo("c2", "Mirpur Dojo Masters", "Kensei_Dhaka", 45, 50, 16900, "Mirpur"),
        ClanInfo("c3", "Old Dhaka Biryani Ninjas", "Haji_Shinobi", 50, 50, 15820, "Old Dhaka"),
        ClanInfo("c4", "Uttara Cyber Ronin", "Neon_Ninja99", 42, 50, 14200, "Uttara"),
        ClanInfo("c5", "Gulshan Gold Syndicate", "Taka_Lord", 39, 50, 12600, "Gulshan")
    )
}
