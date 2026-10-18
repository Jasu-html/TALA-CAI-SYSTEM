package com.capstone.tala.feature.home.components

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.capstone.tala.core.ui.theme.TALAColor
import com.capstone.tala.feature.home.HomeTab

/**
 * Bottom navigation bar for HomeScreen.
 * 4 tabs: Path (active), Closet, Shop, Sky Map (Phase 13+).
 *
 * Uses Material3 NavigationBar with 72dp touch targets.
 */
@Composable
fun HomeBottomNav(
    selectedTab: HomeTab,
    onTabSelected: (HomeTab) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier,
        containerColor = TALAColor.NightBlue,
        contentColor = TALAColor.BackgroundCream
    ) {
        HomeTab.values().forEach { tab ->
            NavigationBarItem(
                selected = selectedTab == tab,
                onClick = { onTabSelected(tab) },
                icon = {
                    Icon(
                        imageVector = tab.icon,
                        contentDescription = tab.contentDescription
                    )
                },
                label = {
                    Text(
                        text = tab.label,
                        style = MaterialTheme.typography.labelMedium
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = TALAColor.StarYellow,
                    selectedTextColor = TALAColor.StarYellow,
                    indicatorColor = TALAColor.NightBlue,
                    unselectedIconColor = TALAColor.BackgroundCream.copy(alpha = 0.7f),
                    unselectedTextColor = TALAColor.BackgroundCream.copy(alpha = 0.7f)
                )
            )
        }
    }
}
