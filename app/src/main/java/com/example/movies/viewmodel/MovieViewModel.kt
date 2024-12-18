package com.example.movies.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movies.model.Movie
import com.example.movies.db.MovieDao
import kotlinx.coroutines.launch

class MovieViewModel(private val movieDao: MovieDao) : ViewModel() {

    val allMovies: LiveData<List<Movie>> = movieDao.getAllMovies()

    fun insert(movie: Movie) {
        viewModelScope.launch {
            movieDao.insert(movie)
        }
    }

    fun deleteMovies(movies: List<Movie>) {
        viewModelScope.launch {
            movieDao.deleteMovies(movies)
        }
    }
}
