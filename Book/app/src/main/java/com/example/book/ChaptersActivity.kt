package com.example.book

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.book.databinding.ActivityChaptersBinding

class ChaptersActivity : AppCompatActivity() {

    private var binding: ActivityChaptersBinding? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chapters)
        binding = ActivityChaptersBinding.bind(findViewById(R.id.rootActivityChapters))

        var isCheckedLoved = intent.getBooleanExtra("isCheckedLoved", false)
        val bookData = intent.getParcelableExtra<BookData?>("bookData")
        val currentChapter = intent.getIntExtra("currentChapter", -1)

        binding?.apply {

            icBackC.setOnClickListener {
                val intent = Intent()
                intent.putExtra("isCheckedLoved", isCheckedLoved)
                setResult(RESULT_OK, intent)
                onBackPressed()
            }

            if (isCheckedLoved) {
                icLovedC.setImageResource(R.drawable.loved_filled)
            }

            icLovedC.setOnClickListener {
                if (isCheckedLoved) {
                    isCheckedLoved = false
                    icLovedC.setImageResource(R.drawable.loved)
                } else {
                    isCheckedLoved = true
                    icLovedC.setImageResource(R.drawable.loved_filled)
                }
            }

            val layoutManager = LinearLayoutManager(
                this@ChaptersActivity,
                LinearLayoutManager.VERTICAL,
                false
            )

            chaptersRecyclerView.layoutManager = layoutManager
            val adapter = ChaptersRecyclerViewAdapter(bookData!!, currentChapter) { newChapter ->
                val intent = Intent()
                intent.putExtra("newChapter", newChapter)
                intent.putExtra("isCheckedLoved", isCheckedLoved)
                setResult(RESULT_OK, intent)
                onBackPressed()
            }
            chaptersRecyclerView.adapter = adapter
        }
    }
}