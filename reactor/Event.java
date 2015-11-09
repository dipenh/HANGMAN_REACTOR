package reactor;

import reactorapi.EventHandler;

public class Event<T> {
	private final T event;
	private final EventHandler<T> handler;

	public Event(T e, EventHandler<T> eh) {
		event = e;
		handler = eh;
	}
	/**
	 * 
	 * @return event
	 */
	public T getEvent() {
		return event;
	}
	/**
	 * 
	 * @return EventHandler for event
	 */
	public EventHandler<T> getHandler() {
		return handler;
	}

	/**
	 * dispatch the event
	 */
	public void handle() {
			handler.handleEvent(event);
	}
}