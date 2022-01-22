package com.QRSCANER.QRscaner

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.QRSCANER.QRscaner.R
import com.google.zxing.integration.android.IntentIntegrator
import com.journeyapps.barcodescanner.CaptureActivity
import org.json.JSONException
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

class MainActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks,
    EasyPermissions.RationaleCallbacks {
    var View1: CardView? = null
    var View2: CardView? = null
    var Scan: Button? = null
    var EnterCode: Button? = null
    var Enter: Button? = null
    var Code: EditText? = null
    var Text: TextView? = null
    var hide: Animation? = null
    var reveal: Animation? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        View1 = findViewById(R.id.View1)
        View2 = findViewById(R.id.View2)
        Scan = findViewById(R.id.სკანერი)
        EnterCode = findViewById(R.id.შესაყვანი)
        Enter = findViewById(R.id.ენთერი)
        Code = findViewById(R.id.კოდიი)
        Text = findViewById(R.id.tvText)

        hide = AnimationUtils.loadAnimation(this, android.R.anim.fade_out)
        reveal = AnimationUtils.loadAnimation(this, android.R.anim.fade_in)

        Text!!.startAnimation(reveal)
        View2!!.startAnimation(reveal)
        Text!!.setText("დაასკანერე კოდი აქ")
        View2!!.visibility = View.VISIBLE

        Scan!!.setOnClickListener {
            Text!!.startAnimation(reveal)
            View1!!.startAnimation(hide)
            View2!!.startAnimation(reveal)

            View2!!.visibility = View.VISIBLE
            View1!!.visibility = View.GONE
            Text!!.setText("დაასკანერე კოდი აქ")


        }

        View2!!.setOnClickListener {
            cameraTask()
        }

        Enter!!.setOnClickListener {

            if (Code!!.text.toString().isNullOrEmpty()) {
                Toast.makeText(this, "შეიყვანე კოდი", Toast.LENGTH_SHORT).show()
            } else {
                var value = Code!!.text.toString()

                Toast.makeText(this, value, Toast.LENGTH_SHORT).show()

            }
        }
        EnterCode!!.setOnClickListener {
            Text!!.startAnimation(reveal)
            View1!!.startAnimation(reveal)
            View2!!.startAnimation(hide)

            View2!!.visibility = View.GONE
            View1!!.visibility = View.VISIBLE
            Text!!.setText("შეიყვანე კოდი აქ")
        }

    }

    private fun hasCameraAccess(): Boolean {
        return EasyPermissions.hasPermissions(this, Manifest.permission.CAMERA)
    }

    private fun cameraTask() {

        if (hasCameraAccess()) {

            var qrScanner = IntentIntegrator(this)
            qrScanner.setPrompt("დაასკანერე QR კოდი")
            qrScanner.setCameraId(0)
            qrScanner.setOrientationLocked(true)
            qrScanner.setBeepEnabled(true)
            qrScanner.captureActivity = CaptureActivity::class.java
            qrScanner.initiateScan()
        } else {
            EasyPermissions.requestPermissions(
                this,
                "ჰქონდეს წვდომა თქვენს კამერაზე?.",
                123,
               android.Manifest.permission.CAMERA
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {


        var result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                Toast.makeText(this, "შედეგი ვერ მოიძებნა", Toast.LENGTH_SHORT).show()
                Code!!.setText("")
            } else {
                try {

                    View1!!.startAnimation(reveal)
                    View2!!.startAnimation(hide)
                    View1!!.visibility = View.VISIBLE
                    View2!!.visibility = View.GONE
                    Code!!.setText(result.contents.toString())
                } catch (exception: JSONException) {
                    Toast.makeText(this, exception.localizedMessage, Toast.LENGTH_SHORT).show()
                    Code!!.setText("")
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }

        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
            Toast.makeText(this, "ნებართვა აღებულია", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
    }

    override fun onRationaleDenied(requestCode: Int) {
    }

    override fun onRationaleAccepted(requestCode: Int) {
    }


}
