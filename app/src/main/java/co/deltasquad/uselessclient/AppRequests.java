package co.deltasquad.uselessclient;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.entity.StringEntity;

public class AppRequests {

    private static AppRequests instance = null;

    public static AppRequests getInstance() {
        if (instance == null) {
            instance = new AppRequests();
        }
        return instance;
    }

    private static String getAbsoluteUrl(String relativeUrl){return "https://10.0.2.2:5000/" + relativeUrl;}

    public void getUsers(AsyncHttpClient client, JsonHttpResponseHandler handler) {
        String url = "users";

        RequestParams params = new RequestParams();

        client.get(getAbsoluteUrl(url), params, handler);
    }

    public void getUserByHandle(String handle, AsyncHttpClient client, JsonHttpResponseHandler handler) {
        String url ="users/" + handle;

        RequestParams params = new RequestParams();

        client.get(getAbsoluteUrl(url), params, handler);
    }

    public void getBlogByPartialuserName(String partial_name, AsyncHttpClient client, JsonHttpResponseHandler handler) {
        String url = "users/blogs";

        RequestParams params = new RequestParams();
        params.put("user_name", partial_name);

        client.get(getAbsoluteUrl(url), params, handler);
    }

    public void createUser(Context context, String handle, String name, String password, AsyncHttpClient client, JsonHttpResponseHandler handler) throws JSONException, UnsupportedEncodingException {
        String url = "users";

        JSONObject jsonParams = new JSONObject();
        jsonParams.put("handle", handle);
        jsonParams.put("name", name);
        jsonParams.put("password", password);
        StringEntity entity = new StringEntity(jsonParams.toString());

        client.post(context, getAbsoluteUrl(url), entity, "application/json",
                handler);
    }

    public void updateUser(Context context, String handle, String name, AsyncHttpClient client, JsonHttpResponseHandler handler) throws JSONException, UnsupportedEncodingException {
        String url = "users";

        JSONObject jsonParams = new JSONObject();
        jsonParams.put("handle", handle);
        jsonParams.put("name", name);
        StringEntity entity = new StringEntity(jsonParams.toString());

        client.put(context, getAbsoluteUrl(url), entity, "application/json",
                handler);
    }

    public void getBlogs(AsyncHttpClient client, JsonHttpResponseHandler handler) {
        String url = "blogs";

        RequestParams params = new RequestParams();

        client.get(getAbsoluteUrl(url), params, handler);
    }

    public void getBlogsBySlug(String slug, AsyncHttpClient client, JsonHttpResponseHandler handler) {
        String url = "blogs/" + slug;

        RequestParams params = new RequestParams();

        client.get(getAbsoluteUrl(url), params, handler);
    }

    public void getBlogsByPartialTitle(String partial_title, AsyncHttpClient client, JsonHttpResponseHandler handler) {
        String url = "blogs";

        RequestParams params = new RequestParams();
        params.put("title", partial_title);

        client.get(getAbsoluteUrl(url), params, handler);
    }

    public void addBlog(Context context, String content, String title, AsyncHttpClient client, JsonHttpResponseHandler handler) throws JSONException, UnsupportedEncodingException {
        String url = "blogs";

        JSONObject jsonParams = new JSONObject();
        jsonParams.put("title", title);
        jsonParams.put("content", content);
        StringEntity entity = new StringEntity(jsonParams.toString());

        client.post(context, getAbsoluteUrl(url), entity, "application/json",
                handler);
    }

    public void updateBlog(Context context, String content, String title, String slug, AsyncHttpClient client, JsonHttpResponseHandler handler) throws UnsupportedEncodingException, JSONException {
        String url = "blogs/" + slug;

        JSONObject jsonParams = new JSONObject();
        jsonParams.put("title", title);
        jsonParams.put("content", content);
        StringEntity entity = new StringEntity(jsonParams.toString());

        client.put(context, getAbsoluteUrl(url), entity, "application/json",
                handler);
    }

    public void loginUser(Context context, String username, String password, AsyncHttpClient client, JsonHttpResponseHandler handler) throws JSONException, UnsupportedEncodingException {
        String url = "auth";

        JSONObject jsonParams = new JSONObject();
        jsonParams.put("username", username);
        jsonParams.put("password", password);
        StringEntity entity = new StringEntity(jsonParams.toString());

        client.post(context, getAbsoluteUrl(url), entity, "application/json",
                handler);
    }

    public void deletePost(String slug, AsyncHttpClient client, JsonHttpResponseHandler handler) {
        String url = "blogs/" + slug;

        RequestParams params = new RequestParams();

        client.delete(getAbsoluteUrl(url), params, handler);
    }



}
