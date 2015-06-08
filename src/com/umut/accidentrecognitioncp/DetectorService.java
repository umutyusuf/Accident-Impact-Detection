package com.umut.accidentrecognitioncp;

import java.io.IOException;

import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;

import com.umut.accidentrecognitioncp.data.Features;
import com.umut.accidentrecognitioncp.listeners.SensorInfoChangedListener;
import com.umut.accidentrecognitioncp.machinelearning.AttributeExtracterClass;
import com.umut.accidentrecognitioncp.machinelearning.MachineLearning;
import com.umut.accidentrecognitioncp.sideactivities.Accident;
import com.umut.accidentrecognitioncp.utils.Helper;
import com.umut.accidentrecognitioncp.utils.Keys;

@SuppressWarnings("deprecation")
@SuppressLint("Wakelock")
public class DetectorService extends Service implements SensorEventListener {

	// To handle any kind of screen off action
	// This receiver will keep sensor registered.

	// To print sensor results to screen we use an interface
	// Because Android only let UI threads to paint screen.
	private static SensorInfoChangedListener sensorInfoChangedListener;

	// Sensor manager to register accelerometer sensor and required sampling
	// rate.
	private SensorManager sensorManager;
	private Sensor accelerometerSensor;
	public static final int SAMPLING_RATE = 10000;

	private double[][] accelerometerDataBuffer;
	private double[][] accelerometerDataBuffer2;
	private double[][] accidentData;

	public final static int NO_CHANNEL = 4;
	public final static int BUFFER_SIZE = 300;
	public final static float INITIAL_PEAK_THRESHOLD = 25;
	public final static float MOTIONLESS_THRESHOLD = 10;
	public final static int SLEEP_TIME_TO_SLIDE_WINDOW = 1200;
	public final static int ACCIDENT_CONTROL_SLEEP = 3150;

	private boolean initialPeakExceeded;
	private boolean checkAgain;

	private PowerManager pm;

	private WakeLock wakeLock;

	int counter = 0;
	private KeyguardManager keyguardManager;

	private KeyguardLock keyguardLock;

	private static Object implementor;
	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
				sensorManager.unregisterListener(DetectorService.this);
				sensorManager.registerListener(DetectorService.this,
						accelerometerSensor, SAMPLING_RATE);
			}
		}
	};

	public static void configureService(
			SensorInfoChangedListener sensorInfoListener, Object nImplementor) {
		sensorInfoChangedListener = sensorInfoListener;
		implementor = nImplementor;
	}

	@Override
	public void onCreate() {
		configureAndRegisterSensors();
		registerBroadcastReceiver();
		initializeVariables();

		pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK
				| PowerManager.ACQUIRE_CAUSES_WAKEUP
				| PowerManager.ON_AFTER_RELEASE, "TAG");
		wakeLock.acquire();
		keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
		keyguardLock = keyguardManager.newKeyguardLock("TAG");

		checkForAccidents accidentDetect = new checkForAccidents();
		Thread thread = new Thread(accidentDetect);
		thread.start();

	}

	private void configureAndRegisterSensors() {
		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		accelerometerSensor = sensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

		sensorManager
				.registerListener(this, accelerometerSensor, SAMPLING_RATE);
	}

	private void registerBroadcastReceiver() {

		IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
		registerReceiver(broadcastReceiver, filter);

	}

	private void initializeVariables() {

		accelerometerDataBuffer = new double[NO_CHANNEL][BUFFER_SIZE];
		accelerometerDataBuffer2 = new double[NO_CHANNEL][BUFFER_SIZE];
		accidentData = new double[NO_CHANNEL][BUFFER_SIZE];

		for (int i = 0; i < NO_CHANNEL; i++) {
			for (int j = 0; j < BUFFER_SIZE; j++) {
				accelerometerDataBuffer[i][j] = 9;
			}
		}

		initialPeakExceeded = false;
		checkAgain = false;

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Notification note = new Notification(R.drawable.ic_launcher,
				"Kaza Tespit Servisi Basladi.", System.currentTimeMillis());
		Intent i = new Intent(this, MainActivity.class);

		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_SINGLE_TOP);

		PendingIntent pi = PendingIntent.getActivity(this, 0, i, 0);

		note.setLatestEventInfo(this, "Kaza Tespiti", "Kaza Kontrol", pi);
		note.flags |= Notification.FLAG_NO_CLEAR;

		startForeground(1337, note);
		return Service.START_NOT_STICKY;
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		wakeLock.release();
		sensorManager.unregisterListener(this);
		unregisterReceiver(broadcastReceiver);
		stopForeground(true);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

			sensorInfoChangedListener.onSensorInfoChanged(event.values);

			pushValuesToBuffer(event.values);

		}
	}

	private void pushValuesToBuffer(float[] accelerometerValues) {
		int i, j = 0;

		double accelerationVector = Math
				.sqrt((accelerometerValues[0] * accelerometerValues[0])
						+ (accelerometerValues[1] * accelerometerValues[1])
						+ (accelerometerValues[2] * accelerometerValues[2]));
		for (i = 0; i < NO_CHANNEL - 1; i++) {
			for (j = 0; j < BUFFER_SIZE - 1; j++) {
				accelerometerDataBuffer[i][j] = accelerometerDataBuffer[i][j + 1];
			}
			accelerometerDataBuffer[i][j] = accelerometerValues[i];
		}
		for (j = 0; j < BUFFER_SIZE - 1; j++) {
			accelerometerDataBuffer[i][j] = accelerometerDataBuffer[i][j + 1];
		}
		accelerometerDataBuffer[i][j] = (float) accelerationVector;
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}

	class checkForAccidents implements Runnable {

		@Override
		public void run() {
			while (true) {
				try {
					accelerometerDataBuffer2 = accelerometerDataBuffer;
					if (initialPeakExceeded && !checkAgain) {

						// Check for motionless moment

						float min = (float) accelerometerDataBuffer2[3][0];
						float max = (float) accelerometerDataBuffer2[3][0];
						for (int i = 0; i < BUFFER_SIZE; i++) {

							if (accelerometerDataBuffer2[3][i] < min) {
								min = (float) accelerometerDataBuffer2[3][i];
							} else if (accelerometerDataBuffer2[3][i] > max) {
								max = (float) accelerometerDataBuffer2[3][i];
							}

						}

						if ((max - min) < MOTIONLESS_THRESHOLD) {
							// Wake phone(if nec) do other warning stuff.
							Log.d("Fall status", "accident difference = "
									+ (max - min));
							classifyAccidentImpact();

							Thread.sleep(ACCIDENT_CONTROL_SLEEP);
							initialPeakExceeded = false;

						} else {
							Log.d("Fall status", "not accident difference = "
									+ (max - min));
							Log.d("Max and min values", "max = " + max
									+ "min = " + min);
							checkAgain = true;
							Thread.sleep(SLEEP_TIME_TO_SLIDE_WINDOW);
						}

					} else if (!initialPeakExceeded) {
						float min = (float) accelerometerDataBuffer2[3][0];
						float max = (float) accelerometerDataBuffer2[3][0];
						for (int i = 0; i < BUFFER_SIZE; i++) {

							if (accelerometerDataBuffer2[3][i] < min) {
								min = (float) accelerometerDataBuffer2[3][i];
							} else if (accelerometerDataBuffer2[3][i] > max) {
								max = (float) accelerometerDataBuffer2[3][i];
							}

						}
						if (max - min > INITIAL_PEAK_THRESHOLD) {
							initialPeakExceeded = true;
							Log.d("threashold exceeded", "" + (max - min));
							accidentData = accelerometerDataBuffer2;

							Thread.sleep(ACCIDENT_CONTROL_SLEEP);
						} else {
							initialPeakExceeded = false;
							Thread.sleep(SLEEP_TIME_TO_SLIDE_WINDOW);
						}
					} else if (checkAgain) {

						initialPeakExceeded = false;
						checkAgain = false;

						float min = (float) accelerometerDataBuffer2[3][0];
						float max = (float) accelerometerDataBuffer2[3][0];
						for (int i = 0; i < BUFFER_SIZE; i++) {

							if (accelerometerDataBuffer2[3][i] < min) {
								min = (float) accelerometerDataBuffer2[3][i];
							} else if (accelerometerDataBuffer2[3][i] > max) {
								max = (float) accelerometerDataBuffer2[3][i];
							}

						}

						if ((max - min) < MOTIONLESS_THRESHOLD) {
							Log.d("Accindent status from check again",
									"accident difference = " + (max - min));

							classifyAccidentImpact();
						} else {
							Log.d("Accident status from not accident check again",
									"accident difference = " + (max - min));
							// To evaluate anyway
							double[] features = AttributeExtracterClass
									.computeFeatures(accidentData);
							String accidentType = "0";
							try {
								MachineLearning alg = new MachineLearning(
										implementor);
								accidentType = alg
										.classifyInstance(new Features(features));
								Log.d("Kaza şiddeti", "" + accidentType);
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							Helper.createLogsFile();
							try {
								Helper.writeLogFile("No accident is determined but log if it was not skipped -> "
										+ accidentType + "\n");
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						}

						Thread.sleep(SLEEP_TIME_TO_SLIDE_WINDOW);
					}

				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}

		private void classifyAccidentImpact() {
			double[] features = AttributeExtracterClass
					.computeFeatures(accidentData);
			String accidentType = "0";
			for (int i = 0; i < features.length; i++) {
				Log.d("features", "" + features[i]);
			}
			try {
				MachineLearning alg = new MachineLearning(implementor);
				accidentType = alg.classifyInstance(new Features(features));
				Log.d("Kaza şiddeti", "" + accidentType);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			wakeLock.acquire();
			keyguardLock.disableKeyguard();
			Intent i = new Intent(DetectorService.this, Accident.class);
			i.putExtra(Keys.IMPACT_TYPE, "" + accidentType);
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(i);
			// stopSelf();
			keyguardManager.exitKeyguardSecurely(null);

		}

	}

}
