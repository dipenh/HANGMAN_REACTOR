package reactortest;

import reactor.*;
import net.sourceforge.groboutils.junit.v1.TestRunnable;

class IPCScreamer extends TestRunnable {
	private int id;
	private IntIPCHandle[] handles;

	public IPCScreamer(int i, Dispatcher di, IntIPCHandle[] h) {
		id = i;
		d = di;
		handles = h;
	}

	Dispatcher d;

	public void runTest() {
		for (int j = 0; j < ReactorStressTest.SCREAMS + 5; j++) {
			for (int i = 0; i < ReactorStressTest.SCREAMERS; i++) {
				handles[i].write(j);
			}
		}
	}

	public String toString() {
		return "IPCScreamer " + id;
	}
}
