package com.example.appweather

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material.Scaffold
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.rememberNavController
import com.example.appweather.ui.theme.AppWeatherTheme

class MainActivity : ComponentActivity() {
    private val hideHandler = Handler(Looper.getMainLooper())
    private val hideNavigationRunnable = Runnable { hideSystemUI() }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val weatherViewModel = ViewModelProvider(this)[WeatherViewModel::class.java]
        enableEdgeToEdge()
        setContent {
            AppWeatherTheme {
                val navController = rememberNavController()
                Scaffold(
                    bottomBar = {
                        BottomNavigationBar(navController = navController)
                    }
                ) { padding ->

                    AppNavigation(
                        navController = navController,
                        viewModel = weatherViewModel,
                        padding = padding
                    )
                }
            }
        }
        hideSystemUI()
        @Suppress("DEPRECATION")
        window.decorView.setOnSystemUiVisibilityChangeListener { visibility ->

            if (visibility and View.SYSTEM_UI_FLAG_HIDE_NAVIGATION == 0) {
                hideHandler.removeCallbacks(hideNavigationRunnable)
                hideHandler.postDelayed(hideNavigationRunnable, 3000)
            }
        }
    }

    private fun hideSystemUI() {
        @Suppress("DEPRECATION")
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                )
    }

    override fun onDestroy() {
        super.onDestroy()
        hideHandler.removeCallbacks(hideNavigationRunnable) // Убираем обработчики при уничтожении активности
    }

}