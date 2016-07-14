package com.example.zhangliang.customchartview.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;

/**
 * 图形操作工具类
 */
public final class Draws {
	private static float density = -1f;

	private Draws() {
	}

	public static Bitmap drawable2Bitmap(Drawable drawable) {
		int intrinsicWidth = drawable.getIntrinsicWidth();
		int intrinsicHeight = drawable.getIntrinsicHeight();
		Bitmap bitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight,
				drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, intrinsicWidth, intrinsicHeight);
		drawable.draw(canvas);
		return bitmap;
	}

	public static int reverseColor(int color) {
		int result = 0x00000000;
		result |= (color & 0xff000000);
		result |= 0x00ff0000 - (color & 0x00ff0000);
		result |= 0x0000ff00 - (color & 0x0000ff00);
		result |= 0x000000ff - (color & 0x000000ff);
		return result;
	}

	public static float getDensity(Context context) {
		if (density == -1f) {
			density = context.getResources().getDisplayMetrics().density;
		}
		return density;
	}

	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dip2px(Context context,float dpValue) {
		return (int) (dpValue * context.getResources().getDisplayMetrics().density + 0.5f);
	}

	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 */
	public static int px2dip(Context context, float pxValue) {
		return (int) (pxValue / context.getResources().getDisplayMetrics().density + 0.5f);
	}

	/**
	 * 将px值转换为sp值，保证文字大小不变
	 *
	 * @param pxValue （DisplayMetrics类中属性scaledDensity）
	 * @return
	 */
	public static int px2sp(Context context, float pxValue) {
		return (int) (pxValue / context.getResources().getDisplayMetrics().scaledDensity + 0.5f);
	}


	/**
	 * 获取状态栏高度 in dp
	 */
//	public static int getStatusBarHeight(Context context) {
//		int result = 0;
//		Resources resources = context.getResources();
//		int resourceId = resources.getIdentifier("status_bar_height", "dimen",
//				"android");
//		if (resourceId > 0) {
//			result = px2dip(resources.getDimensionPixelOffset(context,resourceId));
//		}else{
//			result = 20;
//		}
//		return result;
//	}

	/**
	 * 获取状态栏高度 in px
	 */
	public static int getStatusBarHeightInPx(Context context) {
		int result = 0;
		Resources resources = context.getResources();
		int resourceId = resources.getIdentifier("status_bar_height", "dimen",
				"android");
		if (resourceId > 0) {
			result = resources.getDimensionPixelOffset(resourceId);
		}else{
			result = dip2px(context, 20);
		}
		return result;
	}

}
