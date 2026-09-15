package com.example.ui.components

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Remove
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.local.entities.UserEntity
import com.example.data.model.PlayerWithMatchStats
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
fun PredictionDialog(
    playerWithStats: PlayerWithMatchStats,
    user: UserEntity?,
    onDismiss: () -> Unit,
    onConfirmPrediction: (predictedPoints: Int, coinsToUse: Int) -> Unit
) {
    val player = playerWithStats.player
    val team = playerWithStats.team
    val currentPoints = playerWithStats.currentPoints

    var predictedPoints by remember { mutableStateOf((currentPoints + 15).coerceAtLeast(20)) }
    var coinsToUse by remember { mutableStateOf(100) }
    var isConfirmStep by remember { mutableStateOf(false) }

    val userCoins = user?.coins ?: 1000
    val canAfford = coinsToUse in 1..userCoins

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(vertical = 24.dp)
                .testTag("prediction_dialog_card"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Navy900),
            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header with Close
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isConfirmStep) "Confirm Prediction" else "Predict Points",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = TextPrimary
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(28.dp).testTag("close_prediction_dialog")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = TextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Player Info Banner
                Surface(
                    color = CardDark,
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
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
                                .size(46.dp)
                                .clip(CircleShape)
                                .background(Color(android.graphics.Color.parseColor(player.avatarColorHex))),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = player.avatarInitial,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = player.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = TextPrimary
                            )
                            Text(
                                text = "${team.code} • ${player.role.replace('_', ' ')} • #${player.jerseyNumber}",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "Live Points",
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                            Text(
                                text = "$currentPoints",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 18.sp,
                                color = EmeraldAccent
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                if (!isConfirmStep) {
                    // PREDICTION CONTROLS
                    Text(
                        text = "Predicted Final Points",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // -5 Button
                        IconButton(
                            onClick = { predictedPoints = (predictedPoints - 5).coerceAtLeast(0) },
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Navy800)
                                .testTag("prediction_minus_5_btn")
                        ) {
                            Text("-5", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }

                        // -1 Button
                        IconButton(
                            onClick = { predictedPoints = (predictedPoints - 1).coerceAtLeast(0) },
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Navy800)
                                .testTag("prediction_minus_1_btn")
                        ) {
                            Icon(Icons.Default.Remove, contentDescription = "Decrease", tint = TextPrimary)
                        }

                        // Display Center
                        Surface(
                            color = Navy950,
                            shape = RoundedCornerShape(14.dp),
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, EmeraldAccent),
                            modifier = Modifier.padding(horizontal = 8.dp)
                        ) {
                            Text(
                                text = "$predictedPoints",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Black,
                                color = EmeraldAccent,
                                modifier = Modifier
                                    .padding(horizontal = 24.dp, vertical = 6.dp)
                                    .testTag("predicted_points_value")
                            )
                        }

                        // +1 Button
                        IconButton(
                            onClick = { predictedPoints += 1 },
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Navy800)
                                .testTag("prediction_plus_1_btn")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Increase", tint = TextPrimary)
                        }

                        // +5 Button
                        IconButton(
                            onClick = { predictedPoints += 5 },
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Navy800)
                                .testTag("prediction_plus_5_btn")
                        ) {
                            Text("+5", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // VIRTUAL COINS SECTION
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Virtual Coins To Use",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextSecondary
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.MonetizationOn,
                                contentDescription = null,
                                tint = GoldYellow,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Available: $userCoins",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldYellow
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Coin Input Field
                    OutlinedTextField(
                        value = coinsToUse.toString(),
                        onValueChange = { str ->
                            coinsToUse = str.filter { it.isDigit() }.toIntOrNull() ?: 0
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EmeraldAccent,
                            unfocusedBorderColor = CardBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("coins_input_field"),
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.MonetizationOn,
                                contentDescription = null,
                                tint = GoldYellow
                            )
                        }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Quick Coin Selectors: 10, 50, 100, 250, 500
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(10, 50, 100, 250, 500).forEach { amount ->
                            Surface(
                                color = if (coinsToUse == amount) EmeraldAccent else Navy800,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { coinsToUse = amount }
                                    .testTag("quick_coin_$amount")
                            ) {
                                Text(
                                    text = "$amount",
                                    color = if (coinsToUse == amount) Navy950 else TextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Multiplier & Reward Tiers Info Box
                    Surface(
                        color = Navy950,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "🎯 Accuracy Reward Multipliers:",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = CyanAccent
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "99%+ acc: 5x | 95%+: 4x | 90%+: 3x | 80%+: 2x | 70%+: 1x",
                                fontSize = 10.sp,
                                color = TextMuted
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Legal / Money rule disclosure
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Navy800, RoundedCornerShape(8.dp))
                            .padding(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = GoldYellow,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Virtual coins have no cash value. Free-to-play game.",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = { isConfirmStep = true },
                        enabled = canAfford,
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldAccent),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("proceed_prediction_btn")
                    ) {
                        Text(
                            text = if (canAfford) "REVIEW PREDICTION" else "INSUFFICIENT COINS",
                            fontWeight = FontWeight.Bold,
                            color = Navy950,
                            fontSize = 15.sp
                        )
                    }
                } else {
                    // CONFIRMATION STEP
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Navy950, RoundedCornerShape(16.dp))
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "You are predicting that",
                            fontSize = 13.sp,
                            color = TextSecondary
                        )
                        Text(
                            text = player.name,
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "will finish with",
                            fontSize = 13.sp,
                            color = TextSecondary
                        )
                        Text(
                            text = "$predictedPoints PERFORMANCE POINTS",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 16.sp,
                            color = EmeraldAccent
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.MonetizationOn,
                                contentDescription = null,
                                tint = GoldYellow,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Using $coinsToUse virtual coins",
                                fontWeight = FontWeight.Bold,
                                color = GoldYellow,
                                fontSize = 14.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "\"Virtual coins have no cash value.\"",
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { isConfirmStep = false },
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("cancel_confirm_btn")
                        ) {
                            Text("Back", color = TextPrimary)
                        }

                        Button(
                            onClick = {
                                onConfirmPrediction(predictedPoints, coinsToUse)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldAccent),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .weight(2f)
                                .height(48.dp)
                                .testTag("confirm_prediction_btn")
                        ) {
                            Text(
                                text = "CONFIRM PREDICTION",
                                fontWeight = FontWeight.Bold,
                                color = Navy950,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
