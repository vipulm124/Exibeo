package com.irinnovative.exibeo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.ListView;

import com.irinnovative.exibeo.util.Const;

public class TestActivity extends Activity {
	Button bTest;
	ListView lvTest;
	LayoutInflater inflater;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		
		Intent i = new Intent(this, SplashScreen.class);
		startActivity(i);
		finish();

		setContentView(R.layout.test);
		bTest = (Button) findViewById(R.id.bTest);
		lvTest = (ListView) findViewById(R.id.lvTest);
		inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

	}

	private void log(String string) {
		Log.d(Const.DEBUG_TAG, "LandingActivity: " + string);

	}
}
