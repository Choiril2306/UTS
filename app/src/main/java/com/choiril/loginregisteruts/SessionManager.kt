package com.choiril.loginregisteruts

import android.content.Context
import android.content.SharedPreferences

object SessionManager {
    private var sharedPreferences: SharedPreferences? = null
    private var editor: SharedPreferences.Editor? = null

    private const val PREF_NAME = "user_pref"
    private const val IS_LOGGED_IN = "is_logged_in"
    private const val USER_EMAIL = "user_email"

    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        editor = sharedPreferences?.edit()
    }

    fun setLoginState(isLoggedIn: Boolean, email: String? = null) {
        editor?.putBoolean(IS_LOGGED_IN, isLoggedIn)
        email?.let { editor?.putString(USER_EMAIL, it) }
        editor?.apply()
    }

    fun getLoginState(): Boolean {
        return sharedPreferences?.getBoolean(IS_LOGGED_IN, false) ?: false
    }

    fun getUserEmail(): String? {
        return sharedPreferences?.getString(USER_EMAIL, null)
    }
}
