package ch.ethz.inf.vs.a4.funwithflags;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import java.util.ArrayList;

/**
 * Created by JG on 18.11.2015.
 */
public class SplashScreen extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        Thread timerThread = new Thread(){
            public void run(){
                try{
                    sleep(2000);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }finally{
                    Intent intent = new Intent(SplashScreen.this, MapsActivity.class);
                    startActivity(intent);
                }
            }
        };
        timerThread.start();
        Server.getFlags(getApplicationContext());
        if(MapsActivity.isLoggedIn()) {
            Server.getFavouritesFromServer(getApplicationContext());
            Server.getFollowingUsers();
            Data.calculateUserRating();
        } else {
            Data.myFlags = new ArrayList<Flag>();
            Data.userRating = 0;
            Data.followingUsers = new ArrayList<String>();
            Data.downvotedFlags = new ArrayList<Flag>();
            Data.upvotedFlags = new ArrayList<Flag>();
            Data.favouriteFlagsList = new ArrayList<Flag>();
        }
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        finish();
    }

}
