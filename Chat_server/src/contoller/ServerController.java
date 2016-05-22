package contoller;

public class ServerController {
	private static Server server; 
	public ServerController(){ 
		server = new Server(this);
		iniciarAtencionDelServidor();
	}
	public  void iniciarAtencionDelServidor(){
		if(server != null){
			try{
				server.iniciarAtencionDelServidor();
			}catch(Exception excep){
				System.out.println("Se ha interrumpido la atencion del servidor.\n\nPosible Causa:\n" + excep.getMessage());
			}
		}
	}
	public static void main(String[] args){
		new ServerController();
		
	}
}
