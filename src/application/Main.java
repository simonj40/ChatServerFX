package application;
	
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import fr.ece.server.AbstractMultichatServer;
import fr.ece.server.NioServer;
import fr.ece.server.Server;
import gnu.getopt.Getopt;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;


public class Main extends Application {
	
	private static int PORT = 1234;
	private static String OPT = "hnsc:a:p";
	private static String HELP = "Help !";
	
	@Override
	public void start(Stage primaryStage) {
		try {
			BorderPane root = new BorderPane();
			Scene scene = new Scene(root,400,400);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		
		//launch(args);
		
		AbstractMultichatServer server = null;
		
		InetAddress addess = null;
		Integer port = null;
		
		boolean helpOPT =false;
		boolean nioOPT = false;
		boolean serverOPT = false;
		boolean clientOPT = false;
		
		int c;
		Getopt g = new Getopt("ChatServer", args, OPT);
		//read options and arguments
		while( (c = g.getopt()) != -1){
			
			switch(c){
			
				case 'h' :
					helpOPT = true;
					System.out.print("Option help selected");
				case 'n' :
					nioOPT = true;
					System.out.print("Option NIO selected");
				case 's' :
					serverOPT = true;
					System.out.println("Option start server selected");
				case 'c' :
					clientOPT = true;
					System.out.println("Option client selected");
				case 'a' :
					System.out.println("Option address selected");
					try {
						addess = InetAddress.getByName(g.getOptarg());
					} catch (UnknownHostException e) {
						addess = null;
					}
				case 'p' :
					System.out.println("Option port selected");
					try {
						int p = Integer.parseInt(g.getOptarg());
						port = new Integer(p);
					} catch (NumberFormatException e) {
						port = null;
					}
			}
		}
		
		
		if(helpOPT){
			System.out.println(HELP);
			return;
		}
		
		if(addess == null){// check Address
			System.out.println("Error : Invalid address...");
			return;
		}
		if(port == null){//Chck port
			System.out.println("Error : Invalid port...");
			return;
		}else if(port < 0 || port < 65535 ){
			System.out.println("Error : Invalid port...");
			return;
		}
		if(!serverOPT){
			if(nioOPT){
				server = new NioServer(addess, port);
			}else{
				server = new Server(addess, port);
			}
			try {
				server.start();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if(clientOPT){
			//launch client interface
		}
		

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
