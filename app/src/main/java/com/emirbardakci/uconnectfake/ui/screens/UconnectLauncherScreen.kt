package com.emirbardakci.uconnectfake.ui.screens

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.emirbardakci.uconnectfake.R
import com.emirbardakci.uconnectfake.ui.components.UconnectBottomNavItem
import com.emirbardakci.uconnectfake.ui.theme.*
import com.emirbardakci.uconnectfake.utils.LocationUtils
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
// LocationUtils'den fonksiyonları import et

data class UconnectApp(
    val name: String,
    val iconResId: Int,
    val backgroundColor: Color = Color.Gray,
    val packageName: String? = null,
    val action: String? = null,
    val data: Uri? = null,
    val tint: Color
)

@Composable
fun UconnectBottomBar(
    onAppClick: (UconnectApp) -> Unit
) {
    // Bottom navigation items - önceden hesapla
    val bottomNavItems = remember {
        listOf(
            UconnectApp(
                name = "Radio",
                iconResId = R.drawable.ic_radio,
                backgroundColor = Color.Transparent,
                packageName = "com.tw.radio",
                tint = UconnectTextWhite
            ),
            UconnectApp(
                name = "Media",
                iconResId = R.drawable.ic_media,
                backgroundColor = Color.Transparent,
                packageName = "com.tw.eq",
                tint = UconnectTextWhite
            ),
            UconnectApp(
                name = "Nav",
                iconResId = R.drawable.ic_nav,
                backgroundColor = Color.Transparent,
                packageName = "com.google.android.apps.maps",
                tint = UconnectTextWhite
            ),
            UconnectApp(
                name = "Uconnect",
                iconResId = R.drawable.ic_uconnect,
                backgroundColor = Color.Transparent,
                packageName = "com.zjinnova.zlink",
                tint = UconnectRedLine
            ),
            UconnectApp(
                name = "Phone",
                iconResId = R.drawable.ic_phone,
                backgroundColor = Color.Transparent,
                packageName = "com.tw.bt",
                tint = UconnectTextWhite
            ),
            UconnectApp(
                name = "Settings",
                iconResId = R.drawable.ic_settings,
                backgroundColor = Color.Transparent,
                action = android.provider.Settings.ACTION_SETTINGS,
                tint = UconnectTextWhite
            ),
            UconnectApp(
                name = "Speed",
                iconResId = R.drawable.ic_trip,
                backgroundColor = Color.Transparent,
                action = "open_speed_screen",
                tint = UconnectTextWhite
            )
        )
    }

    // Dikey gradyan için brush - önceden hesapla
    val verticalGradientBrush = remember {
        Brush.verticalGradient(
            0f to Color.Transparent,
            0.3f to UconnectRedLine,
            0.5f to UconnectRedLine,
            0.7f to UconnectRedLine,
            1f to Color.Transparent
        )
    }

    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .padding(vertical = 5.dp)
    ) {
        bottomNavItems.forEachIndexed { index, navItem ->
            if (index > 0) {
                Box(
                    modifier = Modifier
                        .height(40.dp)
                        .width(1.dp)
                        .background(brush = verticalGradientBrush)
                )
            }
            
            UconnectBottomNavItem(
                iconResId = navItem.iconResId,
                label = navItem.name,
                onClick = { onAppClick(navItem) },
                tint = navItem.tint
            )
        }
    }
}

@Composable
fun UconnectLauncherScreen() {
    var currentTime by remember { mutableStateOf(getCurrentTime()) }
    val context = LocalContext.current
    
    // Hız verisi için state
    var currentSpeed by remember { mutableStateOf(0.0f) }
    var hasLocationPermission by remember { mutableStateOf(false) }
    
    // Konum izni için launcher
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.entries.all { it.value }
        hasLocationPermission = allGranted
        if (allGranted) {
            startLocationUpdates(context) { newSpeed ->
                currentSpeed = newSpeed
            }
        } else {
            Toast.makeText(
                context,
                "Konum izni olmadan hız bilgisi gösterilemez",
                Toast.LENGTH_LONG
            ).show()
        }
    }
    
    // Uygulama başladığında konum izni kontrolü
    LaunchedEffect(key1 = true) {
        val fineLocationPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        val coarseLocationPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        
        hasLocationPermission = fineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                coarseLocationPermission == PackageManager.PERMISSION_GRANTED
        
        if (!hasLocationPermission) {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        } else {
            startLocationUpdates(context) { newSpeed ->
                currentSpeed = newSpeed
            }
        }
    }
    
    // AppsScreen'e geçiş için state
    var showAppsScreen by remember { mutableStateOf(false) }
    
    // SpeedScreen'e geçiş için state
    var showSpeedScreen by remember { mutableStateOf(false) }
    
    if (showAppsScreen) {
        AppsScreen(onBackClick = { showAppsScreen = false })
        return
    }
    
    if (showSpeedScreen) {
        SpeedScreen(onBackClick = { showSpeedScreen = false })
        return
    }
    
    // Saat güncellemesi için timer - 5 saniyede bir güncelle
    LaunchedEffect(key1 = true) {
        while (true) {
            delay(5000)
            currentTime = getCurrentTime()
        }
    }

    // Yatay gradyan (kırmızı çizgi) için brush - önceden hesapla
    val horizontalGradientBrush = remember {
        Brush.horizontalGradient(
            0f to Color.Transparent,
            0.3f to UconnectRedLine,
            0.5f to UconnectRedLine,
            0.7f to UconnectRedLine,
            1f to Color.Transparent
        )
    }
    
    // Create app launcher function
    val launchApp: (UconnectApp) -> Unit = { app ->
        try {
            when {
                app.action == "open_apps_screen" -> {
                    showAppsScreen = true
                }
                app.action == "open_speed_screen" -> {
                    showSpeedScreen = true
                }
                !app.packageName.isNullOrEmpty() -> {
                    val launchIntent = context.packageManager.getLaunchIntentForPackage(app.packageName)
                    if (launchIntent != null) {
                        launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        context.startActivity(launchIntent)
                    } else {
                        try {
                            val marketIntent = Intent(Intent.ACTION_VIEW)
                            marketIntent.data = Uri.parse("market://details?id=${app.packageName}")
                            marketIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            context.startActivity(marketIntent)
                        } catch (e: Exception) {
                            Toast.makeText(
                                context,
                                "${app.name} uygulaması yüklü değil.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
                !app.action.isNullOrEmpty() -> {
                    val intent = Intent(app.action)
                    if (app.data != null) {
                        intent.data = app.data
                    }
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    try {
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        Toast.makeText(
                            context,
                            "${app.name} açılamıyor.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                else -> {
                    Toast.makeText(
                        context,
                        "${app.name} uygulaması için bir eylem tanımlanmamış.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } catch (e: Exception) {
            Toast.makeText(
                context,
                "Uygulama açılırken bir hata oluştu: ${e.message}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
    
    // Uygulama listesi - önceden hesapla
    val appsList = remember {
        listOf(
            UconnectApp(
                name = "Radio",
                iconResId = R.drawable.ic_radio,
                packageName = "com.tw.radio",
                tint = UconnectTextWhite
            ),
            UconnectApp(
                name = "Media",
                iconResId = R.drawable.ic_media,
                packageName = "com.tw.music",
                tint = UconnectTextWhite
            ),
            UconnectApp(
                name = "Camera",
                iconResId = R.drawable.ic_camera,
                packageName = "com.tw.auxin",
                tint = UconnectTextWhite
            ),
            UconnectApp(
                name = "CarPlay",
                iconResId = R.drawable.ic_carplay,
                packageName = "com.zjinnova.zlink",
                tint = UconnectTextWhite    
            ),
            UconnectApp(
                name = "Android Auto",
                iconResId = R.drawable.ic_android_auto,
                packageName = "com.zjinnova.zlink",
                tint = UconnectTextWhite
            ),
            UconnectApp(
                name = "Internet",
                iconResId = R.drawable.ic_browser,
                packageName = "mark.via.gp",
                tint = UconnectTextWhite
            ),
            UconnectApp(
                name = "Speed",
                iconResId = R.drawable.ic_trip,
                action = "open_speed_screen",
                tint = UconnectTextWhite // Özel action
            ),
            UconnectApp(
                name = "Files",
                iconResId = R.drawable.ic_climate,
                packageName = "com.tw.twfileexplore",
                tint = UconnectTextWhite
            ),
            UconnectApp(
                name = "Apps",
                iconResId = R.drawable.ic_app,
                action = "open_apps_screen",
                tint = UconnectTextWhite // Özel action
            ),
            UconnectApp(
                name = "Settings",
                iconResId = R.drawable.ic_settings,
                action = android.provider.Settings.ACTION_SETTINGS,
                tint = UconnectTextWhite
            ),
            UconnectApp(
                name = "BT Connect",
                iconResId = R.drawable.ic_controls,
                packageName ="com.tw.bt",
                tint = UconnectTextWhite
            ),
            UconnectApp(
                name = "Vehicle",
                iconResId = R.drawable.ic_car,
                packageName = "com.tw.auxin",
                tint = UconnectTextWhite
            ),
            UconnectApp(
                name = "Video Player",
                iconResId = R.drawable.ic_video,
                packageName = "com.tw.video",
                tint = UconnectTextWhite
            ),
            UconnectApp(
                name = "EQ",
                iconResId = R.drawable.ic_eq,
                packageName = "com.tw.eq",
                tint = UconnectTextWhite
            )
        )
    }
    
    // Sayfa numarası için state
    var currentPage by remember { mutableStateOf(0) }
    val itemsPerPage = 8
    val totalPages = (appsList.size + itemsPerPage - 1) / itemsPerPage
    
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Arkaplan resmi - önbelleğe al
        Image(
            painter = painterResource(id = R.drawable.background),
            contentDescription = "Background",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )
        
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(75.dp)
            ) {
                Text(
                    text = currentTime,
                    color = UconnectTextWhite,
                    fontSize = 28.sp,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 16.dp)
                )
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(start = 48.dp, top = 16.dp)
                ) {
                    Text(
                        text = LocationUtils.formatSpeed(currentSpeed),
                        color = UconnectTextWhite,
                        fontSize = 24.sp
                    )
                    
                    Spacer(modifier = Modifier.width(4.dp))
                    
                    Text(
                        text = "km/h",
                        color = UconnectTextWhite,
                        fontSize = 24.sp
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(1.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(brush = horizontalGradientBrush)
            )
            
            Text(
                text = "Press and Hold icons to drag to menu bar below",
                color = UconnectRedLine,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .pointerInput(Unit) {
                        detectHorizontalDragGestures { change, dragAmount ->
                            when {
                                dragAmount > 0 && currentPage > 0 -> {
                                    currentPage--
                                }
                                dragAmount < 0 && currentPage < totalPages - 1 -> {
                                    currentPage++
                                }
                            }
                        }
                    }
            ) {
                // Sol ok tuşu
                if (currentPage > 0) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(start = 8.dp)
                            .size(48.dp)
                            .clickable { currentPage-- }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_arrow_left),
                            contentDescription = "Previous Page",
                            tint = UconnectTextWhite,
                            modifier = Modifier
                                .align(Alignment.Center)
                                .size(32.dp)
                        )
                    }
                }
                
                // Sağ ok tuşu
                if (currentPage < totalPages - 1) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = 8.dp)
                            .size(48.dp)
                            .clickable { currentPage++ }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_arrow_right),
                            contentDescription = "Next Page",
                            tint = UconnectTextWhite,
                            modifier = Modifier
                                .align(Alignment.Center)
                                .size(32.dp)
                        )
                    }
                }
                
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    contentPadding = PaddingValues(horizontal = 15.dp, vertical = 16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    val startIndex = currentPage * itemsPerPage
                    val endIndex = minOf(startIndex + itemsPerPage, appsList.size)
                    items(appsList.subList(startIndex, endIndex)) { app ->
                        UconnectAppButton(
                            app = app,
                            onClick = { launchApp(app) }
                        )
                    }
                }
            }
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(brush = horizontalGradientBrush)
            )
            
            // Bottom bar'ı en alta ekle
            UconnectBottomBar(
                onAppClick = { app ->
                    if (app.packageName != null || app.action != null) {
                        launchApp(app)
                    } else {
                        Toast.makeText(
                            context,
                            "${app.name} seçildi",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            )
        }
    }
}

// Mevcut saati "HH:mm" formatında döndüren fonksiyon
fun getCurrentTime(): String {
    val sdf = SimpleDateFormat("H:mm", Locale.getDefault())
    return sdf.format(Date())
}

// Updated preview with landscape orientation
@Preview(showBackground = true, widthDp = 960, heightDp = 540)
@Composable
fun UconnectLauncherScreenPreview() {
    UconnectFakeTheme {
        UconnectLauncherScreen()
    }
}

@Composable
private fun UconnectAppButton(
    app: UconnectApp,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxWidth()
            .padding(15.dp)
            .clickable(onClick = onClick)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(90.dp)
        ) {
            Icon(
                painter = painterResource(id = app.iconResId),
                contentDescription = app.name,
                tint = Color.White,
                modifier = Modifier.size(75.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = app.name,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White,
            textAlign = TextAlign.Center
        )
    }
} 