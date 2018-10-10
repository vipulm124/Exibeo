package com.irinnovative.exibeo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.Gravity;
import android.widget.Toast;

public class ForgotPasswordTask extends AsyncTask<Void, Void, String> {

	
	private ProgressDialog dialog;
	private Context context;
	private String email;
	
	
	
	public ForgotPasswordTask(Context context, String email) {
		// TODO Auto-generated constructor stub
		this.context = context;
		this.email = email;
	}

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
		dialog = new ProgressDialog(context);
		dialog.setCancelable(false);
		dialog.setMessage("Sending Password to the Registered Email.Please Wait...");
		dialog.getWindow().setGravity(Gravity.BOTTOM); 
		dialog.show();
	}
	
	@Override
	protected String doInBackground(Void... arg0) {
		// TODO Auto-generated method stub
		final String USER_AGENT = "Mozilla/5.0";
		StringBuffer response = null;
		String url = "http://api.exibeo.co.za/ExibeoService.svc/ForgotPassword/"+email;
		
		 
		try
		{
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
 
		// optional default is GET
		con.setRequestMethod("GET");
 
		//add request header
		con.setRequestProperty("User-Agent", USER_AGENT);
 
		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'GET' request to URL : " + url);
		System.out.println("Response Code : " + responseCode);
 
		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String inputLine;
		 response = new StringBuffer();
 
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
 
		//print result
		System.out.println(response.toString());
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return response.toString();

	}
	
	@Override
	protected void onPostExecute(String result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		dialog.dismiss();
		try
		{
			
			JSONObject obj = new JSONObject(result);
			int statusCode =  Integer.parseInt(obj.getString("StatusCode"));
			if(statusCode == 103){
				Toast.makeText(context,obj.getString("Msg"),Toast.LENGTH_SHORT).show();
			}
			else
			{
				Toast.makeText(context,obj.getString("Msg"),Toast.LENGTH_SHORT).show();
			}
			
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
	}

}
