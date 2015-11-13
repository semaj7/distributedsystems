package ch.ethz.inf.vs.a4.funwithflags;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andres on 13.11.15.
 */
public enum Category {

    ATTRACTION ("Attraction", BitmapDescriptorFactory.HUE_ROSE, 0), //TODO: add all categories here
    SPORT ("Sports", BitmapDescriptorFactory.HUE_GREEN, 1),
    DEFAULT   ("No category", BitmapDescriptorFactory.HUE_ORANGE, 2);


    public final String name;
    public final float hue;
    public final int id;

    Category(String name, float hue, int id) {
        this.name = name;
        this.hue = hue;
        this.id = id;
    }


    public static List<String> getallCategoryNames() {

        ArrayList<String> categoryNames = new ArrayList<String>();
        for (Category c : Category.values()) {
            categoryNames.add(c.name);
        }
        return categoryNames;
    }

}
