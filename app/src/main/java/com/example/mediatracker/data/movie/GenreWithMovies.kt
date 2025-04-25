package com.example.mediatracker.data.movie

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class GenreWithMovies(
    @Embedded val genre: Genre,
    @Relation(
        parentColumn = "genreId",
        entityColumn = "movieId",
        associateBy = Junction(MovieGenreCrossRef::class)
    )
    val movie: List<Movie>
)