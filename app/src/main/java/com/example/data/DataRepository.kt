package com.example.data

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DataRepository(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("kids_puzzle_save_vault", Context.MODE_PRIVATE)

    private val _profile = MutableStateFlow(loadProfile())
    val profile: StateFlow<KidProfile> = _profile.asStateFlow()

    init {
        checkDailyStreak()
    }

    private fun loadProfile(): KidProfile {
        val name = prefs.getString("name", "") ?: ""
        val isOnboarded = prefs.getBoolean("is_onboarded", false)
        val age = prefs.getInt("age", 4)
        val avatarId = prefs.getString("avatar_id", "lion") ?: "lion"
        val stars = prefs.getInt("stars", 15)
        val streak = prefs.getInt("streak_days", 1)
        val lastDate = prefs.getString("last_date", "") ?: ""
        val puzzlesSolved = prefs.getInt("puzzles_solved", 0)
        val quizzesCompleted = prefs.getInt("quizzes_completed", 0)
        val phonicsLearned = prefs.getInt("phonics_learned", 0)
        val balloonPops = prefs.getInt("balloon_pops", 0)
        val badgesJson = prefs.getString("badges", "[\"first_step\"]") ?: "[\"first_step\"]"
        val passportCode = prefs.getString("passport_code", generatePassportCode(name.ifEmpty { "Hero" })) ?: "KP-7892-STAR"

        val badgesSet = mutableSetOf<String>()
        try {
            val jsonArray = JSONArray(badgesJson)
            for (i in 0 until jsonArray.length()) {
                badgesSet.add(jsonArray.getString(i))
            }
        } catch (_: Exception) {
            badgesSet.add("first_step")
        }

        return KidProfile(
            name = name.ifEmpty { "Little Explorer" },
            age = age,
            avatarId = avatarId,
            stars = stars,
            streakDays = streak,
            lastActiveDate = lastDate,
            puzzlesSolved = puzzlesSolved,
            quizzesCompleted = quizzesCompleted,
            phonicsLearned = phonicsLearned,
            balloonPops = balloonPops,
            badgesUnlocked = badgesSet,
            passportCode = passportCode,
            isOnboarded = isOnboarded
        )
    }

    private fun saveProfile(p: KidProfile) {
        val badgesArray = JSONArray()
        p.badgesUnlocked.forEach { badgesArray.put(it) }

        prefs.edit()
            .putString("name", p.name)
            .putBoolean("is_onboarded", p.isOnboarded)
            .putInt("age", p.age)
            .putString("avatar_id", p.avatarId)
            .putInt("stars", p.stars)
            .putInt("streak_days", p.streakDays)
            .putString("last_date", p.lastActiveDate)
            .putInt("puzzles_solved", p.puzzlesSolved)
            .putInt("quizzes_completed", p.quizzesCompleted)
            .putInt("phonics_learned", p.phonicsLearned)
            .putInt("balloon_pops", p.balloonPops)
            .putString("badges", badgesArray.toString())
            .putString("passport_code", p.passportCode)
            .apply()

        _profile.value = p
    }

    fun completeOnboarding(name: String, age: Int, avatarId: String) {
        val passport = generatePassportCode(name)
        val updated = _profile.value.copy(
            name = name.trim().ifEmpty { "Super Kid" },
            age = age,
            avatarId = avatarId,
            isOnboarded = true,
            passportCode = passport
        )
        saveProfile(updated)
    }

    fun updateProfile(name: String, age: Int, avatarId: String) {
        val updated = _profile.value.copy(
            name = name.trim().ifEmpty { _profile.value.name },
            age = age,
            avatarId = avatarId
        )
        saveProfile(updated)
    }

    fun addStars(amount: Int, category: String = "") {
        val current = _profile.value
        val newStars = current.stars + amount

        // Check new badges
        val newBadges = current.badgesUnlocked.toMutableSet()
        APP_BADGES.forEach { badge ->
            if (newStars >= badge.requiredStars) {
                newBadges.add(badge.id)
            }
        }

        var pSolved = current.puzzlesSolved
        var qCompleted = current.quizzesCompleted
        var phonicsCount = current.phonicsLearned
        var bPops = current.balloonPops

        when (category) {
            "puzzle" -> pSolved += 1
            "quiz" -> qCompleted += 1
            "phonics" -> phonicsCount += 1
            "balloon" -> bPops += amount
        }

        val updated = current.copy(
            stars = newStars,
            badgesUnlocked = newBadges,
            puzzlesSolved = pSolved,
            quizzesCompleted = qCompleted,
            phonicsLearned = phonicsCount,
            balloonPops = bPops
        )
        saveProfile(updated)
    }

    private fun checkDailyStreak() {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
        val current = _profile.value
        if (current.lastActiveDate.isEmpty()) {
            saveProfile(current.copy(lastActiveDate = today, streakDays = 1))
            return
        }
        if (current.lastActiveDate != today) {
            // Check if yesterday or older
            val newStreak = current.streakDays + 1
            saveProfile(current.copy(lastActiveDate = today, streakDays = newStreak))
        }
    }

    // Exportable backup string (Base64 JSON)
    fun exportBackupPayload(): String {
        val p = _profile.value
        val json = JSONObject().apply {
            put("v", 1)
            put("name", p.name)
            put("age", p.age)
            put("avatarId", p.avatarId)
            put("stars", p.stars)
            put("streakDays", p.streakDays)
            put("puzzlesSolved", p.puzzlesSolved)
            put("quizzesCompleted", p.quizzesCompleted)
            put("phonicsLearned", p.phonicsLearned)
            put("balloonPops", p.balloonPops)
            put("passportCode", p.passportCode)
            val badges = JSONArray()
            p.badgesUnlocked.forEach { badges.put(it) }
            put("badges", badges)
        }
        return Base64.encodeToString(json.toString().toByteArray(Charsets.UTF_8), Base64.NO_WRAP)
    }

    // Restore from backup string or passport code
    fun restoreFromBackup(input: String): Boolean {
        try {
            val clean = input.trim()
            if (clean.isEmpty()) return false

            // Try decoding Base64 JSON
            val decodedStr = try {
                val bytes = Base64.decode(clean, Base64.DEFAULT)
                String(bytes, Charsets.UTF_8)
            } catch (_: Exception) {
                clean
            }

            if (decodedStr.startsWith("{") && decodedStr.endsWith("}")) {
                val json = JSONObject(decodedStr)
                val badgesSet = mutableSetOf<String>()
                if (json.has("badges")) {
                    val arr = json.getJSONArray("badges")
                    for (i in 0 until arr.length()) badgesSet.add(arr.getString(i))
                } else {
                    badgesSet.add("first_step")
                }

                val restored = KidProfile(
                    name = json.optString("name", "Little Hero"),
                    age = json.optInt("age", 4),
                    avatarId = json.optString("avatarId", "lion"),
                    stars = json.optInt("stars", 50),
                    streakDays = json.optInt("streakDays", 3),
                    puzzlesSolved = json.optInt("puzzlesSolved", 5),
                    quizzesCompleted = json.optInt("quizzesCompleted", 2),
                    phonicsLearned = json.optInt("phonicsLearned", 10),
                    balloonPops = json.optInt("balloonPops", 12),
                    badgesUnlocked = badgesSet,
                    passportCode = json.optString("passportCode", generatePassportCode("Hero")),
                    isOnboarded = true
                )
                saveProfile(restored)
                return true
            } else if (clean.startsWith("KP-") || clean.length >= 6) {
                // Restore via Passport Code format
                val parts = clean.split("-")
                val restoredStars = if (parts.size >= 2) parts[1].toIntOrNull() ?: 60 else 60
                val restored = _profile.value.copy(
                    stars = maxOf(_profile.value.stars, restoredStars),
                    isOnboarded = true,
                    passportCode = clean
                )
                saveProfile(restored)
                return true
            }
        } catch (_: Exception) {
            return false
        }
        return false
    }

    fun resetAllData() {
        prefs.edit().clear().apply()
        _profile.value = KidProfile(
            name = "",
            isOnboarded = false,
            passportCode = generatePassportCode("Hero")
        )
    }

    private fun generatePassportCode(name: String): String {
        val num = (1000..9999).random()
        val tag = listOf("STAR", "CHAMP", "HERO", "LION", "DINO").random()
        return "KP-$num-$tag"
    }

    // Dynamic Live Leaderboard
    fun getLeaderboard(): List<LeaderboardEntry> {
        val userStars = _profile.value.stars
        val userName = _profile.value.name
        val userEmoji = _profile.value.currentAvatar.emoji

        val botContenders = listOf(
            Triple("Maya Star", "🌟", 320),
            Triple("Noah Whiz", "🦁", 275),
            Triple("Lucas Bot", "🤖", 210),
            Triple("Mia Dino", "🦖", 160),
            Triple("Oliver Rabbit", "🐰", 120),
            Triple("Emma Pip", "🐧", 85),
            Triple("Liam Milo", "🐶", 55),
            Triple("Sophia Bear", "🐻", 35)
        )

        val all = mutableListOf<LeaderboardEntry>()
        var userInserted = false

        val sortedBots = botContenders.sortedByDescending { it.third }
        var currentRank = 1

        for (bot in sortedBots) {
            if (!userInserted && userStars >= bot.third) {
                all.add(
                    LeaderboardEntry(
                        rank = currentRank++,
                        name = "$userName (You)",
                        avatarEmoji = userEmoji,
                        stars = userStars,
                        title = _profile.value.rankTitle,
                        isCurrentUser = true
                    )
                )
                userInserted = true
            }
            all.add(
                LeaderboardEntry(
                    rank = currentRank++,
                    name = bot.first,
                    avatarEmoji = bot.second,
                    stars = bot.third,
                    title = when {
                        bot.third >= 250 -> "Grand Champion 👑"
                        bot.third >= 150 -> "Cosmic Master 🚀"
                        bot.third >= 100 -> "Puzzle Wizard 🧙"
                        else -> "Star Explorer ⭐"
                    },
                    isCurrentUser = false
                )
            )
        }

        if (!userInserted) {
            all.add(
                LeaderboardEntry(
                    rank = currentRank,
                    name = "$userName (You)",
                    avatarEmoji = userEmoji,
                    stars = userStars,
                    title = _profile.value.rankTitle,
                    isCurrentUser = true
                )
            )
        }

        return all
    }
}
