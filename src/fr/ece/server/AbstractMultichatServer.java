/**
 * 
 */
package fr.ece.server;

import java.net.InetAddress;

/**
 * @author Simon
 *
 */
public abstract class  AbstractMultichatServer implements MultichatServer  {

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
