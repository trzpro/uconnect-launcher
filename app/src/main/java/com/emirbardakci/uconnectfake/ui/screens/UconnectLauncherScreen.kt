package com.emirbardakci.uconnectfake.ui.screens

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.zIndex
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sqrt
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.drawable.toBitmap
import androidx.compose.foundation.Image
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.emirbardakci.uconnectfake.R
import com.emirbardakci.uconnectfake.ui.components.UconnectBottomNavItem
import com.emirbardakci.uconnectfake.ui.theme.*
import com.emirbardakci.uconnectfake.utils.LocationUtils
import com.emirbardakci.uconnectfake.utils.loadAppConfig
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
// LocationUtils'den fonksiyonları import et
import com.emirbardakci.uconnectfake.ui.screens.startLocationUpdates
// SettingsManager ve BottomBarPosition aynı pakette, ama açıkça referans edelim
import com.emirbardakci.uconnectfake.ui.screens.SettingsManager
import com.emirbardakci.uconnectfake.ui.screens.BottomBarPosition

data class UconnectApp(
    val name: String,
    val iconResId: Int,
    val backgroundColor: Color = Color.Gray,
    val packageName: String? = null,
    val action: String? = null,
    val data: Uri? = null,
    val tint: Color,
    val iconDrawable: Drawable? = null
)

@Composable
fun UconnectBottomBar(
    bottomBarApps: MutableList<UconnectApp>,
    appsList: List<UconnectApp>,
    onAppClick: (UconnectApp) -> Unit,
    onAppRemove: ((Int) -> Unit)? = null,
    onAppDragToGrid: ((Int) -> Unit)? = null,
    onAppDropFromGrid: ((UconnectApp, Int) -> Unit)? = null,
    onAppAddFromList: ((UconnectApp, Int) -> Unit)? = null,
    draggedFromGrid: UconnectApp? = null,
    onDraggedFromGridChanged: ((UconnectApp?) -> Unit)? = null,
    isEditMode: Boolean = false,
    maxSlots: Int = 7,
    modifier: Modifier = Modifier
) {
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
    
    // Yatay gradyan (üstteki çizgi) için brush
    val horizontalGradientBrush = remember {
        Brush.horizontalGradient(
            0f to Color.Transparent,
            0.3f to UconnectRedLine,
            0.5f to UconnectRedLine,
            0.7f to UconnectRedLine,
            1f to Color.Transparent
        )
    }
    
    // Long press state'leri
    var longPressedIndex by remember { mutableStateOf<Int?>(null) }
    // Dialog state
    var showAddAppDialog by remember { mutableStateOf<Int?>(null) }
    
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        // Üstteki gradient line
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(brush = horizontalGradientBrush)
        )
        
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .padding(vertical = 5.dp)
        ) {
        // Maksimum slot sayısı kadar item göster
        for (index in 0 until maxSlots) {
            if (index > 0) {
                Box(
                    modifier = Modifier
                        .height(40.dp)
                        .width(1.dp)
                        .background(brush = verticalGradientBrush)
                )
            }
            
            val app = if (index < bottomBarApps.size) bottomBarApps[index] else null
            val isEmpty = app == null
            val isLongPressed = longPressedIndex == index
            
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .pointerInput(index) {
                        if (isEmpty && draggedFromGrid != null) {
                            // Boş slot'a grid'den item bırakma
                            detectTapGestures(
                                onTap = {
                                    onAppDropFromGrid?.invoke(draggedFromGrid!!, index)
                                    onDraggedFromGridChanged?.invoke(null)
                                }
                            )
                        } else if (isEmpty) {
                            // Boş slot'a tıklama - dialog aç
                            detectTapGestures(
                                onTap = {
                                    showAddAppDialog = index
                                }
                            )
                        } else {
                            // Dolu slot - long press ve tap
                            val nonNullApp = app // app null değil çünkü !isEmpty
                            if (!isEditMode) {
                                // Edit mode kapalıyken normal gesture detection
                                detectTapGestures(
                                    onLongPress = {
                                        longPressedIndex = index
                                    },
                                    onTap = {
                                        if (longPressedIndex == index) {
                                            // Çarpı işaretine tıklandı - sil
                                            try {
                                                onAppRemove?.invoke(index)
                                            } catch (e: Exception) {
                                                // Hata durumunda sessizce devam et
                                            }
                                            longPressedIndex = null
                                        } else {
                                            try {
                                                onAppClick(nonNullApp)
                                            } catch (e: Exception) {
                                                // Hata durumunda sessizce devam et
                                            }
                                        }
                                    }
                                )
                            }
                            // Edit mode açıkken pointerInput yok, sadece X ikonuna tıklanabilir
                        }
                    }
            ) {
                if (isEmpty) {
                    // Boş slot - her zaman + göster
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add app",
                            tint = UconnectRedLine,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                } else {
                    // Dolu slot
                    app?.let { nonNullApp ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(),
                            contentAlignment = Alignment.Center
                        ) {
                            UconnectBottomNavItem(
                                iconResId = nonNullApp.iconResId,
                                label = nonNullApp.name,
                                onClick = { 
                                    if (!isEditMode && longPressedIndex != index) {
                                        try {
                                            onAppClick(nonNullApp)
                                        } catch (e: Exception) {
                                            // Hata durumunda sessizce devam et
                                        }
                                    }
                                },
                                tint = nonNullApp.tint
                            )
                            
                            // Long press veya edit mode durumunda çarpı işareti
                            if (isLongPressed || isEditMode) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .offset(x = (-8).dp, y = (-8).dp)
                                        .size(24.dp)
                                        .zIndex(1f)
                                        .background(
                                            color = UconnectRedLine,
                                            shape = CircleShape
                                        )
                                        .clickable(
                                            enabled = true,
                                            onClick = {
                                                try {
                                                    onAppRemove?.invoke(index)
                                                } catch (e: Exception) {
                                                    // Hata durumunda sessizce devam et
                                                }
                                                longPressedIndex = null
                                            }
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Remove",
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        }
        
        // Uygulama ekleme dialog'u
        showAddAppDialog?.let { slotIndex ->
            AlertDialog(
                onDismissRequest = { showAddAppDialog = null },
                title = {
                    Text(
                        text = "Uygulama Seç",
                        color = UconnectTextWhite
                    )
                },
                text = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 400.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        appsList.forEach { app ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onAppAddFromList?.invoke(app, slotIndex)
                                        showAddAppDialog = null
                                    }
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    painter = painterResource(id = app.iconResId),
                                    contentDescription = app.name,
                                    tint = app.tint ?: Color.White,
                                    modifier = Modifier.size(40.dp)
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(
                                    text = app.name,
                                    color = UconnectTextWhite,
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showAddAppDialog = null }) {
                        Text("İptal", color = UconnectRedLine)
                    }
                },
                containerColor = Color.Black.copy(alpha = 0.9f),
                titleContentColor = UconnectTextWhite,
                textContentColor = UconnectTextWhite
            )
        }
    }
}

@Composable
fun UconnectLauncherScreen(
    currentBottomBarPosition: BottomBarPosition = BottomBarPosition.BOTTOM,
    currentBottomBarEditMode: Boolean = false,
    currentSavedPackages: List<String?> = emptyList(),
    currentUseDefaultIcons: List<Boolean> = emptyList(),
    currentVideoPlayerVisible: Boolean = true,
    currentBottomBarPackages: List<String?> = emptyList(),
    currentBottomBarUseDefaultIcons: List<Boolean> = emptyList(),
    onSettingsChanged: (() -> Unit)? = null
) {
    var currentTime by remember { mutableStateOf(getCurrentTime()) }
    val context = LocalContext.current
    
    // Hız verisi için state
    var currentSpeed by remember { mutableStateOf(0.0f) }
    var hasLocationPermission by remember { mutableStateOf(false) }
    
    // Konum izni için launcher
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions: Map<String, Boolean> ->
        val allGranted = permissions.entries.all { it.value }
        hasLocationPermission = allGranted
        if (allGranted) {
            startLocationUpdates(context) { newSpeed: Float ->
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
            startLocationUpdates(context) { newSpeed: Float ->
                currentSpeed = newSpeed
            }
        }
    }
    
    // AppsScreen'e geçiş için state
    var showAppsScreen by remember { mutableStateOf(false) }
    
    // SpeedScreen'e geçiş için state
    var showSpeedScreen by remember { mutableStateOf(false) }
    
    // SettingsScreen'e geçiş için state
    var showSettingsScreen by remember { mutableStateOf(false) }
    
    // Parametreleri kullan
    val bottomBarPosition = currentBottomBarPosition
    val bottomBarEditMode = currentBottomBarEditMode
    val savedPackages = currentSavedPackages
    val useDefaultIcons = currentUseDefaultIcons
    
    if (showAppsScreen) {
        AppsScreen(onBackClick = { showAppsScreen = false })
        return
    }
    
    if (showSpeedScreen) {
        SpeedScreen(onBackClick = { showSpeedScreen = false })
        return
    }
    
    if (showSettingsScreen) {
        SettingsScreen(
            onBackClick = { 
                showSettingsScreen = false
            },
            onDpiChanged = {
                onSettingsChanged?.invoke()
            }
        )
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
                app.action == "open_settings_screen" -> {
                    showSettingsScreen = true
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
    
    // Uygulama listesi - JSON'dan yükle
    val appConfig = remember { loadAppConfig(context) }
    
    // Uygulama listesini oluştur - ayarlardan gelen paketleri kullan veya default JSON'dan
    var appsList by remember(appConfig, savedPackages, useDefaultIcons) {
        val currentPackages = savedPackages
        val currentUseDefaultIcons = useDefaultIcons
        val packages = if (currentPackages.isNotEmpty()) {
            currentPackages
        } else {
            // Default olarak JSON'dan gelen paketler
            appConfig.apps.map { it.packageName }
        }
        
        val list = packages.mapIndexed { index, packageName ->
            val defaultApp = appConfig.apps.getOrNull(index)
            val shouldUseDefaultIcon = currentUseDefaultIcons.getOrNull(index) ?: false
            // Önce packageName kontrolü yap - eğer varsa onu kullan (varsayılan ikonların üzerine de app atanabilir)
            if (packageName != null) {
                // Paket adından uygulama bilgilerini al
                try {
                    val packageManager = context.packageManager
                    val appInfo = packageManager.getApplicationInfo(packageName, 0)
                    val appName = packageManager.getApplicationLabel(appInfo).toString()
                    val icon = packageManager.getApplicationIcon(appInfo)
                    
                    // UconnectApp oluştur - varsayılan ikon kullanılacaksa iconDrawable'ı null yap
                    UconnectApp(
                        name = appName,
                        iconResId = defaultApp?.iconResId ?: R.drawable.ic_app, // Fallback icon
                        packageName = packageName,
                        action = null, // Paket seçildiyse action'ı override et
                        data = null,
                        tint = UconnectTextWhite,
                        iconDrawable = if (shouldUseDefaultIcon) null else icon
                    )
                } catch (e: Exception) {
                    // Paket bulunamazsa default app'i kullan
                    defaultApp ?: UconnectApp(
                        name = "Unknown",
                        iconResId = R.drawable.ic_app,
                        packageName = null,
                        action = null,
                        data = null,
                        tint = UconnectTextWhite
                    )
                }
            } else {
                // Paket seçilmemişse default app'i kullan (action'ı varsa onu koru)
                defaultApp ?: UconnectApp(
                    name = "Empty",
                    iconResId = R.drawable.ic_app,
                    packageName = null,
                    action = null,
                    data = null,
                    tint = UconnectTextWhite
                )
            }
        }.toMutableList()
        
        // Video Player'ı ayarlara göre filtrele
        if (!currentVideoPlayerVisible) {
            list.removeAll { it.packageName == "com.tw.video" || it.name == "Video Player" }
        }
        
        mutableStateOf(list)
    }
    
    var bottomBarApps by remember(appConfig, currentBottomBarPackages, currentBottomBarUseDefaultIcons) {
        val packages = if (currentBottomBarPackages.isNotEmpty()) {
            currentBottomBarPackages
        } else {
            appConfig.bottomBarApps.map { it.packageName }
        }
        
        val list = packages.mapIndexed { index, packageName ->
            val defaultApp = appConfig.bottomBarApps.getOrNull(index)
            val shouldUseDefaultIcon = currentBottomBarUseDefaultIcons.getOrNull(index) ?: false
            
            if (packageName != null) {
                try {
                    val packageManager = context.packageManager
                    val appInfo = packageManager.getApplicationInfo(packageName, 0)
                    val appName = packageManager.getApplicationLabel(appInfo).toString()
                    val icon = packageManager.getApplicationIcon(appInfo)
                    
                    UconnectApp(
                        name = appName,
                        iconResId = defaultApp?.iconResId ?: R.drawable.ic_app,
                        packageName = packageName,
                        action = null,
                        data = null,
                        tint = UconnectTextWhite,
                        iconDrawable = if (shouldUseDefaultIcon) null else icon
                    )
                } catch (e: Exception) {
                    defaultApp ?: UconnectApp(name = "Unknown", iconResId = R.drawable.ic_app, packageName = null, action = null, data = null, tint = UconnectTextWhite)
                }
            } else {
                defaultApp ?: UconnectApp(name = "Empty", iconResId = R.drawable.ic_app, packageName = null, action = null, data = null, tint = UconnectTextWhite)
            }
        }.toMutableList()
        
        // Video Player'ı ayarlara göre filtrele
        if (!currentVideoPlayerVisible) {
            list.removeAll { it.packageName == "com.tw.video" || it.name == "Video Player" }
        }
        
        mutableStateOf(list)
    }
    
    // Scroll için state'ler
    var scrollOffset by remember { mutableStateOf(0f) }
    var isScrolling by remember { mutableStateOf(false) }
    var containerWidth by remember { mutableStateOf(0f) }
    val density = LocalDensity.current
    val itemsPerRow = 4
    val itemHeight = 120f * density.density
    // Container height'ı 2 satır için sabit yap (4x2 grid)
    val containerHeight = itemHeight * 2
    
    // Drag & drop için state'ler
    var draggedAppIndex by remember { mutableStateOf<Int?>(null) }
    var dragOffsetApp by remember { mutableStateOf(Offset.Zero) }
    var isDraggingApp by remember { mutableStateOf(false) }
    var dragStartPosition by remember { mutableStateOf(Offset.Zero) }
    var targetAppIndex by remember { mutableStateOf<Int?>(null) }
    var draggedItemStartPosition by remember { mutableStateOf(Offset.Zero) }
    // Her item'in ekran pozisyonunu sakla
    val itemPositions = remember { mutableMapOf<Int, Offset>() }
    // Grid'den bottom bar'a drag için
    var draggedToBottomBar by remember { mutableStateOf<UconnectApp?>(null) }
    
    // Scroll offset'ini sınırla
    val maxScrollOffset = (appsList.size / itemsPerRow + 1) * itemHeight - containerHeight
    val clampedScrollOffset = scrollOffset.coerceIn(0f, maxOf(0f, maxScrollOffset))
    
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
        
        // Main UI container
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Top bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(90.dp)
                ) {
                    Text(
                        text = currentTime,
                        color = UconnectTextWhite,
                        fontSize = 28.sp,
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 20.dp)
                    )
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(start = 48.dp, top = 20.dp)
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
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Press and Hold icons to drag to menu bar below",
                        color = UconnectRedLine,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "${appsList.size} apps",
                        color = UconnectTextWhite,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                }
                
                // Bottom bar at TOP position
                if (bottomBarPosition == BottomBarPosition.TOP) {
                    UconnectBottomBar(
                        bottomBarApps = bottomBarApps,
                        appsList = appsList,
                        isEditMode = bottomBarEditMode,
                        onAppClick = { app -> launchApp(app) },
                        onAppRemove = { index ->
                            if (index < bottomBarApps.size) {
                                val removedApp = bottomBarApps[index]
                                bottomBarApps.removeAt(index)
                                appsList.add(removedApp)
                            }
                        },
                        onAppDragToGrid = { index ->
                            if (index < bottomBarApps.size) {
                                val app = bottomBarApps[index]
                                bottomBarApps.removeAt(index)
                                appsList.add(app)
                            }
                        },
                        onAppDropFromGrid = { app, slotIndex ->
                            if (slotIndex < bottomBarApps.size) {
                                bottomBarApps.add(slotIndex, app)
                            } else {
                                bottomBarApps.add(app)
                            }
                            appsList.remove(app)
                            draggedToBottomBar = null
                        },
                        onAppAddFromList = { app, slotIndex ->
                            if (slotIndex < bottomBarApps.size) {
                                bottomBarApps.add(slotIndex, app)
                            } else {
                                bottomBarApps.add(app)
                            }
                            appsList.remove(app)
                        },
                        draggedFromGrid = draggedToBottomBar,
                        onDraggedFromGridChanged = { app ->
                            draggedToBottomBar = app
                        }
                    )
                }
                
                // Apps Grid
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .heightIn(min = 240.dp)
                        .onGloballyPositioned { coordinates: LayoutCoordinates ->
                            containerWidth = coordinates.size.width.toFloat()
                        }
                        .pointerInput(Unit) {
                            if (containerWidth > 0) {
                                detectDragGestures(
                                    onDragStart = { isScrolling = true },
                                    onDrag = { _, dragAmount ->
                                        val newOffset = scrollOffset - dragAmount.y
                                        val maxScroll = maxOf(0f, (appsList.size / itemsPerRow + 1) * itemHeight - containerHeight)
                                        scrollOffset = newOffset.coerceIn(0f, maxScroll)
                                    },
                                    onDragEnd = {
                                        isScrolling = false
                                        val rowHeight = itemHeight
                                        val snappedOffset = (scrollOffset / rowHeight).roundToInt() * rowHeight
                                        val maxScroll = maxOf(0f, (appsList.size / itemsPerRow + 1) * itemHeight - containerHeight)
                                        scrollOffset = snappedOffset.coerceIn(0f, maxScroll)
                                    }
                                )
                            }
                        }
                ) {
                    val animatedScrollOffset by animateFloatAsState(
                        targetValue = clampedScrollOffset,
                        animationSpec = if (isScrolling) tween(0) else tween(300),
                        label = "scroll_offset"
                    )
                    
                    if (containerWidth > 0) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .offset { IntOffset(0, -animatedScrollOffset.roundToInt()) }
                        ) {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(itemsPerRow),
                                contentPadding = PaddingValues(horizontal = 15.dp, vertical = 16.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(count = appsList.size) { index ->
                                    val app = appsList[index]
                                    val isDragged = draggedAppIndex == index
                                    val isTarget = targetAppIndex == index
                                    
                                    val targetOffsetValue = if (isTarget && draggedAppIndex != null && isDraggingApp) {
                                        val draggedIndex = draggedAppIndex!!
                                        val draggedRow = draggedIndex / itemsPerRow
                                        val draggedCol = draggedIndex % itemsPerRow
                                        val targetRow = index / itemsPerRow
                                        val targetCol = index % itemsPerRow
                                        val itemWidth = containerWidth / itemsPerRow.toFloat()
                                        Offset((draggedCol - targetCol) * itemWidth, (draggedRow - targetRow) * itemHeight)
                                    } else Offset.Zero
                                    
                                    val animatedTargetOffsetX by animateFloatAsState(targetValue = targetOffsetValue.x, animationSpec = tween(200), label = "x")
                                    val animatedTargetOffsetY by animateFloatAsState(targetValue = targetOffsetValue.y, animationSpec = tween(200), label = "y")
                                    
                                    Box(
                                        modifier = Modifier.onGloballyPositioned { coordinates ->
                                            if (containerWidth > 0) {
                                                val row = index / itemsPerRow
                                                val col = index % itemsPerRow
                                                val itemWidth = containerWidth / itemsPerRow.toFloat()
                                                itemPositions[index] = Offset(col * itemWidth + itemWidth / 2f, row * itemHeight + itemHeight / 2f)
                                            }
                                        }
                                    ) {
                                        if (index == targetAppIndex && draggedAppIndex != null && isDraggingApp) {
                                            Box(modifier = Modifier.fillMaxWidth().padding(15.dp).size(90.dp).background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(8.dp)).border(2.dp, UconnectRedLine.copy(alpha = 0.6f), RoundedCornerShape(8.dp)))
                                        }
                                        UconnectAppButton(
                                            app = app,
                                            onClick = { if (!isDraggingApp) launchApp(app) },
                                            onLongPress = {
                                                draggedAppIndex = index
                                                isDraggingApp = true
                                                dragStartPosition = itemPositions[index] ?: Offset.Zero
                                                draggedItemStartPosition = itemPositions[index] ?: Offset.Zero
                                            },
                                            onDrag = { offset, change ->
                                                dragOffsetApp = offset
                                                val bottomBarY = if (bottomBarPosition == BottomBarPosition.BOTTOM) containerHeight - 100f else 100f
                                                if (draggedAppIndex != null) {
                                                    val draggedIndex = draggedAppIndex!!
                                                    val itemWidth = containerWidth / itemsPerRow.toFloat()
                                                    val startGridX = (draggedIndex % itemsPerRow) * itemWidth + itemWidth / 2f
                                                    val startGridY = (draggedIndex / itemsPerRow) * itemHeight + itemHeight / 2f
                                                    val targetScreenX = startGridX + offset.x
                                                    val targetScreenY = startGridY + offset.y
                                                    
                                                    if (kotlin.math.abs(targetScreenY - bottomBarY) < 50) {
                                                        if (draggedToBottomBar == null) draggedToBottomBar = appsList[draggedIndex]
                                                        targetAppIndex = null
                                                    } else {
                                                        draggedToBottomBar = null
                                                        val targetCol = (targetScreenX / itemWidth).roundToInt().coerceIn(0, itemsPerRow - 1)
                                                        val targetRow = ((targetScreenY + scrollOffset) / itemHeight).roundToInt().coerceAtLeast(0)
                                                        val targetIndex = targetRow * itemsPerRow + targetCol
                                                        if (targetIndex >= 0 && targetIndex < appsList.size && targetIndex != draggedIndex) targetAppIndex = targetIndex else targetAppIndex = null
                                                    }
                                                }
                                            },
                                            onDragEnd = {
                                                if (draggedAppIndex != null) {
                                                    val fromIndex = draggedAppIndex!!
                                                    if (draggedToBottomBar != null) {
                                                        if (bottomBarApps.size < 7) {
                                                            bottomBarApps.add(appsList[fromIndex])
                                                            appsList.removeAt(fromIndex)
                                                        }
                                                        draggedToBottomBar = null
                                                    } else if (targetAppIndex != null) {
                                                        val toIndex = targetAppIndex!!
                                                        val item = appsList[fromIndex]
                                                        val newList = appsList.toMutableList()
                                                        newList.removeAt(fromIndex)
                                                        newList.add((if (toIndex > fromIndex) toIndex - 1 else toIndex).coerceIn(0, newList.size), item)
                                                        appsList = newList
                                                    }
                                                }
                                                draggedAppIndex = null
                                                targetAppIndex = null
                                                isDraggingApp = false
                                                dragOffsetApp = Offset.Zero
                                            },
                                            dragOffset = if (isDragged) dragOffsetApp else Offset.Zero,
                                            isDragging = isDragged,
                                            targetOffset = Offset(animatedTargetOffsetX, animatedTargetOffsetY)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                
                // Add a spacer to prevent grid from going behind bottom bar when at bottom
                if (bottomBarPosition == BottomBarPosition.BOTTOM) {
                    Spacer(modifier = Modifier.height(100.dp))
                }
            }
            
            // Bottom bar at BOTTOM position - Absolutely anchored to screen bottom
            if (bottomBarPosition == BottomBarPosition.BOTTOM) {
                UconnectBottomBar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .background(Color.Black.copy(alpha = 0.5f)),
                    bottomBarApps = bottomBarApps,
                    appsList = appsList,
                    isEditMode = bottomBarEditMode,
                    onAppClick = { app -> launchApp(app) },
                    onAppRemove = { index ->
                        if (index < bottomBarApps.size) {
                            val removedApp = bottomBarApps[index]
                            bottomBarApps.removeAt(index)
                            appsList.add(removedApp)
                        }
                    },
                    onAppDragToGrid = { index ->
                        if (index < bottomBarApps.size) {
                            val app = bottomBarApps[index]
                            bottomBarApps.removeAt(index)
                            appsList.add(app)
                        }
                    },
                    onAppDropFromGrid = { app, slotIndex ->
                        if (slotIndex < bottomBarApps.size) {
                            bottomBarApps.add(slotIndex, app)
                        } else {
                            bottomBarApps.add(app)
                        }
                        appsList.remove(app)
                        draggedToBottomBar = null
                    },
                    onAppAddFromList = { app, slotIndex ->
                        if (slotIndex < bottomBarApps.size) {
                            bottomBarApps.add(slotIndex, app)
                        } else {
                            bottomBarApps.add(app)
                        }
                        appsList.remove(app)
                    },
                    draggedFromGrid = draggedToBottomBar,
                    onDraggedFromGridChanged = { app -> draggedToBottomBar = app }
                )
            }
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
fun UconnectAppButton(
    app: UconnectApp,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onLongPress: (() -> Unit)? = null,
    onDrag: ((Offset, PointerInputChange) -> Unit)? = null,
    onDragEnd: (() -> Unit)? = null,
    dragOffset: Offset = Offset.Zero,
    isDragging: Boolean = false,
    targetOffset: Offset = Offset.Zero
) {
    val density = LocalDensity.current
    // Item boyutunu kaydet (padding + box + text için yaklaşık değer)
    var itemSize by remember { mutableStateOf(androidx.compose.ui.geometry.Size.Zero) }
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxWidth()
            .padding(15.dp)
            .onGloballyPositioned { coordinates ->
                itemSize = androidx.compose.ui.geometry.Size(
                    coordinates.size.width.toFloat(),
                    coordinates.size.height.toFloat()
                )
            }
            .offset { 
                val totalOffset = dragOffset + targetOffset
                IntOffset(totalOffset.x.roundToInt(), totalOffset.y.roundToInt()) 
            }
            .pointerInput(Unit) {
                if (onLongPress != null && onDrag != null && onDragEnd != null) {
                    var dragAnchor = Offset.Zero
                    var currentOffset = Offset.Zero
                    detectDragGesturesAfterLongPress(
                        onDragStart = { offset: Offset ->
                            onLongPress()
                            // Parmağın item içindeki pozisyonunu kaydet (anchor)
                            // offset zaten parmağın item içindeki pozisyonu (item'in sol üst köşesine göre)
                            dragAnchor = offset
                            currentOffset = Offset.Zero
                        },
                        onDrag = { change: PointerInputChange, dragAmount: Offset ->
                            // Toplam drag miktarını hesapla
                            currentOffset += dragAmount
                            // Item'in merkezini parmağın altına getirmek için:
                            // dragAnchor: parmağın item içindeki pozisyonu (item'in sol üst köşesine göre)
                            // currentOffset: toplam drag miktarı
                            // Item'in merkezini parmağın altına getirmek için: 
                            val effectiveItemSize = if (itemSize.width > 0f && itemSize.height > 0f) {
                                itemSize
                            } else {
                                // Yaklaşık item boyutu: padding(15dp*2) + box(90dp) + text(~20dp) = ~140dp yükseklik
                                androidx.compose.ui.geometry.Size(
                                    with(density) { 120.dp.toPx() }, // Genişlik
                                    with(density) { 140.dp.toPx() }  // Yükseklik
                                )
                            }
                            // Item'in merkezini parmağın altına getirmek için offset hesapla
                            val itemCenterOffset = Offset(
                                currentOffset.x - dragAnchor.x + effectiveItemSize.width / 2f,
                                currentOffset.y - dragAnchor.y + effectiveItemSize.height / 2f
                            )
                            onDrag?.invoke(itemCenterOffset, change)
                        },
                        onDragEnd = {
                            onDragEnd()
                            currentOffset = Offset.Zero
                            dragAnchor = Offset.Zero
                        },
                        onDragCancel = {
                            onDragEnd()
                            currentOffset = Offset.Zero
                            dragAnchor = Offset.Zero
                        }
                    )
                } else {
                    detectTapGestures(
                        onTap = { onClick() }
                    )
                }
            }
            .then(if (!isDragging) Modifier.clickable(onClick = onClick) else Modifier)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(90.dp)
        ) {
            if (app.iconDrawable != null) {
                // Gerçek uygulama ikonunu göster
                Image(
                    bitmap = app.iconDrawable.toBitmap().asImageBitmap(),
                    contentDescription = app.name,
                    modifier = Modifier.size(75.dp)
                )
            } else {
                // Resource icon'u göster
                Icon(
                    painter = painterResource(id = app.iconResId),
                    contentDescription = app.name,
                    tint = Color.White,
                    modifier = Modifier.size(75.dp)
                )
            }
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