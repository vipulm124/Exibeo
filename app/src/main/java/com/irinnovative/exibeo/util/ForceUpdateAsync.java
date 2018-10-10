package com.irinnovative.exibeo.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.view.ContextThemeWrapper;

import com.irinnovative.exibeo.R;

import org.json.JSONObject;
import org.jsoup.Jsoup;

import java.io.IOException;

public class ForceUpdateAsync extends AsyncTask<String, String, JSONObject> {

    AlertDialog alertDialog = null;
    private String latestVersion;
    private String currentVersion;
    private Context context;
    public ForceUpdateAsync(String currentVersion, Context context){
        this.currentVersion = currentVersion;
        this.context = context;
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    @Override
    protected JSONObject doInBackground(String... params) {
        try {
            latestVersion = Jsoup.connect("https://play.google.com/store/apps/details?id=com.irinnovative.exibeo&hl=en")
                    .timeout(30000)
                    .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                    .referrer("http://www.google.com")
                    .get()
                    .select(".hAyfc .htlgb")
                    .get(7)
                    .ownText();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new JSONObject();
    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) {
        if(latestVersion!=null){
            if(!currentVersion.equalsIgnoreCase(latestVersion)){
                    if(!((Activity)context).isFinishing()){
                                showForceUpdateDialog(true);
                    }
            }
        }
        super.onPostExecute(jsonObject);
    }

    public void showForceUpdateDialog(boolean force){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(context,
                R.style.AppBaseTheme));

        alertDialogBuilder.setTitle("Update Available");

        if(force) {
            alertDialogBuilder.setMessage("A new version of Exibeo is available. Please update to version" + " " + latestVersion + " now");
            alertDialogBuilder.setCancelable(false);
            alertDialogBuilder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.irinnovative.exibeo&hl=en")));
                    dialog.cancel();
                }
            });
        }
        else{
            alertDialogBuilder.setCancelable(true);
            alertDialogBuilder.setMessage("A new version of Exibeo is available. Please update to version" + " " + latestVersion + " by 7th October");
            alertDialogBuilder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.irinnovative.exibeo&hl=en")));
                    dialog.cancel();
                }
            });
            alertDialogBuilder.setNegativeButton("Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                            int id) {
                            alertDialog.dismiss();
                        }
                    });
        }
        alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }
}