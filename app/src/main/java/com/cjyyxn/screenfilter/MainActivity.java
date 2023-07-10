package com.cjyyxn.screenfilter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.cjyyxn.screenfilter.ui.MainUI;
import com.cjyyxn.screenfilter.ui.PreparatoryActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("ccjy", "MainActivity created");
        new MainUI(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!GlobalStatus.isReady()){
            Toast.makeText(this, "未设置必须的权限", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, PreparatoryActivity.class));
        }
    }
}