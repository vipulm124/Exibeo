package com.irinnovative.exibeo;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.irinnovative.exibeo.util.ConnectionDetector;
import com.irinnovative.exibeo.util.Const;
import com.irinnovative.exibeo.util.Pref;
import com.irinnovative.exibeo.util.Util;

import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class LandingScreen extends BaseActivity implements OnClickListener {

	AlertDialog alertDialog = null;
	String focusPoint = null;
	private Context context;
	Button btnFocusAcknowledge, btnNewStore, btnLeave;
	private TextView textViewWelcome, textViewCompany, textViewReportingTo,
			textViewPosition, textViewFocusPoint;
	ConnectionDetector cd;
	Boolean isInternetPresent = false;
	private String name, company, position, reportingTo, focusDescription,
			focusId;
	private String focusStatus;
	boolean isTest = Const.CONST_IS_TEST;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.landingscreen);
		context = getBaseContext();

		initialiseViews();
		initialiseListeners();
		if (isTest) {
			token = Const.TEST_TOKEN;
			email = Const.TEST_EMAIL;
		}
		if (!Const.CONST_IS_TEST)
			FillPersonalData();

		GetFocusPoint(email, token);

	}

	private void FillPersonalData() {

		name = Pref.Read(getApplicationContext(), Const.PREF_NAME);
		position = Pref.Read(getApplicationContext(), Const.PREF_POSITION);
		reportingTo = Pref.Read(getApplicationContext(), Const.PREF_REPORT_TO);
		company = Pref.Read(getApplicationContext(), Const.PREF_COMPANY);
		textViewWelcome.setText(name);
		textViewCompany.setText(company);
		textViewReportingTo.setText(reportingTo);
		textViewPosition.setText(position);
	}

	private void initialiseListeners() {
		btnFocusAcknowledge.setOnClickListener(this);
		btnLeave.setOnClickListener(this);
		btnNewStore.setOnClickListener(this);
	}

	private void initialiseViews() {
		btnFocusAcknowledge = (Button) findViewById(R.id.btn_focusacknowledgement);
		btnNewStore = (Button) findViewById(R.id.btn_storeaudit);
		btnLeave = (Button) findViewById(R.id.btn_leave);
		textViewCompany = (TextView) findViewById(R.id.textviewcompanyresults);
		textViewWelcome = (TextView) findViewById(R.id.textviewwelcomeresult);
		textViewPosition = (TextView) findViewById(R.id.textviewpositionresults);
		textViewReportingTo = (TextView) findViewById(R.id.textviewreportingresults);
		textViewFocusPoint = (TextView) findViewById(R.id.textviewfocusreults);
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		case R.id.btn_focusacknowledgement:

			cd = new ConnectionDetector(this);
			isInternetPresent = cd.isConnectingToInternet();
			if (isInternetPresent) {
				AcknowledgeFocusPoint(email, token, focusId);
				// new FocusAcknowledgement(this, email, token,
				// focusId).execute();

			} else {
				Toast.makeText(context, "No internet Present",
						Toast.LENGTH_LONG).show();
			}

			break;
		case R.id.btn_storeaudit:
			if (btnFocusAcknowledge.getText().equals("Acknowledge")) {
				Toast.makeText(context,
						"Please acknowledge the focus point first",
						Toast.LENGTH_LONG).show();
				return;
			}
			cd = new ConnectionDetector(this);
			isInternetPresent = cd.isConnectingToInternet();
			if (isInternetPresent) {
				new SelectStoreTask(this, email, token).execute();

			} else {
				Toast.makeText(context, "No internet Present",
						Toast.LENGTH_LONG).show();
			}

			break;
		case R.id.btn_leave:
			if (btnFocusAcknowledge.getText().equals("Acknowledge")) {
				Toast.makeText(context,
						"Please acknowledge the focus point first",
						Toast.LENGTH_LONG).show();
				return;
			}
			Intent leaveApplicationForm = new Intent(this, LeaveForm.class);
			leaveApplicationForm.putExtra(Const.EXTRA_EMAIL_ID, email);
			leaveApplicationForm.putExtra(Const.EXTRA_TOKEN, token);

			startActivity(leaveApplicationForm);

			break;

		default:
			break;
		}

	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub

		//
		//
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		// alertDialogBuilder.setTitle("Exebio Retail Solutions");
		alertDialogBuilder.setTitle("Exebio");
		alertDialogBuilder.setIcon(R.drawable.ic_launcher);
		alertDialogBuilder
				.setMessage("Are you Sure, you want To Exit")
				.setCancelable(false)
				.setPositiveButton("Yes, Exit",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// if this button is clicked, close
								// current activity

								finish();

							}
						})
				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// if this button is clicked, just close
								// the dialog box and do nothing

								alertDialog.dismiss();
							}
						});

		// create alert dialog
		alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();

	}

	private void GetFocusPoint(String email, String token) {
		log("Stored email is : " + email);
		log("Stored token is : " + token);

		String URL = Const.URL_API_GET_FOCUS_POINT + email
				+ Const.URL_SEPARATOR + token;

		log("URL to fetch focus point :" + URL);
		AQuery aq = new AQuery(getApplicationContext());
		ProgressDialog dialog = Util.getProgressDialog(this,
				"Getting Focus Point", "Please Wait...");

		aq.progress(dialog).ajax(URL, JSONArray.class,
				new AjaxCallback<JSONArray>() {
					@Override
					public void callback(String url, JSONArray object,
							AjaxStatus status) {
						super.callback(url, object, status);
						ParseResult(status, object);
					}
				});

	}

	private void ParseResult(AjaxStatus status, JSONArray obj) {
		try {

			int httpCode = status.getCode();
			if (httpCode == HttpStatus.SC_OK) {
				if (obj != null) {
					FillData(obj);
				} else
					Util.showErrorMessage(getApplicationContext(), httpCode);
			} else
				Util.showErrorMessage(getApplicationContext(), httpCode);

		} catch (Exception e) {
			Util.showException(getApplicationContext(), e);
		}
	}

	private void FillData(JSONArray array) {
		try {
			JSONObject objStatus = array.getJSONObject(0);
			int responseCode = Integer.parseInt(objStatus.getString("Focus"));
			if (responseCode == Const.RESPONSE_100) {
				JSONObject objData = array.getJSONObject(1);

				focusDescription = objData.getString("Description");
				focusId = objData.getString("Focus");
				focusStatus = objData.getString("FocusStatus");
				log("focusDesc:" + focusDescription);
				log("focusid:" + focusId);
				log("focusStatus:" + focusStatus);

				textViewFocusPoint.setText(focusDescription);
				if (focusStatus.trim().equalsIgnoreCase(
						"Focus Point already seen")) {

					disableAcknowledgeButton();

				} else if (focusStatus.trim().equalsIgnoreCase(
						"Focus Point not seen")) {
					enableAcknowledgeButton();
				}
			} else if (responseCode == 216) {
				Toast.makeText(getApplicationContext(),
						"Invalid session. Please login again",
						Toast.LENGTH_SHORT).show();
				// Clear session
				Util.Logout(getApplicationContext(), true);
				finish();

			}
		} catch (Exception e) {
			Util.showException(getApplicationContext(), e);
		}

	}

	private void enableAcknowledgeButton() {
		btnFocusAcknowledge.setTop(20);
		btnFocusAcknowledge.setText("Acknowledge");
		btnFocusAcknowledge.setEnabled(true);
		btnFocusAcknowledge.setBackgroundResource(R.color.theme_blue);
	}

	private void disableAcknowledgeButton() {
		btnFocusAcknowledge.setTop(50);
		btnFocusAcknowledge.setText("Acknowledged");
		btnFocusAcknowledge.setEnabled(false);
		btnFocusAcknowledge.setBackgroundResource(R.color.light_gray);
		btnFocusAcknowledge.setTextColor(Color.BLACK);
	}

	private void AcknowledgeFocusPoint(String email, String token,
			String focusID) {
        String currentDate="";
        try {
            currentDate=Util.getCurrentDate();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if(currentDate==null || currentDate.isEmpty()){
            currentDate = Util.getCurrentDateOLD();
        }
		String URL = Const.URL_API_ACKNOWLEDGE_FOCUS_POINT + email
				+ Const.URL_SEPARATOR + token + Const.URL_SEPARATOR + focusID
				+ Const.URL_SEPARATOR + currentDate + Const.URL_SEPARATOR
				+ "12N12N20.00";
		log("Acknowledge URL:" + URL);
		AQuery aq = new AQuery(getApplicationContext());
		ProgressDialog dialog = Util.getProgressDialog(this,
				"Acknowledging Focus Point", "Please Wait...");

		aq.progress(dialog).ajax(URL, JSONObject.class,
				new AjaxCallback<JSONObject>() {
					@Override
					public void callback(String url, JSONObject obj,
							AjaxStatus status) {
						super.callback(url, obj, status);
						try {
							int httpCode = status.getCode();
							if (httpCode == HttpStatus.SC_OK) {
								if (obj != null) {
									log("Acknowledge result:" + obj.toString());
									AcknowledgeFocusPointPost(obj);
								} else
									Util.showErrorMessage(
											getApplicationContext(), httpCode);
							} else
								Util.showErrorMessage(getApplicationContext(),
										httpCode);

						} catch (Exception e) {
							Util.showException(getApplicationContext(), e);
						}
					}

					private void AcknowledgeFocusPointPost(JSONObject obj) {
						try {
							int StatusCode = Integer.parseInt(obj
									.getString("StatusCode"));
							disableAcknowledgeButton();
							if (StatusCode == Const.RESPONSE_FOCUS_ACKNOWLEDGED_SUCCESS) {
								Toast.makeText(
										getApplicationContext(),
										"Focus Point Acknowledged Successfully.",
										Toast.LENGTH_SHORT).show();
							} else if (StatusCode == Const.RESPONSE_FOCUS_ALREADY_SEEN) {
								Toast.makeText(
										getApplicationContext(),
										"Focus Point already acknowledge by user.",
										Toast.LENGTH_SHORT).show();
							} else if (StatusCode == Const.RESPONSE_TOKEN_NOT_MATCHED) {
								Toast.makeText(getApplicationContext(),
										"Invalid session. Please login again",
										Toast.LENGTH_SHORT).show();
								// Clear session
								Util.Logout(getApplicationContext(), true);
								finish();
							} else {
								enableAcknowledgeButton();
								Toast.makeText(
										getApplicationContext(),
										"Acknowledgement service is not available at this time. Please try later",
										Toast.LENGTH_SHORT).show();
							}

						} catch (JSONException e) {
							Util.showException(getApplicationContext(), e);
							e.printStackTrace();
						}

					}
				});

	}

	private void log(String string) {
		Log.d(Const.DEBUG_TAG, "LandingActivity: " + string);

	}

}
