package reactortest;

import reactorapi.*;

class StressHandler implements EventHandler<QueueTestData> {
	int events = 0;

	public class StressHandle implements Handle<QueueTestData> {
		public QueueTestData read() {
			return null;
		}
	}

	StressHandle sh = new StressHandle();

	public synchronized int eventsGot() {
		return events;
	}

	public StressHandle getHandle() {
		return sh;
	}

	public synchronized void handleEvent(QueueTestData qtd) {
		events++;
	}
}
