package reactortest;

class SelfInterrupter extends Thread {
	Thread me;
	int delay;

	public SelfInterrupter(int d) {
		me = Thread.currentThread();
		delay = d;
	}

	public void run() {
		try {
			Thread.sleep(delay);
		} catch (InterruptedException ie) {
		}
		me.interrupt();
	}
}