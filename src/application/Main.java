package application;
	
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Locale;
import java.util.ResourceBundle;

import fr.ece.client.AbstractClient;
import fr.ece.client.Client;
import fr.ece.client.MulticastClient;
import fr.ece.server.AbstractMultichatServer;
import fr.ece.server.NioServer;
import fr.ece.server.Server;
import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;


public class Main extends Application {
	
	private static AbstractClient client;
	private static ResourceBundle messages;
	
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
			primaryStage.setTitle(messages.getString("chat.client.title"));
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		
		Locale locale = Locale.getDefault();
		messages = ResourceBundle.getBundle("Internationalization",locale);


AbstractMultichatServer server = null;
		
		InetAddress address = null;
		Integer port = null;
		
		boolean helpOPT =false;
		boolean nioOPT = false;
		boolean serverOPT = false;
		boolean clientOPT = false;
		boolean multicast = false;
		boolean debug = false;
		
		String OPT = "hnscma:W;p:W;";
		LongOpt[] longopts = new LongOpt[8];
		
		longopts[0] = new LongOpt("help", LongOpt.NO_ARGUMENT, null, 'h');
		longopts[1] = new LongOpt("address", LongOpt.REQUIRED_ARGUMENT, null, 'a'); 
		longopts[2] = new LongOpt("debug", LongOpt.NO_ARGUMENT, null, 'd');
		longopts[3] = new LongOpt("multicast", LongOpt.NO_ARGUMENT, null, 'm');
		longopts[4] = new LongOpt("nio", LongOpt.NO_ARGUMENT, null, 'n');
		longopts[5] = new LongOpt("port", LongOpt.REQUIRED_ARGUMENT, null, 'p');
		longopts[6] = new LongOpt("server", LongOpt.NO_ARGUMENT, null, 's');
		longopts[7] = new LongOpt("client", LongOpt.NO_ARGUMENT, null, 'c');
		
		 //Getopt g = new Getopt("testprog", argv, "-:bc::d:hW;", longopts);
		
		int c;
		Getopt g = new Getopt("ChatServer", args, OPT, longopts);
		//read options and arguments
		while( (c = g.getopt()) != -1){
			switch(c){
				case 'h' :
					helpOPT = true;
					System.out.print(messages.getString("option.help.selected"));
					break;
				case 'n' :
					nioOPT = true;
					System.out.print(messages.getString("option.nio.selected"));
					break;
				case 's' :
					serverOPT = true;
					System.out.println(messages.getString("option.start.server.selected"));
					break;
				case 'c' :
					clientOPT = true;
					System.out.println(messages.getString("option.client.selected"));
					break;
				case 'm' :
					multicast = true;
					System.out.println(messages.getString("option.multicast.client.selected"));
					break;
				case 'a' :
					System.out.println(messages.getString("option.address.selected"));
					try {
						address = InetAddress.getByName(g.getOptarg());
					} catch (UnknownHostException e) {
						address = null;
					}
					break;
				case 'p' :
					System.out.println(messages.getString("option.port.selected"));
					try {
						String sPort = g.getOptarg();
						int p = Integer.parseInt(sPort);
						port = new Integer(p);
					} catch (NumberFormatException e) {
						port = null;
					}
					break;
				case 'd' :
					debug = true;
					break;
			}
		}
		
		
		if(helpOPT){
			System.out.println(messages.getString("help"));
			return;
		}
		
		if(address == null){// check Address
			System.out.println(messages.getString("error.invalid.address"));
			return;
		}
		if(port == null){//Check port
			System.out.println(messages.getString("error.invalid.port"));
			return;
		}else if(port.intValue() < 0 || port.intValue() > 65535 ){
			System.out.println(messages.getString("error.invalid.port"));
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
		} else if (clientOPT && multicast){
			System.out.println(messages.getString("help"));
			return;
		}
		
		if(debug) {
			//TODO do something
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
