package com.irinnovative.exibeo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.irinnovative.exibeo.util.ConnectionDetector;
import com.irinnovative.exibeo.util.Const;
import com.irinnovative.exibeo.util.ForceUpdateAsync;
import com.irinnovative.exibeo.util.Pref;
import com.irinnovative.exibeo.util.Util;

import org.apache.http.HttpStatus;
import org.json.JSONObject;

public class LoginActivity extends Activity implements OnClickListener {

	EditText emailAddress, password;
	String mailAddress, pass;
	Button btnSignIn;
	TextView forgotPassword;
	CheckBox checkBoxRememberMe;
	ConnectionDetector cd;
	Boolean isInternetPresent = false;
	boolean rememberMe = false;
	boolean isTest = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.login_screen);
		intitialiseViews();
		initialiseListeners();
		forceUpdate();
		if (isTest) {
			emailAddress.setText("ExibeoMer");
			password.setText("123456");
		}

	}

	public void forceUpdate(){
		PackageManager packageManager = this.getPackageManager();
		PackageInfo packageInfo = null;
		try {
			packageInfo =  packageManager.getPackageInfo(getPackageName(),0);
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		String currentVersion = packageInfo.versionName;
		new ForceUpdateAsync(currentVersion,LoginActivity.this).execute();
	}

	private void initialiseListeners() {
		// TODO Auto-generated method stub
		btnSignIn.setOnClickListener(this);
		forgotPassword.setOnClickListener(this);

	}

	private void intitialiseViews() {
		// TODO Auto-generated method stub

		emailAddress = (EditText) findViewById(R.id.email_address);
		password = (EditText) findViewById(R.id.password);
		btnSignIn = (Button) findViewById(R.id.sign_in_button);
		forgotPassword = (TextView) findViewById(R.id.forgot_pass);

		checkBoxRememberMe = (CheckBox) findViewById(R.id.rememberme);
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		case R.id.sign_in_button:

			cd = new ConnectionDetector(this);
			isInternetPresent = cd.isConnectingToInternet();

			if (isInternetPresent) {
				if ((emailAddress.getText().toString().length() > 0 && password
						.getText().toString().length() > 0)) {

					if (checkBoxRememberMe.isChecked()) {
						rememberMe = true;
					}
					PerformLogin(emailAddress.getText().toString(), password
							.getText().toString());
					// new LoginTask(this, emailAddress.getText().toString(),
					// password.getText().toString(), rememberMe)
					// .execute();
				} else {
					Toast.makeText(this, "All fields are mandatory",
							Toast.LENGTH_SHORT).show();
				}
			}

			else {
				Toast.makeText(
						this,
						"No networkd provider is enabled. Please enable WiFi/Data mode",
						Toast.LENGTH_LONG).show();
			}

			break;
		case R.id.forgot_pass:
			Intent forgotPassword = new Intent(this, ForgotPassword.class);
			startActivity(forgotPassword);
			break;
		default:
			break;
		}

	}

	private void PerformLogin(String email, String password) {

		String URL = Const.URL_API_LOGIN + email + Const.URL_SEPARATOR
				+ password;
		log("Login url: " + URL);
		AQuery aq = new AQuery(getApplicationContext());
		ProgressDialog dialog = Util.getProgressDialog(this, "Logging In",
				"Please Wait...");
		ConnectionDetector detector = new ConnectionDetector(
				getApplicationContext());
		if (detector.isConnectingToInternet()) {
			aq.progress(dialog).ajax(URL, JSONObject.class,
					new AjaxCallback<JSONObject>() {
						@Override
						public void callback(String url, JSONObject object,
								AjaxStatus status) {
							super.callback(url, object, status);
							ParseResult(status, object);
						}
					});
		} else
			Toast.makeText(
					this,
					"No networkd provider is enabled. Please enable WiFi/Data mode",
					Toast.LENGTH_LONG).show();
	}

	private void ParseResult(AjaxStatus status, JSONObject obj) {
		try {
			int httpCode = status.getCode();
			if (httpCode == HttpStatus.SC_OK) {
				if (obj != null)
					PerformPostLogin(obj);
				else {
					log("obj is null");
					Util.showErrorMessage(getApplicationContext(), httpCode);
				}
			} else {
				Util.showErrorMessage(getApplicationContext(), httpCode);
				log("HTTP status code error :" + httpCode);
			}

		} catch (Exception e) {
			log("Exception while getting http code and json");
			Util.showException(getApplicationContext(), e);
		}
	}

	private void PerformPostLogin(JSONObject obj) {
		try {
			int responseCode = Integer.parseInt(obj.getString("statusCode"));
			log("Response Code:" + responseCode);
			log("Response from API:" + obj.toString());
			if (responseCode == Const.RESPONSE_100) {
				String token = obj.getString("Token");
				String name = obj.getString("Name");
				String position = obj.getString("Position");
				String reportTo = obj.getString("ReportTo");
				String company = obj.getString("Store");
				if (checkBoxRememberMe.isChecked()) {
					Pref.Write(getApplicationContext(), Const.PREF_TOKEN, token);
					Pref.WriteBoolean(getApplicationContext(),
							Const.PREF_SHOULD_REMEMBER, true);
					Pref.Write(getApplicationContext(), Const.PREF_EMAIL,
							emailAddress.getText().toString().trim());

				}
				Pref.Write(getApplicationContext(), Const.PREF_NAME, name);
				Pref.Write(getApplicationContext(), Const.PREF_POSITION,
						position);
				Pref.Write(getApplicationContext(), Const.PREF_REPORT_TO,
						reportTo);
				Pref.Write(getApplicationContext(), Const.PREF_COMPANY, company);
				fireIntent(token, name, position, reportTo, company,
						emailAddress.getText().toString().trim());

			} else if (responseCode == 201) {

				Toast.makeText(
						getApplicationContext(),
						responseCode + ":Invalid credentials, please try again",
						Toast.LENGTH_SHORT).show();
			} else if (responseCode == 203) {

				Toast.makeText(
						getApplicationContext(),
						responseCode
								+ ":This user doesn't exist. Please contact Exibeo admin",
						Toast.LENGTH_SHORT).show();
			} else {
				Util.showErrorMessage(getApplicationContext(), responseCode);
			}
		} catch (Exception e) {
			Util.showException(getApplicationContext(), e);
		}

	}

	private void fireIntent(String token, String name, String position,
			String reportTo, String company, String trim) {
		Intent intent = new Intent(this, LandingScreen.class);
		intent.putExtra(Const.EXTRA_TOKEN, token);
		// intent.putExtra(Const.EXTRA_NAME, name);
		// intent.putExtra(Const.EXTRA_POSITION, position);
		// intent.putExtra(Const.EXTRA_REPORT_TO, reportTo);
		// intent.putExtra(Const.EXTRA_COMPANY, company);
		intent.putExtra(Const.EXTRA_EMAIL_ID, emailAddress.getText().toString()
				.trim());

		startActivity(intent);
		finish();

	}

	private void log(String string) {
		Log.d(Const.DEBUG_TAG, "LoginActivity: " + string);

	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();

		this.finish();

		// Toast.makeText(this, "Back Pressed called",
		// Toast.LENGTH_LONG).show();
	}

}
