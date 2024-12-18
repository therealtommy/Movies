package com.example.movies

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MovieAdapter(private val onDeleteClickListener:(Movie)->Unit) : RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {

    private var moviesList = listOf<Movie>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_movie, parent, false)
        return MovieViewHolder(view)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.bind(moviesList[position])
    }

    override fun getItemCount(): Int = moviesList.size

    inner class MovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val titleTextView = itemView.findViewById<TextView>(R.id.text_title)
        private val releaseDateTextView = itemView.findViewById<TextView>(R.id.text_release_date)

        init {
            itemView.setOnClickListener {
                onDeleteClickListener(moviesList[adapterPosition])
            }
        }

        fun bind(movie: Movie) {
            titleTextView.text = movie.title
            releaseDateTextView.text = movie.releaseDate
            // Загрузка постера можно реализовать с помощью библиотеки Glide или Picasso.
        }
    }

    fun submitList(moviesListNew : List<Movie>) {
        moviesList = moviesListNew
        notifyDataSetChanged()
    }

    fun getSelectedIds(): List<Int> {
        // Реализуйте логику для получения выбранных ID фильмов.
        return emptyList() // Верните список ID выбранных фильмов.
    }
}