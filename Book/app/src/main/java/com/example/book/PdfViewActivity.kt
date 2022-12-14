package com.example.book

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.book.databinding.PdfViewBinding
import com.google.firebase.storage.FirebaseStorage

@Suppress("NAME_SHADOWING")
class PdfViewActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private var binding: PdfViewBinding? = null
    private var bookData: BookData? = null
    private var position: Int? = null

    private var isConnectedToInternet: Boolean? = null
    private var connectionLiveData: ConnectionLiveData? = null

    private companion object {
        const val TAG = "PDF_VIEW_TAG"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pdf_view)
        binding = PdfViewBinding.bind(findViewById(R.id.rootBookDetails))

        bookData = intent.getParcelableExtra("bookData")
        position = intent.getIntExtra("position", -1)
        val sharedPref = getSharedPreferences("Book", MODE_PRIVATE)
        val lovedBooks = sharedPref.getString("lovedBooks", "")

        var isCheckedLoved = false
        for (i in lovedBooks!!.indices) {
            if (position!!.toString() == lovedBooks[i].toString()) {
                isCheckedLoved = true
                binding!!.icLoved.setImageResource(R.drawable.loved_filled)
            }
        }

        binding?.apply {
            var isCheckedSettings = false
            icSettings.setOnClickListener {
                if (isCheckedSettings) {
                    isCheckedSettings = false
                    icSettings.setImageResource(R.drawable.settings)
                    bottomLayout.visibility = View.VISIBLE
                    bottomLayoutSettings.visibility = View.GONE
                } else {
                    isCheckedSettings = true
                    icSettings.setImageResource(R.drawable.settings_filled)
                    bottomLayout.visibility = View.GONE
                    bottomLayoutSettings.visibility = View.VISIBLE
                }

            }

            btnWhiteColor.setOnClickListener {
                with(sharedPref.edit()) {
                    putBoolean("isNightMode", false)
                    apply()
                }
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            btnDarkColor.setOnClickListener {
                with(sharedPref.edit()) {
                    putBoolean("isNightMode", true)
                    apply()
                }
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }

            icLoved.setOnClickListener {
                if (isCheckedLoved) {
                    icLoved.setImageResource(R.drawable.loved)
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
                    icLoved.setImageResource(R.drawable.loved_filled)
                    isCheckedLoved = true

                    val lovedBooks = sharedPref.getString("lovedBooks", "")

                    with(sharedPref.edit()) {
                        putString("lovedBooks", lovedBooks.plus("$position"))
                        apply()
                    }
                }
            }
        }

        checkNetworkConnection()
        recyclerViewInitialization()
    }

    private fun recyclerViewInitialization() {
        val layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.VERTICAL,
            false
        )

        binding?.apply {
            chaptersRecyclerView.layoutManager = layoutManager
            val adapter = ChaptersRecyclerViewAdapter(bookData!!)
            chaptersRecyclerView.adapter = adapter
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

            currentPageBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, p2: Boolean) {
                    pdfView.positionOffset = progress.toFloat() / 100F
                }

                override fun onStartTrackingTouch(p0: SeekBar?) {
                }

                override fun onStopTrackingTouch(p0: SeekBar?) {
                }
            })
        }

        if (bookData!!.name != "Не удалось загрузить данные с сервера(") {
            bookUrl?.loadBookFromUrl(sharedPref!!, position!!, chaptersPage, chaptersName)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun String.loadBookFromUrl(
        sharedPref: SharedPreferences,
        position: Int,
        chaptersPage: ArrayList<Int>?,
        chaptersName: ArrayList<String>?
    ) {
        Log.d(TAG, this)
        val reference = FirebaseStorage.getInstance().getReferenceFromUrl(this)
        reference.getBytes(Constants.MAX_BYTES_PDF)
            .addOnSuccessListener { bytes ->
                Log.d(TAG, "loadBookFromUrl: Pdf got from url successfully")

                binding!!.pdfView.fromBytes(bytes)
                    .defaultPage(sharedPref.getInt("lastPage of ${position + 1}", 0))
                    .swipeHorizontal(true)
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

                        binding!!.currentPageBar.progress = sharedPref.getFloat("seekBar ${position + 1}", 0F).toInt()

                        for (i in 0..chaptersPage!!.size) {
                            if (i + 1 < chaptersPage.size) {
                                if (currentPage >= chaptersPage[i] && currentPage < chaptersPage[i + 1]) {
                                    binding!!.tvChapterName.text = chaptersName!![i]
                                }

                                if (currentPage < chaptersPage[0]) {
                                    binding!!.tvChapterName.text = "Йоьхье"
                                }
                            } else if (currentPage >= chaptersPage[chaptersPage.size - 1]) {
                                binding!!.tvChapterName.text = chaptersName!![chaptersName.size - 1]
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
                            icSettings.visibility = View.VISIBLE
                            icChapters.visibility = View.VISIBLE
                            icLoved.visibility = View.VISIBLE
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

    @Suppress("SENSELESS_COMPARISON", "UNCHECKED_CAST")
    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val chaptersPage = bookData?.chaptersPage
        val pageCount = binding?.pdfView?.pageCount?.toFloat()

        if (chaptersPage != null && pageCount!! > 0F) {
            binding?.pdfView?.jumpTo(chaptersPage[position] - 1)
        } else {
            Log.d(TAG, "chapters page/ pageCount = null")
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }
}