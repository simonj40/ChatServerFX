/**
 * 
 */
package fr.ece.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import application.ClientController;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;

/**
 * @author Simon
 *
 */
public class Client implements ClientInterface {

	Socket socket;
	PrintWriter out;
	BufferedReader in;

	public Client(InetAddress address, int port) throws UnknownHostException,
			IOException {
		socket = new Socket(address, port);

		out = new PrintWriter(socket.getOutputStream(), true);
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	}
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.ece.client.ClientInterface#send(java.lang.String)
	 */
	@Override
	public void send(String message) {
		out.println(message);
	}
	
	/* (non-Javadoc)
	 * @see fr.ece.client.ClientInterface#setListener(fr.ece.view.ClientController)
	 */
	@Override
	public void setListener(ClientController controller) {
		
		Receiver receiver = new Receiver();
		receiver.messageProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable,
					String oldValue, String newValue) {
				controller.newMessage(newValue);
			}	
		});
		(new Thread( receiver)).start();
	}
	
	

	private class Receiver extends Task<Void> {

		/*
		 * (non-Javadoc)
		 * 
		 * @see javafx.concurrent.Task#call()
		 */
		@Override
		protected Void call() throws Exception {
			String s;
			while(true){
				if ((s = in.readLine()) != null) {
					this.updateMessage(s);
				}
			}
			
		}

	}

	

}
