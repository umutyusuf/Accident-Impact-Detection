package com.umut.accidentrecognitioncp.utils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.os.Environment;

import com.google.gson.Gson;
import com.umut.accidentrecognitioncp.data.ContactsData;
import com.umut.accidentrecognitioncp.exceptions.FileMissingException;

public class Helper {
	public static final String folderName = "ytu/ce/";
	public static final String sessionFileName = "contacts.tl";
	public static final String logFileName = "logs.txt";

	public static File folderFile, sessionFile, logsFile;

	public static boolean contactsDataExist() {

		folderFile = new File(Environment.getExternalStorageDirectory(),
				folderName);

		if (folderFile.exists() && folderFile.isDirectory()) {

			sessionFile = new File(folderFile, sessionFileName);

			if (sessionFile.exists()) {
				return true;
			}

			return false;

		} else {
			return false;
		}

	}

	public static boolean createLogsFile() {
		folderFile = new File(Environment.getExternalStorageDirectory(),
				folderName);

		if (folderFile.exists() && folderFile.isDirectory()) {

			logsFile = new File(folderFile, logFileName);

			if (logsFile.exists()) {
				return true;
			}

			return false;

		} else {
			return false;
		}
	}

	public static void writeLogFile(String logs) throws IOException {

		FileOutputStream fos = null;
		DataOutputStream dos = null;

		logsFile = new File(folderFile, logFileName);
		
		fos = new FileOutputStream(logsFile);
		dos = new DataOutputStream(fos);
		
		dos.writeUTF(logs);

		fos.close();
		dos.close();

		fos = null;
		dos = null;

	}

	public static void createContacts(String jsonData) throws IOException {

		if (!folderFile.exists()) {
			folderFile.mkdirs();
		}

		sessionFile = new File(folderFile, sessionFileName);

		FileOutputStream fos = null;
		DataOutputStream dos = null;

		fos = new FileOutputStream(sessionFile);
		dos = new DataOutputStream(fos);

		dos.writeUTF(jsonData);

		fos.close();
		dos.close();

		fos = null;
		dos = null;

	}

	public static ContactsData getContactsData(Context context)
			throws IOException, FileMissingException {

		sessionFile = new File(folderFile, sessionFileName);

		ContactsData temp = null;

		if (!sessionFile.exists()) {
			throw new FileMissingException(context, "Dosya yok!");
		} else {
			FileInputStream fis;
			DataInputStream dis;

			fis = new FileInputStream(sessionFile);
			dis = new DataInputStream(fis);

			temp = new Gson().fromJson(dis.readUTF(), ContactsData.class);

			fis.close();
			dis.close();
		}

		return temp;

	}

	public static void destroyContactData(Context context)
			throws FileMissingException {

		sessionFile = new File(folderFile, sessionFileName);

		if (!sessionFile.delete()) {

			throw new FileMissingException(context, "Dosya silinemedi");
		}

	}

}
