package reactorapi;

import java.util.List;

/**
 * A bounded queue that blocks until it can complete its operations.
 * 
 * @param <T>
 *            type of the contents of the queue
 */
public interface BlockingQueue<T> {
	/**
	 * Wait until queue is not empty, then remove an object from the head of the
	 * queue and return it.
	 * 
	 * @return the object at the head of the queue
	 * @throws InterruptedException
	 */
	public T get() throws InterruptedException;

	/**
	 * Wait until queue is not full, then put an object at the tail of the queue
	 * and return it.
	 * 
	 * @param e
	 * @throws InterruptedException
	 */
	public void put(T e) throws InterruptedException;

	/**
	 * Remove the entire contents of the queue and return it (in the order it
	 * would be read by get) as a List. This method is optional and can be
	 * implemented for bonus points.
	 * 
	 * @return the entire queue
	 */
	public List<T> getAll();

	/**
	 * Get the capacity of the queue.
	 * 
	 * @return how many objects the queue can hold
	 */
	public int getCapacity();

	/**
	 * Get the size of the queue.
	 * 
	 * @return how many objects the queue currently holds
	 */
	public int getSize();
}
