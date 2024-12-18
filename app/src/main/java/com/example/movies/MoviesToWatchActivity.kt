package com.example.movies


import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.movies.db.MovieDatabase
import com.example.movies.viewmodel.MovieViewModel
import com.example.movies.viewmodel.MovieViewModelFactory
import com.example.movies.databinding.ActivityMoviesToWatchBinding
import com.example.movies.ui.MoviesToWatchAdapter

class MoviesToWatchActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMoviesToWatchBinding

    // Используем делегат by viewModels для получения экземпляра MovieViewModel
    private val movieViewModel: MovieViewModel by viewModels {
        MovieViewModelFactory(MovieDatabase.getDatabase(application).movieDao())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Инициализация View Binding
        binding = ActivityMoviesToWatchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Настройка RecyclerView.
        val layoutManager = if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            GridLayoutManager(this, 2) // Два столбца в альбомной ориентации
        } else {
            LinearLayoutManager(this) // Один столбец в портретной ориентации
        }

        binding.recyclerView.layoutManager = layoutManager

        // Наблюдение за изменениями в списке фильмов.
        movieViewModel.allMovies.observe(this, Observer { movies ->
            if (movies.isEmpty()) {
                binding.recyclerView.visibility = View.GONE
                binding.emptyView.visibility = View.VISIBLE
            } else {
                binding.recyclerView.visibility = View.VISIBLE
                binding.emptyView.visibility = View.GONE

                binding.recyclerView.adapter = MoviesToWatchAdapter(movies)
            }
        })

        // Обработка нажатия на FAB для добавления нового фильма.
        binding.fabAddMovie.setOnClickListener {
            val intent = Intent(this, AddMovieActivity::class.java)
            startActivity(intent) // Запускаем новую активность
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_movies_to_watch, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_delete -> {
                deleteSelectedMovies()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun deleteSelectedMovies() {
        val selectedMovies = (binding.recyclerView.adapter as? MoviesToWatchAdapter)?.getSelectedMovies()
        if (!selectedMovies.isNullOrEmpty()) {
            movieViewModel.deleteMovies(selectedMovies)
            // Обновите список фильмов после удаления, если это необходимо.
            // Например, можно вызвать метод обновления списка или перезагрузить данные.
        }
    }
}