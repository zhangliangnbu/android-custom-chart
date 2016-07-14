package com.example.zhangliang.customchartview.chart;

/**
 * chart text
 * Created by zhangliang on 16/6/14.
 */
public class ChartText {
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public float getL() {
		return l;
	}

	public void setL(float l) {
		this.l = l;
	}

	public float getB() {
		return b;
	}

	public void setB(float b) {
		this.b = b;
	}

	public void setProps(String text, float l, float b) {
		this.text = text;
		this.l = l;
		this.b = b;
	}

	private String text;
	private float l;
	private float b;

	public ChartText(){}
	public ChartText(String text, float l, float b) {
		this.text = text;
		this.l = l;
		this.b = b;
	}
}
