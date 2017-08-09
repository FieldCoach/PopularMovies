package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by AaronC on 7/26/2017.
 */

public class MoviePosterAdapter extends RecyclerView.Adapter<MoviePosterAdapter.MoviePosterAdapterViewHolder>{

    private static final String TAG = MoviePosterAdapter.class.getSimpleName();
    private static final String MOVIE_POSTER = "moviePoster";
    private static final String POSITION = "position";
    private Context context;

    private Uri[] moviePosterLocationsArray;

    MoviePosterAdapter(Context context){
        this.context = context;
    }

    public class MoviePosterAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public final ImageView moviePoster;

        MoviePosterAdapterViewHolder(View itemView) {
            super(itemView);
            moviePoster = (ImageView) itemView.findViewById(R.id.iv_movie_poster);

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
         * Creates an intent, passes it the location of the moviePoster and the current adapter position
         * @param view The moviePoster ImageView that was clicked
         */
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(view.getContext(), MovieDetailsActivity.class);
            int position = getAdapterPosition();
            String moviePoster = moviePosterLocationsArray[position].toString();

            intent.putExtra(MOVIE_POSTER, moviePoster);
            intent.putExtra(POSITION, position);
            view.getContext().startActivity(intent);
        }
    }

    /**
     * Called when RecyclerView needs a new {@link ViewHolder} of the given type to represent
     * an item.
     * <p>
     * This new ViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file.
     * <p>
     * The new ViewHolder will be used to display items of the adapter using
     * {@link #onBindViewHolder(ViewHolder, int, List)}. Since it will be re-used to display
     * different items in the data set, it is a good idea to cache references to sub views of
     * the View to avoid unnecessary {@link View#findViewById(int)} calls.
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     * @see #getItemViewType(int)
     * @see #onBindViewHolder(ViewHolder, int)
     */
    @Override
    public MoviePosterAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int movieListItemId = R.layout.movie_list_item;
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        View view = layoutInflater.inflate(movieListItemId, parent, false);

        return new MoviePosterAdapterViewHolder(view);
    }

    /**
     * Called by RecyclerView to display the data at the specified position. This method should
     * update the contents of the {@link ViewHolder#itemView} to reflect the item at the given
     * position.
     * <p>
     * Note that unlike {@link ListView}, RecyclerView will not call this method
     * again if the position of the item changes in the data set unless the item itself is
     * invalidated or the new position cannot be determined. For this reason, you should only
     * use the <code>position</code> parameter while acquiring the related data item inside
     * this method and should not keep a copy of it. If you need the position of an item later
     * on (e.g. in a click listener), use {@link ViewHolder#getAdapterPosition()} which will
     * have the updated adapter position.
     * <p>
     * Override {@link #onBindViewHolder(ViewHolder, int, List)} instead if Adapter can
     * handle efficient partial bind.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(MoviePosterAdapterViewHolder holder, int position) {
        String currentMoviePoster = moviePosterLocationsArray[position].toString();
        Picasso.with(context)
                .load(currentMoviePoster)
                .fit()
                .into(holder.moviePoster);
        Log.d(TAG, "onBindViewHolder() returned: " + currentMoviePoster);
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        if (null == moviePosterLocationsArray) return 0;
        Log.d(TAG, "getItemCount() returned: " + moviePosterLocationsArray.length);
        return moviePosterLocationsArray.length;
    }

    public void setMoviePosterLocationsArray(ArrayList<Uri> moviePosterLocationsArray) {
        int size = moviePosterLocationsArray.size();
        this.moviePosterLocationsArray = moviePosterLocationsArray.toArray(new Uri[size]);

        notifyDataSetChanged();
        Log.d(TAG, "setMoviePosterLocationsArray() returned: " + this.moviePosterLocationsArray[1]);
    }
}
