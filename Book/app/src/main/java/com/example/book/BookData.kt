package com.example.book

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class BookData(
    val name: String,
    val writer: String,
    val bookUrl: String,
    val imageUrl: String,
    val chaptersPage: ArrayList<Int>,
    val chaptersName: ArrayList<String>,
    val progress: Float
    ) : Parcelable