package com.xtrd.obdcar.config;

import java.io.File;

import android.os.Environment;

public class CacheConfig {
	private static final String ROOT_DIR = Environment.getExternalStorageDirectory() + "/xtrd/";
	private static final String DOWNLOAD_DIR = Environment.getExternalStorageDirectory() + "/xtrd/download/";

	
	public static String getRootDir() {
		File file = new File(ROOT_DIR);
		if (!file.exists()) {
			file.mkdirs();
		}
		return file.getAbsolutePath() + "/";
	}
	public static File getRootFile() {
		File file = new File(ROOT_DIR);
		if (!file.exists()) {
			file.mkdirs();
		}
		return file;
	}
	
	public static String getDownloadDir() {
		File file = new File(DOWNLOAD_DIR);
		if (!file.exists()) {
			file.mkdirs();
		}
		return file.getAbsolutePath() + "/";
	}
	public static File getDownloadFile() {
		File file = new File(DOWNLOAD_DIR);
		if (!file.exists()) {
			file.mkdirs();
		}
		return file;
	}
	
}
