/**
 * 
 */
package fr.ece.client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.nio.channels.SocketChannel;

import application.ClientController;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;

/**
 * @author Simon
 *
 */
public class MulticastClient implements ClientInterface {
	
	MulticastSocket socket;
	InetAddress group;
	int PORT;
	String name;
	
	
	public MulticastClient (InetAddress group, int port) throws IOException{
		this.group = group;
		this.PORT = port;
		socket =  new MulticastSocket(PORT);
		socket.joinGroup(group);
	}
	
	
	/* (non-Javadoc)
	 * @see fr.ece.client.ClientInterface#send(java.lang.String)
	 */
	@Override
	public void send(String message) {
		DatagramPacket pkt = new DatagramPacket(message.getBytes(), message.length(), group, PORT);
		try {
			socket.send(pkt);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
		(new Thread(receiver)).start();
	}
	
	
	private class Receiver extends Task<Void>{

		/* (non-Javadoc)
		 * @see javafx.concurrent.Task#call()
		 */
		@Override
		protected Void call() throws Exception {
			
			byte [] buf = new byte [1000];
			DatagramPacket resp = new DatagramPacket(buf, buf.length);
			
			while(true){
				socket.receive(resp);
				String answer = new String(buf);
				this.updateMessage(answer);
			}
		}
		
	}


	
	
	
	
	
	
	

}
