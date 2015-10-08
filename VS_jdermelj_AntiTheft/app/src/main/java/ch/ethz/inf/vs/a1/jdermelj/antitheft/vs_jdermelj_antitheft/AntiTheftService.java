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
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by Andres on 06.10.15.
 */
public class AntiTheftService extends AbstractAntiTheftService {

    private NotificationManager notificationManager;
    public static final String DEACTIVATION_SOURCE = "ch.ethz.inf.vs.a1.jdermelj.antitheft.stop";
    public static final int NOTIFICATION_ID = 42;

    private Ringtone ringTone;
    private AudioManager audioManager;
    private int standardVolume;

    private boolean ringToneRunning = false;


    @Override
    public void onCreate() {
        super.onCreate();

       ((MovementDetector)listener).setContext(this);

        audioManager = (AudioManager)getSystemService(AUDIO_SERVICE);

        ringTone = getDefaultRingtone();

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
        return RingtoneManager.getRingtone(getApplicationContext(), alert);
    }

    private void makeNotification(Context context) {

        Intent intent = new Intent(context, MainActivity.class);

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

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, n);
    }

    @Override
    public void startAlarm() {

        makeNotification(this.getApplicationContext());

     
        int defuseTime = Settings.timeout;

        //TODO: make a countdown that delays the activation of the ringtone
        activateRingtone();

    }

    private void activateRingtone() {

        if (ringToneRunning) return;

        //save the current volume
        standardVolume = audioManager.getStreamVolume(AudioManager.STREAM_RING);

        //Dreh das Volume VOLL ume!
        audioManager.setStreamVolume(AudioManager.STREAM_RING,
                audioManager.getStreamMaxVolume(AudioManager.STREAM_RING), //<- this is the interisting part
                audioManager.FLAG_ALLOW_RINGER_MODES | audioManager.FLAG_PLAY_SOUND);

        //start the ringtone
        ringTone.play();
        ringToneRunning = true;

    }

    private void cancelRingtone() {

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
        notificationManager.cancel(42);

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
