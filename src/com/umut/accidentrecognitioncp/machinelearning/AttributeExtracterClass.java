package com.umut.accidentrecognitioncp.machinelearning;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.umut.accidentrecognitioncp.utils.Keys;

public class AttributeExtracterClass {

	public double[][] rowData;
	public double[] accVector;
	public double[][] meanValues;
	public double[][] rootMeanSquare;
	public double[] standartDerivation;
	public double[][] angleVals;
	public double[] avc;

	public static final int windowSize = 300;

	public static double[] computeFeatures(double[][] rowData) {

		double[] features = new double[10];
		double avc = 0;

		double rmsY = 0, rmsAcc = 0, meanAcc = 0;
		for (int i = 0; i < rowData[3].length - 1; i++) {
			avc += Math.abs(rowData[3][i + 1] - rowData[3][i]);
		}
		for (int i = 0; i < rowData[3].length; i++) {
			rmsY += rowData[1][i] * rowData[1][i];
			rmsAcc += (rowData[3][i] / 9.8) * (rowData[3][i] / 9.8);
			meanAcc += rowData[3][i];
		}
		meanAcc += rowData[3][rowData[3].length - 1];
		meanAcc /= rowData[3].length;
		avc /= rowData[3].length;
		features[0] = avc;
		rmsY /= rowData[1].length;
		rmsAcc /= rowData[3].length;
		features[1] = Math.sqrt(rmsY);
		features[2] = Math.sqrt(rmsAcc);

		List<HashMap<String, Double>> advancingValues = computeAdvancingValues(rowData);
		features[3] = advancingValues.get(0).get(Keys.CHANGE_COUNT);
		features[4] = advancingValues.get(1).get(Keys.FALL_Divide);
		features[5] = meanAcc;
		features[6] = advancingValues.get(1).get(Keys.MAX_RISE);
		features[7] = advancingValues.get(1).get(Keys.RISE_Divide);
		features[8] = advancingValues.get(3).get(Keys.CHANGE_COUNT);
		features[9] = advancingValues.get(3).get(Keys.FALL_Divide);

		return features;
	}

	@SuppressWarnings("unused")
	public static List<HashMap<String, Double>> computeAdvancingValues(
			double[][] rowData) {

		List<HashMap<String, Double>> advancingValues = new ArrayList<HashMap<String, Double>>();

		boolean rise_on = true;

		for (int i = 0; i < rowData.length; i++) {

			double max_rise = 0, max_fall = 0, rise_count = 0, fall_count = 0, change_count = 0;
			double rise_start = 0, rise_end = 0, fall_start = 0, fall_end = 0, real_fall_start = 0, real_rise_start = 0;

			System.out.println(i);
			for (int j = 0; j < rowData[i].length - 1; j++) {
				if (rowData[i][j] <= rowData[i][j + 1]) {

					rise_count += 1;

					if (!rise_on) {
						rise_on = true;
						change_count += 1;
						rise_start = j;
						if (fall_count > max_fall) {
							max_fall = fall_count;
							fall_end = j;
							real_fall_start = fall_start;
						}
					}

					fall_count = 0;

				} else {
					fall_count += 1;

					if (rise_on) {
						rise_on = false;
						change_count += 1;
						fall_start = j;
						if (rise_count > max_rise) {
							max_rise = rise_count;
							rise_end = j;
							real_rise_start = rise_start;
						}
					}

					rise_count = 0;
				}

			}
			HashMap<String, Double> info_hash = new HashMap<String, Double>();
			double divideFall = (Math.abs(rowData[i][(int) fall_end]
					- rowData[i][(int) fall_start]))
					/ Math.abs(fall_end - fall_start);
			double divideRise = (Math.abs(rowData[i][(int) rise_end]
					- rowData[i][(int) rise_start]))
					/ Math.abs(rise_end - rise_start);
			info_hash.put(Keys.FALL_Divide, divideFall);
			info_hash.put(Keys.RISE_Divide, divideRise);
			info_hash.put(Keys.MAX_FALL, max_fall);
			info_hash.put(Keys.MAX_RISE, max_rise);
			info_hash.put(Keys.CHANGE_COUNT, change_count);

			advancingValues.add(info_hash);

			max_fall = 0;
			max_rise = 0;
			change_count = 0;

		}

		return advancingValues;
	}
}
