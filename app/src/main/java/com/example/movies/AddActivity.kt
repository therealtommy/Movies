package com.example.movies

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider

class AddActivity : AppCompatActivity() {

    private lateinit var movieViewModel: MovieViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)

        movieViewModel = ViewModelProvider(this).get(MovieViewModel::class.java)

        findViewById<Button>(R.id.button_search).setOnClickListener {
            // Открытие SearchActivity для поиска фильмов.
            startActivity(Intent(this, SearchActivity::class.java))
        }

        findViewById<Button>(R.id.button_add_movie).setOnClickListener {
            // Логика добавления фильма в БД.
            val title = findViewById<EditText>(R.id.edit_title).text.toString()
            val releaseDate = findViewById<EditText>(R.id.edit_release_date).text.toString()
            val posterUrl = "" // Здесь должен быть URL постера.

            if (title.isNotEmpty()) {
                val newMovie = Movie(title = title, releaseDate = releaseDate, posterUrl = posterUrl)
                movieViewModel.addMovie(newMovie)
                finish() // Закрыть активность после добавления.
            }
        }
    }
}