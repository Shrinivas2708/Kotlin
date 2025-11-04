package com.example.dailyjournal.network

import android.util.Log
import org.json.JSONArray
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.URL

object NetworkUtils {

//    private const val BASE_URL = "https://kotlin-backend-zz4x.onrender.com/api/blogs"
    private const val BASE_URL = "https://kotlin-backend.ssherikar2005.workers.dev/api/blogs"

    fun getAllBlogs(): JSONArray? {
        try {
            val url = URL(BASE_URL)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connect()

            val stream = BufferedReader(InputStreamReader(connection.inputStream))
            val response = stream.readText()
            stream.close()
            return JSONArray(response)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    fun createBlog(title: String, body: String): JSONObject? {
        try {
            val url = URL(BASE_URL)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.doOutput = true
            connection.setRequestProperty("Content-Type", "application/json")

            val data = JSONObject()
            data.put("title", title)
            data.put("body", body)
            data.put("tags", JSONArray())

            val output = BufferedWriter(OutputStreamWriter(connection.outputStream))
            output.write(data.toString())
            output.flush()
            output.close()

            val input = BufferedReader(InputStreamReader(connection.inputStream))
            val response = input.readText()
            input.close()

            return JSONObject(response)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    fun deleteBlog(id: String): Boolean {
        try {
            val url = URL("$BASE_URL/$id")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "DELETE"
            connection.setRequestProperty("Accept", "application/json")
            connection.setRequestProperty("Content-Type", "application/json")
            connection.doOutput = true
            connection.outputStream.write("{}".toByteArray()) // Required for Cloudflare Workers
            connection.connect()

            val code = connection.responseCode
            Log.d("DELETE_RESPONSE", "HTTP $code for ID=$id")
            return code in 200..299
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }


}
