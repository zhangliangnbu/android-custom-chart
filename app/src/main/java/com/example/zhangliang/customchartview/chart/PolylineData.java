package com.example.zhangliang.customchartview.chart;

import android.support.annotation.ColorInt;

import java.util.List;

/**
 * polyline common data
 * Created by zhangliang on 16/6/13.
 */
public class PolylineData extends ChartData {

	public PolylineData(){}
	public PolylineData(List<? extends ChartPoint> pointList){super(pointList);}
	public PolylineData(List<? extends ChartPoint> pointList, int color) {
		super(pointList, color);
	}

	// --------------touch cross---------------
	private CrossType crossType;
	private CrossTextType crossTextType;
	private int crossLineColor = 0x99999999;
	private int crossTextRiseColor = 0x66FF0000;
	private int crossTextFallColor = 0x6600FF00;
	private int crossTextDefaultColor = 0x66666666;
	private int crossPointColor = 0xFFFFFFFF;
	private int crossRectColor = 0x66999999;

	private int crossPointRadius = 1;// dip

	// --------------polyline color---------------
	private int shadowTopColor = 0xFF4C9BFF;//shadow顶部颜色
	private int shadowBottomColor = 0x00000000;//shadow底部颜色
	private int polylineColor = 0xFF4C9BFF;


	public CrossType getCrossType() {
		return crossType;
	}

	public void setCrossType(CrossType crossType) {
		this.crossType = crossType;
	}

	public CrossTextType getCrossTextType() {
		return crossTextType;
	}

	public void setCrossTextType(CrossTextType crossTextType) {
		this.crossTextType = crossTextType;
	}

	public int getCrossLineColor() {
		return crossLineColor;
	}

	public void setCrossLineColor(int crossLineColor) {
		this.crossLineColor = crossLineColor;
	}

	@ColorInt
	public int getCrossTextRiseColor() {
		return crossTextRiseColor;
	}

	public void setCrossTextRiseColor(int crossTextRiseColor) {
		this.crossTextRiseColor = crossTextRiseColor;
	}

	@ColorInt
	public int getCrossTextFallColor() {
		return crossTextFallColor;
	}

	public void setCrossTextFallColor(int crossTextFallColor) {
		this.crossTextFallColor = crossTextFallColor;
	}

	public int getCrossTextDefaultColor() {
		return crossTextDefaultColor;
	}

	public void setCrossTextDefaultColor(int crossTextDefaultColor) {
		this.crossTextDefaultColor = crossTextDefaultColor;
	}

	public int getCrossPointColor() {
		return crossPointColor;
	}

	public void setCrossPointColor(int crossPointColor) {
		this.crossPointColor = crossPointColor;
	}

	public int getCrossRectColor() {
		return crossRectColor;
	}

	public void setCrossRectColor(int crossRectColor) {
		this.crossRectColor = crossRectColor;
	}

	public int getCrossPointRadius() {
		return crossPointRadius;
	}

	public void setCrossPointRadius(int crossPointRadius) {
		this.crossPointRadius = crossPointRadius;
	}

	public int getShadowTopColor() {
		return shadowTopColor;
	}

	public void setShadowTopColor(int shadowTopColor) {
		this.shadowTopColor = shadowTopColor;
	}

	public int getShadowBottomColor() {
		return shadowBottomColor;
	}

	public void setShadowBottomColor(int shadowBottomColor) {
		this.shadowBottomColor = shadowBottomColor;
	}

	public int getPolylineColor() {
		return polylineColor;
	}

	public void setPolylineColor(int polylineColor) {
		this.polylineColor = polylineColor;
	}

	public enum CrossType {
		Horizontal, Vertical, MultiOrientation
	}

	public enum CrossTextType {
		OneColor, TwoColor
	}

}
