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
import java.util.logging.Level;

import application.ClientController;
import fr.ece.logger.ChatLogger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;

/**
 * @author Simon
 *
 */
public class MulticastClient extends AbstractClient {

    private ChatLogger myLogger;
	
	MulticastSocket socket;
	InetAddress group;
	int PORT;
	String name;
	
	
	public MulticastClient (InetAddress group, int port, boolean debugOn) throws IOException{

        myLogger = new ChatLogger(debugOn, MulticastClient.class.getName());
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
            myLogger.logException(Level.WARNING,"logger.fail.send.message", e);
		}
	}
	
	
	/* (non-Javadoc)
	 * @see fr.ece.client.ClientInterface#setListener(fr.ece.view.ClientController)
	 */
	@Override
	public void setController(ClientController controller) {
		
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
