/**
 * 
 */
package fr.ece.client;

import javafx.beans.property.StringProperty;
import application.ClientController;

/**
 * @author Simon
 *
 */
public interface ClientInterface {
	
	public void send(String message);
	public void setController(ClientController controller);
	

}
