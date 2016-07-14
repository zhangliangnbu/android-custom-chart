package com.example.zhangliang.customchartview.chart;

/**
 * chart line
 * Created by zhangliang on 16/6/14.
 */
public class ChartLine {
	private float startX;
	private float startY;
	private float stopX;
	private float stopY;

	public ChartLine(){}
	public ChartLine(float stopY, float startX, float startY, float stopX) {
		this.stopY = stopY;
		this.startX = startX;
		this.startY = startY;
		this.stopX = stopX;
	}

	public float getStartX() {
		return startX;
	}

	public void setStartX(float startX) {
		this.startX = startX;
	}

	public float getStartY() {
		return startY;
	}

	public void setStartY(float startY) {
		this.startY = startY;
	}

	public float getStopX() {
		return stopX;
	}

	public void setStopX(float stopX) {
		this.stopX = stopX;
	}

	public float getStopY() {
		return stopY;
	}

	public void setStopY(float stopY) {
		this.stopY = stopY;
	}

	public void setLinePositioin(float startX, float startY, float stopX, float stopY) {
		this.startX = startX;
		this.startY = startY;
		this.stopX = stopX;
		this.stopY = stopY;
	}
}
