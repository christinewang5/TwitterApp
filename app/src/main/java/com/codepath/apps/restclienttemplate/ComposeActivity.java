package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import cz.msebera.android.httpclient.Header;

public class ComposeActivity extends AppCompatActivity {

    private TwitterClient client;

    Context context;

    User cur_user;
    ImageView ivProfileImage;
    TextView tvName;
    TextView tvUsername;

    EditText etTweetBody;
    String tweetString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);
        getSupportActionBar().hide();

        client = TwitterApp.getRestClient(this);

        context = getApplicationContext();

        // text field containing updated item description
        ivProfileImage = findViewById(R.id.ivProfileImage);
        tvName = findViewById(R.id.tvName);
        tvUsername = findViewById(R.id.tvName);
        etTweetBody = findViewById(R.id.etTweetBody);

        fillUserData();
    }

    public void fillUserData() {
        client.getCurrentUser(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("ComposeActivity", "Succeeded getting current user");
                try {
                    cur_user = User.fromJSON(response);
                    // put profile image with glide
                    Glide.with(context).load(cur_user.getProfileImageUrl()).into(ivProfileImage);
                    // set username
                    tvUsername.setText("@"+cur_user.screenName);
                    tvName.setText(cur_user.name);
                } catch (JSONException e) {
                    Log.e("ComposeActivity", "Failed parsing current user");
                }

            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e("ComposeActivity", errorResponse.toString());
            }
        });

    }

    public void onTweet(View view) {
        tweetString = etTweetBody.getText().toString();

        client.sendTweet(tweetString, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    Log.i("ComposeActivity", "Succeeded sending tweet");
                    Tweet tweet = Tweet.fromJSON(response);
                    // prepare data intent
                    Intent data = new Intent();
                    // pass relevant data back as a result
                    data.putExtra(Tweet.class.getSimpleName(), Parcels.wrap(tweet));
                    setResult(RESULT_OK, data); // set result code and bundle data for response
                    finish(); // closes the activity, pass data to parent
                } catch (JSONException e) {
                    Log.e("ComposeActivity", "Failed parsing sent tweet");
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e("ComposeActivity", errorResponse.toString());
                throwable.printStackTrace();
            }
        });
    }
}