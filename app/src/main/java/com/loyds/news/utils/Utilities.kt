package com.loyds.news.utils

import android.app.KeyguardManager
import android.content.Context
import android.content.Context.KEYGUARD_SERVICE
import android.os.Build
import android.util.Log
import androidx.biometric.BiometricManager
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


object Utilities {

    fun String.formatDate(): String {
        var convertedDate = ""
        try {
            if (this.isNotEmpty() && this.contains("T")) {
                val locale = Locale("US")
                var format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", locale)
                val newDate: Date? = format.parse(this)

                format = SimpleDateFormat("MMM dd, yyyy hh:mm a", locale)
                newDate?.let {
                    convertedDate = format.format(it)
                }
            } else {
                convertedDate = this
            }
        } catch (e: Exception) {
            e.message?.let {
                Log.e("Utilities", it)
            }
            convertedDate = this
        }
        return convertedDate
    }
    fun isBiometricHardWareAvailable(con: Context): Boolean {
        var result = false
        val biometricManager = BiometricManager.from(con)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            when (biometricManager.canAuthenticate(BiometricManager.Authenticators.DEVICE_CREDENTIAL or BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
                BiometricManager.BIOMETRIC_SUCCESS -> result = true
                BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> result = false
                BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> result = false
                BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> result = false
                BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED ->
                    result = true

                BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED ->
                    result = true

                BiometricManager.BIOMETRIC_STATUS_UNKNOWN ->
                    result = false
            }
        } else {
            when (biometricManager.canAuthenticate()) {
                BiometricManager.BIOMETRIC_SUCCESS -> result = true
                BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> result = false
                BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> result = false
                BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> result = false
            }
        }
        return result
    }


    fun deviceHasPasswordPinLock(con: Context): Boolean {
        val keyManager = con.getSystemService(KEYGUARD_SERVICE) as KeyguardManager
        return keyManager.isKeyguardSecure
    }

    fun getStringArray(context: Context, resId: Int): List<String> {
        return context.resources.getStringArray(resId).toList()
    }
}