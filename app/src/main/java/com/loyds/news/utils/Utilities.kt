package com.loyds.news.utils

import android.app.KeyguardManager
import android.content.Context
import android.content.Context.KEYGUARD_SERVICE
import android.os.Build
import android.util.Log
import androidx.biometric.BiometricManager
import java.io.UnsupportedEncodingException
import java.net.URLDecoder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.regex.Pattern


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

    private val HEX_PATTERN: Pattern = Pattern.compile("%[0-9A-Fa-f]{2}")

    fun safeDecode(data: String?): String? {
        var decodeData = data
        if (decodeData.isNullOrEmpty()) {
            return decodeData
        }

        // Replace invalid percent-encoded sequences
        decodeData = decodeData.replace("%(?![0-9A-Fa-f]{2})".toRegex(), "%25")
        val result = StringBuilder()

        // Iterate through each character in the URL
        var i = 0
        while (i < decodeData.length) {
            val ch = decodeData[i]
            if (ch == '%' && i + 2 < decodeData.length) {
                val hex = decodeData.substring(i, i + 3)
                if (HEX_PATTERN.matcher(hex).matches()) {
                    result.append(hex)
                    i += 2
                } else {
                    result.append("%25")
                }
            } else {
                result.append(ch)
            }
            i++
        }

        // Decode the sanitized URL
        return try {
            URLDecoder.decode(result.toString(), "UTF-8")
        } catch (e: UnsupportedEncodingException) {
            throw IllegalArgumentException("Failed to decode URL: Unsupported encoding", e)
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException(
                "Failed to decode URL: Illegal hex characters in escape (%) pattern",
                e
            )
        }
    }
}