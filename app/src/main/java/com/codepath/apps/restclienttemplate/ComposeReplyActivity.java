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

public class ComposeReplyActivity extends AppCompatActivity {

    private TwitterClient client;

    Context context;

    EditText etReplyBody;
    // the tweet we will reply to
    Tweet tweet;

    User cur_user;
    ImageView ivProfileImage;
    TextView tvName;
    TextView tvUsername;
    TextView tvReplyUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose_reply);
        getSupportActionBar().hide();

        context = getApplicationContext();

        client = TwitterApp.getRestClient(this);

        // text field containing reply
        etReplyBody = findViewById(R.id.etReplyBody);

        // finding id of data
        ivProfileImage = findViewById(R.id.ivProfileImage);
        tvName = findViewById(R.id.tvName);
        tvUsername = findViewById(R.id.tvUsername);
        tvReplyUser = findViewById(R.id.tvReplyUser);

        // unwrap data
        tweet = Parcels.unwrap(getIntent().getParcelableExtra(Tweet.class.getSimpleName()));

        fillData();

    }

    public void fillData() {
        // get current user data
        client.getCurrentUser(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("ComposeActivity", "Succeeded getting current user");
                try {
                    cur_user = User.fromJSON(response);
                    // put profile image with glide
                    Glide.with(context).load(cur_user.profileImageUrl).into(ivProfileImage);
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

        // fill in reply user
        tvReplyUser.setText("@"+tweet.user.screenName);
    }

    public void onReply(View view) {

        String replyString = String.format("@%s %s", tweet.user.screenName, etReplyBody.getText().toString());
        client.sendTweet(replyString, tweet.uid, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    Log.d("ComposeReplyActivity", "Succeeded sending tweet");
                    Tweet tweet = Tweet.fromJSON(response);
                    // prepare data intent
                    Intent data = new Intent();
                    // pass relevant data back as a result
                    data.putExtra(Tweet.class.getSimpleName(), Parcels.wrap(tweet));
                    setResult(RESULT_OK, data); // set result code and bundle data for response
                    finish(); // closes the activity, pass data to parent
                } catch (JSONException e) {
                    Log.e("ComposeReplyActivity", "Failed parsing sent tweet");
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e("ComposeReplyActivity", errorResponse.toString());
                throwable.printStackTrace();
            }
        });
    }
}
