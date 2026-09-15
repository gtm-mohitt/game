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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.SportsCricket
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PredictionWithDetails
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

@Composable
fun PredictionsScreen(
    predictions: List<PredictionWithDetails>,
    selectedFilter: String,
    onFilterChange: (String) -> Unit,
    onMatchClick: (Long) -> Unit
) {
    val filterTabs = listOf("ALL", "PENDING", "LIVE", "WON", "LOST")
    val selectedIndex = filterTabs.indexOf(selectedFilter).coerceAtLeast(0)

    val filteredPredictions = predictions.filter {
        if (selectedFilter == "ALL") true else it.prediction.status == selectedFilter
    }

    val totalPreds = predictions.size
    val wonPreds = predictions.count { it.prediction.status == "WON" }
    val completedPreds = predictions.filter { it.prediction.status == "WON" || it.prediction.status == "LOST" }
    val avgAccuracy = if (completedPreds.isNotEmpty()) {
        (completedPreds.sumOf { it.prediction.accuracy } / completedPreds.size * 10).toInt() / 10.0
    } else 0.0
    val totalCoinsWon = predictions.sumOf { it.prediction.rewardCoins }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Navy950)
            .padding(horizontal = 16.dp)
            .testTag("predictions_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Stats Summary Grid
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    color = CardDark,
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Total", fontSize = 11.sp, color = TextMuted)
                        Text("$totalPreds", fontSize = 20.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                        Text("$wonPreds Won", fontSize = 10.sp, color = EmeraldAccent, fontWeight = FontWeight.Bold)
                    }
                }

                Surface(
                    color = CardDark,
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Avg Accuracy", fontSize = 11.sp, color = TextMuted)
                        Text("$avgAccuracy%", fontSize = 20.sp, fontWeight = FontWeight.Black, color = CyanAccent)
                        Text("Across settled", fontSize = 10.sp, color = TextSecondary)
                    }
                }

                Surface(
                    color = CardDark,
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Coins Won", fontSize = 11.sp, color = TextMuted)
                        Text("+$totalCoinsWon", fontSize = 20.sp, fontWeight = FontWeight.Black, color = GoldYellow)
                        Text("Virtual tokens", fontSize = 10.sp, color = TextSecondary)
                    }
                }
            }
        }

        // Filter Tabs
        item {
            TabRow(
                selectedTabIndex = selectedIndex,
                containerColor = Navy900,
                contentColor = TextPrimary,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedIndex]),
                        color = EmeraldAccent
                    )
                },
                modifier = Modifier.testTag("prediction_tabs")
            ) {
                filterTabs.forEachIndexed { index, filter ->
                    Tab(
                        selected = selectedIndex == index,
                        onClick = { onFilterChange(filter) },
                        text = {
                            Text(
                                text = filter,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    )
                }
            }
        }

        if (filteredPredictions.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.SportsCricket,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "No predictions found in this category",
                            color = TextMuted,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        } else {
            items(filteredPredictions, key = { it.prediction.id }) { item ->
                val pred = item.prediction
                val isWon = pred.status == "WON"
                val isLost = pred.status == "LOST"
                val isLive = pred.status == "LIVE"

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("prediction_card_${pred.id}"),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = CardDark),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        when {
                            isWon -> EmeraldAccent.copy(alpha = 0.6f)
                            isLost -> LiveRed.copy(alpha = 0.4f)
                            isLive -> CyanAccent.copy(alpha = 0.5f)
                            else -> CardBorder
                        }
                    )
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        // Top row: Player, Match, Status Badge
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(Color(android.graphics.Color.parseColor(item.player.avatarColorHex))),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = item.player.avatarInitial,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = item.player.name,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = "${item.team.code} • ${item.match.tournament}",
                                        fontSize = 11.sp,
                                        color = TextSecondary
                                    )
                                }
                            }

                            // Status Badge
                            Surface(
                                color = when {
                                    isWon -> EmeraldAccent.copy(alpha = 0.2f)
                                    isLost -> LiveRed.copy(alpha = 0.2f)
                                    isLive -> CyanAccent.copy(alpha = 0.2f)
                                    else -> Navy800
                                },
                                shape = RoundedCornerShape(8.dp),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    when {
                                        isWon -> EmeraldAccent
                                        isLost -> LiveRed
                                        isLive -> CyanAccent
                                        else -> CardBorder
                                    }
                                )
                            ) {
                                Text(
                                    text = pred.status,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 10.sp,
                                    color = when {
                                        isWon -> EmeraldAccent
                                        isLost -> LiveRed
                                        isLive -> CyanAccent
                                        else -> TextSecondary
                                    },
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Stats Comparison Bar
                        Surface(
                            color = Navy950,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Predicted", fontSize = 10.sp, color = TextMuted)
                                    Text("${pred.predictedPoints} pts", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                }

                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Actual", fontSize = 10.sp, color = TextMuted)
                                    Text("${pred.actualPoints} pts", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = EmeraldAccent)
                                }

                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Diff", fontSize = 10.sp, color = TextMuted)
                                    Text("${pred.difference}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text("Accuracy", fontSize = 10.sp, color = TextMuted)
                                    Text("${pred.accuracy}%", fontSize = 14.sp, fontWeight = FontWeight.Black, color = if (pred.accuracy >= 70.0) EmeraldAccent else LiveRed)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Reward & Coins Footer
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = GoldYellow, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Used: ${pred.coinsUsed} coins", fontSize = 12.sp, color = TextSecondary)
                            }

                            if (isWon) {
                                Text(
                                    text = "Reward: +${pred.rewardCoins} coins (${pred.multiplier}x) • +${pred.xpEarned} XP",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldAccent
                                )
                            } else if (isLost) {
                                Text(
                                    text = "0x multiplier • +${pred.xpEarned} XP",
                                    fontSize = 11.sp,
                                    color = TextMuted
                                )
                            } else {
                                Text(
                                    text = "Potential: up to 5x",
                                    fontSize = 11.sp,
                                    color = CyanAccent
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
}
