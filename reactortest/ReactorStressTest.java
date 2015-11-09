package reactortest;

import junit.framework.*;
import reactor.*;
import concassess.testee.*;
import net.sourceforge.groboutils.junit.v1.TestRunnable;
import net.sourceforge.groboutils.junit.v1.MultiThreadedTestRunner;

/**
 * This test feeds large amounts of data from multiple threads into a large
 * amount of handles in an attempt to find synchronisation problems in your
 * solution.
 * 
 * The tests are run both with one dispatcher for all handles and multiple
 * simultaneous dispatchers among which the threads are divided.
 */
public class ReactorStressTest extends TestCase {
	public static final int SCREAMERS = 20;
	public static final int ITERATIONS = 50;
	public static final int SCREAMS = 250;
	static ConcTestRunner ctr;

	public void testStress() throws Throwable {
		for (int i = 0; i < ITERATIONS; i++) {
			doTest();
			if (ctr != null)
				ctr.resetWatchdog();
		}
	}

	public void doTest() throws Throwable {
		final TestRunnable[] tct = new TestRunnable[SCREAMERS + 1];
		IntIPCHandle[] allHandles = new IntIPCHandle[SCREAMERS * SCREAMERS];
		int ahi = 0;

		Dispatcher d = new Dispatcher();

		for (int i = 0; i < SCREAMERS; i++) {
			IntIPCHandle[] handles = new IntIPCHandle[SCREAMERS];

			for (int j = 0; j < SCREAMERS; j++) {
				handles[j] = new IntIPCHandle();
				allHandles[ahi++] = handles[j];
			}

			tct[i] = new IPCScreamer(i, d, handles);
		}
		tct[SCREAMERS] = new IPCListener(d, allHandles);

		MultiThreadedTestRunner mttr = new MultiThreadedTestRunner(tct);
		mttr.runTestRunnables(1200000);
	}

	public static void main(String[] args) {
		ctr = new ConcTestRunner(args);
		ctr.start(ReactorStressTest.class);
	}
}
