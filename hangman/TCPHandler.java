package hangman;

import java.net.Socket;

import hangmanrules.HangmanRules;
import reactorapi.EventHandler;
import reactorapi.Handle;

/**
 * Event handler that receives string from a (creator-supplied)
 * TCPHandle and handles game server depending upon received string.
 */

public class TCPHandler implements EventHandler<String> {
	
	private TCPHandle tcpHandle;
	private HangmanServer gameServer;
	private boolean isFirst = true; // is new player; new Socket
	private HangmanRules<Socket>.Player player = null;

	public TCPHandler(TCPHandle tcpHandle, HangmanServer gameServer){
		this.tcpHandle = tcpHandle;
		this.gameServer = gameServer;
	}
	
	@Override
	public Handle<String> getHandle() {
		return tcpHandle;
	}

	@Override
	public void handleEvent(String s) {
		// client closed the connection.
	    if (s == null)	gameServer.removeHandler(this);
	    else {
	        if (isFirst){ // adds new player
		          player = gameServer.addNewPlayer (tcpHandle.getSocket(), s.trim());
		          tcpHandle.write (gameServer.getStatus ());
		          isFirst = false; // not a new player; not a new socket
	        }
	        else gameServer.guess(s.trim().charAt (0), player.name);
	    }
	}


	public void remove() {
	      gameServer.removeHandler(this);
	  }
}
