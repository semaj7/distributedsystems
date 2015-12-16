package ch.ethz.inf.vs.a4.funwithflags;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Andres on 09.12.15.
 */
public class CategoryArrayAdapter extends ArrayAdapter<Category> {

    protected final List<Category> categories;
    HashMap<Integer, Category> mCatMap = new HashMap<>();

    Context context;

    public CategoryArrayAdapter(Context ctx, int textViewResourceId,
                                List<Category> cats) {
        super(ctx, textViewResourceId, cats);
        this.context = ctx;
        this.categories = cats;
        for (int i = 0; i < cats.size(); ++i) {
            mCatMap.put(i, cats.get(i));
        }
    }


    public Category getItemAtPosition(int position) {
        return mCatMap.get(position);
    }


    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.simple_row_category, parent, false);

        TextView categoryTextView = (TextView) rowView.findViewById(R.id.categorySimpleTextView);

        Category currentCategory = categories.get(position);

        //change color of text

        categoryTextView.setText(currentCategory.name);

        categoryTextView.setTextColor(Color.HSVToColor(new float[]{currentCategory.hue, 1f, 1f}));

        return rowView;
    }

}
