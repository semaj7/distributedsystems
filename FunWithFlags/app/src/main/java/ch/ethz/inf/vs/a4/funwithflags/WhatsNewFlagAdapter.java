package ch.ethz.inf.vs.a4.funwithflags;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.List;
import java.util.Locale;

/**
 * Created by Andreas on 13.12.2015.
 */
public class WhatsNewFlagAdapter extends FlagArrayAdapter {
    public WhatsNewFlagAdapter(Context context, int textViewResourceId, List<Flag> flags) {
        super(context, textViewResourceId, flags);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.whats_new_popup, parent, false);

        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);

        //TextView whoPosted = (TextView) rowView.findViewById(R.id.whoPosted);
        TextView whenPosted = (TextView) rowView.findViewById(R.id.whenPosted);
        TextView textOfFlag = (TextView) rowView.findViewById(R.id.textOfFlag);
        // Populate the data into the template view using the data object


        // POPULATE ROW VIEW WITH DATA ACCORDING TO FLAG

        final Flag flag = flags.get(position);

        //whoPosted.setText(flag.getUserName());

        Resources res = context.getResources();
        String posterText = res.getString(R.string.user_posted_flag);
        posterText = posterText.replace("@user", flag.getUserName());
        textOfFlag.setText(posterText);


        //add the prettytime class initialized with the display language
        PrettyTime time = new PrettyTime(new Locale(Locale.getDefault().getDisplayLanguage()));
        whenPosted.setText(time.format(flag.getDate()));


        rowView.setBackgroundColor(Color.HSVToColor(new float[]{flag.getCategory().hue, 0.5f, 1f}));

        rowView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (context instanceof MapsActivity) {
                    MapsActivity maps = (MapsActivity) context;
                    maps.selectedFlag(flag);
                }
            }

        });

        return rowView;
    }
}
