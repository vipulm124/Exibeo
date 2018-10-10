package com.irinnovative.exibeo;

import com.irinnovative.exibeo.util.Const;
import com.irinnovative.exibeo.util.Pref;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class SplashScreen extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.splashscreen);
		boolean shouldAutoLogin = Pref.ReadBoolean(getApplicationContext(),
				Const.PREF_SHOULD_REMEMBER, false);
		final Intent intent;
		if (shouldAutoLogin) {
			intent = new Intent(this, LandingScreen.class);
			intent.putExtra(Const.EXTRA_TOKEN,
					Pref.Read(getApplicationContext(), Const.PREF_TOKEN));
			intent.putExtra(Const.EXTRA_EMAIL_ID,
					Pref.Read(getApplicationContext(), Const.PREF_EMAIL));
			intent.putExtra(Const.EXTRA_NAME,
					Pref.Read(getApplicationContext(), Const.PREF_NAME));
			intent.putExtra(Const.EXTRA_POSITION,
					Pref.Read(getApplicationContext(), Const.PREF_POSITION));
			intent.putExtra(Const.EXTRA_REPORT_TO,
					Pref.Read(getApplicationContext(), Const.PREF_REPORT_TO));
			intent.putExtra(Const.EXTRA_COMPANY,
					Pref.Read(getApplicationContext(), Const.PREF_COMPANY));
		} else {
			intent = new Intent(this, LoginActivity.class);
		}
		Thread background = new Thread() {
			public void run() {

				try {
					// Thread will sleep for 5 seconds
					sleep(1 * 1000);

					// After 5 seconds redirect to another intent

					startActivity(intent);
					finish();

				} catch (Exception e) {

				}
			}

		};
		background.start();
	}
}
