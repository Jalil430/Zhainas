package com.example.book.localRoomDatabase

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.book.BookData

@Entity
data class Books(
    @PrimaryKey(autoGenerate = true) val id: Long?,
    @ColumnInfo(name = "book_data") val bookData: List<BookData>,
    @ColumnInfo(name = "children_count") val childrenCount: Int
)