package com.example.zhangliang.customchartview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.example.zhangliang.customchartview.chart.ChartPoint;
import com.example.zhangliang.customchartview.chart.PolylineChart;
import com.example.zhangliang.customchartview.chart.PolylineData;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
	PolylineChart polylineChart;
	PolylineData polylineData;

	TextView tv_value;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		tv_value = (TextView) findViewById(R.id.tv_value);

		polylineChart = (PolylineChart) findViewById(R.id.polyline_chart);
		polylineChart.setOnTouchChartListener(new PolylineChart.OnTouchChartListener() {
			@Override
			public void onCallback(ChartPoint point) {
				onPolylineChartCallback(point);
			}
		});
//		initPolylineChartPropsMin();
//		initPolylineChartPropsNormol1();
		initPolylineChartPropsNormol2();
	}

	@Override
	protected void onResume() {
		super.onResume();
		refreshPolylineChartData();
		polylineChart.setDataAndRender(polylineData);
	}

	private void initPolylineChartPropsMin() {
		if(polylineData == null) {
			polylineData = new PolylineData();
		}

		// axis
		// grid
		// cross
		// polyline color
		// gesture
		// display range

	}

	// axis default. right and bottom
	// grid default. horizontal only
	// cross default.
	// polyline color default.
	// gesture. show indicator(cross) only
	// display range. show all points
	private void initPolylineChartPropsNormol1() {
		if(polylineData == null) {
			polylineData = new PolylineData();
		}

		//  -------props-------
		// axis
		// grid
		// cross
		// polyline color

		// gesture
		polylineData.setEnableTouchGesture(true, false, false, false);
		// display range
		polylineData.setEnableShowAll(true);
	}

	private void initPolylineChartPropsNormol2() {
		if(polylineData == null) {
			polylineData = new PolylineData();
		}

		//  -------props-------
		// axis
		// grid
		// cross
		// polyline color

		// gesture
		// display range
		polylineData.setEnableTouchGesture(true, true, true, true);
//		polylineData.setEnableShowAll(true);
	}

	private void initPolylineChartPropsNormol3() {
		if(polylineData == null) {
			polylineData = new PolylineData();
		}

		//  -------props-------
		// axis
		// grid
		// cross
		// polyline color

		// gesture
		// display range
		polylineData.setEnableTouchGesture(true, true, true, true);
//		polylineData.setEnableShowAll(true);
	}

	private void initPolylineChartPropsMax() {
		if(polylineData == null) {
			polylineData = new PolylineData();
		}

		//  -------props-------
		// axis
		// grid
		// cross
		// polyline color

		// gesture
		// display range
//		polylineData.setEnableTouchGesture(true, true, true, true);
//		polylineData.setEnableShowAll(true);
	}

	private void refreshPolylineChartData() {
		if(polylineData == null) {
			polylineData = new PolylineData();
		}

		polylineData.clear();

		// -------points-------
		ChartPoint point;
		List<ChartPoint> list = new ArrayList<>();
		for(int i = 0, len = 50; i < len; i ++) {
			point = new ChartPoint();
			point.setValX(String.valueOf(i));
			point.setValY(new BigDecimal((Math.random() - 0.5) * 10));
			point.setId(i);
			list.add(point);
		}
		polylineData.setPointList(list);
	}

	private void onPolylineChartCallback(ChartPoint point) {
		tv_value.setText(point.getYScale2Str());
	}
}
