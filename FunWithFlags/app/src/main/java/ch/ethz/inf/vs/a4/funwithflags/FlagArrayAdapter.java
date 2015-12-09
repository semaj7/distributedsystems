package ch.ethz.inf.vs.a4.funwithflags;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Created by Andres on 09.12.15.
 */
public class FlagArrayAdapter extends ArrayAdapter<Flag> {

    private final List<Flag> flags;
    HashMap<Flag, Integer> mIdMap = new HashMap<Flag, Integer>();

    Context context;


    public FlagArrayAdapter(Context context, int textViewResourceId,
                            List<Flag> flags) {
        super(context, textViewResourceId, flags);
        this.context = context;
        this.flags = flags;
        for (int i = 0; i < flags.size(); ++i) {
            mIdMap.put(flags.get(i), i);
        }
    }

    @Override
    public long getItemId(int position) {
        Flag item = getItem(position);
        return mIdMap.get(item);
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.close_flag_row_layout, parent, false);

        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);

        TextView whoPosted = (TextView) rowView.findViewById(R.id.whoPosted);
        TextView whenPosted = (TextView) rowView.findViewById(R.id.whenPosted);
        TextView textOfFlag = (TextView) rowView.findViewById(R.id.textOfFlag);
        // Populate the data into the template view using the data object


        // POPULATE ROW VIEW WITH DATA ACCORDING TO FLAG

        Flag flag = flags.get(position);

        whoPosted.setText(flag.getUserName());
        textOfFlag.setText(flag.getText());


        //add the prettytime class initialized with the display language
        PrettyTime time = new PrettyTime(new Locale(Locale.getDefault().getDisplayLanguage()));
        whenPosted.setText(time.format(flag.getDate()));


        rowView.setBackgroundColor(Color.HSVToColor(new float[]{flag.getCategory().hue, 0.5f, 1f}));


        return rowView;
    }

}
