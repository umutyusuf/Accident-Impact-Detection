package com.umut.accidentrecognitioncp;

import java.io.BufferedReader;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.umut.accidentrecognitioncp.fileoperations.ArffReader;
import com.umut.accidentrecognitioncp.listeners.SensorInfoChangedListener;
import com.umut.accidentrecognitioncp.machinelearning.J48Classifier;
import com.umut.accidentrecognitioncp.machinelearning.NaiveBayesImplementor;
import com.umut.accidentrecognitioncp.sideactivities.ConfigureContacts;

public class MainActivity extends Activity implements
		SensorInfoChangedListener, View.OnClickListener {

	private ArffReader arffReader;
	private NaiveBayesImplementor nImplementor;

	private ImageButton triggerButton, configureContacts, stopService;
	private TextView xDisplayer, yDisplayer, zDisplayer;

	private J48Classifier j48Classifier;

	SharedPreferences pref;
	SharedPreferences.Editor editor;

	SharedPreferences getData;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		initializeViews();

		try {
			configureMachineLearningImplementor();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void initializeViews() {

		// Initialize Buttons and manage click listeners...
		triggerButton = (ImageButton) findViewById(R.id.detector_service_trigger);
		configureContacts = (ImageButton) findViewById(R.id.contact_configure);
		stopService = (ImageButton) findViewById(R.id.detector_service_destroyer);

		triggerButton.setImageResource(R.drawable.start);
		triggerButton.setOnClickListener(this);
		configureContacts.setOnClickListener(this);
		stopService.setOnClickListener(this);
		stopService.setEnabled(false);

		// Initialize textViews to display accelerometer values...
		xDisplayer = (TextView) findViewById(R.id.accident_recognition_x_value);
		yDisplayer = (TextView) findViewById(R.id.accident_recognition_y_value);
		zDisplayer = (TextView) findViewById(R.id.accident_recognition_z_value);

		getData = PreferenceManager
				.getDefaultSharedPreferences(MainActivity.this);

	}

	private void configureMachineLearningImplementor() throws Exception {

		arffReader = new ArffReader(MainActivity.this);
		BufferedReader reader = arffReader.readArffFile("DataSet.arff");
		nImplementor = new NaiveBayesImplementor(reader);

		j48Classifier = new J48Classifier();

	}

	@Override
	public void onSensorInfoChanged(float[] sensorInfo) {

		xDisplayer.setText("" + sensorInfo[0]);
		yDisplayer.setText("" + sensorInfo[1]);
		zDisplayer.setText("" + sensorInfo[2]);

	}

	@Override
	public void onClick(View v) {
		if (v.getId() == triggerButton.getId()) {

			String alg = getData.getString("Algoritma", "J48");
			Log.d("String alg = ","" + alg);
			if (TextUtils.equals(alg, "J48")) {
				DetectorService.configureService(this, j48Classifier);
			}else{
				DetectorService.configureService(this, nImplementor);
			}
			Intent startServiceIntent = new Intent(MainActivity.this,
					DetectorService.class);
			this.startService(startServiceIntent);
			stopService.setEnabled(true);
			triggerButton.setEnabled(false);

		} else if (v.getId() == configureContacts.getId()) {
			Intent startConfigureContacts = new Intent(MainActivity.this,
					ConfigureContacts.class);
			startActivity(startConfigureContacts);
		} else if (v.getId() == stopService.getId()) {
			Intent stopServiceIntent = new Intent(MainActivity.this,
					DetectorService.class);
			this.stopService(stopServiceIntent);
			stopService.setEnabled(false);
			triggerButton.setEnabled(true);
		}

	}

	@Override
	protected void onResume() {

		super.onResume();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub

		MenuInflater blowUp = getMenuInflater();
		blowUp.inflate(R.menu.main, menu);
		return true;

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub

		switch (item.getItemId()) {

		case R.id.alg_selector:
			Intent a = new Intent("com.umut.accidentrecognitioncp.sideactivities.PREFERENCES");
			startActivity(a);
			break;

		}

		return false;
	}
}