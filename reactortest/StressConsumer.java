package reactortest;

import net.sourceforge.groboutils.junit.v1.TestRunnable;
import reactor.*;
import java.util.List;


public class StressConsumer extends TestRunnable {
	BlockingEventQueue<QueueTestData> queue;
	boolean interrupts, getAlls;

	public StressConsumer(BlockingEventQueue<QueueTestData> q,
			boolean interrupts, boolean getAlls) {
		queue = q;
		this.interrupts = interrupts;
		this.getAlls = getAlls;
	}

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

		while (true) {
			if (getAlls) {
				List<Event<? extends QueueTestData>> le = queue.getAll();

				boolean die = false;
				for (Event<? extends QueueTestData> n : le) {
					if (n.getEvent() == null) {
						if (die) {
							boolean putBack = false;
							while (!putBack)
								try {
									queue.put(n);
									putBack = true;
								} catch (InterruptedException ie) {

								}
						}
						die = true;
					} else
						n.handle();
				}
				if (die)
					break;
			}

			Event<? extends QueueTestData> e;
			try {
				e = queue.get();
			} catch (InterruptedException ie) {
				continue;
			}

			if (e.getEvent() == null) {
				return;
			}
			e.handle();
		}
	}
}
