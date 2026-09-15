package com.example.data.repository

import com.example.data.local.dao.CricketDao
import com.example.data.local.entities.AchievementEntity
import com.example.data.local.entities.AuditLogEntity
import com.example.data.local.entities.CoinTransactionEntity
import com.example.data.local.entities.MatchEntity
import com.example.data.local.entities.MissionEntity
import com.example.data.local.entities.NotificationEntity
import com.example.data.local.entities.PlayerEntity
import com.example.data.local.entities.PlayerMatchStatsEntity
import com.example.data.local.entities.PredictionEntity
import com.example.data.local.entities.ScoringRuleEntity
import com.example.data.local.entities.TeamEntity
import com.example.data.local.entities.UserAchievementEntity
import com.example.data.local.entities.UserEntity
import com.example.data.local.entities.UserMissionEntity
import com.example.data.model.AdminDashboardStats
import com.example.data.model.LeaderboardEntry
import com.example.data.model.MatchWithTeams
import com.example.data.model.PlayerWithMatchStats
import com.example.data.model.PredictionWithDetails
import com.example.engine.GameEngine
import com.example.engine.ScoringConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID
import kotlin.random.Random

class CricketRepository(
    private val dao: CricketDao,
    private val scope: CoroutineScope
) {
    // Current Active User Id: Defaults to 1 (Demo User)
    private val _currentUserId = MutableStateFlow<Long>(1L)
    val currentUserId: StateFlow<Long> = _currentUserId.asStateFlow()

    // Simulation active state
    private val _isSimulating = MutableStateFlow(false)
    val isSimulating: StateFlow<Boolean> = _isSimulating.asStateFlow()
    private var simulationJob: Job? = null

    // Observe active user
    val currentUserFlow: Flow<UserEntity?> = combine(_currentUserId, dao.getAllUsersByXp()) { id, users ->
        users.find { it.id == id } ?: users.firstOrNull()
    }.flowOn(Dispatchers.IO)

    fun switchUser(userId: Long) {
        _currentUserId.value = userId
    }

    suspend fun login(email: String, password: String): Result<UserEntity> = withContext(Dispatchers.IO) {
        val user = dao.getUserByEmail(email)
        if (user == null) {
            Result.failure(Exception("User with email $email not found."))
        } else if (user.passwordHash != password) {
            Result.failure(Exception("Invalid password."))
        } else {
            _currentUserId.value = user.id
            Result.success(user)
        }
    }

    suspend fun register(email: String, username: String, password: String): Result<UserEntity> = withContext(Dispatchers.IO) {
        val existing = dao.getUserByEmail(email)
        if (existing != null) {
            return@withContext Result.failure(Exception("User with this email already exists."))
        }
        val newUser = UserEntity(
            email = email,
            username = username,
            passwordHash = password,
            coins = 1000,
            xp = 0,
            level = 1,
            avatarInitial = username.take(1).uppercase(),
            avatarColorHex = "#10B981"
        )
        val id = dao.insertUser(newUser)
        dao.insertTransaction(
            CoinTransactionEntity(
                userId = id,
                amount = 1000,
                type = "WELCOME_BONUS",
                description = "Welcome bonus - 1,000 free coins!",
                balanceAfter = 1000
            )
        )
        val saved = newUser.copy(id = id)
        _currentUserId.value = id
        Result.success(saved)
    }

    // MATCHES WITH TEAMS
    val matchesWithTeamsFlow: Flow<List<MatchWithTeams>> = combine(
        dao.getAllMatchesFlow(),
        dao.getAllTeamsFlow()
    ) { matches, teams ->
        val teamMap = teams.associateBy { it.id }
        matches.map { match ->
            val teamA = teamMap[match.teamAId] ?: TeamEntity(1, "India", "IND", "IND", "#1D4ED8", "🇮🇳")
            val teamB = teamMap[match.teamBId] ?: TeamEntity(2, "Australia", "AUS", "AUS", "#CA8A04", "🇦🇺")
            MatchWithTeams(match, teamA, teamB)
        }
    }.flowOn(Dispatchers.IO)

    fun getMatchDetailsFlow(matchId: Long): Flow<MatchWithTeams?> = combine(
        dao.getMatchByIdFlow(matchId),
        dao.getAllTeamsFlow()
    ) { match, teams ->
        if (match == null) null
        else {
            val teamMap = teams.associateBy { it.id }
            val teamA = teamMap[match.teamAId] ?: TeamEntity(1, "India", "IND", "IND", "#1D4ED8", "🇮🇳")
            val teamB = teamMap[match.teamBId] ?: TeamEntity(2, "Australia", "AUS", "AUS", "#CA8A04", "🇦🇺")
            MatchWithTeams(match, teamA, teamB)
        }
    }.flowOn(Dispatchers.IO)

    // PLAYERS FOR MATCH (WITH LIVE STATS & PREDICTIONS)
    fun getPlayersForMatchFlow(matchId: Long): Flow<List<PlayerWithMatchStats>> = combine(
        dao.getMatchByIdFlow(matchId),
        dao.getAllPlayersFlow(),
        dao.getAllTeamsFlow(),
        dao.getStatsForMatchFlow(matchId),
        dao.getUserPredictionsForMatchFlow(_currentUserId.value, matchId)
    ) { match, players, teams, statsList, userPreds ->
        if (match == null) return@combine emptyList()

        val teamMap = teams.associateBy { it.id }
        val statsMap = statsList.associateBy { it.playerId }
        val predMap = userPreds.associateBy { it.playerId }

        // Filter players belonging to either team A or team B
        players.filter { it.teamId == match.teamAId || it.teamId == match.teamBId }
            .map { player ->
                val team = teamMap[player.teamId] ?: TeamEntity(0, "Unknown", "UNK", "UNK", "#64748B", "🏏")
                val stats = statsMap[player.id]
                val currentPoints = stats?.points ?: 0
                val userPred = predMap[player.id]
                PlayerWithMatchStats(
                    player = player,
                    team = team,
                    stats = stats,
                    currentPoints = currentPoints,
                    userPrediction = userPred
                )
            }
    }.flowOn(Dispatchers.IO)

    // USER PREDICTIONS WITH DETAILS
    val userPredictionsWithDetailsFlow: Flow<List<PredictionWithDetails>> = combine(
        _currentUserId,
        dao.getAllMatchesFlow(),
        dao.getAllPlayersFlow(),
        dao.getAllTeamsFlow()
    ) { uid, matches, players, teams ->
        Triple(matches.associateBy { it.id }, players.associateBy { it.id }, teams.associateBy { it.id })
    }.combine(dao.getAllPredictionsFlow()) { (matchesMap, playersMap, teamsMap), allPreds ->
        val uid = _currentUserId.value
        allPreds.filter { it.userId == uid }
            .mapNotNull { pred ->
                val player = playersMap[pred.playerId] ?: return@mapNotNull null
                val match = matchesMap[pred.matchId] ?: return@mapNotNull null
                val team = teamsMap[player.teamId] ?: TeamEntity(0, "Team", "TM", "TM", "#64748B", "🏏")
                PredictionWithDetails(
                    prediction = pred,
                    player = player,
                    team = team,
                    match = match,
                    currentPoints = pred.actualPoints
                )
            }
    }.flowOn(Dispatchers.IO)

    // WALLET TRANSACTIONS
    fun getUserTransactionsFlow(): Flow<List<CoinTransactionEntity>> = combine(
        _currentUserId,
        dao.getAllUsersByXp()
    ) { uid, _ -> uid }.combine(dao.getAllUsersByXp()) { uid, _ -> uid }
        .let { dao.getUserTransactionsFlow(_currentUserId.value) }

    // NOTIFICATIONS
    fun getUserNotificationsFlow(): Flow<List<NotificationEntity>> =
        dao.getUserNotificationsFlow(_currentUserId.value)

    fun getUnreadCountFlow(): Flow<Int> =
        dao.getUnreadNotificationsCountFlow(_currentUserId.value)

    suspend fun markNotificationRead(id: Long) = withContext(Dispatchers.IO) {
        dao.markNotificationAsRead(_currentUserId.value, id)
    }

    suspend fun markAllNotificationsRead() = withContext(Dispatchers.IO) {
        dao.markAllNotificationsAsRead(_currentUserId.value)
    }

    // LEADERBOARD
    val leaderboardFlow: Flow<List<LeaderboardEntry>> = combine(
        dao.getAllUsersByXp(),
        dao.getAllPredictionsFlow()
    ) { users, predictions ->
        val predsByUser = predictions.groupBy { it.userId }
        users.mapIndexed { index, user ->
            val userPreds = predsByUser[user.id].orEmpty()
            val total = userPreds.size
            val won = userPreds.count { it.status == "WON" }
            val avgAcc = if (userPreds.isNotEmpty()) {
                val completed = userPreds.filter { it.status == "WON" || it.status == "LOST" }
                if (completed.isNotEmpty()) {
                    (completed.sumOf { it.accuracy } / completed.size * 100).toInt() / 100.0
                } else 0.0
            } else 0.0
            val totalWonCoins = userPreds.sumOf { it.rewardCoins }

            LeaderboardEntry(
                rank = index + 1,
                user = user,
                totalPredictions = total,
                wonPredictions = won,
                averageAccuracy = avgAcc,
                totalCoinsWon = totalWonCoins
            )
        }
    }.flowOn(Dispatchers.IO)

    // ACHIEVEMENTS & MISSIONS
    val achievementsFlow: Flow<List<Pair<AchievementEntity, Boolean>>> = combine(
        dao.getAllAchievementsFlow(),
        dao.getUserAchievementIdsFlow(_currentUserId.value)
    ) { allAchievements, unlockedIds ->
        val unlockedSet = unlockedIds.toSet()
        allAchievements.map { ach -> ach to unlockedSet.contains(ach.id) }
    }.flowOn(Dispatchers.IO)

    val missionsFlow: Flow<List<Pair<MissionEntity, UserMissionEntity?>>> = combine(
        dao.getAllMissionsFlow(),
        dao.getUserMissionsFlow(_currentUserId.value)
    ) { allMissions, userMissions ->
        val userMap = userMissions.associateBy { it.missionId }
        allMissions.map { mission -> mission to userMap[mission.id] }
    }.flowOn(Dispatchers.IO)

    // PREDICTION SUBMISSION
    suspend fun submitPrediction(
        matchId: Long,
        playerId: Long,
        predictedPoints: Int,
        coinsUsed: Int
    ): Result<Long> = withContext(Dispatchers.IO) {
        val uid = _currentUserId.value
        val idempotencyKey = "pred_${uid}_${matchId}_${playerId}_${predictedPoints}_${coinsUsed}"
        dao.executePredictionTransaction(
            userId = uid,
            matchId = matchId,
            playerId = playerId,
            predictedPoints = predictedPoints,
            coinsUsed = coinsUsed,
            idempotencyKey = idempotencyKey
        )
    }

    // SIMULATOR CONTROLS
    fun startSimulation(matchId: Long) {
        if (_isSimulating.value) return
        _isSimulating.value = true
        simulationJob = scope.launch(Dispatchers.IO) {
            while (isActive && _isSimulating.value) {
                delay(2500) // delivery every 2.5 seconds
                val shouldContinue = simulateNextDelivery(matchId)
                if (!shouldContinue) {
                    _isSimulating.value = false
                    break
                }
            }
        }
    }

    fun pauseSimulation() {
        _isSimulating.value = false
        simulationJob?.cancel()
    }

    suspend fun simulateNextDelivery(matchId: Long): Boolean = withContext(Dispatchers.IO) {
        val match = dao.getMatchById(matchId) ?: return@withContext false
        if (match.status == "COMPLETED") return@withContext false

        // Random delivery generator
        val roll = Random.nextInt(100)
        var runsOnBall = 0
        var isFour = false
        var isSix = false
        var isWicket = false
        var isCatch = false

        when {
            roll < 45 -> runsOnBall = Random.nextInt(0, 3)
            roll < 65 -> { runsOnBall = 4; isFour = true }
            roll < 75 -> { runsOnBall = 6; isSix = true }
            roll < 85 -> {
                isWicket = true
                if (Random.nextBoolean()) isCatch = true
            }
            else -> runsOnBall = 1
        }

        // Update match score
        val newScore = match.scoreTeamA + runsOnBall
        val newWkts = match.wicketsTeamA + (if (isWicket) 1 else 0)
        val currentBalls = ((match.oversTeamA * 10).toInt() % 10) + 1
        val fullOvers = (match.oversTeamA).toInt() + (if (currentBalls >= 6) 1 else 0)
        val newOvers = fullOvers + (if (currentBalls >= 6) 0.0 else currentBalls * 0.1)

        val updatedMatch = match.copy(
            scoreTeamA = newScore,
            wicketsTeamA = newWkts,
            oversTeamA = (newOvers * 10).toInt() / 10.0,
            summaryText = "India $newScore/$newWkts (${(newOvers * 10).toInt() / 10.0} ov) - ${if (isSix) "💥 SIX!" else if (isFour) "🔥 FOUR!" else if (isWicket) "🎯 WICKET!" else "$runsOnBall run"}"
        )
        dao.updateMatch(updatedMatch)

        // Select an active batsman to credit runs (e.g. Virat Kohli [3] or Rohit Sharma [1] or Gill [2])
        val batterId = if (Random.nextBoolean()) 3L else 1L
        val existingStats = dao.getPlayerStats(matchId, batterId) ?: PlayerMatchStatsEntity(matchId = matchId, playerId = batterId)

        val updatedRuns = existingStats.runs + runsOnBall
        val updatedFours = existingStats.fours + (if (isFour) 1 else 0)
        val updatedSixes = existingStats.sixes + (if (isSix) 1 else 0)
        val updatedBalls = existingStats.balls + 1

        val newPoints = GameEngine.calculatePoints(
            runs = updatedRuns,
            fours = updatedFours,
            sixes = updatedSixes,
            wickets = existingStats.wicketsTaken,
            maidens = existingStats.maidenOvers,
            catches = existingStats.catches,
            runOuts = existingStats.runOuts,
            stumpings = existingStats.stumpings
        )

        dao.upsertPlayerStat(
            existingStats.copy(
                runs = updatedRuns,
                fours = updatedFours,
                sixes = updatedSixes,
                balls = updatedBalls,
                points = newPoints
            )
        )

        // If wicket fell, also credit bowler (e.g. Mitchell Starc [20] or Cummins [19])
        if (isWicket) {
            val bowlerId = if (Random.nextBoolean()) 20L else 19L
            val bowlerStats = dao.getPlayerStats(matchId, bowlerId) ?: PlayerMatchStatsEntity(matchId = matchId, playerId = bowlerId)
            val updatedWkts = bowlerStats.wicketsTaken + 1
            val updatedCatches = bowlerStats.catches + (if (isCatch) 1 else 0)
            val bowlerPoints = GameEngine.calculatePoints(
                runs = bowlerStats.runs,
                fours = bowlerStats.fours,
                sixes = bowlerStats.sixes,
                wickets = updatedWkts,
                maidens = bowlerStats.maidenOvers,
                catches = updatedCatches,
                runOuts = bowlerStats.runOuts,
                stumpings = bowlerStats.stumpings
            )
            dao.upsertPlayerStat(
                bowlerStats.copy(
                    wicketsTaken = updatedWkts,
                    catches = updatedCatches,
                    points = bowlerPoints
                )
            )
        }

        // If match reaches 20 overs, auto-complete
        if (newOvers >= 20.0 || newWkts >= 10) {
            finishMatchAndSettle(matchId)
            return@withContext false
        }

        return@withContext true
    }

    // FINISH MATCH & SETTLE ALL PREDICTIONS
    suspend fun finishMatchAndSettle(matchId: Long) = withContext(Dispatchers.IO) {
        pauseSimulation()
        val match = dao.getMatchById(matchId) ?: return@withContext
        val updatedMatch = match.copy(
            status = "COMPLETED",
            predictionsLocked = true,
            winnerTeamName = "India",
            summaryText = "India won by ${Random.nextInt(15, 35)} runs! Match completed."
        )
        dao.updateMatch(updatedMatch)

        // Fetch stats & predictions for match
        val stats = dao.getStatsForMatch(matchId).associateBy { it.playerId }
        val predictions = dao.getPredictionsForMatch(matchId)

        for (pred in predictions) {
            val actualPoints = stats[pred.playerId]?.points ?: 0
            val diff = kotlin.math.abs(pred.predictedPoints - actualPoints)
            val accuracy = GameEngine.calculateAccuracy(pred.predictedPoints, actualPoints)
            val multiplier = GameEngine.calculateMultiplier(accuracy)
            val rewardCoins = GameEngine.calculateRewardCoins(pred.coinsUsed, multiplier)
            val xpEarned = GameEngine.calculateXp(accuracy, pred.coinsUsed)
            val isWon = multiplier > 0

            // Update Prediction
            dao.updatePrediction(
                pred.copy(
                    actualPoints = actualPoints,
                    difference = diff,
                    accuracy = accuracy,
                    multiplier = multiplier,
                    rewardCoins = rewardCoins,
                    xpEarned = xpEarned,
                    status = if (isWon) "WON" else "LOST"
                )
            )

            // Update user wallet & XP
            val user = dao.getUserById(pred.userId) ?: continue
            val newCoins = user.coins + rewardCoins
            val newXp = user.xp + xpEarned
            val newLevel = GameEngine.calculateLevel(newXp)
            val newStreak = if (isWon) user.currentStreak + 1 else 0
            val longest = maxOf(user.longestStreak, newStreak)

            dao.updateUser(
                user.copy(
                    coins = newCoins,
                    xp = newXp,
                    level = newLevel,
                    currentStreak = newStreak,
                    longestStreak = longest
                )
            )

            // Add transaction if reward won
            if (rewardCoins > 0) {
                dao.insertTransaction(
                    CoinTransactionEntity(
                        userId = user.id,
                        amount = rewardCoins,
                        type = "PREDICTION_REWARD",
                        description = "Reward won (${multiplier}x) on player prediction! Accuracy: $accuracy%",
                        balanceAfter = newCoins
                    )
                )
            }

            // Notification
            val title = if (isWon) "🎉 Prediction Won (${multiplier}x Reward)!" else "Match Result Settled"
            val message = if (isWon) {
                "Your prediction of ${pred.predictedPoints} pts achieved $accuracy% accuracy. You earned $rewardCoins virtual coins and $xpEarned XP!"
            } else {
                "Actual points: $actualPoints vs your prediction: ${pred.predictedPoints}. Accuracy: $accuracy%. Better luck next time!"
            }
            dao.insertNotification(
                NotificationEntity(
                    userId = user.id,
                    title = title,
                    message = message,
                    type = "PREDICTION_RESULT"
                )
            )

            // Check achievements
            if (isWon) {
                // First Prediction achievement
                dao.insertUserAchievement(
                    UserAchievementEntity(userId = user.id, achievementId = 1)
                )
                if (accuracy >= 98.0) {
                    dao.insertUserAchievement(
                        UserAchievementEntity(userId = user.id, achievementId = 2)
                    )
                }
                if (newStreak >= 3) {
                    dao.insertUserAchievement(
                        UserAchievementEntity(userId = user.id, achievementId = 3)
                    )
                }
            }
        }
    }

    // ADMIN OPERATIONS
    suspend fun getAdminDashboardStats(): AdminDashboardStats = withContext(Dispatchers.IO) {
        val users = dao.getAllUsersList()
        val matches = dao.getAllMatchesFlow().first()
        val predictions = dao.getAllPredictionsList()

        val totalUsers = users.size
        val activeUsers = users.count { it.coins > 0 }
        val liveMatches = matches.count { it.status == "LIVE" }
        val upcomingMatches = matches.count { it.status == "SCHEDULED" }
        val totalPredictions = predictions.size
        val completedPredictions = predictions.count { it.status == "WON" || it.status == "LOST" }
        val completedList = predictions.filter { it.status == "WON" || it.status == "LOST" }
        val avgAccuracy = if (completedList.isNotEmpty()) {
            (completedList.sumOf { it.accuracy } / completedList.size * 100).toInt() / 100.0
        } else 0.0
        val coinsDistributed = predictions.sumOf { it.rewardCoins }

        AdminDashboardStats(
            totalUsers = totalUsers,
            activeUsers = activeUsers,
            liveMatches = liveMatches,
            upcomingMatches = upcomingMatches,
            predictionsToday = totalPredictions,
            predictionsCompleted = completedPredictions,
            totalCoinsDistributed = coinsDistributed,
            averageAccuracy = avgAccuracy
        )
    }

    suspend fun adjustUserCoins(adminId: Long, targetUserId: Long, amount: Int, reason: String): Result<Unit> = withContext(Dispatchers.IO) {
        val user = dao.getUserById(targetUserId) ?: return@withContext Result.failure(Exception("User not found"))
        val newBalance = (user.coins + amount).coerceAtLeast(0)
        dao.updateUser(user.copy(coins = newBalance))
        dao.insertTransaction(
            CoinTransactionEntity(
                userId = targetUserId,
                amount = amount,
                type = "ADMIN_ADJUSTMENT",
                description = "Admin adjustment: $reason",
                balanceAfter = newBalance
            )
        )
        dao.insertAuditLog(
            AuditLogEntity(
                adminId = adminId,
                action = "ADJUST_COINS",
                target = "User ${user.username} (ID: ${user.id})",
                oldValue = "${user.coins} coins",
                newValue = "$newBalance coins (Reason: $reason)"
            )
        )
        Result.success(Unit)
    }

    suspend fun toggleMatchPredictionLock(matchId: Long): Result<Boolean> = withContext(Dispatchers.IO) {
        val match = dao.getMatchById(matchId) ?: return@withContext Result.failure(Exception("Match not found"))
        val newLocked = !match.predictionsLocked
        dao.updateMatch(match.copy(predictionsLocked = newLocked))
        Result.success(newLocked)
    }

    val scoringRulesFlow: Flow<List<ScoringRuleEntity>> = dao.getAllScoringRulesFlow()

    suspend fun updateScoringRule(rule: ScoringRuleEntity, adminId: Long): Result<Unit> = withContext(Dispatchers.IO) {
        val oldRules = dao.getAllScoringRules().associateBy { it.ruleKey }
        val oldVal = oldRules[rule.ruleKey]?.pointValue?.toString() ?: "0.0"
        dao.updateScoringRule(rule)
        dao.insertAuditLog(
            AuditLogEntity(
                adminId = adminId,
                action = "UPDATE_SCORING_RULE",
                target = rule.name,
                oldValue = oldVal,
                newValue = rule.pointValue.toString()
            )
        )
        Result.success(Unit)
    }

    val auditLogsFlow: Flow<List<AuditLogEntity>> = dao.getAllAuditLogsFlow()
}
