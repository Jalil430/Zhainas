package com.example.book

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.book.databinding.ChaptersItemBinding

class ChaptersRecyclerViewAdapter(
    private val bookData: BookData,
    private val currentChapter: Int,
    private val callback: (Int) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var binding: ChaptersItemBinding? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        binding = ChaptersItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ChaptersItem(binding!!, parent.context, callback)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val chaptersItem: ChaptersItem = holder as ChaptersItem
        chaptersItem.bind(bookData, currentChapter)
    }

    override fun getItemCount(): Int {
        return bookData.chaptersName!!.size
    }
}

class ChaptersItem(
    private val binding: ChaptersItemBinding,
    private val context: Context,
    private val callback: (Int) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    @Suppress("DEPRECATION")
    fun bind(bookData: BookData, currentChapter: Int) {
        binding.apply {
            val chaptersName = bookData.chaptersName

            if (adapterPosition == currentChapter && currentChapter >= 0) {
                tvChapter.setTextColor(context.resources.getColor(R.color.foreground))
            }

            tvChapter.text = chaptersName!![adapterPosition]

            chaptersCard.setOnClickListener {
                callback.invoke(adapterPosition)
            }
        }
    }
}