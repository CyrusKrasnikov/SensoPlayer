package com.tensorion.sensoplayer

import android.app.Application
import android.content.Context
import android.hardware.SensorManager
import androidx.lifecycle.*
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer

/**
 * Provides player as LiveData
 */
class MainViewModel(application: Application) : AndroidViewModel(application) {

    val playerLive = SensorLiveData(SensorProcessor(),
        getApplication<Application>().getSystemService(Context.SENSOR_SERVICE) as SensorManager)
    /**
     * Initializes a player from url and prepares it
     */
    {currentPosition: Long ->
        val app = getApplication<Application>()
        return@SensorLiveData ExoPlayer.Builder(app)
            .build()
            .also { exoPlayer ->
                val mediaItem = MediaItem.fromUri(app.getString(R.string.media_url_mp4))
                exoPlayer.setMediaItem(mediaItem)
                exoPlayer.playWhenReady = false
                exoPlayer.seekTo(0, currentPosition)
                exoPlayer.prepare()
            }
    }

}