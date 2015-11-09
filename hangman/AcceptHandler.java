package hangman;

import java.net.Socket;

import reactorapi.EventHandler;
import reactorapi.Handle;

/**
 * Event handler that receives socket from a (creator-supplied)
 * AcceptHandle and handles client socket.
 */
public class AcceptHandler implements EventHandler<Socket>{

	 private Handle <Socket> handle;
	 private HangmanServer gameServer;
	 
	 public AcceptHandler(Handle<Socket> handle, HangmanServer gameServer){
		 this.handle = handle;
		 this.gameServer = gameServer;
	 }
	@Override
	public Handle<Socket> getHandle() {
		return handle;
	}

	@Override
	public void handleEvent(Socket s) {
		// A handler for handling client data 
		TCPHandle tcpHandle = new TCPHandle(s);
		TCPHandler tcpHandler = new TCPHandler(tcpHandle, gameServer);
		// game server adds handler to the dispatcher.
		gameServer.addHandler(tcpHandler);
	}

}
