package mx.edu.utez.fitlife.data.local

import android.content.Context
import android.content.SharedPreferences
import android.provider.Settings
import java.util.UUID

class PreferencesManager(private val context: Context) {
    private val prefs: SharedPreferences = 
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    
    companion object {
        private const val PREFS_NAME = "FitLifePrefs"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_TOKEN = "token"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_DEVICE_ID = "device_id"
        private const val KEY_DAILY_GOAL = "daily_goal"
        private const val KEY_THEME_MODE = "theme_mode"
    }
    
    fun saveUser(userId: Int, name: String, email: String, token: String) {
        prefs.edit().apply {
            putInt(KEY_USER_ID, userId)
            putString(KEY_USER_NAME, name)
            putString(KEY_USER_EMAIL, email)
            putString(KEY_TOKEN, token)
            putBoolean(KEY_IS_LOGGED_IN, true)
            apply()
        }
    }
    
    fun getUserName(): String? = prefs.getString(KEY_USER_NAME, null)
    fun getUserEmail(): String? = prefs.getString(KEY_USER_EMAIL, null)
    fun getUserId(): Int = prefs.getInt(KEY_USER_ID, -1)
    fun getToken(): String? = prefs.getString(KEY_TOKEN, null)
    fun isLoggedIn(): Boolean = prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    
    /**
     * Obtiene un ID único del dispositivo.
     * Combina el Android ID con un UUID generado para mayor unicidad.
     */
    fun getDeviceId(): String {
        var deviceId = prefs.getString(KEY_DEVICE_ID, null)
        
        if (deviceId == null) {
            // Generar un ID único combinando Android ID y UUID
            val androidId = Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ANDROID_ID
            ) ?: ""
            
            val uuid = UUID.randomUUID().toString()
            deviceId = "$androidId-$uuid"
            
            // Guardar para uso futuro
            prefs.edit().putString(KEY_DEVICE_ID, deviceId).apply()
        }
        
        return deviceId
    }
    
    fun getDailyGoal(): Int = prefs.getInt(KEY_DAILY_GOAL, 6000)
    
    fun setDailyGoal(steps: Int) {
        prefs.edit().putInt(KEY_DAILY_GOAL, steps).apply()
    }
    
    fun getThemeMode(): String = prefs.getString(KEY_THEME_MODE, "system") ?: "system"
    
    fun setThemeMode(mode: String) {
        prefs.edit().putString(KEY_THEME_MODE, mode).apply()
    }
    
    fun clearSession() {
        val deviceId = getDeviceId() // Preservar device ID
        prefs.edit().clear().apply()
        prefs.edit().putString(KEY_DEVICE_ID, deviceId).apply()
    }
}
