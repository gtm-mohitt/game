package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SportsCricket
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.AuditLogEntity
import com.example.data.local.entities.ScoringRuleEntity
import com.example.data.model.AdminDashboardStats
import com.example.data.model.MatchWithTeams
import com.example.ui.components.LiveSimulatorControls
import com.example.ui.theme.CardBorder
import com.example.ui.theme.CardDark
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldAccent
import com.example.ui.theme.GoldYellow
import com.example.ui.theme.LiveRed
import com.example.ui.theme.Navy800
import com.example.ui.theme.Navy900
import com.example.ui.theme.Navy950
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.flow.Flow

@Composable
fun AdminDashboardScreen(
    stats: AdminDashboardStats?,
    matches: List<MatchWithTeams>,
    scoringRulesFlow: Flow<List<ScoringRuleEntity>>,
    auditLogsFlow: Flow<List<AuditLogEntity>>,
    isSimulating: Boolean,
    onBack: () -> Unit,
    onRefreshStats: () -> Unit,
    onToggleLock: (Long) -> Unit,
    onUpdateScoringRule: (ScoringRuleEntity) -> Unit,
    onAdjustCoins: (targetUserId: Long, amount: Int, reason: String) -> Unit,
    onStartSimulation: (Long) -> Unit,
    onPauseSimulation: () -> Unit,
    onNextDelivery: (Long) -> Unit,
    onFinishMatch: (Long) -> Unit
) {
    val scoringRules by scoringRulesFlow.collectAsState(initial = emptyList())
    val auditLogs by auditLogsFlow.collectAsState(initial = emptyList())

    var selectedTab by remember { mutableStateOf(0) } // 0: Overview & Simulator, 1: Scoring Rules, 2: Coin Adjustments & Logs
    val tabs = listOf("Live Engine", "Scoring Rules", "Audit & Adjust")

    var targetUserIdText by remember { mutableStateOf("1") }
    var adjustAmountText by remember { mutableStateOf("500") }
    var adjustReasonText by remember { mutableStateOf("Bonus promotion reward") }

    val liveMatch = matches.find { it.match.status == "LIVE" } ?: matches.firstOrNull()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Navy950)
            .padding(horizontal = 16.dp)
            .testTag("admin_dashboard_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Admin Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("admin_back_btn")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text(
                            text = "Admin Control Center",
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp,
                            color = TextPrimary
                        )
                        Text(
                            text = "Operations & Game Engine Console",
                            fontSize = 11.sp,
                            color = CyanAccent
                        )
                    }
                }

                IconButton(
                    onClick = onRefreshStats,
                    modifier = Modifier.testTag("admin_refresh_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh",
                        tint = TextSecondary
                    )
                }
            }
        }

        // Analytics Cards Grid
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MetricPill("Users", "${stats?.totalUsers ?: 0}", "Active: ${stats?.activeUsers ?: 0}", EmeraldAccent, Modifier.weight(1f))
                    MetricPill("Live Matches", "${stats?.liveMatches ?: 0}", "Upcoming: ${stats?.upcomingMatches ?: 0}", CyanAccent, Modifier.weight(1f))
                    MetricPill("Predictions", "${stats?.predictionsToday ?: 0}", "Settled: ${stats?.predictionsCompleted ?: 0}", GoldYellow, Modifier.weight(1f))
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MetricPill("Coins Distributed", "${stats?.totalCoinsDistributed ?: 0}", "Virtual currency", GoldYellow, Modifier.weight(1.5f))
                    MetricPill("Avg Accuracy", "${stats?.averageAccuracy ?: 0.0}%", "Game-wide", EmeraldAccent, Modifier.weight(1f))
                }
            }
        }

        // Navigation Tabs
        item {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Navy900,
                contentColor = TextPrimary,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = CyanAccent
                    )
                },
                modifier = Modifier.testTag("admin_tabs")
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    )
                }
            }
        }

        when (selectedTab) {
            0 -> {
                // Live Match Simulator Section
                if (liveMatch != null) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = CardDark),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${liveMatch.teamA.code} vs ${liveMatch.teamB.code}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = TextPrimary
                                    )
                                    OutlinedButton(
                                        onClick = { onToggleLock(liveMatch.match.id) },
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.height(34.dp).testTag("admin_toggle_lock_btn")
                                    ) {
                                        Icon(
                                            if (liveMatch.match.predictionsLocked) Icons.Default.Lock else Icons.Default.LockOpen,
                                            contentDescription = null,
                                            tint = if (liveMatch.match.predictionsLocked) LiveRed else EmeraldAccent,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = if (liveMatch.match.predictionsLocked) "LOCKED" else "OPEN",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (liveMatch.match.predictionsLocked) LiveRed else EmeraldAccent
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = liveMatch.match.summaryText,
                                    fontSize = 13.sp,
                                    color = EmeraldAccent,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    item {
                        LiveSimulatorControls(
                            matchId = liveMatch.match.id,
                            isSimulating = isSimulating,
                            onStartSimulation = { onStartSimulation(liveMatch.match.id) },
                            onPauseSimulation = onPauseSimulation,
                            onNextDelivery = { onNextDelivery(liveMatch.match.id) },
                            onFinishMatch = { onFinishMatch(liveMatch.match.id) }
                        )
                    }
                }
            }

            1 -> {
                // Scoring Rules Configurator
                item {
                    Text(
                        text = "EDIT FANTASY SCORING RULES",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = CyanAccent,
                        letterSpacing = 0.8.sp
                    )
                }

                items(scoringRules, key = { it.ruleKey }) { rule ->
                    var ruleValueText by remember(rule.pointValue) { mutableStateOf(rule.pointValue.toString()) }

                    Surface(
                        color = CardDark,
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = rule.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "${rule.category} • ${rule.description}",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            OutlinedTextField(
                                value = ruleValueText,
                                onValueChange = { ruleValueText = it },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                singleLine = true,
                                modifier = Modifier
                                    .width(70.dp)
                                    .testTag("rule_input_${rule.ruleKey}"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = CyanAccent,
                                    unfocusedBorderColor = CardBorder,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary
                                )
                            )

                            Spacer(modifier = Modifier.width(6.dp))

                            IconButton(
                                onClick = {
                                    val newVal = ruleValueText.toDoubleOrNull() ?: rule.pointValue
                                    onUpdateScoringRule(rule.copy(pointValue = newVal))
                                },
                                modifier = Modifier.testTag("rule_save_${rule.ruleKey}")
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = "Save", tint = EmeraldAccent)
                            }
                        }
                    }
                }
            }

            2 -> {
                // Coin Adjustment & Audit Logs
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = CardDark),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "ADJUST USER COIN BALANCE",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = CyanAccent
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = targetUserIdText,
                                    onValueChange = { targetUserIdText = it },
                                    label = { Text("User ID", color = TextSecondary) },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    singleLine = true,
                                    modifier = Modifier.weight(1f).testTag("adjust_user_id_input"),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = CyanAccent,
                                        unfocusedBorderColor = CardBorder,
                                        focusedTextColor = TextPrimary,
                                        unfocusedTextColor = TextPrimary
                                    )
                                )

                                OutlinedTextField(
                                    value = adjustAmountText,
                                    onValueChange = { adjustAmountText = it },
                                    label = { Text("Amount (+/-)", color = TextSecondary) },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    singleLine = true,
                                    modifier = Modifier.weight(1f).testTag("adjust_amount_input"),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = CyanAccent,
                                        unfocusedBorderColor = CardBorder,
                                        focusedTextColor = TextPrimary,
                                        unfocusedTextColor = TextPrimary
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = adjustReasonText,
                                onValueChange = { adjustReasonText = it },
                                label = { Text("Mandatory Reason for Audit Log", color = TextSecondary) },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth().testTag("adjust_reason_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = CyanAccent,
                                    unfocusedBorderColor = CardBorder,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary
                                )
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = {
                                    val uId = targetUserIdText.toLongOrNull() ?: 1L
                                    val amt = adjustAmountText.toIntOrNull() ?: 0
                                    onAdjustCoins(uId, amt, adjustReasonText)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = CyanAccent),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth().height(44.dp).testTag("execute_adjust_btn")
                            ) {
                                Text("APPLY ADJUSTMENT & LOG AUDIT", fontWeight = FontWeight.Bold, color = Navy950)
                            }
                        }
                    }
                }

                item {
                    Text(
                        text = "AUDIT LOG HISTORY",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextSecondary,
                        letterSpacing = 0.8.sp
                    )
                }

                items(auditLogs, key = { it.id }) { log ->
                    Surface(
                        color = Navy950,
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = log.action,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = CyanAccent
                                )
                                Text(
                                    text = "Admin #${log.adminId}",
                                    fontSize = 10.sp,
                                    color = TextMuted
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Target: ${log.target}",
                                fontSize = 11.sp,
                                color = TextPrimary
                            )
                            Text(
                                text = "${log.oldValue} ➔ ${log.newValue}",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun MetricPill(
    title: String,
    value: String,
    subtitle: String,
    color: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Surface(
        color = CardDark,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(title, fontSize = 10.sp, color = TextMuted)
            Text(value, fontSize = 16.sp, fontWeight = FontWeight.Black, color = color)
            Text(subtitle, fontSize = 9.sp, color = TextSecondary)
        }
    }
}
