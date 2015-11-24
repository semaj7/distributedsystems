package ch.ethz.inf.vs.a4.funwithflags;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseUser;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        updateUI();
    }

    public String getUserId() {
        //TODO: get user's name
        if(Data.user != null)
            return Data.user.getUsername();
        Resources res = getResources();
        return res.getString(R.string.user_name_not_found);
    }

    public void followUserClick(View v) {

        //TODO: implement this

        //show Toast if successful
        Resources res = getResources();
        String fU = String.format(res.getString(R.string.followedUser));
        fU = fU.replace("@name", getUserId());
        Toast.makeText(this, fU, Toast.LENGTH_LONG).show();
    }

    public void logOut(View v) {
        ParseUser.logOut();
    }

    public void updateUI() {

        //TODO: add additional UI changes
        setTitle(getUserId());
        setRatingTextView();
        setSettedFlagsTextView();
    }

    public void setRatingTextView() {

        int rating = 0;

        //TODO: set rating from server

        Resources res = getResources();
        String ratingText = String.format(res.getString(R.string.ratingText));
        ratingText = ratingText.replace("@rating", String.valueOf(rating));

        TextView ratingsTextView = (TextView) findViewById(R.id.ratingTextView);
        ratingsTextView.setText(ratingText);
    }

    public void setSettedFlagsTextView() {

        int settedFlags = 0;

        //TODO: setted flags from server

        Resources res = getResources();
        String setFText = String.format(res.getString(R.string.settedFlagsText));
        setFText = setFText.replace("@count", String.valueOf(settedFlags));

        TextView ratingsTextView = (TextView) findViewById(R.id.settedFlagsCountTextView);
        ratingsTextView.setText(setFText);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
