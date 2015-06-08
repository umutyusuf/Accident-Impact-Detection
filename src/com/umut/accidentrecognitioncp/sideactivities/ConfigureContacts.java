package com.umut.accidentrecognitioncp.sideactivities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.umut.accidentrecognitioncp.R;
import com.umut.accidentrecognitioncp.data.ContactsData;
import com.umut.accidentrecognitioncp.exceptions.FileMissingException;
import com.umut.accidentrecognitioncp.utils.Helper;
import com.umut.accidentrecognitioncp.utils.Keys;

public class ConfigureContacts extends Activity implements View.OnClickListener {

	// Views activity has
	private Button selectContactsFromPhoneButton;
	private Button applyContactsButton;
	private Button addContactButton;
	private Button deleteAllContacts;
	private LinearLayout contactListContainer;

	private List<String> contactNameList;
	private List<String> contactPhoneNumberList;

	// Booleans to lock and open
	private boolean onEdittingProcess;
	private boolean readyToApply;
	private EditText nameEditText;
	private EditText numberEditText;

	// Contact List Data to save in json format
	private ContactsData contactsData;

	// View and adapter to show existing contacts
	private ArrayAdapter<String> adapter;
	private ListView contactListView;
	private List<String> bufferList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.configure_contacts);

		initializeViews();
		checkForPreLoadedContacts();
	}

	private void checkForPreLoadedContacts() {
		if (Helper.contactsDataExist()) {
			try {
				if (Helper.contactsDataExist()) {
					contactsData = Helper
							.getContactsData(ConfigureContacts.this);
					contactNameList = contactsData.getContacts();
					contactPhoneNumberList = contactsData
							.getContactPhoneNumbers();
					bufferList = new ArrayList<String>();
					for (int i = 0; i < contactNameList.size(); i++) {
						bufferList.add(contactNameList.get(i) + " - "
								+ contactPhoneNumberList.get(i));
					}
					// Initialize adapter
					adapter = new ArrayAdapter<String>(this,
							android.R.layout.simple_list_item_multiple_choice,
							bufferList);

					contactListView.setAdapter(adapter);
					showContactInfo();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				adapter = new ArrayAdapter<String>(ConfigureContacts.this, 0);
			} catch (FileMissingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				adapter = new ArrayAdapter<String>(ConfigureContacts.this, 0);
			}
		}

	}

	private void showContactInfo() {
		if (adapter == null) {
			bufferList = new ArrayList<String>();
			for (int i = 0; i < contactNameList.size(); i++) {
				bufferList.add(contactNameList.get(i) + " - "
						+ contactPhoneNumberList.get(i));
			}
			adapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_list_item_multiple_choice,
					bufferList);
			contactListView.setAdapter(adapter);
		} else {
			bufferList.clear();
			for (int i = 0; i < contactNameList.size(); i++) {
				bufferList.add(contactNameList.get(i) + " - "
						+ contactPhoneNumberList.get(i));
			}
			adapter.notifyDataSetChanged();
			contactListView.invalidateViews();
		}

	}

	private void initializeViews() {

		// Find All Views
		selectContactsFromPhoneButton = (Button) findViewById(R.id.accident_recognition_configure_contacts_select_from_contacts);
		applyContactsButton = (Button) findViewById(R.id.accident_recognition_cofigure_contacts_apply_update);
		addContactButton = (Button) findViewById(R.id.accident_recognition_add_contact_button);
		deleteAllContacts = (Button) findViewById(R.id.accident_recognition_cofigure_contacts_delete_all);
		contactListContainer = (LinearLayout) findViewById(R.id.accident_recognition_configure_contacts_contacts_container);
		contactListView = (ListView) findViewById(R.id.accident_recognition_accident_configure_contact_contacts_listView);

		// Set listeners for buttons
		selectContactsFromPhoneButton.setOnClickListener(this);
		applyContactsButton.setOnClickListener(this);
		addContactButton.setOnClickListener(this);
		deleteAllContacts.setOnClickListener(this);

		// Initializ editText and their containers list
		contactNameList = new ArrayList<String>();
		contactPhoneNumberList = new ArrayList<String>();

		nameEditText = new EditText(ConfigureContacts.this);
		numberEditText = new EditText(ConfigureContacts.this);

		// Initialize variables
		onEdittingProcess = false;
		readyToApply = false;

		// Configure contactListView
		contactListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {

		int clickId = v.getId();

		if (clickId == selectContactsFromPhoneButton.getId()) {
			// To staff to open contact list
			addFromContacts();
			readyToApply = true;
		} else if (clickId == applyContactsButton.getId()) {
			if (readyToApply) {
				saveContactsToFile();
			}
		} else if (clickId == addContactButton.getId()) {

			if (!onEdittingProcess) {

				// Handle logic
				readyToApply = false;

				// Change the name of button
				addContactButton
						.setText(R.string.accident_recognition_configure_contact_save);

				// Prepare Edit Text For Name space
				nameEditText
						.setHint(getResources()
								.getString(
										R.string.accident_recognition_configure_contact_dynamic_edit_text_name_hint));
				// Configure params for placement
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.MATCH_PARENT,
						LinearLayout.LayoutParams.WRAP_CONTENT);
				params.leftMargin = (int) getResources().getDimension(
						R.dimen.accident_recognition_margin_20dp);
				params.rightMargin = (int) getResources().getDimension(
						R.dimen.accident_recognition_margin_20dp);

				// Prepare edittext to get phone numbers
				numberEditText
						.setHint(getResources()
								.getString(
										R.string.accident_recognition_configure_contact_dynamic_edit_text_phone_number_hint));
				numberEditText.setInputType(InputType.TYPE_CLASS_NUMBER);

				// Add them to layout
				contactListContainer.addView(nameEditText, params);
				contactListContainer.addView(numberEditText, params);
				onEdittingProcess = true;
			} else if (onEdittingProcess) {
				String name = nameEditText.getText().toString();
				String phoneNumber = numberEditText.getText().toString();

				if (TextUtils.isEmpty(name)) {
					Toast.makeText(ConfigureContacts.this,
							"Lütfen geçerli bir isim giriniz",
							Toast.LENGTH_SHORT).show();
				} else if (TextUtils.isEmpty(phoneNumber)
						|| phoneNumber.length() < 10) {
					Toast.makeText(ConfigureContacts.this,
							"Lütfen geçerli bir telefon numarası giriniz",
							Toast.LENGTH_SHORT).show();
				} else {
					addContactButton
							.setText(getResources()
									.getString(
											R.string.accident_recognition_configure_contact_add_contact_number));
					contactNameList.add(name);
					contactPhoneNumberList.add(phoneNumber);
					readyToApply = true;
					onEdittingProcess = false;
					contactListContainer.removeView(numberEditText);
					contactListContainer.removeView(nameEditText);
					numberEditText.setHint("");
					nameEditText.setHint("");

					showContactInfo();
				}
			}

		} else if (clickId == deleteAllContacts.getId()) {
			// Delete selected contacts
			SparseBooleanArray checkedItems = contactListView
					.getCheckedItemPositions();
			int removeCount = 0;
			for (int i = 0; i < checkedItems.size(); i++) {
				int position = checkedItems.keyAt(i);
				Log.d("Delete", "Will be deleted" + position);
				if (checkedItems.valueAt(i)) {
					contactNameList.remove(position - removeCount);
					contactPhoneNumberList.remove(position - removeCount);
					Log.d("Delete", "be deleted" + (position - removeCount));
					removeCount++;
				}
			}
			readyToApply = true;
			showContactInfo();
		}

	}

	private void addFromContacts() {
		Intent contactListStarter = new Intent(Intent.ACTION_PICK,
				ContactsContract.Contacts.CONTENT_URI);
		startActivityForResult(contactListStarter,
				Keys.CONTACT_LIST_REQUEST_CODE);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == Keys.CONTACT_LIST_REQUEST_CODE) {
			if (resultCode == RESULT_OK) {
				Uri contactData = data.getData();
				Cursor c = getContentResolver().query(contactData, null, null,
						null, null);
				if (c.moveToFirst()) {
					do {

						String name = c
								.getString(c
										.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
						Log.d("name = ", "" + name);
						String hasPhoneNumber = c
								.getString(c
										.getColumnIndexOrThrow(ContactsContract.Contacts.HAS_PHONE_NUMBER));
						String id = c
								.getString(c
										.getColumnIndexOrThrow(ContactsContract.Contacts._ID));

						if (hasPhoneNumber.equalsIgnoreCase("1")) {
							Cursor phones = getContentResolver()
									.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
											null,
											ContactsContract.CommonDataKinds.Phone.CONTACT_ID
													+ " = " + id, null, null);
							phones.moveToFirst();
							String cNumber = phones.getString(phones
									.getColumnIndex("data1"));
							Log.d("number = ", "" + cNumber);
							contactNameList.add(name);
							contactPhoneNumberList.add(cNumber);
						}
					} while (c.moveToNext());
				}
				showContactInfo();
			}
		}

	}

	private void saveContactsToFile() {
		if (contactsData != null) {
			contactsData.setContacts(contactNameList);
			contactsData.setContactPhoneNumbers(contactPhoneNumberList);
		} else if (contactsData == null) {
			contactsData = new ContactsData(contactNameList,
					contactPhoneNumberList);
		}

		String contactInfoInJson = new Gson().toJson(contactsData,
				ContactsData.class);
		try {
			Helper.createContacts(contactInfoInJson);
			Toast.makeText(ConfigureContacts.this, "Kaydetme işlemi başarılı.",
					Toast.LENGTH_LONG).show();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
