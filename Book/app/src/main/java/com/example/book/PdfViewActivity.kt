package com.example.book

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.example.book.databinding.PdfViewBinding
import com.google.firebase.storage.FirebaseStorage

class PdfViewActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private var binding: PdfViewBinding? = null
    private var bookData: BookData? = null

    private companion object {
        const val TAG = "PDF_VIEW_TAG"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pdf_view)
        binding = PdfViewBinding.bind(findViewById(R.id.rootBookDetails))

        bookData = intent.getParcelableExtra("bookData")
        val bookUrl = bookData?.bookUrl
        val bookName = bookData?.name
        val chaptersName = bookData?.chaptersName

        binding?.apply {
            textView.text = bookName
            imageView2.setOnClickListener {
                onBackPressed()
            }

            ArrayAdapter(
                this@PdfViewActivity,
                android.R.layout.simple_spinner_item,
                chaptersName!!
            ).also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinner.adapter = adapter
            }
            spinner.onItemSelectedListener = this@PdfViewActivity
        }

        bookUrl?.loadBookFromUrl()
    }

    @SuppressLint("SetTextI18n")
    private fun String.loadBookFromUrl() {
        Log.d(TAG, this)
        val reference = FirebaseStorage.getInstance().getReferenceFromUrl(this)
        reference.getBytes(Constants.MAX_BYTES_PDF)
            .addOnSuccessListener { bytes ->
                Log.d(TAG, "loadBookFromUrl: Pdf got from url successfully")

                binding!!.pdfView.fromBytes(bytes)
                    .swipeHorizontal(false)
                    .onPageChange {page, pageCount ->
                        val currentPage = page + 1
                        binding!!.textView2.text = "$currentPage/$pageCount"
                        Log.d(TAG, "loadBookFromUrl: $currentPage/$pageCount")
                    }
                    .onError { t ->
                        Log.d(TAG, "loadBookFromUrl: ${t.message}")
                    }
                    .onPageError { page, t ->
                        Log.d(TAG, "loadBookFromUrl: ${t.message} on page: $page")
                    }
                    .onLoad {
                        binding!!.pageBar.visibility = View.GONE
                        binding!!.pdfBar.visibility = View.GONE
                    }
                    .load()
            }
            .addOnFailureListener { e ->
                Log.d(TAG, "loadBookFromUrl: Failed to get pdf due to ${e.message}")
                binding!!.pageBar.visibility = View.GONE
                binding!!.pdfBar.visibility = View.GONE
            }
    }

    @Suppress("SENSELESS_COMPARISON", "UNCHECKED_CAST")
    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val chaptersPage = bookData?.chaptersPage
        val pageCount = binding?.pdfView?.pageCount?.toFloat()

        if (chaptersPage != null && pageCount!! > 0F) {
            binding?.pdfView?.jumpTo(chaptersPage[position])
//            val page = chaptersPage[position] / pageCount
//            Log.d("dffrrr", "${chaptersPage[position]} / $pageCount = ${chaptersPage[position] / pageCount}")
//            binding?.pdfView?.positionOffset = page
        } else {
            Log.d(TAG, "chapters page/ pageCount = null")
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }
}