package ch.ethz.inf.vs.a3.clock;

public class LamportClock implements Clock {

	private int time;


	@Override
	public void update(Clock other) {

		if (happenedBefore(other)) setClock(other);

	}

	@Override
	public void setClock(Clock other) {
		if (other instanceof LamportClock) {

			LamportClock otherL = (LamportClock) other;

			this.time = otherL.getTime();

		}

	}

	@Override
	public void tick(Integer pid) {

		time += 1;

	}

	@Override
	public boolean happenedBefore(Clock other) {
		if (other instanceof LamportClock) {

			LamportClock otherL = (LamportClock) other;

			return time < otherL.getTime();

		}
		else {

			return false;
		}
	}

	@Override
	public void setClockFromString(String clock) {

		if (clock.matches("[0-9]+")) {

			int newVal = Integer.valueOf(clock);

			time = newVal;

		}

	}
	@Override
	public String toString(){

		return String.valueOf(this.getTime());
	}

	// overrides the current clock value with the one provided as input
	void setTime(int time) {
		this.time = time;
	}

	// return the current clock value
	int getTime() {
		return time;
	}
}