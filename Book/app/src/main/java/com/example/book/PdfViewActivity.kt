package com.example.book

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.example.book.databinding.PdfViewBinding
import com.google.firebase.storage.FirebaseStorage

class PdfViewActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private var binding: PdfViewBinding? = null
    private var bookData: BookData? = null
    private var position: Int? = null

    private companion object {
        const val TAG = "PDF_VIEW_TAG"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pdf_view)
        binding = PdfViewBinding.bind(findViewById(R.id.rootBookDetails))

        bookData = intent.getParcelableExtra("bookData")
        position = intent.getIntExtra("position", -1)
        val bookUrl = bookData?.bookUrl
        val bookName = bookData?.name
        val chaptersPage = bookData?.chaptersPage
        val chaptersName = bookData?.chaptersName

        binding?.apply {
            tvBookNamePdf.text = bookName
            icBack.setOnClickListener {
                onBackPressed()
            }

            currentPageBar.visibility = View.GONE
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

        val sharedPref = getSharedPreferences("BookProgress", MODE_PRIVATE)

        bookUrl?.loadBookFromUrl(sharedPref, position!!, chaptersPage, chaptersName)
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
                        binding!!.tvPages.text = "$currentPage из $pageCount стр"
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

                        binding!!.currentPageBar.visibility = View.VISIBLE
                        binding!!.currentPageBar.progress = sharedPref.getFloat("seekBar ${position + 1}", 0F).toInt()

                        for (i in 0..chaptersPage!!.size) {
                            if (i + 1 < chaptersPage.size) {
                                if (currentPage >= chaptersPage[i] && currentPage < chaptersPage[i + 1]) {
                                    binding!!.tvChapterName.text = chaptersName!![i]
                                } else {
                                    binding!!.tvChapterName.text = "Йоьхье"
                                }
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
                        binding!!.pdfBar.visibility = View.GONE
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