package com.example.dailyjournal

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.AsyncTask
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dailyjournal.model.Blog
import com.example.dailyjournal.network.NetworkUtils
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.snackbar.Snackbar
import org.json.JSONArray

class MainActivity : AppCompatActivity() {

    private lateinit var adapter: BlogAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        setSupportActionBar(findViewById(R.id.toolbar))
//        findViewById<MaterialToolbar>(R.id.toolbar).setOnMenuItemClickListener { item ->
//            when (item.itemId) {
//                R.id.action_refresh -> {
//                    LoadBlogsTask().execute()
//                    true
//                }
//                else -> false
//            }
//        }


        adapter = BlogAdapter(mutableListOf(), onClick = { openDetail(it) }, onDelete = { confirmDelete(it) })
        findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.rvBlogs).apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
        }

        findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.fabCreate)
            .setOnClickListener { showDialog() }

        LoadBlogsTask().execute()
    }

    private fun showDialog() {
        val v = LayoutInflater.from(this).inflate(R.layout.dialog_create, null)
        val etTitle = v.findViewById<EditText>(R.id.etTitle)
        val etBody = v.findViewById<EditText>(R.id.etBody)

        val dialog = AlertDialog.Builder(this, R.style.RetroDialog)
            .setView(v)
            .setCancelable(false)
            .create()

        dialog.show()

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val btnSave = Button(this).apply {
            text = "Save"
            setTextColor(Color.WHITE)
            setBackgroundColor(Color.parseColor("#FF4081"))
        }

        val btnCancel = Button(this).apply {
            text = "Cancel"
            setTextColor(Color.parseColor("#FF4081"))
            setBackgroundColor(Color.TRANSPARENT)
        }

        val container = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.END
            setPadding(0, 20, 0, 0)
            addView(btnCancel)
            addView(btnSave)
        }

        (v as LinearLayout).addView(container)

        btnCancel.setOnClickListener { dialog.dismiss() }
        btnSave.setOnClickListener {
            val title = etTitle.text.toString().trim()
            val body = etBody.text.toString().trim()
            if (title.isEmpty() || body.isEmpty()) {
                Toast.makeText(this, "Please fill both fields", Toast.LENGTH_SHORT).show()
            } else {
                CreateBlogTask(title, body).execute()
                dialog.dismiss()
            }
        }
    }


    inner class LoadBlogsTask : AsyncTask<Void, Void, List<Blog>>() {
        override fun onPreExecute() {
            super.onPreExecute()
            findViewById<View>(R.id.loadingOverlay).visibility = View.VISIBLE
        }

        override fun doInBackground(vararg params: Void?): List<Blog> {
            val arr: JSONArray = NetworkUtils.getAllBlogs() ?: JSONArray()
            val list = mutableListOf<Blog>()
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                list.add(
                    Blog(
                        obj.optString("id"),
                        obj.optString("title"),
                        obj.optString("body"),
                        obj.optString("createdAt")
                    )
                )
            }
            return list
        }

        override fun onPostExecute(result: List<Blog>) {
            findViewById<View>(R.id.loadingOverlay).visibility = View.GONE
            adapter.updateList(result)
        }
    }


    inner class CreateBlogTask(private val title: String, private val body: String) : AsyncTask<Void, Void, Blog?>() {
        override fun doInBackground(vararg params: Void?): Blog? {
            val obj = NetworkUtils.createBlog(title, body)
            return obj?.let {
                Blog(it.optString("_id"), it.optString("title"), it.optString("body"), it.optString("createdAt"))
            }
        }

        override fun onPostExecute(result: Blog?) {
            if (result != null) {
                Snackbar.make(findViewById(R.id.rootMain), "Blog Created!", Snackbar.LENGTH_SHORT).show()
                LoadBlogsTask().execute()
            } else Toast.makeText(this@MainActivity, "Failed to create blog", Toast.LENGTH_SHORT).show()
        }
    }

    private fun confirmDelete(blog: Blog) {
        val v = LayoutInflater.from(this).inflate(R.layout.dialog_confirm_delete, null)
        val btnCancel = v.findViewById<Button>(R.id.btnCancel)
        val btnDelete = v.findViewById<Button>(R.id.btnDelete)

        val dialog = AlertDialog.Builder(this, R.style.RetroDialog)
            .setView(v)
            .setCancelable(false)
            .create()

        dialog.show()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        btnCancel.setOnClickListener { dialog.dismiss() }

        btnDelete.setOnClickListener {
            dialog.dismiss()
            AsyncTask.execute {
                val success = NetworkUtils.deleteBlog(blog.id ?: "")
                runOnUiThread {
                    if (success) {
                        adapter.removeById(blog.id!!)
                        Snackbar.make(findViewById(R.id.rootMain), "Deleted", Snackbar.LENGTH_SHORT).show()
                    } else Toast.makeText(this, "Delete failed", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }




    private fun openDetail(blog: Blog) {
        val i = Intent(this, BlogDetailActivity::class.java)
        i.putExtra("title", blog.title)
        i.putExtra("body", blog.body)
        i.putExtra("date", blog.createdAt)
        startActivity(i)
    }
}
