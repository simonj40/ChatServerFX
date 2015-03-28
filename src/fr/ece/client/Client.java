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
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.json.simple.JSONValue;

import application.ClientController;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

/**
 * @author Simon
 *
 */
public class Client extends AbstractClient {

	Socket socket;
	PrintWriter out;
	BufferedReader in;
	String tag = "<#BUDDYUP>";
	
	ClientController controller;
	private Lock buddyLock = new ReentrantLock();
	
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
	public void setController(ClientController controller) {
		this.controller = controller;
		Receiver receiver = new Receiver();
		(new Thread(receiver)).start();
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
					System.out.println(messages.getString("received") +s);
					if(s.contains(tag)){
						Map jsonMap = (Map)JSONValue.parse(s.replace(tag, ""));
						Iterator iter = jsonMap.entrySet().iterator();
						
						Platform.runLater(new Runnable() {
		                     @Override public void run() {
		                    	 buddyLock.lock();
		                    	 buddies.clear();
		 						while(iter.hasNext()){
		 							Map.Entry entry = (Map.Entry)iter.next();
		 							buddies.add((String)entry.getValue());
		 						}
		 						buddyLock.unlock();
		                     }
		                 });
						
					}else{
						String message = "\n"+s;
						//string.setValue(string.getValue()+"\n"+s);
						Platform.runLater(new Runnable() {
							@Override
							public void run() {
								controller.getText().appendText(message);
							}
							
						});
						
						
					}
	
				}
			}
			
		}

	}

	

}
