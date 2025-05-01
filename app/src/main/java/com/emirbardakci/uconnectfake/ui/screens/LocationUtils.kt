package com.emirbardakci.uconnectfake.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.core.app.ActivityCompat
import java.text.DecimalFormat

// Hızı formatlama
fun formatSpeed(speed: Float): String {
    val df = DecimalFormat("#.#")
    return df.format(speed)
}

// Konum izinlerini kontrol et
fun checkLocationPermission(context: Context): Boolean {
    return ActivityCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
}

// Konum güncellemelerini başlat
fun startLocationUpdates(context: Context, onSpeedUpdate: (Float) -> Unit) {
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    
    val locationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            // GPS'ten hızı al (m/s olarak gelir, km/h'e çevir)
            val speedInKmh = if (location.hasSpeed()) {
                location.speed * 3.6f  // m/s -> km/h dönüşümü (3.6 ile çarpılır)
            } else {
                0.0f
            }
            
            onSpeedUpdate(speedInKmh)
        }
        
        @Deprecated("Deprecated in Java")
        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
        
        override fun onProviderEnabled(provider: String) {}
        
        override fun onProviderDisabled(provider: String) {}
    }
    
    try {
        // GPS sağlayıcı varsa kullan
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                1000,  // 1 saniyede bir
                0f,     // minimum mesafe değişimi (metre)
                locationListener
            )
        } else {
            // GPS kapalıysa ağ bazlı konumu kullan
            locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                1000,
                0f,
                locationListener
            )
        }
    } catch (e: SecurityException) {
        // İzin hatası
        e.printStackTrace()
    }
} 