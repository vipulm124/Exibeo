package com.irinnovative.exibeo.util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.Toast;

import com.irinnovative.exibeo.LoginActivity;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Util {

	public static ProgressDialog getProgressDialog(Activity activity,
			String Title, String Description) {
		ProgressDialog dialog = new ProgressDialog(activity);
		dialog.setCancelable(false);
		dialog.setTitle(Title);
		dialog.setMessage(Description);
		dialog.getWindow().setGravity(Gravity.CENTER);
		return dialog;
	}

	public static void showErrorMessage(Context context, int httpCode) {
		Toast.makeText(
				context,
				"Your request can not be completed at this time. Please try later. Error Code:"
						+ httpCode, Toast.LENGTH_LONG).show();

	}

	public static void showException(Context context, Exception e) {
		Toast.makeText(
				context,
				"There is a problem with the server. Please contact your system administrator.",
				Toast.LENGTH_LONG).show();
		log("Exception:Message:" + e.getMessage());
		log("Exception:LocalizedMessage:" + e.getLocalizedMessage());
		log("Exception:Cause:" + e.getCause());
		log("Exception:StackTrace:" + e.getStackTrace());

	}

	private static void log(String string) {
		Log.d(Const.DEBUG_TAG, "Util: " + string);

	}

	public static void Logout(Context context, boolean shouldShowLogin) {
		Pref.Write(context, Const.PREF_TOKEN, "");
		Pref.WriteBoolean(context, Const.PREF_SHOULD_REMEMBER, false);
		Toast.makeText(context, "You have been Logged out Successfully",
				Toast.LENGTH_SHORT).show();
		if (shouldShowLogin) {
			Intent intent = new Intent(context, LoginActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
			context.startActivity(intent);
		}
	}

	public static String getCurrentDateOLD() {

		Calendar c = Calendar.getInstance();
		SimpleDateFormat df = new SimpleDateFormat("MM-dd-yyyy");
		String formattedDate = df.format(c.getTime());

		return formattedDate;
	}

	public static String getCurrentTimeOLD() {
		Calendar c = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		return sdf.format(c.getTime()).replace(':', 'N');

	}

	public static String getCurrentDate() throws IOException, ParseException {
		HttpClient httpclient = new DefaultHttpClient();
		HttpResponse response = httpclient.execute(new HttpGet("https://google.com/"));
		StatusLine statusLine = response.getStatusLine();
		if(statusLine.getStatusCode() == HttpStatus.SC_OK){
			String dateStr = response.getFirstHeader("Date").getValue();
			//dateStr=Thu, 27 Sep 2018 16:04:01 GMT
			if(dateStr.length()>0){
				String[] separatedDate = dateStr.split(" ");
				if(separatedDate.length>0)
				{
				    String currentDate = separatedDate[1]+ " "+ separatedDate[2] + " "+separatedDate[3];
				    DateFormat inputFormat = new SimpleDateFormat("dd MMM yyyy");
				    DateFormat outputFormat = new SimpleDateFormat("MM-dd-yyyy");
				    Date date = inputFormat.parse(currentDate);
				    String outputDateStr = outputFormat.format(date);
				    return  outputDateStr;
				}
			}
		}
		return "";
	}
	public static String getCurrentTime() throws IOException, ParseException {
		HttpClient httpclient = new DefaultHttpClient();
		HttpResponse response = httpclient.execute(new HttpGet("https://google.com/"));
		StatusLine statusLine = response.getStatusLine();
		if(statusLine.getStatusCode() == HttpStatus.SC_OK){
			String dateStr = response.getFirstHeader("Date").getValue();
			//dateStr=Thu, 27 Sep 2018 16:04:01 GMT
			if(dateStr.length()!=0){
				String[] separatedDate = dateStr.split(" ");
				String currentTime = separatedDate[4];
				SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss"); // 24 hour format
				Date d1 = format.parse(currentTime);
				long hour = 3600 * 1000;
				Date newDate = new Date(d1.getTime() + (2 * hour));
				String newTime = format.format(newDate.getTime());
				return  newTime.replace(':','N');
			}
		}
		return "";
	}

	public static String ConvertDateToString(Date d) {
		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy", Locale.US);
		String strDate = sdf.format(d);
		return strDate;

	}

	public static Date ConvertStringToDate(String s) {
		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy", Locale.US);
		try {
			return sdf.parse(s);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;

		}
	}

	public static boolean CheckNetworkConnection(Context mContext) {
		ConnectivityManager connectivityMgr = (ConnectivityManager) mContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivityMgr != null) {
			log("Checking availability of net");
			if (connectivityMgr.getActiveNetworkInfo() != null
					&& connectivityMgr.getActiveNetworkInfo().isAvailable()
					&& connectivityMgr.getActiveNetworkInfo().isConnected()) {
				log("network is available");
				return true;

			} else {
				log("network is not available");
				return false;
			}
		} else {
			log("Connectivity service mangr is null");
			return false;
		}
	}

	public static int dpToPx(Resources res, int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				res.getDisplayMetrics());
	}

}
