package com.example.zhangliang.customchartview.chart;

import android.graphics.RectF;
import android.support.annotation.ColorInt;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;

import com.example.zhangliang.customchartview.util.CollectionUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * chart common data
 * base point include point and bar props
 * base chart axis border color...
 * Created by zhangliang on 16/6/13.
 */
public class ChartData {
	private int color;
	private List<? extends ChartPoint> pointList;

	// ---------------max and min of y---------------
	// 实际最大和最小值
	private float maxY;
	private float minY;
	// y axis display range
	private float fluctuateMaxY;// 浮动后的最大值 用于图表显示
	private float fluctuateMinY;// 浮动后的最小值 用于图表显示
	private float highFluctuationRate = 0.2f;// y max 浮动比例 fluctuateMaxY = maxY + (maxY - minY) * highFluctuationRate
	private float lowFluctuationRate = 0.2f;// y min 浮动比例

	// touching fluctuate rate
	private RectF touchingFluctuateRate;
	private boolean isEnableTouchFling = false;
	private boolean isEnableTouchIndicator = false;
	private boolean isEnableTouchPan = false;
	private boolean isEnableTouchScale= false;
	private boolean isEnableShowAll = false; // if true ze show all point and fling = pan = scale = false;

	// ---------------axis---------------
	// y right axis text scale
	private int axisYRightTextScale = 2;// 小数点位数
	// y right axis text add suffix;
	private CharSequence axisYRightTextSuffix = "";
	// x bottom axis text time format
	private String axisXBottomTextTimeFormat = "MM.dd";

	private int axisTextColor = 0xFF666666;
	private int axisTextSizeInSp = 8;
	private float axisTextHight = 0f;
	private boolean isEnableShowLeftAxisTexts = false;
	private boolean isEnableShowRightAxisTexts = true;
	private boolean isEnableShowBottomAxisTexts = true;
	private boolean isEnableShowTopAxisTexts = false;
	private List<ChartText> axisRightTextList = new ArrayList<>();
	private List<ChartText> axisBottomTextList = new ArrayList<>();
	private List<ChartText> axisTopTextList = new ArrayList<>();
	private List<ChartText> axisLeftTextList = new ArrayList<>();


	// ---------------grid---------------
	// 水平线
	private List<ChartLine> gridHorLineList = new ArrayList<>();
	private List<ChartLine> gridVerLineList = new ArrayList<>();
	private int gridColor = 0xFF666666;
	private int gridZeroColor = 0xFF000000;// 0点线或分界线
	private boolean isEnableShowHorGrid;
	private boolean isEnableShowVerGrid;

	// color
	private int selectedPointColor = 0xFFFFFFFF;// 选中点或bar边框的颜色

	// ---------------display range---------------
	// display point count
	private int minimumDisplayCount = 5;
	private int initialDisplayCount = 10;
	// display point index
	private int fromIndex = -1;
	private int toIndex = -1;

	public ChartData(List<? extends ChartPoint> pointList, int color) {
		this.pointList = pointList;
		this.color = color;
	}
	public ChartData(List<? extends ChartPoint> pointList) {
		this.pointList = pointList;
	}
	public ChartData(){}

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}

	public List<? extends ChartPoint> getPointList() {
		return pointList;
	}

	public void setPointList(List<? extends ChartPoint> pointList) {
		this.pointList = pointList;
	}


	public float getMaxY() {
		return maxY;
	}

	public void setMaxY(float maxY) {
		this.maxY = maxY;
	}

	public float getMinY() {
		return minY;
	}

	public void setMinY(float minY) {
		this.minY = minY;
	}

	public float getFluctuateMaxY() {
		return fluctuateMaxY;
	}

	public void setFluctuateMaxY(float fluctuateMaxY) {
		this.fluctuateMaxY = fluctuateMaxY;
	}

	public float getFluctuateMinY() {
		return fluctuateMinY;
	}

	public void setFluctuateMinY(float fluctuateMinY) {
		this.fluctuateMinY = fluctuateMinY;
	}

	public boolean isEmpty() {
		return this.pointList == null || this.pointList.size() == 0;
	}

	public float getHighFluctuationRate() {
		return highFluctuationRate;
	}

	public void setHighFluctuationRate(float highFluctuationRate) {
		this.highFluctuationRate = highFluctuationRate;
	}

	public float getLowFluctuationRate() {
		return lowFluctuationRate;
	}

	public void setLowFluctuationRate(float lowFluctuationRate) {
		this.lowFluctuationRate = lowFluctuationRate;
	}

	public RectF getTouchingFluctuateRate() {
		return touchingFluctuateRate;
	}

	public void setTouchingFluctuateRate(RectF touchingFluctuateRate) {
		this.touchingFluctuateRate = touchingFluctuateRate;
	}

	public int getFromIndex() {
		return fromIndex < 0 ? 0 : fromIndex;
	}

	public void setFromIndex(int fromIndex) {
		this.fromIndex = fromIndex;
	}

	public int getToIndex() {
		if(toIndex < 0 || toIndex > getDataCount()) {
			return getDataCount();
		}

		return toIndex;
	}

	public void setToIndex(int toIndex) {
		this.toIndex = toIndex;
	}

	public int getAxisYRightTextScale() {
		return axisYRightTextScale;
	}

	public void setAxisYRightTextScale(int axisYRightTextScale) {
		this.axisYRightTextScale = axisYRightTextScale;
	}

	public CharSequence getAxisYRightTextSuffix() {
		return axisYRightTextSuffix;
	}

	public void setAxisYRightTextSuffix(CharSequence axisYRightTextSuffix) {
		this.axisYRightTextSuffix = axisYRightTextSuffix;
	}

	public String getAxisXBottomTextTimeFormat() {
		return axisXBottomTextTimeFormat;
	}

	public void setAxisXBottomTextTimeFormat(String axisXBottomTextTimeFormat) {
		this.axisXBottomTextTimeFormat = axisXBottomTextTimeFormat;
	}

	public int getInitialDisplayCount() {
		return initialDisplayCount;
	}

	public void setInitialDisplayCount(int initialDisplayCount) {
		this.initialDisplayCount = initialDisplayCount;
	}

	public int getMinimumDisplayCount() {
		return minimumDisplayCount;
	}

	public void setMinimumDisplayCount(int minimumDisplayCount) {
		this.minimumDisplayCount = minimumDisplayCount;
	}

	/**
	 * 设置手势
	 * @param indicator  点击
	 * @param fling 单指飞快滑动
	 * @param pan 平移
	 * @param scale 收缩
	 */
	public void setEnableTouchGesture(boolean indicator, boolean fling, boolean pan, boolean scale) {
		this.isEnableTouchIndicator = indicator;
		this.isEnableTouchFling = fling;
		this.isEnableTouchPan = pan;
		this.isEnableTouchScale = scale;
	}

	public boolean isEnableTouchFling() {
		return isEnableTouchFling;
	}

	public boolean isEnableTouchIndicator() {
		return isEnableTouchIndicator;
	}

	public boolean isEnableTouchPan() {
		return isEnableTouchPan;
	}

	public boolean isEnableTouchScale() {
		return isEnableTouchScale;
	}

	public int getSelectedPointColor() {
		return selectedPointColor;
	}

	public void setSelectedPointColor(int selectedPointColor) {
		this.selectedPointColor = selectedPointColor;
	}

	public boolean isEnableShowAll() {
		return isEnableShowAll;
	}

	public void setEnableShowAll(boolean enableShowAll) {
		isEnableShowAll = enableShowAll;
		this.isEnableTouchFling = false;
		this.isEnableTouchPan = false;
		this.isEnableTouchScale = false;
	}

	public List<ChartLine> getGridHorLineList() {
		if(this.gridHorLineList == null) {
			this.gridHorLineList = new ArrayList<>();
		}

		if(CollectionUtil.isEmpty(this.gridHorLineList)) {
			// default
			for(int i = 0; i < 4; i ++) {
				this.gridHorLineList.add(new ChartLine());
			}
		}
		return gridHorLineList;
	}

	public List<ChartLine> getGridVerLineList() {
		if(this.gridVerLineList == null) {
			this.gridVerLineList = new ArrayList<>();
		}
		if(CollectionUtil.isEmpty(this.gridVerLineList)) {
			// default
			for(int i = 0; i < 5; i ++) {
				this.gridVerLineList.add(new ChartLine());
			}
		}
		return gridVerLineList;
	}

	public void setGridHorLineList(@IntRange(from = 2, to = 255) int gridHorLineNum) {
		if(this.gridHorLineList == null) {
			this.gridHorLineList = new ArrayList<>();
		}
		this.gridHorLineList.clear();
		if(gridHorLineNum < 2) {
			gridHorLineNum = 2;
		}
		for(int i = 0; i < gridHorLineNum; i ++) {
			this.gridHorLineList.add(new ChartLine());
		}
	}

	public void setGridVerLineList(@IntRange(from = 2, to = 255) int gridVerLineNum ) {
		if(this.gridVerLineList == null) {
			this.gridVerLineList = new ArrayList<>();
		}
		this.gridVerLineList.clear();
		if(gridVerLineNum < 2) {
			gridVerLineNum = 2;
		}
		for(int i = 0; i < gridVerLineNum; i ++) {
			this.gridVerLineList.add(new ChartLine());
		}
	}


	public int getGridColor() {
		return gridColor;
	}

	public void setGridColor(@ColorInt int gridColor) {
		this.gridColor = gridColor;
	}

	public boolean isEnableShowHorGrid() {
		return isEnableShowHorGrid;
	}

	public void setEnableShowHorGrid(boolean enableShowHorGrid) {
		isEnableShowHorGrid = enableShowHorGrid;
	}

	public boolean isEnableShowVerGrid() {
		return isEnableShowVerGrid;
	}

	public void setEnableShowVerGrid(boolean enableShowVerGrid) {
		isEnableShowVerGrid = enableShowVerGrid;
	}

	public int getAxisTextColor() {
		return axisTextColor;
	}

	public void setAxisTextColor(int axisTextColor) {
		this.axisTextColor = axisTextColor;
	}

	public int getAxisTextSizeInSp() {
		return axisTextSizeInSp;
	}

	public void setAxisTextSizeInSp(int axisTextSizeInSp) {
		this.axisTextSizeInSp = axisTextSizeInSp;
	}

	public boolean isEnableShowLeftAxisTexts() {
		return isEnableShowLeftAxisTexts;
	}

	public void setEnableShowLeftAxisTexts(boolean enableShowLeftAxisTexts) {
		isEnableShowLeftAxisTexts = enableShowLeftAxisTexts;
	}

	public boolean isEnableShowRightAxisTexts() {
		return isEnableShowRightAxisTexts;
	}

	public void setEnableShowRightAxisTexts(boolean enableShowRightAxisTexts) {
		isEnableShowRightAxisTexts = enableShowRightAxisTexts;
	}

	public boolean isEnableShowBottomAxisTexts() {
		return isEnableShowBottomAxisTexts;
	}

	public void setEnableShowBottomAxisTexts(boolean enableShowBottomAxisTexts) {
		isEnableShowBottomAxisTexts = enableShowBottomAxisTexts;
	}

	public boolean isEnableShowTopAxisTexts() {
		return isEnableShowTopAxisTexts;
	}

	public void setEnableShowTopAxisTexts(boolean enableShowTopAxisTexts) {
		isEnableShowTopAxisTexts = enableShowTopAxisTexts;
	}


	protected float getAxisTextHight() {
		return axisTextHight;
	}

	// 在char里被动设置 不在外部设置
	protected void setAxisTextHight(float axisTextHight) {
		this.axisTextHight = axisTextHight;
	}

	public List<ChartText> getAxisRightTextList() {
		if(axisRightTextList.isEmpty()) {
			// default
			for (int i = 0; i < 4; i++) {
				axisRightTextList.add(new ChartText());
			}
		}
		return axisRightTextList;
	}

	public void setAxisRightTextList(int num) {
		if(num < 2) {
			num = 2;
		}
		axisRightTextList.clear();
		for (int i = 0; i < num; i++) {
			axisRightTextList.add(new ChartText());
		}
	}

	public List<ChartText> getAxisBottomTextList() {
		if(axisBottomTextList.isEmpty()) {
			// default
			for (int i = 0; i < 2; i++) {
				axisBottomTextList.add(new ChartText());
			}
		}
		return axisBottomTextList;
	}

	public void setAxisBottomTextList(int num) {
		if(num < 2) {
			num = 2;
		}
		axisBottomTextList.clear();
		for (int i = 0; i < num; i++) {
			axisBottomTextList.add(new ChartText());
		}
	}

	public List<ChartText> getAxisTopTextList() {
		return axisTopTextList;
	}

	public void setAxisTopTextList(int num) {
		if(num < 2) {
			num = 2;
		}
		axisTopTextList.clear();
		for (int i = 0; i < num; i++) {
			axisTopTextList.add(new ChartText());
		}
	}

	public List<ChartText> getAxisLeftTextList() {
		return axisLeftTextList;
	}

	public void setAxisLeftTextList(int num) {
		if(num < 2) {
			num = 2;
		}
		axisLeftTextList.clear();
		for (int i = 0; i < num; i++) {
			axisLeftTextList.add(new ChartText());
		}
	}

	public enum AxisStyle {
		FirstAndLast,
	}

	/**
	 * 获取Y的最大和最小值
	 */
	public float getListMax() {
		if(this.pointList == null || this.pointList.size() <= 0) {
			return -1f;
		}

		float[] arr = toFloatArr(this.pointList);

		float max = arr[0];
		for (int i = 1, len = arr.length; i < len; i++) {
			max = max >= arr[i] ? max : arr[i];
		}
		return max;
	}

	public float getListMin() {
		if(this.pointList == null || this.pointList.size() <= 0) {
			return -1f;
		}

		float[] arr = toFloatArr(this.pointList);

		float min = arr[0];
		for (int i = 1, len = arr.length; i < len; i++) {
			min = min <= arr[i] ? min : arr[i];
		}
		return min;
	}

	private float[] toFloatArr(@NonNull List<? extends ChartPoint> list) {
		float[] arr = new float[list.size()];
		for(int i = 0, len = list.size(); i < len; i ++) {
			arr[i] = list.get(i).getYFloat();
		}
		return arr;
	}

	/**
	 * 包含(x,y)的bar
	 */
	public ChartPoint getBarPoint(float x , float y) {
		float dL = 0f, dT = 0f, dR = 0f, dB = 0f;
		if(touchingFluctuateRate != null) {
			dL = touchingFluctuateRate.left;
			dT = touchingFluctuateRate.top;
			dR = touchingFluctuateRate.right;
			dB = touchingFluctuateRate.bottom;
		}

		for(ChartPoint point : getPointList()) {
			if(x >= point.getL() - dL * (point.getR() - point.getL())
					&& x <= point.getR() + dR * (point.getR() - point.getL())
					&& y >= point.getT() - dT * (point.getB() - point.getT())
					&& y <= point.getB() + dB * (point.getB() - point.getT())) {
				return point;
			}
		}

		return null;
	}

	public void clear() {
		if(getPointList() != null) {
			getPointList().clear();
		}
	}

	public int getDataCount() {
		if(CollectionUtil.isEmpty(getPointList())) {
			return 0;
		}
		return getPointList().size();
	}

	public int getDisplayPointCount() {
		return getToIndex() - getFromIndex();
	}

	public ChartPoint getFromIndexPoint() {
		if(pointList == null || pointList.size() <= fromIndex) {
			return null;
		}
		return pointList.get(fromIndex);
	}

	public ChartPoint getToIndexPoint() {
		if(pointList == null || pointList.size() < toIndex) {
			return null;
		}
		return pointList.get(toIndex - 1);
	}



}
