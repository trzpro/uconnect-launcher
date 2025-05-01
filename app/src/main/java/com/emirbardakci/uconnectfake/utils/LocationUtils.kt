package com.emirbardakci.uconnectfake.utils

class LocationUtils {
    companion object {
        fun formatSpeed(speed: Float): String {
            return speed.toInt().toString() // Ondalık kısmı kaldırıldı
        }
    }
} 