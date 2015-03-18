package com.xtrd.obdcar.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class TimeUtils {


	/**
	 * 2014-07-18 10:49:58
	 * 
	 * @param str
	 */
	public static String getDateByTime(String str) {
		if (!StringUtils.isNullOrEmpty(str)&&!"null".equals(str)) {
			try {
				SimpleDateFormat format = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");
				Date date = format.parse(str);
				format = new SimpleDateFormat(
						"yyyy-MM-dd");
				return format.format(date);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * 2014-07-18 10:49:58
	 * 
	 * @param str
	 */
	public static Date getTimeByStr(String str) {
		if (!StringUtils.isNullOrEmpty(str)&&!"null".equals(str)) {
			try {
				SimpleDateFormat format = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");
				return format.parse(str);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * 时间戳
	 * 
	 * @param str
	 */
	public static long getTimeStampByStr(String str) {
		try {
			SimpleDateFormat format = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			return format.parse(str).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * 
	 * @param date
	 * @return 10:49:58
	 */
	public static String formatDate2Hour(Date date) {
		SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
		return format.format(date);
	}

	/**
	 * 
	 * @param date
	 * @return 07月18日
	 */
	public static String formatDate2Day(Date date) {
		SimpleDateFormat format = new SimpleDateFormat("MM月dd日");
		return format.format(date);
	}

	/**
	 * 
	 * @param date
	 * @return 2014年
	 */
	public static String formatDate2Year(Date date) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy年");
		return format.format(date);
	}

	/**
	 * 获取当前日期
	 * 
	 * @return
	 */
	public static String getCurrentTime() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		return format.format(date);
	}
	/**
	 * 获取当前日期
	 * 
	 * @return
	 */
	public static String getCurrentTime(String formatStr) {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat format = new SimpleDateFormat(formatStr);
		return format.format(date);
	}

	/**
	 * 获取当前日期
	 * 时分秒
	 * @return
	 */
	public static String getCurrentTimeInHour() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
		return format.format(date);
	}

	/**
	 * 获取当前时间
	 * 
	 * @return
	 */
	public static String getCurrentDay() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat format = new SimpleDateFormat("MM-dd");
		return format.format(date);
	}

	/**
	 * 获取当前前
	 * 
	 * @param 间隔
	 * @return
	 */
	public static String getPreDay(int interval) {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DAY_OF_MONTH, -interval);
		return new SimpleDateFormat("MM-dd").format(c.getTime());
	}


	/**
	 * 获取当前日期
	 * 
	 * @return
	 */
	public static String getCurrentMonth() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
		return format.format(date);
	}

	/**
	 * 获取当前前
	 * @param end_date 
	 * 
	 * @param 间隔
	 * @return
	 */
	public static String getPreMonth(int interval, String end_date) {
		if(!StringUtils.isNullOrEmpty(end_date)) {
			try {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");//格式化为年月
				Calendar c = Calendar.getInstance();
				c.setTime(sdf.parse(end_date));
				c.add(Calendar.MONTH, -interval);
				return new SimpleDateFormat("yyyy-MM").format(c.getTime());
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}else {
			Calendar c = Calendar.getInstance();
			c.add(Calendar.MONTH, -interval);
			return new SimpleDateFormat("yyyy-MM").format(c.getTime());
		}
	
		return "";
	}
	
	public static int getDateInterval(String start_date, String end_date) {
		ArrayList<String> result = new ArrayList<String>();
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");//格式化为年月

			Calendar min = Calendar.getInstance();
			Calendar max = Calendar.getInstance();

			min.setTime(sdf.parse(start_date));
			min.set(min.get(Calendar.YEAR), min.get(Calendar.MONTH), 1);

			max.setTime(sdf.parse(end_date));
			max.set(max.get(Calendar.YEAR), max.get(Calendar.MONTH), 2);

			Calendar curr = min;
			while (curr.before(max)) {
			 result.add(sdf.format(curr.getTime()));
			 curr.add(Calendar.MONTH, 1);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return result.size();
	}

	public static int getInterval(String sdate, String edate) {
		try {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM");
			Date beginDate = simpleDateFormat.parse(sdate);
			Date endDate = simpleDateFormat.parse(edate);

			Calendar beginCalendar = Calendar.getInstance();
			Calendar endCalendar = Calendar.getInstance();
			beginCalendar.setTime(beginDate);
			endCalendar.setTime(endDate);
			return getByField(beginCalendar, endCalendar, Calendar.YEAR) * 12
					+ getByField(beginCalendar, endCalendar, Calendar.MONTH);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return 0;
	}

	private static int getByField(Calendar beginCalendar, Calendar endCalendar,
			int calendarField) {
		return endCalendar.get(calendarField)
				- beginCalendar.get(calendarField);
	}

	/**
	 * 
	 * @param duration
	 * @return
	 */
	public static String getTime(long duration) {
		if (duration <= 60) {
			return duration + "秒";
		} else if (duration > 60 && duration <= 60 * 60) {
			return duration / 60 + "分钟";
		} else if (duration > 60 * 60 && duration <= 24 * 60 * 60) {
			return duration / (60 * 60) + "小时";
		} else if (duration > 24 * 60 * 60) {
			return duration / (24 * 60 * 60) + "天";
		}
		return "未知";
	}

	/**
	 * 
	 * @param duration
	 * @return
	 */
	public static int getTimeLength(long duration) {
		if (duration <= 60) {
			return (duration + "").length();
		} else if (duration > 60 && duration <= 60 * 60) {
			return (duration / 60 + "").length();
		} else if (duration > 60 * 60 && duration <= 24 * 60 * 60) {
			return (duration / (60 * 60) + "").length();
		} else if (duration > 24 * 60 * 60) {
			return (duration / (24 * 60 * 60) + "").length();
		}
		return 0;
	}

	public static String autoFormat(long duration) {
		if (duration <= 60) {
			return duration + "秒";
		} else if (duration > 60 && duration <= 60 * 60) {
			return duration / 60 + "分钟" + duration % 60 + "秒";
		} else if (duration > 60 * 60 && duration <= 24 * 60 * 60) {
			return duration / (60 * 60) + "小时" + (duration % (60 * 60)) / 60
					+ "分" + duration % 60 + "秒";
		} else if (duration > 24 * 60 * 60) {
			return duration / (24 * 60 * 60) + "天"
					+ (duration % (24 * 60 * 60)) / (60 * 60) + "时"
					+ (duration % (60 * 60)) / 60 + "分" + duration % 60 + "秒";
		}
		return "";
	}

	/**
	 * yyyy-mm
	 * @param start
	 * @param end
	 * @param formatStr
	 * @return
	 */
	public static boolean compare(String start, String end,String formatStr) {
		try {
			SimpleDateFormat format = new SimpleDateFormat(formatStr);
			Date start_date = format.parse(start);
			Date end_date = format.parse(end);
			return start_date.compareTo(end_date) < 0;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 
	 * @param start_date yyyy-mm
	 * @return yyyy-mm-dd
	 */
	public static String getFirstDatebyMonth(String str) {
		try {
			if (!StringUtils.isNullOrEmpty(str)) {
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
				Date parse = format.parse(str);
				// 获取Calendar
				Calendar calendar = Calendar.getInstance();
				// 设置时间,当前时间不用设置
				calendar.setTime(parse);
				// 设置日期为本月最大日期
				calendar.set(Calendar.DATE,
						calendar.getActualMinimum(Calendar.DATE));

				// 打印
				format = new SimpleDateFormat("yyyy-MM-dd");
				return format.format(calendar.getTime());
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 
	 * @param start_date yyyy-mm
	 * @return yyyy-mm-dd
	 */
	public static String getLastDatebyMonth(String str) {
		try {
			if (!StringUtils.isNullOrEmpty(str)) {
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
				Date parse = format.parse(str);
				// 获取Calendar
				Calendar calendar = Calendar.getInstance();
				// 设置时间,当前时间不用设置
				calendar.setTime(parse);
				// 设置日期为本月最大日期
				calendar.set(Calendar.DATE,
						calendar.getActualMaximum(Calendar.DATE));

				// 打印
				format = new SimpleDateFormat("yyyy-MM-dd");
				return format.format(calendar.getTime());
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static boolean isSameYear(String start_date, String end_date) {
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy");

			Calendar min = Calendar.getInstance();
			Calendar max = Calendar.getInstance();

			min.setTime(format.parse(start_date));
			max.setTime(format.parse(end_date));
			if( min.get(Calendar.YEAR)==max.get(Calendar.YEAR)) {
				return true;
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static int getIntervalYear(String sdate, String edate) {
		try {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy");
			Date beginDate = simpleDateFormat.parse(sdate);
			Date endDate = simpleDateFormat.parse(edate);

			Calendar beginCalendar = Calendar.getInstance();
			Calendar endCalendar = Calendar.getInstance();
			beginCalendar.setTime(beginDate);
			endCalendar.setTime(endDate);
			return getByField(beginCalendar, endCalendar, Calendar.YEAR) ;
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return 0;
	}

	/**
	 * 获取当前前
	 * 
	 * @param 间隔
	 * @return
	 */
	public static String getPreYear(int interval) {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.YEAR, -interval);
		return new SimpleDateFormat("yyyy").format(c.getTime());
	}

}
