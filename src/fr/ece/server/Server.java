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

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/**
 * @author Simon
 *
 */
public class Server extends AbstractMultichatServer {

	private int BACKLOG = 5;
	private HashMap<Integer, BufferedWriter> writers = new HashMap<Integer, BufferedWriter>();
	private Lock buddyLock = new ReentrantLock();
	private Lock writerLock = new ReentrantLock();


	/**
	 * @param address
	 * @param port
	 */
	public Server(InetAddress address, int port) {
		super(address, port);
	}

	// NOT USED ===> this class extends Runnable (use run instead)
	public void start() throws IOException {
		ServerSocket socket = new ServerSocket(this.getPort(), BACKLOG,
				this.getAddress());
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
			socket = new ServerSocket(this.getPort(), BACKLOG,
					this.getAddress());
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

	private void updateBuddyList(){

		buddyLock.lock();
		String json = JSONValue.toJSONString(buddyMap);
		System.out.println("JSON Array: "+json);
		(new Thread( new Broadcaster(tag+json) )).start();;
		buddyLock.unlock();

	}

	private class Broadcaster implements Runnable {

		String message;
		Integer hashcode = null;

		public Broadcaster(String message, int hashcode) {
			this.message = message;
			this.hashcode = new Integer(hashcode);
		}

		public Broadcaster(String message) {
			this.message = message;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			writerLock.lock();
			for (Entry<Integer, BufferedWriter> entry : writers.entrySet()) {
				if (!entry.getKey().equals(hashcode)) {
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
			writerLock.unlock();
		}
	}

	private class Messenger implements Runnable {

		private Socket socket;
		String nickname = defaultBuddyName;
		boolean nicknameSet = false;
		
		/**
		 * @param socket
		 */
		public Messenger(Socket socket) {
			super();
			this.socket = socket;
			buddyLock.lock();
			buddyMap.put(this.hashCode(), nickname);
			buddyLock.unlock();
			// TODO update buddy list
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

			try {
				input = socket.getInputStream();
				output = socket.getOutputStream();
				BufferedWriter writer = new BufferedWriter(
						new OutputStreamWriter(output));
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(input));
				// store writer in map
				writerLock.lock();
				writers.put(this.hashCode(), writer);
				writerLock.unlock();
				// Welcome message
				writer.write(welcome);
				writer.newLine();
				writer.flush();
				
				updateBuddyList();
				// read write loop
				String message;
				while (((message = reader.readLine()) != null)) {

					// get message and display it
					System.out.println(message);
					// Broadcast message
					String[] arrayMessage = message.split(" ");
					if (arrayMessage[0].equals("/nick")) {
						if (!nicknameSet) {
							// set nickname
							nickname = arrayMessage[1];
							buddyLock.lock();
							buddyMap.put(this.hashCode(), this.nickname);
							buddyLock.unlock();
							updateBuddyList();
							nicknameSet = true;
							writer.write(messages.getString("pseudo.set") + nickname);
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
								new Integer(this.hashCode()))).start();
					}
				}

				System.out.println(messages.getString("connection.closed")
						+ socket.getLocalAddress());
				// remove writr from map
				writerLock.lock();
				writers.remove(this.hashCode());
				writerLock.unlock();
				buddyLock.lock();
				buddyMap.remove(this.hashCode());
				buddyLock.unlock();
				updateBuddyList();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	
}
