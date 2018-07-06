package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;
import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

public class TweetDetailsActivity extends AppCompatActivity {
    Tweet tweet;
    TwitterClient client;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_details);
        ButterKnife.bind(this);
        getSupportActionBar().hide();

        // get the data according to position
        tweet = Parcels.unwrap(getIntent().getParcelableExtra(Tweet.class.getSimpleName()));

        // get client
        client = TwitterApp.getRestClient(this);

        // populate the views according to this data
        tvName.setText(tweet.user.name);
        tvBody.setText(tweet.body);
        tvCreatedAt.setText(tweet.createdAt);
        tvUsername.setText("@"+tweet.user.screenName);
        tvFavoriteCount.setText(String.valueOf(tweet.favoriteCount));
        tvRetweetCount.setText(String.valueOf(tweet.retweetCount));
        if (tweet.favorited) { btnLike.setBackgroundResource(R.drawable.ic_vector_heart); }
        if (tweet.retweeted) { btnRetweet.setBackgroundResource(R.drawable.ic_vector_retweet); }
        Glide.with(this).load(tweet.user.profileImageUrl).into(ivProfileImage);
    }

    @OnClick(R.id.btnReply)
    public void onReply() {
        // create intent for the new activity
        Intent intent = new Intent(this, ComposeReplyActivity.class);
        // serialize the tweet using parceler, use its short name as a key
        intent.putExtra(Tweet.class.getSimpleName(), Parcels.wrap(tweet));
        // show the activity
        startActivityForResult(intent,1);
    }

    @OnClick(R.id.btnLike)
    public void onLike() {
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
        } }


    @OnClick(R.id.btnRetweet)
    public void onRetweet() {
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



