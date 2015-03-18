package com.xtrd.obdcar.utils;

import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {

	public static boolean regexMatcherCaseInsensitive(String str, String regex) {
		Pattern p = Pattern.compile(regex,Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(str);
		return m.matches();
	}

	/**
	 * 根据给定的小时和分钟数 返回 00:00 格式的时间字符串
	 * 
	 * @param hourOfDay
	 * @param minute
	 * @return
	 */
	public static String getTimeString(int hourOfDay, int minute) {
		String h = hourOfDay + "";
		String m = minute + "";
		if (h.length() == 1) {
			h = "0" + h;
		}
		if (m.length() == 1) {
			m = "0" + m;
		}
		return h + ":" + m;
	}

	/**
	 * 修改过长的文件名
	 * 
	 * @param filePath
	 * @return
	 */
	public static String modifyLongFileName(String filePath) {
		if (filePath != null && filePath.length() > 220) {
			filePath = filePath.substring(0, 100) + filePath.substring(filePath.length() - 130, filePath.length());
		}
		return filePath;
	}

	/**
	 * 格式化数字,控制显示长度
	 * 
	 * @param count
	 * @return
	 */
	public static String formatCount(int count) {
		if (count / (10000 * 100) > 0) {
			return "100W+";
		} else if (count / 10000 > 0) {
			return count / 10000 + "W+";
		} else {
			return count + "";
		}
	}

	/**
	 * 将byte数组转换为字符串,
	 * 
	 * @param array
	 *            byte数组
	 * @param length
	 *            截取的长度,从0开始,因中文字符等原因,实际截取的长度可能小于length
	 * @param charsetName
	 *            生成的字符串的字符编码
	 * @return
	 */
	public static String getStringFromByteArray(byte[] array, int length, String charsetName) {
		String str = "";
		int sig = 1;
		if (length <= array.length) {
			for (int i = 0; i < length; i++) {
				sig = array[i] * sig >= 0 ? 1 : -1;
			}
			if (sig < 0) {
				length -= 1;
			}
			try {
				str = new String(array, 0, length, charsetName);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return str;
	}

	public static boolean isNullOrEmpty(String str) {
		boolean flag = true;
		if (str != null && !"".equals(str.trim())) {
			flag = false;
		}
		return flag;
	}

	public static boolean isNotNullOrEmpty(String str) {
		return !isNullOrEmpty(str);
	}

	public static boolean isNickname(final String str) {
		try {
			byte[] bytes = str.getBytes("gbk");
			if (bytes.length > 20) {
				return false;
			}
		} catch (UnsupportedEncodingException e) {
		}
		final String regex = "[\\u4e00-\\u9fa5\\w]{1,}";
		return match(regex, str);
	}

	public static boolean isEmail(final String str) {
		final String regex = "^[a-zA-Z0-9]{1,}[a-zA-Z0-9\\_\\.\\-]{0,}@(([a-zA-Z0-9]){1,}\\.){1,3}[a-zA-Z0-9]{0,}[a-zA-Z]{1,}$";
		return match(regex, str);
	}

	public static boolean isRegUserName(final String str) {
		final String regex = "[0-9a-zA-Z\\_]{5,20}";
		return match(regex, str);
	}

	public static boolean isLoginUserName(final String str) {
		final String regex = "[0-9a-zA-Z\\_]{3,20}";
		return match(regex, str);
	}

	public static boolean isPassword(final String str) {
		final String regex = "[\\S]{6,16}";
		return match(regex, str);
	}

	private static boolean match(final String regex, final String str) {
		final Pattern pattern = Pattern.compile(regex);
		final Matcher matcher = pattern.matcher(str);
		return matcher.matches();
	}


	public static Set<String> parseUrl(String s) {
		Set<String> result = new HashSet<String>();
		if (isNullOrEmpty(s)) {
			return result;
		}

		Matcher matcher = URL_PATTERN.matcher(s);
		boolean find = matcher.find();
		StringBuffer sb = new StringBuffer();
		while (find) {
			result.add(matcher.group());
			find = matcher.find();
		}
		matcher.appendTail(sb);

		return result;
	}

	public static final Pattern URL_PATTERN = Pattern.compile("((?:https|http)://)" + "(?:[0-9a-z_!~*'()-]+\\.)*" // 域名-
																													// www.
			+ "(?:[0-9a-z][0-9a-z!~*#&'.^:@+$%-]{0,61})?[0-9a-z]\\." // 二级域名
																		// ,可以含有符号
			+ "[a-z]{0,6}" // .com
			+ "(?::[0-9]{1,4})?" // 端口
			+ "(?:/[0-9A-Za-z_!~*'().?:@&=+,$%#-]*)*" // 除了域名外中间参数允许的字符
			+ "[0-9A-Za-z-/?~#%*&()$+=^]", Pattern.CASE_INSENSITIVE); // 指定可以作为结尾的字符

}
