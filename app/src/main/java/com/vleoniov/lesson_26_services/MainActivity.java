package com.vleoniov.lesson_26_services;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";

    private TextView mTextView;

    private TimerService mTimerService;
    private TimerServiceConnection mTimerServiceConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setContentView(R.layout.activity_main);

        findViewById(R.id.start_service_button).setOnClickListener(this);
        findViewById(R.id.stop_service_button).setOnClickListener(this);
        findViewById(R.id.bind_service_button).setOnClickListener(this);
        findViewById(R.id.unbind_service_button).setOnClickListener(this);
        findViewById(R.id.start_timer_button).setOnClickListener(this);

        mTextView = findViewById(R.id.timer_text_view);

        mTimerServiceConnection = new TimerServiceConnection();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_service_button:
                Log.d(TAG, "start_service_button clicked");

                startService();
                break;

            case R.id.stop_service_button:
                Log.d(TAG, "stop_service_button clicked");

                stopService();
                break;
            case R.id.bind_service_button:
                Log.d(TAG, "bind_service_button clicked");

                bindService();
                break;
            case R.id.unbind_service_button:
                Log.d(TAG, "unbind_service_button clicked");

                unbindService();
                break;
            case R.id.start_timer_button:
                Log.d(TAG, "start_timer_button clicked");

                startTimer();
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

    private void startListeningTimer() {
        mTimerService.setOnTimerChangedListener(timer -> mTextView.setText(timer));
    }

    private void stopListeningTimer() {
        if (mTimerService != null) {
            mTimerService.setOnTimerChangedListener(null);
        }
    }

    private void startTimer() {
        if (mTimerService != null) {
            mTimerService.startCountdownTimer(10000L, 1000L);
        }
    }

    private class TimerServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "onServiceConnected() called with: name = [" + name + "], service = [" + service + "]");

            mTimerService = ((TimerService.LocalTimerServiceBinder) service).getTimerService();

            startListeningTimer();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected() called with: name = [" + name + "]");
        }
    }

    private void bindService() {
        Intent intent = new Intent(this, TimerService.class);
        bindService(intent, mTimerServiceConnection, BIND_AUTO_CREATE);
    }

    private void unbindService() {
        if (mTimerServiceConnection != null && mTimerService != null) {
            unbindService(mTimerServiceConnection);

            stopListeningTimer();
            mTimerService = null;
        }
    }
}