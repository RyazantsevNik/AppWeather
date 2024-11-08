package com.example.appweather.bottom_navigation_bar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home

object Constants {
    val BottomNavItems = listOf(
        BottomNavItem(
            label = "Today",
            icon = Icons.Filled.Home,
            route = "main_screen"
        ),
        BottomNavItem(
            label = "Weekly",
            icon = Icons.Filled.DateRange,
            route = "second_screen"
        )
    )
}