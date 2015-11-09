package reactortest;

import junit.framework.*;
import reactor.*;
import reactorapi.*;
import concassess.testee.*;

/**
 * The Reactor semantics test creates two objects and feeds objects to them
 * according to a brief predefined sequence and ensures that the right objects
 * are passed to the right handle and that null values are correctly processed.
 */
public class ReactorSemanticsTest extends TestCase {
	Dispatcher d;
	TestHandler h1, h2;
	boolean done;
	int state = 0;

	public static final Object o1 = new Object();
	public static final Object o2 = new Object();
	public static final Object o3 = new Object();

	public void testSemantics() {
		d = new Dispatcher();
		h1 = new TestHandler();
		d.addHandler(h1);
		h2 = new TestHandler();
		d.addHandler(h2);
		h1.write(o1);
		try {
			d.handleEvents();
		} catch (InterruptedException ie) {
			fail("Unexpected interruption.");
		}
		if (!done)
			fail("handleEvents ended too soon");
	}

	public class TestHandler implements EventHandler<Object> {
		TestHandle h = new TestHandle();

		public Handle<Object> getHandle() {
			return h;
		}

		public void write(Object o) {
			h.write(o);
		}

		public void handleEvent(Object o) {
			switch (state) {
			case 0:
				assertEquals("Wrong object passed", o1, o);
				assertEquals("Wrong handler activated", h1, this);
				h1.write(o2);
				break;
			case 1:
				assertEquals("Wrong object passed", o2, o);
				assertEquals("Wrong handler activated", h1, this);
				h2.write(o3);
				break;
			case 2:
				assertEquals("Wrong object passed", o3, o);
				assertEquals("Wrong handler activated", h2, this);
				h2.write(o1);
				break;
			case 3:
				assertEquals("Wrong object passed", o1, o);
				assertEquals("Wrong handler activated", h2, this);
				h1.write(null);
				break;
			case 4:
				assertEquals("Wrong object passed", null, o);
				assertEquals("Wrong handler activated", h1, this);
				h1.write(o1);
				h2.write(o2);
				break;
			case 5:
				assertEquals("Handler activated after null", h2, this);
				assertEquals("Wrong object passed", o2, o);
				h2.write(o2);
				try {
					d.removeHandler(h1);
				} catch (Exception e) {
					e.printStackTrace();
				}
				d.removeHandler(h2);
				done = true;
				break;
			case 6:
				if (this == h1)
					fail("Handler called after null");
				else
					fail("Removed handler called");
			}
			state++;
		}
	}

	public class TestHandle implements Handle<Object> {
		Object obj;
		boolean avail = false;

		public synchronized void write(Object o) {
			if (avail)
				fail("Event sequence wrong."); /*
												 * Shouldn't happen in this
												 * test.
												 */
			obj = o;
			avail = true;
			/* Only intended for one read at a time, right? */
			notify();
		}

		public synchronized Object read() {
			while (!avail)
				try {
					wait();
				} catch (Exception e) {
					return null;
				}
			avail = false;
			return obj;
		}
	}

	public static void main(String[] args) {
		new ConcTestRunner(args).start(ReactorSemanticsTest.class);
	}
}
