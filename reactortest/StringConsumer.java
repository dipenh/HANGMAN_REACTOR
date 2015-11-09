package reactortest;

import net.sourceforge.groboutils.junit.v1.TestRunnable;
import reactor.*;
import java.util.Random;

/**
 * Handle that adds character events one at a time slowly from a
 * creator-supplied String.
 */
class StringConsumer extends TestRunnable {
	int position = 0;
	StringBuffer data = new StringBuffer();
	Random r = new Random();
	boolean waiting;
	BlockingEventQueue<Character> queue;

	public StringConsumer(BlockingEventQueue<Character> q, boolean w) {
		waiting = w;
		queue = q;
	}

	public String toString() {
		return data.toString();
	}

	public void runTest() {
		while (true) {
			Event<? extends Character> e;
			try {
				if (waiting)
					Thread.sleep(r.nextInt(10));

				e = queue.get();
			} catch (InterruptedException ie) {
				data.append("--- Interrupted ---");
				break;
			}

			if (e.getEvent() == null) {
				break;
			}

			data.append(e.getEvent());
		}
	}
}
