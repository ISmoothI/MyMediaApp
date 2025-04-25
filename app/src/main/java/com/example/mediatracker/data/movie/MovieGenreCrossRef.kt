package com.example.mediatracker.data.movie

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(
    tableName = "movieGenreCrossRefs",
    primaryKeys = ["movieId", "genreId"]
)
data class MovieGenreCrossRef(
    @ColumnInfo(name = "movieId") var movieId: Long,
    @ColumnInfo(name = "genreId") var genreId: Long
)