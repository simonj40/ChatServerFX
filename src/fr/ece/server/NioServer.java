/**
 * 
 */
package fr.ece.server;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Map.Entry;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.Set;

import org.json.simple.JSONValue;

/**
 * @author Simon
 *
 */
public class NioServer extends AbstractMultichatServer {

	private Lock clientLock = new ReentrantLock();
	private Map<Integer, SocketChannel> clientsmap = new HashMap<>();


	/**
	 * @param address
	 * @param port
	 */
	public NioServer(InetAddress address, int port) {
		super(address, port);
	}

	// NOT USED ===> Runnable (use run instead)
	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.ece.server.MultichatServer#start()
	 */
	@Override
	public void start() throws IOException {

		ServerSocketChannel server = ServerSocketChannel.open();
		server.bind(new InetSocketAddress(this.getAddress(), this.getPort()));
		server.configureBlocking(false);
		// SocketChannel client = server.accept();
		Selector selector = Selector.open();
		server.register(selector, SelectionKey.OP_ACCEPT);

		while (true) {
			selector.select();
			Set<SelectionKey> set = selector.selectedKeys();
			Iterator<SelectionKey> keyIterator = set.iterator();
			while (keyIterator.hasNext()) {
				SelectionKey key = keyIterator.next();

				if (key.isAcceptable()) {
					SocketChannel client = ((ServerSocketChannel) key.channel())
							.accept();
					clientLock.lock();
					clientsmap.put(new Integer(client.hashCode()), client);
					clientLock.unlock();
					client.configureBlocking(false);
					client.register(selector, SelectionKey.OP_READ);
					System.out
							.println(client.getRemoteAddress() + messages.getString("connected"));
				}
				if (key.isReadable()) {
					SocketChannel client = (SocketChannel) key.channel();
					ByteBuffer bbuf = ByteBuffer.allocate(8192);
					if (client.read(bbuf) != -1) {
						Charset charset = Charset.defaultCharset();
						bbuf.flip();
						CharBuffer cbuf = charset.decode(bbuf);
						System.out.println(cbuf);
						cbuf.compact();
					} else {
						System.out.println(client.getRemoteAddress()
								+ messages.getString("disconnected"));
						clientLock.lock();
						clientsmap.remove(client.hashCode());
						clientLock.unlock();
						client.close();
					}
				}
				keyIterator.remove();

			}

		}

	}

	private void updateBuddyList() {

		buddyLock.lock();
		String json = JSONValue.toJSONString(buddyMap);
		System.out.println(messages.getString("json.array") + json);
		(new Thread(new Broadcaster(tag + json))).start();
		System.out.println(messages.getString("send.json")+tag + json);
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
			message += "\n";
			clientLock.lock();
			if (hashcode == null) {
				for (Entry<Integer, SocketChannel> entry : clientsmap
						.entrySet()) {
					ByteBuffer bbuf = ByteBuffer.wrap(message.getBytes());
					try {
						while (bbuf.hasRemaining()) {
							entry.getValue().write(bbuf);
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
			} else {
				for (Entry<Integer, SocketChannel> entry : clientsmap
						.entrySet()) {
					if (entry.getValue().hashCode() != this.hashcode.intValue()) {
						ByteBuffer bbuf = ByteBuffer.wrap(message.getBytes());
						try {
							while (bbuf.hasRemaining()) {
								entry.getValue().write(bbuf);
							}
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
			clientLock.unlock();
		}
	}

	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		ServerSocketChannel server;
		
		try {
			server = ServerSocketChannel.open();
			server.bind(new InetSocketAddress(this.getAddress(), this.getPort()));
			server.configureBlocking(false);
			Selector selector = Selector.open();
			server.register(selector, SelectionKey.OP_ACCEPT);

			while (true) {
				selector.select();
				Set<SelectionKey> set = selector.selectedKeys();
				Iterator<SelectionKey> keyIterator = set.iterator();
				while (keyIterator.hasNext()) {
					SelectionKey key = keyIterator.next();

					if (key.isAcceptable()) {
						SocketChannel client = ((ServerSocketChannel) key
								.channel()).accept();
						clientLock.lock();
						clientsmap.put(new Integer(client.hashCode()), client);
						clientLock.unlock();
						buddyLock.lock();
						buddyMap.put(new Integer(client.hashCode()),
								defaultBuddyName);
						buddyLock.unlock();
						updateBuddyList();
						client.configureBlocking(false);
						client.register(selector, SelectionKey.OP_READ);
						System.out.println(client.getRemoteAddress()
								+ messages.getString("connected"));
						String message = welcome;
						ByteBuffer bbuf = ByteBuffer.wrap(message
								.getBytes());
						client.write(bbuf);
					}
					if (key.isReadable()) {
						SocketChannel client = (SocketChannel) key.channel();
						ByteBuffer bbuf = ByteBuffer.allocate(8192);
						if (client.read(bbuf) != -1) {
							Charset charset = Charset.defaultCharset();
							bbuf.flip();
							CharBuffer cbuf = charset.decode(bbuf);

							String[] arrayMessage = cbuf.toString().split(" ");
							if (arrayMessage[0].equals("/nick")) {
								String nickname = arrayMessage[1];
								if (buddyMap
										.get(new Integer(client.hashCode()))
										.equals(defaultBuddyName)) {
									buddyLock.lock();
									buddyMap.put(
											new Integer(client.hashCode()),
											nickname);
									buddyLock.unlock();
									updateBuddyList();
									String message = "Nickname has been set : "
											+ nickname;
									ByteBuffer bbuf2 = ByteBuffer.wrap(message
											.getBytes());
									client.write(bbuf2);
								} else {
									String message = "Nickname has already been set : "
											+ nickname;
									ByteBuffer bbuf2 = ByteBuffer.wrap(message
											.getBytes());
									client.write(bbuf2);
								}

							} else {
								String message = buddyMap.get(new Integer(client.hashCode()))+": "+cbuf.toString();
								(new Thread(new Broadcaster(message, client.hashCode()))).start();;
								System.out.println(cbuf);
								cbuf.compact();
							}

						} else {
							System.out.println(client.getRemoteAddress()
									+ " Disconnected");
							clientLock.lock();
							clientsmap.remove(client.hashCode());
							clientLock.unlock();
							buddyLock.lock();
							buddyMap.remove(client.hashCode());
							buddyLock.unlock();
							updateBuddyList();
							client.close();
						}
					}
					keyIterator.remove();

				}

			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
