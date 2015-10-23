package ch.ethz.inf.vs.a3.clock;

public class VectorClock implements Clock {


	@Override
	public void update(Clock other) {

	}

	@Override
	public void setClock(Clock other) {

	}

	@Override
	public void tick(Integer pid) {

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

		return 0;
	}

	// adds a new process and its vector clock to the current clock
	void addProcess(Integer pid, int time) {


	}
}