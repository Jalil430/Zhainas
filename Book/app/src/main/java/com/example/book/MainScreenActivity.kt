package com.example.book

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.transition.Slide
import android.transition.Transition
import android.transition.Transition.TransitionListener
import android.transition.TransitionManager
import android.view.Gravity
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.book.databinding.MainScreenBinding
import com.example.book.localRoomDatabase.BooksDao
import com.example.book.localRoomDatabase.BooksRoomDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainScreenActivity : AppCompatActivity() {

    private var binding: MainScreenBinding? = null
    private var whichPage = 0

    private var bookData: List<BookData>? = null
    private var localDatabase: BooksRoomDatabase? = null
    private var booksDao: BooksDao? = null
    private var childrenCount = 0

    @SuppressLint("MissingInflatedId")
    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_screen)
        binding = MainScreenBinding.bind(findViewById(R.id.rootMainScreen))

        whichPage = 1
        childrenCount = intent.getIntExtra("childrenCount", 0)
        initializeDatabaseAndBookData()

        binding?.apply {
            homePage.setOnClickListener {
                if (whichPage != 1) {
                    whichPage = 1
                    homePage.setImageResource(R.drawable.home_page_filled)
                    lovedPage.setImageResource(R.drawable.loved_page)
                    onResume()
                }
            }
            lovedPage.setOnClickListener {
                if (whichPage != 2) {
                    whichPage = 2
                    homePage.setImageResource(R.drawable.home_page)
                    lovedPage.setImageResource(R.drawable.loved_page_filled)
                    onResume()
                }
            }
        }
    }

    private fun initializeDatabaseAndBookData() {
        CoroutineScope(Dispatchers.IO).launch {
            localDatabase = Room.databaseBuilder(
                applicationContext,
                BooksRoomDatabase::class.java, "books-database"
            ).build()
            booksDao = localDatabase?.userDao()

            bookData = booksDao!!.get().bookData
        }
    }

    override fun onResume() {
        super.onResume()
        binding!!.progressBar.visibility = View.VISIBLE

        val preferences = getPreferences(MODE_PRIVATE)
        val sharedPref = getSharedPreferences("Book", MODE_PRIVATE)
        val lovedBooks = sharedPref.getString("lovedBooks", "")

        val progress = arrayListOf<Float>()
        for (i in 0..childrenCount) {
            progress.add(
                sharedPref.getFloat("${i + 1}", 0F)
            )
        }

        if (bookData != null) {
            initializeView(bookData!!, preferences, lovedBooks, progress)
        } else {
            onResume()
        }
    }

    private fun initializeView(
        bookData: List<BookData>,
        preferences: SharedPreferences,
        lovedBooks: String?,
        progress: ArrayList<Float>
    ) {
        binding?.apply {
            progressBar.visibility = View.GONE

            val transitionStart: Transition = Slide(Gravity.START)
            transitionStart.duration = 400
            transitionStart.addTarget(binding!!.booksLayout)

            val transitionEnd: Transition = Slide(Gravity.END)
            transitionEnd.duration = 400
            transitionEnd.addTarget(binding!!.lovedLayout)
            bookPage.setOnClickListener {
                val recent = preferences.getInt("recent", -1)
                val intent = Intent(this@MainScreenActivity, PdfViewActivity::class.java)

                if (recent >= 0 && recent <= bookData.size) {
                    intent.putExtra("bookData", bookData[recent])
                    intent.putExtra("position", recent)
                    intent.putExtra("progress", progress[recent])
                    startActivity(intent)
                } else {
                    intent.putExtra("bookData", bookData[0])
                    intent.putExtra("position", 0)
                    intent.putExtra("progress", progress[0])
                    startActivity(intent)
                }
            }

            if (whichPage == 1) {
                TransitionManager.beginDelayedTransition(rootLovedView, transitionEnd)
                lovedLayout.visibility = View.GONE
                TransitionManager.beginDelayedTransition(rootMainScreen, transitionStart)
                booksLayout.visibility = View.VISIBLE

                val layoutManager = LinearLayoutManager(
                    this@MainScreenActivity,
                    LinearLayoutManager.VERTICAL,
                    false
                )

                booksRecyclerView.layoutManager = layoutManager
                val adapter = BookRecyclerViewAdapter(bookData, preferences, progress)
                booksRecyclerView.adapter = adapter
            } else {
                TransitionManager.beginDelayedTransition(rootMainScreen, transitionStart)
                booksLayout.visibility = View.GONE
                TransitionManager.beginDelayedTransition(rootLovedView, transitionEnd)
                lovedLayout.visibility = View.VISIBLE

                val layoutManager = LinearLayoutManager(
                    this@MainScreenActivity,
                    LinearLayoutManager.VERTICAL,
                    false
                )

                lovedRecyclerView.layoutManager = layoutManager
                val adapter = LovedRecyclerViewAdapter(bookData, lovedBooks!!, progress)
                lovedRecyclerView.adapter = adapter
            }
        }
    }
}