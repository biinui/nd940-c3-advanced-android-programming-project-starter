package com.udacity.home

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.udacity.R
import com.udacity.databinding.ActivityMainBinding
import com.udacity.util.sendNotification
import com.udacity.view.ButtonState
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import timber.log.Timber


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0
    private lateinit var  binding: ActivityMainBinding
    private var urlToDownload = ""
    private var messageBody = R.string.project_three_repository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        download_button.setOnClickListener {
            if (urlToDownload.isEmpty()) {
                Toast.makeText(applicationContext, R.string.please_select_an_option, Toast.LENGTH_SHORT).show()
            } else {
                download_button.buttonState = ButtonState.Loading
                download()
            }
        }

        createChannel(
            getString(R.string.download_notification_channel_id),
            getString(R.string.download_notification_channel_name)
        )
    }

    fun onDownloadOptionClicked(view: View) {
        if (view is RadioButton) {
            val checked = view.isChecked

            when (view.getId()) {
                R.id.picasso_radiobutton ->
                    if (checked) {
                        urlToDownload = PICASSO_URL
                        messageBody = R.string.picasso_repository
                    }
                R.id.loadapp_radiobutton ->
                    if (checked) {
                        urlToDownload = UDACITY_URL
                        messageBody = R.string.project_three_repository
                    }
                R.id.retrofit_radiobutton ->
                    if (checked) {
                        urlToDownload = RETROFIT_URL
                        messageBody = R.string.retrofit_repository
                    }
            }
        }
        Timber.i("urlToDownload: $urlToDownload")
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            Timber.i("downloadDone: $id")
            val notificationManager = ContextCompat.getSystemService(applicationContext, NotificationManager::class.java) as NotificationManager
            notificationManager.sendNotification(applicationContext.getText(messageBody).toString(), applicationContext.getText(R.string.success).toString(), applicationContext)
            download_button.buttonState = ButtonState.Completed
        }
    }

    private fun download() {
        val request = DownloadManager.Request(Uri.parse(urlToDownload))
            .setTitle(getString(R.string.app_name))
            .setDescription(getString(R.string.app_description))
            .setRequiresCharging(false)
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID = downloadManager.enqueue(request)// enqueue puts the download request in the queue.
        Timber.i("downloadRequested: $downloadID")
    }

    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_DEFAULT
            )
                .apply {
                    setShowBadge(false)
                }

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = getString(R.string.download)

            val notificationManager = ContextCompat.getSystemService(
                applicationContext,
                NotificationManager::class.java
            ) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)

        }
    }

    companion object {
        private const val PICASSO_URL  = "https://github.com/square/picasso/archive/master.zip"
        private const val UDACITY_URL  = "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val RETROFIT_URL = "https://github.com/square/retrofit/archive/master.zip"
    }

}
