package reactortest;

import net.sourceforge.groboutils.junit.v1.TestRunnable;
import reactor.*;
import java.util.Random;

class StringProducer extends TestRunnable {
	int position = 0;
	String data;
	Random r = new Random();
	boolean waiting;
	BlockingEventQueue<Character> queue;

	public StringProducer(BlockingEventQueue<Character> q, String s, boolean w) {
		data = s;
		waiting = w;
		queue = q;
	}

	public void runTest() {
		while (true) {
			try {
				if (waiting)
					Thread.sleep(r.nextInt(10));
				if (position >= data.length()) {
					queue.put(new Event<Character>(null, new StringHandler(
							null, new StringHandle("", false))));
					break;
				}

				queue.put(new Event<Character>(new Character(data
						.charAt(position++)), new StringHandler(null,
						new StringHandle("", false))));
			} catch (InterruptedException ie) {
				break;
			}
		}
	}
}
