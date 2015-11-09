package reactortest;

class QueueTestData {
	int value;
	StressProducer source;

	QueueTestData(int v, StressProducer s) {
		value = v;
		source = s;
	}
}
