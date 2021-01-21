import java.util.*;
import org.apache.xmlrpc.*;

public class Serveur{
	
	private static Vector<String> utilisateur = new Vector<String>();
	String message_bi = null;
	
	public static void main (String [] args){
	try { 
			System.out.println("Attempting to start XML-RPC Server...");
			WebServer server = new WebServer(8787);
			server.addHandler("sample", new Serveur());
			server.start();
			System.out.println("Started successfully.");
			System.out.println("Accepting requests. (Halt program to stop.)");
	}
	catch (Exception exception){ System.err.println("JavaServer: " + exception); }
	}

	public boolean subscribe(String pseudo) {
		boolean isConnected = false;
		if(!utilisateur.contains(pseudo)){
			utilisateur.add(pseudo);
			isConnected = true;
			String messageEntier = "welcome to our chatRoom "+pseudo ;
			postMessage(pseudo, "welcome to our chatRoom !");
	        System.out.println(messageEntier);
		}
		return isConnected;
	}
	
	public String postMessage(String pseudo, String message) {
        String messageEntier = pseudo + " : " + message;
        System.out.println(messageEntier);
        message_bi = messageEntier;
        return messageEntier;
    }
	
	public String getMessage(){
		return message_bi;
	}
	
	public boolean unsubscribe(String pseudo) {
		boolean isDeconnected = false;
		if(utilisateur.contains(pseudo)){
			utilisateur.remove(pseudo);
			isDeconnected = true;
			postMessage(pseudo, " is deconnected");
		}
		return isDeconnected;
	}
}