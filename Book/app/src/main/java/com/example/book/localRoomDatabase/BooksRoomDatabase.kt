package com.example.book.localRoomDatabase

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@TypeConverters(value = [BookDataListConverters::class])
@Database(entities = [Books::class], version = 4, exportSchema = true)
abstract class BooksRoomDatabase : RoomDatabase() {
    abstract fun userDao(): BooksDao
}
