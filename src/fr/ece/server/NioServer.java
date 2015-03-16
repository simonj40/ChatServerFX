/**
 * 
 */
package fr.ece.server;

import java.io.IOException;
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
import java.util.Iterator;
import java.util.Set;

/**
 * @author Simon
 *
 */
public class NioServer extends AbstractMultichatServer {

	/**
	 * @param address
	 * @param port
	 */
	public NioServer(InetAddress address, int port) {
		super(address, port);
	}

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
		// System.out.println("New Client :" + client.getRemoteAddress());
		Selector selector = Selector.open();
		server.register(selector, SelectionKey.OP_ACCEPT);

		while (true) {
			selector.select();
			Set<SelectionKey> set = selector.selectedKeys();
			Iterator<SelectionKey> keyIterator = set.iterator();
			while(keyIterator.hasNext()) {
				SelectionKey key = keyIterator.next();
				
				if (key.isAcceptable()) {
					SocketChannel client = ((ServerSocketChannel)key.channel()).accept();
					client.configureBlocking(false);
					client.register(selector, SelectionKey.OP_READ);
					System.out.println(client.getRemoteAddress() + " Connected");
				}
				if (key.isReadable()) {
					SocketChannel client = (SocketChannel) key.channel();
					ByteBuffer bbuf = ByteBuffer.allocate(8192);
					if(client.read(bbuf) != -1){
						Charset charset = Charset.defaultCharset();
						bbuf.flip();
						CharBuffer cbuf = charset.decode(bbuf);
						System.out.println(cbuf);
						cbuf.compact();
					}else{
						System.out.println(client.getRemoteAddress() + " Disconnected");
						client.close();
					}
				}
				keyIterator.remove();
				
			}

		}

	}
}
