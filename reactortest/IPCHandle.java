package reactortest;

import reactorapi.*;
import java.util.concurrent.LinkedBlockingQueue;

class IPCHandle<E> implements Handle<E> {
	boolean closed;
	final LinkedBlockingQueue<E> q = new LinkedBlockingQueue<E>();
	E dummy;

	public IPCHandle(E d) {
		dummy = d;
	}

	public void write(E e) {
		try {
			q.put(e);
		} catch (InterruptedException ie) {
		}
	}

	public E read() {
		synchronized (this) {
			if (closed)
				return null;
		}

		E e;

		try {
			e = q.take();
		} catch (InterruptedException ie) {
			return null;
		}

		synchronized (this) {
			if (closed)
				return null;
		}
		return e;
	}

	public void close() {
		synchronized (this) {
			closed = true;
		}
		write(dummy);
	}
}
