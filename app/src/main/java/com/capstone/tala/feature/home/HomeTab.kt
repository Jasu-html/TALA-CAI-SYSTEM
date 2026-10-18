package com.capstone.tala.feature.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Checkroom
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Home screen tabs for bottom navigation.
 * Only PATH is implemented in Phase 5; others show "Coming Soon" panels.
 */
enum class HomeTab(
    val label: String,
    val icon: ImageVector,
    val contentDescription: String
) {
    PATH(
        label = "Path",
        icon = Icons.Filled.Home,
        contentDescription = "Star Path"
    ),
    CLOSET(
        label = "Closet",
        icon = Icons.Outlined.Checkroom,
        contentDescription = "Avatar Closet"
    ),
    SHOP(
        label = "Shop",
        icon = Icons.Filled.Star,
        contentDescription = "Star Shop"
    ),
    SKY_MAP(
        label = "Sky Map",
        icon = Icons.Filled.Map,
        contentDescription = "Sky Map"
    )
}
