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
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SwitchAccount
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.AchievementEntity
import com.example.data.local.entities.CoinTransactionEntity
import com.example.data.local.entities.MissionEntity
import com.example.data.local.entities.UserEntity
import com.example.data.local.entities.UserMissionEntity
import com.example.engine.GameEngine
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
fun ProfileScreen(
    user: UserEntity?,
    transactionsFlow: Flow<List<CoinTransactionEntity>>,
    achievementsFlow: Flow<List<Pair<AchievementEntity, Boolean>>>,
    missionsFlow: Flow<List<Pair<MissionEntity, UserMissionEntity?>>>,
    onOpenAuthDialog: () -> Unit,
    onOpenAdminPortal: () -> Unit
) {
    if (user == null) return

    val transactions by transactionsFlow.collectAsState(initial = emptyList())
    val achievements by achievementsFlow.collectAsState(initial = emptyList())
    val missions by missionsFlow.collectAsState(initial = emptyList())

    var selectedTab by remember { mutableStateOf(0) } // 0: Overview, 1: Wallet History, 2: Achievements, 3: Missions
    val profileTabs = listOf("Overview", "Wallet", "Badges", "Missions")

    val nextLevelXp = GameEngine.getXpForNextLevel(user.level)
    val progress = (user.xp.toFloat() / nextLevelXp.toFloat()).coerceIn(0f, 1f)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Navy950)
            .padding(horizontal = 16.dp)
            .testTag("profile_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // User Profile Header Card
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = CardDark),
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(58.dp)
                                .clip(CircleShape)
                                .background(Color(android.graphics.Color.parseColor(user.avatarColorHex))),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = user.avatarInitial,
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 22.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = user.username,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 18.sp,
                                    color = TextPrimary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Surface(
                                    color = if (user.role == "ADMIN") CyanAccent.copy(alpha = 0.2f) else EmeraldAccent.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = user.role,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (user.role == "ADMIN") CyanAccent else EmeraldAccent,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = user.email,
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Level and XP Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Level ${user.level}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldYellow
                        )
                        Text(
                            text = "${user.xp} / $nextLevelXp XP",
                            fontSize = 12.sp,
                            color = TextMuted
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = EmeraldAccent,
                        trackColor = Navy900
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Key Stats Pill Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            color = Navy950,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("Virtual Coins", fontSize = 10.sp, color = TextMuted)
                                Text("${user.coins}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = GoldYellow)
                            }
                        }

                        Surface(
                            color = Navy950,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("Win Streak", fontSize = 10.sp, color = TextMuted)
                                Text("${user.currentStreak} 🔥", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = EmeraldAccent)
                            }
                        }

                        Surface(
                            color = Navy950,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("Best Streak", fontSize = 10.sp, color = TextMuted)
                                Text("${user.longestStreak}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = CyanAccent)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = onOpenAuthDialog,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(42.dp)
                                .testTag("switch_account_btn")
                        ) {
                            Icon(Icons.Default.SwitchAccount, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Switch Account", fontSize = 12.sp)
                        }

                        if (user.role == "ADMIN") {
                            Button(
                                onClick = onOpenAdminPortal,
                                colors = ButtonDefaults.buttonColors(containerColor = CyanAccent),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(42.dp)
                                    .testTag("admin_portal_btn")
                            ) {
                                Text("Admin Panel", color = Navy950, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }

        // Sub Tabs
        item {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Navy900,
                contentColor = TextPrimary,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = EmeraldAccent
                    )
                }
            ) {
                profileTabs.forEachIndexed { index, title ->
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
                // Overview Tab
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = CardDark),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "HOW TO PLAY & REWARDS",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 13.sp,
                                color = CyanAccent
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "1. Pick any player from Playing XI before or during live matches.\n" +
                                        "2. Enter your predicted performance points.\n" +
                                        "3. Spend free virtual coins to lock your prediction.\n" +
                                        "4. When the match completes, points are scored using official cricket stats.\n" +
                                        "5. Reach 70%+ accuracy to win up to 5x multiplier rewards and climb the global ladder!",
                                fontSize = 12.sp,
                                color = TextSecondary,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }

            1 -> {
                // Wallet History Tab
                if (transactions.isEmpty()) {
                    item {
                        Text("No wallet transactions yet.", color = TextMuted, fontSize = 13.sp)
                    }
                } else {
                    items(transactions, key = { it.id }) { tx ->
                        Surface(
                            color = CardDark,
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = tx.description,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 13.sp,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = tx.type.replace('_', ' '),
                                        fontSize = 10.sp,
                                        color = TextMuted
                                    )
                                }
                                Text(
                                    text = if (tx.amount > 0) "+${tx.amount}" else "${tx.amount}",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 14.sp,
                                    color = if (tx.amount > 0) EmeraldAccent else LiveRed
                                )
                            }
                        }
                    }
                }
            }

            2 -> {
                // Achievements Tab
                items(achievements, key = { it.first.id }) { (ach, isUnlocked) ->
                    Surface(
                        color = CardDark,
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isUnlocked) EmeraldAccent.copy(alpha = 0.6f) else CardBorder
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(if (isUnlocked) EmeraldAccent.copy(alpha = 0.2f) else Navy950),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isUnlocked) Icons.Default.WorkspacePremium else Icons.Default.Star,
                                    contentDescription = null,
                                    tint = if (isUnlocked) EmeraldAccent else TextMuted,
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = ach.title,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = if (isUnlocked) TextPrimary else TextMuted
                                )
                                Text(
                                    text = ach.description,
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "+${ach.coinReward} Coins",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = GoldYellow
                                )
                                if (isUnlocked) {
                                    Text(
                                        text = "UNLOCKED",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 10.sp,
                                        color = EmeraldAccent
                                    )
                                }
                            }
                        }
                    }
                }
            }

            3 -> {
                // Daily Missions Tab
                items(missions, key = { it.first.id }) { (mission, userMission) ->
                    val curr = userMission?.currentProgress ?: 0
                    val target = mission.targetCount
                    val progressRatio = (curr.toFloat() / target.toFloat()).coerceIn(0f, 1f)
                    val isDone = curr >= target

                    Surface(
                        color = CardDark,
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = mission.title,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "+${mission.coinReward} Coins",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = GoldYellow
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = mission.description,
                                fontSize = 11.sp,
                                color = TextSecondary
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                LinearProgressIndicator(
                                    progress = { progressRatio },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(3.dp)),
                                    color = if (isDone) EmeraldAccent else CyanAccent,
                                    trackColor = Navy950
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "$curr / $target",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDone) EmeraldAccent else TextMuted
                                )
                            }
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
