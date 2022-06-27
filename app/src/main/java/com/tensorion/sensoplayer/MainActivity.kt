package com.tensorion.sensoplayer

import android.Manifest
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.*
import android.util.Log
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.view.*
import androidx.lifecycle.*
import com.tensorion.sensoplayer.Constants.LAUNCH_VIDEO_DELAY_MS
import com.tensorion.sensoplayer.databinding.ActivityMainBinding
import kotlinx.coroutines.delay

/**
 *  Full-screen activity with a video player controlled by shaking, rotating and moving the device
 */
class MainActivity : AppCompatActivity() {

    private lateinit var locationService:LocationService
    private val gpsLocationRequestCode = 1234

    private val model:MainViewModel by viewModels()

    private val viewBinding by lazy(LazyThreadSafetyMode.NONE) {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)

        model.playerLive.observe(this) {
            viewBinding.playerView.player = it
        }

        lifecycleScope.launchWhenStarted{
            Log.d("player","before delay")
            delay(LAUNCH_VIDEO_DELAY_MS)
            viewBinding.playerView.player?.play()
        }
        locationService = LocationService()
        locationService.locationChanged = {
            Log.d(this.javaClass.name, "onLocationChanged $this")
            viewBinding.playerView.player?.seekTo(0)
            viewBinding.playerView.player?.playWhenReady = true
        }
        if(locationAccessGranted())
            locationService.startLocationUpdates(getSystemService(LOCATION_SERVICE) as LocationManager)

        hideSystemUi()
    }

    private fun locationAccessGranted(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ),gpsLocationRequestCode
            )
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,  permissions: Array<out String>,  grantResults: IntArray)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED) {
            locationService.startLocationUpdates(getSystemService(LOCATION_SERVICE) as LocationManager)
        }
    }

    /**
     * Enter fullscreen mode
     */
    private fun hideSystemUi() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, viewBinding.playerView).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }
}