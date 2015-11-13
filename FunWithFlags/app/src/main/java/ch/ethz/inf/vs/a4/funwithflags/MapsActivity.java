package ch.ethz.inf.vs.a4.funwithflags;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MapsActivity extends FragmentActivity {

    public static final double MAX_FLAG_VISIBILITY_RANGE = 10; // i think this is in kilometers :)
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private String slideMenuStrings[];
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //just testing the getFlags(). should print a flag in the terminal
        getFlags();

        setContentView(R.layout.activity_maps);
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

                setUpMap();

            }
        }
    }

    // TODO: implement this method, and delete Toasts afterwards
    private void selectItem(int position) {
        switch (position) {
            case 0: // Search
                Toast.makeText(this, "Clicked " + slideMenuStrings[0] ,Toast.LENGTH_SHORT).show();
                break;
            case 1: // Favourites
                Toast.makeText(this, "Clicked " + slideMenuStrings[1] ,Toast.LENGTH_SHORT).show();
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

    private void filterFlagsWithCategoryDialog(final List<Flag> flagsToFilter) {

        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setTitle(R.string.choose_category);
        List<String> types = Category.getallCategoryNames();

        //TODO: add a reset button that resets the filter
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

        Data.filteredCategories.removeAll(Data.filteredCategories);
        Data.filteredCategories.add(c);

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
            alert.setView(input);

            alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    String inputText = input.getText().toString();
                    LatLng currentPosition = getCoordinates();

                    //TODO: get the category here
                    Flag f = new Flag(currentPosition, Category.DEFAULT, inputText, getApplicationContext());
                    f.isOwner = true;
                    Data.allFlags.add(f);
                    displayFlag(f);

                }
            });

            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
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

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        //see other marker options: https://developers.google.com/maps/documentation/android-api/marker
        mMap.clear();

        if (Data.filteredCategories.size() > 0) { //we have already set a filter and keep it that way
            for (Flag f: Data.flagsToShow) {
                displayFlag(f);
            }
        }
        else { //we just started the App and have not yet set a filter
            for (Flag f: Data.allFlags) {
                displayFlag(f);
            }
        }


        //LatLng in degrees, (double, double), ([-90,90],[-180,180])

    }

    private void displayFlag(Flag f) {

        mMap.addMarker(new MarkerOptions().position(f.getLatLng()).title(f.getText()).icon(BitmapDescriptorFactory.defaultMarker(f.getCategory().hue)));

    }

    //PASCAL: ke ahnig wo dir dää code weit, aber i tues iz mau da ine.
    //ANDRES: jo do isch es denkt :)
    void getFlags(){

        //TODO: please add all the retrieved Flags into Data.allFlags()

        /*
        String id = editTextId.getText().toString().trim();
        if (id.equals("")) {
            Toast.makeText(this, "Please enter an id", Toast.LENGTH_LONG).show();
            return;
        }
        loading = ProgressDialog.show(this,"Please wait...","Fetching...",false,false);
        */

        //String url = Config.DATA_URL+editTextId.getText().toString().trim();
        String url="http://hochschultage.ch/getFlags.php";

        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //loading.dismiss();
                String flagID="";
                String userName="";
                String gpsCoordinates = "";
                String categoryName = "";
                String date = "";
                String content = "";

                try {

                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray result = jsonObject.getJSONArray("result");
                    JSONObject flagData = result.getJSONObject(0);
                    flagID=flagData.getString("flagID");
                    userName=flagData.getString("userName");
                    gpsCoordinates=flagData.getString("gpsCoordinates");
                    categoryName=flagData.getString("categoryName");
                    date=flagData.getString("date");
                    content=flagData.getString("content");

                    Log.d("debug", "got flag with flagID "+flagID);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //textViewResult.setText("Name:\t" + name + "\nAddress:\t" + address + "\nVice Chancellor:\t" + vc);
                //showJSON(response);
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MapsActivity.this,error.getMessage().toString(),Toast.LENGTH_LONG).show();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);


/*
        String result = "";

        //http post
        try{
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://example.com/getAllPeopleBornAfter.php");
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            InputStream is = entity.getContent();
        }catch(Exception e){
            Log.e("log_tag", "Error in http connection "+e.toString());
        }
        //convert response to string
        try{
            BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();

            result=sb.toString();
        }catch(Exception e){
            Log.e("log_tag", "Error converting result "+e.toString());
        }

        //parse json data
        try{
            JSONArray jArray = new JSONArray(result);
            for(int i=0;i<jArray.length();i++){
                JSONObject json_data = jArray.getJSONObject(i);
                Log.i("log_tag","id: "+json_data.getInt("id")+
                                ", name: "+json_data.getString("name")+
                                ", sex: "+json_data.getInt("sex")+
                                ", birthyear: "+json_data.getInt("birthyear")
                );
            }
        }
        catch(JSONException e){
            Log.e("log_tag", "Error parsing data "+e.toString());
        }
        */
    }


}
