package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.SportsCricket
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
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
import com.example.data.model.PlayerWithMatchStats
import com.example.ui.theme.CardBorder
import com.example.ui.theme.CardDark
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldAccent
import com.example.ui.theme.GoldYellow
import com.example.ui.theme.Navy800
import com.example.ui.theme.Navy950
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun PlayerCard(
    playerWithStats: PlayerWithMatchStats,
    onPredictClick: () -> Unit,
    onPlayerClick: () -> Unit,
    isLocked: Boolean = false,
    modifier: Modifier = Modifier
) {
    val player = playerWithStats.player
    val team = playerWithStats.team
    val stats = playerWithStats.stats
    val points = playerWithStats.currentPoints
    val userPred = playerWithStats.userPrediction

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onPlayerClick() }
            .testTag("player_card_${player.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardDark),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Player Avatar with jersey
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(Color(android.graphics.Color.parseColor(player.avatarColorHex))),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = player.avatarInitial,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Player Info: Name, Team, Role
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = player.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "#${player.jerseyNumber}",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }

                Spacer(modifier = Modifier.height(3.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = Navy800,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = team.code,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            color = CyanAccent,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = player.role.replace('_', ' '),
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }

                // If stats exist: show runs or wickets summary
                if (stats != null && (stats.runs > 0 || stats.wicketsTaken > 0 || stats.balls > 0)) {
                    Spacer(modifier = Modifier.height(4.dp))
                    val summary = buildString {
                        if (stats.runs > 0 || stats.balls > 0) append("${stats.runs}r (${stats.balls}b) ")
                        if (stats.fours > 0) append("${stats.fours}x4 ")
                        if (stats.sixes > 0) append("${stats.sixes}x6 ")
                        if (stats.wicketsTaken > 0) append("• ${stats.wicketsTaken} wkts")
                    }
                    Text(
                        text = summary,
                        fontSize = 10.sp,
                        color = TextMuted
                    )
                }
            }

            // Right side: Points & Predict action
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center
            ) {
                // Live points
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Points:",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "$points",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = EmeraldAccent
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                if (userPred != null) {
                    // Already predicted badge
                    Surface(
                        color = Navy800,
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GoldYellow)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = GoldYellow,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Pred: ${userPred.predictedPoints}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = GoldYellow
                            )
                        }
                    }
                } else {
                    Button(
                        onClick = onPredictClick,
                        enabled = !isLocked,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = EmeraldAccent,
                            disabledContainerColor = Navy800
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .height(34.dp)
                            .testTag("predict_player_${player.id}")
                    ) {
                        Text(
                            text = if (isLocked) "LOCKED" else "PREDICT",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = if (isLocked) TextMuted else Navy950
                        )
                    }
                }
            }
        }
    }
}
