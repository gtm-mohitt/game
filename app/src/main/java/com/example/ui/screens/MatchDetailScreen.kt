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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.SportsCricket
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MatchWithTeams
import com.example.data.model.PlayerWithMatchStats
import com.example.ui.components.LiveSimulatorControls
import com.example.ui.components.PlayerCard
import com.example.ui.theme.CardBorder
import com.example.ui.theme.CardDark
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldAccent
import com.example.ui.theme.LiveRed
import com.example.ui.theme.Navy800
import com.example.ui.theme.Navy900
import com.example.ui.theme.Navy950
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun MatchDetailScreen(
    matchWithTeams: MatchWithTeams?,
    players: List<PlayerWithMatchStats>,
    isSimulating: Boolean,
    onBack: () -> Unit,
    onPredictPlayer: (PlayerWithMatchStats) -> Unit,
    onPlayerClick: (PlayerWithMatchStats) -> Unit,
    onStartSimulation: () -> Unit,
    onPauseSimulation: () -> Unit,
    onNextDelivery: () -> Unit,
    onFinishMatch: () -> Unit
) {
    if (matchWithTeams == null) return

    val match = matchWithTeams.match
    val teamA = matchWithTeams.teamA
    val teamB = matchWithTeams.teamB
    val isLive = match.status == "LIVE"
    val isCompleted = match.status == "COMPLETED"

    var selectedTeamFilter by remember { mutableStateOf("ALL") } // "ALL", "TEAM_A", "TEAM_B"
    var selectedRoleFilter by remember { mutableStateOf("ALL") } // "ALL", "BATTER", "BOWLER", "ALL_ROUNDER", "WICKETKEEPER"

    val filteredPlayers = players.filter { p ->
        val teamMatches = when (selectedTeamFilter) {
            "TEAM_A" -> p.player.teamId == teamA.id
            "TEAM_B" -> p.player.teamId == teamB.id
            else -> true
        }
        val roleMatches = when (selectedRoleFilter) {
            "ALL" -> true
            else -> p.player.role == selectedRoleFilter
        }
        teamMatches && roleMatches
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Navy950)
            .padding(horizontal = 16.dp)
            .testTag("match_detail_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Top App Bar
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.testTag("back_to_matches_btn")
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
                        text = "${teamA.code} vs ${teamB.code}",
                        fontWeight = FontWeight.Black,
                        fontSize = 17.sp,
                        color = TextPrimary
                    )
                    Text(
                        text = match.tournament,
                        fontSize = 11.sp,
                        color = CyanAccent
                    )
                }
            }
        }

        // Match Live Scoreboard Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CardDark),
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = match.venue,
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                        Surface(
                            color = if (isLive) LiveRed.copy(alpha = 0.2f) else Navy800,
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isLive) LiveRed else CardBorder)
                        ) {
                            Text(
                                text = if (isLive) "LIVE IN PROGRESS" else match.status,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                color = if (isLive) LiveRed else TextSecondary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(teamA.flagEmoji, fontSize = 24.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(teamA.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary)
                            }
                            if (isLive || isCompleted) {
                                Text(
                                    text = "${match.scoreTeamA}/${match.wicketsTeamA} (${match.oversTeamA} ov)",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldAccent
                                )
                            }
                        }

                        Text("VS", fontWeight = FontWeight.Black, color = TextMuted, fontSize = 14.sp)

                        Column(horizontalAlignment = Alignment.End) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(teamB.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(teamB.flagEmoji, fontSize = 24.sp)
                            }
                            if (isLive || isCompleted) {
                                Text(
                                    text = "${match.scoreTeamB}/${match.wicketsTeamB} (${match.oversTeamB} ov)",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldAccent
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Surface(
                        color = Navy950,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = match.summaryText,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (isLive) EmeraldAccent else TextSecondary,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }
            }
        }

        // Simulator Controls if Live
        if (isLive) {
            item {
                LiveSimulatorControls(
                    matchId = match.id,
                    isSimulating = isSimulating,
                    onStartSimulation = onStartSimulation,
                    onPauseSimulation = onPauseSimulation,
                    onNextDelivery = onNextDelivery,
                    onFinishMatch = onFinishMatch
                )
            }
        }

        // Playing XI Section Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "PLAYING XI & PERFORMANCE",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 13.sp,
                    color = TextSecondary,
                    letterSpacing = 0.8.sp
                )
                if (match.predictionsLocked || isCompleted) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = LiveRed, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Predictions Closed", color = LiveRed, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Team filter row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    "ALL" to "All Teams",
                    "TEAM_A" to "${teamA.flagEmoji} ${teamA.code}",
                    "TEAM_B" to "${teamB.flagEmoji} ${teamB.code}"
                ).forEach { (key, label) ->
                    FilterChip(
                        selected = selectedTeamFilter == key,
                        onClick = { selectedTeamFilter = key },
                        label = { Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = EmeraldAccent,
                            selectedLabelColor = Navy950,
                            containerColor = Navy900,
                            labelColor = TextPrimary
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Role filter row
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(
                    "ALL" to "All Roles",
                    "BATTER" to "Batters",
                    "BOWLER" to "Bowlers",
                    "ALL_ROUNDER" to "All-Rounders",
                    "WICKETKEEPER" to "Wicketkeepers"
                ).forEach { (key, label) ->
                    item {
                        FilterChip(
                            selected = selectedRoleFilter == key,
                            onClick = { selectedRoleFilter = key },
                            label = { Text(label, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = CyanAccent,
                                selectedLabelColor = Navy950,
                                containerColor = Navy900,
                                labelColor = TextSecondary
                            )
                        )
                    }
                }
            }
        }

        // Player Cards List
        items(filteredPlayers, key = { it.player.id }) { p ->
            PlayerCard(
                playerWithStats = p,
                isLocked = match.predictionsLocked || isCompleted,
                onPredictClick = { onPredictPlayer(p) },
                onPlayerClick = { onPlayerClick(p) }
            )
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
