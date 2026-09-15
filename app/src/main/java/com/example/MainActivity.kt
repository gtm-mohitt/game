package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.example.ui.CricketViewModel
import com.example.ui.MainTab
import com.example.ui.components.AppBottomBar
import com.example.ui.components.AppTopBar
import com.example.ui.components.AuthDialog
import com.example.ui.components.NotificationsDialog
import com.example.ui.components.PredictionDialog
import com.example.ui.screens.AdminDashboardScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LeaderboardScreen
import com.example.ui.screens.MatchDetailScreen
import com.example.ui.screens.MatchesScreen
import com.example.ui.screens.PredictionsScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.Navy950
import kotlinx.coroutines.flow.collectLatest

class MainActivity : ComponentActivity() {

    private val viewModel: CricketViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                CricketPredictApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun CricketPredictApp(viewModel: CricketViewModel) {
    val currentUser by viewModel.currentUser.collectAsState()
    val currentTab by viewModel.currentTab.collectAsState()
    val matches by viewModel.matches.collectAsState()
    val userPredictions by viewModel.userPredictions.collectAsState()
    val unreadNotifications by viewModel.unreadNotificationsCount.collectAsState()
    val leaderboard by viewModel.leaderboard.collectAsState()
    val isSimulating by viewModel.isSimulating.collectAsState()

    val selectedMatchId by viewModel.selectedMatchId.collectAsState()
    val currentMatchWithTeams by viewModel.currentMatchWithTeams.collectAsState()
    val currentMatchPlayers by viewModel.currentMatchPlayers.collectAsState()

    val showPredictionDialog by viewModel.showPredictionDialog.collectAsState()
    val predictingPlayer by viewModel.predictingPlayer.collectAsState()
    val showAdminScreen by viewModel.showAdminScreen.collectAsState()
    val showNotificationsDialog by viewModel.showNotificationsDialog.collectAsState()
    val showAuthDialog by viewModel.showAuthDialog.collectAsState()

    val matchFilter by viewModel.matchStatusFilter.collectAsState()
    val matchSearch by viewModel.matchSearchQuery.collectAsState()
    val predictionFilter by viewModel.predictionFilter.collectAsState()
    val adminStats by viewModel.adminStats.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.toastMessage.collectLatest { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    // Handle Back Button
    BackHandler(enabled = showAdminScreen || selectedMatchId != null) {
        if (showAdminScreen) {
            viewModel.toggleAdminScreen(false)
        } else if (selectedMatchId != null) {
            viewModel.closeMatchDetail()
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("cricket_predict_scaffold"),
        containerColor = Navy950,
        topBar = {
            if (!showAdminScreen && selectedMatchId == null) {
                AppTopBar(
                    user = currentUser,
                    unreadCount = unreadNotifications,
                    onCoinsClick = { viewModel.selectTab(MainTab.PROFILE) },
                    onNotificationsClick = { viewModel.toggleNotifications(true) },
                    onProfileClick = { viewModel.toggleAuthDialog(true) },
                    onAdminClick = { viewModel.toggleAdminScreen(true) }
                )
            }
        },
        bottomBar = {
            if (!showAdminScreen && selectedMatchId == null) {
                AppBottomBar(
                    currentTab = currentTab,
                    onTabSelected = { viewModel.selectTab(it) }
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Navy950)
                .padding(innerPadding)
        ) {
            when {
                showAdminScreen -> {
                    AdminDashboardScreen(
                        stats = adminStats,
                        matches = matches,
                        scoringRulesFlow = viewModel.repository.scoringRulesFlow,
                        auditLogsFlow = viewModel.repository.auditLogsFlow,
                        isSimulating = isSimulating,
                        onBack = { viewModel.toggleAdminScreen(false) },
                        onRefreshStats = { viewModel.refreshAdminStats() },
                        onToggleLock = { viewModel.toggleLock(it) },
                        onUpdateScoringRule = { viewModel.updateScoringRule(it) },
                        onAdjustCoins = { targetId, amt, reason -> viewModel.adjustUserCoins(targetId, amt, reason) },
                        onStartSimulation = { viewModel.startSimulation(it) },
                        onPauseSimulation = { viewModel.pauseSimulation() },
                        onNextDelivery = { viewModel.nextDelivery(it) },
                        onFinishMatch = { viewModel.finishMatch(it) }
                    )
                }

                selectedMatchId != null -> {
                    MatchDetailScreen(
                        matchWithTeams = currentMatchWithTeams,
                        players = currentMatchPlayers,
                        isSimulating = isSimulating,
                        onBack = { viewModel.closeMatchDetail() },
                        onPredictPlayer = { player ->
                            selectedMatchId?.let { mId ->
                                viewModel.openPredictionDialog(player, mId)
                            }
                        },
                        onPlayerClick = { player ->
                            viewModel.openPlayerDetail(player)
                        },
                        onStartSimulation = { selectedMatchId?.let { viewModel.startSimulation(it) } },
                        onPauseSimulation = { viewModel.pauseSimulation() },
                        onNextDelivery = { selectedMatchId?.let { viewModel.nextDelivery(it) } },
                        onFinishMatch = { selectedMatchId?.let { viewModel.finishMatch(it) } }
                    )
                }

                else -> {
                    when (currentTab) {
                        MainTab.HOME -> {
                            HomeScreen(
                                user = currentUser,
                                matches = matches,
                                userPredictions = userPredictions,
                                topLeaderboard = leaderboard,
                                isSimulating = isSimulating,
                                onViewMatch = { viewModel.openMatchDetail(it) },
                                onViewAllMatches = { viewModel.selectTab(MainTab.MATCHES) },
                                onViewAllPredictions = { viewModel.selectTab(MainTab.PREDICTIONS) },
                                onViewLeaderboard = { viewModel.selectTab(MainTab.LEADERBOARD) },
                                onStartSimulation = { viewModel.startSimulation(it) },
                                onPauseSimulation = { viewModel.pauseSimulation() },
                                onNextDelivery = { viewModel.nextDelivery(it) },
                                onFinishMatch = { viewModel.finishMatch(it) }
                            )
                        }

                        MainTab.MATCHES -> {
                            MatchesScreen(
                                matches = matches,
                                selectedFilter = matchFilter,
                                searchQuery = matchSearch,
                                onFilterChange = { viewModel.setMatchFilter(it) },
                                onSearchChange = { viewModel.setMatchSearch(it) },
                                onViewMatch = { viewModel.openMatchDetail(it) }
                            )
                        }

                        MainTab.PREDICTIONS -> {
                            PredictionsScreen(
                                predictions = userPredictions,
                                selectedFilter = predictionFilter,
                                onFilterChange = { viewModel.setPredictionFilter(it) },
                                onMatchClick = { viewModel.openMatchDetail(it) }
                            )
                        }

                        MainTab.LEADERBOARD -> {
                            LeaderboardScreen(
                                leaderboard = leaderboard
                            )
                        }

                        MainTab.PROFILE -> {
                            ProfileScreen(
                                user = currentUser,
                                transactionsFlow = viewModel.repository.getUserTransactionsFlow(),
                                achievementsFlow = viewModel.repository.achievementsFlow,
                                missionsFlow = viewModel.repository.missionsFlow,
                                onOpenAuthDialog = { viewModel.toggleAuthDialog(true) },
                                onOpenAdminPortal = { viewModel.toggleAdminScreen(true) }
                            )
                        }
                    }
                }
            }
        }
    }

    // Prediction Modal Dialog
    if (showPredictionDialog && predictingPlayer != null) {
        PredictionDialog(
            playerWithStats = predictingPlayer!!,
            user = currentUser,
            onDismiss = { viewModel.closePredictionDialog() },
            onConfirmPrediction = { points, coins ->
                viewModel.submitPrediction(points, coins)
            }
        )
    }

    // Notifications Dialog
    if (showNotificationsDialog) {
        val notifications by viewModel.repository.getUserNotificationsFlow().collectAsState(initial = emptyList())
        NotificationsDialog(
            notifications = notifications,
            onDismiss = { viewModel.toggleNotifications(false) },
            onMarkRead = { viewModel.markNotificationRead(it) },
            onMarkAllRead = { viewModel.markAllNotificationsRead() }
        )
    }

    // Auth & Account Switcher Dialog
    if (showAuthDialog) {
        AuthDialog(
            currentUser = currentUser,
            onDismiss = { viewModel.toggleAuthDialog(false) },
            onLogin = { email, pass -> viewModel.login(email, pass) },
            onRegister = { email, user, pass -> viewModel.register(email, user, pass) },
            onSwitchUser = { userId ->
                viewModel.switchUser(userId)
                viewModel.toggleAuthDialog(false)
            }
        )
    }
}
