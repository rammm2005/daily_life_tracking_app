import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "user_session")

class SessionManager(private val context: Context) {

    companion object {
        val KEY_EMAIL = stringPreferencesKey("email")
        val KEY_ROLE = stringPreferencesKey("role")
    }

    suspend fun saveUserSession(email: String, role: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_EMAIL] = email
            preferences[KEY_ROLE] = role
        }
    }

    val userEmail: Flow<String?> = context.dataStore.data.map { it[KEY_EMAIL] }
    val userRole: Flow<String?> = context.dataStore.data.map { it[KEY_ROLE] }

    suspend fun clearSession() {
        context.dataStore.edit { it.clear() }
    }
}
