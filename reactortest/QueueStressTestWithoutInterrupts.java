package reactortest;

import concassess.testee.*;

/**
 * The event queue stress test checks correct functioning of your solution in
 * the face of multiple producers and consumers. This variant does not
 * interrupt.
 */
public class QueueStressTestWithoutInterrupts extends QueueStressTest {
	public boolean interrupts() {
		return false;
	}

	public static void main(String[] args) {
		ctr = new ConcTestRunner(args);
		ctr.start(QueueStressTestWithoutInterrupts.class);
	}
}
