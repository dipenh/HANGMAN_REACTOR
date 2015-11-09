package reactor;

import java.util.HashMap;

import reactorapi.EventHandler;

public class Dispatcher {
	private BlockingEventQueue<Object> beQueue;
	private HashMap<EventHandler <?>, WorkerThread <?>> hMap;
	
	public Dispatcher() {
		this(10);
	}

	public Dispatcher(int capacity) {
		  beQueue = new BlockingEventQueue<Object> (capacity);
		  hMap   = new HashMap<EventHandler<?>, WorkerThread<?>> ();
	}

	public void handleEvents() throws InterruptedException {
		Event <?> ev = null;
		EventHandler<?> eh = null;
		while (!hMap.isEmpty()){
			ev = select();
			eh = ev.getHandler();
//		    if ((eh != null) && (hMap.containsKey (eh))){
			 if (hMap.containsKey (eh)){
		    	ev.handle();
		    }
	    }
	}

	public Event<?> select() throws InterruptedException {
		return beQueue.get();
	}

//	public <T> void addHandler(EventHandler<?> h) {
//		WorkerThread <T> wThread = new WorkerThread<T> ((EventHandler<T>) h, beQueue);
//	    hMap.put(h, wThread);
//	    wThread.start();
//	}
	
	public void addHandler(EventHandler<?> h){
		WorkerThread<Object> wThread = new WorkerThread<Object>( (EventHandler<Object>) h, beQueue);
		hMap.put(h, wThread);
		wThread.start();
	}

	public void removeHandler(EventHandler<?> h) {
		WorkerThread <?> wThread = null;

		wThread = hMap.get(h);
	    if (wThread != null)
	    {
	    	wThread.cancelThread();
	    }
	    hMap.remove(h);
		
	}

	   public HashMap<EventHandler <?>, WorkerThread <?>> getMap (){
	    return hMap;
	  }
	
	
	// Add methods and fields as needed.
}
