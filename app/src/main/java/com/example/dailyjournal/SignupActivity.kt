package com.example.dailyjournal

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.dailyjournal.network.NetworkUtils
import org.json.JSONObject

class SignupActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        val etName = findViewById<EditText>(R.id.etName)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnSignup = findViewById<Button>(R.id.btnSignup)
        val tvLogin = findViewById<TextView>(R.id.tvLogin)

        tvLogin.setOnClickListener { startActivity(Intent(this, LoginActivity::class.java)) }

        btnSignup.setOnClickListener {
            val name = etName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            if (name.isBlank() || email.isBlank() || password.isBlank()) {
                Toast.makeText(this, "All fields required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            SignupTask(name, email, password).execute()
        }
    }

    inner class SignupTask(private val name: String, private val email: String, private val password: String)
        : AsyncTask<Void, Void, JSONObject?>() {
        override fun doInBackground(vararg params: Void?): JSONObject? {
            return NetworkUtils.signup(name, email, password)
        }

        override fun onPostExecute(result: JSONObject?) {
            if (result != null && result.has("user")) {
                Toast.makeText(this@SignupActivity, "Account created", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@SignupActivity, LoginActivity::class.java))
                finish()
            } else {
                Toast.makeText(this@SignupActivity, "Signup failed", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
