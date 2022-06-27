package com.tensorion.sensoplayer

/**
 * Constants to calibrate sensors
 */
object Constants {
    const val LAUNCH_VIDEO_DELAY_MS = 4000L
    const val SHAKE_THRESHOLD_G = 1.1f
    const val SHAKE_INTERVAL = 200

    const val ROTATE_Z_COEFFICIENT = 1000
    const val ROTATE_X_COEFFICIENT = 1

    const val LOCATION_MINIMUM_DISTANCE_BETWEEN_UPDATES_M = 10f // meters
    const val LOCATION_MINIMUM_TIME_BETWEEN_UPDATES_MS = 1000L * 3 // seconds
}