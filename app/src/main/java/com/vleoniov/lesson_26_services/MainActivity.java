package com.vleoniov.lesson_26_services;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.vleoniov.timerservice.ITimerServiceAIDL;
import com.vleoniov.timerservice.ITimerServiceResult;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";

    private TextView mTextView;

    private TimerServiceConnection mTimerServiceConnection;
    private ITimerServiceAIDL mITimerServiceAIDL;
    private Handler mHandler = new Handler();


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
        ComponentName componentName = new ComponentName("com.vleoniov.timerservice",
                "com.vleoniov.timerservice.TimerService");
        Intent intent = new Intent();
        intent.setComponent(componentName);
        startService(intent);
    }

    private void stopService() {
        // Intent intent = new Intent(this, TimerService.class);
        // stopService(intent);

        ComponentName componentName = new ComponentName("com.vleoniov.timerservice",
                "com.vleoniov.timerservice.TimerService");
        Intent intent = new Intent();
        intent.setComponent(componentName);
        intent.setAction("TIMER_SERVICE_ACTION_CLOSE");

        startService(intent);
    }

    private void startTimer() {
        if (mITimerServiceAIDL != null) {
            try {
                mITimerServiceAIDL.startTimer(30_000L, 1000L, mTimerServiceResult);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private void bindService() {
        ComponentName componentName = new ComponentName("com.vleoniov.timerservice",
                "com.vleoniov.timerservice.TimerService");
        Intent intent = new Intent();
        intent.setComponent(componentName);
        bindService(intent, mTimerServiceConnection, BIND_AUTO_CREATE);
    }

    private void unbindService() {
        if (mTimerServiceConnection != null) {
            unbindService(mTimerServiceConnection);
        }
    }

    private class TimerServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "onServiceConnected() called with: name = [" + name + "], service = [" + service + "]");

            mITimerServiceAIDL = ITimerServiceAIDL.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected() called with: name = [" + name + "]");
        }
    }

    private final ITimerServiceResult.Stub mTimerServiceResult = new ITimerServiceResult.Stub() {
        @Override
        public void onTick(long time) throws RemoteException {
            mHandler.post(() -> mTextView.setText(Long.toString(time)));
        }
    };
}