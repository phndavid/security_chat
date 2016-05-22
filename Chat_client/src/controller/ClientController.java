package controller;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import view.WindowClient;
import model.Message;

public class ClientController {
	
	public final static int PORT = 6500; 
	public final static String MENSAJE = "mensaje";


	private static Socket socket; 
	private static ObjectInputStream in;
	private static ObjectOutputStream out;
	private static WindowClient windowClient;
	private static Message msgClient;
	
	public ObjectOutputStream getWritter(){
		return out;
	}
	private static void comunicacionServidor() {
		try{
			socket = new Socket(InetAddress.getLocalHost(), PORT);
			out = new ObjectOutputStream(socket.getOutputStream());
			msgClient = new Message();
			windowClient = new WindowClient(out,msgClient);
			
			//El cliente genera la llave publica y privada
			msgClient.generateKeys();
			//El cliente comparte la llave publica con el servidor
			out.writeObject(msgClient);
			out.flush();
			messageServer(socket);
		}catch(Exception e){
			System.out.println("No hay ningun servidor en linea");
		}	
	}
	public static void messageServer(Socket socket ){
		byte[] messageServer;
		try {
			in = new ObjectInputStream(socket.getInputStream());
			Message msgServer = (Message) in.readObject();
			System.out.println("Llave del servidor:" + msgServer.getPublicKey().toString());
			//El cliente crea una llave comun usando la llave publica del servidor
			msgClient.receivePublicKeyFrom(msgServer);
			msgClient.generateCommonSecretKey();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		boolean connected = true;
		while(connected){
			try {
				messageServer = (byte[]) in.readObject();
				if(!messageServer.equals("")){
					System.out.println("messageServer: "+messageServer);
					msgClient.receiveAndDecryptMessage(messageServer);
					windowClient.readConversation(msgClient.getMessage());
				}
			} catch (IOException ex) {
                System.out.println("Error al leer del stream de entrada: " + ex.getMessage());
                connected = false;
            } catch (NullPointerException ex) {
                System.out.println("El socket no se creo correctamente. ");
                connected = false;
            } catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	public static void main(String[] args){
		comunicacionServidor();		
	}
	
}
