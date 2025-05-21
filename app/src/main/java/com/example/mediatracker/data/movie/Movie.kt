package com.example.mediatracker.data.movie

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movies")
data class Movie(
    @PrimaryKey(autoGenerate = true) var movieId: Long = 0,
    @ColumnInfo(name = "poster") var poster: String = "",
    @ColumnInfo(name = "title") var title: String = "",
    @ColumnInfo(name = "year") var year: Int = 0,
    @ColumnInfo(name = "director") var director: String = "",
    @ColumnInfo(name = "body") var body: String = "",
    @ColumnInfo(name = "runtime") var runtime: Int = 0,
    @ColumnInfo(name = "tagline") var tagline: String = "",
    @ColumnInfo(name = "rating") var rating: Int = 0,
    @ColumnInfo(name = "note") var note: String = "",
    @ColumnInfo(name = "watchlist") var watchlist: Boolean = false,
    @ColumnInfo(name = "completed") var completed: Boolean = false,
)
