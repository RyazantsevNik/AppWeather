package com.example.appweather.bottom_navigation_bar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.appweather.R

object Constants {
    val BottomNavItems = listOf(
        BottomNavItem(
            label = "Сегодня",
            icon = Icons.Filled.Home,
            route = "main_screen"
        ),
        BottomNavItem(
            label = "Неделя",
            icon = Icons.Filled.DateRange,
            route = "second_screen"
        )
    )
}