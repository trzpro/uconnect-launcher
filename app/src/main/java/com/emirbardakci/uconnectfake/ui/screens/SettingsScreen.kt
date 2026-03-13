package com.emirbardakci.uconnectfake.ui.screens

import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.core.graphics.drawable.toBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.ui.text.style.TextOverflow
import com.emirbardakci.uconnectfake.R
import com.emirbardakci.uconnectfake.utils.loadAppConfig
import com.emirbardakci.uconnectfake.ui.theme.UconnectRedLine
import com.emirbardakci.uconnectfake.ui.theme.UconnectTextWhite

enum class DpiMode {
    CAR,
    PHONE
}

enum class BottomBarPosition {
    BOTTOM,
    TOP
}

object SettingsManager {
    private const val PREFS_NAME = "uconnect_settings"
    private const val KEY_DPI_MODE = "dpi_mode"
    private const val KEY_BOTTOM_BAR_POSITION = "bottom_bar_position"
    private const val KEY_BOTTOM_BAR_EDIT_MODE = "bottom_bar_edit_mode"
    private const val KEY_LAUNCHER_APP_PACKAGES = "launcher_app_packages"
    private const val KEY_LAUNCHER_APP_USE_DEFAULT_ICONS = "launcher_app_use_default_icons"
    private const val KEY_VIDEO_PLAYER_VISIBLE = "video_player_visible"
    private const val KEY_BOTTOM_BAR_PACKAGES = "bottom_bar_packages"
    private const val KEY_BOTTOM_BAR_USE_DEFAULT_ICONS_SETTING = "bottom_bar_use_default_icons_setting"
    
    fun getVideoPlayerVisible(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_VIDEO_PLAYER_VISIBLE, true)
    }
    
    fun setVideoPlayerVisible(context: Context, visible: Boolean) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_VIDEO_PLAYER_VISIBLE, visible).apply()
    }
    
    fun getDpiMode(context: Context): DpiMode {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val modeString = prefs.getString(KEY_DPI_MODE, DpiMode.CAR.name) ?: DpiMode.CAR.name
        return try {
            DpiMode.valueOf(modeString)
        } catch (e: Exception) {
            DpiMode.CAR
        }
    }
    
    fun setDpiMode(context: Context, mode: DpiMode) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_DPI_MODE, mode.name).apply()
    }
    
    fun getDensityDpi(context: Context): Float {
        val mode = getDpiMode(context)
        return when (mode) {
            DpiMode.CAR -> 1.0f // Araba için mevcut DPI
            DpiMode.PHONE -> 0.7f // Telefon için küçültülmüş DPI
        }
    }
    
    fun getBottomBarPosition(context: Context): BottomBarPosition {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val positionString = prefs.getString(KEY_BOTTOM_BAR_POSITION, BottomBarPosition.BOTTOM.name) ?: BottomBarPosition.BOTTOM.name
        return try {
            BottomBarPosition.valueOf(positionString)
        } catch (e: Exception) {
            BottomBarPosition.BOTTOM
        }
    }
    
    fun setBottomBarPosition(context: Context, position: BottomBarPosition) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_BOTTOM_BAR_POSITION, position.name).apply()
    }
    
    fun getBottomBarEditMode(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_BOTTOM_BAR_EDIT_MODE, false)
    }
    
    fun setBottomBarEditMode(context: Context, enabled: Boolean) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_BOTTOM_BAR_EDIT_MODE, enabled).apply()
    }
    
    fun getLauncherAppPackages(context: Context): List<String?> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val packagesString = prefs.getString(KEY_LAUNCHER_APP_PACKAGES, null)
        if (packagesString == null || packagesString.isEmpty()) {
            return emptyList()
        }
        return packagesString.split(",").map { if (it.isEmpty()) null else it }
    }
    
    fun setLauncherAppPackages(context: Context, packages: List<String?>) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val packagesString = packages.joinToString(",") { it ?: "" }
        prefs.edit().putString(KEY_LAUNCHER_APP_PACKAGES, packagesString).apply()
    }
    
    fun getLauncherAppUseDefaultIcons(context: Context): List<Boolean> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val iconsString = prefs.getString(KEY_LAUNCHER_APP_USE_DEFAULT_ICONS, null)
        if (iconsString == null || iconsString.isEmpty()) {
            return emptyList()
        }
        return iconsString.split(",").map { it == "true" }
    }
    
    fun setLauncherAppUseDefaultIcons(context: Context, useDefaultIcons: List<Boolean>) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val iconsString = useDefaultIcons.joinToString(",") { if (it) "true" else "false" }
        prefs.edit().putString(KEY_LAUNCHER_APP_USE_DEFAULT_ICONS, iconsString).apply()
    }

    fun getBottomBarPackages(context: Context): List<String?> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val packagesString = prefs.getString(KEY_BOTTOM_BAR_PACKAGES, null)
        if (packagesString == null || packagesString.isEmpty()) {
            return emptyList()
        }
        return packagesString.split(",").map { if (it.isEmpty()) null else it }
    }
    
    fun setBottomBarPackages(context: Context, packages: List<String?>) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val packagesString = packages.joinToString(",") { it ?: "" }
        prefs.edit().putString(KEY_BOTTOM_BAR_PACKAGES, packagesString).apply()
    }
    
    fun getBottomBarUseDefaultIcons(context: Context): List<Boolean> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val iconsString = prefs.getString(KEY_BOTTOM_BAR_USE_DEFAULT_ICONS_SETTING, null)
        if (iconsString == null || iconsString.isEmpty()) {
            return emptyList()
        }
        return iconsString.split(",").map { it == "true" }
    }
    
    fun setBottomBarUseDefaultIcons(context: Context, useDefaultIcons: List<Boolean>) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val iconsString = useDefaultIcons.joinToString(",") { if (it) "true" else "false" }
        prefs.edit().putString(KEY_BOTTOM_BAR_USE_DEFAULT_ICONS_SETTING, iconsString).apply()
    }
}

@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    onDpiChanged: (() -> Unit)? = null
) {
    val context = LocalContext.current
    var dpiMode by remember { mutableStateOf(SettingsManager.getDpiMode(context)) }
    var bottomBarPosition by remember { mutableStateOf(SettingsManager.getBottomBarPosition(context)) }
    var bottomBarEditMode by remember { mutableStateOf(SettingsManager.getBottomBarEditMode(context)) }
    var videoPlayerVisible by remember { mutableStateOf(SettingsManager.getVideoPlayerVisible(context)) }
    
    // Launcher uygulamaları seçimi için state
    val appConfig = remember { loadAppConfig(context) }
    var showLauncherAppSelector by remember { mutableStateOf(false) }
    var showBottomBarAppSelector by remember { mutableStateOf(false) }
    var selectedLauncherIndex by remember { mutableStateOf<Int?>(null) }
    var installedApps by remember { mutableStateOf<List<AppInfo>>(emptyList()) }
    
    // Geri tuşu davranışı
    BackHandler(onBack = onBackClick)
    
    // Yüklü uygulamaları al
    LaunchedEffect(Unit) {
        installedApps = getInstalledApps(context)
    }
    
    // Yatay gradyan (kırmızı çizgi) için brush
    val horizontalGradientBrush = Brush.horizontalGradient(
        0f to Color.Transparent,
        0.3f to UconnectRedLine,
        0.5f to UconnectRedLine,
        0.7f to UconnectRedLine,
        1f to Color.Transparent
    )
    
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
                    text = "Settings",
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
            
            // Settings content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 32.dp, vertical = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // DPI Mode seçimi
                Text(
                    text = "DPI Mode",
                    color = UconnectTextWhite,
                    fontSize = 24.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                // Araba seçeneği
                DpiModeOption(
                    title = "Araba",
                    description = "Tablet için ideal DPI",
                    isSelected = dpiMode == DpiMode.CAR,
                    onClick = {
                        dpiMode = DpiMode.CAR
                        SettingsManager.setDpiMode(context, DpiMode.CAR)
                        onDpiChanged?.invoke()
                    }
                )
                
                // Telefon seçeneği
                DpiModeOption(
                    title = "Telefon",
                    description = "Telefon için küçültülmüş DPI",
                    isSelected = dpiMode == DpiMode.PHONE,
                    onClick = {
                        dpiMode = DpiMode.PHONE
                        SettingsManager.setDpiMode(context, DpiMode.PHONE)
                        onDpiChanged?.invoke()
                    }
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Bottom Bar Position seçimi
                Text(
                    text = "Bottom Bar Position",
                    color = UconnectTextWhite,
                    fontSize = 24.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                // Alt seçeneği
                DpiModeOption(
                    title = "Alt",
                    description = "Bottom bar ekranın altında",
                    isSelected = bottomBarPosition == BottomBarPosition.BOTTOM,
                    onClick = {
                        bottomBarPosition = BottomBarPosition.BOTTOM
                        SettingsManager.setBottomBarPosition(context, BottomBarPosition.BOTTOM)
                        onDpiChanged?.invoke()
                    }
                )
                
                // Üst seçeneği
                DpiModeOption(
                    title = "Üst",
                    description = "Bottom bar ekranın üstünde",
                    isSelected = bottomBarPosition == BottomBarPosition.TOP,
                    onClick = {
                        bottomBarPosition = BottomBarPosition.TOP
                        SettingsManager.setBottomBarPosition(context, BottomBarPosition.TOP)
                        onDpiChanged?.invoke()
                    }
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Bottom Bar Düzenleme seçimi
                Text(
                    text = "Bottom Bar Düzenleme",
                    color = UconnectTextWhite,
                    fontSize = 24.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                DpiModeOption(
                    title = "Düzeni Değiştir",
                    description = "Bottom bar'ı düzenleme moduna geçir",
                    isSelected = bottomBarEditMode,
                    onClick = {
                        bottomBarEditMode = !bottomBarEditMode
                        SettingsManager.setBottomBarEditMode(context, bottomBarEditMode)
                        onDpiChanged?.invoke()
                    }
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Launcher Uygulamaları seçimi
                Text(
                    text = "Launcher Uygulamaları",
                    color = UconnectTextWhite,
                    fontSize = 24.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                DpiModeOption(
                    title = "Uygulamaları Seç",
                    description = "Launcher'da gösterilecek uygulamaları telefon listesinden seç",
                    isSelected = false,
                    onClick = {
                        showLauncherAppSelector = true
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Bottom Bar Uygulamaları seçimi
                Text(
                    text = "Bottom Bar Uygulamaları",
                    color = UconnectTextWhite,
                    fontSize = 24.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                DpiModeOption(
                    title = "Bottom Bar Uygulamalarını Seç",
                    description = "Bottom Bar'da gösterilecek uygulamaları telefon listesinden seç",
                    isSelected = false,
                    onClick = {
                        showBottomBarAppSelector = true
                    }
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Video Player Görünürlüğü
                Text(
                    text = "Görünüm Ayarları",
                    color = UconnectTextWhite,
                    fontSize = 24.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                DpiModeOption(
                    title = "Video Player'ı Göster",
                    description = "Launcher'da Video Player uygulamasını gizle/göster",
                    isSelected = videoPlayerVisible,
                    onClick = {
                        videoPlayerVisible = !videoPlayerVisible
                        SettingsManager.setVideoPlayerVisible(context, videoPlayerVisible)
                        onDpiChanged?.invoke()
                    }
                )
            }
        }
        
        // Launcher uygulamaları seçim ekranı
        if (showLauncherAppSelector) {
            LauncherAppSelectorScreen(
                title = "Launcher Uygulamaları",
                appsToConfigure = appConfig.apps,
                installedApps = installedApps,
                initialPackages = SettingsManager.getLauncherAppPackages(context),
                initialUseDefaultIcons = SettingsManager.getLauncherAppUseDefaultIcons(context),
                onBackClick = { showLauncherAppSelector = false },
                onSave = { packages, useDefaultIcons ->
                    SettingsManager.setLauncherAppPackages(context, packages)
                    SettingsManager.setLauncherAppUseDefaultIcons(context, useDefaultIcons)
                    showLauncherAppSelector = false
                    onDpiChanged?.invoke()
                }
            )
        }

        // Bottom bar uygulamaları seçim ekranı
        if (showBottomBarAppSelector) {
            LauncherAppSelectorScreen(
                title = "Bottom Bar Uygulamaları",
                appsToConfigure = appConfig.bottomBarApps,
                installedApps = installedApps,
                initialPackages = SettingsManager.getBottomBarPackages(context),
                initialUseDefaultIcons = SettingsManager.getBottomBarUseDefaultIcons(context),
                onBackClick = { showBottomBarAppSelector = false },
                onSave = { packages, useDefaultIcons ->
                    SettingsManager.setBottomBarPackages(context, packages)
                    SettingsManager.setBottomBarUseDefaultIcons(context, useDefaultIcons)
                    showBottomBarAppSelector = false
                    onDpiChanged?.invoke()
                }
            )
        }
    }
}

@Composable
fun LauncherAppSelectorScreen(
    title: String,
    appsToConfigure: List<com.emirbardakci.uconnectfake.ui.screens.UconnectApp>,
    installedApps: List<AppInfo>,
    initialPackages: List<String?>,
    initialUseDefaultIcons: List<Boolean>,
    onBackClick: () -> Unit,
    onSave: (List<String?>, List<Boolean>) -> Unit
) {
    val context = LocalContext.current
    
    var savedPackages by remember { 
        mutableStateOf(
            if (initialPackages.isEmpty()) {
                appsToConfigure.map { it.packageName }.toMutableList()
            } else {
                initialPackages.toMutableList()
            }
        )
    }
    
    var useDefaultIcons by remember {
        mutableStateOf(
            if (initialUseDefaultIcons.isEmpty()) {
                appsToConfigure.map { false }.toMutableList()
            } else {
                initialUseDefaultIcons.toMutableList()
            }
        )
    }
    
    // Eksik slot'ları null ile doldur
    LaunchedEffect(Unit) {
        while (savedPackages.size < appsToConfigure.size) {
            savedPackages.add(null)
        }
        while (useDefaultIcons.size < appsToConfigure.size) {
            useDefaultIcons.add(false)
        }
    }
    
    var selectedIndex by remember { mutableStateOf<Int?>(null) }
    
    // Yatay gradyan (kırmızı çizgi) için brush
    val horizontalGradientBrush = Brush.horizontalGradient(
        0f to Color.Transparent,
        0.3f to UconnectRedLine,
        0.5f to UconnectRedLine,
        0.7f to UconnectRedLine,
        1f to Color.Transparent
    )
    
    // Geri tuşu davranışı
    BackHandler(onBack = onBackClick)
    
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
                    text = title,
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
            
            // İçerik - LazyColumn kullanarak hem launcher app listesini hem de uygulama grid'ini göster
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                // Mevcut launcher uygulamaları listesi
                item {
                    Text(
                        text = "Mevcut Uygulamalar:",
                        color = UconnectTextWhite,
                        fontSize = 20.sp,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }
                
                items(appsToConfigure.size) { index ->
                    val app = appsToConfigure[index]
                    val currentPackage = savedPackages.getOrNull(index)
                    val currentApp = installedApps.find { it.packageName == currentPackage }
                    val defaultPackage = app.packageName
                    // Eğer bir paket seçilmişse ve varsayılan paketten farklıysa (veya varsayılan null ise) "Varsayılana Dön" göster
                    val hasCustomPackage = currentPackage != null && (defaultPackage == null || currentPackage != defaultPackage)
                    val useDefaultIcon = useDefaultIcons.getOrNull(index) ?: false
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedIndex = if (selectedIndex == index) null else index
                            }
                            .padding(vertical = 12.dp, horizontal = 8.dp)
                            .background(
                                color = if (selectedIndex == index) UconnectRedLine.copy(alpha = 0.2f) else Color.Transparent,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (currentApp != null && !useDefaultIcon) {
                            Image(
                                bitmap = currentApp.icon.toBitmap().asImageBitmap(),
                                contentDescription = currentApp.name,
                                modifier = Modifier.size(48.dp)
                            )
                        } else {
                            // Varsayılan ikon göster
                            Icon(
                                painter = painterResource(id = app.iconResId),
                                contentDescription = app.name,
                                tint = UconnectTextWhite,
                                modifier = Modifier.size(48.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "${index + 1}. ${app.name}",
                                color = UconnectTextWhite,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            if (currentApp != null && !useDefaultIcon) {
                                Text(
                                    text = currentApp.name,
                                    color = UconnectTextWhite.copy(alpha = 0.9f),
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = currentApp.packageName,
                                    color = UconnectTextWhite.copy(alpha = 0.7f),
                                    fontSize = 12.sp
                                )
                            } else {
                                Text(
                                    text = if (app.action != null) "Varsayılan (${app.action})" else if (currentApp != null) "Varsayılan ikon kullanılıyor" else "Paket seçilmedi",
                                    color = UconnectTextWhite.copy(alpha = 0.7f),
                                    fontSize = 12.sp
                                )
                            }
                        }
                        
                        // Varsayılan ikonu kullan checkbox'ı - sadece paket seçilmişse göster
                        if (currentPackage != null) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable {
                                    val newUseDefaultIcons = useDefaultIcons.toMutableList()
                                    newUseDefaultIcons[index] = !useDefaultIcon
                                    useDefaultIcons = newUseDefaultIcons
                                }
                            ) {
                                Checkbox(
                                    checked = useDefaultIcon,
                                    onCheckedChange = { checked ->
                                        val newUseDefaultIcons = useDefaultIcons.toMutableList()
                                        newUseDefaultIcons[index] = checked
                                        useDefaultIcons = newUseDefaultIcons
                                    },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = UconnectRedLine,
                                        uncheckedColor = UconnectTextWhite.copy(alpha = 0.7f)
                                    )
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Varsayılan ikon",
                                    color = UconnectTextWhite,
                                    fontSize = 12.sp
                                )
                            }
                        }
                        
                        // Varsayılana dön butonu - sadece özel paket seçilmişse göster
                        if (hasCustomPackage) {
                            Spacer(modifier = Modifier.width(8.dp))
                            TextButton(
                                onClick = {
                                    val newPackages = savedPackages.toMutableList()
                                    newPackages[index] = defaultPackage
                                    savedPackages = newPackages
                                }
                            ) {
                                Text(
                                    text = "Varsayılana Dön",
                                    color = UconnectRedLine,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
                
                // Telefon listesinden seçim
                if (selectedIndex != null) {
                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "Telefon Listesinden Seç:",
                            color = UconnectTextWhite,
                            fontSize = 20.sp,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }
                    
                    item {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(4),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 400.dp)
                        ) {
                            items(installedApps) { app ->
                                val currentSelectedIndex = selectedIndex
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp)
                                        .clickable {
                                            if (currentSelectedIndex != null) {
                                                val newPackages = savedPackages.toMutableList()
                                                newPackages[currentSelectedIndex] = app.packageName
                                                savedPackages = newPackages
                                                selectedIndex = null
                                            }
                                        }
                                ) {
                                    Image(
                                        bitmap = app.icon.toBitmap().asImageBitmap(),
                                        contentDescription = app.name,
                                        modifier = Modifier.size(56.dp)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = app.name,
                                        color = UconnectTextWhite,
                                        fontSize = 12.sp,
                                        textAlign = TextAlign.Center,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }
                    }
                }
            }
            
            // Alt butonlar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                TextButton(
                    onClick = onBackClick,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("İptal", color = UconnectTextWhite)
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                TextButton(
                    onClick = {
                        onSave(savedPackages, useDefaultIcons)
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Kaydet", color = UconnectRedLine)
                }
            }
        }
    }
}

@Composable
private fun DpiModeOption(
    title: String,
    description: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp)
            .background(
                color = if (isSelected) UconnectRedLine.copy(alpha = 0.3f) else Color.Transparent
            )
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    color = UconnectTextWhite,
                    fontSize = 20.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    color = UconnectTextWhite.copy(alpha = 0.7f),
                    fontSize = 14.sp
                )
            }
            
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(
                            color = UconnectRedLine,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "✓",
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

