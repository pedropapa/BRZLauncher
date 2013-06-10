package BRZLauncher;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * TODO
 * 		O BRZLauncher não funcionar no windows xp porque o comando TASKLIST do prompt de comando não é pré instalado neste sistema operacional
 */

public class BRZLauncher {
    public static void main (String args[]) throws IOException, URISyntaxException {
    	Gaia g = new Gaia();
    	
    	//g.Api = new apiRequest(g); // É setado na classe loginForm
    	g.loginForm = new loginForm(g);
    	g.Init = new inicializar(g);
    	g.janelaJogo = new janelaJogo(g);
    	g.janelaPronto = new janelaPronto(g);
    	
    	g.init();
    }
}
