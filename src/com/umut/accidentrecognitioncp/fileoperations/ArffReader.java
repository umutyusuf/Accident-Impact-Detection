package com.umut.accidentrecognitioncp.fileoperations;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import android.content.Context;
import android.content.res.AssetManager;

/* 
 * This class reads .arff file from assets to use evaluation/training 
 * machine learning method chosen. Fields.
 * 
 *  - appContext: context of application (necessary to get assets)
 *  - BufferedReader reader: Instance class of WEKA takes reader parameter to create instances.
 *  - assetManager: to get the assets and read the data 
 * */

public class ArffReader {
	
	private Context appContext;
	
	private BufferedReader reader;
	private AssetManager assetManager;
	
	/* Constructor parameters
	 *  -appContext: Context
	 *  */
	public ArffReader(Context appContext){
		this.appContext = appContext;
	}
	/* 
	 * Reads given file from assets to BufferedReader and returns it.
	 * Parameters
	 * 	-fileName: name of file to read
	 * */
	public BufferedReader readArffFile(String fileName) throws IOException{
		
		assetManager = appContext.getAssets();
		reader = new BufferedReader(new InputStreamReader(assetManager.open(fileName)));
		
		return reader;
	}
}
