package com.umut.accidentrecognitioncp.machinelearning;

import java.io.BufferedReader;

import weka.classifiers.bayes.NaiveBayes;
import weka.core.Instance;
import weka.core.Instances;

public class NaiveBayesImplementor {

	BufferedReader reader;

	private Instances trainInstance;
	private NaiveBayes naiveBayes;

	public NaiveBayesImplementor(BufferedReader reader) {
		this.reader = reader;
	}

	public void buildClassifier() throws Exception {

		trainInstance = new Instances(reader);

		trainInstance.setClassIndex(trainInstance.numAttributes() - 1);
		naiveBayes = new NaiveBayes();
		naiveBayes.buildClassifier(trainInstance);

	}

	public String classifyInstance(double[] values) throws Exception {

		Instance instance = convertToInstance(values);

		return classifyInstance(instance);
		
	}

	private Instance convertToInstance(double[] values) {

		Instance testInstance = new Instance(values.length);

		for (int i = 0; i < values.length; i++) {
			testInstance.setValue(trainInstance.attribute(i), values[i]);
		}

		return testInstance;
	}

	public String classifyInstance(Instance instance) throws Exception {

		instance.setDataset(trainInstance);

		return String.valueOf(naiveBayes.classifyInstance(instance));
	}
}
