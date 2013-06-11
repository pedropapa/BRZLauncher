package BRZLauncher.Gui;

/**
 * TODO
 * Antiga classe "loginForm"
 */

import java.awt.Desktop;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import net.miginfocom.swing.MigLayout;
import BRZLauncher.BRZLauncher;
import BRZLauncher.Gaia;
import BRZLauncher.Api;

public class JanelaLogin extends JFrame {
	private static final long serialVersionUID = -710628035208732494L;
	
	// Variáveis dos elementos da interface
	public JButton 			inputEnviar 		= null;
	public JTextField 		inputLogin 			= null;
	private JPasswordField 	inputSenha 			= null;
	private boolean verificarDoRegistro			= false;
	public String login							= null;
	public String senha							= null;
	public boolean enviarClicado				= false;
	public JLabel tempLabel						= null;

	public void criar(Gaia g) {
		final Gaia Gaia = g;
   
    	Gaia.formularioLogin = new JPanel(new MigLayout("alignx center, aligny top,width 578!"));
    	Gaia.formularioLogin.setOpaque(false);
    	Gaia.formularioLogin.setBorder(new EmptyBorder(15, 22, 15, 15));
    	
    	JLabel		labelLogin	= new JLabel("<html><span style='color: white'>Login:</span></html> ");
    	JLabel		labelSenha	= new JLabel("<html><span style='color: white'>Senha:</span></html> ");
    	
    	inputLogin 	= new JTextField(10);
    	inputLogin.setBorder(new EmptyBorder(5, 5, 5, 5));
    	inputSenha 	= new JPasswordField(10);
    	inputSenha.setBorder(new EmptyBorder(5, 5, 5, 5));
    	inputEnviar = new JButton("Login");
    	
    	Gaia.formularioLogin.add(new JLabel("<html><span style='color: white;'>Insira o seu login e senha do RPG/Minigames/Competitivo abaixo:</span></html>"), "alignx center,wrap");
    	
    	JPanel conteudo = new JPanel(new MigLayout("alignx center, width 415!"));
    	conteudo.setOpaque(false);
    	
    	conteudo.add(labelLogin);
    	conteudo.add(inputLogin, "wrap");
    	conteudo.add(labelSenha);
    	conteudo.add(inputSenha, "wrap");
    	
    	Gaia.formularioLogin.add(conteudo, "wrap");
    	
    	JPanel links = new JPanel(new MigLayout("width 450!, center"));
    	links.setOpaque(false);
    	
    	final Gaia h = Gaia;
    	
    	JLabel esqueceuSenhaLink = new JLabel("<html><a href='#'>Esqueceu sua senha?</a></html>");
    	esqueceuSenhaLink.setBorder(new EmptyBorder(0, 0, 0, 20));
    	esqueceuSenhaLink.addMouseListener(new MouseAdapter() {
    		public void mouseClicked(MouseEvent e) {
  			  	if (e.getButton() == MouseEvent.BUTTON1) {
  			  		if(!Gaia.estaEmFila) {
  			  			try {
							Desktop.getDesktop().browse(new URI("http://samp.brazucas-server.com/recuperar"));
						} catch (IOException | URISyntaxException e1) {
							JOptionPane.showMessageDialog(h.guiJanelaLogin, e1.getStackTrace());
						}
  			  		}
  			  	}
  		  	}
    	});
    	
    	JLabel registrarLink = new JLabel("<html><a href='#'>Não possui uma conta?</a></html>");

    	registrarLink.addMouseListener(new MouseAdapter() {
    		public void mouseClicked(MouseEvent e) {
  			  	if (e.getButton() == MouseEvent.BUTTON1) {
  			  		if(!Gaia.estaEmFila) {
  			  			try {
							Desktop.getDesktop().browse(new URI("http://samp.brazucas-server.com/registro"));
						} catch (IOException | URISyntaxException e1) {
							JOptionPane.showMessageDialog(h.guiJanelaLogin, e1.getStackTrace());
						}
  			  		}
  			  	}
  		  	}
    	});
    	
    	links.add(esqueceuSenhaLink);
    	links.add(registrarLink);
    	
    	Gaia.formularioLogin.add(links, "wrap");
    	Gaia.formularioLogin.add(inputEnviar, "center, wrap");
    	
    	// Baixo
    	JPanel baixo = new JPanel(new MigLayout("left"));
    	baixo.setBorder(new EmptyBorder(50, 0, 0, 0));
    	baixo.setOpaque(false);
    	baixo.add(new JLabel("<html><span style='color: white'>teste</span></html>"));
    	/* ************ */
    	
    	//BRZLauncher.formularioLogin.add(baixo);
    	Gaia.Gui.layout.add(Gaia.formularioLogin);
    	
    	Gaia.Gui.Core.pack();
    	Gaia.Gui.layout.setVisible(true);
    	Gaia.Gui.Core.setVisible(true);

    	final Gaia.buscarRegistro registro = Gaia.new buscarRegistro();
    	
    	inputEnviar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	if(!enviarClicado) {
            		enviarClicado = true;
            		
            		try {
            			Gaia.apiRequest = new Api(Gaia);
					} catch (HeadlessException | IOException e2) {

					}
            		
	            	String loginF, senhaMd5;
	            	
	            	if(!verificarDoRegistro) {
		            	loginF 				= inputLogin.getText();
		            	byte[] senhaBytes 	= new String(inputSenha.getPassword()).getBytes();
		            	
		            	MessageDigest md;
						try {
							md = MessageDigest.getInstance("MD5");
						} catch (NoSuchAlgorithmException e1) {
							md = null;
						}
		            	senhaMd5 = new BigInteger(1, md.digest(senhaBytes)).toString(16);
	            	} else {
	            		loginF 		= registro.registroLogin;
	            		senhaMd5 	= registro.registroSenha;
	            	}
	
	            	if(inputSenha.getPassword().length == 0 || loginF.length() == 0) {
	            		JOptionPane.showMessageDialog(h.guiJanelaLogin, "Preencha todos os campos!");
	            	} else {
	            		inputEnviar.setText("Cancelar");
	            		
	            		tempLabel = new JLabel("<html><span style='color: white; font-size: 8px'>Fazendo login...</span></html>");
	            		Gaia.formularioLogin.add(tempLabel, "center, wrap");
	            		Gaia.formularioLogin.updateUI();
	            		
	            		login 	= loginF;
	            		senha	= senhaMd5;
	            		
	            		inputLogin.setEditable(false);
	            		inputSenha.setEditable(false);
	
	            		Gaia.apiRequest.cmd("a=login&u="+login+"&s="+senhaMd5);
	            	}
            	} else {
            		Gaia.removerRegistros();
            		
            		String path = BRZLauncher.class.getProtectionDomain().getCodeSource().getLocation().getPath();
            		String decodedPath;
            		
            		try {
            			decodedPath = URLDecoder.decode(path, "UTF-8");
            		} catch (IOException e2) {
            			decodedPath = null;
            		}
            		
            		Runtime rt = Runtime.getRuntime() ;
        			try {
						rt.exec(decodedPath);
					} catch (IOException e1) {
						
					}
        			
        			System.exit(0);
            	}
            }
        });
    	
    	if(registro.registroLogin != null && registro.registroSenha != null) {
    		this.inputLogin.setText(registro.registroLogin);
    		this.inputSenha.setText(registro.registroSenha);
    		this.verificarDoRegistro = true;
    		
    		this.inputEnviar.doClick(MouseEvent.BUTTON1);
    	}
	}
}
