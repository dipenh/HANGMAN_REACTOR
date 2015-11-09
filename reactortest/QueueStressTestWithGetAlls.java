package reactortest;

import concassess.testee.*;

/**
 * The event queue stress test checks correct functioning of your solution in
 * the face of multiple producers and consumers. This variant adds use of
 * {@link reactor.BlockingEventQueue#getAll()}.
 */
public class QueueStressTestWithGetAlls extends QueueStressTest {
	public boolean getAlls() {
		return true;
	}

	public static void main(String[] args) {
		ctr = new ConcTestRunner(args);
		ctr.start(QueueStressTestWithGetAlls.class);
	}
}
