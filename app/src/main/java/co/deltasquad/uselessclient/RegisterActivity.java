package co.deltasquad.uselessclient;

import android.content.DialogInterface;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;

public class RegisterActivity extends FragmentActivity {

	RelativeLayout backButton;
	RelativeLayout registerButton;

	EditText handleEdit;
	EditText nameEdit;
	EditText passwordEdit;

	AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);

		setAllTheViews();
		setAllTheClickListeners();
	}

	private void setAllTheViews() {
		backButton = findViewById(R.id.backButton);
		registerButton = findViewById(R.id.registerButton);

		handleEdit = findViewById(R.id.handleEdit);
		nameEdit = findViewById(R.id.nameEdit);
		passwordEdit = findViewById(R.id.passwordEdit);
	}

	private void setAllTheClickListeners() {
		backButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		registerButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					registerUser();
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}

	private void registerUser() throws UnsupportedEncodingException, JSONException {
		boolean check = checkAllFields();

		if(check) {
			AppRequests.getInstance().createUser(RegisterActivity.this,"@" + handleEdit.getText().toString(), nameEdit.getText().toString(), passwordEdit.getText().toString(), client, new JsonHttpResponseHandler() {
				@Override
				public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
					AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
					builder.setTitle("Success").setMessage("You have successfully created new user!").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();

							finish();
						}
					}).setCancelable(false).show();
				}

				@Override
				public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
					AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
					builder.setTitle("Error").setMessage("User can't be created because handle is already taken").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
						}
					}).show();
				}
			});
		}
	}

	private boolean checkAllFields() {
		if(handleEdit.getText().toString().length() == 0 || nameEdit.getText().toString().length() == 0 || passwordEdit.getText().toString().length() == 0) {

			AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
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
