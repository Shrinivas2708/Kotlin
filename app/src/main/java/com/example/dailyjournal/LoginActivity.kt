package com.example.dailyjournal

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.dailyjournal.network.NetworkUtils
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)  // ← MUST BE FIRST!
        Log.d("LoginActivity", "onCreate START")

        try {
            val prefs = getSharedPreferences("user", MODE_PRIVATE)
            val userId = prefs.getString("userId", null)
            Log.d("LoginActivity", "Checked userId: $userId")

            if (userId != null) {
                Log.d("LoginActivity", "User logged in, redirecting to MainActivity")
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
                return
            }

            Log.d("LoginActivity", "About to setContentView")
            setContentView(R.layout.activity_login)
            Log.d("LoginActivity", "setContentView completed")

            val etEmail = findViewById<EditText>(R.id.etEmail)
            val etPassword = findViewById<EditText>(R.id.etPassword)
            val btnLogin = findViewById<Button>(R.id.btnLogin)
            val tvSignup = findViewById<TextView>(R.id.tvSignup)

            Log.d("LoginActivity", "All views found successfully")

            tvSignup.setOnClickListener {
                startActivity(Intent(this, SignupActivity::class.java))
            }

            btnLogin.setOnClickListener {
                val email = etEmail.text.toString().trim()
                val password = etPassword.text.toString().trim()
                Log.d("LoginActivity", "Login clicked: email=$email")

                if (email.isBlank() || password.isBlank()) {
                    Toast.makeText(this, "Fill both fields", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                LoginTask(email, password).execute()
            }

            Log.d("LoginActivity", "onCreate COMPLETE")

        } catch (e: Exception) {
            Log.e("LoginActivity", "ERROR in onCreate", e)
            e.printStackTrace()
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