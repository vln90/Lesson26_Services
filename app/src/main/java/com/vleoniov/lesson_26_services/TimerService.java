package com.vleoniov.lesson_26_services;

import android.app.Service;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

public class TimerService extends Service {

    private static final String TAG = "TimerService";

    private static final long TIME_COUNTDOWN = 1000 * 600L;
    private static final long TIMER_PERIOD = 1000L;

    public static final String ACTION_CLOSE = "TIMER_SERVICE_ACTION_CLOSE";

    private CountDownTimer mCountDownTimer;

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG, "onCreate() called");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand() called with: intent = [" + intent + "], flags = [" + flags + "], startId = [" + startId + "]");

        if (ACTION_CLOSE.equals(intent.getAction())) {
            stopSelf();
        } else {
            startCountdownTimer(TIME_COUNTDOWN, TIMER_PERIOD);
        }

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.d(TAG, "onDestroy() called");

        stopCountdownTimer();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void startCountdownTimer(long time, long period) {
        mCountDownTimer = new CountDownTimer(time, period) {
            @Override
            public void onTick(long millisUntilFinished) {
                Log.d(TAG, "onTick() called with: millisUntilFinished = [" + millsToSeconds(millisUntilFinished) + "]");
            }

            @Override
            public void onFinish() {
                Log.d(TAG, "onFinish() called");

                stopSelf();
            }
        };

        mCountDownTimer.start();
    }

    private void stopCountdownTimer() {
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
            mCountDownTimer = null;
        }
    }

    private long millsToSeconds(long time) {
        return time / 1000L;
    }
}
