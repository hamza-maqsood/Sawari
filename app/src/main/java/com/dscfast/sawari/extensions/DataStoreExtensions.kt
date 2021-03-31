package com.dscfast.sawari.extensions

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

fun <T> DataStore<Preferences>.getValueFlow(
    key: Preferences.Key<T>,
    defaultValue: T
): Flow<T> {
    return this.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            preferences[key] ?: defaultValue
        }
}

suspend fun <T> DataStore<Preferences>.setValue(key: Preferences.Key<T>, value: T) {
    this.edit { preferences ->
        preferences[key] = value
    }
}

fun String.toStringPreferenceKey(): Preferences.Key<String> = stringPreferencesKey(this)

fun String.toBooleanPreferenceKey(): Preferences.Key<Boolean> = booleanPreferencesKey(this)

fun String.toLongPreferenceKey(): Preferences.Key<Long> = longPreferencesKey(this)

fun String.toIntPreferenceKey(): Preferences.Key<Int> = intPreferencesKey(this)