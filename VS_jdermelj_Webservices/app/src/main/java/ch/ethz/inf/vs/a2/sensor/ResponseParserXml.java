 package ch.ethz.inf.vs.a2.sensor;

    import android.util.Log;

    import java.util.regex.Matcher;
    import java.util.regex.Pattern;

    import ch.ethz.inf.vs.a2.http.RemoteServerConfiguration;

    /**
     * Parser to extract temperature from XML-Response
     */

    public class ResponseParserXml implements ResponseParser {

        //We are interested in the string between the temperature-tags
        private static final String XML_CODE = "<temperature>(\\S+)</temperature>";

        @Override
        public double parseResponse(String response) {

            if (response != null) {
                Pattern pattern = Pattern.compile(XML_CODE);
                Matcher matcher = pattern.matcher(response);
                if (matcher.find()) {

                    String res = matcher.group(1);
                    return Double.parseDouble(res);
                }
            }
        Log.d("debug", "Response is not in the expected format!");
        return RemoteServerConfiguration.ERROR_TEMPERATURE;
        }
    }
