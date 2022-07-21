package com.tensorion.sensoplayer

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import com.tensorion.sensoplayer.databinding.ActivityLocationAccessRationaleBinding

/**
 * Educational UI activity used for Privacy Dashboard, App Info and
 * shouldShowRequestPermissionRationale in MainActivity
 */
class LocationAccessRationaleActivity : AppCompatActivity() {
    private val viewBinding by lazy(LazyThreadSafetyMode.NONE) {
        ActivityLocationAccessRationaleBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)

        viewBinding.buttonOk.setOnClickListener{
            val data = Intent()
            setResult(Activity.RESULT_OK, data)
            finish()
        }

        val intent = intent
        viewBinding.buttonNo.visibility =
            if(intent.action==null) VISIBLE else GONE

        viewBinding.buttonNo.setOnClickListener{
            val data = Intent()
            setResult(Activity.RESULT_CANCELED, data)
            finish()
        }
    }
}