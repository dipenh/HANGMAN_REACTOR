package reactortest;

import reactorapi.*;
import java.util.Random;

/**
 * Handle that returns characters one at a time slowly from a creator-supplied
 * String.
 */
class StringHandle implements Handle<Character> {
	int position = 0;
	String data;
	Random r = new Random();
	boolean waiting;

	public StringHandle(String s, boolean w) {
		data = s;
		waiting = w;
	}

	public Character read() {
		if (waiting)
			try {
				Thread.sleep(r.nextInt(10));
			} catch (InterruptedException ie) {
				return null;
			}

		if (position >= data.length())
			return null;
		return new Character(data.charAt(position++));
	}
}
