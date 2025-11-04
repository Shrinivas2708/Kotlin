package com.example.dailyjournal

import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.dailyjournal.network.NetworkUtils
import org.json.JSONObject
import kotlin.jvm.java

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val tvSignup = findViewById<TextView>(R.id.tvSignup)

        // paste from clipboard helper
        findViewById<TextView>(R.id.tvLoginTitle)?.setOnLongClickListener {
            val cm = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = cm.primaryClip
            if (clip != null && clip.itemCount > 0) {
                val text = clip.getItemAt(0).coerceToText(this).toString()
                // basic parse: email:pass or email,password or JSON
                if (text.contains(":")) {
                    val parts = text.split(":")
                    if (parts.size >= 2) {
                        etEmail.setText(parts[0].trim())
                        etPassword.setText(parts[1].trim())
                        Toast.makeText(this, "Pasted credentials", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            true
        }

        tvSignup.setOnClickListener { startActivity(Intent(this, SignupActivity::class.java)) }

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            if (email.isBlank() || password.isBlank()) {
                Toast.makeText(this, "Fill both fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            LoginTask(email, password).execute()
        }
    }

    inner class LoginTask(private val email: String, private val password: String) : AsyncTask<Void, Void, JSONObject?>() {
        override fun doInBackground(vararg params: Void?): JSONObject? {
            return NetworkUtils.login(email, password)
        }

        override fun onPostExecute(result: JSONObject?) {
            if (result != null && result.has("user")) {
                val user = result.getJSONObject("user")
                val prefs = getSharedPreferences("user", Context.MODE_PRIVATE)
                prefs.edit()
                    .putString("userId", user.optString("id"))
                    .putString("userName", user.optString("name"))
                    .putString("userEmail", user.optString("email"))
                    .apply()
                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                finish()
            } else {
                Toast.makeText(this@LoginActivity, "Login failed", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
