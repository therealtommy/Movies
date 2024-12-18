package com.example.movies.api.response


import com.example.movies.model.Movie

sealed class ApiResult {
    data class Success(val movies: List<Movie?>) : ApiResult()
    data class Error(val message: String) : ApiResult()
}
