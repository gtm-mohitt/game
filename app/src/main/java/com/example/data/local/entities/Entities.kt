package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "users",
    indices = [Index(value = ["email"], unique = true)]
)
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val email: String,
    val username: String,
    val passwordHash: String,
    val role: String = "USER", // "USER", "ADMIN"
    val coins: Int = 1000,
    val xp: Int = 0,
    val level: Int = 1,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val accuracyStreak: Int = 0,
    val avatarInitial: String = "U",
    val avatarColorHex: String = "#10B981",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "teams")
data class TeamEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val shortName: String,
    val code: String,
    val primaryColorHex: String,
    val flagEmoji: String
)

@Entity(
    tableName = "players",
    indices = [Index(value = ["teamId"])]
)
data class PlayerEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val teamId: Long,
    val name: String,
    val role: String, // "BATTER", "BOWLER", "ALL_ROUNDER", "WICKETKEEPER"
    val jerseyNumber: Int,
    val avatarInitial: String,
    val avatarColorHex: String,
    val isActive: Boolean = true
)

@Entity(
    tableName = "matches",
    indices = [Index(value = ["teamAId"]), Index(value = ["teamBId"])]
)
data class MatchEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val teamAId: Long,
    val teamBId: Long,
    val tournament: String,
    val venue: String,
    val status: String, // "SCHEDULED", "LIVE", "COMPLETED", "CANCELLED"
    val scheduledTime: String,
    val scoreTeamA: Int = 0,
    val wicketsTeamA: Int = 0,
    val oversTeamA: Double = 0.0,
    val scoreTeamB: Int = 0,
    val wicketsTeamB: Int = 0,
    val oversTeamB: Double = 0.0,
    val currentInnings: Int = 1, // 1 or 2
    val currentBattingTeamId: Long = 0,
    val predictionsLocked: Boolean = false,
    val winnerTeamName: String? = null,
    val summaryText: String = "Match starting soon"
)

@Entity(
    tableName = "player_match_stats",
    indices = [Index(value = ["matchId", "playerId"], unique = true)]
)
data class PlayerMatchStatsEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val matchId: Long,
    val playerId: Long,
    val runs: Int = 0,
    val balls: Int = 0,
    val fours: Int = 0,
    val sixes: Int = 0,
    val oversBowled: Double = 0.0,
    val maidenOvers: Int = 0,
    val wicketsTaken: Int = 0,
    val runsConceded: Int = 0,
    val catches: Int = 0,
    val runOuts: Int = 0,
    val stumpings: Int = 0,
    val points: Int = 0
)

@Entity(
    tableName = "predictions",
    indices = [
        Index(value = ["idempotencyKey"], unique = true),
        Index(value = ["userId"]),
        Index(value = ["matchId"]),
        Index(value = ["playerId"])
    ]
)
data class PredictionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val idempotencyKey: String,
    val userId: Long,
    val matchId: Long,
    val playerId: Long,
    val predictedPoints: Int,
    val coinsUsed: Int,
    val actualPoints: Int = 0,
    val difference: Int = 0,
    val accuracy: Double = 0.0,
    val multiplier: Int = 0,
    val rewardCoins: Int = 0,
    val xpEarned: Int = 0,
    val status: String = "PENDING", // "PENDING", "LIVE", "WON", "LOST", "CANCELLED"
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "coin_transactions",
    indices = [Index(value = ["userId"])]
)
data class CoinTransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long,
    val amount: Int, // can be positive or negative
    val type: String, // "WELCOME_BONUS", "PREDICTION_ENTRY", "PREDICTION_REWARD", "DAILY_BONUS", "STREAK_REWARD", "ACHIEVEMENT_REWARD", "ADMIN_ADJUSTMENT"
    val description: String,
    val balanceAfter: Int,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "achievements")
data class AchievementEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val key: String,
    val title: String,
    val description: String,
    val iconName: String,
    val targetRequirement: Int,
    val coinReward: Int,
    val xpReward: Int
)

@Entity(
    tableName = "user_achievements",
    indices = [Index(value = ["userId", "achievementId"], unique = true)]
)
data class UserAchievementEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long,
    val achievementId: Long,
    val unlockedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "missions")
data class MissionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val key: String,
    val title: String,
    val description: String,
    val targetCount: Int,
    val coinReward: Int,
    val xpReward: Int
)

@Entity(
    tableName = "user_missions",
    indices = [Index(value = ["userId", "missionId"], unique = true)]
)
data class UserMissionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long,
    val missionId: Long,
    val currentProgress: Int = 0,
    val isCompleted: Boolean = false,
    val isClaimed: Boolean = false
)

@Entity(tableName = "scoring_rules")
data class ScoringRuleEntity(
    @PrimaryKey
    val ruleKey: String,
    val name: String,
    val category: String, // "BATTING", "BOWLING", "FIELDING"
    val pointValue: Double,
    val description: String
)

@Entity(tableName = "reward_rules")
data class RewardRuleEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val minAccuracy: Double,
    val maxAccuracy: Double,
    val multiplier: Int,
    val label: String
)

@Entity(
    tableName = "notifications",
    indices = [Index(value = ["userId"])]
)
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long,
    val title: String,
    val message: String,
    val type: String, // "MATCH_STARTING", "PREDICTION_CLOSING", "PREDICTION_RESULT", "REWARD_RECEIVED", "ACHIEVEMENT_UNLOCKED", "LEADERBOARD_UPDATE"
    val isRead: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "audit_logs")
data class AuditLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val adminId: Long,
    val action: String,
    val target: String,
    val oldValue: String,
    val newValue: String,
    val timestamp: Long = System.currentTimeMillis()
)
