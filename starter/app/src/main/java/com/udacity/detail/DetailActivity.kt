package com.udacity.detail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.udacity.R
import kotlinx.android.synthetic.main.activity_detail.*
import timber.log.Timber

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        val filename = intent.getStringExtra(applicationContext.getString(R.string.file_name))
        val status = intent.getStringExtra(applicationContext.getString(R.string.status))

        Timber.i("$filename, $status")
    }

}
