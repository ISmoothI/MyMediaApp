package com.example.mediatracker.data.movie

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MovieRepository @Inject constructor(private val movieDao: MovieDao) {

    suspend fun upsertMovie(movie: Movie) {
        movieDao.upsertMovie(movie)
    }

    suspend fun deleteMovie(movie: Movie) {
        movieDao.deleteMovie(movie)
    }

    suspend fun getMovie(movieId: Long): Movie {
        return movieDao.getMovie(movieId)
    }

    suspend fun getMoviesOnWatchlist(): List<Movie> {
        return movieDao.getMoviesOnWatchlist()
    }

    fun getMoviesByTitle(search: String): Flow<List<Movie>> = movieDao.getMoviesByTitle(search)

    suspend fun getAllMovies(): List<Movie> {
        return movieDao.getAllMovies()
    }

    suspend fun upsertGenre(genre: Genre) {
        movieDao.upsertGenre(genre)
    }

    suspend fun getGenres(): List<Genre> {
        return movieDao.getGenres()
    }

    suspend fun upsertMovieGenreCrossRef(movieGenreCrossRef: MovieGenreCrossRef) {
        movieDao.upsertMovieGenreCrossRef(movieGenreCrossRef)
    }

    suspend fun addGenreWithMovie(genre: Genre, movieId: Long) {
        movieDao.addGenreWithMovie(genre, movieId)
    }

    suspend fun getMoviesWithGenres(): List<MovieWithGenres> {
        return movieDao.getMoviesWithGenres()
    }

    suspend fun getMovieWithGenres(movieId: Long): MovieWithGenres {
        return movieDao.getMovieWithGenres(movieId)
    }

    suspend fun getGenresWithMovies(): List<GenreWithMovies> {
        return movieDao.getGenresWithMovies()
    }

    suspend fun getGenreWithMovies(genreId: Long): GenreWithMovies {
        return movieDao.getGenreWithMovies(genreId)
    }

    suspend fun updateRating(movieId: Long, rating: Int) {
        movieDao.updateRating(movieId, rating)
    }

}