package cl.duoc.app.ui.screens.map

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng

// Stub para CameraUpdateFactory si no está disponible directamente
object CameraUpdateFactoryCompat {
    fun newLatLng(latLng: LatLng) = CameraUpdateFactory.newLatLng(latLng)
    fun newLatLngZoom(latLng: LatLng, zoom: Float) = CameraUpdateFactory.newLatLngZoom(latLng, zoom)
}

