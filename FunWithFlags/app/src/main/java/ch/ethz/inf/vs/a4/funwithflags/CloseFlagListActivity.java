package ch.ethz.inf.vs.a4.funwithflags;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class CloseFlagListActivity extends Activity {



        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_close_flag_list);

            final ListView listview = (ListView) findViewById(R.id.listview);


            final ArrayList<Flag> flagList = new ArrayList<Flag>(Data.closeFlags);

            final ArrayList<Flag> sortedFlagList = quickSortListByDate(Data.closeFlags);

            final FlagArrayAdapter adapter = new FlagArrayAdapter(this,
                    android.R.layout.simple_list_item_1, flagList);
            listview.setAdapter(adapter);

            listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, final View view,
                                        int position, long id) {
                    final Flag item = (Flag) parent.getItemAtPosition(position);

                    Data.showMeThisCloseFlagPleaseInOtherActivity.add(item);
                    Intent intent = new Intent(CloseFlagListActivity.this, MapsActivity.class);
                    Bundle b = new Bundle();
                    b.putInt("otherActivity", 1); //Your id
                    intent.putExtras(b); //Put your id to your next Intent
                    startActivity(intent);
                    finish();

                    //TODO: show some loading progress because this activity switch takes a long time
                }

            });
        }

    static Random r = new Random();

    private ArrayList<Flag> quickSortListByDate(ArrayList<Flag> closeFlags) {

        if (closeFlags.size() <= 1)
            return closeFlags;
        int rotationplacement = r.nextInt(closeFlags.size());
        Flag rotation = closeFlags.get(rotationplacement);
        closeFlags.remove(rotationplacement);
        ArrayList<Flag> lower = new ArrayList<Flag>();
        ArrayList<Flag> higher = new ArrayList<Flag>();
        for (Flag f : closeFlags)
            if (f.getDate().before(rotation.getDate()))
                lower.add(f);
            else
                higher.add(f);
        quickSortListByDate(lower);
        quickSortListByDate(higher);

        closeFlags.clear();
        closeFlags.addAll(lower);
        closeFlags.add(rotation);
        closeFlags.addAll(higher);
        return closeFlags;
    }


    private class FlagArrayAdapter extends ArrayAdapter<Flag> {

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

                TextView textView = (TextView) rowView.findViewById(R.id.label);
                ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);

                Flag flag = flags.get(position);

                //TODO: change the layout according to flag
                textView.setText(flag.getText());

                /*
                if (s.startsWith("iPhone")) {
                    imageView.setImageResource(R.drawable.no);
                } else {
                    imageView.setImageResource(R.drawable.ok);
                }
                */

                return rowView;
            }

        }

    }
