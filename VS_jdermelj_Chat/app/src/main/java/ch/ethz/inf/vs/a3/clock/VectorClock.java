package ch.ethz.inf.vs.a3.clock;

import java.util.Map;

public class VectorClock implements Clock {

	// For each process id associate a logical time
	private Map<Integer, Integer> vector;


	@Override
	public void update(Clock other) {

	}

	@Override
	public void setClock(Clock other) {

	}

	@Override
	public void tick(Integer pid) {

		int oldval = vector.get(pid);
		vector.put(pid, oldval+1);

	}

	@Override
	public boolean happenedBefore(Clock other) {
		return false;
	}

	@Override
	public void setClockFromString(String clock) {

	}

	// return the current clock for the given process id
	int getTime(Integer pid) {

		return vector.get(pid);
	}

	// adds a new process and its vector clock to the current clock
	void addProcess(Integer pid, int time) {
		vector.put(pid, time);
	}
}