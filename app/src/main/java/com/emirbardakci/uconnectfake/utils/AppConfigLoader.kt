package com.emirbardakci.uconnectfake.utils

import android.content.Context
import android.net.Uri
import com.emirbardakci.uconnectfake.R
import com.emirbardakci.uconnectfake.ui.screens.UconnectApp
import com.emirbardakci.uconnectfake.ui.theme.UconnectTextWhite
import org.json.JSONArray
import org.json.JSONObject
import java.io.InputStream

data class AppConfig(
    val apps: List<UconnectApp>,
    val bottomBarApps: List<UconnectApp>
)

fun loadAppConfig(context: Context): AppConfig {
    val inputStream: InputStream = context.resources.openRawResource(R.raw.apps_config)
    val jsonString = inputStream.bufferedReader().use { it.readText() }
    val jsonObject = JSONObject(jsonString)
    
    val appsArray = jsonObject.getJSONArray("apps")
    val apps = mutableListOf<UconnectApp>()
    for (i in 0 until appsArray.length()) {
        val appObj = appsArray.getJSONObject(i)
        apps.add(parseApp(appObj))
    }
    
    val bottomBarArray = jsonObject.getJSONArray("bottomBarApps")
    val bottomBarApps = mutableListOf<UconnectApp>()
    for (i in 0 until bottomBarArray.length()) {
        val appObj = bottomBarArray.getJSONObject(i)
        bottomBarApps.add(parseApp(appObj))
    }
    
    return AppConfig(apps, bottomBarApps)
}

private fun parseApp(appObj: JSONObject): UconnectApp {
    val name = appObj.getString("name")
    val iconResIdName = appObj.getString("iconResId")
    val iconResId = getDrawableResourceId(iconResIdName)
    val packageName = if (appObj.isNull("packageName")) null else appObj.getString("packageName")
    val action = if (appObj.isNull("action")) null else appObj.getString("action")
    val data = if (appObj.isNull("data")) null else Uri.parse(appObj.getString("data"))
    
    return UconnectApp(
        name = name,
        iconResId = iconResId,
        packageName = packageName,
        action = action,
        data = data,
        tint = UconnectTextWhite
    )
}

private fun getDrawableResourceId(name: String): Int {
    return when (name) {
        "ic_radio" -> com.emirbardakci.uconnectfake.R.drawable.ic_radio
        "ic_media" -> com.emirbardakci.uconnectfake.R.drawable.ic_media
        "ic_camera" -> com.emirbardakci.uconnectfake.R.drawable.ic_camera
        "ic_carplay" -> com.emirbardakci.uconnectfake.R.drawable.ic_carplay
        "ic_android_auto" -> com.emirbardakci.uconnectfake.R.drawable.ic_android_auto
        "ic_browser" -> com.emirbardakci.uconnectfake.R.drawable.ic_browser
        "ic_trip" -> com.emirbardakci.uconnectfake.R.drawable.ic_trip
        "ic_climate" -> com.emirbardakci.uconnectfake.R.drawable.ic_climate
        "ic_app" -> com.emirbardakci.uconnectfake.R.drawable.ic_app
        "ic_settings" -> com.emirbardakci.uconnectfake.R.drawable.ic_settings
        "ic_controls" -> com.emirbardakci.uconnectfake.R.drawable.ic_controls
        "ic_car" -> com.emirbardakci.uconnectfake.R.drawable.ic_car
        "ic_video" -> com.emirbardakci.uconnectfake.R.drawable.ic_video
        "ic_eq" -> com.emirbardakci.uconnectfake.R.drawable.ic_eq
        "ic_nav" -> com.emirbardakci.uconnectfake.R.drawable.ic_nav
        "ic_uconnect" -> com.emirbardakci.uconnectfake.R.drawable.ic_uconnect
        "ic_phone" -> com.emirbardakci.uconnectfake.R.drawable.ic_phone
        else -> com.emirbardakci.uconnectfake.R.drawable.ic_app
    }
}

