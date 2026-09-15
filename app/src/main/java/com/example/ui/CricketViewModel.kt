package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.entities.PlayerEntity
import com.example.data.local.entities.ScoringRuleEntity
import com.example.data.local.entities.UserEntity
import com.example.data.model.AdminDashboardStats
import com.example.data.model.LeaderboardEntry
import com.example.data.model.MatchWithTeams
import com.example.data.model.PlayerWithMatchStats
import com.example.data.model.PredictionWithDetails
import com.example.data.repository.CricketRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class MainTab {
    HOME,
    MATCHES,
    PREDICTIONS,
    LEADERBOARD,
    PROFILE
}

class CricketViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application, viewModelScope)
    val repository = CricketRepository(db.cricketDao(), viewModelScope)

    // UI Navigation State
    private val _currentTab = MutableStateFlow(MainTab.HOME)
    val currentTab: StateFlow<MainTab> = _currentTab.asStateFlow()

    private val _selectedMatchId = MutableStateFlow<Long?>(null)
    val selectedMatchId: StateFlow<Long?> = _selectedMatchId.asStateFlow()

    private val _selectedPlayer = MutableStateFlow<PlayerWithMatchStats?>(null)
    val selectedPlayer: StateFlow<PlayerWithMatchStats?> = _selectedPlayer.asStateFlow()

    // Dialog States
    private val _showPredictionDialog = MutableStateFlow(false)
    val showPredictionDialog: StateFlow<Boolean> = _showPredictionDialog.asStateFlow()

    private val _predictingPlayer = MutableStateFlow<PlayerWithMatchStats?>(null)
    val predictingPlayer: StateFlow<PlayerWithMatchStats?> = _predictingPlayer.asStateFlow()

    private val _predictingMatchId = MutableStateFlow<Long?>(null)
    val predictingMatchId: StateFlow<Long?> = _predictingMatchId.asStateFlow()

    private val _showAdminScreen = MutableStateFlow(false)
    val showAdminScreen: StateFlow<Boolean> = _showAdminScreen.asStateFlow()

    private val _showNotificationsDialog = MutableStateFlow(false)
    val showNotificationsDialog: StateFlow<Boolean> = _showNotificationsDialog.asStateFlow()

    private val _showAuthDialog = MutableStateFlow(false)
    val showAuthDialog: StateFlow<Boolean> = _showAuthDialog.asStateFlow()

    // Filters
    private val _matchStatusFilter = MutableStateFlow("ALL") // "ALL", "LIVE", "SCHEDULED", "COMPLETED"
    val matchStatusFilter: StateFlow<String> = _matchStatusFilter.asStateFlow()

    private val _matchSearchQuery = MutableStateFlow("")
    val matchSearchQuery: StateFlow<String> = _matchSearchQuery.asStateFlow()

    private val _playerRoleFilter = MutableStateFlow("ALL") // "ALL", "BATTER", "BOWLER", "ALL_ROUNDER", "WICKETKEEPER"
    val playerRoleFilter: StateFlow<String> = _playerRoleFilter.asStateFlow()

    private val _predictionFilter = MutableStateFlow("ALL") // "ALL", "PENDING", "LIVE", "COMPLETED"
    val predictionFilter: StateFlow<String> = _predictionFilter.asStateFlow()

    // Feedback Messages
    private val _toastMessage = MutableSharedFlow<String>()
    val toastMessage: SharedFlow<String> = _toastMessage.asSharedFlow()

    // Flows from repository
    val currentUser: StateFlow<UserEntity?> = repository.currentUserFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val matches: StateFlow<List<MatchWithTeams>> = repository.matchesWithTeamsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userPredictions: StateFlow<List<PredictionWithDetails>> = repository.userPredictionsWithDetailsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val unreadNotificationsCount: StateFlow<Int> = repository.getUnreadCountFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val leaderboard: StateFlow<List<LeaderboardEntry>> = repository.leaderboardFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val isSimulating: StateFlow<Boolean> = repository.isSimulating

    // Active Match details flow
    val currentMatchWithTeams = _selectedMatchId.flatMapLatest { id ->
        if (id != null) repository.getMatchDetailsFlow(id) else flowOf(null)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Players for selected match flow
    val currentMatchPlayers = _selectedMatchId.flatMapLatest { id ->
        if (id != null) repository.getPlayersForMatchFlow(id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Admin Stats
    private val _adminStats = MutableStateFlow<AdminDashboardStats?>(null)
    val adminStats: StateFlow<AdminDashboardStats?> = _adminStats.asStateFlow()

    fun selectTab(tab: MainTab) {
        _currentTab.value = tab
        if (tab != MainTab.MATCHES) {
            // retain match selection if needed, or clear if jumping away
        }
    }

    fun openMatchDetail(matchId: Long) {
        _selectedMatchId.value = matchId
    }

    fun closeMatchDetail() {
        _selectedMatchId.value = null
    }

    fun openPlayerDetail(player: PlayerWithMatchStats) {
        _selectedPlayer.value = player
    }

    fun closePlayerDetail() {
        _selectedPlayer.value = null
    }

    fun openPredictionDialog(player: PlayerWithMatchStats, matchId: Long) {
        _predictingPlayer.value = player
        _predictingMatchId.value = matchId
        _showPredictionDialog.value = true
    }

    fun closePredictionDialog() {
        _showPredictionDialog.value = false
        _predictingPlayer.value = null
        _predictingMatchId.value = null
    }

    fun setMatchFilter(filter: String) {
        _matchStatusFilter.value = filter
    }

    fun setMatchSearch(query: String) {
        _matchSearchQuery.value = query
    }

    fun setPlayerRoleFilter(filter: String) {
        _playerRoleFilter.value = filter
    }

    fun setPredictionFilter(filter: String) {
        _predictionFilter.value = filter
    }

    fun toggleAdminScreen(show: Boolean) {
        _showAdminScreen.value = show
        if (show) {
            refreshAdminStats()
        }
    }

    fun toggleNotifications(show: Boolean) {
        _showNotificationsDialog.value = show
    }

    fun toggleAuthDialog(show: Boolean) {
        _showAuthDialog.value = show
    }

    fun submitPrediction(predictedPoints: Int, coinsToUse: Int) {
        val player = _predictingPlayer.value ?: return
        val matchId = _predictingMatchId.value ?: return
        val user = currentUser.value ?: return

        if (coinsToUse <= 0) {
            emitToast("Coins used must be greater than zero")
            return
        }
        if (coinsToUse > user.coins) {
            emitToast("Insufficient coins! You have ${user.coins} virtual coins")
            return
        }

        viewModelScope.launch {
            val result = repository.submitPrediction(
                matchId = matchId,
                playerId = player.player.id,
                predictedPoints = predictedPoints,
                coinsUsed = coinsToUse
            )
            result.onSuccess {
                emitToast("Prediction placed on ${player.player.name} for $predictedPoints pts!")
                closePredictionDialog()
            }.onFailure { error ->
                emitToast("Failed: ${error.message}")
            }
        }
    }

    fun startSimulation(matchId: Long) {
        repository.startSimulation(matchId)
        emitToast("Live simulator started! Watch ball-by-ball updates.")
    }

    fun pauseSimulation() {
        repository.pauseSimulation()
        emitToast("Simulator paused.")
    }

    fun nextDelivery(matchId: Long) {
        viewModelScope.launch {
            repository.simulateNextDelivery(matchId)
        }
    }

    fun finishMatch(matchId: Long) {
        viewModelScope.launch {
            repository.finishMatchAndSettle(matchId)
            emitToast("Match completed! Actual points, accuracy, and rewards settled.")
        }
    }

    fun refreshAdminStats() {
        viewModelScope.launch {
            _adminStats.value = repository.getAdminDashboardStats()
        }
    }

    fun adjustUserCoins(targetUserId: Long, amount: Int, reason: String) {
        viewModelScope.launch {
            val adminId = currentUser.value?.id ?: 2L
            val result = repository.adjustUserCoins(adminId, targetUserId, amount, reason)
            result.onSuccess {
                emitToast("Adjusted coins by $amount for user ID $targetUserId")
                refreshAdminStats()
            }.onFailure {
                emitToast("Adjustment failed: ${it.message}")
            }
        }
    }

    fun updateScoringRule(rule: ScoringRuleEntity) {
        viewModelScope.launch {
            val adminId = currentUser.value?.id ?: 2L
            repository.updateScoringRule(rule, adminId)
            emitToast("Updated scoring rule: ${rule.name}")
        }
    }

    fun toggleLock(matchId: Long) {
        viewModelScope.launch {
            repository.toggleMatchPredictionLock(matchId)
            emitToast("Prediction lock toggled.")
        }
    }

    fun login(email: String, pass: String) {
        viewModelScope.launch {
            val res = repository.login(email, pass)
            res.onSuccess {
                emitToast("Logged in as ${it.username}")
                _showAuthDialog.value = false
            }.onFailure {
                emitToast(it.message ?: "Login failed")
            }
        }
    }

    fun register(email: String, username: String, pass: String) {
        viewModelScope.launch {
            val res = repository.register(email, username, pass)
            res.onSuccess {
                emitToast("Account created for ${it.username} with 1,000 free coins!")
                _showAuthDialog.value = false
            }.onFailure {
                emitToast(it.message ?: "Registration failed")
            }
        }
    }

    fun switchUser(userId: Long) {
        repository.switchUser(userId)
        emitToast("Switched user account")
    }

    fun markNotificationRead(id: Long) {
        viewModelScope.launch {
            repository.markNotificationRead(id)
        }
    }

    fun markAllNotificationsRead() {
        viewModelScope.launch {
            repository.markAllNotificationsRead()
            emitToast("All notifications marked as read")
        }
    }

    private fun emitToast(msg: String) {
        viewModelScope.launch {
            _toastMessage.emit(msg)
        }
    }
}
