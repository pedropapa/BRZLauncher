package BRZLauncher;

import java.io.IOException;
import java.net.URISyntaxException;

import BRZLauncher.Gui.JanelaCliente;
import BRZLauncher.Gui.JanelaConfirma;
import BRZLauncher.Gui.JanelaLogin;

/**
 * TODO
 * 		O BRZLauncher n�o funcionar no windows xp porque o comando TASKLIST do prompt de comando n�o � pr� instalado neste sistema operacional
 */

public class BRZLauncher {
    public static void main (String args[]) throws IOException, URISyntaxException {
    	Gaia g = new Gaia();
    	
    	//g.Api = new apiRequest(g); // � setado na classe loginForm
    	g.Init 				= new Inicializar(g);
    	g.guiJanelaCliente 	= new JanelaCliente();
    	g.guiJanelaConfirma = new JanelaConfirma();
    	g.guiJanelaLogin 	= new JanelaLogin();
    	
    	g.init();
    	
    	g.Gui 				= (g.new Gui(g));
    }
}
