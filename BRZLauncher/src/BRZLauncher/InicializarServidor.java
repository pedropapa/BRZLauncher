package BRZLauncher;

import java.io.IOException;
import java.io.PrintWriter;

import javax.swing.JOptionPane;

public class InicializarServidor implements Runnable {
	// Referência da classe principal
	private Gaia Gaia = null;
	
	public InicializarServidor(Gaia g) {
		this.Gaia = g;
	}
	
	public void run() {
		try {    			
	    	String executavelDir = "\""+ this.Gaia.tempFolder + "servidor SA-MP";
	    	
	    	Process child = Runtime.getRuntime().exec(new String[] {"cmd"});
	        
	        new Thread(new SyncPipe(child.getErrorStream(), System.err)).start();
	        new Thread(new SyncPipe(child.getInputStream(), System.out)).start();
	        PrintWriter stdin = new PrintWriter(child.getOutputStream());
	        stdin.println("cd "+executavelDir);
	        stdin.println("samp-server.exe");
	        stdin.flush();
	        stdin.close();

			System.out.println("Servidor inicializado");
			
			child.waitFor();
			
			this.Gaia.quandoServidorFechar();
		} catch (IOException | InterruptedException e) {
			JOptionPane.showMessageDialog(this.Gaia.Gui, e.getStackTrace());
		}
	}
}