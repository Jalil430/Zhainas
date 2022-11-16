package com.example.book

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.book.databinding.BookItemBinding

class BookRecyclerViewAdapter(private val bookData: List<BookData>) : RecyclerView.Adapter<Book>() {

    private var binding: BookItemBinding? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Book {
        binding = BookItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return Book(binding!!, parent.context)
    }

    override fun onBindViewHolder(holder: Book, position: Int) {
        holder.bind(bookData[position])
    }

    override fun getItemCount(): Int {
        return bookData.size
    }

}

class Book(private val binding: BookItemBinding, private val context: Context) : RecyclerView.ViewHolder(binding.root) {
    @SuppressLint("UseCompatLoadingForDrawables")
    fun bind(bookData: BookData) {
        binding.apply {
            bookImage.setImageDrawable(context.getDrawable(bookData.image))

            bookImage.setOnClickListener {
                val intent = Intent(context, PdfViewActivity::class.java)
                intent.putExtra("bookUrl", bookData.bookUrl)
                intent.putExtra("bookName", bookData.name)
                context.startActivity(intent)
            }
        }
    }
}