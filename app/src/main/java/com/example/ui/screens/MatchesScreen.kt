package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MatchWithTeams
import com.example.ui.components.MatchCard
import com.example.ui.theme.CardBorder
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldAccent
import com.example.ui.theme.Navy900
import com.example.ui.theme.Navy950
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun MatchesScreen(
    matches: List<MatchWithTeams>,
    selectedFilter: String,
    searchQuery: String,
    onFilterChange: (String) -> Unit,
    onSearchChange: (String) -> Unit,
    onViewMatch: (Long) -> Unit
) {
    val filterTabs = listOf("ALL", "LIVE", "SCHEDULED", "COMPLETED")
    val selectedIndex = filterTabs.indexOf(selectedFilter).coerceAtLeast(0)

    val filteredMatches = matches.filter { matchWithTeams ->
        val statusMatches = when (selectedFilter) {
            "ALL" -> true
            else -> matchWithTeams.match.status == selectedFilter
        }
        val query = searchQuery.trim().lowercase()
        val textMatches = query.isEmpty() ||
                matchWithTeams.teamA.name.lowercase().contains(query) ||
                matchWithTeams.teamA.code.lowercase().contains(query) ||
                matchWithTeams.teamB.name.lowercase().contains(query) ||
                matchWithTeams.teamB.code.lowercase().contains(query) ||
                matchWithTeams.match.tournament.lowercase().contains(query) ||
                matchWithTeams.match.venue.lowercase().contains(query)

        statusMatches && textMatches
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Navy950)
            .padding(horizontal = 16.dp)
            .testTag("matches_screen_root")
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // Search bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            placeholder = { Text("Search teams, venues, or series...", color = TextMuted, fontSize = 13.sp) },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = "Search", tint = TextMuted)
            },
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = EmeraldAccent,
                unfocusedBorderColor = CardBorder,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("matches_search_input")
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Filter Tabs
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
            modifier = Modifier.testTag("matches_filter_tabs")
        ) {
            filterTabs.forEachIndexed { index, filter ->
                Tab(
                    selected = selectedIndex == index,
                    onClick = { onFilterChange(filter) },
                    text = {
                        Text(
                            text = if (filter == "SCHEDULED") "UPCOMING" else filter,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (filteredMatches.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
            ) {
                Text(
                    text = "No matches found matching criteria.",
                    color = TextMuted,
                    fontSize = 14.sp
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(filteredMatches, key = { it.match.id }) { matchWithTeams ->
                    MatchCard(
                        matchWithTeams = matchWithTeams,
                        onViewMatch = { onViewMatch(matchWithTeams.match.id) },
                        onPredictNow = { onViewMatch(matchWithTeams.match.id) }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}
