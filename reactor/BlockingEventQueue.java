package reactor;

import reactorapi.BlockingQueue;

import java.util.LinkedList;
import java.util.List;
import hangman.Semaphore;
public class BlockingEventQueue<T> implements BlockingQueue<Event<? extends T>> {
	
	private Semaphore empty;
	private Semaphore mutex;
	private Semaphore full;
	private LinkedList<Event<? extends T>> beQueue;
	private int capacity;
	
	
	public BlockingEventQueue(int capacity) {
		// TODO: Implement BlockingEventQueue(int).
		
		empty         = new Semaphore (0);
		mutex         = new Semaphore (1);
		full          = new Semaphore (capacity);
		beQueue         = new LinkedList<Event<? extends T>> ();
		this.capacity = capacity;
		
	}
	/* Returns the size of the contents of the queue*/
	public int getSize() {
		    try{
		      mutex.acquire ();
		    } catch (InterruptedException e){
		      // TODO Auto-generated catch block
		      e.printStackTrace();
		    }
		    Integer tmp = beQueue.size ();
		    mutex.release ();
		    return tmp;
	}
	/*Returns capacity of queue*/
	public int getCapacity() {
		return capacity;
	}
	
	public Event<? extends T> get() throws InterruptedException {
		Event<? extends T> tmp;

	    empty.acquire ();
	    /*start of critical section (cs) 
	     * waits until queue is non-empty
	     * */
	    synchronized (this)
	    {
	      tmp = (Event<? extends T>) beQueue.removeFirst();
	    }
	    /*cs Ends*/

	    full.release();

	    return (Event<? extends T>) tmp;
	
	}

	public List<Event<? extends T>> getAll() {
		throw new UnsupportedOperationException(); // Replace this.
		// TODO: Implement BlockingEventQueue.getAll().
//		       return new ArrayList<Event<? extends T>>();
		      
		       
	}

	public void put(Event<? extends T> event) throws InterruptedException {
		full.acquire ();
		/*start of cs */
	    synchronized (this){
	    	
	    	beQueue.addLast ((Event<? extends T>) event);
	    }

	    empty.release (); // increases the count of empty
	    /*CS ends*/
	    System.out.println ("puts:" + event.getEvent ());
	    }
	

	// Add other methods and variables here as needed.
}