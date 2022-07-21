package com.tensorion.sensoplayer

import android.hardware.Sensor
import android.hardware.SensorEvent
import androidx.media3.common.Player
import androidx.media3.common.util.Log
import org.junit.Test
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

/**
 * Unit tests sensor processor verification
 */
class SensorUnitTest {
    @Test
    fun volume_isCorrect() {
        val event = mockEvent(arrayOf(1f,1f,1f).toFloatArray(),Sensor.TYPE_GYROSCOPE)
        val player:Player = mockPlayer()

        val sensorProcessor:ISensorProcessor = SensorProcessor()
        sensorProcessor.player = player

        player.volume = 0f
        sensorProcessor.process(event)
        verify(player).volume = 1f*Constants.ROTATE_X_COEFFICIENT
    }

    private fun mockPlayer(): Player {
        return mock {
            on {seekTo(5)} doAnswer { Log.d("seekTo","5")}
        }
    }

    private fun mockEvent(values: FloatArray, sensorType:Int): SensorEvent {
        val sensorEvent = mock<SensorEvent> {}

        sensorEvent.sensor = mock {}
        whenever(sensorEvent.sensor.type).thenReturn(sensorType)

        // values is final property       whenever(sensorEvent.values).thenReturn(values)
        val valuesField = SensorEvent::class.java.getField("values")
        valuesField.isAccessible = true
        try {
            valuesField.set(sensorEvent,values)
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }

        return sensorEvent
    }

}