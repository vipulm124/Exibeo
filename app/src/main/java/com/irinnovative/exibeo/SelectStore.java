package com.irinnovative.exibeo;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.irinnovative.exibeo.util.Const;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class SelectStore extends BaseActivity implements OnClickListener

{

	private ListView listViewStore;
	private ArrayList<String> storeList;
	private ArrayList<String> storeId;
	private ArrayList<String> storeListId;
	private String sId;
	private String sName;
	String[] dataStoreListId;
	AlertDialog alertDialog = null;
	private String emailAddress;
	private String token;
    private static final String REGEX = "com\\.(.*\\.)*android\\.(.+)";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.selectstore);
		token = getIntent().getStringExtra(Const.EXTRA_TOKEN);
		email = getIntent().getStringExtra(Const.EXTRA_EMAIL_ID);

		initialiseViews();
		initialiseListeners();
		storeList = getIntent().getStringArrayListExtra(Const.EXTRA_STORE_LIST);
		// storeId = getIntent().getStringArrayListExtra("StoreId");
		storeListId = getIntent().getStringArrayListExtra(
				Const.EXTRA_STORE_LIST_IDS);

		if (storeList.size() > 1) {
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_list_item_single_choice, storeList);
			listViewStore.setAdapter(adapter);
		} else {
			if (!storeList.get(0).equals("No Store Found")) {
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
						android.R.layout.simple_list_item_single_choice,
						storeList);
				listViewStore.setAdapter(adapter);
			} else {
				Toast.makeText(getApplicationContext(),
						"No Stores assigned to you", Toast.LENGTH_SHORT).show();
			}
		}
		listViewStore.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				// Toast.makeText(SelectStore.this,
				// "" + storeListId.get(position), Toast.LENGTH_SHORT)
				// .show();
				dataStoreListId = storeListId.get(position).split("~");
				sName = dataStoreListId[0];
				sId = dataStoreListId[1];
				if(checkFakeGPSApps()) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                            SelectStore.this);
                    alertDialogBuilder.setTitle("Exebio");
                    alertDialogBuilder.setIcon(R.drawable.ic_launcher);
                    alertDialogBuilder
                            .setMessage(
                                    "Are you sure you want to visit store \n "
                                            + sName)
                            .setCancelable(false)
                            .setPositiveButton("Confirm",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,
                                                            int id) {
                                            // if this button is clicked, close
                                            // current activity

                                            alertDialog.dismiss();
                                            // Call Visit Task.
                                            new VisitStartTask(SelectStore.this,
                                                    email, token, sId).execute();
                                            // Toast.makeText(SelectStore.this,
                                            // "Under Construction",
                                            // Toast.LENGTH_SHORT).show();

                                        }
                                    })

                            .setNegativeButton("Cancel",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,
                                                            int id) {
                                            // if this button is clicked, just close
                                            // the dialog box and do nothing

                                            // BuyFullVersion(imageItem.getOriginalPdfName());

                                            // Call Cancel Visit Operation
                                            alertDialog.dismiss();
                                        }
                                    });
                    // create alert dialog
                    alertDialog = alertDialogBuilder.create();

                    // show it
                    alertDialog.show();
                }
			}
		});

	}

	private boolean checkFakeGPSApps(){

		if(areThereMockPermissionApps(getApplicationContext())){
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					SelectStore.this);
			alertDialogBuilder.setTitle("Exebio");
			alertDialogBuilder.setIcon(R.drawable.ic_launcher);
			alertDialogBuilder
					.setMessage(
							"Location Error!")
					.setCancelable(false)
					.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
													int id) {
									// if this button is clicked, close
									// current activity

									alertDialog.dismiss();
									// Call SelectStore Task.
									//new SelectStoreTask(getApplicationContext(), email, token).execute();
								}
							});
			// create alert dialog
			alertDialog = alertDialogBuilder.create();

			// show it
			alertDialog.show();
			return false;
		}
			return true;
	}


    public static boolean isMockSettingsON(Context context) {
		// returns true if mock location enabled, false if not enabled.
		if (Settings.Secure.getString(context.getContentResolver(),Settings.Secure.ALLOW_MOCK_LOCATION).equals("0"))
			return false;
		else
			return true;
	}

	public static boolean areThereMockPermissionApps(Context context) {
		int count = 0;

		PackageManager pm = context.getPackageManager();
		List<ApplicationInfo> packages =
				pm.getInstalledApplications(PackageManager.GET_META_DATA);

		for (ApplicationInfo applicationInfo : packages) {
			try {
				PackageInfo packageInfo = pm.getPackageInfo(applicationInfo.packageName,
						PackageManager.GET_PERMISSIONS);

				// Get Permissions
				String[] requestedPermissions = packageInfo.requestedPermissions;
                Pattern p = Pattern.compile(REGEX);
                Matcher m;
				if (requestedPermissions != null) {
					for (int i = 0; i < requestedPermissions.length; i++) {
						if (requestedPermissions[i]
								.equals("android.permission.ACCESS_MOCK_LOCATION")
								&& !applicationInfo.packageName.equals(context.getPackageName())) {
						    m = p.matcher(applicationInfo.packageName);
						    if(!m.matches()){
						        count++;
                            }
//							if(!applicationInfo.packageName.contains("com.android"))
//								{
//									count++;
//								}
						}
					}
				}
			} catch (PackageManager.NameNotFoundException e) {
				Log.e("Got exception " , e.getMessage());
			}
		}

		if (count > 0)
			return true;
		return false;
	}

	private void initialiseListeners() {
		// TODO Auto-generated method stub
		// logOut.setOnClickListener(this);
	}

	private void initialiseViews() {
		// TODO Auto-generated method stub
		listViewStore = (ListView) findViewById(R.id.listviewstore);
		// logOut = (Button)findViewById(R.id.btnLogoutselectstore);

	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub

		switch (view.getId()) {

		// case R.id.btnLogoutselectstore:
		// Intent logOut = new Intent(this, LoginActivity.class);
		// startActivity(logOut);
		// break;

		default:
			break;
		}

	}

}
