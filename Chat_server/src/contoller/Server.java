package contoller;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
public static final int PORT = 6500;
	
	private Socket socket;
	private ServerSocket serverSocket;
	private ServerController controller;
	public Server(ServerController controller){
		try{
			this.controller = controller;
			serverSocket = new ServerSocket(PORT);
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	public void iniciarAtencionDelServidor() throws Exception { 
		while(true){
			socket = serverSocket.accept();
			new ConnectionThread(socket, controller).start();
		}
	}
	
}
