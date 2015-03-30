/**
 * 
 */
package application;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import fr.ece.client.AbstractClient;
import fr.ece.client.Client;
import fr.ece.client.ClientInterface;
import fr.ece.client.MulticastClient;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.Callback;

/**
 * @author Simon
 *
 */
public class ClientController {
	
	@FXML 
	private TextArea textReceived;
	@FXML
	private TextArea textSend;
	@FXML 
	private ListView<String> buddyList;
	
	AbstractClient client;
	
	
	
	public void setClient(AbstractClient client){
		this.client = client;
		client.setController(this);
		buddyList.setItems(client.buddies);
		
		textSend.setOnKeyPressed(new EventHandler<KeyEvent>() {
		    @Override
		    public void handle(KeyEvent keyEvent) {
		        if (keyEvent.getCode() == KeyCode.ENTER)  {
		            String text = textSend.getText();
		            send();
		        }
		    }
		});
		
		
	}
	
	
	@FXML
	private void send() {
		String message = textSend.getText();
		client.send(message);
		this.newMessage("You: "+message);
		textSend.clear();
		
	}
	
	public void newMessage(String message){
		textReceived.appendText("\n"+message);
		
	}
	
	public TextArea getText(){
		return textReceived;
	}
	
	

	
	

}
