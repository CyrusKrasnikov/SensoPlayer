package com.tensorion.sensoplayer

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.media3.common.Player

/**
 * Directs sensor events to processor with player control according to lifecycle.
 *
 * @param buildPlayer called when new player instance is needed
 */
class SensorLiveData(
    private val sensorProcessor:ISensorProcessor,
    private val sensorManager:SensorManager,
    private val buildPlayer: (Long) -> Player)
    : LiveData<Player>(), SensorEventListener
{
    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}

    override fun onSensorChanged(event: SensorEvent) {
        Log.d("sensor.values", event.sensor.type.toString() + " = " + event.values.toString())

        sensorProcessor.process(event)
    }

    private var currentPosition = 0L

    override fun onActive() {
        sensorManager.let { sm ->
            sm.getDefaultSensor(Sensor.TYPE_GYROSCOPE).let {
                sm.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
            }
        }
        sensorManager.let { sm ->
            sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER).let {
                sm.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
            }
        }

        value=buildPlayer(currentPosition)
        sensorProcessor.player = value
    }

    override fun onInactive() {
        sensorManager.unregisterListener(this)

        releasePlayer()
    }

    /**
     * Frees resources used by player
     */
    private fun releasePlayer() {
        value?.let { it ->
            currentPosition = it.currentPosition
            it.release()
        }
        value = null
        sensorProcessor.player = null
    }

}