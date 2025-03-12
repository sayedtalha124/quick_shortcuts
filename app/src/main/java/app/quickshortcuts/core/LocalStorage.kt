package app.quickshortcuts.core

import ContactInfo
import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import app.quickshortcuts.core.Utils.showLog
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.IOException

val Context.dataStore by preferencesDataStore(name = "settings")
val isSpeedDialAdded = booleanPreferencesKey("isSpeedDialAdded")
val speedDialContacts = stringPreferencesKey("speedDialContacts")


fun getPreference(context: Context): Flow<Boolean> = context.dataStore.data.catch { exception ->
    if (exception is IOException) {
        emit(emptyPreferences())
    } else {
        throw exception
    }
}.map { preferences ->
    val result = preferences[isSpeedDialAdded] ?: false
    result
}

suspend fun onDeleteContact(contact: ContactInfo, context: Context, list: List<ContactInfo>) {
    var list2  = list - contact

    context.dataStore.edit { settings ->
        settings[speedDialContacts] = Json.encodeToString<List<ContactInfo>>(list2 )
    }

}

suspend fun clearUserPreference(context: Context) {
    context.dataStore.edit { settings ->
        settings[speedDialContacts] = Json.encodeToString<ArrayList<ContactInfo>>(arrayListOf())
    }

}

fun getUserPreference(context: Context): Flow<List<ContactInfo>> {
    return context.dataStore.data.catch { exception ->
        if (exception is IOException) {
            emit(emptyPreferences())
        } else {
            throw exception
        }
    }.map { preferences ->
        val result: String? = preferences[speedDialContacts] ?: ""
        showLog("result $result")
        if (result.isNullOrEmpty()) {
            arrayListOf()

        } else {
            Json.decodeFromString<ArrayList<ContactInfo>>(result).toImmutableList()
        }
    }
}

suspend fun saveContactDetails(context: Context, list: List<ContactInfo>) {
    val currentList = list
    val newList = ArrayList<ContactInfo>(currentList)
    context.dataStore.edit { settings ->
        settings[speedDialContacts] = Json.encodeToString(newList)
    }

}

suspend fun setSpeedDial(context: Context, speedDial: Boolean) {

    context.dataStore.edit { settings ->
        settings[isSpeedDialAdded] = speedDial
    }
}