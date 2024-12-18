package com.example.movies

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.movies.api.ApiClient.apiService
import com.example.movies.api.ApiFetcher
import com.example.movies.api.repository.MovieRepository
import com.example.movies.api.response.ApiResult
import com.example.movies.databinding.ActivityMoviesBrowseBinding
import com.example.movies.ui.MoviesBrowseAdapter


class MoviesBrowseActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMoviesBrowseBinding

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
    }

    private lateinit var apiFetcher: ApiFetcher

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Инициализация View Binding
        binding = ActivityMoviesBrowseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Инициализация Fetcher и MovieRepository
        apiFetcher = ApiFetcher(MovieRepository(apiService))

        // Настройка RecyclerView.

        binding.recyclerView.layoutManager = if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            GridLayoutManager(this, 2) // Два столбца в альбомной ориентации
        } else {
            LinearLayoutManager(this) // Один столбец в портретной ориентации
        }

//        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        // Получаем данные из Intent
        val title = intent.getStringExtra("title") ?: ""
        val year = intent.getStringExtra("year")

        // Загрузка данных из API с использованием полученных значений title и year.
        loadMovies(title, year)

        // Обработка нажатия на элемент списка.
        binding.recyclerView.adapter = MoviesBrowseAdapter(emptyList()) { movie ->
            val intent = Intent(this, AddMovieActivity::class.java)
            intent.putExtra("movie", movie) // Передаем объект Movie в AddMovieActivity
            startActivity(intent)
        }
    }

    private fun loadMovies(title: String, year: String?) {
        // Показать ProgressBar перед началом загрузки
        binding.progressBar.visibility = View.VISIBLE

        // Вызов метода вашего репозитория для поиска фильмов с использованием title и year.
        apiFetcher.fetchMoviesList(title, year) { result ->


            when (result) {
                is ApiResult.Success -> {
                    binding.progressBar.visibility = View.GONE // Скрыть ProgressBar после завершения загрузки
                    // Фильтруем список, чтобы удалить null значения
                    val filteredMovies = result.movies.filterNotNull()

                    if (filteredMovies.isNotEmpty()) {
                        binding.recyclerView.visibility = View.VISIBLE
                        binding.recyclerView.adapter = MoviesBrowseAdapter(filteredMovies) { movie ->
                            val intent = Intent() // Создаем новый Intent, но не указываем целевую активность
                            intent.putExtra("movie", movie) // Передаем объект Movie обратно
                            setResult(RESULT_OK, intent) // Устанавливаем результат для возврата
                            finish() // Закрываем текущую активность и возвращаемся к AddMovieActivity
                        }
                    } else {
                        binding.recyclerView.visibility = View.GONE
                        showError("No movies found")
                    }
                }
                is ApiResult.Error -> {
                    binding.progressBar.visibility = View.GONE // Скрыть ProgressBar после завершения загрузки
                    Log.e("GMD", result.message)
                    showError(result.message)
                }
            }
        }
    }


    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}