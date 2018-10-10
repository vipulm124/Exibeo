package com.irinnovative.exibeo;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.irinnovative.exibeo.util.BOQuestion;

public class CustomAdapter extends ArrayAdapter<BOQuestion> {

	int layoutResourceID;
	Context context;
	List<BOQuestion> allQuestion;
	public ArrayList<Integer> selectedIds = new ArrayList<Integer>();

	public CustomAdapter(Context context, int resource,
			List<BOQuestion> allQuestion) {
		super(context, resource, allQuestion);
		layoutResourceID = resource;
		this.context = context;
		this.allQuestion = allQuestion;
	}

	class ViewHolder {
		public TextView tvQuestion;
		public ImageView ivMore;
	}

	@Override
	public BOQuestion getItem(int position) {
		return allQuestion.get(position);
	}

	// @Override
	// public View getView(int position, View convertView, ViewGroup parent) {
	// final ViewHolder holder;
	// if (convertView == null) {
	// LayoutInflater inflater = (LayoutInflater) context
	// .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	// convertView = inflater.inflate(layoutResourceID, parent, false);
	// holder = new ViewHolder();
	// holder.tvQuestion = (TextView) convertView
	// .findViewById(R.id.tvQuestion);
	// holder.ivMore = (ImageView) convertView.findViewById(R.id.i_more);
	// holder.tvQuestion.setTag(position);
	// convertView.setTag(holder);
	// } else {
	// holder = (ViewHolder) convertView.getTag();
	// }
	// BOQuestion oneQuestion = getItem(position);
	// holder.tvQuestion.setText(oneQuestion.getQuestion());
	// if (oneQuestion.isAnswered())
	// holder.ivMore.setImageResource(R.drawable.right);
	// convertView.setOnClickListener(new OnClickListener() {
	//
	// @Override
	// public void onClick(View v) {
	// // ViewHolder holder1 = (ViewHolder) v.getTag();
	// holder.tvQuestion.setBackgroundColor(R.color.light_gray);
	// }
	// });
	// return convertView;
	// }
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(layoutResourceID, parent, false);
			holder = new ViewHolder();
			holder.tvQuestion = (TextView) convertView
					.findViewById(R.id.tvQuestion);
			holder.ivMore = (ImageView) convertView.findViewById(R.id.i_more);
			holder.tvQuestion.setTag(position);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		// //////////////////here paste code /////////////////////////
		// if (selectedIds.contains(position)) {
		// convertView.setSelected(true);
		// convertView.setPressed(true);
		// convertView.setBackgroundColor(Color.parseColor("#999999"));
		// } else {
		// convertView.setSelected(false);
		// convertView.setPressed(false);
		// convertView.setBackgroundColor(Color.parseColor("#ffffff"));
		// }

		// ///////////////////////////////////////////////////////////////////
		BOQuestion oneQuestion = getItem(position);
		holder.tvQuestion.setText(oneQuestion.getQuestion());
		if (oneQuestion.isAnswered())
			holder.ivMore.setImageResource(R.drawable.right);
		// convertView.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// // ViewHolder holder1 = (ViewHolder) v.getTag();
		// holder.tvQuestion.setBackgroundColor(R.color.light_gray);
		// }
		// });
		return convertView;
	}

	public void toggleSelected(Integer position) {
		BOQuestion oneQuestion = getItem(position);
		if (oneQuestion.isAnswered()) {
			oneQuestion.setAnswered(false);
		} else {

			oneQuestion.setAnswered(true);
			// selectedIds.add(position);
		}
	}
}
