package com.mahamkhurram.i210681

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.widget.ImageView
import android.Manifest
import android.content.pm.PackageManager
import android.view.SurfaceView
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import io.agora.rtc2.ChannelMediaOptions
import io.agora.rtc2.Constants
import io.agora.rtc2.RtcEngine
import io.agora.rtc2.RtcEngineConfig
import io.agora.rtc2.IRtcEngineEventHandler
import io.agora.rtc2.video.VideoCanvas
class videocall : AppCompatActivity() {
    private val appId = "004832ecc0de4f95b8d9dfd42d040b3d"
    private val channelName = "mentorme"
    private val token="007eJxTYODf7d2tsv7/xgw7tnXSAn7BL4U1O1wfb/prYf9VOF1w0nkFBgMDEwtjo9TkZIOUVJM0S9MkixTLlLQUE6MUAxODJOMUJo1/qQ2BjAyHGlVZGRkgEMTnYMhNzSvJL8pNZWAAAEm8H5A="
    private val uid = 0
    private var isJoined = false

    private var agoraEngine: RtcEngine? = null

    private var localSurfaceView: SurfaceView? = null

    private var remoteSurfaceView: SurfaceView? = null
    private val PERMISSION_REQ_ID = 22
    private val REQUESTED_PERMISSIONS = arrayOf<String>(
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.CAMERA
    )
    private fun checkSelfPermission(): Boolean {
        return !(ContextCompat.checkSelfPermission(
            this,
            REQUESTED_PERMISSIONS[0]
        ) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    this,
                    REQUESTED_PERMISSIONS[1]
                ) != PackageManager.PERMISSION_GRANTED)
    }
    fun showMessage(message: String?) {
        runOnUiThread {
            Toast.makeText(
                applicationContext,
                message,
                Toast.LENGTH_SHORT
            ).show()
        }
    }
    private fun setupVideoSDKEngine() {
        try {
            val config = RtcEngineConfig()
            config.mContext = baseContext
            config.mAppId = appId
            config.mEventHandler = mRtcEventHandler
            agoraEngine = RtcEngine.create(config)
            // By default, the video module is disabled, call enableVideo to enable it.
            agoraEngine!!.enableVideo()
        } catch (e: Exception) {
            showMessage(e.toString())
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_videocall)
        val endIcon = findViewById<ImageView>(R.id.endcall)
        setupVideoSDKEngine();
        if (!checkSelfPermission()) {
            ActivityCompat.requestPermissions(this, REQUESTED_PERMISSIONS, PERMISSION_REQ_ID);
        }
        joinCall()
        // Set a click listener on the search icon
        endIcon.setOnClickListener {
            // Navigate to the search activity
            leaveCall()
            val intent = Intent(this, chatscreen::class.java)
            startActivity(intent)
        }
    }
    private fun leaveCall() {
        if (!isJoined) {
            showMessage("You are not in the call")
        }
        else {
            agoraEngine!!.leaveChannel()
            showMessage("You left the channel")
            if (remoteSurfaceView != null) remoteSurfaceView!!.visibility = View.GONE
            if (localSurfaceView != null) localSurfaceView!!.visibility = View.GONE
            isJoined = false
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        agoraEngine!!.stopPreview()
        agoraEngine!!.leaveChannel()

        Thread {
            RtcEngine.destroy()
            agoraEngine = null
        }.start()
    }
    private val mRtcEventHandler: IRtcEngineEventHandler =
        object : IRtcEngineEventHandler() {
            override fun onUserJoined(uid: Int, elapsed: Int) {
                showMessage("Remote user joined $uid")

                // Set the remote video view
                runOnUiThread { setupRemoteVideo(uid) }
            }

            override fun onJoinChannelSuccess(channel: String, uid: Int, elapsed: Int) {
                isJoined = true
                showMessage("Joined Channel $channel")
            }

            override fun onUserOffline(uid: Int, reason: Int) {
                showMessage("Remote user offline $uid $reason")
                runOnUiThread { remoteSurfaceView!!.visibility = View.GONE }
            }
        }

    private fun setupRemoteVideo(uid: Int) {
        remoteSurfaceView = SurfaceView(baseContext)
        remoteSurfaceView!!.setZOrderMediaOverlay(true)

        // Find the FrameLayout by its ID
        val remoteUserFrameLayout = findViewById<FrameLayout>(R.id.remoteuser)

        // Add the SurfaceView to the FrameLayout
        remoteUserFrameLayout.addView(remoteSurfaceView)

        agoraEngine!!.setupRemoteVideo(
            VideoCanvas(
                remoteSurfaceView,
                VideoCanvas.RENDER_MODE_FIT,
                uid
            )
        )
        remoteSurfaceView!!.visibility = View.VISIBLE
    }


    private fun setupLocalVideo() {
        localSurfaceView = SurfaceView(baseContext)

        // Find the FrameLayout by its ID
        val localUserFrameLayout = findViewById<FrameLayout>(R.id.localuser)

        // Add the SurfaceView to the FrameLayout
        localUserFrameLayout.addView(localSurfaceView)

        agoraEngine!!.setupLocalVideo(
            VideoCanvas(
                localSurfaceView,
                VideoCanvas.RENDER_MODE_HIDDEN,
                0
            )
        )
    }

    private fun joinCall() {
        if (checkSelfPermission()) {
            val options = ChannelMediaOptions()

            options.channelProfile = Constants.CHANNEL_PROFILE_COMMUNICATION
            options.clientRoleType = Constants.CLIENT_ROLE_BROADCASTER
            setupLocalVideo()
            localSurfaceView!!.visibility = View.VISIBLE
            agoraEngine!!.startPreview()
            agoraEngine!!.joinChannel(token, channelName, uid, options)
        } else {
            Toast.makeText(applicationContext, "Permissions was not granted", Toast.LENGTH_SHORT)
                .show()
        }
    }

}