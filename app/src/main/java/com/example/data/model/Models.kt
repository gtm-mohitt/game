package com.example.data.model

import com.example.data.local.entities.MatchEntity
import com.example.data.local.entities.PlayerEntity
import com.example.data.local.entities.PlayerMatchStatsEntity
import com.example.data.local.entities.PredictionEntity
import com.example.data.local.entities.TeamEntity
import com.example.data.local.entities.UserEntity

data class MatchWithTeams(
    val match: MatchEntity,
    val teamA: TeamEntity,
    val teamB: TeamEntity
)

data class PlayerWithMatchStats(
    val player: PlayerEntity,
    val team: TeamEntity,
    val stats: PlayerMatchStatsEntity?,
    val currentPoints: Int,
    val userPrediction: PredictionEntity? = null
)

data class PredictionWithDetails(
    val prediction: PredictionEntity,
    val player: PlayerEntity,
    val team: TeamEntity,
    val match: MatchEntity,
    val currentPoints: Int
)

data class LeaderboardEntry(
    val rank: Int,
    val user: UserEntity,
    val totalPredictions: Int,
    val wonPredictions: Int,
    val averageAccuracy: Double,
    val totalCoinsWon: Int
)

data class AdminDashboardStats(
    val totalUsers: Int,
    val activeUsers: Int,
    val liveMatches: Int,
    val upcomingMatches: Int,
    val predictionsToday: Int,
    val predictionsCompleted: Int,
    val totalCoinsDistributed: Int,
    val averageAccuracy: Double
)
