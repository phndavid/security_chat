package contoller;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import model.Message;

import view.WindowServer;

public class ConnectionThread extends Thread{
	
	private Socket socket; 
	private ObjectInputStream in;
	private ObjectOutputStream out;
	private ServerController server; 
	private WindowServer windowServer;
	private Message msgServer;
	public ConnectionThread(Socket socket, ServerController serverController){
		this.windowServer = new WindowServer(this);
		this.server = serverController; 
		this.socket = socket; 
		try{
			in = new ObjectInputStream(socket.getInputStream());
			out = new ObjectOutputStream(socket.getOutputStream());
			out.flush();
			//El servidor genera la llave publica y privada
			msgServer = new Message();
			msgServer.generateKeys();
			//El servidor comparte la llave publica con el cliente
			out.writeObject(msgServer);
			out.flush();
			Message msgClient = (Message) in.readObject();
			System.out.println("Llave del client:" + msgClient.getPublicKey().toString());
			//El servidor crea una llave comun usando la llave publica del cliente
			msgServer.receivePublicKeyFrom(msgClient);
			msgServer.generateCommonSecretKey();
		}catch(IOException e){
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void run(){ 
		byte[]  messageClient;
		try{
			while(true) { 
				messageClient = (byte[]) in.readObject();
				msgServer.receiveAndDecryptMessage(messageClient);
				windowServer.readConversation(msgServer.getMessage());
			}
		}catch(NumberFormatException ex) { 
			ex.printStackTrace();
		}catch(Exception e){
			System.out.println("Se ha desconectado un cliente");
		}
	}
	public Message getMsgServer(){
		return msgServer;
	}
	public ObjectOutputStream getWriter(){
		return out;
	}	
}
