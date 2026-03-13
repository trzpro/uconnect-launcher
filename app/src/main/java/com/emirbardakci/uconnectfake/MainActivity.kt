package com.emirbardakci.uconnectfake

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.core.view.WindowCompat
import com.emirbardakci.uconnectfake.ui.screens.SettingsManager
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
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val context = androidx.compose.ui.platform.LocalContext.current
    
    // Recomposition trigger for settings changes
    var settingsUpdateKey by remember { mutableStateOf(0) }
    
    // Read all settings here so they are synchronized across recompositions
    val dpiFactor = remember(settingsUpdateKey) { SettingsManager.getDensityDpi(context) }
    val bottomBarPosition = remember(settingsUpdateKey) { SettingsManager.getBottomBarPosition(context) }
    val bottomBarEditMode = remember(settingsUpdateKey) { SettingsManager.getBottomBarEditMode(context) }
    val savedPackages = remember(settingsUpdateKey) { SettingsManager.getLauncherAppPackages(context) }
    val useDefaultIcons = remember(settingsUpdateKey) { SettingsManager.getLauncherAppUseDefaultIcons(context) }
    val videoPlayerVisible = remember(settingsUpdateKey) { SettingsManager.getVideoPlayerVisible(context) }
    val bottomBarPackages = remember(settingsUpdateKey) { SettingsManager.getBottomBarPackages(context) }
    val bottomBarUseDefaultIcons = remember(settingsUpdateKey) { SettingsManager.getBottomBarUseDefaultIcons(context) }
    
    // Custom density ile provider
    CompositionLocalProvider(
        LocalDensity provides Density(
            density = density.density * dpiFactor,
            fontScale = density.fontScale
        )
    ) {
        UconnectLauncherScreen(
            currentBottomBarPosition = bottomBarPosition,
            currentBottomBarEditMode = bottomBarEditMode,
            currentSavedPackages = savedPackages,
            currentUseDefaultIcons = useDefaultIcons,
            currentVideoPlayerVisible = videoPlayerVisible,
            currentBottomBarPackages = bottomBarPackages,
            currentBottomBarUseDefaultIcons = bottomBarUseDefaultIcons,
            onSettingsChanged = {
                settingsUpdateKey++
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun UconnectAppPreview() {
    UconnectFakeTheme {
        UconnectApp()
    }
}