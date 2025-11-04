package com.example.dailyjournal

import android.view.*
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.dailyjournal.model.Blog
import java.text.SimpleDateFormat
import java.util.*

private fun formatDate(raw: String?): String {
    return try {
        val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        parser.timeZone = TimeZone.getTimeZone("UTC")
        val date = parser.parse(raw!!)
        val fmt = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        fmt.format(date!!)
    } catch (e: Exception) {
        raw ?: ""
    }
}
class BlogAdapter(
    private val items: MutableList<Blog>,
    private val onClick: (Blog) -> Unit,
    private val onDelete: (Blog) -> Unit
) : RecyclerView.Adapter<BlogAdapter.VH>() {

    inner class VH(v: View) : RecyclerView.ViewHolder(v) {
        val title: TextView = v.findViewById(R.id.tvTitle)
        val body: TextView = v.findViewById(R.id.tvBody)
        val date: TextView = v.findViewById(R.id.tvDate)
        val delete: Button = v.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_blog, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val blog = items[position]
        holder.title.text = blog.title
        holder.body.text = blog.body
        holder.date.text = formatDate(blog.createdAt)
        holder.itemView.setOnClickListener { onClick(blog) }
        holder.delete.setOnClickListener { onDelete(blog) }
    }

    override fun getItemCount() = items.size

    fun updateList(newList: List<Blog>) {
        items.clear()
        items.addAll(newList)
        notifyDataSetChanged()
    }

    fun removeById(id: String) {
        val index = items.indexOfFirst { it.id == id }
        if (index >= 0) {
            items.removeAt(index)
            notifyItemRemoved(index)
        }
    }


}
