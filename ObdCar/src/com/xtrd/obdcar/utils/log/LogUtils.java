package com.xtrd.obdcar.utils.log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.Date;

import org.apache.http.conn.ConnectTimeoutException;

import android.util.Log;

import com.xtrd.obdcar.config.LogConfig;
import com.xtrd.obdcar.utils.StringUtils;
import com.xtrd.obdcar.utils.Utils;

/**
 * 日志输出,可以通过LogConfig进行配置和控制
 */
@SuppressWarnings("unused")
public class LogUtils {

	public static void v(String tag, String msg) {
		if (LogConfig.PRINT_TO_SYSTEM && Log.VERBOSE >= LogConfig.LEVEL_PRINT_TO_SYSTEM) {
			Log.v(tag, msg);
		}
		if (LogConfig.PRINT_TO_FILE && Log.VERBOSE >= LogConfig.LEVEL_PRINT_TO_FILE) {
			writeLogToFile(tag, msg);
		}
	}

	public static void v(String tag, String msg, Throwable tr) {
		if (LogConfig.PRINT_TO_SYSTEM && Log.VERBOSE >= LogConfig.LEVEL_PRINT_TO_SYSTEM) {
			Log.v(tag, msg, tr);
		}
		if (LogConfig.PRINT_TO_FILE && Log.VERBOSE >= LogConfig.LEVEL_PRINT_TO_FILE) {
			writeLogToFile(tag, msg, tr);
		}
	}

	public static void d(String tag, String msg) {
		if (LogConfig.PRINT_TO_SYSTEM && Log.DEBUG >= LogConfig.LEVEL_PRINT_TO_SYSTEM) {
			Log.d(tag, msg);
		}
		if (LogConfig.PRINT_TO_FILE && Log.DEBUG >= LogConfig.LEVEL_PRINT_TO_FILE) {
			writeLogToFile(tag, msg);
		}
	}

	public static void d(String tag, String msg, Throwable tr) {
		if (LogConfig.PRINT_TO_SYSTEM && Log.DEBUG >= LogConfig.LEVEL_PRINT_TO_SYSTEM) {
			Log.d(tag, msg, tr);
		}
		if (LogConfig.PRINT_TO_FILE && Log.DEBUG >= LogConfig.LEVEL_PRINT_TO_FILE) {
			writeLogToFile(tag, msg, tr);
		}
	}

	public static void i(String tag, String msg) {
		if (LogConfig.PRINT_TO_SYSTEM && Log.INFO >= LogConfig.LEVEL_PRINT_TO_SYSTEM) {
			Log.i(tag, msg);
		}
		if (LogConfig.PRINT_TO_FILE && Log.INFO >= LogConfig.LEVEL_PRINT_TO_FILE) {
			writeLogToFile(tag, msg);
		}
	}

	public static void i(String tag, String msg, Throwable tr) {
		if (LogConfig.PRINT_TO_SYSTEM && Log.INFO >= LogConfig.LEVEL_PRINT_TO_SYSTEM) {
			Log.i(tag, msg);
		}
		if (LogConfig.PRINT_TO_FILE && Log.INFO >= LogConfig.LEVEL_PRINT_TO_FILE) {
			writeLogToFile(tag, msg, tr);
		}
	}

	public static void w(String tag, String msg) {
		if (LogConfig.PRINT_TO_SYSTEM && Log.WARN >= LogConfig.LEVEL_PRINT_TO_SYSTEM) {
			Log.w(tag, msg);
		}
		if (LogConfig.PRINT_TO_FILE && Log.WARN >= LogConfig.LEVEL_PRINT_TO_FILE) {
			writeLogToFile(tag, msg);
		}
	}

	public static void w(String tag, String msg, Throwable tr) {
		if (LogConfig.PRINT_TO_SYSTEM && Log.WARN >= LogConfig.LEVEL_PRINT_TO_SYSTEM) {
			Log.w(tag, msg, tr);
		}
		if (LogConfig.PRINT_TO_FILE && Log.WARN >= LogConfig.LEVEL_PRINT_TO_FILE) {
			writeLogToFile(tag, msg, tr);
		}
	}

	public static void w(String tag, Throwable tr) {
		if (LogConfig.PRINT_TO_SYSTEM && Log.WARN >= LogConfig.LEVEL_PRINT_TO_SYSTEM) {
			Log.w(tag, tr);
		}
		if (LogConfig.PRINT_TO_FILE && Log.WARN >= LogConfig.LEVEL_PRINT_TO_FILE) {
			writeLogToFile(tag, tr);
		}
	}

	public static void e(String tag, String msg) {
		if (LogConfig.PRINT_TO_SYSTEM && Log.ERROR >= LogConfig.LEVEL_PRINT_TO_SYSTEM) {
			Log.e(tag, msg);
		}
		if (LogConfig.PRINT_TO_FILE && Log.ERROR >= LogConfig.LEVEL_PRINT_TO_FILE) {
			writeLogToFile(tag, msg);
		}
	}

	public static void e(String tag, String msg, Throwable tr) {
		if (LogConfig.PRINT_TO_SYSTEM && Log.ERROR >= LogConfig.LEVEL_PRINT_TO_SYSTEM) {
			Log.e(tag, msg, tr);
		}
		if (LogConfig.PRINT_TO_FILE && Log.ERROR >= LogConfig.LEVEL_PRINT_TO_FILE) {
			writeLogToFile(tag, msg, tr);
		}
	}

	public static void e(String tag, Throwable e) {
		e(tag, e.toString());
		if (e instanceof ConnectTimeoutException || e instanceof SocketTimeoutException
				|| e instanceof UnknownHostException) {
			return;
		}
		StackTraceElement[] trace = e.getStackTrace();
		for (int i = 0; i < trace.length; i++) {
			e(tag, "\tat " + trace[i]);
		}
		Throwable ourCause = e.getCause();
		if (ourCause != null) {
			e(tag, ourCause);
		}
	}

	public static void println(int priority, String tag, String msg) {
		if (LogConfig.PRINT_TO_SYSTEM) {
			Log.println(priority, tag, msg);
		}
		if (LogConfig.PRINT_TO_FILE) {
			writeLogToFile(tag, msg);
		}
	}

	private static void writeLogToFile(String tag, String msg) {
		writeLogToFile(tag, msg, null);
	}

	private static void writeLogToFile(String tag, Throwable tr) {
		writeLogToFile(tag, null, tr);
	}

	private static void writeLogToFile(String tag, String msg, Throwable tr) {
		if (msg != null || tr != null) {
			if (Utils.isSDCardEnable()) {
				FileOutputStream fos;
				try {
					File log = new File(LogConfig.getLogDir(), LogConfig.LOG_FILENAME);
					if (!log.exists()) {
						try {
							log.createNewFile();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					if (log.length() >= LogConfig.MAX_LOG_SIZE) {
						log.delete();
						try {
							log.createNewFile();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					fos = new FileOutputStream(log, true);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
					return;
				}

				Calendar cal = Calendar.getInstance();
				Date date = cal.getTime();
				try {
					if (fos != null) {
						if (StringUtils.isNotNullOrEmpty(msg)) {
							fos.write((date.toLocaleString() + " : " + tag + " : " + msg).getBytes());
							fos.write("\n".getBytes());
							fos.flush();
						}
						if (tr != null) {
							fos.write((date.toLocaleString() + " : " + tag + " : " + getStackTraceString(tr))
									.getBytes());
							fos.write("\n".getBytes());
							fos.flush();
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					try {
						if (fos != null) {
							fos.close();
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	public static String getStackTraceString(Throwable tr) {
		if (tr == null) {
			return "";
		}

		Throwable t = tr;
		while (t != null) {
			if (t instanceof UnknownHostException) {
				return "";
			}
			t = t.getCause();
		}
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		tr.printStackTrace(pw);
		return sw.toString();
	}

}
