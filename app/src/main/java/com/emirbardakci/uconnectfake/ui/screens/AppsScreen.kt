package com.emirbardakci.uconnectfake.ui.screens

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import com.emirbardakci.uconnectfake.MainActivity
import com.emirbardakci.uconnectfake.R
import com.emirbardakci.uconnectfake.ui.theme.UconnectRedLine
import com.emirbardakci.uconnectfake.ui.theme.UconnectTextWhite

data class AppInfo(
    val name: String,
    val packageName: String,
    val icon: Drawable
)

@Composable
fun AppsScreen(onBackClick: () -> Unit) {
    val context = LocalContext.current
    var apps by remember { mutableStateOf(listOf<AppInfo>()) }
    
    // Uygulamaları yükle
    LaunchedEffect(Unit) {
        apps = getInstalledApps(context)
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
                    text = "Apps",
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
                    .background(brush = Brush.horizontalGradient(
                        0f to Color.Transparent,
                        0.3f to UconnectRedLine,
                        0.5f to UconnectRedLine,
                        0.7f to UconnectRedLine,
                        1f to Color.Transparent
                    ))
            )
            
            // Apps grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                contentPadding = PaddingValues(horizontal = 15.dp, vertical = 16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                items(apps) { app ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(15.dp)
                            .clickable {
                                val launchIntent = context.packageManager.getLaunchIntentForPackage(app.packageName)
                                if (launchIntent != null) {
                                    launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    context.startActivity(launchIntent)
                                }
                            }
                    ) {
                        Image(
                            bitmap = app.icon.toBitmap().asImageBitmap(),
                            contentDescription = app.name,
                            modifier = Modifier.size(72.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = app.name,
                            color = UconnectTextWhite,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
            
            // Bottom bar
            UconnectBottomBar(
                onAppClick = { app ->
                    when {
                        app.action == "open_speed_screen" -> {
                            // Speed ekranına git
                            val intent = Intent(context, MainActivity::class.java).apply {
                                putExtra("screen", "speed")
                                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            }
                            context.startActivity(intent)
                        }
                        app.action == "open_apps_screen" -> {
                            // Zaten apps ekranındayız, bir şey yapma
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

@Composable
fun AppItem(appInfo: AppInfo, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(16.dp)
            .clickable { onClick() }
    ) {
        // İkon
        Image(
            bitmap = appInfo.icon.toBitmap().asImageBitmap(),
            contentDescription = appInfo.name,
            modifier = Modifier.size(48.dp)
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Uygulama adı
        Text(
            text = appInfo.name,
            color = UconnectTextWhite,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.width(100.dp)
        )
    }
}

// Cihazda yüklü uygulamaları getir
fun getInstalledApps(context: Context): List<AppInfo> {
    val packageManager = context.packageManager
    val apps = mutableListOf<AppInfo>()
    
    val packages = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
    
    for (packageInfo in packages) {
        val appName = packageManager.getApplicationLabel(packageInfo).toString()
        val packageName = packageInfo.packageName
        val icon = packageManager.getApplicationIcon(packageInfo)
        
        // Başlatılabilir uygulamaları kontrol et
        val launchIntent = packageManager.getLaunchIntentForPackage(packageName)
        if (launchIntent != null) {
            apps.add(AppInfo(appName, packageName, icon))
        }
    }
    
    // İsme göre sırala
    return apps.sortedBy { it.name }
} 