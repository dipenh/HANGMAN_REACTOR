package hangmanrules;

import java.util.List;
import java.util.LinkedList;

/**
 * The rules of a Hangman game.
 * 
 * @author Jan Lönnberg
 * 
 * @param <T>
 *            type of data you want to associate with players
 */
public class HangmanRules<T> {
	private List<Player> players = new LinkedList<Player>();
	private int tries;
	private final String word;
	private boolean ended;
	private String currentGuess;

	/**
	 * Create a new Hangman game state with no players and guesses.
	 * 
	 * @param word
	 *            word to guess
	 * @param tries
	 *            number of incorrect guesses before loss
	 */
	public HangmanRules(String word, int tries) {
		this.tries = tries;
		this.word = word.toLowerCase();
		StringBuffer s = new StringBuffer(word.length());
		for (int i = 0; i < word.length(); i++)
			s.append('-');
		currentGuess = s.toString();
	}

	/**
	 * Internal representation of a player used by the game.
	 * 
	 * @author Jan Lönnberg
	 * 
	 */
	public class Player {
		/**
		 * Any object you want for your player data
		 */
		public final T playerData;

		/**
		 * The name of the player
		 */
		public final String name;

		/**
		 * Create a new player with the specified name and data.
		 * 
		 * @param p
		 *            data
		 * @param n
		 *            name
		 */
		Player(T p, String n) {
			playerData = p;
			name = n;
		}

		/**
		 * Get the string describing a guess made by this player.
		 * 
		 * @param g
		 *            guess
		 * @return string describing the guess
		 */
		public String getGuessString(char g) {
			return g + " " + getStatus() + " " + name;
		}
	}

	/**
	 * Update the guessing state with a guessed letter from a player.
	 * 
	 * @param g
	 * @return whether the guess was correct
	 */
	public boolean makeGuess(char g) {
		boolean found = false;

		char[] cg = currentGuess.toCharArray();

		for (int i = 0; i < currentGuess.length(); i++)
			if (word.charAt(i) == g) {
				cg[i] = g;
				found = true;
			}
		currentGuess = new String(cg);
		if (!found)
			tries--;

		if (tries == 0 || currentGuess.indexOf('-') < 0)
			ended = true;
		return found;
	}

	/**
	 * Get the current guessing state (with unknown letters replaced with
	 * dashes).
	 * 
	 * @return the current guessing state
	 */
	public String getMaskedWord() {
		return currentGuess;
	}

	/**
	 * Get the number of incorrect guesses before losing
	 * 
	 * @return incorrect guesses before losing
	 */
	public int getTriesLeft() {
		return tries;
	}

	/**
	 * Get whether the game has ended.
	 * 
	 * @return whether the game has ended
	 */
	public boolean gameEnded() {
		return ended;
	}

	/**
	 * Get the status string to sent to newly connected players.
	 * 
	 * @return status string
	 */
	public String getStatus() {
		return currentGuess + " " + tries;
	}

	/**
	 * Add a player to the game.
	 * 
	 * @param data
	 *            a student-defined object to associate with the player
	 * @param name
	 *            the name of the player
	 * @return the new player
	 */
	public Player addNewPlayer(T data, String name) {
		Player p = new Player(data, name);
		players.add(p);
		return p;
	}

	/**
	 * Remove a player from the game.
	 * 
	 * @param p
	 *            the player to remove
	 */
	public void removePlayer(Player p) {
		players.remove(p);
	}

	/**
	 * Get a list of all the players in the game.
	 * 
	 * @return list of the players
	 */
	public List<Player> getPlayers() {
		return new LinkedList<Player>(players);
	}
}