package com.example.data

data class KidAvatar(
    val id: String,
    val name: String,
    val emoji: String,
    val colorHex: Long,
    val title: String
)

val AVAILABLE_AVATARS = listOf(
    KidAvatar("lion", "Leo Lion", "🦁", 0xFFFFAA00, "Brave Explorer"),
    KidAvatar("bunny", "Luna Bunny", "🐰", 0xFFFF7675, "Speedy Hopper"),
    KidAvatar("dino", "Sparky Dino", "🦖", 0xFF00B894, "Mighty Thinker"),
    KidAvatar("penguin", "Pip Penguin", "🐧", 0xFF0984E3, "Cool Solver"),
    KidAvatar("puppy", "Milo Puppy", "🐶", 0xFFE17055, "Happy Friend"),
    KidAvatar("robot", "Cosmo Bot", "🤖", 0xFF6C5CE7, "Tech Genius"),
    KidAvatar("star", "Stella Star", "🌟", 0xFFFDCB6E, "Shining Star"),
    KidAvatar("kitten", "Daisy Kitten", "🐱", 0xFFFD79A8, "Curious Paw")
)

data class Badge(
    val id: String,
    val title: String,
    val description: String,
    val iconEmoji: String,
    val requiredStars: Int
)

val APP_BADGES = listOf(
    Badge("first_step", "First Explorer", "Welcome to Kids Puzzle World!", "🌟", 0),
    Badge("phonics_master", "Phonics Hero", "Learned 10 Phonics letters", "🔤", 15),
    Badge("puzzle_whiz", "Puzzle Wizard", "Completed 5 Jigsaw & Shape Puzzles", "🧩", 30),
    Badge("quiz_champion", "Quiz Champion", "Scored high in Kids Quiz", "🏆", 50),
    Badge("memory_ace", "Memory Ace", "Matched cards in Memory Game", "🧠", 75),
    Badge("artist_spark", "Magic Artist", "Created colorful rainbow drawings", "🎨", 100),
    Badge("grand_master", "Grand Superstar", "Earned 200+ Bright Stars", "👑", 200)
)

data class KidProfile(
    val name: String = "Little Explorer",
    val age: Int = 4,
    val avatarId: String = "lion",
    val stars: Int = 20,
    val streakDays: Int = 1,
    val lastActiveDate: String = "",
    val puzzlesSolved: Int = 0,
    val quizzesCompleted: Int = 0,
    val phonicsLearned: Int = 0,
    val balloonPops: Int = 0,
    val badgesUnlocked: Set<String> = setOf("first_step"),
    val passportCode: String = "KP-7892-STAR",
    val isOnboarded: Boolean = false
) {
    val level: Int
        get() = maxOf(1, 1 + stars / 25)

    val currentAvatar: KidAvatar
        get() = AVAILABLE_AVATARS.find { it.id == avatarId } ?: AVAILABLE_AVATARS.first()

    val rankTitle: String
        get() = when {
            stars >= 250 -> "Grand Champion 👑"
            stars >= 150 -> "Cosmic Master 🚀"
            stars >= 100 -> "Puzzle Wizard 🧙"
            stars >= 50 -> "Star Explorer ⭐"
            stars >= 20 -> "Clever Cub 🐾"
            else -> "Little Sprout 🌱"
        }
}

data class LeaderboardEntry(
    val rank: Int,
    val name: String,
    val avatarEmoji: String,
    val stars: Int,
    val title: String,
    val isCurrentUser: Boolean = false
)
