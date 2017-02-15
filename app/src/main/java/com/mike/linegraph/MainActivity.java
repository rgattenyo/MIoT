package com.mike.linegraph;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private final Handler mHandler = new Handler();
    private Runnable mTimer1, mTimer2;
    private LineGraphSeries<DataPoint> mSeries1;
    private double lastX = 5d;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GraphView graph = (GraphView) findViewById(R.id.graph);
        mSeries1 = new LineGraphSeries<>(generateData());
        graph.addSeries(mSeries1);

        Viewport viewport = graph.getViewport();
        viewport.setXAxisBoundsManual(true);
        viewport.setMinX(0);
        viewport.setMaxX(40);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mTimer1 = new Runnable(){
            @Override
            public void run() {
                mSeries1.resetData(generateData());
                mHandler.postDelayed(this, 300);
            }
        };
        mHandler.postDelayed(mTimer1,300);
    }

    @Override
    protected void onPause() {
        mHandler.removeCallbacks(mTimer1);
        super.onPause();
    }

    private DataPoint[] generateData() {
        int count = 40;
        DataPoint[] values = new DataPoint[count];
        for(int i = 0; i < count; i++) {
            double x = i;
            //double f = mRand.nextDouble()*0.15+0.3;
            //double y = Math.sin(i*f+2) + mRand.nextDouble()*0.3;
            double y =  mRand.nextDouble();
            DataPoint v = new DataPoint(x, y);
            values[i] = v;
        }
        return values;
    }

    double mLastRandom = 2;
    Random mRand = new Random();
    private double getRandom() {
        return mLastRandom += mRand.nextDouble()*0.5 - 0.25;
    }
}
