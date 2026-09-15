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
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Stars
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LeaderboardEntry
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

@Composable
fun LeaderboardScreen(
    leaderboard: List<LeaderboardEntry>
) {
    var selectedScope by remember { mutableStateOf(0) } // 0: Global, 1: Weekly, 2: Daily
    val scopeLabels = listOf("Global", "Weekly", "Daily")

    val top1 = leaderboard.getOrNull(0)
    val top2 = leaderboard.getOrNull(1)
    val top3 = leaderboard.getOrNull(2)
    val restOfList = if (leaderboard.size > 3) leaderboard.drop(3) else emptyList()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Navy950)
            .padding(horizontal = 16.dp)
            .testTag("leaderboard_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Top Scope Tabs
        item {
            Spacer(modifier = Modifier.height(4.dp))
            TabRow(
                selectedTabIndex = selectedScope,
                containerColor = Navy900,
                contentColor = TextPrimary,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedScope]),
                        color = EmeraldAccent
                    )
                },
                modifier = Modifier.testTag("leaderboard_tabs")
            ) {
                scopeLabels.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedScope == index,
                        onClick = { selectedScope = index },
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

        // Top 3 Podium
        if (leaderboard.isNotEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("leaderboard_podium_card"),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = CardDark),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "TOP CRICKET PREDICTORS",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 13.sp,
                            color = CyanAccent,
                            letterSpacing = 0.8.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Podium Row (2nd, 1st, 3rd)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            // 2nd Place (Silver)
                            if (top2 != null) {
                                PodiumColumn(
                                    entry = top2,
                                    rank = 2,
                                    badgeColor = Color(0xFFC0C0C0),
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            // 1st Place (Gold)
                            if (top1 != null) {
                                PodiumColumn(
                                    entry = top1,
                                    rank = 1,
                                    badgeColor = GoldYellow,
                                    isFirst = true,
                                    modifier = Modifier.weight(1.1f)
                                )
                            }

                            // 3rd Place (Bronze)
                            if (top3 != null) {
                                PodiumColumn(
                                    entry = top3,
                                    rank = 3,
                                    badgeColor = Color(0xFFCD7F32),
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Leaderboard List (Rank 4+)
        if (restOfList.isNotEmpty()) {
            item {
                Text(
                    text = "ALL RANKINGS",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 12.sp,
                    color = TextSecondary,
                    letterSpacing = 0.8.sp
                )
            }

            items(restOfList, key = { it.user.id }) { entry ->
                Surface(
                    color = CardDark,
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "#${entry.rank}",
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp,
                            color = TextSecondary,
                            modifier = Modifier.width(32.dp)
                        )

                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(android.graphics.Color.parseColor(entry.user.avatarColorHex))),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = entry.user.avatarInitial,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = entry.user.username,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = TextPrimary
                            )
                            Text(
                                text = "Lvl ${entry.user.level} • ${entry.user.xp} XP",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "${entry.user.coins} Coins",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = GoldYellow
                            )
                            Text(
                                text = "${entry.wonPredictions} Won",
                                fontSize = 11.sp,
                                color = EmeraldAccent
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
private fun PodiumColumn(
    entry: LeaderboardEntry,
    rank: Int,
    badgeColor: Color,
    isFirst: Boolean = false,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isFirst) {
            Icon(
                Icons.Default.EmojiEvents,
                contentDescription = null,
                tint = GoldYellow,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
        }

        Box(
            modifier = Modifier
                .size(if (isFirst) 56.dp else 46.dp)
                .clip(CircleShape)
                .border(2.dp, badgeColor, CircleShape)
                .background(Color(android.graphics.Color.parseColor(entry.user.avatarColorHex))),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = entry.user.avatarInitial,
                color = Color.White,
                fontWeight = FontWeight.Black,
                fontSize = if (isFirst) 18.sp else 15.sp
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = entry.user.username,
            fontWeight = FontWeight.Bold,
            fontSize = if (isFirst) 13.sp else 12.sp,
            color = TextPrimary,
            maxLines = 1
        )

        Text(
            text = "${entry.user.coins} Coins",
            fontSize = 11.sp,
            color = GoldYellow,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Pedestal block
        Surface(
            color = if (isFirst) EmeraldAccent.copy(alpha = 0.2f) else Navy800,
            shape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, badgeColor.copy(alpha = 0.5f)),
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .height(if (isFirst) 64.dp else if (rank == 2) 48.dp else 36.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = "#$rank",
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp,
                    color = badgeColor
                )
            }
        }
    }
}
