package cl.duoc.app.data.local

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Manager para gestionar almacenamiento local usando SharedPreferences
 * Proporciona métodos genéricos para guardar y recuperar datos
 */
class SharedPreferencesManager(context: Context) {
    
    private val sharedPreferences: SharedPreferences = 
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    
    private val gson = Gson()
    
    companion object {
        private const val PREFS_NAME = "vital_care_prefs"
        
        // Keys
        const val KEY_USER_ID = "user_id"
        const val KEY_USER_NAME = "user_name"
        const val KEY_USER_EMAIL = "user_email"
        const val KEY_USER_PHONE = "user_phone"
        const val KEY_USERS_LIST = "users_list"
        const val KEY_RESERVATIONS_LIST = "reservations_list"
        const val KEY_VITAL_SIGNS_LIST = "vital_signs_list"
        const val KEY_IS_LOGGED_IN = "is_logged_in"
    }
    
    /**
     * Guarda un String
     */
    fun saveString(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).apply()
    }
    
    /**
     * Obtiene un String
     */
    fun getString(key: String, defaultValue: String = ""): String {
        return sharedPreferences.getString(key, defaultValue) ?: defaultValue
    }
    
    /**
     * Guarda un Int
     */
    fun saveInt(key: String, value: Int) {
        sharedPreferences.edit().putInt(key, value).apply()
    }
    
    /**
     * Obtiene un Int
     */
    fun getInt(key: String, defaultValue: Int = 0): Int {
        return sharedPreferences.getInt(key, defaultValue)
    }
    
    /**
     * Guarda un Boolean
     */
    fun saveBoolean(key: String, value: Boolean) {
        sharedPreferences.edit().putBoolean(key, value).apply()
    }
    
    /**
     * Obtiene un Boolean
     */
    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean {
        return sharedPreferences.getBoolean(key, defaultValue)
    }
    
    /**
     * Guarda un Long
     */
    fun saveLong(key: String, value: Long) {
        sharedPreferences.edit().putLong(key, value).apply()
    }
    
    /**
     * Obtiene un Long
     */
    fun getLong(key: String, defaultValue: Long = 0L): Long {
        return sharedPreferences.getLong(key, defaultValue)
    }
    
    /**
     * Guarda un objeto serializado a JSON
     */
    fun <T> saveObject(key: String, obj: T) {
        val json = gson.toJson(obj)
        saveString(key, json)
    }
    
    /**
     * Obtiene un objeto deserializado desde JSON
     */
    inline fun <reified T> getObject(key: String): T? {
        val json = getString(key)
        return if (json.isEmpty()) {
            null
        } else {
            try {
                gson.fromJson(json, T::class.java)
            } catch (e: Exception) {
                null
            }
        }
    }
    
    /**
     * Guarda una lista de objetos
     */
    fun <T> saveList(key: String, list: List<T>) {
        val json = gson.toJson(list)
        saveString(key, json)
    }
    
    /**
     * Obtiene una lista de objetos
     */
    inline fun <reified T> getList(key: String): List<T> {
        val json = getString(key)
        return if (json.isEmpty()) {
            emptyList()
        } else {
            try {
                val type = object : TypeToken<List<T>>() {}.type
                gson.fromJson(json, type) ?: emptyList()
            } catch (e: Exception) {
                emptyList()
            }
        }
    }
    
    /**
     * Elimina un valor por key
     */
    fun remove(key: String) {
        sharedPreferences.edit().remove(key).apply()
    }
    
    /**
     * Limpia todos los datos
     */
    fun clear() {
        sharedPreferences.edit().clear().apply()
    }
    
    /**
     * Verifica si existe una key
     */
    fun contains(key: String): Boolean {
        return sharedPreferences.contains(key)
    }
}
