package com.example.movies.ui

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.movies.databinding.ItemMovieBinding
import com.example.movies.extenstion.fetchImage
import com.example.movies.model.Movie

class MoviesBrowseAdapter(
    private val movies: List<Movie>,
    private val onClick: (Movie) -> Unit // Добавляем обработчик клика на элемент списка
) : RecyclerView.Adapter<MoviesBrowseAdapter.MovieViewHolder>() {

    inner class MovieViewHolder(private val binding: ItemMovieBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(movie: Movie) {
            binding.textViewTitle.text = movie.title
            binding.textViewYear.text = movie.year
            binding.textViewType.text = movie.type // Предполагаем, что у вас есть поле type в классе Movie
            binding.imageViewPoster.fetchImage(movie.poster)
            binding.checkboxSelect.isVisible = false
            binding.root.setOnClickListener {
                Log.e("GMD IM THE MOVIE", "$movie")
                onClick(movie)
            } // Устанавливаем обработчик клика
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val binding = ItemMovieBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MovieViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.bind(movies[position])
    }

    override fun getItemCount(): Int {
        return movies.size
    }
}
