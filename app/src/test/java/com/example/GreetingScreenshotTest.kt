package com.example

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.example.ui.theme.MyApplicationTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class GreetingScreenshotTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun greeting_screenshot() {
    val sampleMatch = com.example.data.model.MatchWithTeams(
      match = com.example.data.local.entities.MatchEntity(
        id = 1,
        teamAId = 1,
        teamBId = 2,
        tournament = "Border-Gavaskar Trophy 2026",
        venue = "Melbourne Cricket Ground",
        status = "LIVE",
        scheduledTime = "LIVE NOW",
        scoreTeamA = 156,
        wicketsTeamA = 3,
        oversTeamA = 18.2,
        summaryText = "India 156/3 (18.2 ov) vs Australia"
      ),
      teamA = com.example.data.local.entities.TeamEntity(1, "India", "IND", "IND", "#1D4ED8", "🇮🇳"),
      teamB = com.example.data.local.entities.TeamEntity(2, "Australia", "AUS", "AUS", "#CA8A04", "🇦🇺")
    )
    composeTestRule.setContent {
      MyApplicationTheme {
        com.example.ui.components.MatchCard(
          matchWithTeams = sampleMatch,
          onViewMatch = {},
          onPredictNow = {}
        )
      }
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/greeting.png")
  }
}
