package com.irinnovative.exibeo.util;

import java.util.ArrayList;

import javax.crypto.spec.IvParameterSpec;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.irinnovative.exibeo.R;

public class ExpandableListViewAdapter extends BaseAdapter implements
		OnClickListener {

	private Context context;
	private ArrayList<BOQuestion> allQuestion;
	Activity activity;
	AlertDialog.Builder alert;
	public ArrayList<Integer> selectedIds = new ArrayList<Integer>();

	// TextView tvQuestion;
	// ImageView ivMore;

	// ImageView ivMore;

	public ExpandableListViewAdapter(Context context, Activity a,
			ArrayList<BOQuestion> allQuestion) {
		this.context = context;
		this.allQuestion = allQuestion;
		activity = a;
	}

	public ArrayList<BOQuestion> getAllAnswer() {
		return allQuestion;
	}

	@Override
	public int getCount() {
		return allQuestion.size();
	}

	@Override
	public Object getItem(int position) {
		return allQuestion.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			LayoutInflater mInflater = (LayoutInflater) context
					.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
			convertView = mInflater.inflate(R.layout.list_more, parent, false);
			holder = new ViewHolder();
			holder.tvQuestion = (TextView) convertView
					.findViewById(R.id.tvQuestion);
			holder.ivMore = (ImageView) convertView.findViewById(R.id.i_more);
			convertView.setTag(holder);
		} else {

			holder = (ViewHolder) convertView.getTag();
		}
		BOQuestion oneQuestion = (BOQuestion) getItem(position);
		holder.tvQuestion.setText(position + 1 + ": "
				+ oneQuestion.getQuestion());
		// if (oneQuestion.isAnswered()) {
		// log("Already answered. position:" + position);
		// // holder.ivMore.setImageResource(R.drawable.right);
		// convertView.setBackgroundColor(Color.parseColor("#999999"));
		// }
		ImageView iv = (ImageView) convertView.findViewById(R.id.i_more);
		if (selectedIds.contains(position)) {
			convertView.setSelected(true);
			convertView.setPressed(true);
			holder.ivMore.setImageResource(R.drawable.right);
			// convertView.setBackgroundColor(Color.parseColor("#999999"));
			// iv.setImageResource(R.drawable.right);
		} else {
			convertView.setSelected(false);
			convertView.setPressed(false);
			// convertView.setBackgroundColor(Color.parseColor("#ffffff"));
			// iv.setImageResource(R.drawable.ic_list_more);
			holder.ivMore.setImageResource(R.drawable.ic_list_more);
		}
		// convertView.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// log("Onclick. pos:" + position);
		// CollectData(allQuestion.get(position), position);
		//
		// }
		// });

		return convertView;
	}

	public void toggleSelected(Integer position) {
		if (!selectedIds.contains(position)) {
			selectedIds.add(position);

		}
	}

	// @Override
	// public View getView(final int position, View convertView, ViewGroup
	// parent) {
	// if (convertView == null) {
	// LayoutInflater mInflater = (LayoutInflater) context
	// .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
	// convertView = mInflater.inflate(R.layout.list_more, null);
	// // log("Convert view is null. pos:" + position);
	// } else {
	// // log("Convert view is not null. pos:" + position);
	// }
	// tvQuestion = (TextView) convertView.findViewById(R.id.tvQuestion);
	//
	// // if (isAnswerProvided(position)) {
	// // log("Answer provided for pos:" + position);
	// // tvQuestion.setBackgroundColor(R.color.list_header_blue);
	// // }
	// ivMore = (ImageView) convertView.findViewById(R.id.i_more);
	// BOQuestion oneQuestion = (BOQuestion) getItem(position);
	// tvQuestion.setText(position + 1 + "-:" + oneQuestion.getQuestion());
	// // log("Question:" + oneQuestion.getQuestion() + ", isAnswered:"
	// // + oneQuestion.isAnswered());
	// if (oneQuestion.isAnswered()) {
	//
	// log("Already answered. position:" + position);
	// ivMore.setImageResource(R.drawable.right);
	// }
	// convertView.setOnClickListener(new OnClickListener() {
	//
	// @Override
	// public void onClick(View v) {
	// CollectData(allQuestion.get(position), position);
	// // tv.setBackgroundColor(R.color.list_header_blue);
	// }
	// });
	// // notifyDataSetChanged();
	// return convertView;
	// }

	class ViewHolder {
		public TextView tvQuestion;
		public ImageView ivMore;
		public int index;
	}

	private void CollectData(final BOQuestion oneQuestion, final int index) {
		alert = new AlertDialog.Builder(activity);

		alert.setTitle(oneQuestion.getQuestion());
		LayoutInflater mInflater = (LayoutInflater) context
				.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		final View view = mInflater.inflate(R.layout.question_list_row, null);
		alert.setView(view);
		final RadioGroup rg = (RadioGroup) view.findViewById(R.id.rgAnswer);

		String[] options = oneQuestion.getOptions();
		RadioButton rb;
		for (int i = 0; i < options.length; i++) {
			rb = new RadioButton(context);
			rb.setText(options[i]);
			rb.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT));
			rg.addView(rb);
		}

//		RadioButton rbYes = (RadioButton) view.findViewById(R.id.rbYes);
//		RadioButton rbNo = (RadioButton) view.findViewById(R.id.rbNo);
		final EditText etComment = (EditText) view.findViewById(R.id.etComment);
		etComment.setText(oneQuestion.getComment());
//		if (oneQuestion.getAnswer() == 'y')
//			rbYes.setChecked(true);
//		else if (oneQuestion.getAnswer() == 'n')
//			rbNo.setChecked(true);

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
						int selectedRadioId=rg.getCheckedRadioButtonId();
						RadioButton rb=(RadioButton) dialog.findViewById(selectedRadioId);
						if(rb!=null)
						{
							allQuestion.get(index).setAnswer(rb.getText().toString());
						}else {
							Toast.makeText(context,
									"You must answer this question to proceed",
									Toast.LENGTH_SHORT).show();
							return;
						}
//						if (rg.getCheckedRadioButtonId() == R.id.rbYes) {
//
//							allQuestion.get(index).setAnswer('y');
//						} else if (rg.getCheckedRadioButtonId() == R.id.rbNo) {
//							allQuestion.get(index).setAnswer('n');
//						} else {
//							Toast.makeText(context,
//									"You must answer this question to proceed",
//									Toast.LENGTH_SHORT).show();
//							return;
//						}
						String Comment = etComment.getText().toString().trim();
						if (oneQuestion.isCommentRequired()) {
							log("Comment required");
							if (Comment.length() < 2) {
								Toast.makeText(context, "Comment is required",
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

	private void log(String string) {
		Log.d(Const.DEBUG_TAG, "Expandable List adapter: " + string);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

	}
}
