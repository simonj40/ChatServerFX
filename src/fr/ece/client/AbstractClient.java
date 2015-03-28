/**
 * 
 */
package fr.ece.client;

import java.net.InetAddress;
import java.util.Locale;
import java.util.ResourceBundle;

import application.ClientController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * @author Simon
 *
 */
public abstract class AbstractClient implements ClientInterface {
	
	public static ResourceBundle messages;

	
	public ObservableList<String> buddies = FXCollections.observableArrayList();
	
	public AbstractClient() {
		
		Locale locale = Locale.getDefault();
		messages = ResourceBundle.getBundle("Internationalization",locale);
	}
	
}
