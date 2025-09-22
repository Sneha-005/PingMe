package com.devsneha.pingme.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.devsneha.pingme.model.AuthUser
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun saveUser(user: AuthUser) {
        val userJson = gson.toJson(user)
        sharedPreferences.edit().putString("user_data", userJson).apply()
    }

    fun getUser(): AuthUser? {
        val userJson = sharedPreferences.getString("user_data", null)
        return if (userJson != null) {
            gson.fromJson(userJson, AuthUser::class.java)
        } else {
            null
        }
    }

    fun clearUser() {
        sharedPreferences.edit().remove("user_data").apply()
    }
} 