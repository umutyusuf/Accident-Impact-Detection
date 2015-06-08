package com.umut.accidentrecognitioncp.sideactivities;

import com.umut.accidentrecognitioncp.R;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class Preferences extends PreferenceActivity{
	@SuppressWarnings({ "deprecation" })
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.prefs);
	
	}
	
}
