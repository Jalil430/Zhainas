package com.example.book.localRoomDatabase

import androidx.room.TypeConverter
import com.example.book.BookDataList
import com.google.gson.Gson

class BookDataListConverters {

    @TypeConverter
    fun fromBookDataToJSON(bookDataList: BookDataList): String {
        return Gson().toJson(bookDataList)
    }
    @TypeConverter
    fun fromJSONToBookData(json: String): BookDataList {
        return Gson().fromJson(json,BookDataList::class.java)
    }

}