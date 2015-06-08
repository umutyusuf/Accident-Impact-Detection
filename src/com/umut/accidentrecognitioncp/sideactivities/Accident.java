package com.umut.accidentrecognitioncp.sideactivities;

import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.umut.accidentrecognitioncp.R;
import com.umut.accidentrecognitioncp.data.ContactsData;
import com.umut.accidentrecognitioncp.exceptions.FileMissingException;
import com.umut.accidentrecognitioncp.utils.Helper;
import com.umut.accidentrecognitioncp.utils.Keys;

public class Accident extends Activity implements OnClickListener {

	private TextView impactDisplayer;
	private LinearLayout ll1;
	private Intent triggerIntent;
	private Button cancelButton;
	private Thread sleepThread;
	private boolean buttonClicked;
	
	private Ringtone ringtone;
	public BroadcastReceiver receiverSent = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String result = "";

			switch (getResultCode()) {

			case Activity.RESULT_OK:
				result = "Transmission successful";
				break;
			case android.telephony.SmsManager.RESULT_ERROR_GENERIC_FAILURE:
				result = "Transmission failed";
				break;
			case android.telephony.SmsManager.RESULT_ERROR_RADIO_OFF:
				result = "Radio off";
				break;
			case android.telephony.SmsManager.RESULT_ERROR_NULL_PDU:
				result = "No PDU defined";
				break;
			case android.telephony.SmsManager.RESULT_ERROR_NO_SERVICE:
				result = "No service";
				break;
			}

			Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG)
					.show();

		}
	};

	public BroadcastReceiver receiverDeliver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			Toast.makeText(getApplicationContext(), "Deliverd",
					Toast.LENGTH_LONG).show();
			unregisterReceiver(this);

		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.accident);
		initializeViews();

		getData();
		Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);

		if (alert == null) {
			// alert is null, using backup
			alert = RingtoneManager
					.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

			// I can't see this ever being null (as always have a default
			// notification)
			// but just incase
			if (alert == null) {
				// alert backup is null, using 2nd backup
				alert = RingtoneManager
						.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
			}
		}

		ringtone = RingtoneManager.getRingtone(Accident.this, alert);
		ringtone.play();
		sleepThread = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					Thread.sleep(5000);
					if (!buttonClicked && Helper.contactsDataExist()) {
						try {

							ContactsData contactData = Helper
									.getContactsData(Accident.this);
							List<String> phoneNumbers = contactData
									.getContactPhoneNumbers();

							for (int i = 0; i < phoneNumbers.size(); i++) {

								String text = "Kazaya maruz kaldım";
								String phoneNo = phoneNumbers.get(i);

								try {

									String SENT = "sent";
									String DELIVERED = "delivered";

									Intent sentIntent = new Intent(SENT);
									/* Create Pending Intents */
									PendingIntent sentPI = PendingIntent
											.getBroadcast(
													getApplicationContext(),
													0,
													sentIntent,
													PendingIntent.FLAG_UPDATE_CURRENT);

									Intent deliveryIntent = new Intent(
											DELIVERED);

									PendingIntent deliverPI = PendingIntent
											.getBroadcast(
													getApplicationContext(),
													0,
													deliveryIntent,
													PendingIntent.FLAG_UPDATE_CURRENT);
									/* Register for SMS send action */
									registerReceiver(receiverSent,
											new IntentFilter(SENT));
									/* Register for Delivery event */
									registerReceiver(receiverDeliver,
											new IntentFilter(DELIVERED));

									/* Send SMS */
									android.telephony.SmsManager smsManager = android.telephony.SmsManager
											.getDefault();
									smsManager.sendTextMessage(phoneNo, null,
											text, sentPI, deliverPI);
								} catch (Exception ex) {
									Toast.makeText(getApplicationContext(),
											ex.getMessage().toString(),
											Toast.LENGTH_LONG).show();
									ex.printStackTrace();
								}
							}

						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (FileMissingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				ringtone.stop();

			}
		});
		sleepThread.start();

	}

	private void initializeViews() {
		impactDisplayer = (TextView) findViewById(R.id.accident_recognition_accident_identifier);
		ll1 = (LinearLayout) findViewById(R.id.accident_recognition_accident_activity_ll1);
		cancelButton = (Button) findViewById(R.id.accident_recognition_accident_cancel_button);
		cancelButton.setOnClickListener(this);
	}

	private void getData() {
		triggerIntent = getIntent();
		String impactNum = triggerIntent.getStringExtra(Keys.IMPACT_TYPE);
		int num = (int) Double.parseDouble(impactNum);
		switch (num) {
		case 0:
			impactDisplayer.setText("Yüksek Şiddet");
			ll1.setBackgroundColor(getResources().getColor(R.color.RED));
			break;
		case 1:
			impactDisplayer.setText("Orta Şiddet");
			ll1.setBackgroundColor(getResources().getColor(R.color.BLUE));
			break;
		case 2:
			impactDisplayer.setText("Düşük Şiddet");
			ll1.setBackgroundColor(getResources().getColor(R.color.GREEN));
			break;
		}

	}

	@Override
	protected void onPause() {

		super.onPause();
		try {
			unregisterReceiver(receiverDeliver);
			unregisterReceiver(receiverSent);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		int clickId = v.getId();

		if (clickId == cancelButton.getId()) {
			buttonClicked = true;
			ringtone.stop();
		}

	}

}
