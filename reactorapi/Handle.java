package reactorapi;

/**
 * One end of a (possibly bidirectional) communications channel. Corresponds to
 * e.g. a file handle or a network socket.
 * 
 * @param <T>
 *            type of event passed on the channel
 */
public interface Handle<T> {
	/**
	 * Wait for an event on the channel and return it.
	 * 
	 * @return the event
	 */
	public T read();
}
