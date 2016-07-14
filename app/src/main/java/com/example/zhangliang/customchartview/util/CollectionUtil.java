package com.example.zhangliang.customchartview.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

/**
 *
 * Created by zhangliang on 16/3/25.
 */
public class CollectionUtil {
	public static boolean isEmpty(Collection<?> list) {
		return list == null || list.isEmpty();
	}
	public static boolean isEmpty(Map<?,?> map) {
		return map == null || map.isEmpty();
	}

	public static boolean isEmpty(float[] arr) {
		return arr == null || arr.length == 0;
	}

	/**
	 * @return 最近触摸点index
	 */
	private static int getNearestIndex(float[] xArr, float xc) {

		float dX;
		int index = Arrays.binarySearch(xArr, xc);// xc位置或比xc稍大的位置
		if(index < 0) {
			index = - index - 1;
		}
		if(index == 0) {
			return 0;
		} else if(index == xArr.length) {
			return xArr.length - 1;
		} else {
			dX = xArr[index] - xc - xc + xArr[index - 1];
			if (dX >= 0) {
				index --;
			}
			return index;
		}
	}

	/**
	 * 最大值
	 */
	private float getMax(float[] arr) {
		if(arr == null || arr.length <= 0) {
			return -1;
		}

		float max = arr[0];
		for (int i = 1, len = arr.length; i < len; i++) {
			max = max >= arr[i] ? max : arr[i];
		}
		return max;
	}

	/**
	 * 最小值
	 */
	private float getMin(float[] arr) {
		if(arr == null || arr.length <= 0) {
			return -1;
		}

		float min = arr[0];
		for (int i = 1, len = arr.length; i < len; i++) {
			min = min <= arr[i] ? min : arr[i];
		}
		return min;
	}
}
