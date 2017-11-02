package com.example.android.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by AaronC on 11/2/2017.
 */

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewAdapterViewHolder> {

    private Context mContext;
    private ArrayList<Review> mReviewArrayList = new ArrayList<>();

    ReviewAdapter(Context context) {
        mContext = context;
    }

    class ReviewAdapterViewHolder extends RecyclerView.ViewHolder{

        private TextView author;
        private TextView content;

        ReviewAdapterViewHolder(View itemView) {
            super(itemView);

            author = (TextView) itemView.findViewById(R.id.tv_author);
            content = (TextView) itemView.findViewById(R.id.tv_content);
        }
    }


    @Override
    public ReviewAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.review_list_item, parent, false);

        return new ReviewAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewAdapterViewHolder holder, int position) {
        Review review = mReviewArrayList.get(position);

        holder.author.setText(review.getAuthor());
        holder.content.setText(review.getContent());
    }

    @Override
    public int getItemCount() {
        if (null == mReviewArrayList) return 0;
        return mReviewArrayList.size();
    }

    public void setmReviewArrayList(ArrayList<Review> mReviewArrayList) {
        this.mReviewArrayList.clear();
        this.mReviewArrayList.addAll(mReviewArrayList);

        notifyDataSetChanged();
    }
}
