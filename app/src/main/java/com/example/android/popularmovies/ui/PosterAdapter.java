package com.example.android.popularmovies.ui;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.android.popularmovies.R;
import com.example.android.popularmovies.data.Movie;

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

    /**
     * Inflates the layout of the Movie list_item then returns a ViewHolder containing that layout
     * @param parent the parent ViewGroup
     * @param viewType identifies the type of view
     * @return ViewHolder containing the inflated layout
     */
    @Override
    public MoviePosterAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.movie_list_item, parent,false);
        return new MoviePosterAdapterViewHolder(itemView);
    }

    /**
     * Loads the poster image and binds it to the ImageView in the ViewHolder
     * @param holder the ViewHolder
     * @param position the position of the ViewHolder to be bound
     */
    @Override
    public void onBindViewHolder(MoviePosterAdapterViewHolder holder, int position) {
        holder.onBind(movieArrayList.get(position));
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        if (null == movieArrayList) return 0;
        return movieArrayList.size();
    }

    // TODO 6/29/2019: What is difference between this method and updateMoviesList() method? - Emre
    /**
     * Clears previous data from the ArrayList of Movies, adds new Movies, then notifies the Adapter
     * @param moviesArrayList arrayList containing all of the movies
     * @param sortBySelectionString String to inform if these are favorite movies
     */
    void setMoviesArrayList(ArrayList<Movie> moviesArrayList, String sortBySelectionString) {
        this.movieArrayList.clear();
        this.movieArrayList.addAll(moviesArrayList);
        String movieTitles = "";
        for (Movie movie : movieArrayList) {
            movieTitles = movieTitles.concat(movie.getTitle() + "\n");
        }
        Log.d(TAG, "PosterAdapter.setMoviesArrayList() " + "\n" +
                        "sortBySelectionString:\t" + sortBySelectionString + "\n" +
                        "arraySize:\t" + movieArrayList.size() + "\n" +
                        "TITLES\n" +
                        movieTitles);
        //Set viewingFavorites for use later
        // TODO 6/29/2019 - When and for what? - Emre
        Boolean viewingFavorites = sortBySelectionString.equals("favorites");
        notifyDataSetChanged();
    }

    public void updateMoviesList(List<Movie> moviesArrayList) {
        this.movieArrayList.clear();
        this.movieArrayList.addAll(moviesArrayList);
        notifyDataSetChanged();
    }

    public class MoviePosterAdapterViewHolder extends RecyclerView.ViewHolder {
        final TextView tvTitle;
        final ImageView moviePoster;

        MoviePosterAdapterViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(v ->
                    listener.onPosterClick(movieArrayList.get(getAdapterPosition()).getId()));
            tvTitle = itemView.findViewById(R.id.tv_movie_title);
            moviePoster = itemView.findViewById(R.id.iv_movie_poster);
        }

        public void onBind(Movie movie) {
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