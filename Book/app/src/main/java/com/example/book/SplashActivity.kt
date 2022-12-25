package com.example.book

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.example.book.localRoomDatabase.Books
import com.example.book.localRoomDatabase.BooksDao
import com.example.book.localRoomDatabase.BooksRoomDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@Suppress("DEPRECATION")
class SplashActivity : AppCompatActivity() {

    private var bookData: List<BookData>? = null
    private var localDatabase: BooksRoomDatabase? = null
    private var booksDao: BooksDao? = null

    private var isConnectedToInternet: Boolean? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        initializeDatabase()

        isConnectedToInternet = isNetworkConnected()
        if (isConnectedToInternet == true) {
            getBooksInformation()
            Handler().postDelayed({
                if (bookData == null) {
                    var childrenCount = 0
                    CoroutineScope(Dispatchers.IO).launch {
                        if (booksDao?.isExists() == true) {
                            childrenCount = booksDao?.get()!!.childrenCount
                            CoroutineScope(Dispatchers.Main).launch {
                                Toast.makeText(
                                    this@SplashActivity,
                                    "Не удалось загузить данные с сервера, возможно слишком плохое интернет подключение." +
                                            " Приложение загружается в оффлайн режиме",
                                    Toast.LENGTH_LONG
                                ).show()

                                startAnimationWithIntent(childrenCount)
                            }
                        } else {
                            CoroutineScope(Dispatchers.Main).launch {
                                Toast.makeText(
                                    this@SplashActivity,
                                    "Не удалось загузить данные с сервера, возможно слишком плохое интернет подключение." +
                                            "Приложение нуждается в интернет подключении хотя бы при первом запуске",
                                    Toast.LENGTH_LONG
                                ).show()
                                Handler().postDelayed({
                                    finishAndRemoveTask()
                                }, 1500)
                            }
                        }
                    }
                }
            }, 7000)
        } else {
            Handler().postDelayed({
                var childrenCount = 0

                CoroutineScope(Dispatchers.IO).launch {
                    if (booksDao?.isExists() == true) {
                        childrenCount = booksDao!!.get().childrenCount

                        CoroutineScope(Dispatchers.Main).launch {
                            Toast.makeText(
                                this@SplashActivity,
                                "Нет подключения к интернету. Приложение загружается в оффлайн режиме",
                                Toast.LENGTH_LONG
                            ).show()

                            startAnimationWithIntent(childrenCount)
                        }
                    } else {
                        CoroutineScope(Dispatchers.Main).launch {
                            Toast.makeText(
                                this@SplashActivity,
                                "Нет подключения к интернету. Приложение нуждается в интернет подключении хотя бы при первом запуске",
                                Toast.LENGTH_LONG
                            ).show()
                            Handler().postDelayed({
                                finishAndRemoveTask()
                            }, 1500)
                        }
                    }
                }
            }, 5000)
        }
    }

    private fun initializeDatabase() {
        CoroutineScope(Dispatchers.IO).launch {
            localDatabase = Room.databaseBuilder(
                applicationContext,
                BooksRoomDatabase::class.java, "books-database"
            ).build()
            booksDao = localDatabase?.userDao()
        }
    }

    private fun isNetworkConnected(): Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.activeNetworkInfo != null && cm.activeNetworkInfo!!.isConnected
    }

    @Suppress("UNCHECKED_CAST")
    private fun getBooksInformation() {
        val ref = FirebaseDatabase.getInstance().reference

        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val childrenCount = (snapshot.childrenCount).toInt()

                fun saveData(): List<BookData> {
                    val arrayBookData = arrayListOf<BookData>()
                    for (i in 0 until snapshot.childrenCount) {
                        snapshot.child("${i + 1}").apply {
                            arrayBookData.add(BookData(
                                this.child("Name").value.toString(),
                                this.child("Writer").value.toString(),
                                this.child("Book url").value.toString(),
                                this.child("Image url").value.toString(),
                                this.child("Chapters page").value as ArrayList<Int>?,
                                this.child("Chapters name").value as ArrayList<String>?
                            ))
                        }
                    }

                    bookData = arrayBookData

                    return bookData!!
                }

                val bookData = saveData()
                saveToLocalDatabase(bookData, childrenCount)
                startAnimationWithIntent(childrenCount)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d(TAG, error.message)
            }
        })
    }

    private fun saveToLocalDatabase(
        bookData: List<BookData>,
        childrenCount: Int
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            booksDao?.insertAll(Books(null, bookData, childrenCount))
        }
    }

    private companion object {
        const val TAG = "SPLASH_ACTIVITY_TAG"
    }

    private fun startAnimationWithIntent(ChildrenCount: Int) {
        val hehamTvSplash = findViewById<ImageView>(R.id.heham_tv_splash)
        val scaleDownAnim = AnimationUtils.loadAnimation(this, R.anim.scale_down_to_center)

        Handler().postDelayed({
            val intent = Intent(this@SplashActivity, MainScreenActivity::class.java)
            intent.putExtra("childrenCount", ChildrenCount)
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }, 350)

        CoroutineScope(Dispatchers.Default).launch {
            hehamTvSplash.startAnimation(scaleDownAnim)
            scaleDownAnim.setAnimationListener(object: AnimationListener {
                override fun onAnimationStart(p0: Animation?) {
                }

                override fun onAnimationEnd(p0: Animation?) {
                    hehamTvSplash.visibility = View.GONE
                }

                override fun onAnimationRepeat(p0: Animation?) {
                }
            })
        }
    }
}