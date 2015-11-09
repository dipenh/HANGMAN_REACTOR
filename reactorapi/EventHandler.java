package reactorapi;

/**
 * Handles events of a user-defined type from its {@link Handle}. Each handler
 * may be registered and deregistered only once. Registered handlers may prevent
 * the program from terminating when the <code>main</code> method returns.
 * 
 * @param <T>
 *            type of events
 */
public interface EventHandler<T> {
	/**
	 * Get the {@link Handle} from which the {@link EventHandler} receives
	 * events (for any given {@link EventHandler}, the same {@link Handle} must
	 * be returned every time; in other words, an {@link EventHandler} may only
	 * listen to one {@link Handle}).
	 * 
	 * @return the handle for the handler
	 */
	public Handle<T> getHandle();

	/**
	 * Handle an incoming message/event represented by an object
	 * <code>s, as received from the {@link Handle}. Messages are dispatched by
	 * calling this method.
	 * 
	 * @param s
	 *            the message/event to handle
	 */
	public void handleEvent(T s);
}
