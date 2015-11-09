package reactortest;

import reactor.*;
import concassess.testee.*;

/**
 * This variant of {@link BasicReactorTest} does not sleep between characters.
 */
public class SleeplessReactorTest extends BasicReactorTest {
	public void doSetUp() {
		d = new Dispatcher();
		sh1 = new StringHandler(d, new StringHandle("Reactor test ", false));
		d.addHandler(sh1);
		sh2 = new StringHandler(d, new StringHandle("completed OK.", false));
		d.addHandler(sh2);
	}

	public static void main(String[] args) {
		ctr = new ConcTestRunner(args);
		ctr.start(SleeplessReactorTest.class);
	}
}
