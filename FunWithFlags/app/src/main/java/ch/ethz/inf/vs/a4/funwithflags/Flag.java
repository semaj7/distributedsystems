package ch.ethz.inf.vs.a4.funwithflags;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Andres on 13.11.15.
 */
public class Flag {

    private LatLng latLng;

    private String text;

    private long ID;

    private Category category;

    public Flag(LatLng latLng, String t){
        this.latLng = latLng;
        this.text = t;

    }

    public LatLng getLatLng() {
        return latLng;
    }

    public String getText() {
        return text;
    }

    public Category getCategory() {
        return category;
    }
}
