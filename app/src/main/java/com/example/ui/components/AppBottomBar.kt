package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Sports
import androidx.compose.material.icons.filled.SportsCricket
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.ui.MainTab
import com.example.ui.theme.EmeraldAccent
import com.example.ui.theme.Navy800
import com.example.ui.theme.Navy950
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary

@Composable
fun AppBottomBar(
    currentTab: MainTab,
    onTabSelected: (MainTab) -> Unit
) {
    NavigationBar(
        containerColor = Navy950,
        contentColor = TextPrimary,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("app_bottom_bar")
    ) {
        NavigationBarItem(
            selected = currentTab == MainTab.HOME,
            onClick = { onTabSelected(MainTab.HOME) },
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Navy950,
                selectedTextColor = EmeraldAccent,
                indicatorColor = EmeraldAccent,
                unselectedIconColor = TextMuted,
                unselectedTextColor = TextMuted
            ),
            modifier = Modifier.testTag("tab_home")
        )

        NavigationBarItem(
            selected = currentTab == MainTab.MATCHES,
            onClick = { onTabSelected(MainTab.MATCHES) },
            icon = { Icon(Icons.Default.SportsCricket, contentDescription = "Matches") },
            label = { Text("Matches", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Navy950,
                selectedTextColor = EmeraldAccent,
                indicatorColor = EmeraldAccent,
                unselectedIconColor = TextMuted,
                unselectedTextColor = TextMuted
            ),
            modifier = Modifier.testTag("tab_matches")
        )

        NavigationBarItem(
            selected = currentTab == MainTab.PREDICTIONS,
            onClick = { onTabSelected(MainTab.PREDICTIONS) },
            icon = { Icon(Icons.Default.TrackChanges, contentDescription = "Predictions") },
            label = { Text("Predictions", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Navy950,
                selectedTextColor = EmeraldAccent,
                indicatorColor = EmeraldAccent,
                unselectedIconColor = TextMuted,
                unselectedTextColor = TextMuted
            ),
            modifier = Modifier.testTag("tab_predictions")
        )

        NavigationBarItem(
            selected = currentTab == MainTab.LEADERBOARD,
            onClick = { onTabSelected(MainTab.LEADERBOARD) },
            icon = { Icon(Icons.Default.EmojiEvents, contentDescription = "Leaderboard") },
            label = { Text("Rankings", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Navy950,
                selectedTextColor = EmeraldAccent,
                indicatorColor = EmeraldAccent,
                unselectedIconColor = TextMuted,
                unselectedTextColor = TextMuted
            ),
            modifier = Modifier.testTag("tab_leaderboard")
        )

        NavigationBarItem(
            selected = currentTab == MainTab.PROFILE,
            onClick = { onTabSelected(MainTab.PROFILE) },
            icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
            label = { Text("Profile", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Navy950,
                selectedTextColor = EmeraldAccent,
                indicatorColor = EmeraldAccent,
                unselectedIconColor = TextMuted,
                unselectedTextColor = TextMuted
            ),
            modifier = Modifier.testTag("tab_profile")
        )
    }
}
