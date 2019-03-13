package com.example.android.popularmovies.ui;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.data.Review;

import java.util.ArrayList;

/**
 * Created by AaronC on 11/2/2017.
 */

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewAdapterViewHolder> {

    private Context mContext;
    private ArrayList<Review> mReviewArrayList = new ArrayList<>();
    private final static String TAG = ReviewAdapter.class.getSimpleName();

    ReviewAdapter(Context context) {
        mContext = context;
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

    /**
     * Inflates the review_list_item layout then returns a ViewHolder containing that layout
     * @param parent the parent ViewGroup
     * @param viewType identifies the type of view
     * @return ViewHolder containing the inflated layout
     */
    @Override
    public ReviewAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.review_list_item, parent, false);

        return new ReviewAdapterViewHolder(view);
    }

    /**
     * Sets the text on the TextViews in the ViewHolder
     * @param holder the ViewHolder
     * @param position the position of the ViewHolder to be bound
     */
    @Override
    public void onBindViewHolder(ReviewAdapterViewHolder holder, int position) {
        Review review = mReviewArrayList.get(position);

        holder.author.setText(review.getAuthor());
        holder.content.setText(review.getContent());

        if (position == mReviewArrayList.size() - 1){
            holder.view.setVisibility(View.GONE);
        }
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        if (null == mReviewArrayList) return 0;
        return mReviewArrayList.size();
    }

    /**
     * Clears previous data from the ArrayList of Reviews, adds new Reviews, then notifies the Adapter
     * @param mReviewArrayList arrayList containing the reviews
     */
    void setmReviewArrayList(ArrayList<Review> mReviewArrayList) {
        this.mReviewArrayList.clear();
        this.mReviewArrayList.addAll(mReviewArrayList);

        notifyDataSetChanged();
    }
}
