package com.example.zhangliang.customchartview.util;

import android.content.Context;
import android.graphics.RectF;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 组合的手势检测器，支持1个listener接受手势回调，必须设置其触摸范围#setTouchRegion以启动事件检测
 */
public class CompositeHorizontalGestureDetector
		implements ScaleGestureDetector.OnScaleGestureListener, GestureDetector.OnGestureListener,
		           LongPressDetector.LongPressListener {

	/**
	 * 惯性滑动速度参数:手势速度(屏/秒)乘以此参数为滑动速度
	 */
	public static final float FLING_SPEED_PARAM = 0.1f;
	/**
	 * 惯性滑动最大手势速度:10屏/秒
	 */
	public static final float FLING_MAX_SPEED = 10f;
	/**
	 * 惯性滑动终止手势速度:0.5屏/秒
	 */
	public static final float FLING_END_SPEED = 1f;
	/**
	 * 惯性滑动的阻尼系数
	 */
	public static final float FLING_DAMP_PARAM = 3f;
	/**
	 * 帧延迟
	 */
	public static final long FRAME_DELAY_MILLIS = 16;
	public static final int DEFAULT_LONGPRESS_TIMEOUT = 300;
	public static final String TAG = "Detector";
	private final Handler handler;
	private final HorizontalGestureListener listener;
	private final RectF touchRegion = new RectF();
	long lastFlingTime = 0;
	private float displayFrom, displayTo;
	private float minimumScaleSpan = 0.2f;
	private float indic;
	/**
	 * 当前滑动状态
	 */
	@CompositeHorizontalGestureDetector.ScrollState
	private int scrollState = ScrollState.SCROLL_STATE_UNTOUCH;
	private boolean isEnableTouchScale;
	private boolean isEnableTouchPan;
	private boolean isEnableTouchFling;
	private boolean isEnableTouchIndicator;
	private ScaleGestureDetector mScaleGestureDetector;
	private GestureDetector mGestureDetector;
	private LongPressDetector mLongPressDetector;

	public CompositeHorizontalGestureDetector(Context context, @NonNull HorizontalGestureListener listener) {
		this(context, listener, new Handler());
	}

	public CompositeHorizontalGestureDetector(Context context, @NonNull HorizontalGestureListener listener,
	                                          Handler handler) {
		this.handler = new Handler(handler.getLooper());
		this.listener = listener;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			this.mScaleGestureDetector = new ScaleGestureDetector(context, this, handler);
		} else {
			this.mScaleGestureDetector = new ScaleGestureDetector(context, this);
		}
		this.mGestureDetector = new GestureDetector(context, this, handler);
		this.mLongPressDetector = new LongPressDetector(context, this, handler, DEFAULT_LONGPRESS_TIMEOUT);
	}

	public RectF getTouchRegion() {
		return new RectF(touchRegion);
	}

	public void setTouchRegion(RectF touchRegion) {
		this.touchRegion.set(touchRegion);
	}

	public void setTouchRegion(float left, float top, float right, float bottom) {
		this.touchRegion.set(left, top, right, bottom);
	}

	public float getMinimumScaleSpan() {
		return minimumScaleSpan;
	}

	public void setMinimumScaleSpan(float minimumScaleSpan) {
		this.minimumScaleSpan = minimumScaleSpan;
	}

	public boolean onTouchEvent(MotionEvent event) {
		boolean inRegion = true;
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if (scrollState == ScrollState.SCROLL_STATE_FLING) {
				setScrollState(ScrollState.SCROLL_STATE_UNTOUCH);
			}
			break;
		case MotionEvent.ACTION_MOVE:
			// 显示全部数据时(不可缩放)直接移动就进入显示指示器模式
			if (event.getPointerCount() == 1 && displayTo - displayFrom == 1f) {
				setScrollState(ScrollState.SCROLL_STATE_INDICATOR);
			}
			if (scrollState == ScrollState.SCROLL_STATE_INDICATOR) {
				locateIndicator(event);
			}
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			touchStopped();
			break;
		default:
			break;
		}
		for (int i = 0; i < event.getPointerCount(); i++) {
			int x2 = (int) event.getX(i);
			int y2 = (int) event.getY(i);

			if (!touchRegion.contains(x2, y2)) {
				inRegion = false;
				break;
			}
		}
		if (inRegion) {
			mLongPressDetector.onTouchEvent(event);
			inRegion = mScaleGestureDetector.onTouchEvent(event);
			inRegion = mGestureDetector.onTouchEvent(event) || inRegion;
		}
		return inRegion;
	}

	/**
	 * 定位当前触摸位置以绘制
	 *
	 * @param event 最近的触摸事件
	 * @Author lulingzhi
	 */
	private void locateIndicator(MotionEvent event) {
		if (isEnableTouchIndicator()) {
			indic = event.getX() / touchRegion.width();
			if (indic > 1f) {
				indic = 1f;
			}
			if (indic < 0f) {
				indic = 0f;
			}
			listener.onShowIndicator(indic);
		}
	}

	private void touchStopped() {
		setScrollState(ScrollState.SCROLL_STATE_UNTOUCH);
		indic = 1f;
		listener.onTouchStopped();
	}

	public boolean isEnableTouchIndicator() {
		return isEnableTouchIndicator;
	}

	public void setIsEnableTouchIndicator(boolean isEnableTouchIndicator) {
		this.isEnableTouchIndicator = isEnableTouchIndicator;
	}

	public boolean isEnableTouchFling() {
		return isEnableTouchFling;
	}

	public void setIsEnableTouchFling(boolean isEnableTouchFling) {
		this.isEnableTouchFling = isEnableTouchFling;
	}

	public void setIsEnableTouchScale(boolean isEnableTouchScale) {
		this.isEnableTouchScale = isEnableTouchScale;
	}

	public boolean isEnableTouchPan() {
		return isEnableTouchPan;
	}

	public void setIsEnableTouchPan(boolean isEnableTouchPan) {
		this.isEnableTouchPan = isEnableTouchPan;
	}

	/**
	 * 设置当前服务对象(水平图表)的显示区间
	 * @param from 起点相对所有数据的位置
	 * @param to 终点相对所有数据的位置
	 * @return 设置成功
	 */
	public boolean setRange(float from, float to) {
		if (from <= to && from >= 0f && to <= 1f) {
			this.displayFrom = from;
			this.displayTo = to;
			listener.onRangeChanged(from, to);
			return true;
		}
		return false;
	}

	@Override
	public void onLongPresses(MotionEvent downEvent) {
		doLongPress(downEvent);
	}

	@Override
	public void onTapUp() {

	}

	private void doLongPress(MotionEvent e) {
		if (getScrollState() == ScrollState.SCROLL_STATE_UNTOUCH) {
			setScrollState(ScrollState.SCROLL_STATE_INDICATOR);
			locateIndicator(e);
		}
	}

	@ScrollState
	public int getScrollState() {
		return scrollState;
	}

	private void setScrollState(@ScrollState int scrollState) {
		this.scrollState = scrollState;
		// Print.d(VIEW_LOG_TAG, scrollState);
	}

	@Override
	public boolean onDown(MotionEvent e) {
		return true;
	}

	@Override
	public void onShowPress(MotionEvent e) {
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return listener.onTapUp();
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		if (!isEnableTouchPan || e2.getPointerCount() > 1) {
			return false;
		}
		if (getScrollState() == ScrollState.SCROLL_STATE_UNTOUCH) {
			setScrollState(ScrollState.SCROLL_STATE_SCROLLING);
		}
		if (getScrollState() == ScrollState.SCROLL_STATE_SCROLLING) {
			// 位移矢量,左移为正,右移为负
			float offset = distanceX;
			return doScroll(offset);
		}
		return false;
	}

	private boolean doScroll(float offset) {
		offset = offset / touchRegion.width() * (displayTo - displayFrom);
		if (offset < 0 && displayFrom == 0f) {
			// left bound
			listener.onReachLeftBound();
			return false;
		}
		if (offset > 0 && displayTo == 1f) {
			// right bound
			listener.onReachRightBound();
			return false;
		}
		displayTo += offset;
		displayFrom += offset;
		if (displayTo > 1f) {
			displayFrom += (1f - displayTo);
			displayTo = 1f;
		}
		if (displayFrom < 0f) {
			displayTo -= displayFrom;
			displayFrom = 0f;
		}
		listener.onRangeChanged(displayFrom, displayTo);
		return true;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		//		doLongPress(e);
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

		if (getScrollState() != ScrollState.SCROLL_STATE_INDICATOR && isEnableTouchFling()) {
			setScrollState(ScrollState.SCROLL_STATE_FLING);
			float relativeVelocityX = -velocityX;
			float v = touchRegion.width() * FLING_MAX_SPEED;
			relativeVelocityX = Math.abs(relativeVelocityX) > v ? Math.signum(
					relativeVelocityX) * v : relativeVelocityX;
			doFling(relativeVelocityX);
			return true;
		}
		return false;
	}

	/**
	 * 处理惯性滑行
	 *
	 * @param relativeVelocityX 水平速度
	 */
	private void doFling(final float relativeVelocityX) {
		float offsetTemp;
		if (lastFlingTime == 0) {
			offsetTemp = 0.016f * relativeVelocityX;
		} else {
			long uptimeMillis = SystemClock.uptimeMillis();
			offsetTemp = (uptimeMillis - lastFlingTime) / 1000f * relativeVelocityX;
			lastFlingTime = uptimeMillis;
		}
		//		final float offset = 0.016f * relativeVelocityX;
		final float offset = offsetTemp;
		if (Math.abs(relativeVelocityX) < FLING_END_SPEED) {
			setScrollState(ScrollState.SCROLL_STATE_UNTOUCH);
			lastFlingTime = 0;
			return;
		}
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				if (getScrollState() == ScrollState.SCROLL_STATE_FLING) {
					if (doScroll(offset)) {
						doFling(relativeVelocityX - FLING_DAMP_PARAM * offset);
					} else {
						setScrollState(ScrollState.SCROLL_STATE_UNTOUCH);
						lastFlingTime = 0;
					}
				}
			}
		}, FRAME_DELAY_MILLIS);
	}

	@Override
	public boolean onScale(ScaleGestureDetector detector) {
		if (!isEnableTouchScale()) {
			return false;
		}
		// 手指分开时,scale大于1,反之小于1
		float dspan = displayTo - displayFrom;
		float scaleX = 0;
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
			scaleX = (detector.getCurrentSpanX() / detector.getPreviousSpanX());
		} else {
			scaleX = detector.getCurrentSpan() / detector.getPreviousSpan();
		}
		float focusX = detector.getFocusX() / touchRegion.width() * dspan + displayFrom;
		// 显示条目数的最大值为数据集大小
		if (dspan / scaleX > 0.95f) {
			if (dspan == 1f) {
				// 显示条目数到最大(数据集大小)之后不可继续放大
				return false;
			}
			displayTo = 1f;
			displayFrom = 0f;
			listener.onRangeChanged(displayFrom, displayTo);
			return true;
		}
		// 显示条目数的最大值为数据集大小的1/5
		if (dspan / scaleX < minimumScaleSpan) {
			scaleX = dspan / minimumScaleSpan;
		}
		displayFrom = focusX - (focusX - displayFrom) / scaleX;
		displayTo = focusX - (focusX - displayTo) / scaleX;
		// 显示范围为[0,1]
		if (displayFrom < 0f) {
			displayTo = displayTo - displayFrom;
			displayFrom = 0f;
		} else if (displayTo > 1f) {
			displayFrom = displayFrom + 1f - displayTo;
			displayTo = 1f;
		}
		listener.onRangeChanged(displayFrom, displayTo);
		//		Print.v(TAG, String.format("dspan = %f\tfrom = %f\tto = %f\tscale = %f\tresult = %f", dspan, displayFrom,
		//				displayTo, scaleX, (displayTo - displayFrom) / dspan * scaleX));
		return true;
	}

	public boolean isEnableTouchScale() {
		return isEnableTouchScale;
	}

	@Override
	public boolean onScaleBegin(ScaleGestureDetector detector) {
		return true;
	}
	
	@Override
	public void onScaleEnd(ScaleGestureDetector detector) {
	}
	
	public long getLongpressTimeout() {
		return mLongPressDetector.getLongpressTimeout();
	}

	public void setLongpressTimeout(long longPressTimeout) {
		mLongPressDetector.setLongpressTimeout(longPressTimeout);
	}

	@IntDef({ScrollState.SCROLL_STATE_UNTOUCH, ScrollState.SCROLL_STATE_INDICATOR,
			ScrollState.SCROLL_STATE_SCROLLING, ScrollState.SCROLL_STATE_FLING})
	@Retention(RetentionPolicy.SOURCE)
	public @interface ScrollState {
		int SCROLL_STATE_UNTOUCH   = 1;
		int SCROLL_STATE_INDICATOR = 2;
		int SCROLL_STATE_SCROLLING = 3;
		int SCROLL_STATE_FLING     = 4;
	}
	
	public interface HorizontalGestureListener {

		/**
		 * 离开触摸，一般用以取消显示焦点指示器
		 */
		void onTouchStopped();

		/**
		 * 移动到了左边界
		 */
		void onReachLeftBound();

		/**
		 * 移动到了右边界
		 */
		void onReachRightBound();

		/**
		 * 显示焦点指示器
		 * @param indic 相对数据总长的焦点位置比值
		 */
		void onShowIndicator(float indic);

		/**
		 * 显示区间改变
		 * @param displayFrom 相对数据总长的起点比值
		 * @param displayTo 相对数据总长的终点比值
		 */
		void onRangeChanged(float displayFrom, float displayTo);

		/**
		 * 点按
		 */
		boolean onTapUp();
	}
}
