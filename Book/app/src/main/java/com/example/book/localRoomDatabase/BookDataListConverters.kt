package com.example.book.localRoomDatabase

import androidx.room.TypeConverter
import com.example.book.BookData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class BookDataListConverters {

    @TypeConverter
    fun fromBookDataToJSON(bookDataList: List<BookData>): String {
        return Gson().toJson(bookDataList)
    }
    @TypeConverter
    fun fromJSONToBookData(json: String): List<BookData> {
        val bookDataList = object : TypeToken<List<BookData>>() {}.type
        return Gson().fromJson(json, bookDataList)
    }
}