package com.example.zhangliang.customchartview.chart;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Build;
import android.support.annotation.FloatRange;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.example.zhangliang.customchartview.util.CollectionUtil;
import com.example.zhangliang.customchartview.util.CompositeHorizontalGestureDetector;
import com.example.zhangliang.customchartview.util.Draws;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * 基础折线图
 *
 * ---基本说明---
 * 1 尽量减轻onDraw()的压力，在onDraw()之前尽量初始化所有数据
 * 2 第一次draw 顺序 init-setData-onSizeChanged-onDraw
 * 之后是  draw 顺序 (init-onSizeChanged)-setData-onDraw
 * 所以data和size的结合处理 应当在onDraw中进行
 *
 * ---绘图区域---
 * mChartRect是view去掉padding之后的区域，用于绘图
 * axis text 绘制在padding中，所以需要设置padding
 *
 * -----version 1.0----
 * axis 四个坐标轴
 * grid 水平、竖直、零点
 * polyline include shadow
 * gesture 点击 平移 惯性平移 水平缩放
 * callback 选中点的回调
 *
 * Created by zhangliang on 16/6/13.
 */
public class PolylineChart extends View implements CompositeHorizontalGestureDetector.HorizontalGestureListener {

	private PolylineData mData = new PolylineData(new ArrayList<ChartPoint>());

	// paint and path
	private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);// 一般paint
	private Paint mShadowPaint = new Paint();// 填充
	private TextPaint mAxisTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);// text paint
	private Paint mGridPaint = new Paint(Paint.ANTI_ALIAS_FLAG);// 一般会是虚线
	private Path mPath = new Path();// 一般path

	// 位置 纯图区域（去掉padding）
	private RectF mChartRect = new RectF();
	// 坐标轴文本与纯图区域间距 可从属性值传入
	private RectF mAxisPadding = new RectF(dip2px(3), dip2px(5), dip2px(5), dip2px(3));

	private float mChartPartWidth;// 每个点占据的空间
	private float mFirstPointL;// 第一个点的x px

	//--------手势相关-----------
	private boolean isTouching = false;// 是否触摸事件
	private CompositeHorizontalGestureDetector mCompositeHorizontalGestureDetector;

	// ------回调-------
	// 选中的point 注意：选中状态包括回调选中和触摸选中两种 回调选中开始就有一个，之后二者保持一致
	private ChartPoint mSelectedPoint;

	//--------零点-----------
	private ChartText zeroText = new ChartText();// 零点文本

	public PolylineChart(Context context) {
		super(context);
		init(context, null, 0, 0);
	}

	public PolylineChart(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs, 0, 0);
	}

	public PolylineChart(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context, attrs, defStyleAttr, 0);
	}

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	public PolylineChart(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		init(context, attrs, defStyleAttr, defStyleRes);
	}

	protected void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		// 绘制虚线
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			this.setLayerType(LAYER_TYPE_SOFTWARE, null);
		}

		// 初始化图形属性 attributes
//		final TypedArray typedArray = getContext().obtainStyledAttributes(attrs,
//				R.styleable.PolylineChart, defStyleAttr, 0);
//		mChartColor = typedArray.getColor(R.styleable.PolylineChart_color_polyline, 0xFFA4CDFA);
//		mShadowBottomColor = typedArray.getColor(R.styleable.PolylineChart_color_polyline_shadow_bottom, 0x00000000);
//		mShadowColor = typedArray.getColor(R.styleable.PolylineChart_color_polyline_shadow, 0xFFA4CDFA);
//		typedArray.recycle();

		// init paint
		mPaint.setStyle(Paint.Style.FILL);
		mPaint.setAntiAlias(true);
		mPaint.setColor(mData.getColor());

		mShadowPaint.setStyle(Paint.Style.FILL);
		mShadowPaint.setAntiAlias(true);

		mGridPaint.setStrokeWidth(1f);
		mGridPaint.setStyle(Paint.Style.STROKE);
		DashPathEffect dashPathEffect = new DashPathEffect(new float[]{5, 5}, 1);
		mGridPaint.setPathEffect(dashPathEffect);

		/** ------手势-----*/
		mCompositeHorizontalGestureDetector = new CompositeHorizontalGestureDetector(getContext(), this);

		/**------data-------*/
		setDataAndRender(mData);
	}

	// 可初试化与data相关的数据
	public void setDataAndRender(PolylineData data) {
		this.mData = data;
		initData(data);

		invalidate();
	}

	private void initData(PolylineData data) {

		if (data == null || data.getDataCount() == 0) {
			return;
		}

		// display range
		resetDisplayRange();
		measureRange();

		// ----------max and min for display-----------
		data.setMaxY(data.getListMax());
		data.setMinY(data.getListMin());

		float fluctMaxY, fluctMinY;
		// 所有小于0 所有大于0  1
		if (data.getMaxY() == data.getMinY()) {
			fluctMaxY = data.getMaxY() + 1 * data.getHighFluctuationRate();
			fluctMinY = data.getMinY() - 1 * data.getLowFluctuationRate();
		} else {
			fluctMaxY = data.getMaxY() + (data.getMaxY() - data.getMinY()) * data.getHighFluctuationRate();
			fluctMinY = data.getMinY() - (data.getMaxY() - data.getMinY()) * data.getLowFluctuationRate();
		}
		// 整数的情况 include grid count
		int mGridLineNumHor = data.getGridHorLineList().size();
		if (data.getAxisYRightTextScale() <= 0) {
			int min = (int) Math.floor(fluctMinY);
			int max = (int) Math.ceil(fluctMaxY);
			for (; ; ) {
				if ((max - min) % (mGridLineNumHor - 1) == 0) {
					break;
				}
				max++;
			}
			fluctMaxY = max;
			fluctMinY = min;
		}
		data.setFluctuateMaxY(fluctMaxY < 0 ? 0 : fluctMaxY);
		data.setFluctuateMinY(fluctMinY > 0 ? 0 : fluctMinY);

		// ----------------gesture------------
		setIsEnableTouchIndicator(data.isEnableTouchIndicator());
		setIsEnableTouchFling(data.isEnableTouchFling());
		setIsEnableTouchPan(data.isEnableTouchPan());
		setIsEnableTouchScale(data.isEnableTouchScale());

		// ----------------回调------------ last displayed point
		mSelectedPoint = data.getPointList().get(data.getToIndex() - 1);
		if (mOnTouchChartListener != null) {
			mOnTouchChartListener.onCallback(mSelectedPoint);
		}

		// -----------axis-------------
		mAxisTextPaint.setColor(data.getAxisTextColor());
		mAxisTextPaint.setTextSize(Draws.dip2px(getContext(), data.getAxisTextSizeInSp()));
		mData.setAxisTextHight(Math.abs(mAxisTextPaint.ascent() + mAxisTextPaint.descent()));

		// ------------grid--------------
		mGridPaint.setColor(data.getGridColor());
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//		Log.d("onMeasure w-h aft", widthMeasureSpec + "-" + heightMeasureSpec);
	}

	// 可初试化与data无关的数据
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		// 设置与mData无关的布局尺寸 mPadChart mChart mGrid
	}

	/**
	 * reset display range
	 */
	private void resetDisplayRange() {
		if (!CollectionUtil.isEmpty(mData.getPointList())) {
			int count = mData.getDataCount();
			if (count > mData.getInitialDisplayCount()) {
				displayTo = 1f;
				displayFrom = 1f - ((float) mData.getInitialDisplayCount()) / count;
			}
			mCompositeHorizontalGestureDetector.setMinimumScaleSpan(
					(float) mData.getMinimumDisplayCount() / count);
		} else {
			displayFrom = 0f;
			displayTo = 1f;
		}

		if (mData.isEnableShowAll()) {
			displayFrom = 0f;
			displayTo = 1f;
		}
		mCompositeHorizontalGestureDetector.setRange(displayFrom, displayTo);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
//		Log.d("onDraw w-h", getWidth() + "-" + getHeight());

		if (mData == null || mData.isEmpty()) {
			return;
		}

		// range gesture
		measureRange();

		// init draw data
		setDrawData();

		// grid
		mGridPaint.setColor(mData.getGridColor());
		drawLine(canvas, mData.getGridHorLineList(), mGridPaint);// hor

		// zero line
		int mGridZeroColor = 0xFF000000;
		mGridPaint.setColor(mGridZeroColor);
		drawZeroLine(canvas, mData, mGridPaint);

		// zero text
		mAxisTextPaint.setColor(mData.getAxisTextColor());
		canvas.drawText(zeroText.getText(), zeroText.getL(), zeroText.getB(), mAxisTextPaint);

		// axis text
		mAxisTextPaint.setColor(mData.getAxisTextColor());
		drawText(canvas, mData.getAxisRightTextList(), mAxisTextPaint);// y right
		drawText(canvas, mData.getAxisBottomTextList(), mAxisTextPaint);// x bottom

		// selected point
		drawSelectedPoint(canvas, mData.getPointList(), mPaint, mSelectedPoint);

		// chart
		drawPolyline(canvas, mData, mPath, mPaint);

		// shadow
		drawShadow(canvas, mData, mPath, mShadowPaint);

	}

	// 设置绘制需要的data 但又不能在之前的方法中设置的data. mData和尺寸结合的计算
	private void setDrawData() {
		// common data
		setCommonData(mData);

		setPolylineData(mData);

		// ------axis------
		setAxisRightData(mData);
		setAxisBottomData(mData);

		// zero text
		setZeroTextData(mData);

		// -------grid------
		setGridHorLineData(mData);
		setGridVerLineData(mData);
	}

	private void setGridHorLineData(ChartData data) {

		List<ChartLine> list = data.getGridHorLineList();
		float horLineGap = mChartRect.height() / (list.size() - 1);
		for (int i = 0, len = list.size(); i < len; i++) {
			list.get(i).setLinePositioin(
					mChartRect.left,
					mChartRect.top + horLineGap * i,
					mChartRect.right,
					mChartRect.top + horLineGap * i);
		}
	}

	private void setGridVerLineData(ChartData data) {

		List<ChartLine> list = data.getGridVerLineList();
		float horLineGap = mChartRect.width() / (list.size() - 1);
		for (int i = 0, len = list.size(); i < len; i++) {
			list.get(i).setLinePositioin(
					mChartRect.left + horLineGap * i,
					mChartRect.top,
					mChartRect.left + horLineGap * i,
					mChartRect.bottom);
		}
	}

	private void setCommonData(ChartData data) {
		// mChart
		float mChartLeft = getPaddingLeft();
		float mChartTop = getPaddingTop();
		float mChartRight = getWidth() - getPaddingRight();
		float mChartBottom = getHeight() - getPaddingBottom();
		mChartRect.set(mChartLeft, mChartTop, mChartRight, mChartBottom);

		mCompositeHorizontalGestureDetector.setTouchRegion(mChartLeft, mChartTop, mChartRight, mChartBottom);

		if (data.getToIndex() - data.getFromIndex() > 0) {
			mChartPartWidth = mChartRect.width() / (data.getToIndex() - data.getFromIndex());
		}
		mFirstPointL = mChartRect.left + mChartPartWidth / 2;
	}

	private void setPolylineData(ChartData data) {
		if (data == null || data.getPointList() == null || data.getPointList().isEmpty()) {
			return;
		}

		List<? extends ChartPoint> list = data.getPointList();
		ChartPoint point;

		float x, y;
		for (int i = data.getFromIndex(), len = data.getToIndex(); i < len; i++) {
			point = list.get(i);
			x = mFirstPointL + (i - data.getFromIndex()) * mChartPartWidth;
			y = transY(point.getYFloat(), data.getFluctuateMaxY(), data.getFluctuateMinY(), mChartRect.bottom, mChartRect.top);
			point.setXY(x, y);
		}
	}

	private void setAxisRightData(ChartData data) {
		List<ChartText> mAxisRightTextList = data.getAxisRightTextList();
		float yGap = (data.getFluctuateMaxY() - data.getFluctuateMinY()) / (mAxisRightTextList.size() - 1);

		String str;
		float l, b, val;
		ChartText chartText;
		for (int i = 0, len = mAxisRightTextList.size(); i < len; i++) {
			chartText = mAxisRightTextList.get(i);

			val = i == len - 1 ? data.getFluctuateMinY() : data.getFluctuateMaxY() - i * yGap;// 防止-0.00出现
			str = format(data.getAxisYRightTextScale(), val) + data.getAxisYRightTextSuffix();
			l = mChartRect.right + mAxisPadding.right;
			b = data.getAxisTextHight() / 2 + transY(val, data.getFluctuateMaxY(), data.getFluctuateMinY(), mChartRect.bottom, mChartRect.top);

			chartText.setProps(str, l, b);
		}
	}

	// 与 chart 相关
	private void setAxisBottomData(ChartData data) {
		String str;
		float l, b;
		ChartText chartText;
		ChartPoint point;

//		for (int i = 0, j = 0, len = data.getPointList().size(), len2 = mAxisBottomTextList.size();
//		     i < len && j < len2; i = (len - 1) / (len2 - 1) + i, j++) {
//			// len = (len2 - 1) * n + 1 --- remainder
//			point = data.getPointList().get(i);
//			chartText = mAxisBottomTextList.get(j);
//
//			str = point.getValX();
////			Log.d("setAxisBottomData - str", str + "-");
//			float textWidth = mAxisTextPaint.measureText(str);
//			l = point.getX() - textWidth / 2;
//			b = mChartRect.bottom + mAxisPadding.getBottom() + mAxisTextHight;
//
//			chartText.setProps(str, l, b);
//		}
		List<ChartText> mAxisBottomTextList = data.getAxisBottomTextList();
		for (int i = 0, len = mAxisBottomTextList.size(); i < len; i++) {

			point = i == 0 ? data.getFromIndexPoint() : data.getToIndexPoint();
			chartText = mAxisBottomTextList.get(i);

			str = point.getValXFormatTime(data.getAxisXBottomTextTimeFormat());
//			Log.d("setAxisBottomData - str", str + "-");
			float textWidth = mAxisTextPaint.measureText(str);
			l = point.getX() - textWidth / 2;
			b = mChartRect.bottom + mAxisPadding.bottom + data.getAxisTextHight();

			chartText.setProps(str, l, b);
		}


	}

	private void setZeroTextData(ChartData data) {
		String str = format(data.getAxisYRightTextScale(), 0f);
		float l = mChartRect.right + mAxisPadding.right;
		float b = data.getAxisTextHight() / 2 + transY(0f, data.getFluctuateMaxY(), data.getFluctuateMinY(), mChartRect.bottom, mChartRect.top);
		zeroText.setProps(str, l, b);
	}


	private void drawLine(@NonNull Canvas canvas, @NonNull List<ChartLine> list, @NonNull Paint paint) {
		ChartLine chartLine;
		for (int i = 0, len = list.size(); i < len; i++) {
			chartLine = list.get(i);
			canvas.drawLine(chartLine.getStartX(), chartLine.getStartY(), chartLine.getStopX(), chartLine.getStopY(), paint);
		}
	}

	private void drawText(@NonNull Canvas canvas, @NonNull List<ChartText> list, @NonNull TextPaint paint) {
		ChartText text;
		for (int i = 0, len = list.size(); i < len; i++) {
			text = list.get(i);
			canvas.drawText(text.getText(), text.getL(), text.getB(), paint);
		}
	}

	/**
	 * 手势选中的点
	 */
	private void drawSelectedPoint(@NonNull Canvas canvas, @NonNull List<? extends ChartPoint> list,
	                               @NonNull Paint paint, ChartPoint point) {

		// 回调
		if (point != null) {
			// 选中 bing 正在触摸
			if (isTouching) {
				drawCross(canvas, point, mData, mAxisTextPaint, paint);
			}
		}
	}

	/**
	 * y axis min < 0, to show zero line
	 */
	private void drawZeroLine(Canvas canvas, ChartData data, Paint paint) {

		float yMaxDisplay = data.getFluctuateMaxY();
		float yMinDisplay = data.getFluctuateMinY();
		if (yMinDisplay < 0) {
			float y0 = transY(0, yMaxDisplay, yMinDisplay, mChartRect.bottom, mChartRect.top);// 上下bar的分界点
			canvas.drawLine(mChartRect.left, y0, mChartRect.right, y0, paint);
		}
	}

	/**
	 * 画十字线、文本和矩形背景
	 */
	private void drawCross(Canvas c, ChartPoint selectedPoint, PolylineData data, TextPaint textPaint, Paint mFillPaint) {

		float crossRadius = 3;

		float textWidth, textHeight, l, t, r, b, horLineLeft, horLineRight;
		float textVerPadding = dip2px(3);
		float textHorPadding = dip2px(4);
		float rx = dip2px(3);
		float ry = dip2px(3);
		String text;

		int mCrossLineColor = 0x99999999;
		int mCrossPointColor = 0xFFFFFFFF;
		int mCrossRectColor = 0x66999999;

//		// ------左-------
//		// 文本尺寸
//		text = selectedPoint.getValY().setScale(mAxisYLeftScale, RoundingMode.DOWN).toPlainString();
//		textWidth = textPaint.measureText(text);
//		textHeight = Math.abs(textPaint.ascent() + textPaint.descent());
//
//		// 背景尺寸
//		l = mChartLeft;
//		t = y - 0.5f * textHeight - textVerPadding;
//		if (t < mChartTop) {// 边界修正
//			t = mChartTop;
//		} else if (t > mChartBottom - textHeight - 2 * textVerPadding) {
//			t = mChartBottom - textHeight - 2 * textVerPadding;
//		}
//		r = l + textWidth + 2 * textHorPadding;
//		b = t + textHeight + 2 * textVerPadding;
//
//		// 线的尺寸
//		horLineLeft = mChartLeft + textWidth + 2 * textHorPadding;
//
//		// 画背景
//		mFillPaint.setColor(mCrossRectColor);
//		c.drawRoundRect(new RectF(l, t, r, b), rx, ry, mFillPaint);
//
//		// 画文本
//		textPaint.setColor(mCrossTextColor);
//		c.drawText(text, l + textHorPadding, b - textVerPadding, textPaint);

		// ------右-------
		// 文本尺寸
		text = selectedPoint.getValY().setScale(data.getAxisYRightTextScale(), RoundingMode.DOWN).toPlainString();
		textWidth = textPaint.measureText(text);
		textHeight = Math.abs(textPaint.ascent() + textPaint.descent());

		// 背景尺寸
		r = mChartRect.right;
		l = r - textWidth - textHorPadding * 2;
		t = selectedPoint.getY() - 0.5f * textHeight - textVerPadding;
//		if (t < mChartTop) {// 边界修正
//			t = mChartTop;
//		} else if (t > mChartBottom - textHeight - 2 * textVerPadding) {
//			t = mChartBottom - textHeight - 2 * textVerPadding;
//		}
		b = t + textHeight + 2 * textVerPadding;

		// 线尺寸
		horLineRight = mChartRect.right - textWidth - 2 * textHorPadding;

		// 画背景
		mFillPaint.setColor(mCrossRectColor);
		c.drawRoundRect(new RectF(l, t, r, b), rx, ry, mFillPaint);

		// 画文本
		textPaint.setColor(selectedPoint.getValY().signum() >= 0 ? data.getCrossTextRiseColor() : data.getCrossTextFallColor());
		c.drawText(text, l + textHorPadding, b - textVerPadding, textPaint);

		// 十字线
		mFillPaint.setStyle(Paint.Style.STROKE);
		mFillPaint.setColor(mCrossLineColor);
		mFillPaint.setStrokeWidth(1);
		c.drawLine(mChartRect.left, selectedPoint.getY(), horLineRight, selectedPoint.getY(), mFillPaint);// 水平
		c.drawLine(selectedPoint.getX(), mChartRect.top, selectedPoint.getX(), mChartRect.bottom, mFillPaint);// 竖直
		mFillPaint.setStyle(Paint.Style.FILL);

		// 中心点
		mFillPaint.setColor(mCrossPointColor);
		c.drawCircle(selectedPoint.getX(), selectedPoint.getY(), crossRadius, mFillPaint);
	}

	private void drawPolyline(@NonNull Canvas canvas, PolylineData data,
	                          @NonNull Path path, @NonNull Paint paint) {
		if (data == null || CollectionUtil.isEmpty(data.getPointList())) {
			return;
		}

		List<? extends ChartPoint> list = data.getPointList();

//		Print.d("draw f-t-total", data.getFromIndex(), data.getToIndex(), list.size());

		ChartPoint point;

		point = list.get(data.getFromIndex());
		path.rewind();
		path.moveTo(point.getX(), point.getY());
		for (int i = data.getFromIndex(), len = data.getToIndex(); i < len; i++) {
			point = list.get(i);
			path.lineTo(point.getX(), point.getY());
		}

		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(dip2px(1));
		paint.setColor(data.getPolylineColor());

		canvas.drawPath(path, paint);

		paint.setStyle(Paint.Style.FILL);
	}

	private void drawShadow(Canvas canvas, PolylineData data, Path path, Paint paint) {

		paint.setShader(new LinearGradient(0, 0, 0, getHeight(), data.getShadowTopColor(),
				data.getShadowBottomColor(), Shader.TileMode.CLAMP));

		ChartPoint point = data.getPointList().get(data.getFromIndex());

		path.rewind();// 每次draw 清理path
		path.moveTo(point.getX(), point.getY());
		if (data.getDisplayPointCount() > 1) {
			for (int i = data.getFromIndex() + 1, len = data.getToIndex(); i < len; i++) {
				point = data.getPointList().get(i);
				path.lineTo(point.getX(), point.getY());
			}
			point = data.getPointList().get(data.getToIndex() - 1);
			path.lineTo(point.getX(), mChartRect.bottom);
		}
		point = data.getPointList().get(data.getFromIndex());
		path.lineTo(point.getX(), mChartRect.bottom);
		path.close();

		canvas.drawPath(path, paint);
	}


	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		if (mData != null && !CollectionUtil.isEmpty(mData.getPointList())) {
			getParent().requestDisallowInterceptTouchEvent(true);
		}
		return super.dispatchTouchEvent(event);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		event.offsetLocation(-getPaddingLeft(), 0f);// 去掉左边的padding再做计算，不然点击位置会偏移
		return mCompositeHorizontalGestureDetector.onTouchEvent(event);
	}

	/**
	 * 实际值转像素坐标
	 *
	 * @param valCurr 当前实际值
	 * @param valMax  最大实际值
	 * @param valMin  最小实际值
	 * @param pxMax   最大像素值
	 * @param pxMin   最小像素值
	 * @return 当前实际值对应的像素坐标
	 */
	private float transY(float valCurr, float valMax, float valMin, float pxMax, float pxMin) {
		return pxMin + (valMax - valCurr) * (pxMax - pxMin) / (valMax - valMin);
	}

	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public int dip2px(float dpValue) {
		return (int) (dpValue * getResources().getDisplayMetrics().density + 0.5f);
	}

	public String format(int fraction, float f) {
		if (fraction < 0) {
			return f + "";
		}
		return String.format("%." + fraction + "f", f);
	}

	public OnTouchChartListener mOnTouchChartListener;

	/**
	 * 回调的接口
	 */
	public interface OnTouchChartListener {
		/**
		 * point 包含x/y等所有信息
		 */
		void onCallback(ChartPoint point);
	}

	public void setOnTouchChartListener(OnTouchChartListener l) {
		this.mOnTouchChartListener = l;
	}

	public boolean isEnableTouchIndicator() {
		return mCompositeHorizontalGestureDetector.isEnableTouchIndicator();
	}

	public void setIsEnableTouchIndicator(boolean isEnableTouchIndicator) {
		mCompositeHorizontalGestureDetector.setIsEnableTouchIndicator(isEnableTouchIndicator);
	}

	public boolean isEnableTouchFling() {
		return mCompositeHorizontalGestureDetector.isEnableTouchFling();
	}

	public void setIsEnableTouchFling(boolean isEnableTouchFling) {
		mCompositeHorizontalGestureDetector.setIsEnableTouchFling(isEnableTouchFling);
	}

	public boolean isEnableTouchScale() {
		return mCompositeHorizontalGestureDetector.isEnableTouchScale();
	}

	public void setIsEnableTouchScale(boolean isEnableTouchScale) {
		mCompositeHorizontalGestureDetector.setIsEnableTouchScale(isEnableTouchScale);
	}

	public boolean isEnableTouchPan() {
		return mCompositeHorizontalGestureDetector.isEnableTouchPan();
	}

	public void setIsEnableTouchPan(boolean isEnableTouchPan) {
		mCompositeHorizontalGestureDetector.setIsEnableTouchPan(isEnableTouchPan);
	}

	@CompositeHorizontalGestureDetector.ScrollState
	public int getScrollState() {
		return mCompositeHorizontalGestureDetector.getScrollState();
	}

	public long getLongpressTimeout() {
		return this.mCompositeHorizontalGestureDetector.getLongpressTimeout();
	}

	public void setLongpressTimeout(@IntRange(from = 50, to = 1000) long longpressTimeout) {
		if (longpressTimeout >= 50 && longpressTimeout <= 1000) {
			this.mCompositeHorizontalGestureDetector.setLongpressTimeout(longpressTimeout);
		}
	}

	@Override
	public void onTouchStopped() {
		isTouching = false;

		mSelectedPoint = mData.getPointList().get(mData.getToIndex() - 1);
		// 回调
		if (mOnTouchChartListener != null) {
			mOnTouchChartListener.onCallback(mSelectedPoint);
		}
//		Print.d("GestureDetector==", "onTouchStopped");
		invalidate();
	}

	@Override
	public void onReachLeftBound() {
//		Print.d("GestureDetector==", "onReachLeftBound");
	}

	@Override
	public void onReachRightBound() {
//		Print.d("GestureDetector==", "onReachRightBound");
	}

	@Override
	public void onShowIndicator(float indic) {
//		Print.d("GestureDetector==", "onShowIndicator", indic);
		isTouching = true;
		this.indic = indic;

		int from = mData.getFromIndex();
		int to = mData.getToIndex();
		int indicIndex = to - from - 1;
//		Print.d("onDraw=indicIndex", indicIndex, isEnableTouchIndicator(), getScrollState());
		if (isEnableTouchIndicator() &&
				getScrollState() == CompositeHorizontalGestureDetector.ScrollState.SCROLL_STATE_INDICATOR
				&& mData.getDataCount() >= to) {
			indicIndex = (int) (indic * (to - from));
			indicIndex = indicIndex >= to - from ? to - from - 1 : indicIndex;
//			Print.d("onDraw=indicIndex", indicIndex);
			// 6.绘制焦点指示器
//			drawIndicator(canvas, indicIndex, from, to);
			mSelectedPoint = mData.getPointList().get(from + indicIndex);

			// 回调
			if (mOnTouchChartListener != null) {
				mOnTouchChartListener.onCallback(mSelectedPoint);
			}
		}

		invalidate();
	}

	@Override
	public void onRangeChanged(float displayFrom, float displayTo) {
//		Print.d("GestureDetector==", "onRangeChanged", displayFrom, displayTo);
		this.displayFrom = displayFrom;
		this.displayTo = displayTo;
		int prevFrom = mData.getFromIndex();
		int prevTo = mData.getToIndex();
		measureRange();
//		WLog.d(VIEW_LOG_TAG, prevFrom, prevTo, fromIndex, toIndex);
		if (prevFrom != mData.getFromIndex() || prevTo != mData.getToIndex()) {
			invalidate();
		}
	}

	private void measureRange() {
		displayCount = mData.getDataCount();
		int fromIndex = Math.round(displayFrom * displayCount);
		int toIndex = fromIndex + Math.round((displayTo - displayFrom) * displayCount);
		toIndex = toIndex > mData.getDataCount() ? mData.getDataCount() : toIndex;
		fromIndex = fromIndex < 0 ? 0 : fromIndex;
		mData.setFromIndex(fromIndex);
		mData.setToIndex(toIndex);
//		Print.d("measureRange dis f-t", displayFrom, displayTo);
	}

	@Override
	public boolean onTapUp() {
//		Print.d("GestureDetector==", "onTapUp");
		return false;
	}

	/**---------gesture 相关数据--------------*/
	/**
	 * 显示区域内值的范围,displayValueRange[0]为min值,displayValueRange[1]为max值
	 */
	protected final float[] displayValueRange = new float[2];
	protected float indicatorPointerSize;
	@FloatRange(from = 0f, to = 1f)
	protected float displayFrom = 0f;
	@FloatRange(from = 0f, to = 1f)
	protected float displayTo = 1f;
	@FloatRange(from = 0f, to = 1f)
	protected float prevTo = 1f;
	@IntRange(from = 0)
	protected int displayCount;
	/**
	 * 绘制的y坐标最低(y_translated = chartHeight)时对应的值
	 */
	protected float displayValueMin;
	/**
	 * 绘制的y坐标最高(y_translated = 0)时对应的值
	 */
	protected float displayValueMax;

	/**
	 * 当前单指触摸焦点X相对图表的位置[0,1]
	 */
	@FloatRange(from = 0f, to = 1f)
	protected float indic;
	//	private int toIndex;//显示数据终到索引
//	private int fromIndex;//显示数据起始索引
	@FloatRange(from = 0f, to = 1f)
	private float prevFrom = 0f;


}
