package com.example.zhangliang.customchartview.util;

import android.util.Log;


public class Print {
	/**
	 * log开关,发布应关闭
	 */
//	private static final boolean isTest = BuildConfig.DEBUG;
	private static final boolean isTest = true;
	public static void e(String tag, String msg) {
		if (isTest) Log.e(tag, msg);
	}

	public static void e(String tag, String msg, Throwable tr) {
		if (isTest) Log.e(tag, msg, tr);
	}

	public static void d(String tag, String msg) {
		if (isTest)  Log.d(tag, msg);
	}
	
	public static void d(String tag, Object... msgs){
		if(isTest){
			if(msgs == null || msgs.length ==0){
				Log.d(tag, "");
				return;
			}
			Log.d(tag, String.format(genFormatter(msgs), msgs));
		}
	}

	private static String genFormatter(Object... msgs) {
		StringBuilder sb = new StringBuilder();
		for (Object object : msgs) {
			if(object == null){
				sb.append("null,");
				continue;
			}
			switch (object.getClass().getCanonicalName()) {
			case "java.lang.Integer":
			case "java.lang.Long":
			case "java.lang.Short":
				sb.append("%d,");
				break;
			case "java.lang.Byte":
				sb.append("%#x,");
				break;
			case "java.lang.Float":
			case "java.lang.Double":
				sb.append("%f,");
				break;
			case "java.lang.Character":
				sb.append("%c,");
				break;
			case "java.lang.Boolean":
				sb.append("%b,");
				break;
			case "java.lang.String":
			default:
				sb.append("%s,");
				break;
			}
		}
		return sb.substring(0, sb.length()-1);
	}

	public static void w(String tag, String msg) {
		if (isTest)  Log.w(tag, msg);
	}
	public static void w(String tag, String msg, Throwable tr) {
		if (isTest)  Log.w(tag, msg, tr);
	}
	public static void v(String tag, String msg) {
		if (isTest)  Log.v(tag, msg);
	}

	public static void i(String tag, String msg) {
		if (isTest)  {
//			Log.i(tag, msg);
			iOfLong(tag, msg);
		}
	}
	
	/**
	 * 方法描述 : logcat中分条显示超过4000字符的信息
	 */
	public static void iOfLong(String tag, String msg) {
		// 如果msg长度超过4000字符，就分条打印
		while(msg.length() > maxLength) {
			Log.i(tag, msg.substring(0, maxLength));// 截取0 - maxlength-1
			msg = msg.substring(maxLength);
		}
		Log.i(tag, msg);
	}
	private static final int maxLength = 4000;
}
