package com.umut.accidentrecognitioncp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class ScreenOffBroadcastReceiver extends BroadcastReceiver {

	private SensorManager sensorManager;
	private Sensor accelerometerSensor;
	private int samplingRate;
	private SensorEventListener sensorListener;

	public ScreenOffBroadcastReceiver(SensorManager sensorManager,
			Sensor accelerometerSensor, int samplingRate) {
		this.sensorManager = sensorManager;
		this.accelerometerSensor = accelerometerSensor;
		this.samplingRate = samplingRate;
	}

	@Override
	public void onReceive(Context context, Intent intent) {

		String actionFilter = intent.getAction();
		if (actionFilter.equals(Intent.ACTION_SCREEN_OFF)) {
			sensorManager.unregisterListener(sensorListener);
			sensorManager.registerListener(sensorListener, accelerometerSensor,
					samplingRate);
		}

	}

}
