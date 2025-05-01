package com.emirbardakci.uconnectfake

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import com.emirbardakci.uconnectfake.ui.screens.UconnectLauncherScreen
import com.emirbardakci.uconnectfake.ui.theme.UconnectFakeTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Set window to full screen and hide system bars
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        
        setContent {
            UconnectFakeTheme {
                // Configure to hide system bars
                val systemUiController = rememberSystemUiController()
                SideEffect {
                    // Hide both the status bar and the navigation bar
                    systemUiController.setSystemBarsColor(
                        color = Color.Transparent,
                        darkIcons = false
                    )
                    systemUiController.isSystemBarsVisible = false
                }
                
                UconnectApp()
            }
        }
    }
}

@Composable
fun UconnectApp() {
    UconnectLauncherScreen()
}

@Preview(showBackground = true)
@Composable
fun UconnectAppPreview() {
    UconnectFakeTheme {
        UconnectApp()
    }
}