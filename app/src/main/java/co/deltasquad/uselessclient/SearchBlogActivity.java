package co.deltasquad.uselessclient;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;

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

public class SearchBlogActivity extends BlogActivity {

	RelativeLayout backButton;

	SearchView searchView;

	List<BlogPostObject> blogPostObjectList = new ArrayList<>();

	AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);

	ListView blogList;
	BlogPostAdapter adapter;

	String title = "";

	SharedPreferences sPref;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_blog);

		sPref = getSharedPreferences("USELESS", MODE_PRIVATE);

		client.addHeader("Authorization", "Bearer " + sPref.getString("token", ""));

		setAllTheViews();
		setAllTheClickListeners();

		getBlogs();
	}

	private void getBlogs(){
		AppRequests.getInstance().getBlogsByPartialTitle(title, client, new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

				try {
					JSONArray blog_list = response.getJSONArray("results");

					blogPostObjectList = new ArrayList<>();

					for(int i = 0; i < blog_list.length(); i++){
						JSONObject currentObject = blog_list.getJSONObject(i);

						BlogPostObject tempObject = new BlogPostObject();
						tempObject.setAuthor_handle(currentObject.getString("author_handle"));
						tempObject.setContent(currentObject.getString("content"));
						tempObject.setSlug(currentObject.getString("slug"));
						tempObject.setTitle(currentObject.getString("title"));

						blogPostObjectList.add(tempObject);
					}

					adapter.setData(blogPostObjectList);
					adapter.notifyDataSetChanged();

				} catch (JSONException e) {
					e.printStackTrace();
				}

			}
		});
	}

	private void setAllTheViews(){
		backButton = findViewById(R.id.backButton);

		searchView = findViewById(R.id.searchView);

		blogList = findViewById(R.id.blogList);
		adapter = new BlogPostAdapter(this, SearchBlogActivity.this);
		adapter.setData(blogPostObjectList);
		blogList.setAdapter(adapter);
	}

	private void setAllTheClickListeners(){
		backButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});

		searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String query) {
				return false;
			}

			@Override
			public boolean onQueryTextChange(String newText) {

				title = newText;

				getBlogs();

				return false;
			}
		});
	}

	@Override
	public void authorHandleClicked(String handle) {
		GlobalClass.selectedUserHandle = handle;

		Intent newIntent = new Intent(SearchBlogActivity.this, UserProfileActivity.class);
		startActivity(newIntent);
	}

	@Override
	public void deletePostClicked(final String slug) {
		AlertDialog.Builder builder = new AlertDialog.Builder(SearchBlogActivity.this);
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

		Intent newIntent = new Intent(SearchBlogActivity.this, NewPostActivity.class);
		startActivity(newIntent);
	}
}
