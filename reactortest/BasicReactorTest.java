package reactortest;

import junit.framework.*;
import reactor.*;
import concassess.testee.*;

/**
 * The basic reactor test is effectively the example program: It creates two
 * Handles that send strings one character at a time and event handlers that
 * collect these characters and finally check that they received the expected
 * strings.
 * 
 * @author Jan Lönnberg
 */
public class BasicReactorTest extends TestCase {
	Dispatcher d;
	StringHandler sh1, sh2;
	static ConcTestRunner ctr;
	public static int ITERATIONS = 50;

	public void doSetUp() {
		d = new Dispatcher();
		sh1 = new StringHandler(d, new StringHandle("Reactor test ", true));
		d.addHandler(sh1);
		sh2 = new StringHandler(d, new StringHandle("completed OK.", true));
		d.addHandler(sh2);
	}

	public void testSimpleTest() {
		for (int it = 0; it < ITERATIONS; it++) {
			doSetUp();
			try {
				d.handleEvents();
			} catch (InterruptedException ie) {
				fail("Test interrupted.");
			}

			assertEquals("Simple test result wrong.",
					"Reactor test completed OK.", "" + sh1 + sh2);
			if (ctr != null)
				ctr.resetWatchdog();
		}
	}

	public static void main(String[] args) {
		ctr = new ConcTestRunner(args);
		ctr.start(BasicReactorTest.class);
	}
}
