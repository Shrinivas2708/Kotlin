package com.example.dailyjournal

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class BlogDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val title = intent.getStringExtra("title") ?: ""
        val body = intent.getStringExtra("body") ?: ""
        val date = intent.getStringExtra("date") ?: ""

        findViewById<android.widget.TextView>(R.id.tvDetailTitle).text = title
        findViewById<android.widget.TextView>(R.id.tvDetailBody).text = body
        findViewById<android.widget.TextView>(R.id.tvDetailDate).text = date
    }
}
