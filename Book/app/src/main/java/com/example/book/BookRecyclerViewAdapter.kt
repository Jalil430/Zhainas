package com.example.book

import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.Drawable
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide.*
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.book.databinding.BookItemBinding
import com.example.book.databinding.RecentBookItemBinding
import java.io.File

class BookRecyclerViewAdapter(
    private val bookData: List<BookData>,
    private val preferences: SharedPreferences,
    private val progress: ArrayList<Float>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var bookItemBinding: BookItemBinding? = null
    private var bookRecentBinding: RecentBookItemBinding? = null
    private val recent = preferences.getInt("recent", -1)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        bookItemBinding = BookItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        bookRecentBinding = RecentBookItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return when (viewType) {
            0 -> {
                RecentBook(bookRecentBinding!!, parent.context, preferences, progress)
            }
            else -> {
                Book(bookItemBinding!!, parent.context, preferences, progress)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        return when (holder.itemViewType) {
            0 -> {
                val recentBook: RecentBook = holder as RecentBook
                recentBook.bind0(bookData)
            }
            1 -> {
                val book: Book = holder as Book
                book.bind1(bookData)
            }
            else -> {
                val book: Book = holder as Book
                book.bind2(bookData)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (recent >= 0 && recent <= bookData.size && position == 0) {
            0
        } else if (recent >= 0 && recent <= bookData.size && position > 0) {
            1
        } else {
            2
        }
    }

    override fun getItemCount(): Int {
        return if (recent >= 0 && recent <= bookData.size) {
            bookData.size + 1
        } else {
            bookData.size
        }
    }
}

class RecentBook(
    private val binding: RecentBookItemBinding,
    private val context: Context,
    preferences: SharedPreferences,
    private val progress: ArrayList<Float>
) : RecyclerView.ViewHolder(binding.root) {

    private val recent = preferences.getInt("recent", -1)

    @SuppressLint("UseCompatLoadingForDrawables")
    fun bind0 (bookData: List<BookData>) {
        binding.apply {
            val book = bookData[recent]

            with(context).load(book.imageUrl)
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
                        imageBarR.visibility = View.GONE
                        return false
                    }
                })
                .placeholder(android.R.drawable.progress_indeterminate_horizontal)
                .error(android.R.drawable.stat_notify_error)
                .into(imageViewR)

            bookCardR.setOnClickListener {
                val intent = Intent(context, PdfViewActivity::class.java)
                intent.putExtra("bookData", book)
                intent.putExtra("position", recent)
                context.startActivity(intent)
            }

            tvBookNameR.text = book.name
            tvWriterR.text = book.writer

            if (progress[recent] > 0f) {
                tvProgressR.visibility = View.VISIBLE
                progressBookR.visibility = View.VISIBLE
                "${progress[recent].toInt()}%".also { tvProgressR.text = it }
                progressBookR.progress = progress[recent].toInt()
            }
            if (progress[recent] == 100f) {
                tvProgressR.visibility = View.VISIBLE
                progressBookR.visibility = View.VISIBLE
                "${progress[recent].toInt()}%".also { tvProgressR.text = it }
                progressBookR.progress = progress[recent].toInt()
                tvContinue.text = "Прочитано"
            }
        }
    }
}

class Book(
    private val binding: BookItemBinding,
    private val context: Context,
    private val preferences: SharedPreferences,
    private val progress: ArrayList<Float>
) : RecyclerView.ViewHolder(binding.root) {

    @SuppressLint("UseCompatLoadingForDrawables")
    fun bind1(bookData: List<BookData>) {

        val book = bookData[adapterPosition - 1]

        binding.apply {
            with(context).load(book.imageUrl)
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

            bookCard.setOnClickListener {
                with(preferences.edit()) {
                    this.putInt("recent", adapterPosition - 1)
                    this.apply()
                }
                val intent = Intent(context, PdfViewActivity::class.java)
                intent.putExtra("bookData", book)
                intent.putExtra("position", adapterPosition - 1)
                context.startActivity(intent)
            }

            tvBookName.text = book.name
            tvWriter.text = book.writer

//            if (isBookDownloaded("${book.name}.pdf")) {
//                downloadDone.visibility = View.VISIBLE
//            } else {
//                downloadDone.visibility = View.GONE
//            }

            val sharedPref = context.getSharedPreferences("Book", MODE_PRIVATE)
            val hasRead = sharedPref.getBoolean("hasRead $adapterPosition", false)

            if (progress[adapterPosition - 1] > 0f && hasRead) {
                tvProgress.visibility = View.VISIBLE
                progressBook.visibility = View.VISIBLE
                "Прочитать: ${progress[adapterPosition - 1].toInt()}%".also { tvProgress.text = it }
                progressBook.progress = progress[adapterPosition - 1].toInt()
            } else {
                tvProgress.visibility = View.GONE
                progressBook.visibility = View.GONE
            }
            if (progress[adapterPosition - 1] == 100f) {
                tvProgress.visibility = View.VISIBLE
                progressBook.visibility = View.VISIBLE
                "Прочитано: ${progress[adapterPosition - 1].toInt()}%".also { tvProgress.text = it }
                progressBook.progress = progress[adapterPosition - 1].toInt()
            }
        }
    }

    fun bind2(bookData: List<BookData>) {
        binding.apply {
            val book = bookData[adapterPosition]

            with(context).load(book.imageUrl)
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
                with(preferences.edit()) {
                    this.putInt("recent", adapterPosition)
                    this.apply()
                }

                val intent = Intent(context, PdfViewActivity::class.java)
                intent.putExtra("bookData", book)
                intent.putExtra("position", adapterPosition)
                context.startActivity(intent)
            }

            tvBookName.text = book.name
            tvWriter.text = book.writer

//            if (isBookDownloaded("${book.name}.pdf")) {
//                downloadDone.visibility = View.VISIBLEik

//            } else {
//                downloadDone.visibility = View.GONE
//            }
        }
    }

    private fun isBookDownloaded(name: String): Boolean {
        val downloadsFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        downloadsFolder.mkdirs()

        val filePath = downloadsFolder.path + "/" + name
        val file = File(filePath)
        return file.exists()
    }
}
