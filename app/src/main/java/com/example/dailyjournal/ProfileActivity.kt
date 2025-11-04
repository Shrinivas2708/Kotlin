package com.example.dailyjournal

import android.os.AsyncTask
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.dailyjournal.network.NetworkUtils
import org.json.JSONObject

class ProfileActivity : AppCompatActivity() {

    private lateinit var tvName: TextView
    private lateinit var tvEmail: TextView
    private lateinit var tvId: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        tvName = findViewById(R.id.tvName)
        tvEmail = findViewById(R.id.tvEmail)

        val prefs = getSharedPreferences("user", MODE_PRIVATE)
        val userId = prefs.getString("userId", null)

        if (userId != null) {
            LoadProfileTask(userId).execute()
        } else {
            Toast.makeText(this, "No user found", Toast.LENGTH_SHORT).show()
        }

        findViewById<Button>(R.id.btnLogout).setOnClickListener {
            prefs.edit().clear().apply()
            finish()
        }
    }

    inner class LoadProfileTask(private val userId: String) : AsyncTask<Void, Void, JSONObject?>() {
        override fun doInBackground(vararg params: Void?): JSONObject? {
            return NetworkUtils.getProfile(userId)
        }

        override fun onPostExecute(result: JSONObject?) {
            if (result != null) {
                tvName.text = result.optString("name", "Unknown User")
                tvEmail.text = result.optString("email", "No Email")
            } else {
                Toast.makeText(this@ProfileActivity, "Failed to load profile", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
