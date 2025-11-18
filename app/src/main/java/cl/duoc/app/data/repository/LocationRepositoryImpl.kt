package cl.duoc.app.data.repository

import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.Tasks
import cl.duoc.app.data.local.SharedPreferencesManager
import cl.duoc.app.data.local.room.HealthCenterDao
import cl.duoc.app.data.local.room.toHealthCenterDomainList
import cl.duoc.app.domain.repository.LocationRepository
import cl.duoc.app.model.HealthCenter
import cl.duoc.app.model.LocationData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume

/**
 * Implementación del repositorio de ubicaciones
 * Gestiona la obtención de ubicaciones mediante GPS y centros de salud desde Room
 *
 * @property context Contexto de la aplicación
 * @property healthCenterDao DAO para operaciones de centros de salud
 * @property prefsManager Manager de SharedPreferences
 */
class LocationRepositoryImpl(
    private val context: Context,
    private val healthCenterDao: HealthCenterDao,
    private val prefsManager: SharedPreferencesManager
) : LocationRepository {

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    companion object {
        private const val LOCATION_PERMISSION = android.Manifest.permission.ACCESS_FINE_LOCATION
        private const val PREFS_LAST_LAT = "last_location_lat"
        private const val PREFS_LAST_LON = "last_location_lon"
    }

    override suspend fun getCurrentLocation(): LocationData? = withContext(Dispatchers.IO) {
        // Verificar permisos
        if (!hasLocationPermission()) {
            return@withContext null
        }

        return@withContext try {
            val location = getLastKnownLocation()
            if (location != null) {
                LocationData(
                    latitude = location.latitude,
                    longitude = location.longitude,
                    accuracy = location.accuracy,
                    timestamp = System.currentTimeMillis()
                )
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override suspend fun getHealthCenterLocation(): HealthCenter? = withContext(Dispatchers.IO) {
        return@withContext try {
            healthCenterDao.getAllHealthCenters().toHealthCenterDomainList().firstOrNull()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override suspend fun getAllHealthCenters(): List<HealthCenter> = withContext(Dispatchers.IO) {
        return@withContext try {
            healthCenterDao.getAllHealthCenters().toHealthCenterDomainList()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    override fun observeHealthCenters(): Flow<List<HealthCenter>> {
        return kotlinx.coroutines.flow.flow {
            try {
                healthCenterDao.observeHealthCenters().collect { entities ->
                    emit(entities.toHealthCenterDomainList())
                }
            } catch (e: Exception) {
                e.printStackTrace()
                emit(emptyList())
            }
        }
    }

    override suspend fun saveUserLocation(userId: String, location: LocationData): Boolean =
        withContext(Dispatchers.IO) {
        return@withContext try {
            prefsManager.saveString("${userId}_last_lat", location.latitude.toString())
            prefsManager.saveString("${userId}_last_lon", location.longitude.toString())
            prefsManager.saveString("${userId}_last_location_time", location.timestamp.toString())
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override suspend fun getUserLastLocation(userId: String): LocationData? =
        withContext(Dispatchers.IO) {
        return@withContext try {
            val lat = prefsManager.getString("${userId}_last_lat", "").toDoubleOrNull()
            val lon = prefsManager.getString("${userId}_last_lon", "").toDoubleOrNull()
            val time = prefsManager.getString("${userId}_last_location_time", "0").toLongOrNull() ?: 0

            if (lat != null && lon != null) {
                LocationData(
                    latitude = lat,
                    longitude = lon,
                    accuracy = 0f,
                    timestamp = time
                )
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Obtiene la última ubicación conocida
     */
    @Suppress("MissingPermission")
    private suspend fun getLastKnownLocation(): Location? = suspendCancellableCoroutine { continuation ->
        if (!hasLocationPermission()) {
            continuation.resume(null)
            return@suspendCancellableCoroutine
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            continuation.resume(location)
        }.addOnFailureListener {
            continuation.resume(null)
        }
    }

    /**
     * Verifica si se tienen permisos de ubicación
     */
    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            LOCATION_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED
    }
}

