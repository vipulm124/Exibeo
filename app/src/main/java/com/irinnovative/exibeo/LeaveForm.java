package com.irinnovative.exibeo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpStatus;
import org.json.JSONObject;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Spinner;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.irinnovative.exibeo.util.AdapterSpinner;
import com.irinnovative.exibeo.util.BOLeaveType;
import com.irinnovative.exibeo.util.Const;
import com.irinnovative.exibeo.util.Util;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class LeaveForm extends BaseActivity implements OnClickListener {

	AlertDialog alertDialog = null;
	int FROM_DATE = 1;
	int TO_DATE = 2;
	int cur = 0;
	private ImageView imgViewCalFrom, imgViewCalTo;
	private EditText etFrom, etTo, etNumberOfDays;
	private Spinner spLeaveType;
	private Calendar cal;
	private Button btnSubmitLeave;
	private int day;
	private int month;
	private int year;
	List<BOLeaveType> allLeaveType;
	AdapterSpinner adapter;
	BOLeaveType selectedLeaveType;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.leaveform);
		cal = Calendar.getInstance();
		day = cal.get(Calendar.DAY_OF_MONTH);
		month = cal.get(Calendar.MONTH);
		year = cal.get(Calendar.YEAR);
		intilialiseViews();
		initialiseListeners();

	}

	private void initialiseListeners() {
		imgViewCalFrom.setOnClickListener(this);
		imgViewCalTo.setOnClickListener(this);
		btnSubmitLeave.setOnClickListener(this);
	}

	private void intilialiseViews() {
		imgViewCalFrom = (ImageView) findViewById(R.id.imageviewcalfrom);
		imgViewCalTo = (ImageView) findViewById(R.id.imageviewcalto);
		etFrom = (EditText) findViewById(R.id.edittextfrom);
		etTo = (EditText) findViewById(R.id.edittextto);
		etNumberOfDays = (EditText) findViewById(R.id.etNumberOfDays);
		spLeaveType = (Spinner) findViewById(R.id.spLeaveType);
		btnSubmitLeave = (Button) findViewById(R.id.btnsubmitleave);
		etFrom.setEnabled(false);
		etTo.setEnabled(false);

		allLeaveType = new ArrayList<BOLeaveType>();
		allLeaveType.add(new BOLeaveType(0, "--Select Leave Type--"));
		allLeaveType.add(new BOLeaveType(1, "Annual"));
		allLeaveType.add(new BOLeaveType(3, "Maternity"));
		allLeaveType.add(new BOLeaveType(4, "Paternity"));
		allLeaveType.add(new BOLeaveType(5, "Unpaid"));
		allLeaveType.add(new BOLeaveType(6, "Study"));
		allLeaveType.add(new BOLeaveType(7, "FamilyResponsibility"));
		allLeaveType.add(new BOLeaveType(8, "Sick"));
		adapter = new AdapterSpinner(allLeaveType, this);

		spLeaveType.setAdapter(adapter);

		selectedLeaveType = new BOLeaveType(0, "Select");

		spLeaveType.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				selectedLeaveType = allLeaveType.get(position);

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				selectedLeaveType = new BOLeaveType(0, "Select");

			}
		});

	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		case R.id.imageviewcalfrom:
			showDialog(0);
			break;

		case R.id.imageviewcalto:
			showDialog(1);
			break;

		case R.id.btnsubmitleave:
			ValidateFieldsAndSubmit();

			break;

		default:
			break;
		}

	}

	private void ValidateFieldsAndSubmit() {
		String strFromDate = etFrom.getText().toString().trim();
		String strToDate = etTo.getText().toString().trim();

		boolean isValidated = ValidateDates(strFromDate, strToDate);
		if (selectedLeaveType.getLeaveTypeID() == 0)
			isValidated = false;

		if (isValidated) {
			String numberOfDays = etNumberOfDays.getText().toString().trim();
			ApplyLeave(email, token, strFromDate, strToDate, numberOfDays,
					selectedLeaveType.getLeaveTypeID());
		}

	}

	private boolean ValidateDates(String strFromDate, String strToDate) {
		if (strFromDate.length() < 5 || strToDate.length() < 5
				|| etNumberOfDays.length() < 1) {
			Toast.makeText(getApplicationContext(),
					"All fields are manadatory", Toast.LENGTH_LONG).show();
			return false;
		}
		Date dtFromDate = Util.ConvertStringToDate(strFromDate);
		Date dtToDate = Util.ConvertStringToDate(strToDate);
		if (dtFromDate == null || dtToDate == null) {
			Toast.makeText(getApplicationContext(),
					"Input dates are not in correct format", Toast.LENGTH_LONG)
					.show();
			return false;
		}
		if (dtToDate.compareTo(dtFromDate) < 0) {
			// To date is less than from date
			Toast.makeText(getApplicationContext(),
					"To Date must be greater than From date", Toast.LENGTH_LONG)
					.show();
			return false;
		}
		return true;
	}

	private void ApplyLeave(String email, String token, String fromDate,
			String ToDate, String NumberOfDays, int LeaveTypeID) {

		String URL = Const.URL_API_APPLY_LEAVE + fromDate + Const.URL_SEPARATOR
				+ ToDate + Const.URL_SEPARATOR + LeaveTypeID
				+ Const.URL_SEPARATOR + NumberOfDays + Const.URL_SEPARATOR
				+ email + Const.URL_SEPARATOR + token;
		log("Apply Leave URL:" + URL);
		AQuery aq = new AQuery(getApplicationContext());
		ProgressDialog dialog = Util.getProgressDialog(this,
				"Sending your leave request", "Please Wait...");

		aq.progress(dialog).ajax(URL, JSONObject.class,
				new AjaxCallback<JSONObject>() {
					@Override
					public void callback(String url, JSONObject object,
							AjaxStatus status) {
						super.callback(url, object, status);
						ParseResult(status, object);
					}
				});

	}

	private void ParseResult(AjaxStatus status, JSONObject obj) {
		try {

			int httpCode = status.getCode();
			if (httpCode == HttpStatus.SC_OK) {
				if (obj != null) {
					log("Response from server:" + obj.toString());
					String responseCode = obj.getString("StatusCode");
					String message=obj.getString("Message");
					if (responseCode != null) {
						int intCode = 0;
						try {
							intCode = Integer.parseInt(responseCode);
						} catch (Exception e) {
							Toast.makeText(
									getApplicationContext(),
									"There is a problem while submitting your leave request. Reason: Unknown response from server",
									Toast.LENGTH_LONG).show();
						}
						if (intCode == Const.RESPONSE_100) {
							Toast.makeText(
									getApplicationContext(),
									message,
									Toast.LENGTH_LONG).show();
							PostLeaveSubmissionTask();
						} else if (intCode == Const.RESPONSE_FAILED_200) {
							Toast.makeText(getApplicationContext(),
									"Invalid session. Please login again",
									Toast.LENGTH_SHORT).show();
							// Clear session
							Util.Logout(getApplicationContext(), true);
							finish();
						} else if (intCode == Const.RESPONSE_FAILED_250) {
							
							Toast.makeText(getApplicationContext(),
									message,
									Toast.LENGTH_SHORT).show();
							// Clear session
							//Util.Logout(getApplicationContext(), true);
							finish();
						} else {
							Toast.makeText(
									getApplicationContext(),
									"There is a problem while submitting your leave request",
									Toast.LENGTH_LONG).show();
						}
					} else
						Toast.makeText(
								getApplicationContext(),
								"There is a problem while submitting your leave request",
								Toast.LENGTH_LONG).show();
				} else
					Util.showErrorMessage(getApplicationContext(), httpCode);
			} else
				Util.showErrorMessage(getApplicationContext(), httpCode);

		} catch (Exception e) {
			Util.showException(getApplicationContext(), e);
		}
	}

	private void PostLeaveSubmissionTask() {
		finish();

	}

	@Override
	protected Dialog onCreateDialog(int id) {

		if (id == 0) {
			cur = FROM_DATE;
			return new DatePickerDialog(this, datePickerListener, year, month,
					day);
		} else if (id == 1) {
			cur = TO_DATE;
			return new DatePickerDialog(this, datePickerListener, year, month,
					day);
		}
		return null;

	}

	private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
		public void onDateSet(DatePicker view, int selectedYear,
				int selectedMonth, int selectedDay) {
			if (cur == FROM_DATE) {
				etFrom.setText((selectedMonth + 1) + "-" + selectedDay + "-"
						+ selectedYear);
				etFrom.setEnabled(false);
			} else if (cur == TO_DATE) {
				etTo.setText((selectedMonth + 1) + "-" + selectedDay + "-"
						+ selectedYear);
				etTo.setEnabled(false);
			}
		}
	};

	private void log(String string) {
		Log.d(Const.DEBUG_TAG, "Apply Leave: " + string);

	}

}
