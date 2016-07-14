package com.example.zhangliang.customchartview.chart;

import com.example.zhangliang.customchartview.util.DateUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 图的点或bar；存储最原始的数据和对应的像素点
 * 一般的曲线图直接用
 * Created by zhangliang on 16/6/13.
 */
public class ChartPoint {
	private String valX;
	private BigDecimal valY;

	// base pix
	private float x;
	private float y;

	// color
	private int color;

	// bar 四维
	private float l;
	private float t;
	private float r;
	private float b;

	// id 唯一标识符
	private int id = 0;

	public ChartPoint(){}
	public ChartPoint(String valX, BigDecimal valY) {
		this.valX = valX;
		this.valY = valY;
	}

	public ChartPoint(String valX, BigDecimal valY, int color) {
		this.valX = valX;
		this.valY = valY;
		this.color = color;
	}

	// x
	public String getValX() {
		return valX;
	}

	public long getValXLong() {
		long l;
		try {
			l = Long.parseLong(valX);
		} catch (Exception e) {
//			e.printStackTrace();
			l = 0;
		}
		return l;
	}

	public String getValXFormatTime(String format) {
		long l = getValXLong();
		return l <= 0 ? getValX() : DateUtils.getFormatDate(l, DateUtils.getDateFormat(format));
	}

	public void setValX(String valX) {
		this.valX = valX;
	}

	// y
	public void setValY(BigDecimal valY) {
		this.valY = valY;
	}
	public BigDecimal getValY() {
		return valY;
	}
	public float getYFloat() {
		return this.valY.floatValue();
	}
	public double getYDouble() {
		return this.valY.doubleValue();
	}

	public String getYScale2Str() {
		return getYScaleStr(2);
	}
	public String getYScaleStr(int scale) {
		return this.valY.setScale(scale, RoundingMode.DOWN).toPlainString();
	}

	// pixel
	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public void setXY(float x, float y) {
		setX(x);
		setY(y);
	}

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}

	public void setColorAlpha(int alpha) {
		color = (alpha << 24) | (color & 0x00FFFFFF);
	}

	// bar

	public float getL() {
		return l;
	}

	public void setL(float l) {
		this.l = l;
	}

	public float getT() {
		return t;
	}

	public void setT(float t) {
		this.t = t;
	}

	public float getR() {
		return r;
	}

	public void setR(float r) {
		this.r = r;
	}

	public float getB() {
		return b;
	}

	public void setB(float b) {
		this.b = b;
	}

	public void setBarPosition(float l, float t, float r, float b) {
		this.l = l;
		this.t = t;
		this.r = r;
		this.b = b;
	}

	/**
	 * 当前bar是否包含点(x, y)
	 */
	public boolean containPoint(float x, float y) {
		return x >= l && x <= r && y >= t && y <= b;
	}


	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
