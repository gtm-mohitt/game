package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
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
import com.example.data.local.entities.RewardRuleEntity
import com.example.data.local.entities.ScoringRuleEntity
import com.example.data.local.entities.TeamEntity
import com.example.data.local.entities.UserAchievementEntity
import com.example.data.local.entities.UserEntity
import com.example.data.local.entities.UserMissionEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserEntity::class,
        TeamEntity::class,
        PlayerEntity::class,
        MatchEntity::class,
        PlayerMatchStatsEntity::class,
        PredictionEntity::class,
        CoinTransactionEntity::class,
        AchievementEntity::class,
        UserAchievementEntity::class,
        MissionEntity::class,
        UserMissionEntity::class,
        ScoringRuleEntity::class,
        RewardRuleEntity::class,
        NotificationEntity::class,
        AuditLogEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun cricketDao(): CricketDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "cricket_predict.db"
                )
                    .addCallback(DatabaseCallback(scope))
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateInitialData(database.cricketDao())
                }
            }
        }

        private suspend fun populateInitialData(dao: CricketDao) {
            // 1. Seed Demo Users
            val demoUserId = dao.insertUser(
                UserEntity(
                    email = "demo@example.com",
                    username = "DemoPredictor",
                    passwordHash = "Demo@12345",
                    role = "USER",
                    coins = 1000,
                    xp = 420,
                    level = 1,
                    currentStreak = 2,
                    longestStreak = 4,
                    accuracyStreak = 2,
                    avatarInitial = "D",
                    avatarColorHex = "#10B981"
                )
            )

            val adminUserId = dao.insertUser(
                UserEntity(
                    email = "admin@example.com",
                    username = "AdminOperator",
                    passwordHash = "Admin@12345",
                    role = "ADMIN",
                    coins = 5000,
                    xp = 2600,
                    level = 4,
                    currentStreak = 5,
                    longestStreak = 8,
                    accuracyStreak = 4,
                    avatarInitial = "A",
                    avatarColorHex = "#38BDF8"
                )
            )

            // Welcome transaction for demo user
            dao.insertTransaction(
                CoinTransactionEntity(
                    userId = demoUserId,
                    amount = 1000,
                    type = "WELCOME_BONUS",
                    description = "Welcome to Cricket Predict! Free starting coins.",
                    balanceAfter = 1000
                )
            )

            // 2. Seed Teams
            val teams = listOf(
                TeamEntity(id = 1, name = "India", shortName = "IND", code = "IND", primaryColorHex = "#1D4ED8", flagEmoji = "🇮🇳"),
                TeamEntity(id = 2, name = "Australia", shortName = "AUS", code = "AUS", primaryColorHex = "#CA8A04", flagEmoji = "🇦🇺"),
                TeamEntity(id = 3, name = "England", shortName = "ENG", code = "ENG", primaryColorHex = "#DC2626", flagEmoji = "🏴󠁧󠁢󠁥󠁮󠁧󠁿"),
                TeamEntity(id = 4, name = "South Africa", shortName = "SA", code = "SA", primaryColorHex = "#15803D", flagEmoji = "🇿🇦")
            )
            dao.insertTeams(teams)

            // 3. Seed Players (22 players for India and Australia Playing XI)
            val players = listOf(
                // India Playing XI
                PlayerEntity(id = 1, teamId = 1, name = "Rohit Sharma", role = "BATTER", jerseyNumber = 45, avatarInitial = "RS", avatarColorHex = "#1E40AF"),
                PlayerEntity(id = 2, teamId = 1, name = "Shubman Gill", role = "BATTER", jerseyNumber = 77, avatarInitial = "SG", avatarColorHex = "#2563EB"),
                PlayerEntity(id = 3, teamId = 1, name = "Virat Kohli", role = "BATTER", jerseyNumber = 18, avatarInitial = "VK", avatarColorHex = "#3B82F6"),
                PlayerEntity(id = 4, teamId = 1, name = "KL Rahul", role = "WICKETKEEPER", jerseyNumber = 1, avatarInitial = "KL", avatarColorHex = "#60A5FA"),
                PlayerEntity(id = 5, teamId = 1, name = "Rishabh Pant", role = "WICKETKEEPER", jerseyNumber = 17, avatarInitial = "RP", avatarColorHex = "#1D4ED8"),
                PlayerEntity(id = 6, teamId = 1, name = "Hardik Pandya", role = "ALL_ROUNDER", jerseyNumber = 33, avatarInitial = "HP", avatarColorHex = "#0284C7"),
                PlayerEntity(id = 7, teamId = 1, name = "Ravindra Jadeja", role = "ALL_ROUNDER", jerseyNumber = 8, avatarInitial = "RJ", avatarColorHex = "#0369A1"),
                PlayerEntity(id = 8, teamId = 1, name = "Axar Patel", role = "ALL_ROUNDER", jerseyNumber = 20, avatarInitial = "AP", avatarColorHex = "#075985"),
                PlayerEntity(id = 9, teamId = 1, name = "Jasprit Bumrah", role = "BOWLER", jerseyNumber = 93, avatarInitial = "JB", avatarColorHex = "#1E3A8A"),
                PlayerEntity(id = 10, teamId = 1, name = "Mohammed Shami", role = "BOWLER", jerseyNumber = 11, avatarInitial = "MS", avatarColorHex = "#1E3A8A"),
                PlayerEntity(id = 11, teamId = 1, name = "Mohammed Siraj", role = "BOWLER", jerseyNumber = 13, avatarInitial = "SI", avatarColorHex = "#172554"),

                // Australia Playing XI
                PlayerEntity(id = 12, teamId = 2, name = "Travis Head", role = "BATTER", jerseyNumber = 62, avatarInitial = "TH", avatarColorHex = "#B45309"),
                PlayerEntity(id = 13, teamId = 2, name = "David Warner", role = "BATTER", jerseyNumber = 31, avatarInitial = "DW", avatarColorHex = "#D97706"),
                PlayerEntity(id = 14, teamId = 2, name = "Steve Smith", role = "BATTER", jerseyNumber = 49, avatarInitial = "SS", avatarColorHex = "#F59E0B"),
                PlayerEntity(id = 15, teamId = 2, name = "Marnus Labuschagne", role = "BATTER", jerseyNumber = 33, avatarInitial = "ML", avatarColorHex = "#FBBF24"),
                PlayerEntity(id = 16, teamId = 2, name = "Glenn Maxwell", role = "ALL_ROUNDER", jerseyNumber = 32, avatarInitial = "GM", avatarColorHex = "#EAB308"),
                PlayerEntity(id = 17, teamId = 2, name = "Marcus Stoinis", role = "ALL_ROUNDER", jerseyNumber = 17, avatarInitial = "ST", avatarColorHex = "#CA8A04"),
                PlayerEntity(id = 18, teamId = 2, name = "Alex Carey", role = "WICKETKEEPER", jerseyNumber = 4, avatarInitial = "AC", avatarColorHex = "#A16207"),
                PlayerEntity(id = 19, teamId = 2, name = "Pat Cummins", role = "BOWLER", jerseyNumber = 30, avatarInitial = "PC", avatarColorHex = "#854D0E"),
                PlayerEntity(id = 20, teamId = 2, name = "Mitchell Starc", role = "BOWLER", jerseyNumber = 56, avatarInitial = "MS", avatarColorHex = "#713F12"),
                PlayerEntity(id = 21, teamId = 2, name = "Josh Hazlewood", role = "BOWLER", jerseyNumber = 38, avatarInitial = "JH", avatarColorHex = "#78350F"),
                PlayerEntity(id = 22, teamId = 2, name = "Adam Zampa", role = "BOWLER", jerseyNumber = 88, avatarInitial = "AZ", avatarColorHex = "#451A03")
            )
            dao.insertPlayers(players)

            // 4. Seed Matches
            val matches = listOf(
                MatchEntity(
                    id = 1,
                    teamAId = 1,
                    teamBId = 2,
                    tournament = "Border-Gavaskar Trophy 2026",
                    venue = "Melbourne Cricket Ground (MCG)",
                    status = "LIVE",
                    scheduledTime = "LIVE NOW",
                    scoreTeamA = 156,
                    wicketsTeamA = 3,
                    oversTeamA = 18.2,
                    scoreTeamB = 0,
                    wicketsTeamB = 0,
                    oversTeamB = 0.0,
                    currentInnings = 1,
                    currentBattingTeamId = 1,
                    predictionsLocked = false,
                    summaryText = "India 156/3 (18.2 ov) vs Australia"
                ),
                MatchEntity(
                    id = 2,
                    teamAId = 3,
                    teamBId = 4,
                    tournament = "T20 Super Series",
                    venue = "Lord's Cricket Ground, London",
                    status = "SCHEDULED",
                    scheduledTime = "Starts Tomorrow, 19:30 IST",
                    scoreTeamA = 0,
                    wicketsTeamA = 0,
                    oversTeamA = 0.0,
                    scoreTeamB = 0,
                    wicketsTeamB = 0,
                    oversTeamB = 0.0,
                    currentInnings = 1,
                    currentBattingTeamId = 3,
                    predictionsLocked = false,
                    summaryText = "Match starts in 18 hours"
                ),
                MatchEntity(
                    id = 3,
                    teamAId = 1,
                    teamBId = 4,
                    tournament = "World Cricket Championship",
                    venue = "Eden Gardens, Kolkata",
                    status = "COMPLETED",
                    scheduledTime = "Completed Yesterday",
                    scoreTeamA = 188,
                    wicketsTeamA = 5,
                    oversTeamA = 20.0,
                    scoreTeamB = 164,
                    wicketsTeamB = 8,
                    oversTeamB = 20.0,
                    currentInnings = 2,
                    predictionsLocked = true,
                    winnerTeamName = "India",
                    summaryText = "India won by 24 runs"
                )
            )
            dao.insertMatches(matches)

            // 5. Seed Player Match Stats for Match 1 (Live)
            val stats = listOf(
                PlayerMatchStatsEntity(matchId = 1, playerId = 1, runs = 42, balls = 28, fours = 4, sixes = 2, points = 50),
                PlayerMatchStatsEntity(matchId = 1, playerId = 2, runs = 18, balls = 14, fours = 2, sixes = 0, points = 20),
                PlayerMatchStatsEntity(matchId = 1, playerId = 3, runs = 64, balls = 44, fours = 5, sixes = 2, points = 83), // Virat Kohli
                PlayerMatchStatsEntity(matchId = 1, playerId = 4, runs = 22, balls = 16, fours = 2, sixes = 1, points = 26),
                PlayerMatchStatsEntity(matchId = 1, playerId = 5, runs = 0, balls = 0, points = 0),
                PlayerMatchStatsEntity(matchId = 1, playerId = 6, runs = 0, balls = 0, points = 0),
                PlayerMatchStatsEntity(matchId = 1, playerId = 7, runs = 0, balls = 0, points = 0),
                PlayerMatchStatsEntity(matchId = 1, playerId = 8, runs = 0, balls = 0, points = 0),
                PlayerMatchStatsEntity(matchId = 1, playerId = 9, runs = 0, balls = 0, points = 0),
                PlayerMatchStatsEntity(matchId = 1, playerId = 10, runs = 0, balls = 0, points = 0),
                PlayerMatchStatsEntity(matchId = 1, playerId = 11, runs = 0, balls = 0, points = 0),

                // Australia bowling
                PlayerMatchStatsEntity(matchId = 1, playerId = 19, oversBowled = 4.0, runsConceded = 28, wicketsTaken = 1, points = 20), // Cummins
                PlayerMatchStatsEntity(matchId = 1, playerId = 20, oversBowled = 3.2, runsConceded = 32, wicketsTaken = 2, catches = 1, points = 48), // Starc
                PlayerMatchStatsEntity(matchId = 1, playerId = 21, oversBowled = 4.0, runsConceded = 35, wicketsTaken = 0, points = 0), // Hazlewood
                PlayerMatchStatsEntity(matchId = 1, playerId = 22, oversBowled = 4.0, runsConceded = 29, wicketsTaken = 0, catches = 1, points = 8) // Zampa
            )
            dao.insertPlayerStats(stats)

            // 6. Seed default Scoring Rules
            val scoringRules = listOf(
                ScoringRuleEntity(ruleKey = "RUN", name = "Run Scored", category = "BATTING", pointValue = 1.0, description = "+1 point per run"),
                ScoringRuleEntity(ruleKey = "FOUR", name = "Four Boundary", category = "BATTING", pointValue = 1.0, description = "+1 bonus for boundary 4"),
                ScoringRuleEntity(ruleKey = "SIX", name = "Six Boundary", category = "BATTING", pointValue = 2.0, description = "+2 bonus for maximum 6"),
                ScoringRuleEntity(ruleKey = "FIFTY", name = "Half Century", category = "BATTING", pointValue = 10.0, description = "+10 bonus for reaching 50 runs"),
                ScoringRuleEntity(ruleKey = "CENTURY", name = "Century", category = "BATTING", pointValue = 20.0, description = "+20 bonus for reaching 100 runs"),
                ScoringRuleEntity(ruleKey = "WICKET", name = "Wicket Taken", category = "BOWLING", pointValue = 20.0, description = "+20 points per wicket"),
                ScoringRuleEntity(ruleKey = "MAIDEN", name = "Maiden Over", category = "BOWLING", pointValue = 10.0, description = "+10 points per maiden over"),
                ScoringRuleEntity(ruleKey = "THREE_WKT", name = "3-Wicket Haul", category = "BOWLING", pointValue = 10.0, description = "+10 bonus for 3 wickets"),
                ScoringRuleEntity(ruleKey = "FIVE_WKT", name = "5-Wicket Haul", category = "BOWLING", pointValue = 20.0, description = "+20 bonus for 5 wickets"),
                ScoringRuleEntity(ruleKey = "CATCH", name = "Catch Taken", category = "FIELDING", pointValue = 8.0, description = "+8 points per catch"),
                ScoringRuleEntity(ruleKey = "RUN_OUT", name = "Run Out", category = "FIELDING", pointValue = 10.0, description = "+10 points per run out"),
                ScoringRuleEntity(ruleKey = "STUMPING", name = "Stumping", category = "FIELDING", pointValue = 10.0, description = "+10 points per stumping")
            )
            dao.insertScoringRules(scoringRules)

            // 7. Seed Reward Rules
            val rewardRules = listOf(
                RewardRuleEntity(minAccuracy = 99.0, maxAccuracy = 100.0, multiplier = 5, label = "99%+ Accuracy (5x)"),
                RewardRuleEntity(minAccuracy = 95.0, maxAccuracy = 98.99, multiplier = 4, label = "95% - 98.9% (4x)"),
                RewardRuleEntity(minAccuracy = 90.0, maxAccuracy = 94.99, multiplier = 3, label = "90% - 94.9% (3x)"),
                RewardRuleEntity(minAccuracy = 80.0, maxAccuracy = 89.99, multiplier = 2, label = "80% - 89.9% (2x)"),
                RewardRuleEntity(minAccuracy = 70.0, maxAccuracy = 79.99, multiplier = 1, label = "70% - 79.9% (1x)"),
                RewardRuleEntity(minAccuracy = 0.0, maxAccuracy = 69.99, multiplier = 0, label = "Below 70% (0x)")
            )
            dao.insertRewardRules(rewardRules)

            // 8. Seed Achievements
            val achievements = listOf(
                AchievementEntity(key = "FIRST_PREDICTION", title = "First Prediction", description = "Submit your very first cricket performance prediction", iconName = "SportsCricket", targetRequirement = 1, coinReward = 100, xpReward = 50),
                AchievementEntity(key = "PERFECT_PREDICTION", title = "Spot On!", description = "Achieve 98%+ prediction accuracy on any player", iconName = "MilitaryTech", targetRequirement = 1, coinReward = 300, xpReward = 150),
                AchievementEntity(key = "STREAK_3", title = "3 Correct Streak", description = "Make 3 successful predictions in a row", iconName = "Bolt", targetRequirement = 3, coinReward = 200, xpReward = 100),
                AchievementEntity(key = "PREDICTIONS_10", title = "Veteran Predictor", description = "Complete 10 total match predictions", iconName = "WorkspacePremium", targetRequirement = 10, coinReward = 500, xpReward = 250),
                AchievementEntity(key = "CENTURY_MAKER", title = "Century Predictor", description = "Predict a player who scores 100+ points", iconName = "Stars", targetRequirement = 1, coinReward = 250, xpReward = 100)
            )
            dao.insertAchievements(achievements)

            // 9. Seed Missions
            val missions = listOf(
                MissionEntity(key = "MISSION_3_PRED", title = "Triple Threat", description = "Place 3 player predictions today", targetCount = 3, coinReward = 150, xpReward = 60),
                MissionEntity(key = "MISSION_2_PLAYERS", title = "Diversified Pitch", description = "Predict performance of 2 different players in a match", targetCount = 2, coinReward = 100, xpReward = 40),
                MissionEntity(key = "MISSION_80_ACCURACY", title = "Laser Focus", description = "Achieve at least 80% accuracy on a match result", targetCount = 1, coinReward = 200, xpReward = 80)
            )
            dao.insertMissions(missions)

            // 10. Initial Notification for Demo User
            dao.insertNotification(
                NotificationEntity(
                    userId = demoUserId,
                    title = "Welcome to Cricket Predict!",
                    message = "1,000 free virtual coins have been added to your wallet. Pick an eligible player in IND vs AUS to get started!",
                    type = "REWARD_RECEIVED"
                )
            )
        }
    }
}
