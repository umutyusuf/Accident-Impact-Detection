package com.umut.accidentrecognitioncp.adapters;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.umut.accidentrecognitioncp.R;

public class ContactListAdapter extends BaseAdapter {

	private Activity activity;
	private List<String> phoneNumbers;
	private List<String> names;

	public ContactListAdapter(Activity activity, List<String> phoneNumbers,
			List<String> names) {
		super();
		this.activity = activity;
		this.names = names;
		this.phoneNumbers = phoneNumbers;
	}

	public Activity getActivity() {
		return activity;
	}

	public void setActivity(Activity activity) {
		this.activity = activity;
	}

	public List<String> getPhoneNumbers() {
		return phoneNumbers;
	}

	public void setPhoneNumbers(List<String> phoneNumbers) {
		this.phoneNumbers = phoneNumbers;
	}

	public List<String> getNames() {
		return names;
	}

	public void setNames(List<String> names) {
		this.names = names;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return names.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return names.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	public static class ViewHolder {
		public TextView nameTextView;
		public TextView phoneNumberTextView;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;
		ViewHolder holder = new ViewHolder();

		if (rowView == null) {
			LayoutInflater inflater = (LayoutInflater) activity
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			rowView = inflater.inflate(R.layout.contactlistviewitem, parent,
					false);

			rowView.setTag(holder);

		} else {
			holder = (ViewHolder) rowView.getTag();
		}

		holder.nameTextView = (TextView) rowView
				.findViewById(R.id.accident_recognition_contacts_list_adapter_name_textView);
		holder.phoneNumberTextView = (TextView) rowView
				.findViewById(R.id.accident_recognition_contacts_list_adapter_phone_number_textView);
		try {
			holder.nameTextView.setText(names.get(position));
			holder.phoneNumberTextView.setText(phoneNumbers.get(position));
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		return rowView;
	}

}
