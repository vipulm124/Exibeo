package com.irinnovative.exibeo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

public class ForgotPassword extends Activity implements OnClickListener {

	private Button submitForgotPassword;

	private EditText emailAddress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.forgotpassword);

		initialiseViews();
		initilialiseListener();
	}

	private void initilialiseListener() {
		// TODO Auto-generated method stub
		submitForgotPassword.setOnClickListener(this);
	}

	private void initialiseViews() {
		// TODO Auto-generated method stub
		submitForgotPassword = (Button) findViewById(R.id.submitforgotbutton);
		emailAddress = (EditText) findViewById(R.id.emailaddressforgotpassword);
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub

		switch (view.getId()) {
		case R.id.submitforgotbutton:

			new ForgotPasswordTask(this, emailAddress.getText().toString())
					.execute();

			break;

		default:
			break;
		}

	}

}
