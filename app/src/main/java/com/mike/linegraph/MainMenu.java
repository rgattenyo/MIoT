package com.mike.linegraph;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainMenu extends AppCompatActivity {

    private Button GraphButton;
    private Button OtherButton;
    private Button BlueButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        GraphButton = (Button) findViewById(R.id.graphButton);
        OtherButton = (Button) findViewById(R.id.otherButton);
        BlueButton = (Button) findViewById(R.id.blueButton);
    }

    public void onGraphClick(View view) {
        Intent getGraphIntent = new Intent(this,MainActivity.class);
        startActivity(getGraphIntent);
    }

    public void onOtherClick(View view){
        Intent getOtherIntent = new Intent(this,Main2Activity.class);
        startActivity(getOtherIntent);
    }

    public void onBlueClick(View view){
        Intent getBlueIntent = new Intent(this,Bluetooth.class);
        startActivity(getBlueIntent);
    }

}
