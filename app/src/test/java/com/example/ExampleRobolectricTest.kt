package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.DataRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read app name string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Kids Puzzle", appName)
  }

  @Test
  fun `data repository profile persistence and stars`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val repo = DataRepository(context)

    repo.completeOnboarding(name = "Leo", age = 5, avatarId = "lion")
    val profile = repo.profile.value
    assertEquals("Leo", profile.name)
    assertEquals(5, profile.age)
    assertTrue(profile.isOnboarded)

    val initialStars = profile.stars
    repo.addStars(15, "puzzle")
    assertEquals(initialStars + 15, repo.profile.value.stars)

    // Test backup & restore
    val backup = repo.exportBackupPayload()
    assertNotNull(backup)
    assertTrue(backup.isNotEmpty())

    val restored = repo.restoreFromBackup(backup)
    assertTrue(restored)

    // Test leaderboard ranking
    val leaderboard = repo.getLeaderboard()
    assertTrue(leaderboard.isNotEmpty())
    assertTrue(leaderboard.any { it.isCurrentUser })
  }
}
