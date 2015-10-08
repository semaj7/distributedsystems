package ch.ethz.inf.vs.a1.jdermelj.antitheft.vs_jdermelj_antitheft;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by Andres on 06.10.15.
 */
public class AntiTheftServiceImpl extends AbstractAntiTheftService {

    private NotificationManager notificationManager;
    public static final String DEACTIVATION_CODE = "STOPSTOPSTOP!";
    public static final int NOTIFICATION_ID = 42;

    private Ringtone ringTone;
    private AudioManager audioManager;
    private int standardVolume;

    private boolean ringToneRunning = false;

    private CountDownTimer countDownTimer;


    @Override
    public void onCreate() {

        //creates a MovementDetector called listener (without context)
        super.onCreate();

        //set the context of the listener
       ((MovementDetector)listener).setContext(this);

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        audioManager = (AudioManager)getSystemService(AUDIO_SERVICE);

        ringTone = getDefaultRingtone();

        Log.d("asdf","created antitheftservice");


    }

    public Ringtone getDefaultRingtone() {
        //first check, if there is an available alarm tone, then ringtone and if not, a notification tone

        Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);

        if(alert == null){
            // user has not set default alarm
            alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            //again, if user has not set a ringtone:
            if(alert == null) {
                // alert backup is null, using 2nd backup
                alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                //hope, that the user has at least one default notficiation, otherwise we have a problem
            }
        }
        Log.d("asdf","got a ringtone");

        return RingtoneManager.getRingtone(getApplicationContext(), alert);


    }

    private void makeNotification(Context context) {

        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(DEACTIVATION_CODE, true);

        PendingIntent pendingIntent = PendingIntent.getActivity(context,
                NOTIFICATION_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);


        Notification.Builder builder = new Notification.Builder(context)
                .setContentTitle(getString(R.string.notification_title))
                .setContentText(getString(R.string.notification_text))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setSmallIcon(R.mipmap.ic_launcher) //TODO: change these two pictures
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                ;

        Notification n = builder.build();

        //this makes the notification sticky
        n.flags |= Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;

        notificationManager.notify(NOTIFICATION_ID, n);
    }

    @Override
    public void startAlarm() {

        makeNotification(this.getApplicationContext());

     
        int defuseTime = Settings.timeout; //in seconds

        defuseTime = 1000 * defuseTime;

        countDownTimer = new CountDownTimer(defuseTime, 1) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {

                activateRingtone();

            }
        };

        countDownTimer.start();

    }

    private void activateRingtone() {

        if (ringToneRunning || countDownTimer != null) return;

        //save the current volume
        standardVolume = audioManager.getStreamVolume(AudioManager.STREAM_RING);

        //Dreh das Volume VOLL ume!
        /*audioManager.setStreamVolume(AudioManager.STREAM_RING,
                audioManager.getStreamMaxVolume(AudioManager.STREAM_RING), //<- this is the interisting part
                audioManager.FLAG_ALLOW_RINGER_MODES | audioManager.FLAG_PLAY_SOUND);
        */
        //start the ringtone
        ringTone.play();
        ringToneRunning = true;

    }

    private void cancelRingtone() {

        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }

        if (ringTone.isPlaying()) {

            ringTone.stop();
            ringToneRunning = false;

            audioManager.setStreamVolume(AudioManager.STREAM_RING,
                    standardVolume,
                    audioManager.FLAG_ALLOW_RINGER_MODES | audioManager.FLAG_PLAY_SOUND);

        }

    }

    @Override
    public void onDestroy() {
        notificationManager.cancel(NOTIFICATION_ID);

        ((MovementDetector)listener).destroy();
        cancelRingtone();

        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
