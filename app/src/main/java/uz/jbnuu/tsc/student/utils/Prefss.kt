package uz.jbnuu.tsc.student.utils

import android.content.Context
import android.content.SharedPreferences
import javax.inject.Inject


class Prefs @Inject constructor(context: Context) {

    private val prefsName: String = "BonusDodasi"
    private var prefs: SharedPreferences =
        context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)


    val language = "language"
    val token = "token"
    val login = "login"
    val semester = "semester"
    val image = "image"
    val group = "group"
    val firebaseToken = "firebaseToken"
    val error_code = "error_code"
    val error = "error"
    val password = "password"
//    val tutorLogin = "tutorLogin"
//    val tutorPassword = "tutorPassword"
    val hemisToken = "hemisToken"
    val fam = "fam"
    val name = "name"
    val loginStop = "loginStop"
//    val role = "role" // 7-Admin 2-Tyutor 3-Developer 4-Student




    fun save(key: String, value: Int) {
        prefs.edit().putInt(key, value).apply()
    }

    fun save(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }

    fun save(key: String, value: Float) {
        prefs.edit().putFloat(key, value).apply()
    }

    fun save(key: String, value: Boolean) {
        prefs.edit().putBoolean(key, value).apply()
    }

    fun save(key: String, value: Long) {
        prefs.edit().putLong(key, value).apply()
    }

    fun get(key: String, defValue: Int) = prefs.getInt(key, defValue)

    fun get(key: String, defValue: String) = prefs.getString(key, defValue) ?: ("" + defValue)

    fun get(key: String, defValue: Float) = prefs.getFloat(key, defValue)

    fun get(key: String, defValue: Boolean) = prefs.getBoolean(key, defValue)

    fun get(key: String, defValue: Long) = prefs.getLong(key, defValue)

    fun clear() {
        prefs.edit().clear().apply()
    }
}