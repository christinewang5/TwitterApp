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

import com.bumptech.glide.request.RequestOptions;
import com.codepath.apps.restclienttemplate.models.GlideApp;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/*import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;*/


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
        return new ViewHolder(tweetView);
    }

    // bind the values based on the position of the element
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // get the data according to position
        Tweet tweet = mTweets.get(position);

        // populate the views according to this data
        holder.tvName.setText(tweet.user.name);
        holder.tvBody.setText(tweet.body);
        //holder.tvBody.setLinksClickable(Html.fromHtml(tweet.embedUrl));
        holder.tvCreatedAt.setText(tweet.createdAt);
        holder.tvUsername.setText("@"+tweet.user.screenName);
        holder.tvFavoriteCount.setText(String.valueOf(tweet.favoriteCount));
        holder.tvRetweetCount.setText(String.valueOf(tweet.retweetCount));
        if (tweet.favorited) { holder.btnLike.setBackgroundResource(R.drawable.ic_vector_heart); }
        if (tweet.retweeted) { holder.btnRetweet.setBackgroundResource(R.drawable.ic_vector_retweet); }
        GlideApp.with(context)
                .load(tweet.user.profileImageUrl)
                .apply(RequestOptions.circleCropTransform())
                 .into(holder.ivProfileImage);
        GlideApp.with(context)
                .load(tweet.embedUrl)
                .transform(new RoundedCornersTransformation(25, 2))
                .into(holder.ivMedia);
    }

    // create ViewHolder class
    public class ViewHolder extends RecyclerView.ViewHolder {
        // perform findViewById lookups
        @BindView(R.id.ivProfileImage) ImageView ivProfileImage;
        @BindView(R.id.tvName) TextView tvName;
        @BindView(R.id.tvUsername) TextView tvUsername;
        @BindView(R.id.tvCreatedAt) TextView tvCreatedAt;
        @BindView(R.id.tvBody) TextView tvBody;
        @BindView(R.id.btnReply) Button btnReply;
        @BindView(R.id.btnLike) Button btnLike;
        @BindView(R.id.btnRetweet) Button btnRetweet;
        @BindView(R.id.tvFavoriteCount) TextView tvFavoriteCount;
        @BindView(R.id.tvRetweetCount) TextView tvRetweetCount;
        @BindView(R.id.ivMedia ) ImageView ivMedia;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick
        void onClick() {
            Log.d("TweetAdapter", "Launching Tweet Details Activity");
            // gets item position
            int position = getAdapterPosition();
            // make sure the position is valid, i.e. actually exists in the view
            if (position != RecyclerView.NO_POSITION) {
                // get the tweet at the position, this won't work if the class is static
                Tweet tweet = mTweets.get(position);
                // create intent for the new activity
                Intent intent = new Intent(context, TweetDetailsActivity.class);
                // serialize the tweet using parceler, use its short name as a key
                intent.putExtra(Tweet.class.getSimpleName(), Parcels.wrap(tweet));
                context.startActivity(intent);
            }
        }

        @OnClick(R.id.btnReply)
        public void onReply() {
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

        @OnClick(R.id.btnLike)
        public void onLike() {
            // gets item position
            int position = getAdapterPosition();
            // make sure the position is valid, i.e. actually exists in the view
            if (position != RecyclerView.NO_POSITION) {
                // get the tweet at the position, this won't work if the class is static
                Tweet tweet = mTweets.get(position);
                if (tweet.favorited) {
                    // set to unlike
                    client.unlike(tweet.uid, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            btnLike.setBackgroundResource(R.drawable.ic_vector_heart_stroke);
                            Log.d("TweetAdapter", "Succeeded unliking");
                            super.onSuccess(statusCode, headers, response);
                        }
                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            Log.e("TweetAdapter", "Failed unliking");
                            super.onFailure(statusCode, headers, throwable, errorResponse);
                        }
                    });
                }
                else {
                    // fill in heart
                    client.like(tweet.uid, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            Log.d("TweetAdapter", "Succeeded liking");
                            btnLike.setBackgroundResource(R.drawable.ic_vector_heart);
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

        @OnClick(R.id.btnRetweet)
        public void onRetweet() {
            // gets item position
            int position = getAdapterPosition();
            // make sure the position is valid, i.e. actually exists in the view
            if (position != RecyclerView.NO_POSITION) {
                // get the tweet at the position, this won't work if the class is static
                Tweet tweet = mTweets.get(position);
                if (tweet.retweeted) {
                    // unretweet
                    client.unretweet(tweet.uid, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            Log.d("TweetAdapter", "Succeeded unretweet");
                            btnRetweet.setBackgroundResource(R.drawable.ic_vector_retweet_stroke);
                            super.onSuccess(statusCode, headers, response);
                        }
                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            Log.e("TweetAdapter", "Failed unretweet");
                            super.onFailure(statusCode, headers, throwable, errorResponse);
                        }
                    });
                }
                else {
                    // fill in retweet
                    client.retweet(tweet.uid, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            Log.d("TweetAdapter", "Succeeded retweet");
                            btnRetweet.setBackgroundResource(R.drawable.ic_vector_retweet);
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
