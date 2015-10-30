package ch.ethz.inf.vs.a3.message;

import java.util.Comparator;

import ch.ethz.inf.vs.a3.clock.VectorClock;
import ch.ethz.inf.vs.a3.solution.message.Message;

/**
 * Message comparator class. Use with PriorityQueue.
 */
public class MessageComparator implements Comparator<Message> {

    @Override
    public int compare(Message lhs, Message rhs) {

        //don't know if the VectorClock actually takes the whole string,
        // or if i have to process it before i setClockFromString
        VectorClock lClock=new VectorClock();
        lClock.setClockFromString(lhs.toString());
        VectorClock rClock=new VectorClock();
        rClock.setClockFromString(rhs.toString());

        if (lClock.happenedBefore(rClock)){
            return 1;
        }
        else if (rClock.happenedBefore(lClock)) return -1;
        else {
            return 0;
        }




    }

}
