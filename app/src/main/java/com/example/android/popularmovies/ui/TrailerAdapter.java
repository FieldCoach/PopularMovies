package com.example.android.popularmovies.ui;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.example.android.popularmovies.ApiKeyFile;
import com.example.android.popularmovies.R;
import com.example.android.popularmovies.data.Movies.Result;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeStandalonePlayer;
import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.google.android.youtube.player.YouTubeThumbnailView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ioutd on 11/6/2017.
 */
/**
 * TODO 7/1/2019 CODE CLEAN-UP:
 *  Aaron, please update this class using PosterAdapter as a template - Emre
 */
public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerAdapterViewHolder> {

    /*
     TODO: 11/21/2017 () - Change activity to weak reference to avoid memory leak - Aaron
     TODO 7/1/2019(Update) CODE CLEAN-UP: Whatever requires the activity itself to do its job,
      shouldn't be here so, this implementation needs to be handled in another way - Emre
     */
    private final Activity activity;
    private ArrayList<Result> trailerArrayList = new ArrayList<>();

    public TrailerAdapter(Activity activity){
        this.activity = activity;
    }

    /**
     * Inflates the trailer_list_item layout then returns a ViewHolder containing that layout
     * @param parent the parent ViewGroup
     * @param viewType identifies the type of view
     * @return ViewHolder containing the inflated layout
     */
    @Override
    public TrailerAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.trailer_list_item, parent, false);
        return new TrailerAdapterViewHolder(view);
    }

    /**
     * Loads the YouTube thumbnail image and binds it to the ImageView in the ViewHolder
     * @param holder the ViewHolder
     * @param position the position of the ViewHolder to be bound
     */
    @Override
    public void onBindViewHolder(TrailerAdapterViewHolder holder, int position) {
        //query for trailer urls and attach them to thumbnail
        Result result = trailerArrayList.get(position);
        final String trailerKey = result.getKey();
        holder.itemView.setTag(trailerKey);
        holder.trailerThumbnail.initialize(ApiKeyFile.YOUTUBE_API_KEY, new YouTubeThumbnailView.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubeThumbnailView youTubeThumbnailView, YouTubeThumbnailLoader youTubeThumbnailLoader) {
                youTubeThumbnailLoader.setVideo(trailerKey);
            }
            @Override
            public void onInitializationFailure(YouTubeThumbnailView youTubeThumbnailView, YouTubeInitializationResult youTubeInitializationResult) {

            }
        });
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        if (null == trailerArrayList) return 0;
        return trailerArrayList.size();
    }

    /**
     * Clears previous data from the ArrayList of keys, adds new keys, then notifies the Adapter
     * @param trailerArrayList arrayList containing the trailer video keys
     */
    public void setTrailerArrayList(List<Result> trailerArrayList) {
        this.trailerArrayList.clear();
        this.trailerArrayList.addAll(trailerArrayList);
        notifyDataSetChanged();
    }

    class TrailerAdapterViewHolder extends RecyclerView.ViewHolder {
        private YouTubeThumbnailView trailerThumbnail;

        TrailerAdapterViewHolder(final View itemView) {
            super(itemView);
            trailerThumbnail = itemView.findViewById(R.id.tn_youtube_trailer);
            //When the thumbnail is clicked, play the video
            trailerThumbnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    /* TODO 7/1/I2019 CODE CLEAN-UP: Aaron, never handle these click events
                    *   here in the holder class. Instead use a listener interface
                    *   (check out PosterAdapter impl) and delegate the work to Fragment,
                    *   that way you don't have to pass Activity instance to adapter. - Emre
                    */
                    Intent intent = YouTubeStandalonePlayer.createVideoIntent(
                            activity,
                            ApiKeyFile.YOUTUBE_API_KEY,
                            itemView.getTag().toString(),
                            0,
                            true,
                            false);
                    activity.startActivity(intent);
                }
            });
        }
    }
}
