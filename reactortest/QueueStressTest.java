package reactortest;

import junit.framework.*;
import reactor.BlockingEventQueue;
import concassess.testee.*;
import net.sourceforge.groboutils.junit.v1.*;

/**
 * The event queue stress test checks correct functioning of your solution in
 * the face of multiple producers and consumers. This variant bombards the
 * threads with interruptions to ensure the solution stays compliant.
 * 
 * {@link reactor.BlockingEventQueue#getAll()} is not used.
 */
public class QueueStressTest extends TestCase {
	TestRunnable[] sh = new TestRunnable[SOURCES + CONSUMERS];
	static ConcTestRunner ctr;
	public static final int ITERATIONS = 50;
	public static final int EVENTS = 1000;
	public static final int SOURCES = 20;
	public static final int CONSUMERS = SOURCES; // Invariant for quit!
	private BlockingEventQueue<QueueTestData> q;

	public boolean interrupts() {
		return true;
	}

	public boolean getAlls() {
		return false;
	}

	public void doSetUp() {
		q = new BlockingEventQueue<QueueTestData>(20);
		for (int i = 0; i < SOURCES; i++)
			sh[i] = new StressProducer(q, interrupts());
		for (int i = SOURCES; i < SOURCES + CONSUMERS; i++)
			sh[i] = new StressConsumer(q, interrupts(), getAlls());
	}

	public void testStressTest() throws Throwable {
		for (int it = 0; it < ITERATIONS; it++) {
			doSetUp();

			MultiThreadedTestRunner mttr = new MultiThreadedTestRunner(sh);
			mttr.runTestRunnables(1200000);

			for (int i = 0; i < SOURCES; i++)
				assertEquals("Wrong number of events", EVENTS,
						((StressProducer) (sh[i])).eventsGot());

			if (ctr != null)
				ctr.resetWatchdog();
		}
	}

	public static void main(String[] args) {
		ctr = new ConcTestRunner(args);
		ctr.start(QueueStressTest.class);
	}
}
