package com.example.android.popularmovies;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeStandalonePlayer;
import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.google.android.youtube.player.YouTubeThumbnailView;

import java.util.ArrayList;

/**
 * Created by ioutd on 11/6/2017.
 */

public class MovieTrailerAdapter extends RecyclerView.Adapter<MovieTrailerAdapter.TrailerAdapterViewHolder> {

    private final Activity activity;
    private ArrayList<String> trailerArrayList = new ArrayList<>();

    MovieTrailerAdapter(Activity activity){
        this.activity = activity;
    }

    class TrailerAdapterViewHolder extends RecyclerView.ViewHolder {

        private YouTubeThumbnailView trailerThumbnail;

        TrailerAdapterViewHolder(final View itemView) {
            super(itemView);
            trailerThumbnail = (YouTubeThumbnailView) itemView.findViewById(R.id.tn_youtube_trailer);

            trailerThumbnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
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

    @Override
    public TrailerAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.trailer_list_item, parent, false);

        return new TrailerAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrailerAdapterViewHolder holder, int position) {
        //query for trailer urls and attach them to thumbnail
        final String trailerKey = trailerArrayList.get(position);
        holder.itemView.setTag(trailerKey);

        holder.trailerThumbnail.initialize(ApiKeyFile.YOUTUBE_API_KEY, new YouTubeThumbnailView.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubeThumbnailView youTubeThumbnailView, YouTubeThumbnailLoader youTubeThumbnailLoader) {
                youTubeThumbnailLoader.setVideo(trailerKey);
            }

            @Override
            public void onInitializationFailure(YouTubeThumbnailView youTubeThumbnailView, YouTubeInitializationResult youTubeInitializationResult) {
                // TODO: 11/7/2017 onInitializationFailure() - set an error image signalling no internet connection
            }
        });
    }

    @Override
    public int getItemCount() {
        if (null == trailerArrayList) return 0;
        return trailerArrayList.size();
    }

    void setTrailerArrayList(ArrayList<String> trailerArrayList) {
        this.trailerArrayList.clear();
        this.trailerArrayList.addAll(trailerArrayList);

        notifyDataSetChanged();
    }

}
