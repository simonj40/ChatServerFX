/**
 * 
 */
package fr.ece.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Map.Entry;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Simon
 *
 */
public class Server extends AbstractMultichatServer {

	private int BACKLOG = 5;
	private HashMap<Integer, BufferedWriter> writers = new HashMap<Integer, BufferedWriter>();
	private Lock lock = new ReentrantLock();


	/**
	 * @param address
	 * @param port
	 */
	public Server(InetAddress address, int port) {
		super(address, port);
	}

	
	//NOT USED ===> Runnable (use run instead)
	public void start() throws IOException {
		ServerSocket socket = new ServerSocket(this.getPort(), BACKLOG, this.getAddress());
		while (true) {
			Socket con = socket.accept();
			Messenger messenger = new Messenger(con);
			(new Thread(messenger)).start();
			System.out.println(messages.getString("connection.accepted.from")
					+ con.getInetAddress());
		}
		
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
	
ServerSocket socket = null;
		try {
			socket = new ServerSocket(this.getPort(), BACKLOG, this.getAddress());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		while (true) {
			Socket con;
			try {
				con = socket.accept();
				Messenger messenger = new Messenger(con);
				(new Thread(messenger)).start();
				System.out.println(messages.getString("connection.accepted.from")
						+ con.getInetAddress());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}		
		}
	}
	
	

	private class Broadcaster implements Runnable {

		String message;
		int hashcode;

		public Broadcaster(String message, int hashcode) {
			this.message = message;
			this.hashcode = hashcode;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			lock.lock();
			for (Entry<Integer, BufferedWriter> entry : writers.entrySet()) {
				if (entry.getKey().intValue() != hashcode) {
					try {
						entry.getValue().write(message);
						entry.getValue().newLine();
						entry.getValue().flush();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			lock.unlock();
		}

	}

	private class Messenger implements Runnable {

		private Socket socket;

		/**
		 * @param socket
		 */
		public Messenger(Socket socket) {
			super();
			this.socket = socket;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			InputStream input;
			OutputStream output;
			String nickname = "";
			try {
				input = socket.getInputStream();
				output = socket.getOutputStream();
				BufferedWriter writer = new BufferedWriter(
						new OutputStreamWriter(output));
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(input));
				// store writer in map
				writers.put(this.hashCode(), writer);
				// Welcome message
				writer.write(messages.getString("welcome"));
				writer.newLine();
				writer.flush();
				// read write loop
				String message;
				while (((message = reader.readLine()) != null)) {

					// get message and display it
					System.out.println(message);
					// Broadcast message
					String[] arrayMessage = message.split(" ");
					if (arrayMessage[0].equals("/nick")) {
						if (nickname.isEmpty()) {
							// set nickname
							nickname = arrayMessage[1];
							writer.write(messages.getString("pseudo.set")
									+ nickname);
							writer.newLine();
							writer.flush();
						} else {
							writer.write(messages.getString("pseudo.already.set")
									+ nickname);
							writer.newLine();
							writer.flush();
						}
					} else {
						new Thread(new Broadcaster(nickname + " : " + message,
								this.hashCode())).start();
					}

				}

				System.out.println(messages.getString("connection.closed")
						+ socket.getLocalAddress());
				// remove writr from map
				writers.remove(this.hashCode());

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	
}
