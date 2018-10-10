package com.irinnovative.exibeo.util;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

public class AdapterSpinner extends BaseAdapter implements SpinnerAdapter {

	/**
	 * The internal data (the ArrayList with the Objects).
	 */
	private List<BOLeaveType> data;
	private Activity activity;

	public AdapterSpinner(List<BOLeaveType> data, Activity a) {
		this.data = data;
		activity = a;
	}

	/**
	 * Returns the Size of the ArrayList
	 */
	@Override
	public int getCount() {
		return data.size();
	}

	/**
	 * Returns one Element of the ArrayList at the specified position.
	 */
	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int i) {
		return i;
	}

	/**
	 * Returns the View that is shown when a element was selected.
	 */
	@Override
	public View getView(int position, View recycle, ViewGroup parent) {
		TextView text;
		if (recycle != null) {
			// Re-use the recycled view here!
			text = (TextView) recycle;
		} else {
			// No recycled view, inflate the "original" from the platform:
			text = (TextView) activity.getLayoutInflater().inflate(
					android.R.layout.simple_dropdown_item_1line, parent, false);
		}
		//text.setTextColor(Color.BLACK);
		text.setPadding(10, 0, 0, 0);
		text.setTop(0);
		text.setBottom(0);
		text.setText(data.get(position).getLeaveType());
		return text;
	}

}
