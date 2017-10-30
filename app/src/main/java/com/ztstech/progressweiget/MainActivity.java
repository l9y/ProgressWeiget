package com.ztstech.progressweiget;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PointerProgressView pointerProgressView = (PointerProgressView) findViewById(R.id.ppv);
        PointerProgressView.PointerItem item = new PointerProgressView.PointerItem();
        item.text = "目标值：50%";
        item.position = 0.5f;
        item.pointerColor = Color.GREEN;
        item.textColor = Color.WHITE;
        item.id = 1;
        pointerProgressView.addPointer(item);
    }
}
