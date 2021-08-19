package com.vleoniov.lesson_26_services;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setContentView(R.layout.activity_main);

        findViewById(R.id.start_service_button).setOnClickListener(this);
        findViewById(R.id.stop_service_button).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_service_button:
                Log.d(TAG, "start_service_button clicked");

                startService();
                break;

            case R.id.stop_service_button:
            default:
                Log.d(TAG, "stop_service_button clicked");

                stopService();
                break;
        }
    }

    private void startService() {
        Intent intent = new Intent(this, TimerService.class);
        startService(intent);
    }

    private void stopService() {
        Intent intent = new Intent(this, TimerService.class);
        stopService(intent);

        // Intent intent = new Intent(this, TimerService.class);
        // intent.setAction(TimerService.ACTION_CLOSE);
        //
        // startService(intent);
    }
}