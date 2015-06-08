package com.umut.accidentrecognitioncp.machinelearning;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.umut.accidentrecognitioncp.data.Features;
import com.umut.accidentrecognitioncp.fileoperations.ArffReader;

public class NaiveBayes {

	// Train data for all 3 classes
	private List<Features> fastTrainData;
	private List<Features> normalTrainData;
	private List<Features> slowTrainData;

	// Train data read from file
	// 0 - fast, 1 - normal 2 - slow
	private Features[][] allTrainData;

	// We need to read train set from assets
	private ArffReader trainSetReader;
	private BufferedReader reader;

	// To buffer line read represent a train data
	private String bufferString;

	private double[] featureArray;
	
	private Context context;

	public NaiveBayes(Context context) {
		this.context = context;
		initializeVariables();

		trainSetReader = new ArffReader(this.context);
		try {
			reader = trainSetReader.readArffFile("TrainDataSet.arff");
			while ((bufferString = reader.readLine()) != null) {

				// Segment all features
				String[] featuresString = bufferString.split(",");

				// Store accident Class
				String accidentClass = featuresString[featuresString.length - 1];

				// Initialize array to copy features
				featureArray = new double[featuresString.length];

				// Convert String to double to contract features
				for (int i = 0; i < featuresString.length - 1; i++) {
					featureArray[i] = Double.parseDouble(featuresString[i]);
				}

				// Finally create features and add corresponding list
				Features tempFeatures = new Features(featureArray);

				if (TextUtils.equals(accidentClass, "fast")) {
					fastTrainData.add(tempFeatures);
				} else if (TextUtils.equals(accidentClass, "normal")) {
					normalTrainData.add(tempFeatures);
				} else if (TextUtils.equals(accidentClass, "slow")) {
					slowTrainData.add(tempFeatures);
				}

			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void initializeVariables() {

		// Initialize train data set
		fastTrainData = new ArrayList<Features>();
		normalTrainData = new ArrayList<Features>();
		slowTrainData = new ArrayList<Features>();

		// Initialize features for naive bayes
		allTrainData = new Features[3][2];

		for (int i = 0; i < allTrainData.length; i++) {
			for (int j = 0; j < allTrainData[i].length; j++) {
				allTrainData[i][j] = new Features();
			}
		}

	}

	public void buildClassifier() {

		// Compute means and std for fast, normal and slow data
		// mean values ...
		allTrainData[0][0] = computeMeans(fastTrainData);
		allTrainData[1][0] = computeMeans(normalTrainData);
		allTrainData[2][0] = computeMeans(slowTrainData);

		// std values...

		allTrainData[0][1] = computeStd(fastTrainData, allTrainData[0][0]);
		allTrainData[1][1] = computeStd(normalTrainData, allTrainData[1][0]);
		allTrainData[2][1] = computeStd(slowTrainData, allTrainData[2][0]);
	}

	private Features computeStd(List<Features> trainData, Features meanValues) {

		Features stdFeatures = new Features();

		for (int i = 0; i < trainData.size(); i++) {
			stdFeatures.avc += Math.pow(
					(trainData.get(i).avc - meanValues.avc), 2);
			stdFeatures.changeCountAcc += Math
					.pow((trainData.get(i).changeCountAcc - meanValues.changeCountAcc),
							2);
			stdFeatures.changeCountX += Math.pow(
					(trainData.get(i).changeCountX - meanValues.changeCountX),
					2);
			stdFeatures.fallDivideAcc += Math
					.pow((trainData.get(i).fallDivideAcc - meanValues.fallDivideAcc),
							2);
			stdFeatures.fallDivideY += Math.pow(
					(trainData.get(i).fallDivideY - meanValues.fallDivideY), 2);
			stdFeatures.maxRiseY += Math.pow(
					(trainData.get(i).maxRiseY - meanValues.maxRiseY), 2);
			stdFeatures.meanAcc += Math.pow(
					(trainData.get(i).meanAcc - meanValues.meanAcc), 2);
			stdFeatures.riseDivideY += Math.pow(
					(trainData.get(i).riseDivideY - meanValues.riseDivideY), 2);
			stdFeatures.rmsAcc += Math.pow(
					(trainData.get(i).rmsAcc - meanValues.rmsAcc), 2);
			stdFeatures.rmsY += Math.pow(
					(trainData.get(i).rmsY - meanValues.rmsY), 2);
		}

		stdFeatures.avc = Math.sqrt(stdFeatures.avc / trainData.size());
		stdFeatures.changeCountAcc = Math.sqrt(stdFeatures.changeCountAcc
				/ trainData.size());
		stdFeatures.changeCountX = Math.sqrt(stdFeatures.changeCountX
				/ trainData.size());
		stdFeatures.fallDivideAcc = Math.sqrt(stdFeatures.fallDivideAcc
				/ trainData.size());
		stdFeatures.fallDivideY = Math.sqrt(stdFeatures.fallDivideY
				/ trainData.size());
		stdFeatures.maxRiseY = Math.sqrt(stdFeatures.maxRiseY
				/ trainData.size());
		stdFeatures.meanAcc = Math.sqrt(stdFeatures.meanAcc / trainData.size());
		stdFeatures.riseDivideY = Math.sqrt(stdFeatures.riseDivideY
				/ trainData.size());
		stdFeatures.rmsAcc = Math.sqrt(stdFeatures.rmsAcc / trainData.size());
		stdFeatures.rmsY = Math.sqrt(stdFeatures.rmsY / trainData.size());
		return stdFeatures;
	}

	private Features computeMeans(List<Features> trainData) {
		// Initialize feature to return as average values
		Features meansFeatures = new Features();

		for (int i = 0; i < trainData.size(); i++) {
			meansFeatures.avc += trainData.get(i).avc;
			meansFeatures.changeCountAcc += trainData.get(i).changeCountAcc;
			meansFeatures.changeCountX += trainData.get(i).changeCountX;
			meansFeatures.fallDivideAcc += trainData.get(i).fallDivideAcc;
			meansFeatures.fallDivideY += trainData.get(i).fallDivideY;
			meansFeatures.maxRiseY += trainData.get(i).maxRiseY;
			meansFeatures.meanAcc += trainData.get(i).meanAcc;
			meansFeatures.riseDivideY += trainData.get(i).riseDivideY;
			meansFeatures.rmsAcc += trainData.get(i).rmsAcc;
			meansFeatures.rmsY += trainData.get(i).rmsY;
		}

		meansFeatures.avc /= trainData.size();
		meansFeatures.changeCountAcc /= trainData.size();
		meansFeatures.changeCountX /= trainData.size();
		meansFeatures.fallDivideAcc /= trainData.size();
		meansFeatures.fallDivideY /= trainData.size();
		meansFeatures.maxRiseY /= trainData.size();
		meansFeatures.meanAcc /= trainData.size();
		meansFeatures.riseDivideY /= trainData.size();
		meansFeatures.rmsAcc /= trainData.size();
		meansFeatures.rmsY /= trainData.size();

		return meansFeatures;
	}

	public double laplaceAdjustment(double x, double mu, double sigma) {

		double pi = Math.PI;
		double e = Math.E;

		return (1 / Math.sqrt(2 * pi * sigma))
				* Math.pow(e,
						(-(Math.pow((x - mu), 2)) / (2 * Math.pow(sigma, 2))));
	}

	public String testSample(Features feature) {

		double fastProbability = 1;

		fastProbability *= laplaceAdjustment(feature.avc,
				allTrainData[0][0].avc, allTrainData[0][1].avc);
		fastProbability *= laplaceAdjustment(feature.changeCountAcc,
				allTrainData[0][0].changeCountAcc,
				allTrainData[0][1].changeCountAcc);
		fastProbability *= laplaceAdjustment(feature.changeCountX,
				allTrainData[0][0].changeCountX,
				allTrainData[0][1].changeCountX);
		fastProbability *= laplaceAdjustment(feature.fallDivideAcc,
				allTrainData[0][0].fallDivideAcc,
				allTrainData[0][1].fallDivideAcc);
		fastProbability *= laplaceAdjustment(feature.fallDivideY,
				allTrainData[0][0].fallDivideY, allTrainData[0][1].fallDivideY);
		fastProbability *= laplaceAdjustment(feature.maxRiseY,
				allTrainData[0][0].maxRiseY, allTrainData[0][1].maxRiseY);
		fastProbability *= laplaceAdjustment(feature.meanAcc,
				allTrainData[0][0].meanAcc, allTrainData[0][1].meanAcc);
		fastProbability *= laplaceAdjustment(feature.riseDivideY,
				allTrainData[0][0].riseDivideY, allTrainData[0][1].riseDivideY);
		fastProbability *= laplaceAdjustment(feature.rmsAcc,
				allTrainData[0][0].rmsAcc, allTrainData[0][1].rmsAcc);
		fastProbability *= laplaceAdjustment(feature.rmsY,
				allTrainData[0][0].rmsY, allTrainData[0][1].rmsY);

		double slowProbability = 1;

		slowProbability *= laplaceAdjustment(feature.avc,
				allTrainData[2][0].avc, allTrainData[2][1].avc);
		slowProbability *= laplaceAdjustment(feature.changeCountAcc,
				allTrainData[2][0].changeCountAcc,
				allTrainData[2][1].changeCountAcc);
		slowProbability *= laplaceAdjustment(feature.changeCountX,
				allTrainData[2][0].changeCountX,
				allTrainData[2][1].changeCountX);
		slowProbability *= laplaceAdjustment(feature.fallDivideAcc,
				allTrainData[2][0].fallDivideAcc,
				allTrainData[2][1].fallDivideAcc);
		slowProbability *= laplaceAdjustment(feature.fallDivideY,
				allTrainData[2][0].fallDivideY, allTrainData[2][1].fallDivideY);
		slowProbability *= laplaceAdjustment(feature.maxRiseY,
				allTrainData[2][0].maxRiseY, allTrainData[2][1].maxRiseY);
		slowProbability *= laplaceAdjustment(feature.meanAcc,
				allTrainData[2][0].meanAcc, allTrainData[2][1].meanAcc);
		slowProbability *= laplaceAdjustment(feature.riseDivideY,
				allTrainData[2][0].riseDivideY, allTrainData[2][1].riseDivideY);
		slowProbability *= laplaceAdjustment(feature.rmsAcc,
				allTrainData[2][0].rmsAcc, allTrainData[2][1].rmsAcc);
		slowProbability *= laplaceAdjustment(feature.rmsY,
				allTrainData[2][0].rmsY, allTrainData[2][1].rmsY);

		double normalProbability = 1;

		normalProbability *= laplaceAdjustment(feature.avc,
				allTrainData[1][0].avc, allTrainData[1][1].avc);
		normalProbability *= laplaceAdjustment(feature.changeCountAcc,
				allTrainData[1][0].changeCountAcc,
				allTrainData[1][1].changeCountAcc);
		normalProbability *= laplaceAdjustment(feature.changeCountX,
				allTrainData[1][0].changeCountX,
				allTrainData[1][1].changeCountX);
		normalProbability *= laplaceAdjustment(feature.fallDivideAcc,
				allTrainData[1][0].fallDivideAcc,
				allTrainData[1][1].fallDivideAcc);
		normalProbability *= laplaceAdjustment(feature.fallDivideY,
				allTrainData[1][0].fallDivideY, allTrainData[1][1].fallDivideY);
		normalProbability *= laplaceAdjustment(feature.maxRiseY,
				allTrainData[1][0].maxRiseY, allTrainData[1][1].maxRiseY);
		normalProbability *= laplaceAdjustment(feature.meanAcc,
				allTrainData[1][0].meanAcc, allTrainData[1][1].meanAcc);
		normalProbability *= laplaceAdjustment(feature.riseDivideY,
				allTrainData[1][0].riseDivideY, allTrainData[1][1].riseDivideY);
		normalProbability *= laplaceAdjustment(feature.rmsAcc,
				allTrainData[1][0].rmsAcc, allTrainData[1][1].rmsAcc);
		normalProbability *= laplaceAdjustment(feature.rmsY,
				allTrainData[1][0].rmsY, allTrainData[1][1].rmsY);

		Log.d("fast = ", "" + fastProbability);
		Log.d("normal = ", "" + normalProbability);
		Log.d("slow = ", "" + slowProbability);
		
		if (fastProbability > normalProbability) {
			if (fastProbability > slowProbability) {
				return "0";
			}
		} else if (normalProbability > slowProbability) {
			return "1";
		}

		return "2";
	}

}
