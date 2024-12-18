package com.example.movies

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class MovieViewModel(application: Application) : AndroidViewModel(application) {
    private val movieDao = MovieDatabase.getDatabase(application).movieDao()

    private val _movies = MutableLiveData<List<Movie>>()
    val movies: LiveData<List<Movie>> get() = _movies

    init {
        viewModelScope.launch {
            _movies.value = movieDao.getAllMovies()
        }
    }

    fun addMovie(movie: Movie) {
        viewModelScope.launch {
            movieDao.insert(movie)
            _movies.value = movieDao.getAllMovies()
        }
    }

    fun deleteMovies(ids: List<Int>) {
        viewModelScope.launch {
            movieDao.deleteMoviesByIds(ids)
            _movies.value = movieDao.getAllMovies()
        }
    }
}