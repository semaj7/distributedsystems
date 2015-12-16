package ch.ethz.inf.vs.a4.funwithflags;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Andres on 03.11.15.
 */
public class GPSTracker extends Service implements LocationListener {

    private final Context mContext;

    // flag for GPS status
    boolean isGPSEnabled = false;

    // flag for network status
    boolean isNetworkEnabled = false;

    boolean canGetLocation = false;

    Location location; // location
    double latitude; // latitude
    double longitude; // longitude

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 10 * 1; // 10 seconds (for train

    // Declaring a Location Manager
    protected LocationManager locationManager;
    private MapsActivity mapsActivity;

    public GPSTracker(Context context, MapsActivity mapsActivity) {
        this.mContext = context;
        this.mapsActivity = mapsActivity;
        getLocation();
    }


    public double getLatitude() {
        if (location != null) {
            latitude = location.getLatitude();
        }

        // return latitude
        return latitude;
    }

    public double getLongitude() {
        if (location != null) {
            longitude = location.getLongitude();
        }

        // return longitude
        return longitude;
    }

    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    /**
     * Function to show settings alert dialog
     * */

    private static AtomicBoolean alreadyShowingAlert = new AtomicBoolean(false);
    public void showSettingsAlert() {

        if (alreadyShowingAlert.compareAndSet(false, true)) {

            Resources res = mContext.getResources();
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

            // Setting Dialog Title
            String slaveString = res.getString(R.string.gpsAlertDialogTitle);
            alertDialog.setTitle(slaveString);

            // Setting Dialog Message
            slaveString = res.getString(R.string.gpsAlertDialogMessage);
            alertDialog.setMessage(slaveString);

            // Setting Icon to Dialog
            //alertDialog.setIcon(R.drawable.delete);

            // On pressing Settings button
            slaveString = res.getString(R.string.action_settings);
            alertDialog.setPositiveButton(slaveString, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    alreadyShowingAlert.set(false);
                    dialog.cancel();
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    mContext.startActivity(intent);


                }
            });

            // on pressing cancel button
            slaveString = res.getString(R.string.Cancel);
            alertDialog.setNegativeButton(slaveString, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    alreadyShowingAlert.set(false);
                    dialog.cancel();
                }
            });

            // Showing Alert Message
            alertDialog.show();
        }
    }

    public Location getLocation() {
        try {
            locationManager = (LocationManager) mContext
                    .getSystemService(LOCATION_SERVICE);

            // getting GPS status
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
                showSettingsAlert();
            } else {
                this.canGetLocation = true;
                // First get location from Network Provider
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.d("Network", "Network");
                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        Log.d("GPS Enabled", "GPS Enabled");
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                }

                Data.lastLocation = location;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return location;
    }


    @Override
    public void onLocationChanged(Location location) {
        this.location = location;

        Data.lastLocation = location;

        if (mapsActivity != null) {
            mapsActivity.locationChanged();

        }
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }
}