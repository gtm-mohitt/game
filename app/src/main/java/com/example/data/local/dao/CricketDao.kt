package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.data.local.entities.AchievementEntity
import com.example.data.local.entities.AuditLogEntity
import com.example.data.local.entities.CoinTransactionEntity
import com.example.data.local.entities.MatchEntity
import com.example.data.local.entities.MissionEntity
import com.example.data.local.entities.NotificationEntity
import com.example.data.local.entities.PlayerEntity
import com.example.data.local.entities.PlayerMatchStatsEntity
import com.example.data.local.entities.PredictionEntity
import com.example.data.local.entities.RewardRuleEntity
import com.example.data.local.entities.ScoringRuleEntity
import com.example.data.local.entities.TeamEntity
import com.example.data.local.entities.UserAchievementEntity
import com.example.data.local.entities.UserEntity
import com.example.data.local.entities.UserMissionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CricketDao {

    // USERS
    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    fun getUserByIdFlow(userId: Long): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    suspend fun getUserById(userId: Long): UserEntity?

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users ORDER BY xp DESC")
    fun getAllUsersByXp(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users ORDER BY xp DESC")
    suspend fun getAllUsersList(): List<UserEntity>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUser(user: UserEntity): Long

    @Update
    suspend fun updateUser(user: UserEntity)

    // TEAMS
    @Query("SELECT * FROM teams")
    fun getAllTeamsFlow(): Flow<List<TeamEntity>>

    @Query("SELECT * FROM teams WHERE id = :teamId LIMIT 1")
    suspend fun getTeamById(teamId: Long): TeamEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTeams(teams: List<TeamEntity>)

    // PLAYERS
    @Query("SELECT * FROM players WHERE teamId = :teamId")
    fun getPlayersByTeamFlow(teamId: Long): Flow<List<PlayerEntity>>

    @Query("SELECT * FROM players")
    fun getAllPlayersFlow(): Flow<List<PlayerEntity>>

    @Query("SELECT * FROM players WHERE id = :playerId LIMIT 1")
    suspend fun getPlayerById(playerId: Long): PlayerEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlayers(players: List<PlayerEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlayer(player: PlayerEntity): Long

    @Update
    suspend fun updatePlayer(player: PlayerEntity)

    // MATCHES
    @Query("SELECT * FROM matches ORDER BY id DESC")
    fun getAllMatchesFlow(): Flow<List<MatchEntity>>

    @Query("SELECT * FROM matches WHERE status = :status ORDER BY id DESC")
    fun getMatchesByStatusFlow(status: String): Flow<List<MatchEntity>>

    @Query("SELECT * FROM matches WHERE id = :matchId LIMIT 1")
    fun getMatchByIdFlow(matchId: Long): Flow<MatchEntity?>

    @Query("SELECT * FROM matches WHERE id = :matchId LIMIT 1")
    suspend fun getMatchById(matchId: Long): MatchEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMatch(match: MatchEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMatches(matches: List<MatchEntity>)

    @Update
    suspend fun updateMatch(match: MatchEntity)

    // STATS
    @Query("SELECT * FROM player_match_stats WHERE matchId = :matchId")
    fun getStatsForMatchFlow(matchId: Long): Flow<List<PlayerMatchStatsEntity>>

    @Query("SELECT * FROM player_match_stats WHERE matchId = :matchId AND playerId = :playerId LIMIT 1")
    suspend fun getPlayerStats(matchId: Long, playerId: Long): PlayerMatchStatsEntity?

    @Query("SELECT * FROM player_match_stats WHERE matchId = :matchId")
    suspend fun getStatsForMatch(matchId: Long): List<PlayerMatchStatsEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlayerStats(stats: List<PlayerMatchStatsEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertPlayerStat(stat: PlayerMatchStatsEntity)

    // PREDICTIONS
    @Query("SELECT * FROM predictions WHERE userId = :userId ORDER BY id DESC")
    fun getUserPredictionsFlow(userId: Long): Flow<List<PredictionEntity>>

    @Query("SELECT * FROM predictions WHERE userId = :userId AND matchId = :matchId")
    fun getUserPredictionsForMatchFlow(userId: Long, matchId: Long): Flow<List<PredictionEntity>>

    @Query("SELECT * FROM predictions WHERE matchId = :matchId")
    suspend fun getPredictionsForMatch(matchId: Long): List<PredictionEntity>

    @Query("SELECT * FROM predictions WHERE idempotencyKey = :key LIMIT 1")
    suspend fun getPredictionByIdempotencyKey(key: String): PredictionEntity?

    @Query("SELECT * FROM predictions ORDER BY id DESC")
    fun getAllPredictionsFlow(): Flow<List<PredictionEntity>>

    @Query("SELECT * FROM predictions")
    suspend fun getAllPredictionsList(): List<PredictionEntity>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertPrediction(prediction: PredictionEntity): Long

    @Update
    suspend fun updatePrediction(prediction: PredictionEntity)

    // COIN TRANSACTIONS
    @Query("SELECT * FROM coin_transactions WHERE userId = :userId ORDER BY timestamp DESC")
    fun getUserTransactionsFlow(userId: Long): Flow<List<CoinTransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: CoinTransactionEntity): Long

    // SCORING RULES
    @Query("SELECT * FROM scoring_rules")
    fun getAllScoringRulesFlow(): Flow<List<ScoringRuleEntity>>

    @Query("SELECT * FROM scoring_rules")
    suspend fun getAllScoringRules(): List<ScoringRuleEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScoringRules(rules: List<ScoringRuleEntity>)

    @Update
    suspend fun updateScoringRule(rule: ScoringRuleEntity)

    // REWARD RULES
    @Query("SELECT * FROM reward_rules ORDER BY minAccuracy DESC")
    fun getAllRewardRulesFlow(): Flow<List<RewardRuleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRewardRules(rules: List<RewardRuleEntity>)

    // ACHIEVEMENTS
    @Query("SELECT * FROM achievements")
    fun getAllAchievementsFlow(): Flow<List<AchievementEntity>>

    @Query("SELECT * FROM achievements")
    suspend fun getAllAchievementsList(): List<AchievementEntity>

    @Query("SELECT achievementId FROM user_achievements WHERE userId = :userId")
    fun getUserAchievementIdsFlow(userId: Long): Flow<List<Long>>

    @Query("SELECT achievementId FROM user_achievements WHERE userId = :userId")
    suspend fun getUserAchievementIds(userId: Long): List<Long>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertUserAchievement(userAchievement: UserAchievementEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAchievements(achievements: List<AchievementEntity>)

    // MISSIONS
    @Query("SELECT * FROM missions")
    fun getAllMissionsFlow(): Flow<List<MissionEntity>>

    @Query("SELECT * FROM user_missions WHERE userId = :userId")
    fun getUserMissionsFlow(userId: Long): Flow<List<UserMissionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMissions(missions: List<MissionEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserMissions(userMissions: List<UserMissionEntity>)

    @Update
    suspend fun updateUserMission(userMission: UserMissionEntity)

    // NOTIFICATIONS
    @Query("SELECT * FROM notifications WHERE userId = :userId ORDER BY timestamp DESC")
    fun getUserNotificationsFlow(userId: Long): Flow<List<NotificationEntity>>

    @Query("SELECT COUNT(*) FROM notifications WHERE userId = :userId AND isRead = 0")
    fun getUnreadNotificationsCountFlow(userId: Long): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity)

    @Query("UPDATE notifications SET isRead = 1 WHERE userId = :userId AND id = :notificationId")
    suspend fun markNotificationAsRead(userId: Long, notificationId: Long)

    @Query("UPDATE notifications SET isRead = 1 WHERE userId = :userId")
    suspend fun markAllNotificationsAsRead(userId: Long)

    // AUDIT LOGS
    @Query("SELECT * FROM audit_logs ORDER BY timestamp DESC")
    fun getAllAuditLogsFlow(): Flow<List<AuditLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuditLog(log: AuditLogEntity)

    // TRANSACTIONAL PREDICTION CONFIRMATION
    @Transaction
    suspend fun executePredictionTransaction(
        userId: Long,
        matchId: Long,
        playerId: Long,
        predictedPoints: Int,
        coinsUsed: Int,
        idempotencyKey: String
    ): Result<Long> {
        // 1. Check idempotency
        val existing = getPredictionByIdempotencyKey(idempotencyKey)
        if (existing != null) {
            return Result.success(existing.id)
        }

        // 2. Validate User & Balance
        val user = getUserById(userId) ?: return Result.failure(Exception("User not found"))
        if (user.coins < coinsUsed) {
            return Result.failure(Exception("Insufficient virtual coins. Balance: ${user.coins}"))
        }

        // 3. Validate Match
        val match = getMatchById(matchId) ?: return Result.failure(Exception("Match not found"))
        if (match.predictionsLocked || match.status == "COMPLETED" || match.status == "CANCELLED") {
            return Result.failure(Exception("Predictions are locked for this match."))
        }

        // 4. Validate Player
        val player = getPlayerById(playerId) ?: return Result.failure(Exception("Player not found"))
        if (player.teamId != match.teamAId && player.teamId != match.teamBId) {
            return Result.failure(Exception("Player does not belong to this match."))
        }

        // 5. Deduct Coins
        val newBalance = user.coins - coinsUsed
        updateUser(user.copy(coins = newBalance))

        // 6. Record Transaction
        insertTransaction(
            CoinTransactionEntity(
                userId = userId,
                amount = -coinsUsed,
                type = "PREDICTION_ENTRY",
                description = "Prediction on ${player.name} ($predictedPoints pts)",
                balanceAfter = newBalance
            )
        )

        // 7. Insert Prediction
        val predictionId = insertPrediction(
            PredictionEntity(
                idempotencyKey = idempotencyKey,
                userId = userId,
                matchId = matchId,
                playerId = playerId,
                predictedPoints = predictedPoints,
                coinsUsed = coinsUsed,
                status = if (match.status == "LIVE") "LIVE" else "PENDING"
            )
        )

        return Result.success(predictionId)
    }
}
