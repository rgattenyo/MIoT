package com.mike.linegraph;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class Main3Activity extends AppCompatActivity {

    LineGraphSeries<DataPoint> series;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bundle bundle = getIntent().getExtras();
        String text = bundle.getString("radio");
        Toast.makeText(Main3Activity.this,
                text, Toast.LENGTH_SHORT).show();
        double y,x;
        x = -0.1;

        GraphView graph = (GraphView) findViewById(R.id.graph);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(0);

        series = new LineGraphSeries<DataPoint>();
        for(int i =-50; i<50; i++) {
           if(new String("X").equals(text)){
                x = i;
                y = x;
               graph.getViewport().setMinX(-50);
                graph.getViewport().setMaxX(50);
               graph.getViewport().setMinY(-50);
                graph.getViewport().setMaxY(50);
            }else if(new String("X^2").equals(text)){
               x = i;
               y = x*x;
               graph.getViewport().setMinX(-50);
               graph.getViewport().setMaxX(50);
               graph.getViewport().setMinY(-2500);
               graph.getViewport().setMaxY(2500);
            }else if(new String("X^3").equals(text)){
                x = i;
                y = x * x * x;
                graph.getViewport().setMinX(-50);
                graph.getViewport().setMaxX(50);
                graph.getViewport().setMinY(-125000);
                graph.getViewport().setMaxY(125000);
            }else if(new String("SinX").equals(text)){
               x = x + 0.1;
               y = Math.sin(x);
               graph.getViewport().setMaxX(10);
               graph.getViewport().setMinY(-2);
               graph.getViewport().setMaxY(2);
           }else if(new String("CosX").equals(text)){
                x = x + 0.1;
                y = Math.cos(x);
                graph.getViewport().setMaxX(10);
                graph.getViewport().setMinY(-2);
                graph.getViewport().setMaxY(2);
            }else if(new String("TanX").equals(text)){
               x = x + 0.1;
               y = Math.tan(x);
               graph.getViewport().setMaxX(10);
               graph.getViewport().setMinY(-10);
               graph.getViewport().setMaxY(10);}
           else{
               x = x + 0.1;
               y = 1;
           }
            series.appendData(new DataPoint(x, y), true, 100);
        }
        graph.addSeries(series);
    }


}
