package ch.ethz.inf.vs.a4.funwithflags;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andres on 13.11.15.
 */
public enum Category {

    ATTRACTION ("Attraction", BitmapDescriptorFactory.HUE_ROSE), //TODO: add all categories here
    SPORT ("Sports", BitmapDescriptorFactory.HUE_GREEN),
    DEFAULT   ("No category", BitmapDescriptorFactory.HUE_ORANGE);


    public final String name;
    public final float hue;


    Category(String name, float hue) {
        this.name = name;
        this.hue = hue;
    }

    public static Category getByIndex(int i) {
        if (i < 0 || i >= Category.values().length ) return null;
        return Category.values()[i];
    }


    public static List<String> getallCategoryNames() {

        ArrayList<String> categoryNames = new ArrayList<String>();
        for (Category c : Category.values()) {
            categoryNames.add(c.name);
        }
        return categoryNames;
    }

}
