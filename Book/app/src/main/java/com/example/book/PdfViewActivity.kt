package com.example.book

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.book.databinding.PdfViewBinding
import com.google.firebase.storage.FirebaseStorage

class PdfViewActivity : AppCompatActivity() {

    private var binding: PdfViewBinding? = null

    private companion object {
        const val TAG = "PDF_VIEW_TAG"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pdf_view)
        binding = PdfViewBinding.bind(findViewById(R.id.rootBookDetails))

        val bookUrl = intent.getStringExtra("bookUrl")
        val bookName = intent.getStringExtra("bookName")

        binding?.apply {
            textView.text = bookName
        }

        bookUrl?.loadBookFromUrl()
    }

    private fun String.loadBookFromUrl() {
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
                    .load()
                binding!!.progressBar.visibility = View.GONE
                binding!!.progressBar2.visibility = View.GONE
            }
            .addOnFailureListener { e ->
                Log.d(TAG, "loadBookFromUrl: Failed to get pdf due to ${e.message}")
                binding!!.progressBar.visibility = View.GONE
                binding!!.progressBar2.visibility = View.GONE
            }
    }
}