package co.deltasquad.uselessclient;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import co.deltasquad.uselessclient.Adapters.BlogPostAdapter;
import co.deltasquad.uselessclient.Objects.BlogPostObject;
import cz.msebera.android.httpclient.Header;

public class MainAppActivity extends AppCompatActivity {

	RelativeLayout myProfileButton;
	RelativeLayout newPostButton;

	List<BlogPostObject> blogPostList = new ArrayList<>();

	ListView postList;
	BlogPostAdapter adapter;

	AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_app);

		setAllTheViews();
		setAllTheClickListeners();
	}

	private void setAllTheViews(){
		myProfileButton = findViewById(R.id.myProfileButton);
		newPostButton = findViewById(R.id.newPostButton);

		postList = findViewById(R.id.postList);
		adapter = new BlogPostAdapter(this);
		adapter.setData(blogPostList);
		postList.setAdapter(adapter);

		getBlogs();
	}

	private void getBlogs() {
		AppRequests.getInstance().getBlogs(client, new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

				try {
					JSONArray blog_list = response.getJSONArray("blog_list");

					blogPostList = new ArrayList<>();

					for(int i = 0; i < blog_list.length(); i++){
						JSONObject currentObject = blog_list.getJSONObject(i);

						BlogPostObject tempObject = new BlogPostObject();
						tempObject.setAuthor_handle(currentObject.getString("author_handle"));
						tempObject.setContent(currentObject.getString("content"));
						tempObject.setSlug(currentObject.getString("slug"));
						tempObject.setTitle(currentObject.getString("title"));

						blogPostList.add(tempObject);
					}

					adapter.setData(blogPostList);
					adapter.notifyDataSetChanged();

				} catch (JSONException e) {
					e.printStackTrace();
				}

			}
		});
	}

	private void setAllTheClickListeners(){
		newPostButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent newIntent = new Intent(MainAppActivity.this, NewPostActivity.class);
				startActivity(newIntent);
			}
		});

		myProfileButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//TODO GO TO MY PROFILE BUTTON
			}
		});
	}
}
