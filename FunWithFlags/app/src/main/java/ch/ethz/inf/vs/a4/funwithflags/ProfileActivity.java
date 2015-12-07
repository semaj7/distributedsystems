package ch.ethz.inf.vs.a4.funwithflags;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseUser;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity {

    private SwipeRefreshLayout refresh;
    private int choosenDialogElement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        updateUI();

        refresh = (SwipeRefreshLayout) findViewById(R.id.refresh_profile);
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh.setRefreshing(true);
                updateUI();
                refresh.setRefreshing(false);
            }
        });
    }

    public void showInfoDialog(View v){
        final int id = v.getId();
        final String[] infoToShow;
        final Flag[] correspondingFlags;
        final boolean nothing;

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        choosenDialogElement = -1;

        switch (id){
            case R.id.ratingButton:
                ArrayList<Flag> myFlagsRated = Data.flagsSortedByRating(Data.myFlags);
                infoToShow = new String[myFlagsRated.size()];
                correspondingFlags = new Flag[myFlagsRated.size()];
                for(int i = 0; i< myFlagsRated.size(); i++){
                    correspondingFlags[i] = myFlagsRated.get(i);
                    infoToShow[i] = correspondingFlags[i].getVoteRateAbsolut()+":\n"+correspondingFlags[i].getText();
                }
                if(myFlagsRated.size() == 0)
                    nothing = true;
                else
                    nothing = false;
                alert.setTitle(R.string.ratedFlagsDialogTitle);
                alert.setMessage(R.string.ratedFlagsDialogMessage);
                break;
            case R.id.settedFlagsCountButton:
                infoToShow = new String[Data.myFlags.size()];
                correspondingFlags = new Flag[Data.myFlags.size()];
                ArrayList<Flag> myFlags = Data.quickSortListByDate(Data.myFlags);
                for(int i = 0; i < infoToShow.length;i++){
                    correspondingFlags[i] = myFlags.get(i);
                    infoToShow[i] = myFlags.get(i).getText();
                }
                if(infoToShow.length == 0)
                    nothing = true;
                else
                    nothing = false;
                alert.setTitle(R.string.myFlagsDialogTitle);
                alert.setMessage(R.string.myFlagsDialogMessage);
                break;
            case R.id.followingUsersButton:
                correspondingFlags = new Flag[1];
                infoToShow = new String[Data.followingUsers.size()];
                for(int i = 0 ; i < infoToShow.length; i ++)
                    infoToShow[i] = Data.followingUsers.get(i);
                if(infoToShow.length == 0)
                    nothing = true;
                else
                    nothing = false;
                alert.setTitle(R.string.followingUsersDialogTitle);
                alert.setMessage(R.string.followingUsersDialogMessage);
                break;
            case R.id.followersButton:
                correspondingFlags = new Flag[0];
                // todo: get users that follow this user from server
                infoToShow = new String[0];
                if(infoToShow.length == 0)
                    nothing = true;
                else
                    nothing = false;
                alert.setTitle(R.string.followerUsersDialogTitle);
                alert.setMessage(R.string.followerUsersDialogMessage);
                break;
            default:// should never happen
                infoToShow = new String[]{""};
                correspondingFlags = new Flag[1];
                alert.setTitle("");
                nothing = true;
                break;
        }

        if(nothing){
            Button b = (Button) findViewById(id);
            Toast.makeText(getApplicationContext(),b.getText(),Toast.LENGTH_SHORT ).show();
            return;
        }

        final ListView list = new ListView(this);
        final ArrayAdapter<String> adapter =new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, infoToShow);
        list.setAdapter(adapter);
        list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        list.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.bg_key));
        list.setOnItemClickListener(new ItemHighlighterListener());
        alert.setView(list);

        final Resources res = getResources();
        String positiveText;
        if(id == R.id.ratingButton | id == R.id.settedFlagsCountButton)
            positiveText = String.format(res.getString(R.string.Delete));
        else {
            if (id == R.id.followersButton)
                positiveText = String.format(res.getString(R.string.OK));
            else
                positiveText = String.format(res.getString(R.string.unfollow));
        }
        alert.setPositiveButton(positiveText+"\n", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (choosenDialogElement == -1) {
                    Toast.makeText(getApplicationContext(), String.format(res.getString(R.string.nothingSelectedToDeleteYet)), Toast.LENGTH_SHORT).show();
                } else {
                    if(id == R.id.ratingButton | id == R.id.settedFlagsCountButton) {
                        // we are dealing with flags
                        deleteFlag(correspondingFlags[choosenDialogElement]);
                    }
                    else {
                        // dealing with users
                        if(id == R.id.followingUsersButton){
                            Data.unFollow(infoToShow[choosenDialogElement]);
                            String toastText = res.getString(R.string.unFollowSuccessToast);
                            toastText.replace("@", infoToShow[choosenDialogElement] );
                            Toast.makeText(getApplicationContext(),toastText, Toast.LENGTH_SHORT).show();
                        }
                        else {
                            // do nothing for now, just look at them :)
                        }
                    }
                    dialog.dismiss();
                    updateUI();
                }

            }
        });

        alert.setNegativeButton(String.format(res.getString(R.string.Cancel))+"\n", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alert.show();

    }

    void deleteFlag(Flag f){
        // edit: a flag might also get deleted from a downvote, even when it is not the current users flag. this should not be tested here
        //is user authorized?
        //if(f.getUserName().equals(getCurrentLoggedInUserName())) {
        //delete locally TODO: hope i have not forgotten some place where the flag is stored
        Data.flagMarkerHashMap.remove(f);
        Data.allFlags.remove(f);
        Data.closeFlags.remove(f);
        Data.myFlags.remove(f);
        //if already uploaded: delete also from server
        if (f.getID() != null) {
            Server.deleteFlagFromServer(f);
        }
        //}
    }

    private class ItemHighlighterListener implements AdapterView.OnItemClickListener {

        private View oldSelection = null;

        public void clearSelection() {
            if(oldSelection != null) {
                oldSelection.setBackgroundColor(getResources().getColor(R.color.default_color));
            }
        }

        public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
            clearSelection();
            oldSelection = view;
            view.setBackgroundColor(getResources().getColor(R.color.pressed_color));
            choosenDialogElement = pos;
        }
    }

    public String getUserId() {
        //TODO: get user's name
        if(Data.user != null)
            return Data.user.getUsername();
        Resources res = getResources();
        return res.getString(R.string.user_name_not_found);
    }

    /* no follow button here
    public void followUserClick(View v) {

        //TODO: implement this

        //show Toast if successful
        Resources res = getResources();
        String fU = String.format(res.getString(R.string.followedUser));
        fU = fU.replace("@name", getUserId());
        Toast.makeText(this, fU, Toast.LENGTH_LONG).show();
    }*/

    public void logOut(View v) {
        ParseUser.logOut();
        Toast.makeText(this, "You are logged out now", Toast.LENGTH_SHORT).show();
        finish(); // user is loged out, therefore can no longer be in the profile
    }

    public void updateUI() {

        //TODO: add additional UI changes
        setTitle(getUserId());
        setRatingText();
        setSettedFlagsText();
        setFollowingText();
        setFollowerText();
    }

    private void setFollowerText() {
        int rating = 0;

        Resources res = getResources();
        String ratingText = String.format(res.getString(R.string.followerText));
        ratingText = ratingText.replace("@", String.valueOf(rating));


        Button ratingsButton = (Button) findViewById(R.id.followersButton);
        ratingsButton.setText(ratingText);
    }

    private void setFollowingText() {
        int rating = Data.followingUsers.size();

        Resources res = getResources();
        String ratingText = String.format(res.getString(R.string.followingText));
        ratingText = ratingText.replace("@", String.valueOf(rating));


        Button ratingsButton = (Button) findViewById(R.id.followingUsersButton);
        ratingsButton.setText(ratingText);
    }

    public void setRatingText() {

        int rating = Data.userRating;

        Resources res = getResources();
        String ratingText = String.format(res.getString(R.string.ratingText));
        ratingText = ratingText.replace("@rating", String.valueOf(rating));


        Button ratingsButton = (Button) findViewById(R.id.ratingButton);
        ratingsButton.setText(ratingText);
    }

    public void setSettedFlagsText() {

        int settedFlags = Data.myFlags.size();

        Resources res = getResources();
        String setFText = String.format(res.getString(R.string.settedFlagsText));
        setFText = setFText.replace("@count", String.valueOf(settedFlags));

        Button flagCountButton = (Button) findViewById(R.id.settedFlagsCountButton);
        flagCountButton.setText(setFText);
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
