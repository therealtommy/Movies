package com.example.movies

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchActivity : AppCompatActivity() {

    private lateinit var searchRecyclerView: RecyclerView
    private lateinit var movieAdapterSearch : MovieAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        searchRecyclerView = findViewById(R.id.recycler_view_search_results)
        movieAdapterSearch = MovieAdapter { movie -> /* обработка выбора фильма */ }
        searchRecyclerView.adapter = movieAdapterSearch

        findViewById<Button>(R.id.button_search_movies).setOnClickListener {
            searchMovie()
        }
    }

    private fun searchMovie() {
        val titleQuery = findViewById<EditText>(R.id.edit_search_query).text.toString()
        if (titleQuery.isNotEmpty()) {
            val apiKey = Secrets.getApiKey() ?: return // Получаем ключ из Secrets
            searchMovieByTitle(titleQuery, apiKey)
        } else {
            Toast.makeText(this, "Введите название фильма", Toast.LENGTH_SHORT).show()
        }
    }

    private fun searchMovieByTitle(title:String, apiKey:String) {
        OMDbApiClient.api.getMovieByTitle(title, apiKey).enqueue(object : Callback<MovieList> {
            override fun onResponse(call : Call<MovieList>, response : Response<MovieList>) {
                if(response.isSuccessful && response.body() != null) {
                    displayData(response.body()!!)
                } else {
                    Toast.makeText(this@SearchActivity, "Фильм не найден", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call : Call<MovieList>, t : Throwable) {
                Toast.makeText(this@SearchActivity, "Ошибка при запросе", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun displayData(movie : MovieList) {
        // Обновите UI с данными о фильме
        // Например, добавьте фильм в адаптер или отобразите его в отдельной активности.
    }
}