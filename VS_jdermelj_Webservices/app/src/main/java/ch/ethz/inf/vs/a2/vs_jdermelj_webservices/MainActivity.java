package ch.ethz.inf.vs.a2.vs_jdermelj_webservices;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    //user pressed Button for Task 1
    public void start_activity_1(View view) {
        // Do something in response to button
        Intent intent = new Intent(this, Activity_1.class);
        startActivity(intent);
    }

    //user pressed Button for Task 2
    public void start_activity_2(View view) {
        // Do something in response to button
        Intent intent = new Intent(this, Activity_2.class);
        startActivity(intent);
    }

    //user pressed Button for Task 3
    public void start_activity_3(View view) {
        // Do something in response to button
        Intent intent = new Intent(this, Activity_3.class);
        startActivity(intent);
    }
}
