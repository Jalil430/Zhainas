package com.example.book

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.book.databinding.ChaptersItemBinding

class ChaptersRecyclerViewAdapter(
    private val bookData: BookData
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var binding: ChaptersItemBinding? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        binding = ChaptersItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ChaptersList(binding!!)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val lovedBooksList: ChaptersList = holder as ChaptersList
        lovedBooksList.bind(bookData)
    }

    override fun getItemCount(): Int {
        return bookData.chaptersName.size
    }
}

class ChaptersList(
    private val binding: ChaptersItemBinding
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(bookData: BookData) {
        binding.tvChapter.text = bookData.chaptersName[adapterPosition]
    }
}