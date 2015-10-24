package ch.ethz.inf.vs.a3.clock;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;



public class VectorClock implements Clock {

	// For each process id associate a logical time

	private Map<Integer, Integer> vector;

	public VectorClock() {
		vector = new HashMap<Integer, Integer>();
	}

	//better than doing toString() and setClockFromString() or changing vector to public
	public Map<Integer, Integer> getVector() {
		return vector;
	}

	@Override
	public void update(Clock other) {

		if (other instanceof VectorClock) {

			VectorClock otherClock = (VectorClock) other;
			Map<Integer, Integer> otherVector = otherClock.getVector();

			Iterator it = otherVector.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry pair = (Map.Entry)it.next();

				int pid = (int) pair.getKey();
				int otherval = (int) pair.getValue();

				if (vector.containsKey(pid)) {
					int val = vector.get(pid);
					if (otherval > val) {
						vector.put(pid, otherval);
					}
				}
				else {
					addProcess(pid, otherval);
				}

				it.remove(); // avoids a ConcurrentModificationException
			}

		}

	}

	@Override
	public void setClock(Clock other) {

		if (other instanceof VectorClock) {
			setClockFromString(other.toString());
		}

	}

	@Override
	public void tick(Integer pid) {

		int oldval = getTime(pid);
		vector.put(pid, oldval+1);

	}

	@Override
	public boolean happenedBefore(Clock other) {

		//TODO: Implement this

		return false;
	}

	@Override
	public void setClockFromString(String clock) {

		String withoutWhiteSpace = clock.replaceAll("\\s+","");

		String regex = "\\{((\"[0-9]+\":[0-9]+)(,\"[0-9]+\":[0-9]+)*)?\\}";

		if (withoutWhiteSpace.matches(regex)) {

			vector = new HashMap<Integer, Integer>();
			String cleanClockString = withoutWhiteSpace.replace("{", "").replace("}", "").replace("\"", "");

			if (cleanClockString.length() > 2) {

				String[] kvPairs = cleanClockString.split(",");

				for (String kvPair : kvPairs) {
					String[] kv = kvPair.split(":");
					String key = kv[0];
					String value = kv[1];

					addProcess(Integer.valueOf(key), Integer.valueOf(value));

				}
			}
		}
	}

	@Override
	public String toString(){

		StringBuilder stringBuilder = new StringBuilder("{");
		boolean first = true;
		for (Map.Entry<Integer, Integer> entry : vector.entrySet()) {

			if (!first) stringBuilder.append(",");
			else first = false;

			stringBuilder.append("\"");
			stringBuilder.append(entry.getKey());
			stringBuilder.append("\"");
			stringBuilder.append(":");
			stringBuilder.append(String.valueOf(entry.getValue()));

		}

		stringBuilder.append("}");
		return stringBuilder.toString();
	}

	// return the current clock for the given process id
	int getTime(Integer pid) {

		if (vector.containsKey(pid)) return vector.get(pid);
		else return 0;

	}

	// adds a new process and its vector clock to the current clock
	void addProcess(Integer pid, int time) {
		vector.put(pid, time);
	}
}