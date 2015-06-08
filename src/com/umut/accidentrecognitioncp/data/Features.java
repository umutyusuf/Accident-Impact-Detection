package com.umut.accidentrecognitioncp.data;


public class Features {

	public double avc;
	public double rmsY;
	public double rmsAcc;
	public double meanAcc;
	public double changeCountX;
	public double maxRiseY;
	public double fallDivideY;
	public double riseDivideY;
	public double changeCountAcc;
	public double fallDivideAcc;

	public static int NUM_OF_FEATURES = 10;

	public Features(double[] featureArray) {

		decodeFromArray(featureArray);

	}

	private void decodeFromArray(double[] featureArray) {

		// Copy features
		avc = featureArray[0];
		rmsY = featureArray[1];
		rmsAcc = featureArray[2];
		changeCountX = featureArray[3];
		fallDivideY = featureArray[4];
		meanAcc = featureArray[5];
		maxRiseY = featureArray[6];
		riseDivideY = featureArray[7];
		changeCountAcc = featureArray[8];
		fallDivideAcc = featureArray[9];

	}

	public Features() {
		// Empty contructer...
	}

}
