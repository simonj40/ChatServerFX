/**
 * 
 */
package fr.ece.server;

import fr.ece.logger.ChatLogger;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Simon
 *
 */
public abstract class  AbstractMultichatServer implements MultichatServer, Runnable   {

	protected String welcome = "WELCOM TO MESSENGER 2.0 !";
	protected String tag = "<#BUDDYUP>";
	protected String defaultBuddyName = "Anonymous Buddy";
	
	protected HashMap<Integer, String> buddyMap = new HashMap<>();
	protected Lock buddyLock = new ReentrantLock();

	public static ResourceBundle messages;

	private InetAddress address;
	private int port;
	/**
	 * @param address
	 * @param port
	 */
	public AbstractMultichatServer(InetAddress address, int port) {
		super();
		this.address = address;
		this.port = port;
		
		Locale locale = Locale.getDefault();
		messages = ResourceBundle.getBundle("Internationalization",locale);
	}
	/**
	 * @return the address
	 */
	protected InetAddress getAddress() {
		return address;
	}
	/**
	 * @param address the address to set
	 */
	protected void setAddress(InetAddress address) {
		this.address = address;
	}
	/**
	 * @return the port
	 */
	protected int getPort() {
		return port;
	}
	/**
	 * @param port the port to set
	 */
	protected void setPort(int port) {
		this.port = port;
	}
	
	
	

	
	
}
