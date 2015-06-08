package com.umut.accidentrecognitioncp.exceptions;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class FileMissingException extends Exception {

	private static final long serialVersionUID = -5425872231715300402L;

	public FileMissingException(Context context, String message) {
		Toast.makeText(context, message, Toast.LENGTH_LONG).show();
		Log.d("File Missing", message);
	}
}
