package com.example.book

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.book.databinding.MainScreenBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainScreenActivity : AppCompatActivity() {

    private val viewModel: SplashScreenViewModel by viewModels()
    private var binding: MainScreenBinding? = null
    private var bookData = listOf(BookData("df","writer", "fd", "fd", arrayListOf(1,3), arrayListOf("", "2"), 0F))

    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().apply {
            setKeepOnScreenCondition {
                viewModel.isLoading.value
            }
        }
        setContentView(R.layout.main_screen)
        binding = MainScreenBinding.bind(findViewById(R.id.rootMainScreen))
    }

    override fun onResume() {
        super.onResume()

        val preferences = getPreferences(MODE_PRIVATE)
        val sharedPref = getSharedPreferences("BookProgress", MODE_PRIVATE)

        getBooksInformation(sharedPref)
        Handler().postDelayed({
            val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            binding?.apply {
                recyclerView.layoutManager = layoutManager
                val adapter = BookRecyclerViewAdapter(bookData, preferences)
                recyclerView.adapter = adapter
            }
        }, 5000)
    }

    @Suppress("UNCHECKED_CAST")
    private fun getBooksInformation(sharedPref: SharedPreferences) {
        val ref = FirebaseDatabase.getInstance().reference

        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val book1: BookData
                val book2: BookData
                val book3: BookData
                val book4: BookData
                val book5: BookData
                val book6: BookData
                val book7: BookData
                val book8: BookData
                val book9: BookData
                val book10: BookData
                val book11: BookData
                snapshot.child("1").apply {
                    book1 = BookData(
                        this.child("Name").value.toString(),
                        this.child("Writer").value.toString(),
                        this.child("Book url").value.toString(),
                        this.child("Image url").value.toString(),
                        this.child("Chapters page").value as ArrayList<Int>,
                        this.child("Chapters name").value as ArrayList<String>,
                        sharedPref.getFloat("1", 0F)
                    )
                }
                snapshot.child("2").apply {
                    book2 = BookData(
                        this.child("Name").value.toString(),
                        this.child("Writer").value.toString(),
                        this.child("Book url").value.toString(),
                        this.child("Image url").value.toString(),
                        this.child("Chapters page").value as ArrayList<Int>,
                        this.child("Chapters name").value as ArrayList<String>,
                        sharedPref.getFloat("2", 0F)
                    )
                }
                snapshot.child("3").apply {
                    book3 = BookData(
                        this.child("Name").value.toString(),
                        this.child("Writer").value.toString(),
                        this.child("Book url").value.toString(),
                        this.child("Image url").value.toString(),
                        this.child("Chapters page").value as ArrayList<Int>,
                        this.child("Chapters name").value as ArrayList<String>,
                        sharedPref.getFloat("3", 0F)
                    )
                }
                snapshot.child("4").apply {
                    book4 = BookData(
                        this.child("Name").value.toString(),
                        this.child("Writer").value.toString(),
                        this.child("Book url").value.toString(),
                        this.child("Image url").value.toString(),
                        this.child("Chapters page").value as ArrayList<Int>,
                        this.child("Chapters name").value as ArrayList<String>,
                        sharedPref.getFloat("4", 0F)
                    )
                }
                snapshot.child("5").apply {
                    book5 = BookData(
                        this.child("Name").value.toString(),
                        this.child("Writer").value.toString(),
                        this.child("Book url").value.toString(),
                        this.child("Image url").value.toString(),
                        this.child("Chapters page").value as ArrayList<Int>,
                        this.child("Chapters name").value as ArrayList<String>,
                        sharedPref.getFloat("5", 0F)
                    )
                }
                snapshot.child("6").apply {
                    book6 = BookData(
                        this.child("Name").value.toString(),
                        this.child("Writer").value.toString(),
                        this.child("Book url").value.toString(),
                        this.child("Image url").value.toString(),
                        this.child("Chapters page").value as ArrayList<Int>,
                        this.child("Chapters name").value as ArrayList<String>,
                        sharedPref.getFloat("6", 0F)
                    )
                }
                snapshot.child("7").apply {
                    book7 = BookData(
                        this.child("Name").value.toString(),
                        this.child("Writer").value.toString(),
                        this.child("Book url").value.toString(),
                        this.child("Image url").value.toString(),
                        this.child("Chapters page").value as ArrayList<Int>,
                        this.child("Chapters name").value as ArrayList<String>,
                        sharedPref.getFloat("7", 0F)
                    )
                }
                snapshot.child("8").apply {
                    book8 = BookData(
                        this.child("Name").value.toString(),
                        this.child("Writer").value.toString(),
                        this.child("Book url").value.toString(),
                        this.child("Image url").value.toString(),
                        this.child("Chapters page").value as ArrayList<Int>,
                        this.child("Chapters name").value as ArrayList<String>,
                        sharedPref.getFloat("8", 0F)
                    )
                }
                snapshot.child("9").apply {
                    book9 = BookData(
                        this.child("Name").value.toString(),
                        this.child("Writer").value.toString(),
                        this.child("Book url").value.toString(),
                        this.child("Image url").value.toString(),
                        this.child("Chapters page").value as ArrayList<Int>,
                        this.child("Chapters name").value as ArrayList<String>,
                        sharedPref.getFloat("9", 0F)
                    )
                }
                snapshot.child("10").apply {
                    book10 = BookData(
                        this.child("Name").value.toString(),
                        this.child("Writer").value.toString(),
                        this.child("Book url").value.toString(),
                        this.child("Image url").value.toString(),
                        this.child("Chapters page").value as ArrayList<Int>,
                        this.child("Chapters name").value as ArrayList<String>,
                        sharedPref.getFloat("10", 0F)
                    )
                }
                snapshot.child("11").apply {
                    book11 = BookData(
                        this.child("Name").value.toString(),
                        this.child("Writer").value.toString(),
                        this.child("Book url").value.toString(),
                        this.child("Image url").value.toString(),
                        this.child("Chapters page").value as ArrayList<Int>,
                        this.child("Chapters name").value as ArrayList<String>,
                        sharedPref.getFloat("11", 0F)
                    )
                }

                bookData = listOf(
                    book1,
                    book2,
                    book3,
                    book4,
                    book5,
                    book6,
                    book7,
                    book8,
                    book9,
                    book10,
                    book11
                )
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
}