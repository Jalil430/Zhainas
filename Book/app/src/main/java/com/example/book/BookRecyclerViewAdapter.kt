package com.example.book

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.Glide.*
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.book.databinding.BookItemBinding

class BookRecyclerViewAdapter(private val bookData: List<BookData>, private val preferences: SharedPreferences) : RecyclerView.Adapter<Book>() {

    private var binding: BookItemBinding? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Book {
        binding = BookItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return Book(binding!!, parent.context, preferences)
    }

    override fun onBindViewHolder(holder: Book, position: Int) {
        holder.bind(bookData)
    }

    override fun getItemCount(): Int {
        return if (preferences.getInt("recent", -1) >= 0) {
            bookData.size + 1
        } else {
            bookData.size
        }
    }
}

class Book(
    private val binding: BookItemBinding,
    private val context: Context,
    private val preferences: SharedPreferences
) : RecyclerView.ViewHolder(binding.root) {

    private var recent = preferences.getInt("recent", -1)

    @SuppressLint("UseCompatLoadingForDrawables")
    fun bind(bookData: List<BookData>) {
        binding.apply {

            if (adapterPosition <= bookData.size) {
                if (recent >= 0 && recent <= bookData.size) {
                    if (adapterPosition == 0) {
                        with(context).load(bookData[recent].imageUrl)
                            .listener(object: RequestListener<Drawable> {
                                override fun onLoadFailed(
                                    e: GlideException?,
                                    model: Any?,
                                    target: Target<Drawable>?,
                                    isFirstResource: Boolean
                                ): Boolean {
                                    Log.d("ji", "fail")
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
                            .placeholder(android.R.drawable.progress_indeterminate_horizontal)
                            .error(android.R.drawable.stat_notify_error)
                            .into(imageView)

                        imageView.setOnClickListener {
                            val intent = Intent(context, PdfViewActivity::class.java)
                            intent.putExtra("bookData", bookData[recent])
                            context.startActivity(intent)
                        }
                    }
                    if (adapterPosition > 0){
                        with(context).load(bookData[adapterPosition - 1].imageUrl)
                            .listener(object: RequestListener<Drawable> {
                                override fun onLoadFailed(
                                    e: GlideException?,
                                    model: Any?,
                                    target: Target<Drawable>?,
                                    isFirstResource: Boolean
                                ): Boolean {
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

                        imageView.setOnClickListener {
                            with(preferences.edit()) {
                                this.putInt("recent", adapterPosition - 1)
                                this.apply()
                            }
                            val intent = Intent(context, PdfViewActivity::class.java)
                            intent.putExtra("bookData", bookData[adapterPosition - 1])
                            context.startActivity(intent)
                        }
                    }
                } else {
                    with(context).load(bookData[adapterPosition].imageUrl)
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

                    imageView.setOnClickListener {
                        with(preferences.edit()) {
                            this.putInt("recent", adapterPosition)
                            this.apply()
                        }

                        val intent = Intent(context, PdfViewActivity::class.java)
                        intent.putExtra("bookData", bookData[adapterPosition])
                        context.startActivity(intent)
                    }
                }
            }
        }
    }
}