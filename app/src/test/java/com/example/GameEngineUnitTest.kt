package com.example

import com.example.engine.GameEngine
import com.example.engine.ScoringConfig
import org.junit.Assert.assertEquals
import org.junit.Test

class GameEngineUnitTest {

    @Test
    fun testBattingPointsCalculation() {
        // 45 runs with 4 fours and 2 sixes
        // runs: 45 * 1 = 45
        // 4s: 4 * 1 = 4
        // 6s: 2 * 2 = 4
        // Total = 53
        val points = GameEngine.calculatePoints(
            runs = 45,
            fours = 4,
            sixes = 2,
            wickets = 0,
            maidens = 0,
            catches = 0,
            runOuts = 0,
            stumpings = 0
        )
        assertEquals(53, points)
    }

    @Test
    fun testHalfCenturyBonus() {
        // 55 runs with 5 fours, 1 six
        // runs: 55 + 5 + 2 + 10 (half-century bonus) = 72
        val points = GameEngine.calculatePoints(
            runs = 55,
            fours = 5,
            sixes = 1,
            wickets = 0,
            maidens = 0,
            catches = 0,
            runOuts = 0,
            stumpings = 0
        )
        assertEquals(72, points)
    }

    @Test
    fun testBowlingAndFieldingPointsCalculation() {
        // 3 wickets (+20 each = 60, + 10 for 3-wicket bonus = 70)
        // 1 maiden (+10 = 80)
        // 2 catches (+8 each = 96)
        val points = GameEngine.calculatePoints(
            runs = 0,
            fours = 0,
            sixes = 0,
            wickets = 3,
            maidens = 1,
            catches = 2,
            runOuts = 0,
            stumpings = 0
        )
        assertEquals(96, points)
    }

    @Test
    fun testAccuracyFormulaExact() {
        // Predicted = 70, Actual = 72
        // Diff = 2
        // Formula: max(0.0, 100.0 - (2 / 72) * 100) = 100 - 2.7777... = 97.22%
        val accuracy = GameEngine.calculateAccuracy(predictedPoints = 70, actualPoints = 72)
        assertEquals(97.22, accuracy, 0.01)
    }

    @Test
    fun testAccuracyZeroDiff() {
        // Perfect prediction: 85 vs 85 -> 100%
        val accuracy = GameEngine.calculateAccuracy(predictedPoints = 85, actualPoints = 85)
        assertEquals(100.0, accuracy, 0.01)
    }

    @Test
    fun testAccuracyFarDiff() {
        // Predicted = 100, Actual = 10 -> Diff = 90 / 10 * 100 = 900 -> capped at 0.0%
        val accuracy = GameEngine.calculateAccuracy(predictedPoints = 100, actualPoints = 10)
        assertEquals(0.0, accuracy, 0.01)
    }

    @Test
    fun testRewardMultiplierTiers() {
        assertEquals(5, GameEngine.calculateMultiplier(99.5))
        assertEquals(5, GameEngine.calculateMultiplier(99.0))
        assertEquals(4, GameEngine.calculateMultiplier(97.22))
        assertEquals(4, GameEngine.calculateMultiplier(95.0))
        assertEquals(3, GameEngine.calculateMultiplier(92.5))
        assertEquals(3, GameEngine.calculateMultiplier(90.0))
        assertEquals(2, GameEngine.calculateMultiplier(85.0))
        assertEquals(2, GameEngine.calculateMultiplier(80.0))
        assertEquals(1, GameEngine.calculateMultiplier(75.0))
        assertEquals(1, GameEngine.calculateMultiplier(70.0))
        assertEquals(0, GameEngine.calculateMultiplier(69.9))
        assertEquals(0, GameEngine.calculateMultiplier(40.0))
    }

    @Test
    fun testRewardCoinsCalculation() {
        // 100 coins with 4x multiplier = 400 coins
        assertEquals(400, GameEngine.calculateRewardCoins(100, 4))
        // 250 coins with 5x multiplier = 1250 coins
        assertEquals(1250, GameEngine.calculateRewardCoins(250, 5))
        // 100 coins with 0x multiplier = 0 coins
        assertEquals(0, GameEngine.calculateRewardCoins(100, 0))
    }

    @Test
    fun testLevelProgression() {
        assertEquals(1, GameEngine.calculateLevel(0))
        assertEquals(1, GameEngine.calculateLevel(450))
        assertEquals(2, GameEngine.calculateLevel(500))
        assertEquals(3, GameEngine.calculateLevel(1200))
        assertEquals(4, GameEngine.calculateLevel(2500))
    }
}
