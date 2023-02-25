package com.othman.loadapp

import android.app.Notification.EXTRA_TITLE
import android.os.Bundle
import android.provider.AlarmClock.EXTRA_MESSAGE
import androidx.appcompat.app.AppCompatActivity
import com.othman.loadapp.utils.NotificationUtils
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

class DetailActivity : AppCompatActivity() {

    private lateinit var fileTitle: String
    private lateinit var downloadStatus: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        val extras = intent.extras
        extras?.let {
            fileTitle = it.getString(EXTRA_TITLE).toString()
            downloadStatus = it.getString(EXTRA_MESSAGE).toString()
            file_name.text = fileTitle
            status.text = downloadStatus

            download_img.setImageResource(
                if (downloadStatus == getString(R.string.success))
                    R.drawable.ic_round_cloud_done_24
                else
                    R.drawable.cloud_alert
            )

            download_img.contentDescription = getString(
                if (downloadStatus == getString(R.string.success))
                    R.string.downloaded_successfully
                else
                    R.string.failed_to_download
            )

            NotificationUtils.clearNotification(this,it.getInt("NOTIFICATION_ID"))
        }
        ok.setOnClickListener {
            finish()
        }
    }

}
