package com.mike.linegraph;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class Main2Activity extends AppCompatActivity {

    private Button GraphItButton;
    private RadioGroup radioGroup;
    private RadioButton radioButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);


    }


    public void onGraphItClick(View view) {
        radioGroup = (RadioGroup) findViewById(R.id.radioGraph);

        int selectedId = radioGroup.getCheckedRadioButtonId();
        radioButton = (RadioButton) findViewById(selectedId);
        Intent getGraphItIntent = new Intent(this,Main3Activity.class);
        Bundle bundle = new Bundle();
        String value = String.valueOf(radioButton.getText());
        bundle.putString("radio",value);

        getGraphItIntent.putExtras(bundle);
        startActivity(getGraphItIntent);
    }
}
