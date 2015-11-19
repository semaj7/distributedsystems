package ch.ethz.inf.vs.a4.funwithflags;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.text.InputType;
import android.util.Log;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MapsActivity extends FragmentActivity {

    public static final double MAX_FLAG_VISIBILITY_RANGE = 0.5; // kilometers

    //TODO: this constant shouldn't be a constant. the distance is dependent on the maximal zoom level of the current position
    public static final double MAX_FLAG_OVERLAPPING_KM = 0.005; // in kilometers, so its 5 meters

    public static final int MAX_NUMBER_OF_FAVOURITES = 20;
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private String slideMenuStrings[];
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private Button showAllButton;
    private PopupWindow flagPopUpWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //just testing the getFlags(). should print a flag in the terminal
        getFlags();


        //testing
        ParseObject testObject = new ParseObject("TestObject");
        testObject.put("foo", "bar");
        testObject.saveInBackground();

        setContentView(R.layout.activity_maps);

        showAllButton = (Button) findViewById(R.id.showAllButton);

        setUpMapIfNeeded();



        slideMenuStrings = Data.slideMenuStrings; // have done this a bit nicer
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // Set the adapter for the list view
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, slideMenuStrings));
        // Set the list's click listener
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
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

    @Override
    public void onBackPressed() {
        if(flagPopUpWindow.isShowing())
        {
            flagPopUpWindow.dismiss();
        }
        else
        {
            super.onBackPressed();
        }

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

                        //this is used to show the text on top of the marker
                        marker.showInfoWindow();
                        return true;
                    }
                });

                setUpMap();

            }
        }
    }

    private void makeButtonDraggable(Button b) {

        //TODO: fix this, this does not work properly yet
        b.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    ClipData clipData = ClipData.newPlainText("", "");
                    View.DragShadowBuilder dsb = new View.DragShadowBuilder(view);
                    view.startDrag(clipData, dsb, view, 0);
                    view.setVisibility(View.INVISIBLE);
                    return true;
                } else {
                    return false;
                }
            }
        });

        b.setOnDragListener(new View.OnDragListener() {
            private boolean containsDragable;
            @Override
            public boolean onDrag(View view, DragEvent dragEvent) {
                int dragAction = dragEvent.getAction();
                View dragView = (View) dragEvent.getLocalState();
                if (dragAction == DragEvent.ACTION_DRAG_EXITED) {
                    containsDragable = false;
                } else if (dragAction == DragEvent.ACTION_DRAG_ENTERED) {
                    containsDragable = true;
                } else if (dragAction == DragEvent.ACTION_DRAG_ENDED) {
                    if (dropEventNotHandled(dragEvent)) {
                        dragView.setVisibility(View.VISIBLE);
                    }
                } else if (dragAction == DragEvent.ACTION_DROP && containsDragable) {
                    dragView.setVisibility(View.VISIBLE);
                }
                return true;
            }

            private boolean dropEventNotHandled(DragEvent dragEvent) {
                return !dragEvent.getResult();
            }
        });


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

        //only do this if we have actually more than 1 flag to select from
        if (closeByFlags.size() > 1){
            // Set up the array to display the flag texts
            final String[] flagEntries = new String[closeByFlags.size()];
            for (int i = 0; i < closeByFlags.size(); i++) {
                flagEntries[i] = closeByFlags.get(i).getText();
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
                    popUpFlag(closeByFlags.get(whichEntry));
                    dialog.dismiss();
                }
            });
            alert.show();
        }
        else if (closeByFlags.size() == 1) {
            popUpFlag(closeByFlags.get(0));
        }
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
                break;
            case 1: // Favourites
                favouriteDisplayDialog();
                break;
            case 2: // Filters
                filterFlagsWithCategoryDialog(Data.allFlags);
                break;
            case 3: // Ranking
                Toast.makeText(this, "Clicked " + slideMenuStrings[3] ,Toast.LENGTH_SHORT).show();
                break;
            case 4: // what's new
                Toast.makeText(this, "Clicked " + slideMenuStrings[4] ,Toast.LENGTH_SHORT).show();
                break;
            default: // Settings
                Toast.makeText(this, "Clicked " + slideMenuStrings[5] ,Toast.LENGTH_SHORT).show();

                break;
        }
    }

    private void favouriteDisplayDialog() {

        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setTitle(R.string.favouriteDisplayDialogTitle);

        String[] favFlagEntries;
        int size = 0;
        for (int i = 0; i < MAX_NUMBER_OF_FAVOURITES; i++) {
            if (Data.favouriteFlags[i] != null)
                size++;
        }
        if (size == 0) {
            Resources res = getResources();
            String noFavYet = String.format(res.getString(R.string.noFavourtieYet));
            favFlagEntries = new String[1];
            favFlagEntries[0] = noFavYet;
        } else {
            favFlagEntries = new String[size];

            for (int i = 0; i < size; i++) {
                favFlagEntries[i] = Data.ithFavourite(i).getText();
            }
        }

        b.setItems(favFlagEntries, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int whichFavourite) {
                Toast.makeText(getApplicationContext(), "looks like your favourite flag says: " + Data.ithFavourite(whichFavourite).getText(), Toast.LENGTH_SHORT).show();
                // todo: do something with selected favourite's flag
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


    public boolean isLoggedIn() {

        //TODO: implement this
/*
        float n = RandomFloat(0, 1);
        if( n >= 0.5f)
            return true;
        return false; */

        return true;
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
                    Flag f = new Flag("No_ID_before_committed",getCurrentLoggedInUserName(), inputText, currentPosition, cat[0], new Timestamp(System.currentTimeMillis()), getApplicationContext());
                    submitFlag(f);


                    f.isOwner = true;
                    addToData(f);
                    displayFlag(f);



                    if(!Data.addFavourite(f)) // todo: this is just for testing, remove when adding to favourites is implemented
                        Toast.makeText(getApplicationContext(), "could not add to favourites", Toast.LENGTH_SHORT).show();

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
        //TODO: check what the current Username is

        return "Homo";
    }

    public LatLng getCoordinates() {

        double lon;
        double lat;

        //TODO: change to something meaningful


        GPSTracker gps = new GPSTracker(this);
        if(gps.canGetLocation()){

            //TODO: this might also use outdated coordinates, check if they're old
            lat = gps.getLatitude(); // returns latitude
            lon = gps.getLongitude();
        }

        else {
            gps.showSettingsAlert();
            lat = RandomFloat( -90, 90 );
            lon = RandomFloat( -180, 180 );
        }

        Log.d("DEBUG", String.valueOf(lat));
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

    public void switchToProfile(View v) {

        Intent newIntent = new Intent(this, ProfileActivity.class);
        startActivity(newIntent);

    }

    public void showAllFlags(View v) {
        Data.filteringEnabled.removeAll(Data.filteringEnabled);
        setUpMap();
    }

    private void popUpFlag(final Flag f) {
        //inflate the popup layout we just created, make sure the name is correct
        View popupView = getLayoutInflater().inflate(R.layout.flag_popup, null);
        //init controls
        TextView text = (TextView) popupView.findViewById(R.id.flagText);
        text.setText(f.getText());
        final Button followUserButton = (Button) popupView.findViewById(R.id.followUserFromFlag);
        Button upVoteButton = (Button) popupView.findViewById(R.id.upVoteButton);
        Button downVoteButton = (Button) popupView.findViewById(R.id.downVoteButton);
        //straightforward, now handle the onclick listeners for all buttons
        upVoteButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                //TODO: do whatever upvote should do
                flagPopUpWindow.dismiss();
            }
        });
        downVoteButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //TODO: do whatever downvote should do
                flagPopUpWindow.dismiss();
            }
        });

        followUserButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                followUser(f.getUserName());
                flagPopUpWindow.dismiss();
            }
        });

        flagPopUpWindow = new PopupWindow(popupView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        //this method shows the popup, the first param is just an anchor, passing in the view
        //we inflated is fine
        flagPopUpWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);

    }

    public void followUser(String userName){
        //TODO: implement this
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

            for (Flag f: Data.flagsToShow) {
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

    private void addToData(Flag f) {

        Data.allFlags.add(f);
        if (!Data.filteringEnabled.isEmpty()) {
            if (Data.filteringEnabled.contains(f.getCategory())) {
                Data.flagsToShow.add(f);
            }
        }
    }

    private void displayFlag(Flag f) {

        mMap.addMarker(new MarkerOptions()
                        .position(f.getLatLng())
                        .title(f.getText())
                        .icon(BitmapDescriptorFactory.defaultMarker(f.getCategory().hue))
                        .alpha(f.getAlpha())
        );
    }

    //PASCAL: ke ahnig wo dir dää code weit, aber i tues iz mau da ine.
    //ANDRES: jo do isch es denkt :)
    void getFlags(){

        //TODO: please add all the retrieved Flags into Data.allFlags()

        ParseQuery<ParseObject> flagQuery=new ParseQuery<ParseObject>("Flag");
        flagQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> flags, com.parse.ParseException e) {
                if (e == null) {
                    ArrayList<Flag> ret = new ArrayList<Flag>();
                    String ID;
                    String userName;
                    String text;
                    LatLng latLng;
                    Category category;
                    Timestamp date;

                    ParseGeoPoint geoPoint;

                    for (int i = 0; i < flags.size(); i++) {
                            /*
                            from the report:
                            Flags(flagId:Int, userName:String, content:String, latitude:Int,
                            longitude:Int, categoryName:String, date:Date)
                            */
                        ID = (String) flags.get(i).getObjectId();
                        userName = (String) flags.get(i).get("userName");
                        text = (String) flags.get(i).get("content");
                        geoPoint = (ParseGeoPoint) flags.get(i).get("geoPoint");
                        latLng = new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude());
                        category = Category.valueOf((String) flags.get(i).get("categoryName"));
                        date = (Timestamp) flags.get(i).get("date");

                        ret.add(new Flag(ID, userName, text, latLng, category, date, getApplicationContext()));
                    }
                    Data.setAllFlags(ret);
                }
            }

        });
        // commented out next line in order to run the code

        //ParseQuery


    }

    void submitFlag(Flag f){

        /*
        From the Report:

        Flags(flagId:Int, userName:String, content:String, latitude:Int,
                longitude:Int, categoryName:String, date:Date)
        */

        final ParseObject parseFlag = new ParseObject("Flag");


        //parseFlag.put("flagId",f. TODO);
        parseFlag.put("userName",f.getUserName());
        parseFlag.put("content",f.getText());
        parseFlag.put("geoPoint",new ParseGeoPoint(f.getLatLng().latitude, f.getLatLng().longitude));
        parseFlag.put("categoryName",f.getCategory().name);
        parseFlag.put("date", f.getDate());
        parseFlag.saveInBackground();

    }

    void deleteFlagFromServer(Flag f){
        ParseQuery<ParseObject> flagQuery=new ParseQuery<ParseObject>("Flag");
        flagQuery.getInBackground(f.getID(), new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, com.parse.ParseException e) {
                try {
                    object.delete();
                } catch (com.parse.ParseException e1) {
                    e1.printStackTrace();
                }
            }
        });


    }

    void deleteFlag(Flag f){
        // TODO: 17.11.15

    }


}
