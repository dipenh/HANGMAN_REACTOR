package reactortest;

import net.sourceforge.groboutils.junit.v1.TestRunnable;
import reactor.*;

class StressProducer extends TestRunnable {
	BlockingEventQueue<QueueTestData> queue;
	StressHandler handler = new StressHandler();
	boolean interrupts;

	public StressProducer(BlockingEventQueue<QueueTestData> q,
			boolean interrupts) {
		queue = q;
		this.interrupts = interrupts;
	}

	int eventsGot() {
		return handler.eventsGot();
	}

	/* Expect one interrupt and resend. */
	public void runTest() {
		final Thread me = Thread.currentThread();
		if (interrupts)
			new Thread() {
				public void run() {
					for (int i = 0; i < 100; i++) {
						me.interrupt();
						Thread.yield();
					}
				}
			}.start();
		for (int i = 0; i < QueueStressTest.EVENTS; i++) {
			boolean done = false;
			while (!done)
				try {
					queue.put(new Event<QueueTestData>(new QueueTestData(i,
							this), handler));
					done = true;
				} catch (InterruptedException ie) {

				}
		}
		boolean done2 = false;
		while (!done2)
			try {
				queue.put(new Event<QueueTestData>(null, handler));
				done2 = true;
			} catch (InterruptedException ie) {

			}
	}
}
