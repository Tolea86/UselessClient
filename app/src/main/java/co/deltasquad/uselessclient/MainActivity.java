package co.deltasquad.uselessclient;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.conn.ssl.AllowAllHostnameVerifier;

public class MainActivity extends FragmentActivity {

	EditText handleEdit;
	EditText passwordEdit;

	RelativeLayout loginButton;
	RelativeLayout registerButton;

	AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);

	SharedPreferences sPref;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		sPref = getSharedPreferences("USELESS", MODE_PRIVATE);

		setAllTheViews();
		setAllTheClickListeners();
	}

	private void setAllTheViews(){
		handleEdit = findViewById(R.id.handleEdit);
		passwordEdit = findViewById(R.id.passwordEdit);

		loginButton = findViewById(R.id.loginButton);
		registerButton = findViewById(R.id.registerButton);
	}

	private void setAllTheClickListeners(){
		registerButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent newIntent = new Intent(MainActivity.this, RegisterActivity.class);
				startActivity(newIntent);
			}
		});

		loginButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					loginUser();
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}

	private void loginUser() throws UnsupportedEncodingException, JSONException {
		boolean check = checkAllFields();

		if(check) {
			AppRequests.getInstance().loginUser(MainActivity.this, "@" + handleEdit.getText().toString(), passwordEdit.getText().toString(), client, new JsonHttpResponseHandler(){
				@Override
				public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
					try {
						String auth_token = response.getString("access_token");

						sPref.edit().putString("token", auth_token).putString("current_user_name", handleEdit.getText().toString()).apply();

						Intent newIntent = new Intent(MainActivity.this, MainAppActivity.class);
						startActivity(newIntent);

					} catch (JSONException e) {
						e.printStackTrace();
					}
				}

				@Override
				public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
					AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
					builder.setTitle("Error").setMessage("Wrong username or password").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
						}
					}).show();
				}

				@Override
				public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
					AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
					builder.setTitle("Error").setMessage("Wrong username or password").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
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
		if(handleEdit.getText().toString().length() == 0 || passwordEdit.getText().toString().length() == 0) {

			AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
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
