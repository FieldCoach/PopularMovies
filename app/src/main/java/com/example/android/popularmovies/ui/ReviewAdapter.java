package com.example.android.popularmovies.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.data.Movies.Result;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AaronC on 11/2/2017.
 */
public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewAdapterViewHolder> {

    private ArrayList<Result> mReviewArrayList = new ArrayList<>();


    public ReviewAdapter() {

    }

    @NonNull
    @Override
    public ReviewAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.review_list_item, parent, false);
        return new ReviewAdapterViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewAdapterViewHolder holder, int position) {
        holder.bind(mReviewArrayList.get(position));

        // Removes divider line under the last Review in the RecyclerView - Aaron
        if (position == mReviewArrayList.size() - 1){
            holder.view.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        if (null == mReviewArrayList) return 0;
        return mReviewArrayList.size();
    }

    /**
     * Clears previous data from the ArrayList of Reviews, adds new Reviews, then notifies the Adapter
     * @param mReviewArrayList arrayList containing the reviews
     */
    public void updateReviewList(List<Result> mReviewArrayList) {
        this.mReviewArrayList.clear();
        this.mReviewArrayList.addAll(mReviewArrayList);
        notifyDataSetChanged();
    }

    class ReviewAdapterViewHolder extends RecyclerView.ViewHolder{
        private TextView author;
        private TextView content;
        private View view;

        ReviewAdapterViewHolder(View itemView) {
            super(itemView);
            author = itemView.findViewById(R.id.tv_author);
            content = itemView.findViewById(R.id.tv_content);
            view = itemView.findViewById(R.id.v_seperator);
        }

        void bind(Result result) {
            author.setText(result.getAuthor());
            content.setText(result.getContent());
        }
    }
}