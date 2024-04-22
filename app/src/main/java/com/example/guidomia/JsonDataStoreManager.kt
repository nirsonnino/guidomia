package com.example.guidomia

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "json_data_store")

class JsonDataStoreManager(private val context: Context) {

    private val dataStore = context.dataStore

    suspend fun saveJsonData(jsonData: String) {
        dataStore.edit { preferences ->
            preferences[STRING_JSON_KEY] = jsonData
        }
    }

    fun getJsonData(): Flow<String?> {
        return dataStore.data.map { preferences ->
            preferences[STRING_JSON_KEY]
        }
    }

    companion object {
        private val STRING_JSON_KEY = stringPreferencesKey("json_data_key")
    }
}
