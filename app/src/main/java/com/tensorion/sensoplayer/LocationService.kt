package com.tensorion.sensoplayer

import android.app.Service
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.IBinder
import com.tensorion.sensoplayer.Constants.LOCATION_MINIMUM_DISTANCE_BETWEEN_UPDATES_M
import com.tensorion.sensoplayer.Constants.LOCATION_MINIMUM_TIME_BETWEEN_UPDATES_MS

/**
 * Starts location updates based on GPS and WIFI
 * It may be necessary to cancel updates when activity stops (app in background)
 */
class LocationService : Service(), LocationListener {

    private var location: Location? = null

    override fun onLocationChanged(location: Location) {
        if (locationChanged != null)
            locationChanged!!(location)
    }

    override fun onProviderDisabled(provider: String) {}
    override fun onProviderEnabled(provider: String) {}
    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
    override fun onBind(arg0: Intent): IBinder? {
        return null
    }

    var locationChanged: ((Location) -> Unit)? = null
    @Throws(SecurityException::class)
    fun startLocationUpdates(locationManager: LocationManager) {
        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
            locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                LOCATION_MINIMUM_TIME_BETWEEN_UPDATES_MS,
                LOCATION_MINIMUM_DISTANCE_BETWEEN_UPDATES_M, this
            )
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            if (location == null) {
                locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    LOCATION_MINIMUM_TIME_BETWEEN_UPDATES_MS,
                    LOCATION_MINIMUM_DISTANCE_BETWEEN_UPDATES_M, this
                )
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            }
    }
}