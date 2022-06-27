package com.tensorion.sensoplayer

import android.hardware.SensorEvent
import androidx.media3.common.Player

/**
 * Declares function and property
 */
interface ISensorProcessor{
    var player: Player?
    fun process(event: SensorEvent)
}