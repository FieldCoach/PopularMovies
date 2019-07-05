package com.example.android.popularmovies.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.example.android.popularmovies.ApiKeyFile;
import com.example.android.popularmovies.R;
import com.example.android.popularmovies.data.Movies.Result;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.google.android.youtube.player.YouTubeThumbnailView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ioutd on 11/6/2017.
 */
public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerAdapterViewHolder> {

    private ArrayList<Result> trailerArrayList = new ArrayList<>();
    private TrailerAdapterListener listener;

    public interface TrailerAdapterListener {
        void onTrailerInteraction(String tag);
    }

    public TrailerAdapter(TrailerAdapterListener listener){
        this.listener = listener;
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
        holder.onBind(trailerArrayList.get(position));

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
            trailerThumbnail.setOnClickListener(view ->
                    listener.onTrailerInteraction(itemView.getTag().toString()));
        }

        void onBind(Result result) {
            final String trailerKey = result.getKey();
            itemView.setTag(trailerKey);
            trailerThumbnail.initialize(ApiKeyFile.YOUTUBE_API_KEY, new YouTubeThumbnailView.OnInitializedListener() {
                @Override
                public void onInitializationSuccess(YouTubeThumbnailView youTubeThumbnailView, YouTubeThumbnailLoader youTubeThumbnailLoader) {
                    youTubeThumbnailLoader.setVideo(trailerKey);
                }
                @Override
                public void onInitializationFailure(YouTubeThumbnailView youTubeThumbnailView, YouTubeInitializationResult youTubeInitializationResult) {

                }
            });
        }
    }
}
