package com.tensorion.sensoplayer

import android.hardware.*
import android.util.Log
import androidx.media3.common.Player
import kotlin.math.sqrt

/**
 * Processes sensor events and controls player if any.
 * This class enables testing of player response to sensor events independently of other features
 */
class SensorProcessor: ISensorProcessor {

    override var player:Player? = null
    private var lastTimeMillis = 0L

    /**
     *  Changes volume, seeks and pauses video player according to type of the sensor
     */
    override fun process(event: SensorEvent) {
        if (player == null) return

        when (event.sensor.type) {
            Sensor.TYPE_GYROSCOPE -> processGyroscope(event.values)
            Sensor.TYPE_ACCELEROMETER -> processAccelerometer(event.values)
        }
    }

    private fun processAccelerometer(vector: FloatArray) {
        val currentTimeMillis = System.currentTimeMillis()

        if (currentTimeMillis - lastTimeMillis > Constants.SHAKE_INTERVAL) { // throttling
            lastTimeMillis = currentTimeMillis

            val x = vector[0]
            val y = vector[1]
            val z = vector[2]

            val gForce = sqrt(x * x + y * y + z * z) / SensorManager.GRAVITY_EARTH

            if (gForce > Constants.SHAKE_THRESHOLD_G) { Log.d("sensor.shake", "force: $gForce")
                player!!.pause()
            }
        }
    }

    private fun processGyroscope(vector: FloatArray) {
        player!!.volume += vector[0] * Constants.ROTATE_X_COEFFICIENT

        if (vector[2] != 0f) { Log.d("sensor.rotateZ", "rad/s: ${vector[2]}")
            val positionMS =
                (player!!.currentPosition + vector[2] * Constants.ROTATE_Z_COEFFICIENT).toLong()
            player!!.seekTo(positionMS)
        }
    }
}