package com.vleoniov.timerservice;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

/**
 * @author Леонидов Василий on 21.08.2021
 */

public class TimerService extends Service {

    private static final String TAG = "TimerService";

    private static final long TIME_COUNTDOWN = 1000 * 600L;
    private static final long TIMER_PERIOD = 1000L;

    private static final String CHANNEL_ID = "channel_id_2";
    private static final int NOTIFICATION_ID = 1;

    public static final String ACTION_CLOSE = "TIMER_SERVICE_ACTION_CLOSE";

    private CountDownTimer mCountDownTimer;

    private ITimerServiceResult mTimerServiceResult;


    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannel();

        Log.d(TAG, "onCreate() called");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand() called with: intent = [" + intent + "], flags = [" + flags + "], startId = [" + startId + "]");

        if (ACTION_CLOSE.equals(intent.getAction())) {
            stopSelf();
        } else {
            startCountdownTimer(TIME_COUNTDOWN, TIMER_PERIOD);

            startForeground(NOTIFICATION_ID, createNotification(1000));
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
        Log.d(TAG, "onBind() called with: intent = [" + intent + "]");

        return binder.asBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind() called with: intent = [" + intent + "]");

        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);

        Log.d(TAG, "onRebind() called with: intent = [" + intent + "]");
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.notification_channel_name);
            String description = getString(R.string.notification_channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager =
                    getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private Notification createNotification(long currentTime) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID);

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        Intent intentCloseService = new Intent(this, TimerService.class);
        intentCloseService.setAction(ACTION_CLOSE);
        PendingIntent pendingIntentCloseService = PendingIntent.getService(this, 0, intentCloseService, PendingIntent.FLAG_ONE_SHOT);

        builder.setContentTitle(getString(R.string.timer_service_content_title))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText(getString(R.string.timer_service_content_description) + currentTime)
                .setOnlyAlertOnce(true)
                .addAction(0, getString(R.string.button_stop_service), pendingIntentCloseService)
                .setContentIntent(pendingIntent);

        return builder.build();
    }

    private void updateNotification(@NonNull Notification notification) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    public void startCountdownTimer(long time, long period) {
        mCountDownTimer = new CountDownTimer(time, period) {
            @Override
            public void onTick(long millisUntilFinished) {
                Log.d(TAG, "onTick() called with: millisUntilFinished = [" + millsToSeconds(millisUntilFinished) + "]");

                if (mTimerServiceResult != null) {
                    try {
                        mTimerServiceResult.onTick(millisUntilFinished);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }

                updateNotification(createNotification(millsToSeconds(millisUntilFinished)));
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

    private Handler mHandler = new Handler();

    private final ITimerServiceAIDL.Stub binder = new ITimerServiceAIDL.Stub() {

        @Override
        public void startTimer(long time, long period, ITimerServiceResult result) throws RemoteException {
            mHandler.post(() -> startCountdownTimer(time, period));

            mTimerServiceResult = result;
        }
    };
}
