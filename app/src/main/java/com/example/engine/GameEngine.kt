package com.example.engine

import kotlin.math.abs
import kotlin.math.max
import kotlin.math.roundToInt

/**
 * Core scoring rules for cricket fantasy performance points.
 */
data class ScoringConfig(
    val runPoints: Double = 1.0,
    val fourBonus: Double = 1.0,
    val sixBonus: Double = 2.0,
    val fiftyBonus: Double = 10.0,
    val centuryBonus: Double = 20.0,
    val wicketPoints: Double = 20.0,
    val maidenPoints: Double = 10.0,
    val threeWicketsBonus: Double = 10.0,
    val fiveWicketsBonus: Double = 20.0,
    val catchPoints: Double = 8.0,
    val runOutPoints: Double = 10.0,
    val stumpingPoints: Double = 10.0
)

object GameEngine {

    /**
     * Calculates game performance points from player statistics using configured scoring rules.
     */
    fun calculatePoints(
        runs: Int,
        fours: Int,
        sixes: Int,
        wickets: Int,
        maidens: Int,
        catches: Int,
        runOuts: Int,
        stumpings: Int,
        config: ScoringConfig = ScoringConfig()
    ): Int {
        var points = 0.0

        // Batting
        points += runs * config.runPoints
        points += fours * config.fourBonus
        points += sixes * config.sixBonus
        if (runs >= 100) {
            points += config.centuryBonus
        } else if (runs >= 50) {
            points += config.fiftyBonus
        }

        // Bowling
        points += wickets * config.wicketPoints
        points += maidens * config.maidenPoints
        if (wickets >= 5) {
            points += config.fiveWicketsBonus
        } else if (wickets >= 3) {
            points += config.threeWicketsBonus
        }

        // Fielding
        points += catches * config.catchPoints
        points += runOuts * config.runOutPoints
        points += stumpings * config.stumpingPoints

        return points.roundToInt()
    }

    /**
     * ACCURACY FORMULA:
     * accuracy = max(0.0, 100.0 - (abs(predicted - actual) / max(actual, 1)) * 100.0)
     * Rounded to 2 decimal places.
     */
    fun calculateAccuracy(predictedPoints: Int, actualPoints: Int): Double {
        val diff = abs(predictedPoints - actualPoints).toDouble()
        val base = max(actualPoints, 1).toDouble()
        val rawAccuracy = max(0.0, 100.0 - (diff / base) * 100.0)
        return (rawAccuracy * 100.0).roundToInt() / 100.0
    }

    /**
     * REWARD ENGINE:
     * 99%+ accuracy: 5x
     * 95%+: 4x
     * 90%+: 3x
     * 80%+: 2x
     * 70%+: 1x
     * Below 70%: 0x
     */
    fun calculateMultiplier(accuracy: Double): Int {
        return when {
            accuracy >= 99.0 -> 5
            accuracy >= 95.0 -> 4
            accuracy >= 90.0 -> 3
            accuracy >= 80.0 -> 2
            accuracy >= 70.0 -> 1
            else -> 0
        }
    }

    fun calculateRewardCoins(coinsUsed: Int, multiplier: Int): Int {
        return coinsUsed * multiplier
    }

    /**
     * XP Calculation based on prediction performance
     */
    fun calculateXp(accuracy: Double, coinsUsed: Int): Int {
        val baseMultiplier = calculateMultiplier(accuracy)
        val accuracyBonus = (accuracy / 2.0).roundToInt()
        val coinWeight = (coinsUsed / 10).coerceAtMost(50)
        return if (baseMultiplier > 0) {
            (50 * baseMultiplier) + accuracyBonus + coinWeight
        } else {
            // Participation XP
            20 + (accuracy / 5.0).roundToInt()
        }
    }

    /**
     * Calculates user level from accumulated XP.
     * Level 1: 0 XP
     * Level 2: 500 XP
     * Level 3: 1,000 XP
     * Level 4: 2,000 XP
     * Level 5: 3,500 XP
     * Level 6: 5,500 XP
     * etc.
     */
    fun calculateLevel(xp: Int): Int {
        val thresholds = listOf(0, 500, 1000, 2000, 3500, 5500, 8000, 11000, 15000, 20000)
        for (i in thresholds.indices.reversed()) {
            if (xp >= thresholds[i]) {
                return i + 1
            }
        }
        return 1
    }

    fun getXpForNextLevel(currentLevel: Int): Int {
        val thresholds = listOf(0, 500, 1000, 2000, 3500, 5500, 8000, 11000, 15000, 20000)
        return if (currentLevel < thresholds.size) {
            thresholds[currentLevel]
        } else {
            20000 + (currentLevel - 10) * 5000
        }
    }
}
