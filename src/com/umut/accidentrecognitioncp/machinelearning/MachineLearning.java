package com.umut.accidentrecognitioncp.machinelearning;

import com.umut.accidentrecognitioncp.data.Features;

public class MachineLearning {
	private Object type;
	
	public MachineLearning(Object type){
		this.type = type;
	}
	
	public String classifyInstance(Features feature){
		if(type instanceof NaiveBayes){
			((NaiveBayes) type).buildClassifier();
			return ((NaiveBayes) type).testSample(feature);
		}else{
			return ((J48Classifier) type).classifyInstance(feature);
		}
	}
}
