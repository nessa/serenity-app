package com.amusebouche.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.amusebouche.activities.NotificationActivity;
import com.amusebouche.activities.R;

import java.io.IOException;

/**
 * Countdown service.
 * Author: Noelia Sales <noelia.salesmontes@gmail.com>
 *
 * This service encapsulates a countdown timer. When the countdown begins, it creates a
 * notification that indicates the remaining seconds to end. When the countdown ends, it
 * plays an alarm sound and waits for an event to cancel it.
 *
 * It only serves one countdown at a time, so it's needed to stop the flow in
 * order to start a new countdown.
 *
 * Related classes:
 * - CountdownServiceState: Posible values for the internal service states.
 * - CountdownServiceBinder: Specific binder for this service.
 *
 * Internal services:
 * - Countdown timer
 * - Max alarm duration timer (to skip the alarm if the user never interacts)
 * - Broadcast manager (to send UI notifications to the fragment)
 * - Notification manager and notification builder (to set and update notifications)
 * - Media player (to play the alarm sound)
 */
public class CountdownService extends Service {

    static final String TAG = CountdownService.class.getName();

    private static final int COUNTDOWN_TICK_INTERVAL = 1000;
    private static final int THOUSAND = 1000;
    private static final int NOTIFICATION_ID = 10000;

    // Services and helpers
    private IBinder mCountdownServiceBinder;
    private CountDownTimer mCountdownTimer;
    private CountDownTimer mMaxAlarmDurationTimer;
    private CountdownServiceState mServiceState;
    private LocalBroadcastManager mBroadcaster;
    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mNotifyBuilder;
    private MediaPlayer mMediaPlayer;

    // Data variables
    private long mRemainingMilliseconds;
    private boolean mSoundIsPlaying;

    public CountdownService() {
        super();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.mServiceState = CountdownServiceState.WAITING;
        this.mRemainingMilliseconds = Long.MAX_VALUE;
        this.mCountdownServiceBinder = new CountdownServiceBinder(this);
        this.mBroadcaster = LocalBroadcastManager.getInstance(this);
        this.mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        this.mSoundIsPlaying = false;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return this.mCountdownServiceBinder;
    }

    @Override
    public void onDestroy() {
        this.stopCountdownTimer();
        super.onDestroy();
    }

    /**
     * Starts the countdown. State transition from WAITING to COUNTING_DOWN.
     * @param seconds the number of seconds to count down
     */
    public void startCountdown(int seconds) {
        Log.d(TAG, "startCountdown(" + seconds + ")");
        if (this.checkState(CountdownServiceState.WAITING)) {
            this.mRemainingMilliseconds = seconds * THOUSAND;
            this.mServiceState = CountdownServiceState.COUNTING_DOWN;
            this.startCountdownTimer();
        }
    }

    /**
     * Stops the countdown completely. State transition from any state to WAITING.
     */
    public void stopCountdown() {
        this.stopCountdownTimer();
        this.cancelAlarmSound();
        this.mServiceState = CountdownServiceState.WAITING;
    }


    /**
     * Check if the service is in one of the given states.
     * @param validStates States to check.
     * @return boolean True if the service is in one of the given states, false otherwise.
     */
    private boolean checkState(CountdownServiceState... validStates) {
        for (CountdownServiceState CountdownServiceState : validStates) {
            if (this.mServiceState == CountdownServiceState) {
                return true;
            }
        }
        return false;
    }

    /**
     * Creates and starts the internal countdown timer.
     */
    private void startCountdownTimer() {
        Log.d(TAG, "startCountdownTimer(" + this.mRemainingMilliseconds + ")");

        Integer m = (int) ((this.mRemainingMilliseconds / 1000) / 60);
        Integer s = (int) (this.mRemainingMilliseconds / 1000) % 60;
        String mString = m + "";
        String sString = s + "";
        if (sString.length() == 1) {
            sString = "0" + sString;
        }

        // Bring app to foreground when clicking on the notification
        Intent showTaskIntent = new Intent(getApplicationContext(), NotificationActivity.class);
        showTaskIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent contentIntent = PendingIntent.getActivity(
            getApplicationContext(),
            0,
            showTaskIntent,
            PendingIntent.FLAG_UPDATE_CURRENT);

        mNotifyBuilder = new NotificationCompat.Builder(this)
            .setContentTitle(getString(R.string.app_name))
            .setContentText(String.format(getString(R.string.detail_timer_notification_time_message),
                mString, sString))
            .setSmallIcon(R.drawable.ic_launcher)
            .setOngoing(true)
            .setContentIntent(contentIntent);

        // Check the notification manager still sets up before notify the new notification
        if (mNotificationManager != null) {
            this.mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        mNotificationManager.notify(NOTIFICATION_ID, mNotifyBuilder.build());

        this.mCountdownTimer = new CountDownTimer(this.mRemainingMilliseconds, COUNTDOWN_TICK_INTERVAL) {
            public void onTick(long millisUntilFinished) {
                CountdownService.this.mRemainingMilliseconds = millisUntilFinished;
                CountdownService.this.onCountdownTimerTick();
            }

            public void onFinish() {
                CountdownService.this.onCountdownTimerFinish();
            }
        }.start();
    }

    /**
     * Stops the internal countdown timer.
     */
    private void stopCountdownTimer() {
        if (this.mCountdownTimer != null) {
            this.mCountdownTimer.cancel();
        }

        // Cancel notification if it's defined
        mNotificationManager.cancel(NOTIFICATION_ID);
    }

    /**
     * Method called every time the timer ticks. Send updates to the notification and the fragment.
     */
    private void onCountdownTimerTick() {
        Log.v(TAG, "countdown tick - remainingMilliseconds: " + this.mRemainingMilliseconds);

        // Update notification
        if (mNotifyBuilder != null && mNotificationManager != null) {
            Integer m = (int) ((this.mRemainingMilliseconds / 1000) / 60);
            Integer s = (int) (this.mRemainingMilliseconds / 1000) % 60;
            String mString = m + "";
            String sString = s + "";
            if (sString.length() == 1) {
                sString = "0" + sString;
            }

            mNotifyBuilder.setContentText(String.format(getString(R.string.detail_timer_notification_time_message),
                mString, sString));

            // Check the notification manager still sets up before notify the notification update
            if (mNotificationManager != null) {
                this.mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            }
            mNotificationManager.notify(NOTIFICATION_ID, mNotifyBuilder.build());
        }

        // Update UI
        Intent intent = new Intent();
        intent.setAction(AppData.BROADCAST_ACTION);
        intent.putExtra(AppData.INTENT_KEY_COUNTDOWN_TICK, true);
        intent.putExtra(AppData.INTENT_KEY_COUNTDOWN_TICK_TIME, this.mRemainingMilliseconds);
        mBroadcaster.sendBroadcast(intent);
    }

    private void onCountdownTimerFinish() {
        Log.d(TAG, "countdown finished");
        this.mRemainingMilliseconds = 0;
        this.startAlarm();

        // Update UI
        Intent intent = new Intent();
        intent.setAction(AppData.BROADCAST_ACTION);
        intent.putExtra(AppData.INTENT_KEY_COUNTDOWN_FINISHED, true);
        mBroadcaster.sendBroadcast(intent);
    }

    /**
     * Kicks off the alarm.
     */
    private void startAlarm() {
        this.mServiceState = CountdownServiceState.BEEPING;
        this.updateStatusBarNotification();
        this.playAlarmSound();
        this.startAlarmTimer();

    }

    /**
     * Update status bar notification when countdown finishes
     */
    private void updateStatusBarNotification() {
        if (mNotifyBuilder != null) {
            // Check the notification manager still sets up before notify the notification update
            if (mNotificationManager != null) {
                this.mNotificationManager = (NotificationManager) getSystemService(
                    Context.NOTIFICATION_SERVICE);
            }

            mNotifyBuilder.setContentText(getString(R.string.detail_timer_notification_finished));
            mNotificationManager.notify(NOTIFICATION_ID, mNotifyBuilder.build());
        }
    }

    /**
     * This timer is needed to send updates to UI.
     */
    private void startAlarmTimer() {
        this.cancelAlarmDurationTimer();

        long maxAlarmDurationMilliseconds = Long.MAX_VALUE;
        this.mMaxAlarmDurationTimer = new CountDownTimer(
            maxAlarmDurationMilliseconds, COUNTDOWN_TICK_INTERVAL) {
            public void onTick(long millisUntilFinished) {
                // Update UI
                Intent intent = new Intent();
                intent.setAction(AppData.BROADCAST_ACTION);
                intent.putExtra(AppData.INTENT_KEY_COUNTDOWN_ALARM_BEEPING, true);
                mBroadcaster.sendBroadcast(intent);
            }

            public void onFinish() {
                if (CountdownService.this.checkState(CountdownServiceState.BEEPING)) {
                    CountdownService.this.cancelAlarmSound();
                    CountdownService.this.mServiceState = CountdownServiceState.FINISHED;
                }
            }
        }.start();
    }

    /**
     * Cancel alarm control timer if it's defined.
     */
    private void cancelAlarmDurationTimer() {
        if (this.mMaxAlarmDurationTimer != null) {
            this.mMaxAlarmDurationTimer.cancel();
            this.mMaxAlarmDurationTimer = null;
        }
    }

    /**
     * Stop alarm sound, cancel alarm control timer and send UI update.
     */
    private void cancelAlarmSound() {
        this.mSoundIsPlaying = false;
        this.cancelAlarmDurationTimer();

        this.stopAlarmSound();

        Intent intent = new Intent();
        intent.setAction(AppData.BROADCAST_ACTION);
        intent.putExtra(AppData.INTENT_KEY_COUNTDOWN_ALARM_STOP, true);
        mBroadcaster.sendBroadcast(intent);
    }

    // MEDIA PLAYER METHODS

    /**
     * Play alarm using Media Player and Audio Manager services.
     */
    void playAlarmSound() {
        if (!this.mSoundIsPlaying) {
            if (this.mMediaPlayer == null) {
                this.mMediaPlayer = new MediaPlayer();
                if (this.checkSound()) {
                    this.mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
                    this.mMediaPlayer.setLooping(true);
                    try {
                        this.mMediaPlayer.prepare();
                    } catch (IllegalStateException | IOException e) {
                        Log.e(TAG, "Could not prepare media player for playback.",
                            e);
                    }
                    if (!this.mMediaPlayer.isPlaying()) {
                        this.mMediaPlayer.start();
                    }
                }
            }
        } else {
            Log.e(TAG, "sound should already be playing");
        }
    }

    void stopAlarmSound() {
        Log.d(TAG, "stopAlarm");
        if (this.mMediaPlayer != null) {
            this.mMediaPlayer.stop();
        }
        this.mMediaPlayer = null;
    }

    /**
     * Checks if the system is prepared to play an alarm sound.
     * @return True if the system is prepared, false if there is any problem.
     */
    private boolean checkSound() {
        final AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (audioManager == null) {
            Log.e(TAG, "Could not get audio manager, sound will not be played.");
            return false;
        }
        if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) == 0) {
            Log.w(TAG, "Volume set to 0, alarm sound will not be played.");
            return false;
        }
        if (this.mMediaPlayer == null) {
            Log.e(TAG,
                "Media player is not initialized, sound will not be played.");
            return false;
        }
        if (!this.loadAlarmSound()) {
            Log.e(TAG,
                "Could not find any alarm sound or could not set the alarm sound as data source in media player, alarm sound will not be played.");
            return false;
        }
        return true;
    }

    private boolean loadAlarmSound() {
        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (!this.setDataSource(alarmUri)) {
            // alert is null or not readable, use notification sound as backup
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            // I don't think this can ever being null (a default notification
            // should always be present ) but just in case
            if (!this
                .setDataSource(alarmUri)) {
                // notification sound is null or not readable, use ringtone as
                // last resort
                alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
                if (!this.setDataSource(alarmUri)) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean setDataSource(Uri alarmUri) {
        if (alarmUri == null) {
            return false;
        }
        try {
            this.mMediaPlayer.setDataSource(this, alarmUri);
            return true;
        } catch (IllegalStateException e) {
            Log.w(TAG, "Could not load the alarm sound from " + alarmUri.toString(), e);
            return false;
        } catch (IOException e) {
            Log.w(TAG, "Could not load the alarm sound from " + alarmUri.toString(), e);
            return false;
        }
    }

}