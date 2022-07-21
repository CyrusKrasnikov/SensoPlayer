package com.tensorion.sensoplayer

import android.hardware.SensorEvent
import androidx.media3.common.Player

/**
 * Contract to process sensor events and control video player
 */
interface ISensorProcessor{
    var player: Player?
    fun process(event: SensorEvent)
}