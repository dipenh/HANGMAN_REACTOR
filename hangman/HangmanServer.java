package hangman;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import hangmanrules.HangmanRules;
import reactor.*;
import reactorapi.EventHandler;
import reactorapi.Handle;

public class HangmanServer {
	private Dispatcher dispatcher;
	private HangmanRules<Socket> hangmanRules;
	private boolean isComplete;

	public HangmanServer(String wordToGuess, Integer numberOfTries) {
		dispatcher = new Dispatcher();
		hangmanRules = new HangmanRules<Socket>(wordToGuess, numberOfTries);
	}

	public <T> void addHandler(EventHandler<T> eh) {
		dispatcher.addHandler(eh);
	}

	public <T> void removeHandler(EventHandler<T> eh) {
		dispatcher.removeHandler(eh);
	}

	public HangmanRules<Socket>.Player addNewPlayer(Socket s, String name) {
		return this.hangmanRules.addNewPlayer(s, name);
	}

	public String getStatus() {
		return hangmanRules.getStatus();
	}

	public void guess(char guess, String name) {
		hangmanRules.makeGuess(guess);
		isComplete = hangmanRules.gameEnded();

		writeMessage(getGameState(guess, name));

		if (isComplete) {
			/* Start cleaning up */
			stopGame();
		}
	}

	private void stopGame() {

		HashMap<EventHandler<?>, WorkerThread<?>> hMap = dispatcher.getMap();
		Iterator<?> it = hMap.keySet().iterator();
		
		WorkerThread<?> thread = null;
		Handle<?> handle = null;
		EventHandler<?> eh = null;

		while (it.hasNext()) {
			eh = (EventHandler<?>) it.next();
			handle = eh.getHandle();
			thread = hMap.get(eh);

			/* If it is the server-socket, then close it */
			if (handle instanceof AcceptHandle) {
				((AcceptHandle) handle).close();
			}
			/* If it is the client-socket, then close it */
			else {
				((TCPHandle) handle).close();
			}

			if (thread != null) {
				thread.cancelThread();
			}
		}
		hMap.clear();
	}

	public void writeMessage(String msg) {
		Socket socket = null;
		TCPHandle handle = null;
		List<HangmanRules<Socket>.Player> players = hangmanRules.getPlayers();

		for (HangmanRules<Socket>.Player player : players) {
			socket = player.playerData;
			handle = new TCPHandle(socket);
			handle.write(msg);
		}
	}

	public String getGameState(char guess, String name) {
		String gameState = guess + " " + hangmanRules.getMaskedWord() + " " + hangmanRules.getTriesLeft() + " "+ name;
		return gameState;

	}

	public static void main(String[] args) {
		AcceptHandle ahandle = null;
		AcceptHandler ah = null;

		HangmanServer gameServer = new HangmanServer(args[0], Integer.parseInt(args[1]));

		try {
			ahandle = new AcceptHandle();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		ah = new AcceptHandler(ahandle, gameServer);

		/* add the new handler and store its mapping to its thread */
		gameServer.dispatcher.addHandler(ah);

		try {
			gameServer.dispatcher.handleEvents();
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}
}