package com.tensorion.sensoplayer

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import com.tensorion.sensoplayer.Constants.LAUNCH_VIDEO_DELAY_MS
import com.tensorion.sensoplayer.databinding.ActivityMainBinding
import kotlinx.coroutines.delay

/**
 *  Full-screen activity with a video player controlled by shaking, rotating and moving the device
 */
class MainActivity : AppCompatActivity() {

    private lateinit var locationService:LocationService

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
        locationService = LocationService() // TODO decouple using HILT
        locationService.locationChanged = {
            Log.d(this.javaClass.name, "onLocationChanged $this")
            viewBinding.playerView.player?.seekTo(0)
            viewBinding.playerView.player?.playWhenReady = true
        }
        if(locationAccessGranted())
            locationService.startLocationUpdates(getSystemService(LOCATION_SERVICE) as LocationManager)

        hideSystemUi()
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

    /**
     * Checks location permission and, if it's not granted, launches educational UI or requests it
     */
    private fun locationAccessGranted(): Boolean {
        when {
            ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION)
                    == PERMISSION_GRANTED -> {
                return true
            }
            shouldShowRequestPermissionRationale(this, ACCESS_FINE_LOCATION) -> {
                val intent = Intent(this,
                    LocationAccessRationaleActivity::class.java)
                startForResult.launch(intent)
            }
            else -> {
                requestPermissionLauncher.launch( ACCESS_FINE_LOCATION )
            }
        }
        return false
    }

    /**
     * Starts location updates if permission is granted or shows toast with explanation otherwise
     */
    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                locationService.startLocationUpdates(getSystemService(LOCATION_SERVICE) as LocationManager)
            } else {
                Toast.makeText(this,R.string.location_permission_needed,Toast.LENGTH_LONG).show()
            }
        }

    /**
     * Asks user for location access if he clicks OK in educational UI activity
     */
    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                requestPermissionLauncher.launch(ACCESS_FINE_LOCATION)
            }
        }
}