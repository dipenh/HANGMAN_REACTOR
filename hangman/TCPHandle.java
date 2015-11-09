package hangman;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

import reactorapi.Handle;


/**
 * A {@link Handle} that reads and writes a line of text at a time from a TCP
 * {@link Socket}.
 */
public class TCPHandle implements Handle<String> {

	private Socket socket;
	private BufferedReader bReader;
	private PrintStream out;

	/**
	 * Create a handle that reads a socket.
	 * 
	 * @param s
	 *            the socket to read from
	 */
	public TCPHandle(Socket socket) {
		this.socket = socket;
		try {
			bReader = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
			out = new PrintStream(socket.getOutputStream(), true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new RuntimeException("Internal socket error");
		}
	}

	/**
	 * Get a line of text from the socket.
	 * 
	 * @returns the line of text (without the newline), or <code>null</code> on
	 *          error or socket closing.
	 */
	@Override
	public String read() {
		try {
			return bReader.readLine();
		} catch (IOException e) {
			return null;
		}
	}
	
	/**
	 * Write a line of text to the socket (adding a newline).
	 * 
	 * @param s
	 *            the text to write
	 */
	public void write(String s) {
		out.println(s);
		out.flush();
	}
	
	/**
	 * returns the socket 
	 */

	public Socket getSocket() {
		return socket;
	}

	/**
	 * Close the socket, interrupting any pending {@link read()}.
	 */
	public void close() {
		try {
			socket.close();
		} catch (IOException e) {
			/* Whatever happened, there's nothing to do about it. */
		}
	}

}
