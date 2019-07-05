package com.example.android.popularmovies.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.data.Movies.Result;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AaronC on 11/2/2017.
 */
/**
 * TODO 7/1/2019 CODE CLEAN-UP:
 *  Aaron, please update this class using PosterAdapter as a template - Emre
 */
public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewAdapterViewHolder> {

    private ArrayList<Result> mReviewArrayList = new ArrayList<>();


    public ReviewAdapter() {

    }

    @Override
    public ReviewAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.review_list_item, parent, false);
        return new ReviewAdapterViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ReviewAdapterViewHolder holder, int position) {
        Result result = mReviewArrayList.get(position);
        holder.author.setText(result.getAuthor());
        holder.content.setText(result.getContent());
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
    }
}