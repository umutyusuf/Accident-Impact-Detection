package com.umut.accidentrecognitioncp.machinelearning;

import android.util.Log;

import com.umut.accidentrecognitioncp.data.Features;

public class J48Classifier {

	public String fast = "0";
	public String normal = "1";
	public String slow = "2";

	public J48Classifier() {

	}

	public String classifyInstance(Features feature) {

		Log.d("features avc", "" + feature.avc);
		Log.d("features fallDivideY", "" + feature.fallDivideY);
		Log.d("features ChangeCountC", "" + feature.changeCountX);
		Log.d("features rmsY", "" + feature.rmsY);
		Log.d("features rmsAcc", "" + feature.rmsAcc);

		if (feature.fallDivideY > 0.000161) {
			if (feature.avc > 0.099286) {
				if (feature.avc <= 0.208003) {
					Log.d("determined", " from  2. avc");
					return normal;
				} else {
					Log.d("determined", " from 2. avc");
					return slow;
				}
			} else if (feature.rmsAcc > 1.002325) {
				if (feature.changeCountX > 156) {
					Log.d("determined", " from changeCountX");
					return fast;
				} else if (feature.changeCountX > 133) {
					Log.d("determined", " from changeCountX");
					return normal;
				} else {
					Log.d("determined", " from changeCountX");
					return fast;
				}

			} else if (feature.rmsY <= 0.904172) {
				Log.d("determined", " from rmsY");
				return fast;
			} else {
				Log.d("determined", " from rmsY");
				return slow;
			}
		} else {
			Log.d("determined", " from fallDivY or rmsAcc");
			return slow;
		}
	}
}
