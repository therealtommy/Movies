package com.example.movies


import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.movies.api.ApiClient.apiService
import com.example.movies.api.ApiFetcher
import com.example.movies.api.repository.MovieRepository
import com.example.movies.api.response.ApiResult
import com.example.movies.databinding.ActivityAddMovieBinding
import com.example.movies.db.MovieDatabase
import com.example.movies.extenstion.fetchImage
import com.example.movies.model.Movie
import com.example.movies.viewmodel.MovieViewModel
import com.example.movies.viewmodel.MovieViewModelFactory
import java.util.Calendar

class AddMovieActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddMovieBinding
    private lateinit var apiFetcher: ApiFetcher
    private lateinit var getMovieLauncher: ActivityResultLauncher<Intent>
    private var isUserInput: Boolean = false

    // Переменная movie с пользовательским сеттером
    private var _movie: Movie? = null
    var movie: Movie?
        get() = _movie
        set(value) {
            _movie = value
            updateButtonState() // Обновляем состояние кнопки при изменении movie
            if (value==null){
                Log.e("GMD", "Gawd mothafuckin dayum")
                binding.poster.setImageDrawable(null)}
            else{
                binding.editTextTitle.setText(value.title)
                setYearSpinnerValue(value.year)
                binding.poster.fetchImage(value.poster)
            }
        }

    // ViewModel для работы с базой данных
    private val movieViewModel: MovieViewModel by viewModels {
        MovieViewModelFactory(MovieDatabase.getDatabase(application).movieDao())
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable("movie", movie) // Сохраняем переменную movie в Bundle
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Инициализация View Binding
        binding = ActivityAddMovieBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Изменяем ориентацию LinearLayout в зависимости от положения экрана
        val orientation = resources.configuration.orientation
        val params = binding.formLayout.layoutParams as LinearLayout.LayoutParams
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            binding.root.orientation = LinearLayout.HORIZONTAL // Устанавливаем горизонтальную ориентацию
            params.weight = 1.5f
            binding.formLayout.layoutParams = params
        } else {
            binding.root.orientation = LinearLayout.VERTICAL // Устанавливаем вертикальную ориентацию
            params.weight = 3.5f
            binding.formLayout.layoutParams = params
        }

        // Инициализация Fetcher
        val movieRepository = MovieRepository(apiService)
        apiFetcher = ApiFetcher(movieRepository)

        // Настройка Spinner для выбора года
        setupYearSpinner()

        // Восстановление состояния переменной movie
        if (savedInstanceState != null) {
            movie = savedInstanceState.getParcelable("movie", Movie::class.java) // Восстанавливаем переменную movie
            Log.e("GOOOOD", "$movie")
        }


        getMovieLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                movie = result.data?.getParcelableExtra("movie", Movie::class.java)
            }
        }


        // Обработка нажатия на кнопку Browse для поиска фильмов
        binding.buttonSearch.setOnClickListener {
            val intent = Intent(this, MoviesBrowseActivity::class.java)

            val title: String = binding.editTextTitle.text.toString()
            val selectedYear = binding.spinnerYear.selectedItem.toString()
            val year: String? = if (selectedYear == "Any") null else selectedYear

            binding.editTextTitle.clearFocus()
            binding.spinnerYear.clearFocus()

            // Передаем title и year в Intent
            intent.putExtra("title", title)
            intent.putExtra("year", year)

            getMovieLauncher.launch(intent) // Используем лончер для запуска активности
        }


        // Устанавливаем слушатель для обработки пользовательского ввода
        binding.editTextTitle.setOnFocusChangeListener { _, hasFocus ->
            isUserInput = hasFocus // Устанавливаем флаг в true, когда поле ввода в фокусе
        }

        binding.spinnerYear.setOnFocusChangeListener { _, hasFocus ->
            isUserInput = hasFocus // Устанавливаем флаг в true, когда поле ввода в фокусе
        }

        // Сброс переменной movie при изменении текста в полях ввода.
        binding.editTextTitle.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (isUserInput){
                    movie = null // Сбрасываем movie при изменении названия.
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.spinnerYear.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (isUserInput) {
                    movie = null // Сбрасываем movie при изменении года.
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun setupYearSpinner() {
        val years = getYearsList() // Получаем список годов с добавленным значением "Any"
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, years)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerYear.adapter = adapter
        setYearSpinnerValue(Calendar.getInstance().get(Calendar.YEAR).toString())
    }

    private fun getYearsList(): List<String> {
        val currentYear = Calendar.getInstance().get(Calendar.YEAR) + 5
        return listOf("Any") + (1870..currentYear).map { it.toString() }.reversed() // Возвращаем список годов
    }

    private fun getYearIndex(year: String): Int {

        val years = getYearsList() // Получаем список годов

        return years.indexOf(year) // Ищем индекс года в списке.
    }

    private fun setYearSpinnerValue(year: String){
        val yearIndex = getYearIndex(year)
        if (yearIndex >= 0) {
            binding.spinnerYear.setSelection(yearIndex) // Устанавливаем выбор в спиннере
        } else {
            showError("Year not found in the list") // Обработка случая, если год не найден
        }
    }

    private fun fetchMovieDetails() {
        binding.editTextTitle.clearFocus()
        binding.spinnerYear.clearFocus()
        val title: String = binding.editTextTitle.text.toString()
        val selectedYear = binding.spinnerYear.selectedItem.toString()
        val year: String? = if (selectedYear == "Any") null else selectedYear

        binding.progressBar.visibility = View.VISIBLE
        apiFetcher.fetchMovie(title, year) { result ->
            when (result) {
                is ApiResult.Success -> {
                    binding.progressBar.visibility = View.GONE
                    if (result.movies.isNotEmpty()) {
                        movie = result.movies.first() // Присваиваем значение переменной movie
                    } else {
                        showError("No movies found") // Обработка случая, если список фильмов пуст
                    }
                }
                is ApiResult.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Log.e("GMD", result.message)
                    showError(result.message) // Показать сообщение об ошибке через Toast
                }
            }
        }
    }

    private fun addMovieToDatabase() {
        if (movie != null){
            movieViewModel.insert(movie!!) // Добавляем фильм в базу данных.
            Toast.makeText(this, "Movie ${movie!!.title} added successfully!", Toast.LENGTH_SHORT).show()
            finish() // Закрываем активити и возвращаемся к MoviesToWatchActivity.
        }
    }

    private fun updateButtonState() {
        Log.i("GMD","update button state")
        if (movie == null) {
            binding.buttonAction.text = getString(R.string.search_movie) // Меняем текст кнопки на SEARCH MOVIE.
            binding.buttonAction.setOnClickListener {
                fetchMovieDetails() // Если movie пусто, выполняем поиск фильма.
            }
        } else {
            binding.buttonAction.text = getString(R.string.add_movie) // Меняем текст кнопки на ADD MOVIE.
            binding.buttonAction.setOnClickListener {
                addMovieToDatabase() // Если movie не пусто, добавляем фильм в базу данных.
            }
        }
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val REQUEST_CODE = 1001 // Или любое другое значение
    }

}
