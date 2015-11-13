package ch.ethz.inf.vs.a4.funwithflags;

import android.content.res.Resources;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;

/**
 * Created by Andres on 13.11.15.
 */
public class SportsCategory implements Category {

    private Resources res;
    public SportsCategory(Resources res) {
        this.res = res;
    }
    @Override
    public String getCategoryName() {

        return String.format(res.getString(R.string.SportsName));
    }

    @Override
    public float getHue() {
        return BitmapDescriptorFactory.HUE_GREEN;
    }
}
