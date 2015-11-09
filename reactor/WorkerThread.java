package reactor;

import reactorapi.*;

public class WorkerThread<T> extends Thread {
	private final EventHandler<T> handler;
	private final BlockingEventQueue<Object> queue;
	

	// Additional fields are allowed.

	public WorkerThread(EventHandler<T> eh, BlockingEventQueue<Object> q) {
		handler = eh;
		queue = q;
	}

	public void run() {
	
	    Handle <T> handle = handler.getHandle ();
	    while(true){
	    	if(!Thread.currentThread().isInterrupted()){
	    		T data = handle.read();
	    		Event <T> ev = new Event<T>(data, handler);
	    		try {
					queue.put(ev);
				} catch (InterruptedException e) {
					// TODO Auto-gedsnerated catch block
//					e.printStackTrace();
					break;
				}
	    	}else{
	    		break; //Thread interuppted. End it.
	    	}
	    }
	}

	public void cancelThread() {
		if(isAlive())interrupt();
	}
}