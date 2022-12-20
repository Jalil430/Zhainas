package com.example.book

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.appcompat.app.AppCompatActivity
import com.example.book.databinding.PdfViewBinding
import com.google.firebase.storage.FirebaseStorage

@Suppress("NAME_SHADOWING")
class PdfViewActivity : AppCompatActivity() {

    private var binding: PdfViewBinding? = null
    private var bookData: BookData? = null
    private var position: Int? = null
    private var currentChapter = -1
    private var isCheckedLoved = false

    private var isConnectedToInternet: Boolean? = null
    private var connectionLiveData: ConnectionLiveData? = null

    private companion object {
        const val TAG = "PDF_VIEW_TAG"
    }

    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pdf_view)
        binding = PdfViewBinding.bind(findViewById(R.id.rootBookDetails))

        bookData = intent.getParcelableExtra("bookData")
        position = intent.getIntExtra("position", -1)
        val sharedPref = getSharedPreferences("Book", MODE_PRIVATE)
        val lovedBooks = sharedPref.getString("lovedBooks", "")

        for (i in lovedBooks!!.indices) {
            if (position!!.toString() == lovedBooks[i].toString()) {
                isCheckedLoved = true
                binding!!.icLoved.setImageResource(R.drawable.loved_filled)
            }
        }
        binding?.apply {

            icChapters.setOnClickListener {
                val intent = Intent(this@PdfViewActivity, ChaptersActivity::class.java)
                intent.putExtra("bookData", bookData)
                intent.putExtra("currentChapter", currentChapter)
                intent.putExtra("isCheckedLoved", isCheckedLoved)
                startActivityForResult(intent, 1)
            }

            icLoved.setOnClickListener {
                icLovedClickListener("A")
            }
        }

        checkNetworkConnection()
    }

    private fun icLovedClickListener(whereCalled: String) {
        val sharedPref = getSharedPreferences("Book", MODE_PRIVATE)

        if (whereCalled == "A") {
            if (isCheckedLoved) {
                binding!!.icLoved.setImageResource(R.drawable.loved)
                isCheckedLoved = false

                val lovedBooks = sharedPref.getString("lovedBooks", "")
                var temp = ""

                for (i in lovedBooks!!.indices) {
                    if (position!!.toString() != lovedBooks[i].toString()) {
                        temp += lovedBooks[i].toString()
                    }
                }
                with(sharedPref.edit()) {
                    putString("lovedBooks", temp)
                    apply()
                }
            } else {
                binding!!.icLoved.setImageResource(R.drawable.loved_filled)
                isCheckedLoved = true

                val lovedBooks = sharedPref.getString("lovedBooks", "")

                with(sharedPref.edit()) {
                    putString("lovedBooks", lovedBooks.plus("$position"))
                    apply()
                }
            }
        } else {
            if (isCheckedLoved) {
                binding!!.icLoved.setImageResource(R.drawable.loved_filled)

                val lovedBooks = sharedPref.getString("lovedBooks", "")

                with(sharedPref.edit()) {
                    putString("lovedBooks", lovedBooks.plus("$position"))
                    apply()
                }
            } else {
                binding!!.icLoved.setImageResource(R.drawable.loved)

                val lovedBooks = sharedPref.getString("lovedBooks", "")
                var temp = ""

                for (i in lovedBooks!!.indices) {
                    if (position!!.toString() != lovedBooks[i].toString()) {
                        temp += lovedBooks[i].toString()
                    }
                }
                with(sharedPref.edit()) {
                    putString("lovedBooks", temp)
                    apply()
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

    @Suppress("DEPRECATION")
    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onResume() {
        super.onResume()

        val sharedPref = getSharedPreferences("Book", MODE_PRIVATE)
        val bookUrl = bookData?.bookUrl
        val bookName = bookData?.name
        val chaptersPage = bookData?.chaptersPage
        val chaptersName = bookData?.chaptersName

        binding?.apply {
            tvBookNamePdf.text = bookName
            icBack.setOnClickListener {
                onBackPressed()
            }

            var isTracking = false
            currentPageBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
                override fun onProgressChanged(p0: SeekBar?, progress: Int, p2: Boolean) {
                    if (isTracking) {
                        pdfView.positionOffset = progress.toFloat() / 100F
                    }
                }

                override fun onStartTrackingTouch(p0: SeekBar?) {
                    isTracking = true
                    currentPageBar.thumb = resources.getDrawable(R.drawable.custom_thumb_big)
                }

                override fun onStopTrackingTouch(p0: SeekBar?) {
                    isTracking = false
                    currentPageBar.thumb = resources.getDrawable(R.drawable.custom_thumb_small)
                }
            })

            if (bookData!!.name != "Не удалось загрузить данные с сервера(") {
                bookUrl?.loadBookFromUrl(
                    sharedPref!!,
                    position!!,
                    chaptersPage,
                    chaptersName,
                    isTracking
                )
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun String.loadBookFromUrl(
        sharedPref: SharedPreferences,
        position: Int,
        chaptersPage: ArrayList<Int>?,
        chaptersName: ArrayList<String>?,
        isTracking: Boolean
    ) {
        Log.d(TAG, this)
        val reference = FirebaseStorage.getInstance().getReferenceFromUrl(this)
        reference.getBytes(Constants.MAX_BYTES_PDF)
            .addOnSuccessListener { bytes ->
                Log.d(TAG, "loadBookFromUrl: Pdf got from url successfully")

                binding!!.pdfView.fromBytes(bytes)
                    .defaultPage(sharedPref.getInt("lastPage of ${position + 1}", 0))
                    .swipeHorizontal(true)
                    .pageFling(true)
                    .onPageChange {page, pageCount ->

                        val currentPage = page + 1
                        binding!!.tvPages.text = "$currentPage/$pageCount"
                        Log.d(TAG, "loadBookFromUrl: $currentPage/$pageCount")

                        with (sharedPref.edit()) {
                            if (currentPage >= sharedPref.getInt("lastMaxPage of ${position + 1}", 0)) {
                                putInt("lastMaxPage of ${position + 1}", currentPage)
                                putFloat("${position + 1}", binding!!.pdfView.positionOffset * 100F)
                                apply()
                            }
                            putInt("lastPage of ${position + 1}", currentPage - 1)
                            putFloat("seekBar ${position + 1}", binding!!.pdfView.positionOffset * 100F)
                            apply()
                        }

                        if (!isTracking) {
                            binding!!.currentPageBar.progress = sharedPref.getFloat("seekBar ${position + 1}", 0F).toInt()
                        }

                        for (i in 0..chaptersPage!!.size) {
                            if (i + 1 < chaptersPage.size) {
                                if (currentPage >= chaptersPage[i] && currentPage < chaptersPage[i + 1]) {
                                    binding!!.tvChapterName.text = chaptersName!![i]
                                    currentChapter = i
                                }

                                if (currentPage < chaptersPage[0]) {
                                    binding!!.tvChapterName.text = ""
                                    currentChapter = -1
                                }
                            } else if (currentPage >= chaptersPage[chaptersPage.size - 1]) {
                                binding!!.tvChapterName.text = chaptersName!![chaptersName.size - 1]
                                currentChapter = chaptersName.size - 1
                            }
                        }
                    }
                    .onError { t ->
                        Log.d(TAG, "loadBookFromUrl: ${t.message}")
                    }
                    .onPageError { page, t ->
                        Log.d(TAG, "loadBookFromUrl: ${t.message} on page: $page")
                    }
                    .onLoad {
                        binding?.apply {
                            icChapters.visibility = View.VISIBLE
                            currentPageBar.visibility = View.VISIBLE
                            pdfBar.visibility = View.GONE
                        }
                    }
                    .load()
            }
            .addOnFailureListener { e ->
                Log.d(TAG, "loadBookFromUrl: Failed to get pdf due to ${e.message}")
                binding!!.pdfBar.visibility = View.GONE
            }
    }

    @Suppress("DEPRECATION")
    @Deprecated("Deprecated in Java", ReplaceWith(
        "super.onActivityResult(requestCode, resultCode, data)",
        "androidx.appcompat.app.AppCompatActivity"
    )
    )
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                val newChapter = data?.getIntExtra("newChapter", -1)

                val chaptersPage = bookData?.chaptersPage
                val pageCount = binding?.pdfView?.pageCount?.toFloat()

                if (chaptersPage != null && pageCount!! > 0F && newChapter!! >= 0) {
                    binding?.pdfView?.jumpTo(chaptersPage[newChapter] - 1)
                } else {
                    Log.d(TAG, "chapters page/ pageCount = null")
                }

                isCheckedLoved = data?.getBooleanExtra("isCheckedLoved", false) !!
                icLovedClickListener("B")
            }
        }
    }
}