package reactortest;

import reactor.*;
import reactorapi.*;
import net.sourceforge.groboutils.junit.v1.TestRunnable;

class IPCListener extends TestRunnable {
	private Dispatcher dispatcher;

	public int done;
	EventHandler<Integer>[] handlers;
	Thread mainThread;

	public class IPCHandler implements EventHandler<Integer> {
		IntIPCHandle handle;
		public int nextScream = 0;

		public IPCHandler(IntIPCHandle h) {
			handle = h;
		}

		public IntIPCHandle getHandle() {
			return handle;
		}

		/*
		 * GroboUtils uses junit.framework.Assert, which is deprecated. Nothing
		 * we can do about that.
		 */
		@SuppressWarnings("deprecation")
		public void handleEvent(Integer s) {
			assertNotNull("Dispatch after removal", s);
			int i = s;
			// System.err.println("Got "+i);
			assertEquals("Wrong message id", nextScream++, i);
			assertEquals("Handler executed in wrong thread", mainThread,
					Thread.currentThread());
			if (nextScream == ReactorStressTest.SCREAMS) {
				done++;
				dispatcher.removeHandler(this);
				handle.close();
			}
		}
	}

	@SuppressWarnings("unchecked")
	public IPCListener(Dispatcher d, IntIPCHandle[] handles) {
		dispatcher = d;
		handlers = (EventHandler<Integer>[]) new EventHandler[handles.length];
		for (int i = 0; i < handles.length; i++) {
			handlers[i] = new IPCHandler(handles[i]);
			d.addHandler(handlers[i]);
		}
	}

	@SuppressWarnings("deprecation")
	public void runTest() {

		mainThread = Thread.currentThread();
		while (done < handlers.length) {
			try {
				dispatcher.handleEvents();
			} catch (InterruptedException ie) {
				return;
			}
			assertEquals(
					"handleEvents ended with incorrect number of processed events",
					handlers.length, done);
		}
	}
}
