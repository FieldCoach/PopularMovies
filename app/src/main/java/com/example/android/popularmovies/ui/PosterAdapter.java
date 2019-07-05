package com.example.android.popularmovies.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.android.popularmovies.R;
import com.example.android.popularmovies.data.Movie;
import com.example.android.popularmovies.utilities.PosterDiffCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AaronC on 7/26/2017.
 */
public class PosterAdapter extends RecyclerView.Adapter<PosterAdapter.MoviePosterAdapterViewHolder>{

    private static final String TAG = "PopM";

    private Context context;
    private ArrayList<Movie> movieArrayList = new ArrayList<>();
    private PosterAdapterClickListener listener;

    public PosterAdapter(Context context, PosterAdapterClickListener listener){
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MoviePosterAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.movie_list_item, parent,false);
        return new MoviePosterAdapterViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MoviePosterAdapterViewHolder holder, int position) {
        holder.onBind(movieArrayList.get(position));
    }

    @Override
    public int getItemCount() {
        if (null == movieArrayList) return 0;
        return movieArrayList.size();
    }

    public void updateMoviesList(List<Movie> newMoviesList) {
        DiffUtil.DiffResult diffResult =
                DiffUtil.calculateDiff(new PosterDiffCallback(this.movieArrayList, newMoviesList));

        movieArrayList.clear();
        movieArrayList.addAll(newMoviesList);
        diffResult.dispatchUpdatesTo(this);
    }

    class MoviePosterAdapterViewHolder extends RecyclerView.ViewHolder {
        final TextView tvTitle;
        final ImageView moviePoster;

        MoviePosterAdapterViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(v ->
                    listener.onPosterClick(movieArrayList.get(getAdapterPosition()).getId()));
            tvTitle = itemView.findViewById(R.id.tv_movie_title);
            moviePoster = itemView.findViewById(R.id.iv_movie_poster);
        }

        void onBind(Movie movie) {
            tvTitle.setText(movie.getTitle());
            //Load the image into the ImageView
            Glide.with(context)
                    .load(movie.getPosterUriString())
                    .into(moviePoster);
        }
    }

    public interface PosterAdapterClickListener {
        void onPosterClick(int movieId);
    }
}