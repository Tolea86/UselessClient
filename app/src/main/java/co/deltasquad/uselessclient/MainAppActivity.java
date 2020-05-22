package co.deltasquad.uselessclient;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
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

public class MainAppActivity extends BlogActivity {

	RelativeLayout myProfileButton;
	RelativeLayout newPostButton;
	RelativeLayout searchUserButton;
	RelativeLayout searchBlogButton;

	List<BlogPostObject> blogPostList = new ArrayList<>();

	ListView postList;
	BlogPostAdapter adapter;

	AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);

	SharedPreferences sPref;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_app);

		sPref = getSharedPreferences("USELESS", MODE_PRIVATE);

		client.addHeader("Authorization", "Bearer " + sPref.getString("token", ""));

		setAllTheViews();
		setAllTheClickListeners();
	}

	private void setAllTheViews(){
		myProfileButton = findViewById(R.id.myProfileButton);
		newPostButton = findViewById(R.id.newPostButton);
		searchUserButton = findViewById(R.id.searchUserButton);
		searchBlogButton = findViewById(R.id.searchBlogButton);

		postList = findViewById(R.id.postList);
		adapter = new BlogPostAdapter(this, MainAppActivity.this);
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

				GlobalClass.updatePost = false;

				Intent newIntent = new Intent(MainAppActivity.this, NewPostActivity.class);
				startActivity(newIntent);
			}
		});

		searchBlogButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent newIntent = new Intent(MainAppActivity.this, SearchBlogActivity.class);
				startActivity(newIntent);
			}
		});

		myProfileButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent newIntent = new Intent(MainAppActivity.this, MyProfileActivity.class);
				startActivity(newIntent);
			}
		});

		searchUserButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent newIntent = new Intent(MainAppActivity.this, SearchUserActivity.class);
				startActivity(newIntent);
			}
		});
	}

	@Override
	public void authorHandleClicked(String handle) {
		GlobalClass.selectedUserHandle = handle;

		Intent newIntent = new Intent(MainAppActivity.this, UserProfileActivity.class);
		startActivity(newIntent);
	}

	@Override
	public void deletePostClicked(final String slug) {
		AlertDialog.Builder builder = new AlertDialog.Builder(MainAppActivity.this);
		builder.setTitle("Confirmation").setMessage("Are you sure you want to delete selected post?").setNegativeButton("No", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		}).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();

				AppRequests.getInstance().deletePost(slug, client, new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
						getBlogs();
					}

					@Override
					public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
						super.onFailure(statusCode, headers, throwable, errorResponse);
					}
				});
			}
		}).show();
	}

	@Override
	public void editPostClicked(String slug) {
		GlobalClass.selectedBlogSlug = slug;
		GlobalClass.updatePost = true;

		Intent newIntent = new Intent(MainAppActivity.this, NewPostActivity.class);
		startActivity(newIntent);
	}
}
