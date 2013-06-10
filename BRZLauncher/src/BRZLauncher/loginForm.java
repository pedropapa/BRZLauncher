package BRZLauncher;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import net.miginfocom.swing.MigLayout;

public class loginForm extends Gaia {
	// Referência da classe principal
	private Gaia Gaia = null;
	
	// Variáveis
	public JButton 			inputEnviar 		= null;
	public JTextField 		inputLogin 			= null;
	private JPasswordField 	inputSenha 			= null;
	private boolean verificarDoRegistro					= false;
	public String login							= null;
	public String senha							= null;
	public boolean enviarClicado					= false;
	public JLabel tempLabel				= null;
	
	public loginForm(Gaia g) {
		this.Gaia = g;
	}
	
	public void abrir() {
		this.Gaia.frame = new JFrame("BRZLauncher ("+this.Gaia.versaoNome+") - Login");
		this.Gaia.frame.addWindowListener(new WindowAdapter(){
			  public void windowClosing(WindowEvent we){
				  Gaia.deslogar();
			  }
		});

		this.Gaia.frame.setSize(600, 350);
		this.Gaia.frame.setMaximumSize(new Dimension(600, 350));
		this.Gaia.frame.setLocationRelativeTo(null);
		this.Gaia.frame.setResizable(false);
		this.Gaia.frame.setUndecorated(true);
		this.Gaia.frame.setBackground(new Color(0,0,0,0));
		
		this.Gaia.frame.addWindowListener(new WindowAdapter(){
			  public void windowClosing(WindowEvent we){
				  Gaia.deslogar();
			  }
		});
    
    	JMenuBar menuBar = new JMenuBar();
    	
    	JMenu menuBarLabel1 = new JMenu("Arquivo");
    	JMenu menuBarLabel2 = new JMenu("Ajuda");
    	
    	menuBar.add(menuBarLabel1);
    	menuBar.add(menuBarLabel2);
    	
    	JMenuItem menuBarLabelItem1 = new JMenuItem("Opções");
    	JMenuItem menuBarLabelItem2 = new JMenuItem("Sobre");
    	
    	menuBarLabel1.add(menuBarLabelItem1);
    	menuBarLabel2.add(menuBarLabelItem2);
    	
    	// Layout
    	this.Gaia.janelaJogo.layout = new JPanel(new MigLayout("width 600!,height 350!,insets 0 0 0 0,novisualpadding,nogrid")) {
			private static final long serialVersionUID = 1L;
			protected void paintComponent(Graphics g) {
    		    super.paintComponent(g);
    		    g.drawImage(Gaia.bgimage, 1, 1, null);
    		  }
    	};
    	
    	MediaTracker mt = new MediaTracker(this.Gaia.janelaJogo.layout);
	    mt.addImage(this.Gaia.bgimage, 0);
	    try {
	    	mt.waitForAll();
	    } catch (InterruptedException e) {
	    	e.printStackTrace();
	    }
    	
    	this.Gaia.janelaJogo.layout.setBackground(new Color (0, 0, 0, 0));
    	/* **************** */
    	
    	// Cima
    	JLabel imagemFechar = new JLabel(new ImageIcon(this.Gaia.imagemFechar));
    	imagemFechar.setBorder(new EmptyBorder(5, 0, 5, 5));
    	imagemFechar.addMouseListener(new MouseAdapter()  {  
    	    public void mouseClicked(MouseEvent e) {
    	    	int key = e.getModifiers();
    	    	
    	        if(key == MouseEvent.BUTTON1_MASK) {  
    	        	Gaia.deslogar();
    	        } 
    	    }
    	});
    	
    	JLabel imagemMinimizar = new JLabel(new ImageIcon(this.Gaia.imagemMinimizar));
    	imagemMinimizar.setBorder(new EmptyBorder(3, 2, 5, 5));
    	imagemMinimizar.addMouseListener(new MouseAdapter()  {  
    	    public void mouseClicked(MouseEvent e) {
    	    	int key = e.getModifiers();
    	    	
    	        if(key == MouseEvent.BUTTON1_MASK) {  
    	            Gaia.frame.setState(Frame.ICONIFIED);
    	        } 
    	    }
    	});
    	
    	JLabel BRZLogo2 = new JLabel(new ImageIcon(this.Gaia.BRZLogo2));
    	BRZLogo2.setBorder(new EmptyBorder(15, 15, 15, 15));
    	
    	this.Gaia.janelaJogo.cima = new JPanel(new MigLayout("top, width 600!, height 52!, insets 0 0 0 0"));
    	this.Gaia.janelaJogo.cima.setOpaque(false);
    	
    	this.Gaia.janelaJogo.cima.add(BRZLogo2, "left, top");
    	this.Gaia.janelaJogo.cima.add(imagemMinimizar, "pushx, alignx right, aligny top");
    	this.Gaia.janelaJogo.cima.add(imagemFechar, "alignx right, aligny top");
    	
    	this.Gaia.janelaJogo.cima.addMouseListener(new MouseAdapter()  {  
    	    public void mousePressed(MouseEvent e) {
    	    	int key = e.getModifiers();
    	    	
    	        if(key == MouseEvent.BUTTON1_MASK) {  
    	        	Gaia.janelaJogo.cimaClicado 			= true;
    	        	Gaia.janelaJogo.mouseDownScreenCoords 	= e.getLocationOnScreen();
    	        	Gaia.janelaJogo.mouseDownCompCoords 	= e.getPoint();
    	        } 
    	    }
    	    
    	    public void mouseReleased(MouseEvent e) {
    	    	int key = e.getModifiers();
    	    	
    	        if(key == MouseEvent.BUTTON1_MASK) {  
    	        	Gaia.janelaJogo.cimaClicado 			= false;
    	        	Gaia.janelaJogo.mouseDownScreenCoords 	= null;
    	        	Gaia.janelaJogo.mouseDownCompCoords 	= null;
    	        } 
    	    }
    	});
    	
    	this.Gaia.janelaJogo.cima.addMouseMotionListener(new MouseMotionListener() {
    		public void mouseDragged(MouseEvent e) {
    			if(Gaia.janelaJogo.cimaClicado) {
    	    		Point currCoords = e.getLocationOnScreen();
    	    		Gaia.frame.setLocation(Gaia.janelaJogo.mouseDownScreenCoords.x + (currCoords.x - Gaia.janelaJogo.mouseDownScreenCoords.x) - Gaia.janelaJogo.mouseDownCompCoords.x, Gaia.janelaJogo.mouseDownScreenCoords.y + (currCoords.y - Gaia.janelaJogo.mouseDownScreenCoords.y) - Gaia.janelaJogo.mouseDownCompCoords.y);
    	    	}
    		}

			@Override
			public void mouseMoved(MouseEvent e) {
			}
    	});
    	
    	this.Gaia.janelaJogo.layout.add(Gaia.janelaJogo.cima, "top, wrap");
    	/* ******************* */

    	this.Gaia.formularioLogin = new JPanel(new MigLayout("alignx center, aligny top,width 578!"));
    	this.Gaia.formularioLogin.setOpaque(false);
    	this.Gaia.formularioLogin.setBorder(new EmptyBorder(15, 22, 15, 15));
    	
    	JLabel		labelLogin	= new JLabel("<html><span style='color: white'>Login:</span></html> ");
    	JLabel		labelSenha	= new JLabel("<html><span style='color: white'>Senha:</span></html> ");
    	
    	inputLogin 	= new JTextField(10);
    	inputLogin.setBorder(new EmptyBorder(5, 5, 5, 5));
    	inputSenha 	= new JPasswordField(10);
    	inputSenha.setBorder(new EmptyBorder(5, 5, 5, 5));
    	inputEnviar = new JButton("Login");
    	
    	this.Gaia.formularioLogin.add(new JLabel("<html><span style='color: white;'>Insira o seu login e senha do RPG/Minigames/Competitivo abaixo:</span></html>"), "alignx center,wrap");
    	
    	JPanel conteudo = new JPanel(new MigLayout("alignx center, width 415!"));
    	conteudo.setOpaque(false);
    	
    	conteudo.add(labelLogin);
    	conteudo.add(inputLogin, "wrap");
    	conteudo.add(labelSenha);
    	conteudo.add(inputSenha, "wrap");
    	
    	this.Gaia.formularioLogin.add(conteudo, "wrap");
    	
    	JPanel links = new JPanel(new MigLayout("width 450!, center"));
    	links.setOpaque(false);
    	
    	JLabel esqueceuSenhaLink = new JLabel("<html><a href='#'>Esqueceu sua senha?</a></html>");
    	esqueceuSenhaLink.setBorder(new EmptyBorder(0, 0, 0, 20));
    	esqueceuSenhaLink.addMouseListener(new MouseAdapter() {
    		public void mouseClicked(MouseEvent e) {
  			  	if (e.getButton() == MouseEvent.BUTTON1) {
  			  		if(!Gaia.estaEmFila) {
  			  			try {
							Desktop.getDesktop().browse(new URI("http://samp.brazucas-server.com/recuperar"));
						} catch (IOException | URISyntaxException e1) {
							JOptionPane.showMessageDialog(Gaia.frame, e1.getStackTrace());
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
							JOptionPane.showMessageDialog(Gaia.frame, e1.getStackTrace());
						}
  			  		}
  			  	}
  		  	}
    	});
    	
    	links.add(esqueceuSenhaLink);
    	links.add(registrarLink);
    	
    	this.Gaia.formularioLogin.add(links, "wrap");
    	this.Gaia.formularioLogin.add(inputEnviar, "center, wrap");
    	
    	// Baixo
    	JPanel baixo = new JPanel(new MigLayout("left"));
    	baixo.setBorder(new EmptyBorder(50, 0, 0, 0));
    	baixo.setOpaque(false);
    	baixo.add(new JLabel("<html><span style='color: white'>teste</span></html>"));
    	/* ************ */
    	
    	//BRZLauncher.formularioLogin.add(baixo);
    	this.Gaia.janelaJogo.layout.add(this.Gaia.formularioLogin);
    	
    	this.Gaia.frame.add(this.Gaia.janelaJogo.layout);
    	this.Gaia.frame.pack();
    	this.Gaia.frame.setVisible(true);
    	
    	final Gaia.buscarRegistro registro = new Gaia.buscarRegistro();
    	
    	inputEnviar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	if(!enviarClicado) {
            		enviarClicado = true;
            		
            		try {
            			Gaia.apiRequest = new apiRequest(Gaia);
					} catch (HeadlessException | IOException e2) {

					}
            		
	            	String login, senhaMd5;
	            	
	            	if(!verificarDoRegistro) {
		            	login 				= inputLogin.getText();
		            	byte[] senhaBytes 	= new String(inputSenha.getPassword()).getBytes();
		            	
		            	MessageDigest md;
						try {
							md = MessageDigest.getInstance("MD5");
						} catch (NoSuchAlgorithmException e1) {
							md = null;
						}
		            	senhaMd5 = new BigInteger(1, md.digest(senhaBytes)).toString(16);
	            	} else {
	            		login 		= registro.registroLogin;
	            		senhaMd5 	= registro.registroSenha;
	            	}
	
	            	if(inputSenha.getPassword().length == 0 || login.length() == 0) {
	            		JOptionPane.showMessageDialog(Gaia.frame, "Preencha todos os campos!");
	            	} else {
	            		inputEnviar.setText("Cancelar");
	            		
	            		tempLabel = new JLabel("<html><span style='color: white; font-size: 8px'>Fazendo login...</span></html>");
	            		Gaia.formularioLogin.add(tempLabel, "center, wrap");
	            		Gaia.formularioLogin.updateUI();
	            		
	            		Gaia.loginForm.login = login;
	            		Gaia.loginForm.senha	= senhaMd5;
	            		
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
    		inputLogin.setText(registro.registroLogin);
    		inputSenha.setText(registro.registroSenha);
    		verificarDoRegistro = true;
    		
    		inputEnviar.doClick(MouseEvent.BUTTON1);
    	}
	}
}
