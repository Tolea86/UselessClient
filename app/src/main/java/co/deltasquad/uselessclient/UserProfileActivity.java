package co.deltasquad.uselessclient;

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

public class UserProfileActivity extends BlogActivity {

	RelativeLayout backButton;

	TextView userHandle;
	TextView userName;

	List<BlogPostObject> blogPostObjectList = new ArrayList<>();

	ListView postList;
	BlogPostAdapter adapter;

	AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_profile);

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
		AppRequests.getInstance().getUserByHandle(GlobalClass.selectedUserHandle, client, new JsonHttpResponseHandler(){
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
		adapter = new BlogPostAdapter(this, UserProfileActivity.this);
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
}
