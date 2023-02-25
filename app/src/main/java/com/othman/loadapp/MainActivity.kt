package com.othman.loadapp

import android.app.DownloadManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.othman.loadapp.utils.NotificationUtils
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_detail.*
import kotlinx.android.synthetic.main.content_main.*
import kotlin.random.Random


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        initChannel()

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        custom_button.setOnClickListener {
            if (radio_group.checkedRadioButtonId == -1)
                Toast.makeText(this, "Please select the file you wanna download", Toast.LENGTH_LONG)
                    .show()
            else {
                URL = when (radio_group.checkedRadioButtonId) {
                    R.id.glide -> GLIDE_URL
                    R.id.loadapp -> LOADAPP_URL
                    R.id.retrofit -> RETROFIT_URL
                    else -> ""
                }
                Log.w("URL", "" + URL)
                download()
                custom_button.buttonState = LoadingButton.ButtonState.DOWNLOADING
            }
        }
    }

    private fun initChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationUtils.createNotificationChannel(
                this,
                NotificationUtils.getChannel(this)
            )
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            Log.w("onReceive", "" + intent?.extras)
//            val pendingIntent = PendingIntent.getActivity(
//                context,id,notifyIntent,PendingIntent.FLAG_UPDATE_CURRENT
//            )
            if (id == downloadID) {
                val query = DownloadManager.Query()
                query.setFilterById(id)
                val manager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
                val cursor: Cursor = manager.query(query)

                if (cursor.moveToFirst()) {
                    Log.w("cursor", "" + cursor.getCount())
                    if (cursor.getCount() > 0) {
                        val status =
                            cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                        Log.w("status", "" + status)
                        if (status == DownloadManager.STATUS_SUCCESSFUL) {
                            Log.w("Download", "Success")
                            // So something here on success
                            sendNotification(true, id)
                        } else {
                            Log.w("Download", "Failed")
                            // So something here on failed.
                            sendNotification(false, id)
                        }
                    }
                }
            }
        }
    }

    private fun sendNotification(isSuccess: Boolean,id:Long) {
        custom_button.buttonState = LoadingButton.ButtonState.IDLE
        NotificationUtils.sendNotification(
            context = this@MainActivity,
            title = map[id]!!,
            status = getString(
                if (isSuccess)
                    R.string.success
                else
                    R.string.failed
            ),
            notificationId = Random.nextInt()
        )
    }

    val map: MutableMap<Long, String> = HashMap()
    private fun download() {
        val request =
            DownloadManager.Request(Uri.parse(URL))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            request.setRequiresCharging(false)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.
        map[downloadID] = getString(
            if (URL == GLIDE_URL)
                R.string.glide_image_loading_library_by_bumptech
            else if (URL == LOADAPP_URL)
                R.string.loadapp_current_repository_by_udacity
            else if (URL == RETROFIT_URL)
                R.string.retrofit_type_safe_http_client_for_android_and_java_by_square_inc
            else
                0
        )
    }

    companion object {
        private var URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"

        private val GLIDE_URL = "https://github.com/bumptech/glide/archive/refs/heads/master.zip"
        private val LOADAPP_URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/refs/heads/master.zip"
        private val RETROFIT_URL =
            "https://github.com/square/retrofit/archive/refs/heads/master.zip"

        private const val CHANNEL_ID = "channelId"
    }

}
