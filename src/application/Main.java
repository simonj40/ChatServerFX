package application;
	
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import fr.ece.client.AbstractClient;
import fr.ece.client.Client;
import fr.ece.client.MulticastClient;
import fr.ece.server.AbstractMultichatServer;
import fr.ece.server.NioServer;
import fr.ece.server.Server;
import gnu.getopt.Getopt;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;


public class Main extends Application {
	
	private static String OPT = "hnscma:p:";
	private static String HELP = "Help !";
	
	private static AbstractClient client;
	
	
	@Override
	public void start(Stage primaryStage) {
		
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent e) {
				Platform.exit();
				System.exit(0);
			}
		});

		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("ClientView.fxml"));
			GridPane root = (GridPane)loader.load();
			ClientController controller = (ClientController)loader.getController();
			controller.setClient(client);
			Scene scene = new Scene(root,400,400);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.setTitle("Chat Client");
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		
		AbstractMultichatServer server = null;
		
		InetAddress address = null;
		Integer port = null;
		
		boolean helpOPT =false;
		boolean nioOPT = false;
		boolean serverOPT = false;
		boolean clientOPT = false;
		boolean multicast = false;
		
		int c;
		Getopt g = new Getopt("ChatServer", args, OPT);
		//read options and arguments
		while( (c = g.getopt()) != -1){
			switch(c){
				case 'h' :
					helpOPT = true;
					System.out.print("Option help selected");
					break;
				case 'n' :
					nioOPT = true;
					System.out.print("Option NIO selected");
					break;
				case 's' :
					serverOPT = true;
					System.out.println("Option start server selected");
					break;
				case 'c' :
					clientOPT = true;
					System.out.println("Option client selected");
					break;
				case 'm' :
					multicast = true;
					System.out.println("Option multicast client selected");
					break;
				case 'a' :
					System.out.println("Option address selected");
					try {
						address = InetAddress.getByName(g.getOptarg());
					} catch (UnknownHostException e) {
						address = null;
					}
					break;
				case 'p' :
					System.out.println("Option port selected");
					try {
						String sPort = g.getOptarg();
						int p = Integer.parseInt(sPort);
						port = new Integer(p);
					} catch (NumberFormatException e) {
						port = null;
					}
					break;
			}
		}
		
		
		if(helpOPT){
			System.out.println(HELP);
			return;
		}
		
		if(address == null){// check Address
			System.out.println("Error : Invalid address...");
			return;
		}
		if(port == null){//Check port
			System.out.println("Error : Invalid port...");
			return;
		}else if(port.intValue() < 0 || port.intValue() > 65535 ){
			System.out.println("Error : Invalid port...");
			return;
		}
		
		if(serverOPT){
			if(nioOPT){
				server = new NioServer(address, port);
			}else{
				server = new Server(address, port);
			}
			(new Thread(server)).start();
		}else if (nioOPT) {
			server = new NioServer(address, port);
			(new Thread(server)).start();
		}
		
		if(clientOPT && !multicast){
			try {
				client = new Client(address, port);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else if (!clientOPT && multicast) {
			try {
				client = new MulticastClient(address, port);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if(clientOPT || multicast) launch(args);
		else return;

		/*
		try {
			//Server server = new Server(InetAddress.getLocalHost(), PORT);
			System.out.println("Server address" + InetAddress.getByName("localhost").toString());
			NewServer server = new NewServer(InetAddress.getByName("localhost"), PORT);
			server.start();
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		*/
		
	}

		

}
