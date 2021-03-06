package com.codepath.apps.restclienttemplate;

import android.content.Context;

import com.codepath.oauth.OAuthBaseClient;
import com.github.scribejava.apis.TwitterApi;
import com.github.scribejava.core.builder.api.BaseApi;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/*
 * 
 * This is the object responsible for communicating with a REST API. 
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes: 
 *   https://github.com/scribejava/scribejava/tree/master/scribejava-apis/src/main/java/com/github/scribejava/apis
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 * 
 * NOTE: You may want to rename this object based on the service i.e TwitterClient or FlickrClient
 * 
 */
public class TwitterClient extends OAuthBaseClient {
	public static final BaseApi REST_API_INSTANCE = TwitterApi.instance();
	public static final String REST_URL = "https://api.twitter.com/1.1";
	public static final String REST_CONSUMER_KEY = "xQ4E1cTJw8D3FSU9ZRDglqWm6";
	public static final String REST_CONSUMER_SECRET = "6KriXGFEIl6x6RbDQspiJOMZc0ulni1GlgvYwP30LXh5nesWHM";

	// Landing page to indicate the OAuth flow worked in case Chrome for Android 25+ blocks navigation back to the app.
	public static final String FALLBACK_URL = "https://codepath.github.io/android-rest-client-template/success.html";

	// See https://developer.chrome.com/multidevice/android/intents
	public static final String REST_CALLBACK_URL_TEMPLATE = "intent://%s#Intent;action=android.intent.action.VIEW;scheme=%s;package=%s;S.browser_fallback_url=%s;end";

	public TwitterClient(Context context) {
		super(context, REST_API_INSTANCE,
				REST_URL,
				REST_CONSUMER_KEY,
				REST_CONSUMER_SECRET,
				String.format(REST_CALLBACK_URL_TEMPLATE, context.getString(R.string.intent_host),
						context.getString(R.string.intent_scheme), context.getPackageName(), FALLBACK_URL));
	}

	public void getHomeTimeline(AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/home_timeline.json");
		// Can specify query string params directly or through RequestParams.
		RequestParams params = new RequestParams();
		params.put("count", 25);
		params.put("since_id", 1);
		client.get(apiUrl, params, handler);
	}

	// TODO - use twitter API auto_populate_reply_metadata param
    public void sendTweet(String message, long in_reply_to_status_id, AsyncHttpResponseHandler handler) {
        // passes relative path to endpoint
        String apiUrl = getApiUrl("statuses/update.json");
        // specify query string params through RequestParams.
        RequestParams params = new RequestParams();
        params.put("status", message);
        if (in_reply_to_status_id != -1) {
            params.put("in_reply_to_status_id", in_reply_to_status_id);
        }
        // define the request method and make a call to the client
        client.post(apiUrl, params, handler);
    }

    // handle no status id with default value
    public void sendTweet(String message, AsyncHttpResponseHandler handler) {
        sendTweet(message, -1, handler);
    }


    public void getCurrentUser(AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("account/verify_credentials.json");
		RequestParams params = new RequestParams();
        client.get(apiUrl, params, handler);
	}

	public void like(long id, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("favorites/create.json");
        RequestParams params = new RequestParams();
        params.put("id", id);
        client.post(apiUrl, params, handler);
    }
	public void unlike(long id, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("favorites/destroy.json");
		RequestParams params = new RequestParams();
		params.put("id", id);
		client.post(apiUrl, params, handler);
	}

    public void retweet(long id, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl(String.format("statuses/retweet/%s.json", id));
        RequestParams params = new RequestParams();
        client.post(apiUrl, params, handler);
    }

	public void unretweet(long id, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl(String.format("statuses/unretweet/%s.json", id));
		RequestParams params = new RequestParams();
		client.post(apiUrl, params, handler);
	}
}
