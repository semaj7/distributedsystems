package ch.ethz.inf.vs.a2.vs_jdermelj_webservices;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ServerService extends Service implements SensorEventListener{

    ServerSocket serverSocket;
    int port;
    Thread serverThread;
    ServerTask serverTask;
    Sensor sensor1;
    Sensor sensor2;
    float[] values1;
    float[] values2;
    ExecutorService handler;
    String sensorName1;
    String sensorName2;
    private Vibrator vib = null;
    private MediaPlayer mp;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        port=intent.getExtras().getInt("PORT");
        Log.d("debug",String.valueOf(port));
        handler = Executors.newFixedThreadPool(6);
        serverTask =new ServerTask();
        serverThread = new Thread(serverTask);
        serverThread.start();
        return START_REDELIVER_INTENT;
    }

    @Override
    public void onCreate(){
        //initializing sensors
        SensorManager sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> sensors = sm.getSensorList(Sensor.TYPE_ALL);
        sensor1=sensors.get(0);
        sensorName1=sensor1.getName();
        sensor2=sensors.get(2);
        sensorName2=sensor2.getName();
        sm.registerListener((SensorEventListener) this, sensor1, SensorManager.SENSOR_DELAY_NORMAL);
        sm.registerListener((SensorEventListener) this, sensor2, SensorManager.SENSOR_DELAY_NORMAL);
    }

    String getSensorHtml(int i){
        if(i==0){
            return "<html><h1>"+sensorName1+"</h1><p>value 1: "+String.valueOf(values1[0])+"</p><p>value 2: "+String.valueOf(values1[1])+"</p><a href='../'>back to the dashboard!</a></html>";
        }
        else
        {
            return "<html><h1>"+sensorName2+"</h1><p>value 1: "+String.valueOf(values2[0])+"</p><p>value 2 of sensor 2: "+String.valueOf(values2[1])+"</p><a href='../'>back to the dashboard!</a></html>";
        }
    }

    void playSound(int volume){
        Log.d("sound","awesome music playing with volume "+String.valueOf(volume)+" and in reality "+String.valueOf((float)(Math.min(volume / 100.0, 1.0f))));
        mp = MediaPlayer.create(this, R.raw.sound);
        mp.setVolume((float)(Math.min(volume / 100.0, 1.0f)),	(float)(Math.min(volume / 100.0, 1.0f)));
        mp.start();
    }

    void vibrate(int duration){
        Vibrator vib	=	(Vibrator)	getSystemService(VIBRATOR_SERVICE);
        vib.vibrate(duration);

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getName().equals(sensor1.getName()))
        {
            values1=event.values;
        }
        else
        {
            values2=event.values;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    public class ServerTask implements Runnable {

        boolean running=true;

        public void run() {
            try {
                serverSocket=new ServerSocket(port);

                Log.d("debug","soon be listening");

                while (running) {
                    Log.d("debug","i'm o be' listening foo clients");
                    // LISTEN FOR INCOMING CLIENTS
                    Socket clientSocket = serverSocket.accept();
                    Log.d("debug", "there was a client!");
                    handler.submit(new Client(clientSocket));
                }
                Log.d("debug", "done with listening");
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        public void stop(){
            if (serverSocket!=null){
                try{
                    serverSocket.close();
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
            running=false;
        }
    }


    @Override
    public void onDestroy() {
        serverTask.stop();
        serverThread.interrupt();

        handler.shutdown();
        try {
            handler.awaitTermination(2, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private class Client implements Runnable {
        Socket socket;
        int start;
        int end;
        String request;

        public Client(Socket clientSocket) {
            socket=clientSocket;
        }

        @Override
        public void run() {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String line = null;

                line = in.readLine();
                if(line!=null) {
                    start = line.indexOf("/");
                    end = line.indexOf(" ", start + 1);
                    request = line.substring(start + 1, end);
                }
                Log.d("debug", "request was:" + line);
                String reply=null;
                if(request!=null) {
                    if (request.equals("sensor1")||request.equals("sensor1/")) {
                        reply = getSensorHtml(0);
                    } else if (request.equals("sensor2")||request.equals("sensor2/")) {
                        Log.d("debug","yeyy! finally sensor 2 working as well");
                        reply = getSensorHtml(1);
                    } else if (request.equals("sound")||request.equals("sound/")) {
                        int volume=30;
                        Log.d("debug","someone yo wann heaa some muusic");

                        for(String line2=in.readLine();line2!=null;line2=in.readLine()){
                            Log.d("debug",line2);
                            if (line2.contains("volume")){
                                volume= Integer.valueOf(line2.substring(line2.indexOf("=")+1));
                                break;
                            }
                        }
                        playSound(volume);
                        Log.d("debug", "heyyyo, everything is fine");

                    } else if (request.equals("vibration")||request.equals("vibration/")) {
                        int duration=10;
                        for(String line2=in.readLine();line2!=null;line2=in.readLine()){

                            Log.d("debug",line2);
                            if (line2.contains("duration")){
                                duration= Integer.valueOf(line2.substring(line2.indexOf("=")+1));
                                break;
                            }
                        }
                        vibrate(duration);
                    }
                }
                if (reply==null){
                    reply = "<html><body><h1>Welcome!</h1><p><b>The best feature of this App: it will only play the sound / vibrate after you click twice.</b></p>" +
                            "<p>For Information about the "+ sensorName1 + " click <a href='/sensor1'>here</a>. </p>" +
                            "<p>For Information about the "+ sensorName2 + " click <a href='/sensor2'>here</a>. </p>" +
                            "<p>Bet you wanna hear awesome soound! <form action='/sound' method='post'> Enter the volume: <input type='text' name='volume'><input type='submit' value='Submit'></form></p>" +
                            "<p>Bet you wanna feel the ground shakin'! <form action='/vibration' method='post'> Enter the duration: <input type='text' name='duration'><input type='submit' value='Submit'></form></p>" +
                            "</body></html>";
                }
                PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                out.println(reply);
                socket.close();
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
    }
}
