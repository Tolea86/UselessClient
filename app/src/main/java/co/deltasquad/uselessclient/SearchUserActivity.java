package co.deltasquad.uselessclient;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
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

import co.deltasquad.uselessclient.Adapters.UserAdapter;
import co.deltasquad.uselessclient.Objects.UserObject;
import cz.msebera.android.httpclient.Header;

public class SearchUserActivity extends AppCompatActivity {

	RelativeLayout backButton;

	SearchView searchView;

	List<UserObject> userObjectList = new ArrayList<>();
	List<UserObject> shownObjectList = new ArrayList<>();

	ListView userList;
	UserAdapter adapter;

	String username = "";

	AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_user);

		setAllTheViews();
		setAllTheClickListeners();

		getUsers();
	}

	private void filterUsers() {
		shownObjectList = new ArrayList<>();

		for(int i = 0; i < userObjectList.size(); i++){
			if(userObjectList.get(i).getUsername().contains(username)){
				shownObjectList.add(userObjectList.get(i));
			}
		}

		adapter.setData(shownObjectList);
		adapter.notifyDataSetChanged();
	}

	private void getUsers() {
		AppRequests.getInstance().getUsers(client, new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				try {
					JSONArray user_list = response.getJSONArray("user_list");

					userObjectList = new ArrayList<>();
					shownObjectList = new ArrayList<>();

					for(int i = 0; i < user_list.length(); i++){
						JSONObject currentObject = user_list.getJSONObject(i);

						UserObject tempObject = new UserObject();
						tempObject.setHandle(currentObject.getString("handle"));
						tempObject.setUsername(currentObject.getString("name"));

						userObjectList.add(tempObject);

					}

					filterUsers();

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}

	private void setAllTheViews(){
		backButton = findViewById(R.id.backButton);

		searchView = findViewById(R.id.searchView);

		userList = findViewById(R.id.userList);
		adapter = new UserAdapter(this);
		adapter.setData(shownObjectList);
		userList.setAdapter(adapter);
	}

	private void setAllTheClickListeners(){
		backButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});

		userList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				GlobalClass.selectedUserHandle = shownObjectList.get(position).getHandle();

				Intent newIntent = new Intent(SearchUserActivity.this, UserProfileActivity.class);
				startActivity(newIntent);
			}
		});

		searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String query) {
				return false;
			}

			@Override
			public boolean onQueryTextChange(String newText) {

				username = newText;

				filterUsers();

				return false;
			}
		});
	}
}
