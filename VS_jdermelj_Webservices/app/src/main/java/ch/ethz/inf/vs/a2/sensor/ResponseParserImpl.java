package ch.ethz.inf.vs.a2.sensor;

import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Andres on 13.10.15.
 */

public class ResponseParserImpl implements ResponseParser {

    private static final String REGULAR_EXPRESSION = "<span class=\"getterValue\">(\\S+)</span>";

    @Override
    public double parseResponse(String response) {


        if (response != null) {
            Pattern pattern = Pattern.compile(REGULAR_EXPRESSION);
            Matcher matcher = pattern.matcher(response);
            if (matcher.find()) {

                String res = matcher.group(1);
                return Double.parseDouble(res);
            }
        }

        Log.d("debug", "Response is not in the expected format!");
        return 0;


    }
}