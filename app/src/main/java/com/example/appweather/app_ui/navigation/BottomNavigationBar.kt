package com.example.appweather.app_ui.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.appweather.bottom_navigation_bar.Constants

@Composable
fun BottomNavigationBar(navController: NavController) {
    BottomNavigation(
        backgroundColor = Color(0xFF0372A1)
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        Constants.BottomNavItems.forEach { navItem ->
            val isSelected = currentRoute == navItem.route
            BottomNavigationItem(
                selected = isSelected,
                onClick = {
                    // навигация происходит только если маршрут отличается
                    if (currentRoute != navItem.route) {
                        navController.navigate(navItem.route) {
                            popUpTo("main_screen") { inclusive = false }  // Возврат к "main_screen"
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = navItem.icon,
                            contentDescription = navItem.label,
                            modifier = Modifier.size(24.dp),
                            tint = if (isSelected) Color.White else Color.Gray
                        )
                        if (isSelected) {
                            Text(
                                text = navItem.label,
                                fontSize = 12.sp,
                                color = Color.White
                            )
                        }
                    }
                },
                alwaysShowLabel = false,
                selectedContentColor = Color.White,
                unselectedContentColor = Color.Gray
            )
        }
    }
}