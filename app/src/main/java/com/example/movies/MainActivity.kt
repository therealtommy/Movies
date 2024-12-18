package com.example.movies

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var movieViewModel: MovieViewModel
    private lateinit var movieAdapter: MovieAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        movieAdapter = MovieAdapter { movie -> deleteMovie(movie) }
        findViewById<RecyclerView>(R.id.recycler_view).apply {
            adapter = movieAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }

        movieViewModel = ViewModelProvider(this).get(MovieViewModel::class.java)

        movieViewModel.movies.observe(this) { movies ->
            movieAdapter.submitList(movies)
            findViewById<TextView>(R.id.empty_message).visibility =
                if (movies.isEmpty()) View.VISIBLE else View.GONE
        }

        findViewById<FloatingActionButton>(R.id.fab_add).setOnClickListener {
            startActivity(Intent(this, AddActivity::class.java))
        }

        findViewById<Button>(R.id.button_delete_selected).setOnClickListener {
            val selectedIds = movieAdapter.getSelectedIds()
            if (selectedIds.isNotEmpty()) {
                movieViewModel.deleteMovies(selectedIds)
            }
        }
    }

    private fun deleteMovie(movie: MovieList) {
        // Логика удаления фильма из списка, если необходимо
    }
}