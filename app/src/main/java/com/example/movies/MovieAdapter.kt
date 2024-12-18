package com.example.movies

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class MovieAdapter(private val onDeleteClickListener:(MovieList)->Unit) : RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {

    private var moviesList = listOf<MovieList>()
    private val selectedMovies = mutableSetOf<String>() // Для хранения ID выбранных фильмов

    override fun onCreateViewHolder(parent : ViewGroup, viewType : Int): MovieViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_movie, parent, false)
        return MovieViewHolder(view)
    }

    override fun onBindViewHolder(holder : MovieViewHolder, position : Int) {
        holder.bind(moviesList[position])
    }

    override fun getItemCount(): Int = moviesList.size

    inner class MovieViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView : TextView = itemView.findViewById(R.id.text_title)
        private val yearTextView : TextView = itemView.findViewById(R.id.text_release_date)
        private val posterImageView : ImageView = itemView.findViewById(R.id.image_poster)
        private val checkBox : CheckBox = itemView.findViewById(R.id.checkbox_select)

        init {
            itemView.setOnClickListener {
                onDeleteClickListener(moviesList[adapterPosition])
            }

            checkBox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    selectedMovies.add(moviesList[adapterPosition].imdbID)
                } else {
                    selectedMovies.remove(moviesList[adapterPosition].imdbID)
                }
            }
        }

        fun bind(movie : MovieList) {
            titleTextView.text = movie.Title
            yearTextView.text = movie.Year
            Glide.with(itemView.context).load(movie.Poster).into(posterImageView)

            // Установка состояния чекбокса
            checkBox.isChecked = selectedMovies.contains(movie.imdbID)
        }
    }

    fun submitList(newMoviesList : List<MovieList>) {
        moviesList = newMoviesList
        notifyDataSetChanged()
    }

    fun getSelectedIds(): List<String> {
        return selectedMovies.toList()
    }
}