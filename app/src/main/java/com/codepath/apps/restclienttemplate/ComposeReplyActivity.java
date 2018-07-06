package com.codepath.apps.restclienttemplate;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

public class ComposeReplyActivity extends AppCompatActivity {

    private TwitterClient client;
    EditText etTweetBodyReply;
    String tweetString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);
        getSupportActionBar().hide();

        client = TwitterApp.getRestClient(this);
        // text field containing updated item description
        etTweetBodyReply = findViewById(R.id.etTweetBodyReply);

    }

    public void onReply(View view) {
    }
}
