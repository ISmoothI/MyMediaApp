package com.example.mediatracker.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.mediatracker.data.movie.Genre
import com.example.mediatracker.data.movie.Movie
import com.example.mediatracker.data.movie.MovieDao
import com.example.mediatracker.data.movie.MovieGenreCrossRef

@Database(entities = [Movie::class, Genre::class, MovieGenreCrossRef::class], version = 1, exportSchema = false)
abstract class MediaDatabase: RoomDatabase() {
    abstract fun movieDao(): MovieDao

    companion object {
        @Volatile
        private var Instance: MediaDatabase? = null

        fun getDatabase(context: Context): MediaDatabase {
            // if the Instance is not null, return it, otherwise create a new database instance.
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, MediaDatabase::class.java, "movie-database")
                    .build()
                    .also { Instance = it }
            }
        }
    }
}