package com.irinnovative.exibeo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

public class GetFocusPoint extends AsyncTask<Boolean, Void, String> {

	private ProgressDialog dialog;
	private Context context;
	private String emailAddress;
	private String name;
	private String position;
	private String reportTo;
	private String company;
	private String token;
	private String focusStatus;

	public GetFocusPoint(Context context, String name, String position,
			String reportTo, String company, String token, String emailAddress) {

		this.context = context;
		this.emailAddress = emailAddress;
		this.token = token;
		this.company = company;
		this.position = position;
		this.name = name;
		this.reportTo = reportTo;
	}

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();

		dialog = new ProgressDialog(context);
		dialog.setCancelable(false);
		dialog.setMessage("Logging In.Please Wait...");
		dialog.getWindow().setGravity(Gravity.BOTTOM);
		dialog.show();
	}

	@Override
	protected String doInBackground(Boolean... arg0) {
		// TODO Auto-generated method stub
		final String USER_AGENT = "Mozilla/5.0";
		StringBuffer response = null;
		String url = "http://api.exibeo.co.za/ExibeoService.svc/FocusPoint/12-12-12"
				+ "/" + emailAddress + "/" + token;

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

		return response.toString();

	}

	@Override
	protected void onPostExecute(String result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		String focusDescription = null;
		String focusId = null;
		dialog.dismiss();
		try {

			JSONArray jsonArray = new JSONArray(result.toString());
			int count = jsonArray.length();
			for (int i = 1; i < count; i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				focusDescription = jsonObject.getString("Description");
				focusId = jsonObject.getString("Focus");
				focusStatus = jsonObject.getString("FocusStatus");

			}

			Toast.makeText(context, "Login Successfully", Toast.LENGTH_LONG)
					.show();
			Intent landingScreen = new Intent(context, LandingScreen.class);
			landingScreen.putExtra("Name", name);
			landingScreen.putExtra("Position", position);
			landingScreen.putExtra("ReportTo", reportTo);
			landingScreen.putExtra("Company", company);
			landingScreen.putExtra("Token", token);
			landingScreen.putExtra("Email", emailAddress);
			landingScreen.putExtra("FocusId", focusId);
			landingScreen.putExtra("FocusDescription", focusDescription);
			landingScreen.putExtra("FocusStatus", focusStatus);

			context.startActivity(landingScreen);
			((Activity) context).finish();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
