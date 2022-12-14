package com.example.book

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
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
    private var bookData = listOf(
        BookData("Не удалось загрузить данные с сервера(", "Не удалось загрузить данные с сервера(", "Не удалось загрузить данные с сервера(", "Не удалось загрузить данные с сервера(", arrayListOf(999), arrayListOf(""),0F),
        BookData("Не удалось загрузить данные с сервера(", "Не удалось загрузить данные с сервера(", "Не удалось загрузить данные с сервера(", "Не удалось загрузить данные с сервера(", arrayListOf(999), arrayListOf(""),0F))
    private var whichPage = 0

    private var isConnectedToInternet: Boolean? = null
    private var connectionLiveData: ConnectionLiveData? = null

    @SuppressLint("MissingInflatedId")
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

        val sharedPref = getSharedPreferences("Book", MODE_PRIVATE)
        val isNightMode = sharedPref.getBoolean("isNightMode", false)
        if (isNightMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        whichPage = 1
        checkNetworkConnection()

        binding?.apply {
            refresh.setOnClickListener {
                onResume()
            }

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

    private fun checkNetworkConnection() {
        connectionLiveData = ConnectionLiveData(application)

        connectionLiveData?.observe(this) { isConnected ->
            val intent = Intent(this, ConnectionLost::class.java)

            if (!isConnected) {
                isConnectedToInternet = false
                intent.putExtra("isConnected", false)
                startActivity(intent)
                finish()
            } else {
                isConnectedToInternet = true
                onResume()
            }
        }
    }

    override fun onResume() {
        super.onResume()

        val preferences = getPreferences(MODE_PRIVATE)
        val sharedPref = getSharedPreferences("Book", MODE_PRIVATE)
        val lovedBooks = sharedPref.getString("lovedBooks", "")

        binding!!.progressBar.visibility = View.VISIBLE
        binding!!.refresh.visibility = View.GONE

        if (isConnectedToInternet == true) {
            getBooksInformation(sharedPref)
        }

        binding!!.bookPage.setOnClickListener {
            val recent = preferences.getInt("recent", -1)
            val intent = Intent(this@MainScreenActivity, PdfViewActivity::class.java)

            if (recent >= 0 && recent <= bookData.size) {
                if (bookData.size > 1) {
                    intent.putExtra("bookData", bookData[recent])
                    intent.putExtra("position", recent)
                    startActivity(intent)
                } else {
                    Handler().postDelayed({
                        intent.putExtra("bookData", bookData[recent])
                        intent.putExtra("position", recent)
                        startActivity(intent)
                    },7000)
                }
            } else {
                if (bookData.size > 1) {
                    intent.putExtra("bookData", bookData[0])
                    intent.putExtra("position", 0)
                    startActivity(intent)
                } else {
                    Handler().postDelayed({
                        intent.putExtra("bookData", bookData[0])
                        intent.putExtra("position", 0)
                        startActivity(intent)
                    },7000)
                }
            }
        }

        if (whichPage == 1) {
            binding!!.booksLayout.visibility = View.VISIBLE
            binding!!.lovedLayout.visibility = View.GONE

            if (isConnectedToInternet == true) {
                Handler().postDelayed({
                    binding!!.progressBar.visibility = View.GONE
                    binding!!.refresh.visibility = View.VISIBLE

                    val layoutManager = LinearLayoutManager(
                        this,
                        LinearLayoutManager.VERTICAL,
                        false
                    )

                    binding?.apply {
                        booksRecyclerView.layoutManager = layoutManager
                        val adapter = BookRecyclerViewAdapter(bookData, preferences)
                        booksRecyclerView.adapter = adapter
                    }
                }, 7000)
            }
        } else {
            binding!!.booksLayout.visibility = View.GONE
            binding!!.lovedLayout.visibility = View.VISIBLE

            if (isConnectedToInternet == true) {
                Handler().postDelayed({
                    binding!!.progressBar.visibility = View.GONE
                    binding!!.refresh.visibility = View.VISIBLE

                    val layoutManager = LinearLayoutManager(
                        this,
                        LinearLayoutManager.VERTICAL,
                        false
                    )

                    binding?.apply {
                        lovedRecyclerView.layoutManager = layoutManager
                        val adapter = LovedRecyclerViewAdapter(bookData, lovedBooks!!)
                        lovedRecyclerView.adapter = adapter
                    }
                }, 7000)
            }
        }
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