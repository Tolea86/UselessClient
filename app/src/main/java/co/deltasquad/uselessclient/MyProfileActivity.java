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
import android.widget.TextView;

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

public class MyProfileActivity extends BlogActivity {

	RelativeLayout backButton;

	TextView userHandle;
	TextView userName;

	List<BlogPostObject> blogPostObjectList = new ArrayList<>();

	ListView postList;
	BlogPostAdapter adapter;

	AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);

	SharedPreferences sPref;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_profile);

		sPref = getSharedPreferences("USELESS", MODE_PRIVATE);

		setAllTheViews();
		setAllTheClickListeners();

		getUserInformation();
	}

	private void getUserPosts() {
		AppRequests.getInstance().getBlogByPartialuserName(userName.getText().toString(), client, new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				try {
					JSONArray results = response.getJSONArray("results");

					blogPostObjectList = new ArrayList<>();

					for(int i = 0; i < results.length(); i++){
						JSONObject currentObject = results.getJSONObject(i);

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

	private void getUserInformation(){
		AppRequests.getInstance().getUserByHandle("@" + sPref.getString("current_user_name", ""), client, new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				try {
					JSONArray userList = response.getJSONArray("user_list");

					JSONObject currentObject = userList.getJSONObject(0);

					String handle = currentObject.getString("handle");
					String name = currentObject.getString("name");

					userHandle.setText(handle);
					userName.setText(name);

					getUserPosts();

				} catch (JSONException e) {
					e.printStackTrace();
				}


			}
		});
	}

	private void setAllTheViews(){
		backButton = findViewById(R.id.backButton);

		userHandle = findViewById(R.id.userHandle);
		userName = findViewById(R.id.userName);

		postList = findViewById(R.id.postList);
		adapter = new BlogPostAdapter(this, MyProfileActivity.this);
		adapter.setData(blogPostObjectList);
		postList.setAdapter(adapter);
	}

	private void setAllTheClickListeners(){
		backButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	@Override
	public void authorHandleClicked(String handle) {
		GlobalClass.selectedUserHandle = handle;

		Intent newIntent = new Intent(MyProfileActivity.this, UserProfileActivity.class);
		startActivity(newIntent);
	}

	@Override
	public void deletePostClicked(final String slug) {
		AlertDialog.Builder builder = new AlertDialog.Builder(MyProfileActivity.this);
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
						getUserPosts();
					}
				});
			}
		}).show();
	}

	@Override
	public void editPostClicked(String slug) {
		super.editPostClicked(slug);
	}
}
