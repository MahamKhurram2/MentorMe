package com.mahamkhurram.i210681



import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.projection.MediaProjectionManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Base64
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.akexorcist.screenshotdetection.ScreenshotDetectionDelegate
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import java.io.ByteArrayOutputStream

private const val TAG = "MainActivity"
private const val REQUEST_MEDIA_PROJECTION = 1

private lateinit var projectionManager: MediaProjectionManager
class MainActivity : AppCompatActivity(),ScreenshotDetectionDelegate.ScreenshotDetectionListener {

    companion object {
        private const val REQUEST_CODE_READ_EXTERNAL_STORAGE_PERMISSION = 3009
    }

    private val CHANNEL_ID = "massage id"
    private val NOTIFICATION_ID = 123
    private var userName: String? = null
    private val url = "http://192.168.8.102/A3/image.php"
    private var encodedImage: String = ""
    private lateinit var bitmap: Bitmap

    private lateinit var img: ImageView
    private val screenshotDetectionDelegate = ScreenshotDetectionDelegate(this, this)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, logina::class.java)
            startActivity(intent)
            finish()
        }, 5000)
    }

    override fun onStart() {
        super.onStart()
        screenshotDetectionDelegate.startScreenshotDetection()
        NotificationHelper(this,"ScreenshotTaken").Notification()
        val kh = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        kh.hideSoftInputFromWindow(currentFocus?.windowToken,0)
    }

    override fun onStop() {
        super.onStop()
        screenshotDetectionDelegate.stopScreenshotDetection()
    }

    override fun onScreenCaptured(path: String) {
        // Convert the captured screen path to a bitmap
        val bitmap = getBitmapFromPath(path)

        // Upload the screenshot image
        uploadScreenshot(bitmap)

        // Optionally, you can add other operations here
        NotificationHelper(this, "Screenshot Taken").Notification()
        val kh = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        kh.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }

    private fun getBitmapFromPath(path: String): Bitmap {
        // Use BitmapFactory to decode the file path into a Bitmap
        return BitmapFactory.decodeFile(path)
    }


    override fun onScreenCapturedWithDeniedPermission() {
        Log.d(TAG, "Screenshot captured but was denied permission")
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_CODE_READ_EXTERNAL_STORAGE_PERMISSION -> {
                if (grantResults.getOrNull(0) == PackageManager.PERMISSION_DENIED) {
                    showReadExternalStoragePermissionDeniedMessage()
                }
            }

            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private fun checkReadExternalStoragePermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestReadExternalStoragePermission()
        }
    }

    private fun requestReadExternalStoragePermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            REQUEST_CODE_READ_EXTERNAL_STORAGE_PERMISSION
        )
    }

    private fun showReadExternalStoragePermissionDeniedMessage() {
        Toast.makeText(this, "Read external storage permission has denied", Toast.LENGTH_SHORT)
            .show()
    }

    private fun uploadScreenshot(bitmap: Bitmap) {
        val request = object : StringRequest(Method.POST, url,
            Response.Listener<String> { response ->
                // img.setImageResource(R.drawable.ic_launcher_foreground)
                Toast.makeText(applicationContext, response.toString(), Toast.LENGTH_LONG).show()
                Log.e(ContentValues.TAG, "Success uploading screenshot: $response")
            },
            Response.ErrorListener { error ->
                Toast.makeText(applicationContext, error.toString(), Toast.LENGTH_LONG).show()
                Log.e(ContentValues.TAG, "Error uploading screenshot: ${error.toString()}", error)
            }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val map = HashMap<String, String>()
                map["upload"] = encodeBitmapImage(bitmap)
                return map
            }
        }

        val queue = Volley.newRequestQueue(applicationContext)
        queue.add(request)
    }


    @SuppressLint("MissingPermission")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_MEDIA_PROJECTION && resultCode == RESULT_OK) {
            // Start capturing the screen
            val screenCaptureIntent = projectionManager.createScreenCaptureIntent()
            startService(screenCaptureIntent)
        }
    }

    fun takeScreenshot(view: View) {
        // Request permission to capture the screen
        startActivityForResult(
            projectionManager.createScreenCaptureIntent(),
            REQUEST_MEDIA_PROJECTION
        )
    }

    private fun encodeBitmapImage(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val bytesofimage: ByteArray = byteArrayOutputStream.toByteArray()
        return android.util.Base64.encodeToString(bytesofimage, Base64.DEFAULT)
    }
}