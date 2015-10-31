package ch.ethz.inf.vs.a3.solution.message;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ch.ethz.inf.vs.a3.clock.Clock;
import ch.ethz.inf.vs.a3.clock.LamportClock;
import ch.ethz.inf.vs.a3.clock.VectorClock;
import ch.ethz.inf.vs.a3.message.MessageTypes;

/**
 * Created by Jimmy on 23/10/15
 *
 * contains the information of the JSON-message
 */
public class Message {

    //Header
    private String username;
    private String uuid;
    private Clock timestamp;
    private String type;

    //Body
    private String content;

    public Message(String username, String uuid, Clock timestamp, String type, String content) {
        this.username = username;
        this.uuid = uuid;
        this.timestamp = timestamp;
        this.type = type;
        this.content = content;
    }

    //TODO PASCAL
    public Message(String toParse){

        this.username = extractFromString("username",toParse);
        this.uuid = extractFromString("uuid", toParse);
        this.timestamp = (new VectorClock());
        this.timestamp.setClockFromString(extractFromString("timestamp",toParse));
        this.type = extractFromString("type",toParse);
        //TODO
        this.content = extractFromString("",toParse);
    }

    String extractFromString(String wanted,String toParse){
        Pattern p = Pattern.compile(String.format("\"%1$s\"\\s*:\\s*\"([^,]*)\"", wanted), Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(toParse);
        if(m.find()) {
            return m.group(1);
        }
        else{ return "";}
    }

    public String getContent(){
        return content;
    }

    public String toString(){
        String name = "\"username\": \"" + username + "\", ";
        String id = "\"uuid\": \"" + uuid + "\", ";
        String stamp;
        if(timestamp != null)
            stamp = "\"timestamp\": \"" + timestamp.toString() + "\", ";
        else
            stamp = "\"timestamp\": \"{}\", ";
        String t = "\"type\": \"" + type + "\" ";
        String header = "\"header\": { " + name + id + stamp + t + "} ";
        String cont;
        if(content != null)
            cont = "\"content\": \"" + content + "\"";
        else
            cont = "";
        String body = "\"body\": {" + cont + "} ";
        return "{ " + header + body + "}";
    }


}
