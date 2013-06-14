package BRZLauncher;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class SampServerListener implements Runnable {
	// Referência da classe principal
	private Gaia Gaia = null;
	
	public SampServerListener(Gaia g) {
		this.Gaia = g;
	}
	
	public void run() {
		ServerSocket serverSock = null;
		
		try {
			serverSock = new ServerSocket(this.Gaia.serverPort);
			
			System.out.println("[SA-MP Server] Escutando porta "+this.Gaia.serverPort+"...");
			
			while(true) {
				Socket clientSocket = serverSock.accept();
				System.out.println("[SA-MP Server] Nova conexão vinda de: "+clientSocket.getInetAddress().getHostAddress());
			}
		} catch (Exception ex) {
			if(serverSock != null) {
				try {
					serverSock.close();
				} catch (IOException e) {

				}
			}
			
			ex.printStackTrace();
		}
	}
}