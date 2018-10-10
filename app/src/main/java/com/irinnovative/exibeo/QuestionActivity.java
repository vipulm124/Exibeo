package com.irinnovative.exibeo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.irinnovative.exibeo.util.BOQuestion;
import com.irinnovative.exibeo.util.Const;
import com.irinnovative.exibeo.util.ExpandableListViewAdapter;
import com.irinnovative.exibeo.util.GPSTracker;
import com.irinnovative.exibeo.util.Pref;
import com.irinnovative.exibeo.util.Util;

import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;

public class QuestionActivity extends BaseActivity implements OnClickListener {

	private ListView lvExpandable;
	private Button btnFinishSubmit;
	private TextView tvTimer;

	boolean isTest = Const.CONST_IS_TEST;
	ExpandableListViewAdapter adapter;
	ArrayList<BOQuestion> allQuestion;
	AlertDialog alertDialog = null;
	String visitID;
	String storeID;
	private CustomAdapter adapter1;
	AlertDialog.Builder alert;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.question_list_main);

		tvTimer = (TextView) findViewById(R.id.tvTimer);

		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		startTimer();
		getDataFromInent();
		getQuestion();

	}

	private void startTimer() {
		if(stopped){
    		startTime = System.currentTimeMillis() - elapsedTime;
    	}
    	else{
    		startTime = System.currentTimeMillis();
    	}
    	mHandler.removeCallbacks(startTimer);
        mHandler.postDelayed(startTimer, 0);
		
	}

	private void getDataFromInent() {
		Intent intent = getIntent();
		if (intent != null) {
			if (intent.hasExtra(Const.EXTRA_VISIT_ID))
				visitID = intent.getExtras().getString(Const.EXTRA_VISIT_ID);
			if (intent.hasExtra(Const.EXTRA_STORE_ID))
				storeID = intent.getExtras().getString(Const.EXTRA_STORE_ID);
		}
		log("Visit ID:" + visitID + " & storeid from intent : " + storeID);

	}

	private void getQuestion() {
		String URL = Const.URL_API_GET_QUESTION + email + Const.URL_SEPARATOR
				+ token;
		log("URL:" + URL);
		AQuery aq = new AQuery(getApplicationContext());
		ProgressDialog dialog = Util.getProgressDialog(this,
				"Getting Audit Questions", "Please Wait...");

		aq.progress(dialog).ajax(URL, JSONArray.class,
				new AjaxCallback<JSONArray>() {
					@Override
					public void callback(String url, JSONArray object,
							AjaxStatus status) {
						super.callback(url, object, status);
						ParseQuestions(status, object);
						// ParseResult(status, object);
					}

				});

	}

	private void ParseQuestions(AjaxStatus status, JSONArray obj) {
		try {

			int httpCode = status.getCode();
			if (httpCode == HttpStatus.SC_OK) {
				if (obj != null) {
					log("Result form servere:" + obj.toString());
					try {
						allQuestion = new ArrayList<BOQuestion>();
						JSONArray jsonArray = new JSONArray(obj.toString());
						int count = jsonArray.length();
						BOQuestion oneQuestion;
						for (int i = 1; i < count; i++) {
							oneQuestion = new BOQuestion();
							JSONObject jsonObject = jsonArray.getJSONObject(i);
							oneQuestion.setQuestion(jsonObject
									.getString("ASKQue"));
							oneQuestion.setQuestionID(jsonObject
									.getString("Questionid"));
							oneQuestion.setOptionsRaw(jsonObject
									.getString("Options"));
							allQuestion.add(oneQuestion);
						}
						PerformPostQuestionsAction();

					} catch (Exception e) {
						e.printStackTrace();
					}
				} else
					Util.showErrorMessage(getApplicationContext(), httpCode);
			} else
				Util.showErrorMessage(getApplicationContext(), httpCode);

		} catch (Exception e) {
			Util.showException(getApplicationContext(), e);
		}
	}

	private void PerformPostQuestionsAction() {
		if (allQuestion != null) {
			initialiseViews();
			initialiseListeners();

			// allQuestion = FormatAllQuestion();
			adapter = new ExpandableListViewAdapter(getApplicationContext(),
					this, allQuestion);
			// adapter1 = new CustomAdapter1(getApplicationContext(),
			// R.layout.list_more, allQuestion);
			lvExpandable.setAdapter(adapter);
			lvExpandable.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					adapter.toggleSelected(new Integer(position));
					adapter.notifyDataSetChanged();
					CollectData(allQuestion.get(position), position);
				}
			});

		} else {
			// Toast.makeText(
			// getApplicationContext(),
			// "There is a problem while getting Audit checklist from server",
			// Toast.LENGTH_SHORT).show();

			performPostAuditAction("There is a problem while getting Audit checklist from server. Please Try Later");
		}
	}

	private void CollectData(final BOQuestion oneQuestion, final int index) {
		alert = new AlertDialog.Builder(this);

		alert.setTitle(oneQuestion.getQuestion());
		LayoutInflater mInflater = (LayoutInflater) getApplicationContext()
				.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		final View view = mInflater.inflate(R.layout.question_list_row, null);
		alert.setView(view);
		final RadioGroup rg = (RadioGroup) view.findViewById(R.id.rgAnswer);
		String[] options = oneQuestion.getOptions();
		RadioButton rb;
		for (int i = 0; i < options.length; i++) {
			rb = new RadioButton(getApplicationContext());
			rb.setId(i);
			rb.setText(options[i]);
			if (oneQuestion.isAnswered()
					&& oneQuestion.getAnswer().equals(options[i]))
				rb.setChecked(true);
			else
				rb.setChecked(false);
			rb.setLayoutParams(new RadioGroup.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			rb.setTextColor(android.graphics.Color.BLACK);
			rg.addView(rb);
		}

		// RadioButton rbYes = (RadioButton) view.findViewById(R.id.rbYes);
		// RadioButton rbNo = (RadioButton) view.findViewById(R.id.rbNo);
		final EditText etComment = (EditText) view.findViewById(R.id.etComment);
		etComment.setText(oneQuestion.getComment());
		// if (oneQuestion.getAnswer() == 'y')
		// rbYes.setChecked(true);
		// else if (oneQuestion.getAnswer() == 'n')
		// rbNo.setChecked(true);

		alert.setCancelable(false);
		alert.setPositiveButton("Save", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				// Do nothing here as we are going to override its closing
				// behaviour
			}

		});

		// alert.setNegativeButton("Cancel",
		// new DialogInterface.OnClickListener() {
		// public void onClick(DialogInterface dialog, int whichButton) {
		// // Canceled.
		// }
		// });
		// Required to override the behaviour
		// Ref:
		// http://stackoverflow.com/questions/2620444/how-to-prevent-a-dialog-from-closing-when-a-button-is-clicked
		final AlertDialog dialog = alert.create();
		dialog.show();
		dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						int selectedRadioId = rg.getCheckedRadioButtonId();
						RadioButton rb = (RadioButton) dialog
								.findViewById(selectedRadioId);
						if (rb != null) {
							allQuestion.get(index).setAnswer(
									rb.getText().toString());
						} else {
							Toast.makeText(getApplicationContext(),
									"You must answer this question to proceed",
									Toast.LENGTH_SHORT).show();
							return;
						}
						// if (rg.getCheckedRadioButtonId() == R.id.rbYes) {
						// allQuestion.get(index).setAnswer('y');
						// } else if (rg.getCheckedRadioButtonId() == R.id.rbNo)
						// {
						// allQuestion.get(index).setAnswer('n');
						// } else {
						// Toast.makeText(getApplicationContext(),
						// "You must answer this question to proceed",
						// Toast.LENGTH_SHORT).show();
						// return;
						// }
						String Comment = etComment.getText().toString().trim();
						if (oneQuestion.isCommentRequired()) {
							log("Comment required");
							if (Comment.length() < 2) {
								Toast.makeText(getApplicationContext(),
										"Comment is required",
										Toast.LENGTH_SHORT).show();
								return;
							}

						}
						allQuestion.get(index).setComment(Comment);
						allQuestion.get(index).setAnswered(true);
						dialog.dismiss();
					}
				});

	}

	protected void onResume() {
		super.onResume();
		// validateSession(token, email);
	}

	private void initialiseListeners() {
		btnFinishSubmit.setOnClickListener(this);
	}

	private void initialiseViews() {
		lvExpandable = (ListView) findViewById(R.id.questionlistmain);
		btnFinishSubmit = (Button) findViewById(R.id.bnfinishandsubmit);

	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub

		switch (view.getId()) {
		case R.id.bnfinishandsubmit: {
			if (adapter != null) {
				ArrayList<BOQuestion> allAnswer = adapter.getAllAnswer();
				if (allAnswer != null) {
					boolean isValidated = validateAnswer(allAnswer);
					if (isValidated)
						SubmitAnswer(allAnswer);
				} else
					log("all answer is null");
			} else
				log("adapter is null");
			break;
		}
		default:
			break;
		}

	}

	private boolean validateAnswer(ArrayList<BOQuestion> allAnswer) {
		boolean isValidated = true;
		int countTarget = allAnswer.size();
		String message = "Please answer following Question: ";
		for (int i = 0; i < countTarget; i++) {
			BOQuestion oneQuestion = allAnswer.get(i);
			if (!oneQuestion.isAnswered()) {
				isValidated = false;
				message += i + 1 + ",";
			}
		}
		message = message.substring(0, message.length() - 1);
		if (!isValidated)
			Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT)
					.show();
		return isValidated;
	}

	private void SubmitAnswer(ArrayList<BOQuestion> allAnswer) {
		log("Ready to submit Answer");
		String QuestionIDs = "";
		String Answers = "";
		String Comments = "";
		for (int i = 0; i < allAnswer.size(); i++) {
			BOQuestion oneAnswer = allAnswer.get(i);
			if (i == 0) {
				QuestionIDs = oneAnswer.getQuestionID();
				Answers = "" + oneAnswer.getAnswer();
				Comments = oneAnswer.getComment();
			} else {
				QuestionIDs += "|" + oneAnswer.getQuestionID();
				Answers += "|" + oneAnswer.getAnswer();
				// if (Comments.length() > 0)
				Comments += "|" + oneAnswer.getComment();
			}
		}
		SubmitAnswerToServer(email, token, QuestionIDs, Answers, Comments,
				visitID, storeID);
	}

	// private void SubmitAnswerToServer(String email, String token,
	// String QuestionIDs, String Answers, String Comments) {
	// String URL = Const.URL_API_SUBMIT_AUDIT + email + Const.URL_SEPARATOR
	// + token + Const.URL_SEPARATOR + QuestionIDs
	// + Const.URL_SEPARATOR + Answers + Const.URL_SEPARATOR
	// + Comments;
	// log("URL:" + URL);
	// AQuery aq = new AQuery(getApplicationContext());
	// ProgressDialog dialog = Util.getProgressDialog(this,
	// "Submitting Audit", "Please Wait...");
	//
	// aq.progress(dialog).ajax(URL, JSONObject.class,
	// new AjaxCallback<JSONObject>() {
	// @Override
	// public void callback(String url, JSONObject object,
	// AjaxStatus status) {
	// super.callback(url, object, status);
	// ParseResult(status, object);
	// }
	// });
	//
	// }

	private void SubmitAnswerToServer(String email, String token,
			String QuestionIDs, String Answers, String Comments,
			String VisitID, String StoreID) {
		String URL = Const.URL_API_INSERT_ANSWER_NEW + email
				+ Const.URL_SEPARATOR + token + Const.URL_SEPARATOR + VisitID
				+ Const.URL_SEPARATOR + StoreID + Const.URL_SEPARATOR
				+ QuestionIDs + Const.URL_SEPARATOR + Answers
				+ Const.URL_SEPARATOR;
		URL += (null != Comments && !Comments.isEmpty() ? Comments : Answers);
		log("URL of submitting store audit:" + URL);
		AQuery aq = new AQuery(getApplicationContext());
		ProgressDialog dialog = Util.getProgressDialog(this,
				"Submitting Audit", "Please Wait...");

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
					log("Result form servere:" + obj.toString());
					String statusCode = obj.getString("StatusCode");
					if (statusCode != null) {
						try {
							int intCode = Integer.parseInt(statusCode);
							if (intCode == Const.RESPONSE_AUDIT_SUCCESS) {
								Toast.makeText(
										getApplicationContext(),
										"Your audit checklist has been saved successfully",
										Toast.LENGTH_LONG).show();
								Pref.WriteInt(getApplicationContext(),
										Const.PREF_COUNT, 0);
								// performPostAuditAction("Audit Completed");
								performVisitEnd();
							} else
								Toast.makeText(
										getApplicationContext(),
										"There is some problem while submitting audit detail.Please try later",
										Toast.LENGTH_SHORT).show();
						} catch (Exception e) {
							Util.showException(getApplicationContext(), e);
						}
					} else
						Toast.makeText(
								getApplicationContext(),
								"There is some problem while submitting audit detail.Please try later",
								Toast.LENGTH_SHORT).show();
				} else
					Util.showErrorMessage(getApplicationContext(), httpCode);
			} else
				Util.showErrorMessage(getApplicationContext(), httpCode);

		} catch (Exception e) {
			Util.showException(getApplicationContext(), e);
		}
	}

	private void performPostAuditAction(String ByeMessage) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setTitle("Exebio");
		alertDialogBuilder.setIcon(R.drawable.ic_launcher);
		alertDialogBuilder.setMessage(ByeMessage)

		.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {

				alertDialog.dismiss();
				performVisitEnd();

				// TODO Need to call Visit end here
			}

		})

		.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				alertDialog.dismiss();
			}
		});
		// create alert dialog
		alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();

	}

	private void performVisitEnd() {
		GPSTracker tracker = new GPSTracker(getApplicationContext());
		double dLatitude = 0.0;
		double dLongitude = 0.0;
		if (tracker.canGetLocation()) {
			dLongitude = tracker.getLongitude();
			dLatitude = tracker.getLatitude();
			log("Longitude:" + dLongitude);
			log("Latitude:" + dLatitude);
		}

		//	Stop timer and get the time from textview
		mHandler.removeCallbacks(startTimer);
    	stopped = true;
        String currentDate="";
        String currentTime="";
        try {
            currentDate=Util.getCurrentDate();
            currentTime = Util.getCurrentTime();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
		String URL = Const.URL_API_STORE_VISIT_END + email
				+ Const.URL_SEPARATOR + token + Const.URL_SEPARATOR + visitID
				+ Const.URL_SEPARATOR + storeID + Const.URL_SEPARATOR
				+ currentDate + Const.URL_SEPARATOR
				+ currentTime + Const.URL_SEPARATOR + dLongitude
				+ Const.URL_SEPARATOR + dLatitude;
		log("visit end url:" + URL);
		AQuery aq = new AQuery(getApplicationContext());
		ProgressDialog dialog = Util.getProgressDialog(this,
				"Performing Visit End", "Please Wait...");

		aq.progress(dialog).ajax(URL, JSONObject.class,
				new AjaxCallback<JSONObject>() {
					@Override
					public void callback(String url, JSONObject object,
							AjaxStatus status) {
						super.callback(url, object, status);
						ParseVisitEndResult(status, object);
					}
				});

	}

	private void ParseVisitEndResult(AjaxStatus status, JSONObject obj) {
		try {

			int httpCode = status.getCode();
			log("HTTP response:" + httpCode);
			if (httpCode == HttpStatus.SC_OK) {
				if (obj != null) {
					log("Response from server:" + obj.toString());
				} else
					Util.showErrorMessage(getApplicationContext(), httpCode);
			} else
				Util.showErrorMessage(getApplicationContext(), httpCode);

		} catch (Exception e) {
			Util.showException(getApplicationContext(), e);
		}
		finish();
		Toast.makeText(getApplicationContext(),
				"Store visit has been ended successfully", Toast.LENGTH_SHORT)
				.show();
	}

	private void performPostAuditAction() {
		finish();

	}

	@Override
	public void onBackPressed() {
		// super.onBackPressed();
		performPostAuditAction("Are you sure you want leave Store?");
	}

	
	
	private Handler mHandler = new Handler();
	private long startTime;
	private long elapsedTime;
	private final int REFRESH_RATE = 100;
	private String hours,minutes,seconds,milliseconds;
	private long secs,mins,hrs,msecs;
	private boolean stopped = false;
	
	private Runnable startTimer = new Runnable() {
		   public void run() {
			   elapsedTime = System.currentTimeMillis() - startTime;
			   updateTimer(elapsedTime);
			   mHandler.postDelayed(this,REFRESH_RATE);
			}
		};
	
	private void updateTimer (float time){
		secs = (long)(time/1000);
		mins = (long)((time/1000)/60);
		hrs = (long)(((time/1000)/60)/60);

		/* Convert the seconds to String
		 * and format to ensure it has
		 * a leading zero when required
		 */
		secs = secs % 60;
		seconds=String.valueOf(secs);
    	if(secs == 0){
    		seconds = "00";
    	}
    	if(secs <10 && secs > 0){
    		seconds = "0"+seconds;
    	}

		/* Convert the minutes to String and format the String */

    	mins = mins % 60;
		minutes=String.valueOf(mins);
    	if(mins == 0){
    		minutes = "00";
    	}
    	if(mins <10 && mins > 0){
    		minutes = "0"+minutes;
    	}

    	/* Convert the hours to String and format the String */

    	hours=String.valueOf(hrs);
    	if(hrs == 0){
    		hours = "00";
    	}
    	if(hrs <10 && hrs > 0){
    		hours = "0"+hours;
    	}

    	/* Although we are not using milliseconds on the timer in this example
    	 * I included the code in the event that you wanted to include it on your own
    	 */
    	milliseconds = String.valueOf((long)time);
    	if(milliseconds.length()==2){
    		milliseconds = "0"+milliseconds;
    	}
      	if(milliseconds.length()<=1){
    		milliseconds = "00";
    	}
		milliseconds = milliseconds.substring(milliseconds.length()-3, milliseconds.length()-2);

		/* Setting the timer text to the elapsed time */
		tvTimer.setText(hours + ":" + minutes + ":" + seconds);
		
	}
	private void log(String string) {
		Log.d(Const.DEBUG_TAG, "Question: " + string);

	}

}
