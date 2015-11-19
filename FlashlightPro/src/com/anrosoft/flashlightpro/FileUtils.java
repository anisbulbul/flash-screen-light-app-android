package com.anrosoft.flashlightpro;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;

import android.content.Context;
import android.widget.Toast;

public class FileUtils {
	public static String readFileData(Context context, File file) {
		String allInfo = "";
		String line = "";
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			while ((line = br.readLine()) != null) {
				allInfo = allInfo + "\n" + line;
			}
			br.close();
		} catch (Exception e) {
			Toast.makeText(context, "read : " + e.getMessage(),
					Toast.LENGTH_SHORT).show();
		}
		return allInfo;
	}

	public static void writeFileData(Context context, File file, String info) {
		try {
			FileOutputStream fOut = new FileOutputStream(file);
			OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
			myOutWriter.write(info.trim());
			myOutWriter.close();
			fOut.close();
		} catch (Exception e) {
			Toast.makeText(context, "write : " + e.getMessage(),
					Toast.LENGTH_SHORT).show();
		}
	}
}
