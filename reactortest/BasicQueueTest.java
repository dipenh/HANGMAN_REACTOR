package reactortest;

import junit.framework.*;
import reactor.BlockingEventQueue;
import reactor.Event;
import concassess.testee.*;
import net.sourceforge.groboutils.junit.v1.*;

/**
 * The basic event queue test checks that all the operations of the queue
 * function correctly: basic data transfer, blocking and interruption as well as
 * status readouts.
 * 
 * @author Jan Lönnberg
 */
public class BasicQueueTest extends TestCase {
	TestRunnable[] sh = new TestRunnable[2];
	static ConcTestRunner ctr;
	public static int ITERATIONS = 50;
	private BlockingEventQueue<Character> q;
	public static final String TEST_STRING = "Queue test completed OK.";

	public void doSetUp() {
		q = new BlockingEventQueue<Character>(5);
		sh[0] = new StringProducer(q, TEST_STRING, true);
		sh[1] = new StringConsumer(q, true);
	}

	public void testSimpleTest() throws Throwable {
		for (int it = 0; it < ITERATIONS; it++) {
			doSetUp();

			assertEquals("Empty queue size wrong.", 0, q.getSize());
			assertEquals("Empty queue capacity wrong.", 5, q.getCapacity());

			MultiThreadedTestRunner mttr = new MultiThreadedTestRunner(sh);
			mttr.runTestRunnables(1200000);
			assertEquals("Simple test result wrong.", TEST_STRING,
					sh[1].toString());

			assertEquals("Empty queue size wrong.", 0, q.getSize());
			assertEquals("Empty queue capacity wrong.", 5, q.getCapacity());

			new SelfInterrupter(100).start();

			try {
				q.get();
				fail("Returned from a get operation on an empty queue!");
			} catch (InterruptedException ie) {

			}

			for (int i = 0; i < 5; i++)
				q.put(new Event<Character>('x', new StringHandler(null,
						new StringHandle("", false))));
			assertEquals("Full queue size wrong.", 5, q.getSize());
			assertEquals("Full queue capacity wrong.", 5, q.getCapacity());

			new SelfInterrupter(100).start();

			try {
				q.put(new Event<Character>('x', new StringHandler(null,
						new StringHandle("", false))));
				fail("Returned from a put operation on a full queue!");
			} catch (InterruptedException ie) {

			}

			if (ctr != null)
				ctr.resetWatchdog();
		}
	}

	public static void main(String[] args) {
		ctr = new ConcTestRunner(args);
		ctr.start(BasicQueueTest.class);
	}
}
