package com.example.mediatracker.data.movie

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "genres")
data class Genre(
    @PrimaryKey(autoGenerate = true) var genreId: Long = 0,
    @ColumnInfo(name = "name") var name: String = ""
)