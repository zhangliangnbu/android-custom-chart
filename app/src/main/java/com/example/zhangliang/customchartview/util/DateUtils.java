package com.example.zhangliang.customchartview.util;

import android.support.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

/**
 * 日期工具
 * Created by zhangliang on 16/3/24.
 */
public class DateUtils {

	public static final Locale SERVICE_DEFAULT_LOCALE   = Locale.CHINA;
	private static final ThreadLocal<Map<String, SimpleDateFormat>> DATE_FORMAT_THREAD_LOCAL = new ThreadLocal<>();

	public static final String HH_MM_SS               = "HH:mm:ss";
	public static final String MM_DD_HH_MM_SS         = "MM-dd HH:mm:ss";
	public static final String YYYY_MM_DD_HH_MM_SS    = "yyyy-MM-dd HH:mm:ss";
	public static final String YYYYMMDD               = "yyyyMMdd";
	public static final String YYYY_MM_DD             = "yyyy-MM-dd";
	public static final String MM_DD_HH_MM            = "MM-dd HH:mm";
	public static final String YYYY_MM_DD_SLASH_HH_MM = "yyyy/MM/dd HH:mm";

	/**
	 * 获取指定格式的dateFormat,dateFormat实例会被保存以复用,存储在ThreadLocal中,保证线程安全
	 * @param formatString 用于SimpleDateFormat的格式化字符串
	 * @return dateFormat实例
	 */
	@NonNull
	public static SimpleDateFormat getDateFormat(@NonNull String formatString) {
		Map<String, SimpleDateFormat> dateFormatMap = DATE_FORMAT_THREAD_LOCAL.get();
		if (dateFormatMap == null) {
			dateFormatMap = Compat.createMap();
			DATE_FORMAT_THREAD_LOCAL.set(dateFormatMap);
		}
		SimpleDateFormat format = dateFormatMap.get(formatString);
		if (format == null) {
			format = new SimpleDateFormat(formatString, SERVICE_DEFAULT_LOCALE);
			dateFormatMap.put(formatString, format);
		}
		return format;
	}

	/**
	 * 日期间隔 天数
	 */
	public static long getDistOnDay(Calendar startDate, Calendar endDate) {

		long timeLong = endDate.getTimeInMillis() - startDate.getTimeInMillis();
		timeLong = timeLong / 1000 / 60 / 60 / 24;
		return timeLong;
	}

	/**
	 * @param startMs 2002年之后的秒数或毫秒数
	 * @param endMs   2002年之后的秒数或毫秒数
	 */
	public static long getDistOnDay(Long startMs, Long endMs) {
		if (startMs == null || endMs == null) {
			return 0;
		}

		if (startMs < 1000000000000L) {
			startMs *= 1000;
		}
		if (endMs < 1000000000000L) {
			endMs *= 1000;
		}
		long timeLong = endMs - startMs;
		timeLong = timeLong / 1000 / 60 / 60 / 24;
		return timeLong;
	}


	/**
	 * 将unix时间戳(秒)转为UTC时间戳(毫秒)
	 * 此方法仅对09/09/2001 @ 1:46am (UTC)之后才正确有效
	 *
	 * @param timeStamp 时间戳
	 * @return UTC毫秒时间戳
	 */
	public static long unixAsUTC(long timeStamp) {
		if (timeStamp < 1000000000000L) {
			return timeStamp*1000;
		}
		return timeStamp;
	}


	/**
	 * 获取xx月xx日
	 */
	public static String getDateOnMD(Calendar calendar) {
		return (calendar.get(Calendar.MONTH) + 1) + "月" + calendar.get(Calendar.DAY_OF_MONTH) + "日";
	}

//	public static String getFormatDate(Calendar calender) {
//		Locale locale = getDeviceLocale();// 本地时区或语言等
//		SimpleDateFormat myFmt = new SimpleDateFormat("yyyyMMdd", locale);// 不写local 默认jvm本地
//		return myFmt.format(calender.getTime());
//	}

//	public static Locale getDeviceLocale() {
//		return getResources().getConfiguration().locale;
//	}

	public static String getFormatDate(long millisecond, SimpleDateFormat sdf) {
		return sdf.format(new Date(millisecond));
	}

	public static String milliToS(Long millisecond) {
		Date date = new Date();
		date.setTime(unixAsUTC(millisecond));
		return getDateFormat(HH_MM_SS).format(date);
	}

	public static String milliToMonthDate(Long millisecond) {
		Date date = new Date();
		date.setTime(unixAsUTC(millisecond));
		return getDateFormat(MM_DD_HH_MM_SS).format(date);
	}

	public static String milliToYearMonthDate(Long millisecond) {
		Date date = new Date();
		date.setTime(unixAsUTC(millisecond));
		return getDateFormat(YYYY_MM_DD_HH_MM_SS).format(date);
	}

	public static String milliToYearMonthDateOther(Long millisecond) {
		Date date = new Date();
		if (millisecond < 1000000000000L) {
			millisecond *= 1000;
		}
		date.setTime(millisecond);
		return getDateFormat(YYYY_MM_DD).format(date);
	}

	public static long milliToYearMonthDateCompare(long millisecond, long dest_ms) {
		return unixAsUTC(millisecond) - unixAsUTC(dest_ms);
	}
}
