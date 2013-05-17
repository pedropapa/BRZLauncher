package BRZLauncher;

import java.awt.Color;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JWindow;

public class inicializar {
	Timer 	timer 					= null;
	public static JWindow janela 	= null;
	int 	otype 					= 1;
	
	public inicializar() {
		janela = new JWindow();
		
		JLabel BRZLogoLarge = new JLabel(new ImageIcon(BRZLauncher.BRZLogoLarge));
		BRZLogoLarge.setOpaque(true);
		
		janela.setBounds(0, 0, 304, 245);
	
		janela.setForeground(Color.blue);
		janela.setBackground(Color.black);

		janela.setOpacity(0.0f);
		
		janela.setAlwaysOnTop(true);
		janela.setLocationRelativeTo(null);
		
		BRZLogoLarge.setBackground(new Color(255, 255, 255, 255));
		janela.add(BRZLogoLarge);
		
		janela.setBackground(new Color(255, 255, 255, 255));
		
		janela.setVisible(true);
		janela.pack();
		
		timer = new Timer();
		timer.schedule(new esconderLogo(), 0, 30);
	}
	
	public void verificarAtualizacao() {
		String path = BRZLauncher.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		String decodedPath;
		
		try {
			decodedPath = URLDecoder.decode(path, "UTF-8");
		} catch (IOException e) {
			decodedPath = null;
		}
		
		try {
			URL oracle 			= new URL(BRZLauncher.brzLauncherUrl+"?v");
	        URLConnection yc 	= oracle.openConnection();
	        BufferedReader in 	= new BufferedReader(new InputStreamReader(yc.getInputStream()));
	        
	        String inputLine;
	        String resposta = "";
	        
	        while ((inputLine = in.readLine()) != null) {
	            resposta += inputLine;
	        }
	        
	        in.close();
	        
	        if(!resposta.equals(BRZLauncher.versao)) {
	        	janela.setAlwaysOnTop(false);
	        	int res = JOptionPane.showConfirmDialog(BRZLauncher.frame, "Há uma atualização disponível para o BRZLauncher.\n\nSua versão: "+BRZLauncher.versao+"\nNova versão encontrada: "+resposta+"\n\nDeseja baixá-la agora? para continuar a utilizar o launcher a atualização é necessária.", "Nova atualização encontrada", JOptionPane.YES_NO_OPTION);
	        	if(res == JOptionPane.YES_OPTION) {
	        		if(baixarArquivo(decodedPath, BRZLauncher.brzLauncherUrl+"?d")) {
	        			Runtime rt = Runtime.getRuntime() ;
	        			rt.exec(decodedPath);
	        			System.exit(0);
	        		} else {
	        			JOptionPane.showMessageDialog(BRZLauncher.frame, "Não foi possível baixar a nova versão do BRZLauncher\n\nTente novamente mais tarde.", "Erro ao baixar uma nova versão", JOptionPane.ERROR_MESSAGE);
	        		}
	        	} else {
	        		System.exit(0);
	        	}
	        }
		} catch (IOException e) {
			JOptionPane.showMessageDialog(BRZLauncher.frame, "Não foi possível detectar se há uma nova versão do software.\n\nPara verificar novamente basta fechar e abrir o programa.", "Erro ao verificar a versão do software!", JOptionPane.ERROR_MESSAGE);
		}
		
		janela.setVisible(false);
      	janela.dispose();
      	new loginForm();
	}
	
	public boolean baixarArquivo(String filename, String urlString) throws IOException {
        BufferedInputStream in 	= null;
        FileOutputStream fout 	= null;
        
        try {
			in = new BufferedInputStream(new URL(urlString).openStream());
			fout = new FileOutputStream(filename);
			
			byte data[] = new byte[1024];
			int count;
			while ((count = in.read(data, 0, 1024)) != -1) {
				fout.write(data, 0, count);
			}
        } catch(IOException e) {
        	return false;
        }
        
        finally {
			if (in != null) {
			    in.close();
			}
			
			if (fout != null) {
				fout.close();
			}
        }
        
        return true;
    }

	
	class esconderLogo extends TimerTask {
		public float novoValor = 0;
		
	    public void run() {
	    	if(otype == 1) {
	    		novoValor = janela.getOpacity() + 0.01f;
	    		
	    		if(novoValor < 1.0f) {
	    			janela.setOpacity(novoValor);
	    		} else {
	    			otype = 2;
	    		}
	    	} else if(otype == 2) {
    			timer.cancel();
		      	
		      	verificarAtualizacao();
	    	}
	    }
	 }
}