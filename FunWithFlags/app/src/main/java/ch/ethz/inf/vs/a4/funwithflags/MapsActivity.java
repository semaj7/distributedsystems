package ch.ethz.inf.vs.a4.funwithflags;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.ocpsoft.prettytime.PrettyTime;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class MapsActivity extends AppCompatActivity {

    public static final double MAX_FLAG_VISIBILITY_RANGE = 0.5; // kilometers

    //TODO: this constant shouldn't be a constant. the distance is dependent on the maximal zoom level of the current position
    public static final double MAX_FLAG_OVERLAPPING_KM = 0.005; // in kilometers, so its 5 meters

    public static final float NON_CAMERA_MOVEMENT_TIMEOUT = 2.5f;
    public static final int TOP_RANKED_FLAGS_AMOUNT = 4;//TODO: discuss this number together, also should the top ranked flag's content always be visible? this could give some insight on what a good flag should contain, also it is quite unlickely that someone would travel the world for some random good ranked flags, just to see their content..
    public static final int MAX_NUMBER_OF_FAVOURITES = 4; // a low value makes testing easier :)
    private static final int FAVOURITE_DIALOG = 0;
    private static final int RANKING_DIALOG = 1;
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private String slideMenuStrings[];
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private ListView mDrawerList;
    private Button showAllButton;
    private ImageButton profileButton, addFlagButton;
    private PopupWindow flagPopUpWindow, closeByFlagsPopUpWindow, whatsNewPopUpWindow;
    private Circle circle_visible_range;
    private GPSTracker gps;
    private SwipeRefreshLayout refresh;
    private ImageView whitescreen;
    private ActionBar toolbar;
    private int favouriteNRtoDelete;
    private MenuItem eye;
    private boolean resumeHasRun;
    private boolean updating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // view's
        setContentView(R.layout.activity_maps);

        showAllButton = (Button) findViewById(R.id.showAllButton);
        addFlagButton = (ImageButton) findViewById(R.id.newFlagButton);
        profileButton = (ImageButton) findViewById(R.id.profileButton);
        whitescreen = (ImageView) findViewById(R.id.whitescreen);
        whitescreen.setVisibility(View.INVISIBLE);

        gps = new GPSTracker(this, this);


        refresh = (SwipeRefreshLayout) findViewById(R.id.refresh);
        refresh.setEnabled(false);


        // compat action bar
        toolbar = getSupportActionBar();
        toolbar.setTitle(R.string.app_name);
        toolbar.setDisplayShowHomeEnabled(true);
        //toolbar.setLogo(R.drawable.logo);
        //toolbar.setDisplayUseLogoEnabled(true);

        // initialize Navigation Drawer
        slideMenuStrings = Data.slideMenuStrings;
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, slideMenuStrings));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_setting_dark, R.string.drawer_open, R.string.drawer_close){
           public void onDrawerClosed(View view){
               super.onDrawerClosed(view);
               if(toolbar.getTitle().equals(getString(R.string.options)))
                   toolbar.setTitle(R.string.app_name);
               else {
                   // let it as it is :)
               }
           }

            public void onDrawerOpened(View drawerView){
                super.onDrawerOpened(drawerView);
                toolbar.setTitle(R.string.options);
            }
        };
        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        toolbar.setDisplayHomeAsUpEnabled(true);
        toolbar.setHomeButtonEnabled(true);


        // map
        setUpMapIfNeeded();
        locationChanged();
        mMap.setOnMapLongClickListener(new MapLongClickListenerRefresh());
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        refresh();
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }


    private class MapLongClickListenerRefresh implements GoogleMap.OnMapLongClickListener {
        // this is pretty stupid... but sadly the only way i found to do it for now
        @Override
        public void onMapLongClick(LatLng latLng) {
            Toast.makeText(getApplicationContext(), "Refreshing...", Toast.LENGTH_SHORT).show();
            refresh.setRefreshing(true);

            Vibrator vib = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vib.vibrate(125); // give some haptic feedback, so that user knows he long-clicked
            refresh();
        }
    }

    private void refresh() {
        // feedback


        // do everything we need to do to refresh
        ParseQuery<ParseObject> flagQuery = new ParseQuery<ParseObject>("Flag");
        flagQuery.setLimit(1000);
        flagQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> flags, com.parse.ParseException e) {
                if (e == null) {
                    ArrayList<Flag> ret = new ArrayList<Flag>();

                    Flag f;
                    for (int i = 0; i < flags.size(); i++) {

                        f = Server.parseFlagToFlag(getApplicationContext(), flags.get(i));

                        ret.add(f);
                    }

                    Data.dataSetChanged(ret);
                }

                setUpMap();

            }

        });

        if(isLoggedIn()) {
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
            Data.favouriteFlags = new Flag[MAX_NUMBER_OF_FAVOURITES];
        }

        setUpMapIfNeeded();
        if(refresh.isRefreshing())
            refresh.setRefreshing(false);
    }



    private void showWhitescreen(String title){
        whitescreen.setVisibility(View.VISIBLE);
        eye.setVisible(false);
        toolbar.setDisplayShowHomeEnabled(false);
        toolbar.setDisplayHomeAsUpEnabled(false);
        toolbar.setHomeButtonEnabled(false);
        toolbar.setTitle(title);
        profileButton.setVisibility(View.INVISIBLE);
        addFlagButton.setVisibility(View.INVISIBLE);
        if(Data.filteringEnabled.size() >= 1)
            showAllButton.setVisibility(View.INVISIBLE);
    }

    private void hideWhitescreen() {
        whitescreen.setVisibility(View.INVISIBLE);
        eye.setVisible(true);
        toolbar.setDisplayShowHomeEnabled(true);
        toolbar.setDisplayHomeAsUpEnabled(true);
        toolbar.setHomeButtonEnabled(true);
        toolbar.setTitle(R.string.app_name);
        profileButton.setVisibility(View.VISIBLE);
        addFlagButton.setVisibility(View.VISIBLE);
        if(Data.filteringEnabled.size() >= 1)
            showAllButton.setVisibility(View.VISIBLE);
    }





    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    public void popUpBackgroundClicked(View view) {
        onBackPressed();
    }

    public void doNothing(View view) {
        //be true to your word
    }

    @Override
    public void onBackPressed() {
        boolean closedSomething = false;
        if (flagPopUpWindow != null) {
            if (flagPopUpWindow.isShowing()) {
                flagPopUpWindow.dismiss();
                closedSomething = true;
            }
        }
        if (closeByFlagsPopUpWindow != null) {
            if (closeByFlagsPopUpWindow.isShowing()) {
                closeByFlagsPopUpWindow.dismiss();
                hideWhitescreen();
                closedSomething = true;
            }
        }
        if (whatsNewPopUpWindow != null){
            if (whatsNewPopUpWindow.isShowing()){
                whatsNewPopUpWindow.dismiss();
                hideWhitescreen();
                closedSomething = true;
            }
        }
        if (!closedSomething) super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_map, menu);

        eye = menu.findItem(R.id.close_info);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
            // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                mMap.setMyLocationEnabled(true);

                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        // get flags close to marker that got clicked
                        LatLng markerPos = marker.getPosition();
                        List<Flag> flagsAtApproxPosition = filterFlagsByApproximatePositions(Data.allFlags, markerPos);
                        chooseFlagTextDialog(flagsAtApproxPosition);

                        return true;
                    }
                });

                setUpMap();

            }
            else {

                //TODO: SHOW USER WHAT TO DO IF NO GOOGLE PLAY SERVICES ARE INSTALLED!
            }
        } else {
            setUpMap();
        }
    }

    public void goToMarker(Flag f) {

        Marker m = Data.flagMarkerHashMap.get(f);
        if (m != null) {
            LatLng latLng = m.getPosition();
       /*     CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 10);
            mMap.animateCamera(cameraUpdate); */


            CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng)      // Sets the center of the map to Mountain View
                .zoom(15)                   // Sets the zoom
                .bearing(0)                // Sets the orientation of the camera to east
                .build();                   // Creates a CameraPosition from the builder
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }

       // popUpFlag(f);
    }

    //this should get called if the user moves 10m or all 30 seconds
    public void locationChanged() {

        System.out.println("Noticed a location change!");

        //add a circle to display how far a user can see
        addCircleToCurrentDestination((int) (MAX_FLAG_VISIBILITY_RANGE * 1000));

    }


    private void searchAndFilterByUserNameDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.search_username_title);

        // Set up the input
        final EditText input = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String searchUserName = input.getText().toString();
                if (!searchUserName.isEmpty()) {
                    Data.flagsToShow.removeAll(Data.flagsToShow);
                    Data.flagsToShow.addAll(filterFlagsByUserName(Data.allFlags, searchUserName));
                    Data.filteringEnabled.removeAll(Data.filteringEnabled);
                    //just add a category to show that we are filtering
                    Data.filteringEnabled.add(Category.DEFAULT);
                    setUpMap();
                }
            }
        });
        builder.setNegativeButton(R.string.decline, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }


    private void chooseFlagTextDialog(final List<Flag> closeByFlags) {


        final List<Flag> visibleCloseFlags = new ArrayList<Flag>();
        for(Flag f: closeByFlags){
            if(f.isVisible()){
                visibleCloseFlags.add(f);
            }
        }

        if(visibleCloseFlags.size() > 0) {

            //only do this if we have actually more than 1 flag to select from
            if (visibleCloseFlags.size() > 1) {
                // Set up the array to display the flag texts
                final String[] flagEntries = new String[visibleCloseFlags.size()];
                for (int i = 0; i < visibleCloseFlags.size(); i++) {
                    flagEntries[i] = visibleCloseFlags.get(i).getText();
                }

                //build and show dialog
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle(R.string.closeByFlagsDialogTitle);

                //alert.setMessage(R.string.closeByFlagsDialogMessage);
                alert.setItems(flagEntries, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichEntry) {
                        System.out.println("DEBUG: markerListener, onClick: clicked entry nr: " + whichEntry);
                        // todo: do what ever we want to do with the clicked "flag"(text)
                        goToMarker(visibleCloseFlags.get(whichEntry));
                        popUpFlag(visibleCloseFlags.get(whichEntry));
                        dialog.dismiss();
                    }
                });
                alert.show();
            } else if (visibleCloseFlags.size() == 1) {
                goToMarker(visibleCloseFlags.get(0));
                popUpFlag(visibleCloseFlags.get(0));
            }
        } else {
            goToMarker(closeByFlags.get(0));
            notInRangePopUp();
        }
    }

    public void notInRangePopUp(){
        View popupView = getLayoutInflater().inflate(R.layout.not_in_range_popup, null);
        flagPopUpWindow = new PopupWindow(popupView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        //this method shows the popup, the first param is just an anchor, passing in the view
        //we inflated is fine
        flagPopUpWindow.setAnimationStyle(R.style.animation);
        flagPopUpWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
    }

    private List<Flag> filterFlagsByUserName(List<Flag> flagsToFilter, String userName) {
        //don't filter if the username to filter is null or empty
        if (userName == null) return flagsToFilter;
        if (userName.isEmpty()) return  flagsToFilter;

        List<Flag> filteredFlags = new ArrayList<Flag>();
        for (Flag f: flagsToFilter) {
            if (f.getUserName().equals(userName))
                filteredFlags.add(f);

        }
        return filteredFlags;
    }

    private List<Flag> filterFlagsByApproximatePositions(List<Flag> InitialFlags, LatLng position) {
        List<Flag> resultList = new ArrayList<Flag>();

        // todo: finding a way not to check every single flag on the whole map would be nice. but i don't know if we can do that
        for(Flag f : InitialFlags){
            double flagLat = f.getLatLng().latitude;
            double flagLon = f.getLatLng().longitude;
            double posLat = position.latitude;
            double posLon = position.longitude;

            ParseGeoPoint positionGeoPoint = new ParseGeoPoint(posLat, posLon);
            ParseGeoPoint flagGeoPoint = new ParseGeoPoint(flagLat, flagLon);

            if (positionGeoPoint.distanceInKilometersTo(flagGeoPoint) < MapsActivity.MAX_FLAG_OVERLAPPING_KM)
                resultList.add(f);
        }

        return resultList;
    }

    // TODO: implement this method, and delete Toasts afterwards
    private void selectItem(int position) {
        switch (position) {
            case 0: // Search
                searchAndFilterByUserNameDialog();
                mDrawerLayout.closeDrawers();
                break;
            case 1: // Favourites
                displayDialog(FAVOURITE_DIALOG);
                mDrawerLayout.closeDrawers();
                break;
            case 2: // Filters
                filterFlagsWithCategoryDialog(Data.allFlags);
                mDrawerLayout.closeDrawers();
                break;
            case 3: // Ranking
                displayDialog(RANKING_DIALOG);
                mDrawerLayout.closeDrawers();
                break;
            case 4: // what's new
                whatsNewPopup();
                mDrawerLayout.closeDrawers();
                break;
            default: // Settings
                mDrawerLayout.closeDrawers();
                startActivity(new Intent(this, SettingsActivity.class));

                break;
        }
    }

    private void whatsNewPopup() {
        if(isLoggedIn()) {

            showWhitescreen(getString(R.string.whatsNew));

            View popupView = getLayoutInflater().inflate(R.layout.activity_close_flag_list, null);
            //this method shows the popup, the first param is just an anchor, passing in the view
            //we inflated is fine

            final ListView listview = (ListView) popupView.findViewById(R.id.listview);

            final ArrayList<Flag> sortedFlagList = Data.quickSortListByDate(Data.flagsFrom(Data.followingUsers));

            final WhatsNewFlagAdapter adapter = new WhatsNewFlagAdapter(this,
                    android.R.layout.simple_list_item_1, sortedFlagList);
            listview.setAdapter(adapter);

            listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    final Flag item = (Flag) parent.getItemAtPosition(position);

                    whatsNewPopUpWindow.dismiss();
                    hideWhitescreen();
                    goToMarker(item);
                    popUpFlag(item);
                }
            });


            whatsNewPopUpWindow = new PopupWindow(popupView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

            whatsNewPopUpWindow.setAnimationStyle(R.style.animation);
            whatsNewPopUpWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
        } else {
            String toastmessage = getString(R.string.notAllowedToSeeMessage);
            Toast.makeText(getApplicationContext(), toastmessage, Toast.LENGTH_SHORT).show();
        }

    }

    public void openCloseFlagsPopUp(MenuItem m) {
        showWhitescreen(getString(R.string.closeByFlagsDialogTitle));
        Data.updateCloseFlagsFromAll();
        
        View popupView = getLayoutInflater().inflate(R.layout.activity_close_flag_list, null);

        //init controls
        final ListView listview = (ListView) popupView.findViewById(R.id.listview);

        final ArrayList<Flag> sortedFlagList = Data.quickSortListByDate(Data.closeFlags);

        final FlagArrayAdapter adapter = new FlagArrayAdapter(this,
                android.R.layout.simple_list_item_1, sortedFlagList);
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                final Flag item = (Flag) parent.getItemAtPosition(position);

                closeByFlagsPopUpWindow.dismiss();
                hideWhitescreen();
                goToMarker(item);
                popUpFlag(item);

            }

        });

        closeByFlagsPopUpWindow = new PopupWindow(popupView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        //this method shows the popup, the first param is just an anchor, passing in the view
        //we inflated is fine
        closeByFlagsPopUpWindow.setAnimationStyle(R.style.animation);
        closeByFlagsPopUpWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);

    }



    private void displayDialog(final int whatKind) {

        Resources res = getResources();
        String nothingThereYet;
        String title;
        Flag[] flagData;
        final List<Flag> nonNullFlagData = new ArrayList<Flag>();
        String[] flagDataText;

        switch (whatKind) {

            case FAVOURITE_DIALOG: // todo: only assign stuff that is different in switch, and then avoid code duplication
                nothingThereYet = String.format(res.getString(R.string.noFavourtieYet));
                title = String.format(res.getString(R.string.favouriteDisplayDialogTitle));
                flagData = Data.favouriteFlags;
                flagDataText = new String[flagData.length];
                for(int i =0; i< flagData.length; i ++){
                    if(Data.ithFavourite(i) != null) {
                        flagDataText[i] = Data.ithFavourite(i).getText();
                        nonNullFlagData.add(Data.ithFavourite(i));
                    }
                }
                break;

            case RANKING_DIALOG:
                nothingThereYet = String.format(res.getString(R.string.noTopFlagsYet));
                title = String.format(res.getString(R.string.topRankingDisplayDialogTitle));
                flagData = Data.topRankedFlags;
                flagDataText = new String[flagData.length];
                for(int i = 0; i < flagData.length; i ++){
                    if(Data.ithRanked(i) != null) {
                        Flag f = Data.ithRanked(i);
                        String time = f.getDate().toGMTString();
                        flagDataText[i] =
                                        "#" + String.valueOf(i+1)+ "\n" +
                                        res.getString(R.string.userName) + ": " + f.getUserName() + "\n" +
                                        res.getString(R.string.time) + ": " + time + "\n" +
                                        res.getString(R.string.upVoteRatio) + ": " + f.getVoteRateAbsolut() + "\n" +
                                        res.getString(R.string.category) + ": " + f.getCategory().name + "\n" ;
                        nonNullFlagData.add(Data.ithRanked(i));
                    }
                }
                break;

            default :
                nothingThereYet = "Wrong usage of method";
                title = nothingThereYet;
                flagData = null;
                flagDataText = null;
                break;

        }
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setTitle(title);

        final boolean[] empty = new boolean[1];
        String[] entries;
        int size = 0;
        for (int i = 0; i < flagDataText.length; i++) {
            if (flagDataText[i] != null)
                size++;
        }
        if (size == 0) {
            entries = new String[1];
            entries[0] = nothingThereYet;
            empty[0] = true;
        } else {
            entries = new String[size];

            for (int i = 0; i < size; i++) {
                entries[i] = flagDataText[i];
            }
        }

        b.setItems(entries, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int whichFlag) {
                // todo: do something with selected flag.. right now, show it :)
                if (!empty[0]) {
                    goToMarker(nonNullFlagData.get(whichFlag));
                    popUpFlag(nonNullFlagData.get(whichFlag));
                }
                dialog.dismiss();

            }

        });

        b.show();
    }


    private void filterFlagsWithCategoryDialog(final List<Flag> flagsToFilter) {

        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setTitle(R.string.choose_category);
        List<String> types = Category.getallCategoryNames();

        String[] cat_names = types.toArray(new String[types.size()]);
        b.setItems(cat_names, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int whichCategory) {

                List<Flag> filteredFlags = filterFlagsByCategory(flagsToFilter, Category.values()[whichCategory]);
                Data.flagsToShow.removeAll(Data.flagsToShow);
                Data.flagsToShow.addAll(filteredFlags);
                setUpMap();
                dialog.dismiss();

            }

        });

        b.show();

    }

    private List<Flag> filterFlagsByCategory(List<Flag> flagsToFilter, Category c) {

        Data.filteringEnabled.removeAll(Data.filteringEnabled);

        if (c == Category.DEFAULT) return flagsToFilter; //Do not filter if someone selects DEFAULT category

        Data.filteringEnabled.add(c);
        ArrayList<Flag> flagsThatAreInCategory = new ArrayList<Flag>();

        for (Flag f: flagsToFilter) {
            if(f.getCategory() == c)
                flagsThatAreInCategory.add(f);
        }
        return flagsThatAreInCategory;


    }


    public static boolean isLoggedIn() {

        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            // do stuff with the user
            Data.user = currentUser;

            System.out.println("username: "+ Data.user.getUsername());
            return true;
        } else {
            // show the signup or login screen
            return false;
        }

    }

    public void addCircleToCurrentDestination(int radius_in_meters){

        //remove old circle
        if (circle_visible_range != null) circle_visible_range.remove();

        LatLng coord = getCoordinates();

        System.out.print("location: " + coord.latitude+ ", " + coord.longitude);

        CircleOptions circleOptions = new CircleOptions()
                .center(getCoordinates())

                // Fill color of the circle
                // 0x represents, this is an hexadecimal code
                // 50 represents percentage of transparency. For 100% transparency, specify 00.
                // For 0% transparency ( ie, opaque ) , specify ff
                // The remaining 6 characters(00ff00) specify the fill color
                .fillColor(0x5000A68B)

                // Border color of the circle
                .strokeColor(0x50003AA1)

                .radius(radius_in_meters); // In meters


        // Get back the mutable Circle
        circle_visible_range = mMap.addCircle(circleOptions);
    }

    public void setNewFlagClick(View v){

        if (isLoggedIn()) {


            AlertDialog.Builder alert = new AlertDialog.Builder(this);

            alert.setTitle(R.string.newFlagDialogTitle);
            alert.setMessage(R.string.newFlagDialogMessage);

            // Set an EditText view to get user input
            final EditText input = new EditText(this);
            input.setHint(R.string.flag_text_hint);

            final Spinner categorySpinner = new Spinner(this);
            List<String> types = Category.getallCategoryNames();
            String[] cat_names = types.toArray(new String[types.size()]);
            ArrayAdapter<String> adapter =new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, cat_names);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            categorySpinner.setAdapter(adapter);
            final Category[] cat = new Category[1];
            categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    switch (position) {
                        case 0:
                            cat[0] = Category.DEFAULT;
                            break;
                        case 1:
                            cat[0] = Category.WORK;
                            break;
                        case 2:
                            cat[0] = Category.LANDSCAPE;
                            break;
                        case 3:
                            cat[0] = Category.SPORT;
                            break;
                        case 4:
                            cat[0] = Category.FOOD;
                            break;
                        case 5:
                            cat[0] = Category.LIFESTYLE;
                            break;
                        case 6:
                            cat[0] = Category.MYSTERY;
                            break;
                        case 7:
                            cat[0] = Category.TOURISM;
                            break;
                        default:
                            cat[0] = Category.DEFAULT;
                            break;
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    cat[0] = Category.DEFAULT;
                }
            });

            LinearLayout layout = new LinearLayout(getApplicationContext());
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.addView(input);
            final TextView textView = new TextView(this);
            textView.setText("\n\t\t Select a category:");
            layout.addView(textView);
            layout.addView(categorySpinner);
            alert.setView(layout);

            Resources res = getResources();
            AlertDialog.Builder builder = alert.setPositiveButton(String.format(res.getString(R.string.OK)), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int whichButton) {
                    String inputText = input.getText().toString();
                    LatLng currentPosition = getCoordinates();


                    //TODO: get the category here
                    Flag f = new Flag(null,getCurrentLoggedInUserName(), inputText, currentPosition, cat[0], new Timestamp(System.currentTimeMillis()), getApplicationContext());
                    //TODO: get the flags ID somewhere
                    // f.setID(ID);
                    Server.submitFlag(f);


                    f.isOwner = true;
                    addToData(f);
                    Data.myFlags.add(f);
                    displayFlag(f);
                }
            });

            alert.setNegativeButton(String.format(res.getString(R.string.Cancel)), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    // Canceled.
                }
            });

            alert.show();

        }

        else {

            switchToLogin();
        }

    }

    private String getCurrentLoggedInUserName() {
        //before executing this, check if user is logged in!

        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            return currentUser.getUsername();
        } else {
            //this should almost never happen, because before executing getCurrentLoggedInUserName, one should always check if the user is logged in
            //but it could happen that the user is logged out right after isLoggedIn() is checked
            //TODO: handle this error with an alert or exit the app gracefully
            return "DefaultUser";
        }


    }

    public LatLng getCoordinates() {

        double lon;
        double lat;

        //TODO: change to something meaningful

        if (gps.canGetLocation()) {
            lat = gps.getLatitude();
            lon = gps.getLongitude();

        }

        else {
            gps.showSettingsAlert();
            lat = RandomFloat( -90, 90 );
            lon = RandomFloat( -180, 180 );
        }

        System.out.println("Lat:" + lat);
        System.out.println("Lon:" + lon);

    //    Toast.makeText(this, "Set flag at Lat:" + lat + "Lon:" + lon, Toast.LENGTH_LONG).show();

        return new LatLng(lat, lon);
    }

    // TODO: delete this method in the end, when we no longer need it
    float RandomFloat(int a, int b) {
        Random random = new Random(System.currentTimeMillis());
        double floatval = Math.random();

        System.out.println("Random float:" + floatval);

        double rand = (floatval * (b - a)) + a;

        System.out.println("Random result:" + rand);

        return (float) rand;
    }

    public void switchToLogin() {

        Intent newIntent = new Intent(this, LoginActivity.class);
        startActivity(newIntent);

    }

    public void switchToAlienOrOwnProfile(View v) {
        if (v instanceof TextView) {
            String username = ((TextView) v).getText().toString();

            if (isLoggedIn()) {
                if (getCurrentLoggedInUserName().equals(username)) {
                    switchToProfile(v);
                }
                else {
                    switchToAlienProfile(username);
                }
            }
            else {
                switchToAlienProfile(username);
            }

        }
    }

    public void switchToAlienProfile(String username){
        Intent newIntent = new Intent(this, ProfileActivity.class);
        newIntent.putExtra("username", username);
        startActivity(newIntent);
    }

    public void switchToProfile(View v) {

        if(isLoggedIn()) {
            Data.calculateUserRating();
            Intent newIntent = new Intent(this, ProfileActivity.class);
            newIntent.putExtra("username", getCurrentLoggedInUserName());
            startActivity(newIntent);
        } else {
            startActivity(new Intent(this, LoginActivity.class));
        }
    }

    public void showAllFlags(View v) {
        Data.filteringEnabled.removeAll(Data.filteringEnabled);
        setUpMap();
    }

    private void popUpFlag(final Flag f) {
        //todo: only show follow button if user does not already follow that user, or at least tell him he already follows that user, if we always show it.
        //inflate the popup layout we just created, make sure the name is correct
        if(f.isVisible()){

            //only invoke this if visible

            View popupView = getLayoutInflater().inflate(R.layout.flag_popup, null);
            //init controls

            //--- Text --- //
            TextView text = (TextView) popupView.findViewById(R.id.flagText);
            text.setText(f.getText());

            //--- RATING --- //
            final TextView ratingTv = (TextView) popupView.findViewById(R.id.ratingTextView);
            ratingTv.setText(String.valueOf(f.getVoteRateAbsolut()));

            //--- Username --- //
            TextView username = (TextView) popupView.findViewById(R.id.placeholderUsername);
            username.setText(f.getUserName());

            //--- Category --- //
            TextView categoryTv = (TextView) popupView.findViewById(R.id.catTextView);
            categoryTv.setText(f.getCategory().name);

            //--- Time --- //
            final TextView whenTv = (TextView) popupView.findViewById(R.id.whenTextView);
            PrettyTime time = new PrettyTime(new Locale(Locale.getDefault().getDisplayLanguage()));
            whenTv.setText(time.format(f.getDate()));

            // -- Background Image -- //
            ImageView smallPopup = (ImageView) popupView.findViewById(R.id.imageView);
            float s=(float)0.5;
            float v= (float) 0.97;
            float h= f.getCategory().hue;
            float hsv[];
            hsv=new float[]{h, s, v};
            smallPopup.setBackgroundColor(Color.HSVToColor(hsv));

            // -- Other Buttons -- //

            final ImageButton favouriteFlagButton = (ImageButton) popupView.findViewById(R.id.addFavourite);
            if(Data.containsFlag(f, Data.favouriteFlags)) {
                favouriteFlagButton.setImageDrawable(getApplicationContext().getResources().getDrawable(R.drawable.ic_action_favorite_red2));

            } else {
                favouriteFlagButton.setImageDrawable(getApplicationContext().getResources().getDrawable(R.drawable.ic_action_favorite));

            }
            final ImageButton followUserButton = (ImageButton) popupView.findViewById(R.id.followUserFromFlag);
            if(Data.followingUsers.contains(f.getUserName())){
                // user already follows this user, so show unfollow icon
                followUserButton.setImageDrawable(getApplicationContext().getResources().getDrawable(R.drawable.ic_action_remove));
            }
            ImageButton upVoteButton = (ImageButton) popupView.findViewById(R.id.upVoteButton);
            ImageButton downVoteButton = (ImageButton) popupView.findViewById(R.id.downVoteButton);
            ImageButton toProfileButton = (ImageButton) popupView.findViewById(R.id.toProfileButton);

            //straightforward, now handle the onclick listeners for all buttons
            final Resources res = getResources();
            upVoteButton.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    if (!isLoggedIn()) {
                        Toast.makeText(getApplicationContext(), String.format(res.getString(R.string.needToBeLogedInForThisMessage)), Toast.LENGTH_SHORT).show();
                        switchToLogin();
                    } else {
                        f.upVote();
                        System.out.println("debug, got upvoted, now is at: " + f.getVoteRateAbsolut());
                        ratingTv.setText(String.valueOf(f.getVoteRateAbsolut()));
                        Toast.makeText(getApplicationContext(), String.format(res.getString(R.string.upVoteSuccessToast)), Toast.LENGTH_SHORT).show();
                    }
                }
            });
            downVoteButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (!isLoggedIn()) {
                        Toast.makeText(getApplicationContext(), String.format(res.getString(R.string.needToBeLogedInForThisMessage)), Toast.LENGTH_SHORT).show();
                        switchToLogin();
                    } else {
                        if (f.downVote()) {
                            // ratio too bad, delete this flag
                            deleteFlag(f);
                            flagPopUpWindow.dismiss();
                            refresh();
                        } else {
                            System.out.println("debug, got downvoted, now is at: " + f.getVoteRateAbsolut());
                        }
                        ratingTv.setText(String.valueOf(f.getVoteRateAbsolut()));
                        Toast.makeText(getApplicationContext(), String.format(res.getString(R.string.downVoteSuccessToast)), Toast.LENGTH_SHORT).show();
                    }
                }
            });

            followUserButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if(!isLoggedIn()) {
                        Toast.makeText(getApplicationContext(), String.format(res.getString(R.string.needToBeLogedInForThisMessage)), Toast.LENGTH_SHORT).show();
                        switchToLogin();
                    } else {
                        /*// follow
                        Data.follow(f.getUserName());

                        followUserButton.setImageDrawable(getApplicationContext().getResources().getDrawable(R.drawable.ic_action_remove));
                        String toastMessage = String.format(res.getString(R.string.newFollowSuccessToast));
                        toastMessage = toastMessage.replace("@", f.getUserName());
                        Toast.makeText(getApplicationContext(), toastMessage, Toast.LENGTH_SHORT).show();*/
                        if (Data.followingUsers.contains(f.getUserName())) {
                            // unfollow
                            Data.unFollow(f.getUserName());
                            Server.unFollow(f.getUserName());
                            followUserButton.setImageDrawable(getApplicationContext().getResources().getDrawable(R.drawable.ic_action_add_person));
                            String toastMessage = String.format(res.getString(R.string.unFollowSuccessToast));
                            toastMessage = toastMessage.replace("@user", f.getUserName());
                            Toast.makeText(getApplicationContext(), toastMessage, Toast.LENGTH_SHORT).show();
                        } else {
                            // follow
                            Data.follow(f.getUserName());
                            Server.follow(f.getUserName());
                            // todo: julia: do au ersetze met unfollow bitte :)
                            followUserButton.setImageDrawable(getApplicationContext().getResources().getDrawable(R.drawable.ic_action_remove));
                            String toastMessage = String.format(res.getString(R.string.newFollowSuccessToast));
                            toastMessage = toastMessage.replace("@", f.getUserName());
                            Toast.makeText(getApplicationContext(), toastMessage, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });

            favouriteFlagButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!isLoggedIn()) {
                        Toast.makeText(getApplicationContext(), String.format(res.getString(R.string.needToBeLogedInForThisMessage)), Toast.LENGTH_SHORT).show();
                        switchToLogin();
                        return;
                    }
                    if (Data.containsFlag(f, Data.favouriteFlags)) {
                        // user wants to unfavourite the flag
                        Data.deleteFavouriteFlag(f);

                        favouriteFlagButton.setImageDrawable(getApplicationContext().getResources().getDrawable(R.drawable.ic_action_favorite));
                        Toast.makeText(getApplicationContext(), String.format(res.getString(R.string.unFavouriteSuccessToast)), Toast.LENGTH_SHORT).show();
                    } else {
                        if (!Data.addFavourite(f)) {
                            // Favourites was full, and f was not added, the user should delete one of his favourite flags first
                            System.out.println("debug, fav was full should now show dialog");
                            deleteFavouriteFlagDialogAndReplace(f);
                            favouriteFlagButton.setImageDrawable(getApplicationContext().getResources().getDrawable(R.drawable.ic_action_favorite_red2));
                        }
                        if(Data.containsFlag(f, Data.favouriteFlags)) {
                            Toast.makeText(getApplicationContext(), String.format(res.getString(R.string.newFavouriteSuccessToast)), Toast.LENGTH_SHORT).show();

                            favouriteFlagButton.setImageDrawable(getApplicationContext().getResources().getDrawable(R.drawable.ic_action_favorite_red2));


                        }
                    }
                }
            });
            toProfileButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switchToAlienProfile(f.getUserName());
                }
            });


            // CREATE ! //

            flagPopUpWindow = new PopupWindow(popupView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
            //this method shows the popup, the first param is just an anchor, passing in the view
            //we inflated is fine
            flagPopUpWindow.setAnimationStyle(R.style.animation);
            flagPopUpWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
        } else {
            // flag is not visible -> show not in range popup

            notInRangePopUp();
        }

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
            favouriteNRtoDelete = pos;
        }
    }

    private void deleteFavouriteFlagDialogAndReplace(final Flag flag) {

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(R.string.deleteFavouriteDialogTitle);
        alert.setMessage(R.string.deleteFavouriteDialogMessage);

        String[] favouriteText = new String[MAX_NUMBER_OF_FAVOURITES];
        for(int i = 0 ; i < MAX_NUMBER_OF_FAVOURITES; i ++){
            if(Data.favouriteFlags[i] != null )
                favouriteText[i] = Data.favouriteFlags[i].getText();
            else // should theoretically never happen
                favouriteText[i] = "";
        }
        favouriteNRtoDelete = -1;
        final Resources res = getResources();

        final ListView favs = new ListView(this);
        final ArrayAdapter<String> adapter =new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, favouriteText);
        favs.setAdapter(adapter);
        favs.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        favs.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.bg_key));
        favs.setOnItemClickListener(new ItemHighlighterListener());
        alert.setView(favs);

        alert.setPositiveButton(String.format(res.getString(R.string.Delete)), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (favouriteNRtoDelete == -1) {
                    Toast.makeText(getApplicationContext(), String.format(res.getString(R.string.noFavourtieSelectedToDeleteYet)), Toast.LENGTH_SHORT).show();
                } else {
                    if (Data.deleteIthFavourite(favouriteNRtoDelete))
                        if(Data.addFavourite(flag))
                            dialog.dismiss();
                        else{
                            // something went wrong :/
                            System.out.println("Debug, unable to add new favourite");
                        }
                    else {
                        // something went wrong :/
                        System.out.println("Debug, unable to delete favourite");
                    }
                }

            }
        });

        alert.setNegativeButton(String.format(res.getString(R.string.Cancel)), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alert.show();

    }



    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        //see other marker options: https://developers.google.com/maps/documentation/android-api/marker
        mMap.clear();

        if (Data.filteringEnabled.size() > 0) { //we have already set a filter and keep it that way

            showAllButton.setVisibility(View.VISIBLE);
            Toast.makeText(this, "Displaying " + Data.flagsToShow.size() + " flags after filtering." ,Toast.LENGTH_SHORT).show();

            for (Flag f : Data.flagsToShow) {
                displayFlag(f);
            }
        }
        else { //we just started the App and have not yet set a filter
            showAllButton.setVisibility(View.INVISIBLE);
            for (Flag f: Data.allFlags) {
                displayFlag(f);
            }
        }


        //LatLng in degrees, (double, double), ([-90,90],[-180,180])

    }
    private void getFlags(){
        Server.getFlags(this);
        setUpMapIfNeeded();
    }

    private void addToData(Flag f) {
        Data.allFlags.add(f);
        if (!Data.filteringEnabled.isEmpty()) {
            if (Data.filteringEnabled.contains(f.getCategory())) {
                Data.flagsToShow.add(f);
            }
        }
    }

    private void displayFlag(Flag f) {

        Marker m = mMap.addMarker(new MarkerOptions()
                        .position(f.getLatLng())
                        .title(f.getText())
                        .icon(BitmapDescriptorFactory.defaultMarker(f.getCategory().hue))
                        .alpha(f.getAlpha())
        );
        Data.flagMarkerHashMap.put(f, m);
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




}
