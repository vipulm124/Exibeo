package com.irinnovative.exibeo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.irinnovative.exibeo.util.Const;
import com.irinnovative.exibeo.util.GPSTracker;
import com.irinnovative.exibeo.util.Util;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;

public class VisitStartTask extends AsyncTask<Boolean, Void, String> {

	private ProgressDialog dialog;
	private Context context;
	private String emailAddress;
	private String token;
	private String storeId;
	Activity activity;
	double dLatitude = 0.0;
	double dLongitude = 0.0;

	public VisitStartTask(Activity a, String emailAddress, String token,
			String storeId) {
		activity = a;
		this.context = a.getApplicationContext();
		this.emailAddress = emailAddress;
		this.token = token;
		this.storeId = storeId;
		GPSTracker tracker = new GPSTracker(context);
		if (tracker.canGetLocation()) {
			dLongitude = tracker.getLongitude();
			dLatitude = tracker.getLatitude();
			log("Longitude:" + dLongitude);
			log("Latitude:" + dLatitude);
		}
	}

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();

		dialog = new ProgressDialog(activity);
		dialog.setCancelable(false);
		dialog.setMessage("Visiting Starts.Please Wait...");
		dialog.getWindow().setGravity(Gravity.BOTTOM);
		dialog.show();
	}

	@Override
	protected String doInBackground(Boolean... arg0) {
		// TODO Auto-generated method stub
		final String USER_AGENT = "Mozilla/5.0";
		StringBuffer response = null;
		String currentDate="";
		String currentTime="";
		try {
			currentDate=Util.getCurrentDate();
			currentTime = Util.getCurrentTime();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		String url = Const.URL_API_STORE_VISIT_START + emailAddress
				+ Const.URL_SEPARATOR + token + Const.URL_SEPARATOR + storeId
				+ Const.URL_SEPARATOR + currentDate
				+ Const.URL_SEPARATOR + currentTime
				+ Const.URL_SEPARATOR + dLongitude + Const.URL_SEPARATOR
				+ dLatitude;
		log("URL:" + url);
		try {
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();

			// optional default is GET
			con.setRequestMethod("GET");

			// add request header
			con.setRequestProperty("User-Agent", USER_AGENT);

			int responseCode = con.getResponseCode();
			System.out.println("\nSending 'GET' request to URL : " + url);
			System.out.println("Response Code : " + responseCode);

			BufferedReader in = new BufferedReader(new InputStreamReader(
					con.getInputStream()));
			String inputLine;
			response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			// print result
			System.out.println(response.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		log("response:" + response.toString());
		return response.toString();

	}

	@Override
	protected void onPostExecute(String result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		dialog.dismiss();
		try {
			JSONObject obj = new JSONObject(result);
			int statusCode = Integer.parseInt(obj.getString("StatusCode"));
			if (statusCode == 109) {
				// String status = obj.getString("Status");
				String storeID = obj.getString("StoreId");
				String visitID = obj.getString("VisitId");
				Intent questionActivity = new Intent(context,
						QuestionActivity.class);
				questionActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				questionActivity.putExtra(Const.EXTRA_EMAIL_ID, emailAddress);
				questionActivity.putExtra(Const.EXTRA_TOKEN, token);
				questionActivity.putExtra(Const.EXTRA_STORE_ID, storeID);
				questionActivity.putExtra(Const.EXTRA_VISIT_ID, visitID);
				context.startActivity(questionActivity);
				activity.finish();
			} else {
				Toast.makeText(context, obj.getString("status"),
						Toast.LENGTH_LONG).show();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void log(String string) {
		Log.d(Const.DEBUG_TAG, "Visit Start Task: " + string);

	}

}
