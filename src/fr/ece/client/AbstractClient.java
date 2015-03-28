/**
 * 
 */
package fr.ece.client;

import application.ClientController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * @author Simon
 *
 */
public abstract class AbstractClient implements ClientInterface {
	
	public ObservableList<String> buddies = FXCollections.observableArrayList();
}
