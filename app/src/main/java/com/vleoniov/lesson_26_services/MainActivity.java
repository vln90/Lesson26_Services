package com.vleoniov.lesson_26_services;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";

    public static final int MSG_START_TIMER = 1;
    public static final int MSG_STOP_TIMER = 2;
    public static final String BUNDLE_KEY_TIME = "BUNDLE_KEY_TIME";

    public static final int MSG_TIMER_CALLED = 3;
    public static final String BUNDLE_CURRENT_TIME = "BUNDLE_CURRENT_TIME";

    private TextView mTextView;

    private TimerServiceConnection mTimerServiceConnection;

    private Messenger mTimerServiceMessenger;
    private Messenger mMainActivityMessenger = new Messenger(new MainActivityHandler());

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
        // Intent intent = new Intent(this, TimerService.class);
        // stopService(intent);

        Intent intent = new Intent(this, TimerService.class);
        intent.setAction(TimerService.ACTION_CLOSE);

        startService(intent);
    }

    private void startTimer() {
        if (mTimerServiceMessenger != null) {
            Message message = Message.obtain(null, MSG_START_TIMER);

            Bundle bundle = new Bundle();
            bundle.putLong(BUNDLE_KEY_TIME, 30000L);
            message.setData(bundle);
            message.replyTo = mMainActivityMessenger;

            try {
                mTimerServiceMessenger.send(message);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private void bindService() {
        Intent intent = new Intent(this, TimerService.class);
        bindService(intent, mTimerServiceConnection, BIND_AUTO_CREATE);
    }

    private void unbindService() {
        if (mTimerServiceConnection != null && mTimerServiceMessenger != null) {
            unbindService(mTimerServiceConnection);

            mTimerServiceMessenger = null;
        }
    }

    public class MainActivityHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case MSG_TIMER_CALLED:
                    String time = msg.getData().getString(BUNDLE_CURRENT_TIME);

                    mTextView.setText(time);

                    break;
            }
        }
    }

    private class TimerServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "onServiceConnected() called with: name = [" + name + "], service = [" + service + "]");

            mTimerServiceMessenger = new Messenger(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected() called with: name = [" + name + "]");
        }
    }
}