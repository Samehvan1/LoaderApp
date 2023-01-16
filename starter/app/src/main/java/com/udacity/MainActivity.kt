package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.udacity.Util.sendNotify
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action
    private var curFName = ""
    private var curProgress: Double = 0.0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        createChannel()
        custom_button.setOnClickListener {
            download()
        }
        if (!isPermissionGranted()) grantPermission()
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {

            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            val dm = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            val cursor = dm.query(id?.let { DownloadManager.Query().setFilterById(it) })
            if (cursor.moveToFirst()) {
                var totalSize =
                    cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
                val dmState = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                when (dmState) {
                    DownloadManager.STATUS_FAILED -> context?.let {
                        notificationManager.sendNotify(it, curFName, "Failed",totalSize)
                        custom_button.updateState(ButtonState.Failed)
                    }
                    DownloadManager.STATUS_SUCCESSFUL -> context?.let {
                        notificationManager.sendNotify(it, curFName, "Completed",totalSize)
                        custom_button.updateState(ButtonState.Completed)
                    }
                    DownloadManager.STATUS_RUNNING -> context?.let {

                        var downloaded =
                            cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
                        var prog: Double = downloaded.toDouble() / totalSize.toDouble()
                        if (prog != curProgress) {
                            curProgress = prog
                            custom_button.upDateProgress(prog)
                        }
                    }
                }
            }
        }
    }

    private fun download() {
        var selUri = getSelectedString()
        if (selUri.isEmpty()) {
            Toast.makeText(this, "Please select an option first", Toast.LENGTH_SHORT).show()
        } else {
            custom_button.updateState(ButtonState.Loading)
            val request =
                DownloadManager.Request(Uri.parse(selUri))
                    .setTitle(getString(R.string.app_name))
                    .setDescription(getString(R.string.app_description))
                    .setRequiresCharging(false)
                    .setAllowedOverMetered(true)
                    .setAllowedOverRoaming(true)

            val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            downloadID =
                downloadManager.enqueue(request)// enqueue puts the download request in the queue.

        }
    }

    private fun getSelectedString(): String {
        var grp: RadioGroup = findViewById(R.id.radioHolder)
        var sele = grp.checkedRadioButtonId
        if (sele < 0) {
            return ""
        }
        when (sele) {
            R.id.rb_loadapp -> {
                curFName = getString(R.string.notification_description)
                return URL
            }
            R.id.rb_loadGlide -> {
                curFName = getString(R.string.download_glid_txt)
                return GlidURL
            }
            R.id.rb_loadRetro -> {
                curFName = getString(R.string.download_retro_txt)
                return RetroURL
            }
            else -> return ""
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notiChannel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notiChannel.enableLights(true)
            notiChannel.lightColor = Color.RED
            notiChannel.enableVibration(true)
            notiChannel.description = "Your download notifier"
            notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(notiChannel)
        }

    }

    companion object {
        private const val URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val GlidURL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val RetroURL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val CHANNEL_ID = "download_channel"
        private const val CHANNEL_NAME = "UdacityDownloader"
    }

    private fun isPermissionGranted(): Boolean {
        return checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }

    private fun grantPermission() {
        requestPermissions(arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 1000)
    }

}
