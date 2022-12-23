package com.example.book

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.book.databinding.PdfViewBinding
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.io.FileOutputStream

@Suppress("NAME_SHADOWING", "DEPRECATION")
class PdfViewActivity : AppCompatActivity() {

    private var binding: PdfViewBinding? = null
    private var bookData: BookData? = null
    private var position: Int? = null
    private var currentChapter = -1
    private var isCheckedLoved = false

    private var isConnectedToInternet: Boolean? = null

    private companion object {
        const val TAG = "PDF_VIEW_TAG"
    }

    @Suppress("DEPRECATION")
    @SuppressLint("UseCompatLoadingForDrawables")
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

        isConnectedToInternet = isNetworkConnected()

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
                if (isBookDownloaded("$bookName.pdf") && !isConnectedToInternet!!) {
                    if (ContextCompat.checkSelfPermission(
                            this@PdfViewActivity,
                            android.Manifest.permission.READ_EXTERNAL_STORAGE
                        ) == PackageManager.PERMISSION_GRANTED) {

                        loadBookFromDownloadsFolder(
                            sharedPref,
                            position,
                            chaptersPage,
                            chaptersName,
                            isTracking
                        )

                    } else {
                        requestStoragePermissionLauncher(
                            bookUrl!!, sharedPref, chaptersPage, chaptersName, isTracking, 2
                        ).launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    }

                } else {
                    if (ContextCompat.checkSelfPermission(
                            this@PdfViewActivity,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                        ) == PackageManager.PERMISSION_GRANTED) {

                        bookUrl?.loadBookFromUrl(
                            sharedPref!!,
                            position!!,
                            chaptersPage,
                            chaptersName,
                            isTracking
                        )

                    } else {
                        requestStoragePermissionLauncher(
                            bookUrl!!, sharedPref, chaptersPage, chaptersName, isTracking, 1
                        ).launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    }
                }
            }
        }
    }

    private fun isBookDownloaded(name: String): Boolean {
        val downloadsFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        downloadsFolder.mkdirs()

        val filePath = downloadsFolder.path + "/" + name
        val file = File(filePath)
        return file.exists()
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

                if (lovedBooks?.contains(position!!.toChar()) == false) {
                    with(sharedPref.edit()) {
                        putString("lovedBooks", lovedBooks.plus("$position"))
                        apply()
                    }
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

    private fun isNetworkConnected(): Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.activeNetworkInfo != null && cm.activeNetworkInfo!!.isConnected
    }

    private fun requestStoragePermissionLauncher(
        bookUrl: String,
        sharedPref: SharedPreferences,
        chaptersPage: ArrayList<Int>?,
        chaptersName: ArrayList<String>?,
        isTracking: Boolean,
        code: Int
    ) = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted && code == 1) {
                bookUrl.loadBookFromUrl(
                    sharedPref,
                    position!!,
                    chaptersPage,
                    chaptersName,
                    isTracking
                )
            } else if (isGranted && code == 2) {
                loadBookFromDownloadsFolder(sharedPref, position, chaptersPage, chaptersName, isTracking)
            }
        }

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

                initializePdfViewer(bytes, sharedPref, position, chaptersPage, chaptersName, isTracking)
                saveToDownloadsFolder(bytes)
            }
            .addOnFailureListener { e ->
                Log.d(TAG, "loadBookFromUrl: Failed to get pdf due to ${e.message}")
                binding!!.pdfBar.visibility = View.GONE
            }
    }

    @SuppressLint("SetTextI18n")
    private fun initializePdfViewer(
        bytes: ByteArray,
        sharedPref: SharedPreferences,
        position: Int,
        chaptersPage: ArrayList<Int>?,
        chaptersName: ArrayList<String>?,
        isTracking: Boolean
    ) {
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
                        if (binding!!.pdfView.positionOffset * 100f <= 0f) {
                            putFloat("${position + 1}", binding!!.pdfView.positionOffset * 100f + 1f)
                        } else {
                            putFloat("${position + 1}", binding!!.pdfView.positionOffset * 100f)
                        }
                        apply()
                    }
                    putInt("lastPage of ${position + 1}", currentPage - 1)
                    putFloat("seekBar ${position + 1}", binding!!.pdfView.positionOffset * 100f)
                    apply()
                }

                if (!isTracking) {
                    binding!!.currentPageBar.progress = sharedPref.getFloat("seekBar ${position + 1}", 0f).toInt()
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
                val hasRead = sharedPref.getBoolean("hasRead ${position + 1}", false)
                if (!hasRead) {
                    with(sharedPref.edit()) {
                        putBoolean("hasRead ${position + 1}", true)
                        apply()
                    }
                }

                binding?.apply {
                    icChapters.visibility = View.VISIBLE
                    currentPageBar.visibility = View.VISIBLE
                    pdfBar.visibility = View.GONE
                }
            }
            .load()
    }

    private fun saveToDownloadsFolder(
        bytes: ByteArray
    ) {
        val nameWithExtension = "${bookData?.name}.pdf"

        try {
            val downloadsFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            downloadsFolder.mkdirs()

            val filePath = downloadsFolder.path + "/" + nameWithExtension

            val out = FileOutputStream(filePath)
            out.write(bytes)
            out.close()
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d(TAG, "Error while saving book" + e.message!!)
        }
    }

    private fun loadBookFromDownloadsFolder(
        sharedPref: SharedPreferences,
        position: Int?,
        chaptersPage: ArrayList<Int>?,
        chaptersName: ArrayList<String>?,
        isTracking: Boolean
    ) {
        val nameWithExtension = "${bookData?.name}.pdf"

        try {
            val downloadsFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            downloadsFolder.mkdirs()

            val filePath = downloadsFolder.path + "/" + nameWithExtension
            val file = File(filePath)
            val bytes = org.apache.commons.io.FileUtils.readFileToByteArray(file)

            initializePdfViewer(bytes!!, sharedPref, position!!, chaptersPage, chaptersName, isTracking)

        } catch (e: Exception) {
            e.printStackTrace()
            Log.d(TAG, "Error while saving book" + e.message!!)
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

                if (isCheckedLoved != data?.getBooleanExtra("isCheckedLoved", false) !!) {
                    isCheckedLoved = data.getBooleanExtra("isCheckedLoved", false)
                    icLovedClickListener("B")
                }
            }
        }
    }
}