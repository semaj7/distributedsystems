package ch.inf.ethz.vs.a1.jdermelj.sensors;

import android.media.MediaPlayer;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

public class ActuatorsActivity extends AppCompatActivity {

    Button button;
    private Vibrator vib = null;
    private int duration = 50;
    private MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actuators);
        button = (Button) findViewById(R.id.button);

        vib = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        SeekBar seekDuration = (SeekBar) findViewById(R.id.seek_duration);
        seekDuration.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                duration = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                vib.vibrate(duration*10);
            }
        });
    }

    public void onClickVibrate(View v)	{
        Vibrator vib	=	(Vibrator)	getSystemService(VIBRATOR_SERVICE);
        vib.vibrate(10*duration);

    }

    public void onClickSound(View	v)	{
        mp	=	MediaPlayer.create(this, R.raw.sound);
        mp.setVolume(1.0f,	1.0f);
        mp.start();
    }
}
