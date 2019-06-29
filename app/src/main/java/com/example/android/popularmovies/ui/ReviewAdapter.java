package com.example.android.popularmovies.ui;

import android.content.Context;
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

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewAdapterViewHolder> {

    private Context mContext;
    private ArrayList<Result> mReviewArrayList = new ArrayList<>();
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
        Result result = mReviewArrayList.get(position);

        holder.author.setText(result.getAuthor());
        holder.content.setText(result.getContent());

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
    void setmReviewArrayList(List<Result> mReviewArrayList) {
        this.mReviewArrayList.clear();
        this.mReviewArrayList.addAll(mReviewArrayList);

        notifyDataSetChanged();
    }
}
