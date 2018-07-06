package com.codepath.apps.restclienttemplate;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.List;

import cz.msebera.android.httpclient.Header;

public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.ViewHolder> {
    List<Tweet> mTweets;
    Context context;
    TwitterClient client;

    // pass in the Tweets array in the constructor
    public TweetAdapter(List<Tweet> tweets) {
        mTweets = tweets;
        client = TwitterApp.getRestClient(context);
    }

    // for each row, inflate the layout and cache reference into ViewHolder class
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View tweetView = inflater.inflate(R.layout.item_tweet, parent, false);
        ViewHolder viewHolder = new ViewHolder(tweetView);
        return viewHolder;
    }

    // bind the values based on the position of the element
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // get the data according to position
        Tweet tweet = mTweets.get(position);

        // populate the views according to this data
        holder.tvName.setText(tweet.user.name);
        holder.tvBody.setText(tweet.body);
        holder.tvCreatedAt.setText(tweet.createdAt);
        holder.tvUsername.setText("@"+tweet.user.screenName);
        if (tweet.favorited) { holder.btnLike.setBackgroundResource(R.drawable.ic_vector_heart); }
        if (tweet.retweeted) { holder.btnRetweet.setBackgroundResource(R.drawable.ic_vector_retweet); }

        Glide.with(context).load(tweet.user.profileImageUrl).into(holder.ivProfileImage);
    }

    // create ViewHolder class
    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivProfileImage;
        public TextView tvName;
        public TextView tvUsername;
        public TextView tvCreatedAt;
        public TextView tvBody;
        public Button btnReply;
        public Button btnLike;
        public Button btnRetweet;

        public ViewHolder(View itemView) {
            super(itemView);

            // perform findViewById lookups

            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
            tvName = itemView.findViewById(R.id.tvName);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvCreatedAt = itemView.findViewById(R.id.tvCreatedAt);
            tvBody = itemView.findViewById(R.id.tvBody);
            btnReply = itemView.findViewById(R.id.btnReply);
            btnLike = itemView.findViewById(R.id.btnLike);
            btnRetweet = itemView.findViewById(R.id.btnRetweet);

            btnReply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // gets item position
                    int position = getAdapterPosition();
                    // make sure the position is valid, i.e. actually exists in the view
                    if (position != RecyclerView.NO_POSITION) {
                        // get the tweet at the position, this won't work if the class is static
                        Tweet tweet = mTweets.get(position);
                        // create intent for the new activity
                        Intent intent = new Intent(context, ComposeReplyActivity.class);
                        // serialize the tweet using parceler, use its short name as a key
                        intent.putExtra(Tweet.class.getSimpleName(), Parcels.wrap(tweet));
                        // show the activity
                        ((Activity) context).startActivityForResult(intent,1);
                    }
                }
            });

            btnLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // gets item position
                    int position = getAdapterPosition();
                    // make sure the position is valid, i.e. actually exists in the view
                    if (position != RecyclerView.NO_POSITION) {
                        // get the tweet at the position, this won't work if the class is static
                        Tweet tweet = mTweets.get(position);
                        if (tweet.favorited) {
                            // set to unlike
                            btnLike.setBackgroundResource(R.drawable.ic_vector_heart_stroke);
                            client.unlike(tweet.uid, new JsonHttpResponseHandler() {
                                @Override
                                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                    Log.d("TweetAdapter", "Succeeded liking");
                                    super.onSuccess(statusCode, headers, response);
                                }

                                @Override
                                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                    Log.e("TweetAdapter", "Failed liking");
                                    super.onFailure(statusCode, headers, throwable, errorResponse);
                                }
                            });
                        }
                        else {
                            // fill in heart
                            btnLike.setBackgroundResource(R.drawable.ic_vector_heart);
                            client.like(tweet.uid, new JsonHttpResponseHandler() {
                                @Override
                                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                    Log.d("TweetAdapter", "Succeeded liking");
                                    super.onSuccess(statusCode, headers, response);
                                }

                                @Override
                                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                    Log.e("TweetAdapter", "Failed liking");
                                    super.onFailure(statusCode, headers, throwable, errorResponse);
                                }
                            });
                        }
                    }
                }
            });

            btnRetweet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // gets item position
                    int position = getAdapterPosition();
                    // make sure the position is valid, i.e. actually exists in the view
                    if (position != RecyclerView.NO_POSITION) {
                        // get the tweet at the position, this won't work if the class is static
                        Tweet tweet = mTweets.get(position);
                        if (tweet.retweeted) {
                            // unretweet
                            btnRetweet.setBackgroundResource(R.drawable.ic_vector_retweet_stroke);
                            client.unretweet(tweet.uid, new JsonHttpResponseHandler() {
                                @Override
                                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                    Log.d("TweetAdapter", "Succeeded retweet");
                                    super.onSuccess(statusCode, headers, response);
                                }
                                @Override
                                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                    Log.e("TweetAdapter", "Failed retweet");
                                    super.onFailure(statusCode, headers, throwable, errorResponse);
                                }
                            });
                        }
                        else {
                            // fill in retweet
                            btnRetweet.setBackgroundResource(R.drawable.ic_vector_retweet);
                            client.retweet(tweet.uid, new JsonHttpResponseHandler() {
                                @Override
                                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                    Log.d("TweetAdapter", "Succeeded retweet");
                                    super.onSuccess(statusCode, headers, response);
                                }
                                @Override
                                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                    Log.e("TweetAdapter", "Failed retweet");
                                    super.onFailure(statusCode, headers, throwable, errorResponse);
                                }
                            });
                        }
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mTweets.size();
    }

    // Clean all elements of the recycler
    public void clear() {
        mTweets.clear();
        notifyDataSetChanged();
    }
    // Add a list of items -- change to type used
    public void addAll(List<Tweet> list) {
        mTweets.addAll(list);
        notifyDataSetChanged();
    }
}
