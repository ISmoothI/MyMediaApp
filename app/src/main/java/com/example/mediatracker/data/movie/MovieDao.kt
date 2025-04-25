package com.example.mediatracker.data.movie

import android.media.Rating
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {

    @Insert
    fun insertAll(vararg movie: Movie)

    @Upsert
    suspend fun upsertMovie(movie: Movie)

    @Delete
    suspend fun deleteMovie(movie: Movie)

    @Query("UPDATE movies SET rating = :rating WHERE movieId = :movieId")
    suspend fun updateRating(movieId: Long, rating: Int)

    @Query("SELECT * FROM movies WHERE movieId = :movieId")
    suspend fun getMovie(movieId: Long): Movie

    @Query("SELECT * FROM movies")
    suspend fun getAllMovies(): List<Movie>

    @Query("SELECT * FROM movies WHERE title LIKE :search")
    fun getMoviesByTitle(search: String): Flow<List<Movie>>

    @Query("SELECT * FROM movies ORDER BY title ASC")
    fun getMoviesOrderedByTitle(): Flow<List<Movie>>

    @Upsert
    suspend fun upsertGenre(genre: Genre): Long

    @Query("SELECT * FROM genres")
    suspend fun getGenres(): List<Genre>

    @Upsert
    suspend fun upsertMovieGenreCrossRef(crossRef: MovieGenreCrossRef)

    @Transaction
    suspend fun addGenreWithMovie(genre: Genre, movieId: Long) {
        val genreId = upsertGenre(genre)
        upsertMovieGenreCrossRef(MovieGenreCrossRef(movieId, genreId))
    }

    @Transaction
    @Query("SELECT * FROM movies WHERE movieId = :movieId")
    suspend fun getMovieWithGenres(movieId: Long): MovieWithGenres

    @Transaction
    @Query("SELECT * FROM movies")
    suspend fun getMoviesWithGenres(): List<MovieWithGenres>

    @Transaction
    @Query("SELECT * FROM genres WHERE genreId = :genreId")
    suspend fun getGenreWithMovies(genreId: Long): GenreWithMovies

    @Transaction
    @Query("SELECT * FROM genres")
    suspend fun getGenresWithMovies(): List<GenreWithMovies>
}