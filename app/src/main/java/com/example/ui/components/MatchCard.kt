package com.example.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.SportsCricket
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MatchWithTeams
import com.example.ui.theme.CardBorder
import com.example.ui.theme.CardDark
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldAccent
import com.example.ui.theme.GoldYellow
import com.example.ui.theme.LiveRed
import com.example.ui.theme.Navy800
import com.example.ui.theme.Navy950
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.White

@Composable
fun MatchCard(
    matchWithTeams: MatchWithTeams,
    onViewMatch: () -> Unit,
    onPredictNow: () -> Unit,
    modifier: Modifier = Modifier
) {
    val match = matchWithTeams.match
    val teamA = matchWithTeams.teamA
    val teamB = matchWithTeams.teamB

    val isLive = match.status == "LIVE"
    val isCompleted = match.status == "COMPLETED"
    val isScheduled = match.status == "SCHEDULED"

    // Pulsing animation for LIVE status
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alphaAnim by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("match_card_${match.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardDark),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Top Row: Tournament, Venue, and Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = match.tournament,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyanAccent
                    )
                    Text(
                        text = match.venue,
                        fontSize = 11.sp,
                        color = TextMuted,
                        maxLines = 1
                    )
                }

                // Status Badge
                Surface(
                    color = when {
                        isLive -> LiveRed.copy(alpha = 0.2f)
                        isCompleted -> Navy800
                        else -> EmeraldAccent.copy(alpha = 0.15f)
                    },
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        when {
                            isLive -> LiveRed
                            isCompleted -> TextMuted
                            else -> EmeraldAccent
                        }
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (isLive) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .alpha(alphaAnim)
                                    .clip(CircleShape)
                                    .background(LiveRed)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = "LIVE",
                                fontWeight = FontWeight.Black,
                                fontSize = 11.sp,
                                color = LiveRed
                            )
                        } else if (isCompleted) {
                            Text(
                                text = "COMPLETED",
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                color = TextSecondary
                            )
                        } else {
                            Text(
                                text = "UPCOMING",
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                color = EmeraldAccent
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Scoreboard Row (Team A vs Team B)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Team A
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = teamA.flagEmoji,
                        fontSize = 26.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = teamA.code,
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp,
                            color = TextPrimary
                        )
                        if (isLive || isCompleted) {
                            Text(
                                text = "${match.scoreTeamA}/${match.wicketsTeamA}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = TextPrimary
                            )
                            Text(
                                text = "(${match.oversTeamA} ov)",
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                        }
                    }
                }

                Text(
                    text = "VS",
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp,
                    color = TextMuted
                )

                // Team B
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = teamB.code,
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp,
                            color = TextPrimary
                        )
                        if (isLive || isCompleted) {
                            Text(
                                text = "${match.scoreTeamB}/${match.wicketsTeamB}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = TextPrimary
                            )
                            Text(
                                text = "(${match.oversTeamB} ov)",
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = teamB.flagEmoji,
                        fontSize = 26.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Match Summary or Countdown
            Surface(
                color = Navy950,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = match.summaryText,
                        fontSize = 12.sp,
                        color = if (isLive) EmeraldAccent else TextSecondary,
                        fontWeight = FontWeight.Medium
                    )
                    if (match.predictionsLocked) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Lock,
                                contentDescription = "Locked",
                                tint = LiveRed,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Locked",
                                fontSize = 11.sp,
                                color = LiveRed,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons: VIEW MATCH and PREDICT NOW
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onViewMatch,
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag("view_match_btn_${match.id}"),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
                ) {
                    Text("VIEW MATCH", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onPredictNow,
                    enabled = !match.predictionsLocked && !isCompleted,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = EmeraldAccent,
                        disabledContainerColor = Navy800
                    ),
                    modifier = Modifier
                        .weight(1.3f)
                        .height(44.dp)
                        .testTag("predict_now_btn_${match.id}"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.SportsCricket,
                        contentDescription = null,
                        tint = if (!match.predictionsLocked && !isCompleted) Navy950 else TextMuted,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (match.predictionsLocked || isCompleted) "CLOSED" else "PREDICT NOW",
                        color = if (!match.predictionsLocked && !isCompleted) Navy950 else TextMuted,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}
