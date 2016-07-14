package com.example.zhangliang.customchartview.util;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

/**
 * 长按检测器,可以传入长按的时长,若不传入,使用默认(500ms)
 */
public class LongPressDetector {

	private static final int LONGPRESS = 16;
	private static final int TAP_TIMEOUT = ViewConfiguration.getTapTimeout();

	private final LongPressListener listener;

	public long getLongpressTimeout() {
		return longpressTimeout;
	}

	public void setLongpressTimeout(long longpressTimeout) {
		this.longpressTimeout = longpressTimeout;
	}

	private long longpressTimeout;
	private final int touchSlopSquare;

	public LongPressDetector(Context context, LongPressListener listener) {
		this(context, listener,null, ViewConfiguration.getLongPressTimeout());
	}

	public LongPressDetector(Context context, LongPressListener listener, Handler handler) {
		this(context,listener,handler, ViewConfiguration.getLongPressTimeout());
	}

	public LongPressDetector(Context context, LongPressListener listener, Handler handler, long longpressTimeout) {
		this.listener = listener;
		this.longpressTimeout = longpressTimeout;
		if (context == null) {
			int touchSlop = ViewConfiguration.getTouchSlop();
			this.touchSlopSquare = touchSlop * touchSlop;
		} else {
			final ViewConfiguration configuration = ViewConfiguration.get(context);
			int touchSlop = configuration.getScaledTouchSlop();
			this.touchSlopSquare = touchSlop * touchSlop;
		}
		if(handler == null){
			this.handler = new LongPressHandler();
		}else {
			this.handler = new LongPressHandler(handler);
		}
	}

	/**
	 * 长按监听
	*/
	public interface LongPressListener {
		void onLongPresses(MotionEvent downEvent);

		void onTapUp();
	}

	float downX, downY;
	boolean out = true;

	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				out = false;
				downX = event.getX();
				downY = event.getY();
				Message msg = handler.obtainMessage();
				msg.obj = event;
				msg.what = LONGPRESS;
				handler.sendMessageAtTime(msg, event.getDownTime() + TAP_TIMEOUT + longpressTimeout);
				break;
			case MotionEvent.ACTION_MOVE:
				float dx = event.getX() - downX;
				float dy = event.getY() - downY;
				if ((dx * dx + dy * dy) > touchSlopSquare) {
					handler.removeMessages(LONGPRESS);
					out = true;
				}
				break;
			case MotionEvent.ACTION_UP:
				if(!out){
					listener.onTapUp();
				}
				handler.removeMessages(LONGPRESS);
				break;
			case MotionEvent.ACTION_CANCEL:
				handler.removeMessages(LONGPRESS);
				break;
		}
		return true;
	}

	private final Handler handler;

	private class LongPressHandler extends Handler {
		public LongPressHandler(Handler handler) {
			super(handler.getLooper());
		}
		public LongPressHandler() {
			super();
		}

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case LONGPRESS:
					if (LongPressDetector.this.listener != null) {
						LongPressDetector.this.listener.onLongPresses((MotionEvent) msg.obj);
					}
					break;
			}
		}
	}
}
