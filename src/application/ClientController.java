/**
 * 
 */
package application;

import fr.ece.client.Client;
import fr.ece.client.ClientInterface;
import fr.ece.client.MulticastClient;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

/**
 * @author Simon
 *
 */
public class ClientController {
	
	@FXML 
	TextArea textReceived;
	@FXML
	TextArea textSend;
	
	ClientInterface client;
	
	public void setClient(ClientInterface client){
		this.client = client;
		client.setListener(this);
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
	
	

	
	

}
