package com.example.book.localRoomDatabase

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface BooksDao {
    @Insert
    fun insertAll(vararg books: Books)

    @Delete
    fun delete(books: Books)

    @Query("SELECT * FROM books")
    fun get(): Books
}