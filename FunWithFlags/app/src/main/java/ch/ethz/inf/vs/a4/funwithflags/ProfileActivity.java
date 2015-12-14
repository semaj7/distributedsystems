package ch.ethz.inf.vs.a4.funwithflags;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.Toast;

import com.parse.ParseUser;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity {

    private SwipeRefreshLayout refresh;
    private int choosenDialogElement;
    private boolean ownProfile;
    private ArrayList<Flag> flags;
    private String profilesUsername;
    private Button logoutFollowButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        logoutFollowButton = (Button) findViewById(R.id.logoutFollowButton);

        Intent intent = getIntent();
        profilesUsername = intent.getStringExtra("username");
        if(Data.user == null)
            ownProfile = false;
        else {
            if (profilesUsername.equals(Data.user.getUsername())) {
                ownProfile = true;
            } else {
                ownProfile = false;
            }
        }

        ArrayList<String> temp = new ArrayList<String>();
        temp.add(profilesUsername);
        flags = Data.flagsFrom(temp);


        updateUI();

        refresh = (SwipeRefreshLayout) findViewById(R.id.refresh_profile);
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh.setRefreshing(true);
                updateUI();
                ArrayList<String> temp = new ArrayList<String>();
                temp.add(profilesUsername);
                flags = Data.flagsFrom(temp);
                refresh.setRefreshing(false);
            }
        });
    }

    @Override
    protected void onResume(){
        updateUI();
        super.onResume();
    }

    public void showInfoDialog(View v){
        final int id = v.getId();
        final String[] infoToShow;
        final Flag[] correspondingFlags;
        final boolean nothing;
        final Resources res = getResources();

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        choosenDialogElement = -1;

        switch (id){
            case R.id.ratingButton:
                ArrayList<Flag> myFlagsRated = Data.flagsSortedByRating(flags);
                infoToShow = new String[myFlagsRated.size()];
                correspondingFlags = new Flag[myFlagsRated.size()];
                for(int i = 0; i< myFlagsRated.size(); i++){
                    correspondingFlags[i] = myFlagsRated.get(i);
                    infoToShow[i] = correspondingFlags[i].getVoteRateAbsolut()+":\t\t\t"+correspondingFlags[i].getText();
                }
                if(myFlagsRated.size() == 0)
                    nothing = true;
                else
                    nothing = false;
                if(!ownProfile){

                    String title = res.getString(R.string.ratedFlagsDialogTitleAlienProfile);
                    String usrnm = profilesUsername;
                    if(usrnm.charAt(usrnm.length() - 1) == 's')
                        usrnm += "\'";
                    else
                        usrnm += "\'s";
                    title = title.replace("@user", usrnm);
                    alert.setTitle(title);

                }
                else {
                    alert.setTitle(R.string.ratedFlagsDialogTitle);
                    alert.setMessage(R.string.ratedFlagsDialogMessage);
                }
                break;
            case R.id.settedFlagsCountButton:
                infoToShow = new String[flags.size()];
                correspondingFlags = new Flag[flags.size()];
                ArrayList<Flag> myFlags = Data.quickSortListByDate(flags);
                for(int i = 0; i < infoToShow.length;i++){
                    correspondingFlags[i] = myFlags.get(i);
                    infoToShow[i] = myFlags.get(i).getText();
                }
                if(infoToShow.length == 0)
                    nothing = true;
                else
                    nothing = false;
                if(!ownProfile){

                    String title = res.getString(R.string.myFlagsDialogTitleAlienProfile);
                    String usrnm = profilesUsername;
                    if(usrnm.charAt(usrnm.length()-1 ) == ('s'))
                        usrnm += "\'";
                    else
                        usrnm += "\'s";
                    title = title.replace("@user", usrnm);
                    alert.setTitle(title);

                }
                else {
                    alert.setTitle(R.string.myFlagsDialogTitle);
                    alert.setMessage(R.string.myFlagsDialogMessage);
                }
                break;
            case R.id.followingUsersButton:
                if(!ownProfile) {
                    Toast.makeText(getApplicationContext(), res.getString(R.string.notAllowedToSeeMessage),Toast.LENGTH_SHORT).show();
                    return;
                }
                correspondingFlags = new Flag[1];
                infoToShow = new String[Data.followingUsers.size()];
                for(int i = 0 ; i < infoToShow.length; i ++)
                    infoToShow[i] = Data.followingUsers.get(i);
                if(infoToShow.length == 0)
                    nothing = true;
                else
                    nothing = false;

                if(!ownProfile){

                    String title = res.getString(R.string.followingUsersDialogTitleAlienProfile);
                    title = title.replace("@user", profilesUsername);
                    alert.setTitle(title);

                }
                else
                    alert.setTitle(R.string.followingUsersDialogTitle);
                alert.setMessage(R.string.followingUsersDialogMessage);
                break;
            case R.id.followersButton:
                if(!ownProfile) {
                    Toast.makeText(getApplicationContext(), res.getString(R.string.notAllowedToSeeMessage),Toast.LENGTH_SHORT).show();
                    return;
                }
                correspondingFlags = new Flag[0];
                // todo: get users that follow this user from server
                infoToShow = new String[0];
                if(infoToShow.length == 0)
                    nothing = true;
                else
                    nothing = false;
                if(!ownProfile){

                    String title = res.getString(R.string.followerUsersDialogTitleAlienProfile);
                    title = title.replace("@user", profilesUsername);
                    alert.setTitle(title);

                }
                else
                    alert.setTitle(R.string.followerUsersDialogTitle);
                alert.setMessage(R.string.followerUsersDialogMessage);
                break;
            default:// should never happen
                System.out.println("debug: error! profileactivity in default");
                infoToShow = new String[]{""};
                correspondingFlags = new Flag[1];
                alert.setTitle("");
                nothing = true;
                break;
        }

        if(nothing){
            String toastMessage;
            if(id == R.id.followersButton | id == R.id.followingUsersButton)
                toastMessage = res.getString(R.string.noUsersYet);
            else
                toastMessage = res.getString(R.string.noFlagsYet);
            Toast.makeText(getApplicationContext(),toastMessage,Toast.LENGTH_SHORT ).show();
            return;
        }

        final ListView list = new ListView(this);
        final ArrayAdapter<String> adapter =new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, infoToShow);
        list.setAdapter(adapter);
        list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        list.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.bg_key));
        list.setOnItemClickListener(new ItemHighlighterListener());
        alert.setView(list);

        String positiveText;
        if(id == R.id.ratingButton | id == R.id.settedFlagsCountButton)
            if(ownProfile)
                positiveText = String.format(res.getString(R.string.Delete));
            else
                positiveText = String.format(res.getString(R.string.OK));
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
                    if(ownProfile)
                        Toast.makeText(getApplicationContext(), String.format(res.getString(R.string.nothingSelectedToDeleteYet)), Toast.LENGTH_SHORT).show();
                    else{
                        dialog.dismiss();
                        updateUI();
                        return;
                    }
                } else {
                    if(id == R.id.ratingButton | id == R.id.settedFlagsCountButton) {
                        // we are dealing with flags
                        if(ownProfile)
                            deleteFlag(correspondingFlags[choosenDialogElement]);
                        else{
                            dialog.dismiss();
                            updateUI();
                            return;
                        }
                    }
                    else {
                        // dealing with users
                        if(id == R.id.followingUsersButton){
                            String toastText = res.getString(R.string.unFollowSuccessToast);
                            toastText = toastText.replace("@user", infoToShow[choosenDialogElement] );
                            Data.unFollow(infoToShow[choosenDialogElement]);
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

        if(id == R.id.followersButton | id == R.id.followingUsersButton) {
            alert.setNeutralButton(String.format(res.getString(R.string.show)), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (choosenDialogElement == -1) {
                        Toast.makeText(getApplicationContext(), String.format(res.getString(R.string.nothingSelectedToDeleteYet)), Toast.LENGTH_SHORT).show();
                    } else {
                        if (id == R.id.followingUsersButton) {
                            Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                            intent.putExtra("username", infoToShow[choosenDialogElement]);
                            startActivity(intent);
                        }
                    }
                }
            });
        }

        if(ownProfile)
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
        if(Data.user != null)
            return Data.user.getUsername();
        Resources res = getResources();
        return res.getString(R.string.user_name_not_found);
    }


    public void logOut(View v) {
        if(!ownProfile){
            followUnfollow();
            updateUI();
            return;
        }
        ParseUser.logOut();
        Data.user = null;
        Data.myFlags.clear();
        String logoutMessage = getResources().getString(R.string.logout_message);
        Toast.makeText(this, logoutMessage, Toast.LENGTH_SHORT).show();
        finish(); // user is loged out, therefore can no longer be in the profile
    }

    private void followUnfollow() {
        if(Data.user == null) {
            Toast.makeText(getApplicationContext(), String.format(getResources().getString(R.string.needToBeLogedInForThisMessage)), Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        } else {
            String message;
            if (Data.followingUsers.contains(profilesUsername)) {
                // unfollow
                message = getResources().getString(R.string.unFollowSuccessToast);
                message = message.replace("@user", profilesUsername);
                Toast.makeText(getApplicationContext(), message , Toast.LENGTH_SHORT).show();
                Data.unFollow(profilesUsername);
                Server.unFollow(profilesUsername);
            } else {
                // follow
                message = getResources().getString(R.string.newFollowSuccessToast);
                message = message.replace("@user", profilesUsername);
                Toast.makeText(getApplicationContext(), message , Toast.LENGTH_SHORT).show();
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                Data.follow(profilesUsername);
                Server.follow(profilesUsername);
            }
        }
    }

    public void updateUI() {

        //TODO: add additional UI changes

        // update info in Data
        if(ownProfile) {
            Data.updateMyFlagsFromAll();
            flags = Data.myFlags;
        } else {
            if(Data.followingUsers.contains(profilesUsername))
                logoutFollowButton.setText(getResources().getString(R.string.unfollow));
            else
                logoutFollowButton.setText(getResources().getString(R.string.followUser));
            flags = new ArrayList<Flag>();
            for(Flag f : Data.allFlags){
                if(f.getUserName().equals(profilesUsername)){
                    flags.add(f);
                }
            }
        }

        // update ui
        setTitle(profilesUsername);
        setRatingText();
        setSettedFlagsText();
        setFollowingText();
        setFollowerText();
    }

    private void setFollowerText() {
        int rating = 0;

        Resources res = getResources();
        //String ratingText = String.format(res.getString(R.string.followerText));
        //ratingText = ratingText.replace("@", String.valueOf(rating));

        String ratingText;

        if(rating >= 1000) {
            rating /= 1000;
            ratingText = String.valueOf(rating)+"K";
        } else
            ratingText = String.valueOf(rating);

        if(!ownProfile)
            ratingText = "?";

        Button ratingsButton = (Button) findViewById(R.id.followersButton);
        ratingsButton.setText(ratingText);
        ratingsButton.setTextSize(36);
    }

    private void setFollowingText() {
        int rating = Data.followingUsers.size();

        Resources res = getResources();
        //String ratingText = String.format(res.getString(R.string.followingText));
        //ratingText = ratingText.replace("@", String.valueOf(rating));

        String ratingText;

        if(rating >= 1000) {
            rating /= 1000;
            ratingText = String.valueOf(rating)+"K";
        } else
            ratingText = String.valueOf(rating);

        if(!ownProfile)
            ratingText = "?";

        Button ratingsButton = (Button) findViewById(R.id.followingUsersButton);
        ratingsButton.setText(ratingText);
        ratingsButton.setTextSize(36);
    }

    public void setRatingText() {
        int rating;

        rating = Data.userRating;
        if(!ownProfile){
            rating = 0;
            for(Flag f : flags){
                rating += f.getVoteRateAbsolut();
            }
        }

        Resources res = getResources();
        // String ratingText = String.format(res.getString(R.string.ratingText));
        // ratingText = ratingText.replace("@rating", String.valueOf(rating));

        String ratingText;

        if(rating >= 1000) {
            rating /= 1000;
            ratingText = String.valueOf(rating)+"K";
        } else
            ratingText = String.valueOf(rating);

        Button ratingsButton = (Button) findViewById(R.id.ratingButton);
        ratingsButton.setText(ratingText);
        ratingsButton.setTextSize(36);
    }

    public void setSettedFlagsText() {

        int settedFlags = flags.size();

        Resources res = getResources();
        //String setFText = String.format(res.getString(R.string.settedFlagsText));
        //setFText = setFText.replace("@count", String.valueOf(settedFlags));

        String setFText;

        if(settedFlags >= 1000) {
            settedFlags /= 1000;
            setFText = String.valueOf(settedFlags)+"K";
        } else
            setFText = String.valueOf(settedFlags);


        Button flagCountButton = (Button) findViewById(R.id.settedFlagsCountButton);
        flagCountButton.setText(setFText);
        flagCountButton.setTextSize(36);
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


        return super.onOptionsItemSelected(item);
    }
}
