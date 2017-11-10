package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by AaronC on 7/26/2017.
 */

public class MoviePosterAdapter extends RecyclerView.Adapter<MoviePosterAdapter.MoviePosterAdapterViewHolder>{

    private Context context;
    private ArrayList<Movie> movieArrayList = new ArrayList<>();

    private static final String MOVIE = "movie";

    private static final String TAG = MoviePosterAdapter.class.getSimpleName();
    private Boolean viewingFavorites;
    private ArrayList<byte[]> favoritesPosterArray = new ArrayList<>();

    MoviePosterAdapter(Context context){
        this.context = context;
    }

    public class MoviePosterAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        final ImageView moviePoster;
        final TextView tvTitle;

        MoviePosterAdapterViewHolder(View itemView) {
            super(itemView);
            moviePoster = (ImageView) itemView.findViewById(R.id.iv_movie_poster);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_movie_title);      // TODO: 10/29/2017 rename tv_title

            //Calculate the number of columns and get the display metrics to prevent white space between posters
            int noOfColumns = MainActivity.calculateNoOfColumns(context);
            DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
            int width = displayMetrics.widthPixels/noOfColumns;

            //moviePoster will have 9:16 aspect ratio
            int height = (width * 16)/9;

            moviePoster.setMinimumHeight(height);
            moviePoster.setMinimumWidth(width);

            itemView.setOnClickListener(this);
        }

        /**
         * Creates an intent, passes it the Movie Object at the current adapter position
         * @param view The moviePoster ImageView that was clicked
         */
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(view.getContext(), MovieDetailsActivity.class);
            int position = getAdapterPosition();

            intent.putExtra(MOVIE, movieArrayList.get(position));
            view.getContext().startActivity(intent);
        }
    }

    /**
     * Inflates the layout of the Movie list item then returns a ViewHolder containing that layout
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public MoviePosterAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int movieListItemId = R.layout.movie_list_item;
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        View view = layoutInflater.inflate(movieListItemId, parent, false);

        return new MoviePosterAdapterViewHolder(view);
    }

    /**
     * Loads the poster image and binds it to the ImageView in the ViewHolder
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(MoviePosterAdapterViewHolder holder, int position) {
        Movie currentMovie = movieArrayList.get(position);
        String currentMoviePoster = currentMovie.getPosterLocationUriString();
        String movieTitle = movieArrayList.get(position).getTitle();

        if (!viewingFavorites) {
            Picasso.with(context)
                    .load(currentMoviePoster)
                    .fit()
                    .into(holder.moviePoster);
        } else {
            // TODO: 11/10/2017 onBindViewHolder() - load images from byte array
            byte[] imageBytes = favoritesPosterArray.get(position);
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

            holder.moviePoster.setImageBitmap(bitmap);
        }
        holder.tvTitle.setText(movieTitle);
        Log.d(TAG, "onBindViewHolder() returned: " + movieArrayList.get(position).getTitle() + "\n" +
                "at position: " + String.valueOf(position));
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

    //Clears previous data from the ArrayList of Movies, adds new Movies, then notifies the Adapter
    void setMoviesArrayList(ArrayList<Movie> moviesArrayList, String sortBySelectionString) {
        this.movieArrayList.clear();
        this.movieArrayList.addAll(moviesArrayList);

        if (sortBySelectionString.equals("favorites"))
            viewingFavorites = true;
        else
            viewingFavorites = false;

        notifyDataSetChanged();
        Log.d(TAG, "setMoviesArrayList() size: " + String.valueOf(moviesArrayList.size()));
    }

    void setFavoritesPosterArray(ArrayList<byte[]> posterBytes){
        this.favoritesPosterArray.clear();
        this.favoritesPosterArray.addAll(posterBytes);
    }
}
