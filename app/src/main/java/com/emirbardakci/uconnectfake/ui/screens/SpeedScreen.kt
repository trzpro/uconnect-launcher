package com.emirbardakci.uconnectfake.ui.screens

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.emirbardakci.uconnectfake.MainActivity
import com.emirbardakci.uconnectfake.R
import com.emirbardakci.uconnectfake.ui.theme.UconnectRedLine
import com.emirbardakci.uconnectfake.ui.theme.UconnectTextWhite
// LocationUtils'den fonksiyonları import et
import com.emirbardakci.uconnectfake.ui.screens.checkLocationPermission
import com.emirbardakci.uconnectfake.ui.screens.formatSpeed
import com.emirbardakci.uconnectfake.ui.screens.startLocationUpdates
import com.emirbardakci.uconnectfake.utils.loadAppConfig
// SettingsManager ve BottomBarPosition aynı pakette, ama açıkça referans edelim
import com.emirbardakci.uconnectfake.ui.screens.SettingsManager
import com.emirbardakci.uconnectfake.ui.screens.BottomBarPosition

@Composable
fun SpeedScreen(onBackClick: () -> Unit) {
    val context = LocalContext.current
    var currentSpeed by remember { mutableStateOf(0.0f) }
    var hasLocationPermission by remember { mutableStateOf(false) }
    val appConfig = remember { loadAppConfig(context) }
    
    // Bottom bar pozisyonunu ayarlardan oku
    var bottomBarPosition by remember { mutableStateOf(SettingsManager.getBottomBarPosition(context)) }
    
    // Ayarlar değiştiğinde bottom bar pozisyonunu güncelle
    LaunchedEffect(Unit) {
        bottomBarPosition = SettingsManager.getBottomBarPosition(context)
    }
    
    // Geri tuşu davranışı
    BackHandler(onBack = onBackClick)
    
    // Yatay gradyan (kırmızı çizgi) için brush
    val horizontalGradientBrush = Brush.horizontalGradient(
        0f to Color.Transparent,
        0.3f to UconnectRedLine,
        0.5f to UconnectRedLine,
        0.7f to UconnectRedLine,
        1f to Color.Transparent
    )
    
    // Konum izinlerini kontrol et
    LaunchedEffect(key1 = true) {
        hasLocationPermission = checkLocationPermission(context)
        if (hasLocationPermission) {
            startLocationUpdates(context) { newSpeed ->
                currentSpeed = newSpeed
            }
        } else {
            Toast.makeText(
                context,
                "Konum izni gerekiyor!",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
    
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Arkaplan resmi
        Image(
            painter = painterResource(id = R.drawable.background),
            contentDescription = "Background",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )
        
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Bottom bar üstte ise önce bottom bar'ı göster
            if (bottomBarPosition == BottomBarPosition.TOP) {
                UconnectBottomBar(
                    bottomBarApps = appConfig.bottomBarApps.toMutableList(),
                    appsList = appConfig.apps,
                    onAppClick = { app ->
                        when {
                            app.action == "open_speed_screen" -> {
                                // Zaten speed ekranındayız, bir şey yapma
                            }
                            app.action == "open_apps_screen" -> {
                                // Apps ekranına git
                                val intent = Intent(context, MainActivity::class.java).apply {
                                    putExtra("screen", "apps")
                                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                }
                                context.startActivity(intent)
                            }
                            !app.packageName.isNullOrEmpty() -> {
                                // Uygulamayı başlat
                                val launchIntent = context.packageManager.getLaunchIntentForPackage(app.packageName)
                                if (launchIntent != null) {
                                    launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    context.startActivity(launchIntent)
                                }
                            }
                            !app.action.isNullOrEmpty() -> {
                                // Sistem ayarlarını aç
                                val intent = Intent(app.action)
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                context.startActivity(intent)
                            }
                        }
                    }
                )
            }
            
            // Top bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(75.dp)
            ) {
                // Geri butonu
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 16.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_arrow_left),
                        contentDescription = "Back",
                        tint = UconnectTextWhite,
                        modifier = Modifier.size(32.dp)
                    )
                }
                
                // Başlık
                Text(
                    text = "Speed",
                    color = UconnectTextWhite,
                    fontSize = 28.sp,
                    modifier = Modifier
                        .align(Alignment.Center)
                )
            }
            
            // Gradyan kırmızı çizgi
            Spacer(modifier = Modifier.height(1.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(brush = horizontalGradientBrush)
            )
            
            // Info text
            Text(
                text = if (hasLocationPermission) "Current speed" else "Location permission required",
                color = UconnectRedLine,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )
            
            // Speed display
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Hız göstergesi
                    Text(
                        text = formatSpeed(currentSpeed),
                        color = UconnectTextWhite,
                        fontSize = 72.sp,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Text(
                        text = "km/h",
                        color = UconnectTextWhite,
                        fontSize = 32.sp
                    )
                }
            }
            
            // Bottom bar altta ise en sonda göster
            if (bottomBarPosition == BottomBarPosition.BOTTOM) {
                UconnectBottomBar(
                    bottomBarApps = appConfig.bottomBarApps.toMutableList(),
                    appsList = appConfig.apps,
                    onAppClick = { app ->
                        when {
                            app.action == "open_speed_screen" -> {
                                // Zaten speed ekranındayız, bir şey yapma
                            }
                            app.action == "open_apps_screen" -> {
                                // Apps ekranına git
                                val intent = Intent(context, MainActivity::class.java).apply {
                                    putExtra("screen", "apps")
                                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                }
                                context.startActivity(intent)
                            }
                            !app.packageName.isNullOrEmpty() -> {
                                // Uygulamayı başlat
                                val launchIntent = context.packageManager.getLaunchIntentForPackage(app.packageName)
                                if (launchIntent != null) {
                                    launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    context.startActivity(launchIntent)
                                }
                            }
                            !app.action.isNullOrEmpty() -> {
                                // Sistem ayarlarını aç
                                val intent = Intent(app.action)
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                context.startActivity(intent)
                            }
                        }
                    }
                )
            }
        }
    }
} 