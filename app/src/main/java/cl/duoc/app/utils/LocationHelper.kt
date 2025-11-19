package cl.duoc.app.utils

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.tasks.await

/**
 * Helper para gestionar ubicación GPS
 * Proporciona métodos para obtener coordenadas actuales
 */
class LocationHelper(private val context: Context) {
    
    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)
    
    /**
     * Verifica si los permisos de ubicación están otorgados
     */
    fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }
    
    /**
     * Verifica si el GPS está habilitado
     */
    fun isLocationEnabled(): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }
    
    /**
     * Obtiene la última ubicación conocida
     * Requiere permisos de ubicación
     */
    @SuppressLint("MissingPermission")
    suspend fun getLastKnownLocation(): Location? {
        if (!hasLocationPermission()) {
            return null
        }
        
        return try {
            fusedLocationClient.lastLocation.await()
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Obtiene la ubicación actual con alta precisión
     * Requiere permisos de ubicación
     */
    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): Location? {
        if (!hasLocationPermission()) {
            return null
        }
        
        return try {
            val cancellationTokenSource = CancellationTokenSource()
            fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                cancellationTokenSource.token
            ).await()
        } catch (e: Exception) {
            // Si falla la ubicación actual, intentar con la última conocida
            getLastKnownLocation()
        }
    }
    
    /**
     * Formatea las coordenadas como string
     * Formato: "latitud,longitud"
     */
    fun formatCoordinates(location: Location): String {
        return "${location.latitude},${location.longitude}"
    }
    
    /**
     * Formatea las coordenadas como string legible
     * Formato: "Lat: XX.XXXX, Lon: YY.YYYY"
     */
    fun formatCoordinatesReadable(location: Location): String {
        return "Lat: ${"%.4f".format(location.latitude)}, Lon: ${"%.4f".format(location.longitude)}"
    }
    
    /**
     * Parsea string de coordenadas a Pair<Double, Double>
     * Formato esperado: "latitud,longitud"
     */
    fun parseCoordinates(coordinates: String?): Pair<Double, Double>? {
        if (coordinates.isNullOrBlank()) return null
        
        return try {
            val parts = coordinates.split(",")
            if (parts.size == 2) {
                val lat = parts[0].toDouble()
                val lon = parts[1].toDouble()
                Pair(lat, lon)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Calcula la distancia entre dos ubicaciones en metros
     */
    fun calculateDistance(
        lat1: Double, lon1: Double,
        lat2: Double, lon2: Double
    ): Float {
        val results = FloatArray(1)
        Location.distanceBetween(lat1, lon1, lat2, lon2, results)
        return results[0]
    }
    
    companion object {
        const val LOCATION_PERMISSION_REQUEST_CODE = 1001
        
        /**
         * Permisos necesarios para ubicación
         */
        val REQUIRED_PERMISSIONS = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }
}
