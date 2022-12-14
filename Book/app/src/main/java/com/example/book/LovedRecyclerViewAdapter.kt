package com.example.book

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.book.databinding.BookItemBinding

class LovedRecyclerViewAdapter(
    private val bookData: List<BookData>,
    private val lovedBooks: String
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var binding: BookItemBinding? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        binding = BookItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return LovedBooksList(binding!!, parent.context, lovedBooks)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val lovedBooksList: LovedBooksList = holder as LovedBooksList
        lovedBooksList.bind(bookData)
    }

    override fun getItemCount(): Int {
        return lovedBooks.length
    }
}

class LovedBooksList(
    private val binding: BookItemBinding,
    private val context: Context,
    private val lovedBooks: String
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(bookData: List<BookData>) {
        binding.apply {

            if (Character.getNumericValue(lovedBooks[adapterPosition]) >= 0) {
                val book = bookData[Character.getNumericValue(lovedBooks[adapterPosition])]

                Glide.with(context).load(book.imageUrl)
                    .listener(object: RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            imageBar.visibility = View.GONE
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            imageBar.visibility = View.GONE
                            return false
                        }
                    })
                    .into(imageView)

                bookCard.setOnClickListener {
                    val intent = Intent(context, PdfViewActivity::class.java)
                    intent.putExtra("bookData", book)
                    intent.putExtra("position", Character.getNumericValue(lovedBooks[adapterPosition]))
                    context.startActivity(intent)
                }

                tvBookName.text = book.name
                tvWriter.text = book.writer
                "Прочитать: ${book.progress.toInt()}%".also { tvProgress.text = it }
                progressBook.progress = book.progress.toInt()
            }
        }
    }
}