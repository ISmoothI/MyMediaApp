package com.example.mediatracker.data.movie

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieViewModel @Inject constructor(private val movieRepository: MovieRepository): ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isLoadingAnother = MutableStateFlow(false)
    val isLoadingAnother: StateFlow<Boolean> = _isLoadingAnother

    private val _search = MutableStateFlow("")

    private val _entries = MutableStateFlow<List<Movie>>(emptyList())
    val entries: StateFlow<List<Movie>> = _entries

    private val _movieWithGenresEntry = MutableStateFlow(MovieWithGenres(Movie(movieId = -1), mutableListOf<Genre>()))
    val movieWithGenresEntry: StateFlow<MovieWithGenres> = _movieWithGenresEntry

    private val _genres = MutableStateFlow<List<Genre>>(emptyList())
    val genres: StateFlow<List<Genre>> = _genres

    private val _genresWithMoviesEntries = MutableStateFlow<List<GenreWithMovies>>(emptyList())
    val genresWithMoviesEntries: StateFlow<List<GenreWithMovies>> = _genresWithMoviesEntries

    private val _genreWithMoviesEntry = MutableStateFlow(GenreWithMovies(Genre(), emptyList()))
    val genreWithMoviesEntry: StateFlow<GenreWithMovies> = _genreWithMoviesEntry


    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val getMoviesByTitle: Flow<List<Movie>> = _search
        .debounce(300)
        .distinctUntilChanged()
        .flatMapLatest { search ->
            movieRepository.getMoviesByTitle("%$search%")
        }

    fun setSearch(search: String) {
        _search.value = search
    }

    fun upsertMovie(movie: Movie) {
        viewModelScope.launch {
            _isLoading.value = true
            movieRepository.upsertMovie(movie)
            _isLoading.value = false
        }
    }

    fun deleteMovie(movie: Movie) {
        viewModelScope.launch {
            movieRepository.deleteMovie(movie)
        }
    }

    fun getAllMovies() {
        viewModelScope.launch {
            _isLoading.value = true
            val result = movieRepository.getAllMovies()
            _entries.value = result
            _isLoading.value = false
        }
    }

    fun getMoviesOnWatchlist() {
        viewModelScope.launch {
            _isLoading.value = true
            val result = movieRepository.getMoviesOnWatchlist()
            _entries.value = result
            _isLoading.value = false
        }
    }

    fun updateRating(movieId: Long, rating: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            movieRepository.updateRating(movieId, rating)
            _movieWithGenresEntry.value = movieRepository.getMovieWithGenres(movieId)
            _isLoading.value = false
        }
    }

    fun getMovieWithGenres(movieId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            if(movieId == 0.toLong()) {
                _movieWithGenresEntry.value = MovieWithGenres(Movie(), mutableListOf())
            }
            else {
                _movieWithGenresEntry.value = movieRepository.getMovieWithGenres(movieId)
            }
            _isLoading.value = false
        }
    }

    fun getGenres() {
        viewModelScope.launch {
            _isLoadingAnother.value = true
            val result = movieRepository.getGenres()
            _genres.value = result
            _isLoadingAnother.value = false
        }
    }

    fun addGenreWithMovie(genre: Genre, movieId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            movieRepository.addGenreWithMovie(genre, movieId)
            _isLoading.value = false
        }
    }

    fun upsertMovieGenreCrossRef(movieGenreCrossRef: MovieGenreCrossRef) {
        viewModelScope.launch {
            _isLoading.value = true
            movieRepository.upsertMovieGenreCrossRef(movieGenreCrossRef)
            _isLoading.value = false
        }
    }

    fun getGenreWithMovies(genreId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = movieRepository.getGenreWithMovies(genreId)
            _genreWithMoviesEntry.value = result
            _isLoading.value = false
        }
    }

    fun getGenresWithMovies() {
        viewModelScope.launch {
            _isLoadingAnother.value = true
            val result = movieRepository.getGenresWithMovies()
            _genresWithMoviesEntries.value = result
            _isLoadingAnother.value = false
        }
    }
}