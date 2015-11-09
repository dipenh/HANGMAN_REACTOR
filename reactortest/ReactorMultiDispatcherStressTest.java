package reactortest;

import net.sourceforge.groboutils.junit.v1.TestRunnable;
import net.sourceforge.groboutils.junit.v1.MultiThreadedTestRunner;
import reactor.*;
import concassess.testee.*;

/**
 * This test feeds large amounts of data from multiple threads into a large
 * amount of handles in an attempt to find synchronisation problems in your
 * solution.
 * 
 * The tests are run both with one dispatcher for all handles and multiple
 * simultaneous dispatchers among which the threads are divided.
 */
public class ReactorMultiDispatcherStressTest extends ReactorStressTest {
	public void doTest() throws Throwable {
		TestRunnable[] tct = new TestRunnable[SCREAMERS * 2];

		for (int i = 0; i < SCREAMERS; i++) {
			Dispatcher d = new Dispatcher();
			IntIPCHandle[] handles = new IntIPCHandle[SCREAMERS];

			for (int j = 0; j < SCREAMERS; j++) {
				handles[j] = new IntIPCHandle();
			}

			tct[i] = new IPCScreamer(i, d, handles);
			tct[SCREAMERS + i] = new IPCListener(d, handles);
		}

		MultiThreadedTestRunner mttr = new MultiThreadedTestRunner(tct);
		mttr.runTestRunnables(1200000);
	}

	public static void main(String[] args) {
		ctr = new ConcTestRunner(args);
		ctr.start(ReactorMultiDispatcherStressTest.class);
	}
}
