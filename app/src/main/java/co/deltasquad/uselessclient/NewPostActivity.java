package co.deltasquad.uselessclient;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;

public class NewPostActivity extends FragmentActivity {

	RelativeLayout backButton;
	RelativeLayout createPostButton;

	EditText postTitleEdit;
	EditText postContentEdit;

	TextView titleText;
	TextView buttonText;

	AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);

	SharedPreferences sPref;

	String handle = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_post);

		sPref = getSharedPreferences("USELESS", MODE_PRIVATE);

		client.addHeader("Authorization", "Bearer " + sPref.getString("token", ""));

		setAllTheViews();
		setAllTheClickListeners();

		if(GlobalClass.updatePost) {
			AppRequests.getInstance().getBlogsBySlug(GlobalClass.selectedBlogSlug, client, new JsonHttpResponseHandler(){
				@Override
				public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
					try {
						JSONArray blog_list = response.getJSONArray("blog_list");

						JSONObject blog = blog_list.getJSONObject(0);

						handle = blog.getString("author_handle");

						postTitleEdit.setText(blog.getString("title"));
						postContentEdit.setText(blog.getString("content"));

					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			});
		}
	}

	private void setAllTheViews(){
		backButton = findViewById(R.id.backButton);
		createPostButton = findViewById(R.id.createPostButton);

		titleText = findViewById(R.id.titleText);
		buttonText = findViewById(R.id.buttonText);

		postTitleEdit = findViewById(R.id.postTitleEdit);
		postContentEdit = findViewById(R.id.postContentEdit);

		if(GlobalClass.updatePost){
			titleText.setText("Edit Post");
			buttonText.setText("Update Post");
		}
		else{
			titleText.setText("New Post");
			buttonText.setText("Create Post");
		}

	}

	private void setAllTheClickListeners(){
		backButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		createPostButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				if(GlobalClass.updatePost) {
					try {
						updatePost();
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				else {

					try {
						createNewPost();
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
		});
	}

	private void updatePost() throws UnsupportedEncodingException, JSONException {
		boolean check = checkAllFields();

		if(check) {

			AppRequests.getInstance().updateBlog(NewPostActivity.this, postContentEdit.getText().toString(), postTitleEdit.getText().toString(), GlobalClass.selectedBlogSlug, handle, client, new JsonHttpResponseHandler() {
				@Override
				public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
					AlertDialog.Builder builder = new AlertDialog.Builder(NewPostActivity.this);
					builder.setTitle("Success").setMessage("You have successfully edited your post").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();

							onBackPressed();
						}
					}).setCancelable(false).show();
				}

				@Override
				public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
					super.onFailure(statusCode, headers, throwable, errorResponse);
				}
			});
		}
	}

	private void createNewPost() throws UnsupportedEncodingException, JSONException {
		boolean check = checkAllFields();

		if(check) {
			AppRequests.getInstance().addBlog(NewPostActivity.this, postContentEdit.getText().toString(), postTitleEdit.getText().toString(), client, new JsonHttpResponseHandler(){
				@Override
				public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
					AlertDialog.Builder builder = new AlertDialog.Builder(NewPostActivity.this);
					builder.setTitle("Success").setMessage("You have successfully created new post").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();

							onBackPressed();
						}
					}).setCancelable(false).show();
				}

				@Override
				public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
					AlertDialog.Builder builder = new AlertDialog.Builder(NewPostActivity.this);
					builder.setTitle("Error").setMessage("An error has occured").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();

							onBackPressed();
						}
					}).setCancelable(false).show();
				}

				@Override
				public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
					AlertDialog.Builder builder = new AlertDialog.Builder(NewPostActivity.this);
					builder.setTitle("Error").setMessage("An error has occured").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();

							onBackPressed();
						}
					}).setCancelable(false).show();
				}
			});
		}
	}

	private boolean checkAllFields() {
		if(postTitleEdit.getText().toString().length() == 0  || postContentEdit.getText().toString().length() == 0) {

			AlertDialog.Builder builder = new AlertDialog.Builder(NewPostActivity.this);
			builder.setTitle("Error").setMessage("All fields are required!").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
				}
			}).show();

			return false;
		}
		else{
			return true;
		}
	}
}
