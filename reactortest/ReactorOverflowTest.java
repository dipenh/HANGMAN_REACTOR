package reactortest;

import junit.framework.*;
import reactor.*;
import reactorapi.*;
import concassess.testee.*;

/**
 * This test attempts to overload your Reactor's buffers by feeding data from a
 * handle much faster than it can be processed. This exposes problems with
 * buffer management.
 */
public class ReactorOverflowTest extends TestCase {
	Dispatcher d;
	OverflowHandler h;
	int got;
	static ConcTestRunner ctr;
	int done;

	public static int EVENTS = 10;
	public static int POSTEVENTS = 10000;
	public static int ITERATIONS = 10;

	public void testOverflow() {
		d = new Dispatcher();
		for (int i = 0; i < ITERATIONS; i++) {
			got = 0;
			h = new OverflowHandler();
			d.addHandler(h);
			try {
				d.handleEvents();
			} catch (InterruptedException ie) {
				fail("Unexpected interruption.");
			}
			if (ctr != null)
				ctr.resetWatchdog();
			if (got < EVENTS + POSTEVENTS)
				fail("handleEvents ended too soon; some events were not dispatched");
		}
	}

	public class OverflowHandler implements EventHandler<byte[]> {
		OverflowHandle h = new OverflowHandle();

		public Handle<byte[]> getHandle() {
			return h;
		}

		public void handleEvent(byte[] b) {
			got++;
			if (got == EVENTS + POSTEVENTS)
				d.removeHandler(this);
			else if (got > EVENTS + POSTEVENTS)
				fail("Dispatched events from removed handler.");

			if (got < EVENTS)
				try {
					Thread.sleep(100);
				} catch (InterruptedException ie) {
				}
		}
	}

	public class OverflowHandle implements Handle<byte[]> {
		public byte[] read() {
			try {
				return new byte[65536];
			} catch (OutOfMemoryError oe) {
				fail("Out of memory reading handle.");
				throw oe;
			}
		}
	}

	public static void main(String[] args) {
		ctr = new ConcTestRunner(args);
		ctr.start(ReactorOverflowTest.class);
	}
}
