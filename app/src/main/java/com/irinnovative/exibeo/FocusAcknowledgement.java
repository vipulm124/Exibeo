package com.irinnovative.exibeo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.view.Gravity;

public class FocusAcknowledgement extends AsyncTask<Void, Void, String> {

	private ProgressDialog dialog;
	private Context context;
	private String email;
	private String token;
	private String focusId;
	AlertDialog alertDialog = null;

	public FocusAcknowledgement(Context context, String email, String token,
			String focusId) {
		// TODO Auto-generated constructor stub

		this.token = token;
		this.email = email;
		this.context = context;
		this.focusId = focusId;
	}

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
		dialog = new ProgressDialog(context);
		dialog.setCancelable(false);
		dialog.setMessage("Acknowledging Focus Point.Please Wait...");
		dialog.getWindow().setGravity(Gravity.BOTTOM);
		dialog.show();
	}

	@Override
	protected void onPostExecute(String result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		dialog.dismiss();
		try {
			JSONObject obj = new JSONObject(result);
			int statusCode = Integer.parseInt(obj.getString("StatusCode"));

			if (statusCode == 120) {
				String msg = obj.getString("Msg");
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						context);
				// alertDialogBuilder.setTitle("Exebio Retail Solutions");
				alertDialogBuilder.setIcon(R.drawable.exebiologo);
				alertDialogBuilder
						.setMessage(msg)
						.setCancelable(false)
						.setPositiveButton("Ok",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										// if this button is clicked, close
										// current activity

										alertDialog.dismiss();

									}
								});
				// create alert dialog
				alertDialog = alertDialogBuilder.create();

				// show it
				alertDialog.show();

			} else {

				String msg = obj.getString("Msg");
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						context);
				// alertDialogBuilder.setTitle("Exebio Retail Solutions");
				alertDialogBuilder.setIcon(R.drawable.exebiologo);
				alertDialogBuilder
						.setMessage(msg)
						.setCancelable(false)
						.setPositiveButton("Ok",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										// if this button is clicked, close
										// current activity

										alertDialog.dismiss();

									}
								});
				// create alert dialog
				alertDialog = alertDialogBuilder.create();

				// show it
				alertDialog.show();

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	protected String doInBackground(Void... arg0) {
		// TODO Auto-generated method stub
		final String USER_AGENT = "Mozilla/5.0";
		StringBuffer response = null;
		String url = "http://exibeoapi.clickcabs.co.za/ExibeoService.svc/Acknowledgment/"
				+ email + "/" + token + "/23/12-12-2104/12N12N20.00";

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

}
