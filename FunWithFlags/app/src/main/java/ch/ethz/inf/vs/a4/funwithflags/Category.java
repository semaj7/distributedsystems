package ch.ethz.inf.vs.a4.funwithflags;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andres on 13.11.15.
 */
public enum Category {

    DEFAULT   ("No category", BitmapDescriptorFactory.HUE_RED),
    WORK ("Work", BitmapDescriptorFactory.HUE_VIOLET),
    LANDSCAPE ("Landscape", BitmapDescriptorFactory.HUE_GREEN),
    SPORT ("Sports", BitmapDescriptorFactory.HUE_ORANGE),
    FOOD ("Food", BitmapDescriptorFactory.HUE_MAGENTA),
    LIFESTYLE ("Lifestyle", BitmapDescriptorFactory.HUE_CYAN),
    MYSTERY ("Mystery", BitmapDescriptorFactory.HUE_BLUE),
    TOURISM ("Tourism", BitmapDescriptorFactory.HUE_YELLOW);


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

    public static Category getByName(String name) {
        //TODO: make this design better, this is ugly
        switch (name) {
            case "No category":
                return DEFAULT;

            case "Work":
                return WORK;

            case "Landscape":
                return LANDSCAPE;

            case "Sports":
                return SPORT;

            case "Food":
                return FOOD;

            case "Lifestyle":
                return LIFESTYLE;

            case "Mystery":
                return MYSTERY;

            case "Tourism":
                return TOURISM;

            default:
                return DEFAULT;



        }
    }



    public static List<String> getallCategoryNames() {

        ArrayList<String> categoryNames = new ArrayList<String>();
        for (Category c : Category.values()) {
            categoryNames.add(c.name);
        }
        return categoryNames;
    }

}
