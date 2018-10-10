package com.irinnovative.exibeo;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.irinnovative.exibeo.util.Const;
import com.irinnovative.exibeo.util.Pref;
import com.irinnovative.exibeo.util.Util;

import org.apache.http.HttpStatus;
import org.json.JSONObject;

public class BaseActivity extends Activity {

	public String token;
	public String email;
	AlertDialog alertDialog = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActionBar actionBar = getActionBar();
		if (actionBar != null) {
			// actionBar.setHomeButtonEnabled(true);
			actionBar.setDisplayShowTitleEnabled(false);
			actionBar.setIcon(R.drawable.exebiologo);
			actionBar.setBackgroundDrawable(new ColorDrawable(Color
					.parseColor("#E6E6E6")));
		}
		token = getIntent().getStringExtra(Const.EXTRA_TOKEN);
		log("Token:" + token);
		email = getIntent().getStringExtra(Const.EXTRA_EMAIL_ID);
		validateSession(token, email);
	}

	private void validateSession(String token, String email) {
		if (token == null || email == null) {
			SendToLoginScreen();
			return;
		}
		if (token.length() < 5 || email.length() < 2) {
			SendToLoginScreen();
			return;
		}
	}

	private void SendToLoginScreen() {
		Pref.Write(getApplicationContext(), Const.PREF_TOKEN, "");
		Pref.WriteBoolean(getApplicationContext(), Const.PREF_SHOULD_REMEMBER,
				false);
		Toast.makeText(getApplicationContext(),
				"Your Session is invalid. Please login Again",
				Toast.LENGTH_SHORT).show();
		Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(intent);
		finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);

		return super.onCreateOptionsMenu(menu);

	}

	public boolean onOptionsItemSelected(MenuItem item) {
		// Take appropriate action for each action item click
		switch (item.getItemId()) {

		case R.id.action_logout:
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					this);
			alertDialogBuilder.setTitle("Exebio");
			alertDialogBuilder.setIcon(R.drawable.ic_launcher);
			alertDialogBuilder
					.setMessage("Are you sure you want to Logout?")

					.setPositiveButton("Yes, Logout",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {

									alertDialog.dismiss();
									performLogout(email, token);
								}
							})

					.setNegativeButton("Cancel",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									alertDialog.dismiss();
								}
							});
			// create alert dialog
			alertDialog = alertDialogBuilder.create();

			// show it
			alertDialog.show();

			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void performLogout(String email, String token) {

		String URL = Const.URL_API_LOGOUT + email + Const.URL_SEPARATOR + token;
		log("Logout url:" + URL);
		AQuery aq = new AQuery(getApplicationContext());
		ProgressDialog dialog = Util.getProgressDialog(this, "Logging Out",
				"Please Wait...");

		aq.progress(dialog).ajax(URL, JSONObject.class,
				new AjaxCallback<JSONObject>() {
					@Override
					public void callback(String url, JSONObject object,
							AjaxStatus status) {
						super.callback(url, object, status);
						ParseResult(status, object);
					}
				});

	}

	private void ParseResult(AjaxStatus status, JSONObject obj) {
		Util.Logout(getApplicationContext(), false);
		Intent logOut = new Intent(this, LoginActivity.class);
		startActivity(logOut);
		finish();
		try {
			int httpCode = status.getCode();
			if (httpCode == HttpStatus.SC_OK) {
				if (obj != null) {
					log("Logout Response from server:" + obj.toString());
					String responseCode = obj.getString("StatusCode");
					try {
						int code = Integer.parseInt(responseCode);
						if (code == Const.RESPONSE_LOGOUT_SUCCESS) {
							log("Logout success");
						} else
							log("Logout failed. code:" + code);
					} catch (Exception e) {
						log("Response code can not be parsed: " + responseCode);
					}
				} else
					log("ERR: code:" + httpCode);
			} else
				log("ERR: code:" + httpCode);

		} catch (Exception e) {
			log("Exception:" + e.getCause());
		}
	}

	private void log(String string) {
		Log.d(Const.DEBUG_TAG, "BaseActivity: " + string);

	}

}
