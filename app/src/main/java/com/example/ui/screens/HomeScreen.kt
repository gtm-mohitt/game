package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.SportsCricket
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.UserEntity
import com.example.data.model.LeaderboardEntry
import com.example.data.model.MatchWithTeams
import com.example.data.model.PredictionWithDetails
import com.example.ui.components.LiveSimulatorControls
import com.example.ui.components.MatchCard
import com.example.ui.theme.CardBorder
import com.example.ui.theme.CardDark
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldAccent
import com.example.ui.theme.GoldYellow
import com.example.ui.theme.Navy800
import com.example.ui.theme.Navy900
import com.example.ui.theme.Navy950
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.White

@Composable
fun HomeScreen(
    user: UserEntity?,
    matches: List<MatchWithTeams>,
    userPredictions: List<PredictionWithDetails>,
    topLeaderboard: List<LeaderboardEntry>,
    isSimulating: Boolean,
    onViewMatch: (Long) -> Unit,
    onViewAllMatches: () -> Unit,
    onViewAllPredictions: () -> Unit,
    onViewLeaderboard: () -> Unit,
    onStartSimulation: (Long) -> Unit,
    onPauseSimulation: () -> Unit,
    onNextDelivery: (Long) -> Unit,
    onFinishMatch: (Long) -> Unit
) {
    val liveMatch = matches.find { it.match.status == "LIVE" } ?: matches.firstOrNull()
    val upcomingMatches = matches.filter { it.match.status == "SCHEDULED" }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Navy950)
            .padding(horizontal = 16.dp)
            .testTag("home_screen_content"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Banner
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("hero_banner_card"),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Navy900),
                border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldAccent.copy(alpha = 0.4f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(Navy900, Color(0xFF0D253A))
                            )
                        )
                        .padding(18.dp)
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Surface(
                                color = EmeraldAccent.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = "FREE-TO-PLAY VIRTUAL CRICKET",
                                    color = EmeraldAccent,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                            Text(
                                text = "Lvl ${user?.level ?: 1}",
                                color = GoldYellow,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Predict. Play. Climb.",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            color = TextPrimary,
                            letterSpacing = (-0.5).sp
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Forecast player fantasy points, score up to 5x multiplier rewards, and top the leaderboard!",
                            fontSize = 13.sp,
                            color = TextSecondary,
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Fast stats pill
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                color = Navy950.copy(alpha = 0.8f),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Bolt, contentDescription = null, tint = EmeraldAccent, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Column {
                                        Text("Streak", fontSize = 10.sp, color = TextMuted)
                                        Text("${user?.currentStreak ?: 0} Wins", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                    }
                                }
                            }

                            Surface(
                                color = Navy950.copy(alpha = 0.8f),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.TrendingUp, contentDescription = null, tint = CyanAccent, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Column {
                                        Text("XP Points", fontSize = 10.sp, color = TextMuted)
                                        Text("${user?.xp ?: 0} XP", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Live Match Spotlight
        if (liveMatch != null) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "FEATURED MATCH",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 13.sp,
                        color = TextSecondary,
                        letterSpacing = 0.8.sp
                    )
                    TextButton(onClick = onViewAllMatches) {
                        Text("View All", color = CyanAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                MatchCard(
                    matchWithTeams = liveMatch,
                    onViewMatch = { onViewMatch(liveMatch.match.id) },
                    onPredictNow = { onViewMatch(liveMatch.match.id) }
                )
            }

            // Interactive Simulator Controls
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

        // Recent Predictions Snippet
        if (userPredictions.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "MY RECENT PREDICTIONS",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 13.sp,
                        color = TextSecondary,
                        letterSpacing = 0.8.sp
                    )
                    TextButton(onClick = onViewAllPredictions) {
                        Text("See All", color = CyanAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            items(userPredictions.take(2)) { predItem ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onViewMatch(predItem.match.id) },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = CardDark),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(Color(android.graphics.Color.parseColor(predItem.player.avatarColorHex))),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = predItem.player.avatarInitial,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = predItem.player.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = TextPrimary
                            )
                            Text(
                                text = "${predItem.team.code} • ${predItem.match.tournament}",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "Pred: ${predItem.prediction.predictedPoints} pts",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = EmeraldAccent
                            )
                            Text(
                                text = "${predItem.prediction.coinsUsed} coins",
                                fontSize = 11.sp,
                                color = GoldYellow
                            )
                        }
                    }
                }
            }
        }

        // Leaderboard Top Preview
        if (topLeaderboard.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "LEADERBOARD TOP PREDICTORS",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 13.sp,
                        color = TextSecondary,
                        letterSpacing = 0.8.sp
                    )
                    TextButton(onClick = onViewLeaderboard) {
                        Text("Rankings", color = CyanAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            items(topLeaderboard.take(3)) { entry ->
                Surface(
                    color = CardDark,
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "#${entry.rank}",
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp,
                            color = when (entry.rank) {
                                1 -> GoldYellow
                                2 -> TextSecondary
                                else -> Color(0xFFCD7F32)
                            },
                            modifier = Modifier.width(30.dp)
                        )
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color(android.graphics.Color.parseColor(entry.user.avatarColorHex))),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = entry.user.avatarInitial,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = entry.user.username,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = TextPrimary
                            )
                            Text(
                                text = "Lvl ${entry.user.level} • ${entry.user.xp} XP",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                        Text(
                            text = "${entry.user.coins} Coins",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = GoldYellow
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
