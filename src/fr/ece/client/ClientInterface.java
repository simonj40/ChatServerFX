/**
 * 
 */
package fr.ece.client;

import application.ClientController;

/**
 * @author Simon
 *
 */
public interface ClientInterface {
	
	public void send(String message);
	public void setListener(ClientController controller);
	

}
