package com.irinnovative.exibeo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.irinnovative.exibeo.util.Const;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Gravity;

public class SelectStoreTask extends AsyncTask<Boolean, Void, String> {

	private ProgressDialog dialog;
	private Context context;
	private String emailAddress;
	private String token;
	ArrayList<String> storeList;
	ArrayList<String> storeId;
	ArrayList<String> storeListId;
	AlertDialog alertDialog = null;

	public SelectStoreTask(Context context, String emailAddress, String token) {

		this.context = context;
		this.emailAddress = emailAddress;
		this.token = token;
	}

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();

		dialog = new ProgressDialog(context);
		dialog.setCancelable(false);
		dialog.setMessage("Getting Store List...");
		dialog.getWindow().setGravity(Gravity.BOTTOM);
		dialog.show();
	}

	@Override
	protected String doInBackground(Boolean... arg0) {
		// TODO Auto-generated method stub
		final String USER_AGENT = "Mozilla/5.0";
		StringBuffer response = null;
		String url = Const.URL_API_SELECT_STORE + emailAddress
				+ Const.URL_SEPARATOR + token;
		log("Url to get stores:" + url);
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
		} catch (Exception e) {
			e.printStackTrace();
		}

		return response.toString();
	}

	@Override
	protected void onPostExecute(String result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		dialog.dismiss();
		storeList = new ArrayList<String>();
		storeId = new ArrayList<String>();
		storeListId = new ArrayList<String>();
		try {
			JSONArray jsonArray = new JSONArray(result.toString());
			int count = jsonArray.length();
			for (int i = 1; i < count; i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				String storeName = jsonObject.getString("StoreName");
				String sId = jsonObject.getString("StoreId");
				storeList.add(storeName);
				storeId.add(sId);
				storeListId.add(storeName + "~" + sId);

			}

			Intent selectStore = new Intent(context, SelectStore.class);
			selectStore.putStringArrayListExtra(Const.EXTRA_STORE_LIST,
					storeList);
			selectStore.putStringArrayListExtra(Const.EXTRA_STORE_LIST_IDS,
					storeListId);
			selectStore.putExtra(Const.EXTRA_EMAIL_ID, emailAddress);
			selectStore.putExtra(Const.EXTRA_TOKEN, token);
			context.startActivity(selectStore);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	private void log(String string) {
		Log.d(Const.DEBUG_TAG, "Async Get store: " + string);

	}

}
