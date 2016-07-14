package com.example.zhangliang.customchartview.util;

import android.os.Build;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.view.View;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class Compat {
	private Compat(){}

	public static<K,V> Map<K,V> createMap(){
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			return new ArrayMap<>();
		}else{
			return new HashMap<>();
		}
	}
	public static<E,K,V> Map<K,V> convert(List<E> list , LMConverter<E,K,V> converter){
		Map<K,V> map = Compat.createMap();
		for (E e : list) {
			map.put(converter.getKey(e),converter.getValue(e));
		}
		return map;
	}
	public interface LMConverter<E, K, V> {
		K getKey(E item);
		V getValue(E item);
	}
	public static void postInvalidateOnAnimation(View view){
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			view.postInvalidateOnAnimation();
		}else{
			view.postInvalidateDelayed(16);
		}
	}

	public static <T> Set<T> createSet() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			return new ArraySet<>();
		}else{
			return new HashSet<>();
		}
	}
}
