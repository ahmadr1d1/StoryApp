package com.ahmadrd.storyapp.data.local.pref

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.ahmadrd.storyapp.data.remote.response.auth.LoginResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.Locale

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class UserPreference private constructor(private val dataStore: DataStore<Preferences>) {

    suspend fun saveSession(user: LoginResult) {
        dataStore.edit { preferences ->
            preferences[USER_ID] = user.userId
            preferences[USER_NAME] = user.name
            preferences[TOKEN] = user.token
            preferences[IS_LOGIN] = true
        }
    }

    // Menyimpan session untuk status login
    fun isUserLoggedIn(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[IS_LOGIN] ?: false
        }
    }

    suspend fun getToken(): String {
        return dataStore.data.map { preferences ->
            preferences[TOKEN] ?: ""
        }.first()
    }

    suspend fun logout() {
        dataStore.edit { preferences ->
            preferences.remove(USER_ID) // Hapus userId saat logout
            preferences.remove(USER_NAME)
            preferences.remove(TOKEN)
            preferences.remove(IS_LOGIN)
        }
    }

    suspend fun setDarkMode(enabled: Boolean) {
        dataStore.edit { it[DARK_MODE] = enabled }
    }

    fun getDarkMode(): Flow<Boolean> = dataStore.data.map { it[DARK_MODE] ?: false }

    suspend fun setLanguage(code: String) {
        dataStore.edit { it[LANGUAGE] = code }
    }

    fun getLanguage(): Flow<String> = dataStore.data.map { it[LANGUAGE] ?: Locale.getDefault().language }

    companion object {
        @Volatile
        private var INSTANCE: UserPreference? = null

        private val USER_ID = stringPreferencesKey("user_id")
        private val USER_NAME = stringPreferencesKey("user_name")
        private val TOKEN = stringPreferencesKey("token")
        private val IS_LOGIN = booleanPreferencesKey("is_login")
        private val DARK_MODE = booleanPreferencesKey("dark_mode")
        private val LANGUAGE = stringPreferencesKey("language")

        fun getInstance(dataStore: DataStore<Preferences>): UserPreference {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: UserPreference(dataStore).also { INSTANCE = it }
            }
        }
    }
}