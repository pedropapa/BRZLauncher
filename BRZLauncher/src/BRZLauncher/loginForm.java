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


public class loginForm {
	public static JButton 			inputEnviar 		= null;
	public static JTextField 		inputLogin 			= null;
	private static JPasswordField 	inputSenha 			= null;
	private boolean verificarDoRegistro					= false;
	public static String login							= null;
	public static String senha							= null;
	public static boolean enviarClicado					= false;
	public static JLabel tempLabel				= null;
	
	public loginForm() {
		BRZLauncher.frame = new JFrame("BRZLauncher ("+BRZLauncher.versaoNome+") - Login");
		BRZLauncher.frame.addWindowListener(new WindowAdapter(){
			  public void windowClosing(WindowEvent we){
				  BRZLauncher.deslogar();
			  }
		});

		BRZLauncher.frame.setSize(600, 350);
		BRZLauncher.frame.setMaximumSize(new Dimension(600, 350));
		BRZLauncher.frame.setLocationRelativeTo(null);
		BRZLauncher.frame.setResizable(false);
		BRZLauncher.frame.setUndecorated(true);
		BRZLauncher.frame.setBackground(new Color(0,0,0,0));
		
		BRZLauncher.frame.addWindowListener(new WindowAdapter(){
			  public void windowClosing(WindowEvent we){
				  BRZLauncher.deslogar();
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
    	janelaJogo.layout = new JPanel(new MigLayout("width 600!,height 350!,insets 0 0 0 0,novisualpadding,nogrid")) {
			private static final long serialVersionUID = 1L;
			protected void paintComponent(Graphics g) {
    		    super.paintComponent(g);
    		    g.drawImage(BRZLauncher.bgimage, 1, 1, null);
    		  }
    	};
    	
    	MediaTracker mt = new MediaTracker(janelaJogo.layout);
	    mt.addImage(BRZLauncher.bgimage, 0);
	    try {
	    	mt.waitForAll();
	    } catch (InterruptedException e) {
	    	e.printStackTrace();
	    }
    	
    	janelaJogo.layout.setBackground(new Color (0, 0, 0, 0));
    	/* **************** */
    	
    	// Cima
    	JLabel imagemFechar = new JLabel(new ImageIcon(BRZLauncher.imagemFechar));
    	imagemFechar.setBorder(new EmptyBorder(5, 0, 5, 5));
    	imagemFechar.addMouseListener(new MouseAdapter()  {  
    	    public void mouseClicked(MouseEvent e) {
    	    	int key = e.getModifiers();
    	    	
    	        if(key == MouseEvent.BUTTON1_MASK) {  
    	            BRZLauncher.deslogar();
    	        } 
    	    }
    	});
    	
    	JLabel imagemMinimizar = new JLabel(new ImageIcon(BRZLauncher.imagemMinimizar));
    	imagemMinimizar.setBorder(new EmptyBorder(3, 2, 5, 5));
    	imagemMinimizar.addMouseListener(new MouseAdapter()  {  
    	    public void mouseClicked(MouseEvent e) {
    	    	int key = e.getModifiers();
    	    	
    	        if(key == MouseEvent.BUTTON1_MASK) {  
    	            BRZLauncher.frame.setState(Frame.ICONIFIED);
    	        } 
    	    }
    	});
    	
    	JLabel BRZLogo2 = new JLabel(new ImageIcon(BRZLauncher.BRZLogo2));
    	BRZLogo2.setBorder(new EmptyBorder(15, 15, 15, 15));
    	
    	janelaJogo.cima = new JPanel(new MigLayout("top, width 600!, height 52!, insets 0 0 0 0"));
    	janelaJogo.cima.setOpaque(false);
    	
    	janelaJogo.cima.add(BRZLogo2, "left, top");
    	janelaJogo.cima.add(imagemMinimizar, "pushx, alignx right, aligny top");
    	janelaJogo.cima.add(imagemFechar, "alignx right, aligny top");
    	
    	janelaJogo.cima.addMouseListener(new MouseAdapter()  {  
    	    public void mousePressed(MouseEvent e) {
    	    	int key = e.getModifiers();
    	    	
    	        if(key == MouseEvent.BUTTON1_MASK) {  
    	        	janelaJogo.cimaClicado 			= true;
    	        	janelaJogo.mouseDownScreenCoords 	= e.getLocationOnScreen();
    	        	janelaJogo.mouseDownCompCoords 	= e.getPoint();
    	        } 
    	    }
    	    
    	    public void mouseReleased(MouseEvent e) {
    	    	int key = e.getModifiers();
    	    	
    	        if(key == MouseEvent.BUTTON1_MASK) {  
    	        	janelaJogo.cimaClicado 			= false;
    	        	janelaJogo.mouseDownScreenCoords 	= null;
    	        	janelaJogo.mouseDownCompCoords 	= null;
    	        } 
    	    }
    	});
    	
    	janelaJogo.cima.addMouseMotionListener(new MouseMotionListener() {
    		public void mouseDragged(MouseEvent e) {
    			if(janelaJogo.cimaClicado) {
    	    		Point currCoords = e.getLocationOnScreen();
    	    		BRZLauncher.frame.setLocation(janelaJogo.mouseDownScreenCoords.x + (currCoords.x - janelaJogo.mouseDownScreenCoords.x) - janelaJogo.mouseDownCompCoords.x, janelaJogo.mouseDownScreenCoords.y + (currCoords.y - janelaJogo.mouseDownScreenCoords.y) - janelaJogo.mouseDownCompCoords.y);
    	    	}
    		}

			@Override
			public void mouseMoved(MouseEvent e) {
			}
    	});
    	
    	janelaJogo.layout.add(janelaJogo.cima, "top, wrap");
    	/* ******************* */

    	BRZLauncher.formularioLogin = new JPanel(new MigLayout("alignx center, aligny top,width 578!"));
    	BRZLauncher.formularioLogin.setOpaque(false);
    	BRZLauncher.formularioLogin.setBorder(new EmptyBorder(15, 22, 15, 15));
    	
    	JLabel		labelLogin	= new JLabel("<html><span style='color: white'>Login:</span></html> ");
    	JLabel		labelSenha	= new JLabel("<html><span style='color: white'>Senha:</span></html> ");
    	
    	inputLogin 	= new JTextField(10);
    	inputLogin.setBorder(new EmptyBorder(5, 5, 5, 5));
    	inputSenha 	= new JPasswordField(10);
    	inputSenha.setBorder(new EmptyBorder(5, 5, 5, 5));
    	inputEnviar = new JButton("Login");
    	
    	BRZLauncher.formularioLogin.add(new JLabel("<html><span style='color: white;'>Insira o seu login e senha do RPG/Minigames/Competitivo abaixo:</span></html>"), "alignx center,wrap");
    	
    	JPanel conteudo = new JPanel(new MigLayout("alignx center, width 415!"));
    	conteudo.setOpaque(false);
    	
    	conteudo.add(labelLogin);
    	conteudo.add(inputLogin, "wrap");
    	conteudo.add(labelSenha);
    	conteudo.add(inputSenha, "wrap");
    	
    	BRZLauncher.formularioLogin.add(conteudo, "wrap");
    	
    	JPanel links = new JPanel(new MigLayout("width 450!, center"));
    	links.setOpaque(false);
    	
    	JLabel esqueceuSenhaLink = new JLabel("<html><a href='#'>Esqueceu sua senha?</a></html>");
    	esqueceuSenhaLink.setBorder(new EmptyBorder(0, 0, 0, 20));
    	esqueceuSenhaLink.addMouseListener(new MouseAdapter() {
    		public void mouseClicked(MouseEvent e) {
  			  	if (e.getButton() == MouseEvent.BUTTON1) {
  			  		if(!BRZLauncher.estaEmFila) {
  			  			try {
							Desktop.getDesktop().browse(new URI("http://samp.brazucas-server.com/recuperar"));
						} catch (IOException | URISyntaxException e1) {
							JOptionPane.showMessageDialog(BRZLauncher.frame, e1.getStackTrace());
						}
  			  		}
  			  	}
  		  	}
    	});
    	
    	JLabel registrarLink = new JLabel("<html><a href='#'>Não possui uma conta?</a></html>");
    	registrarLink.addMouseListener(new MouseAdapter() {
    		public void mouseClicked(MouseEvent e) {
  			  	if (e.getButton() == MouseEvent.BUTTON1) {
  			  		if(!BRZLauncher.estaEmFila) {
  			  			try {
							Desktop.getDesktop().browse(new URI("http://samp.brazucas-server.com/registro"));
						} catch (IOException | URISyntaxException e1) {
							JOptionPane.showMessageDialog(BRZLauncher.frame, e1.getStackTrace());
						}
  			  		}
  			  	}
  		  	}
    	});
    	
    	links.add(esqueceuSenhaLink);
    	links.add(registrarLink);
    	
    	BRZLauncher.formularioLogin.add(links, "wrap");
    	BRZLauncher.formularioLogin.add(inputEnviar, "center, wrap");
    	
    	// Baixo
    	JPanel baixo = new JPanel(new MigLayout("left"));
    	baixo.setBorder(new EmptyBorder(50, 0, 0, 0));
    	baixo.setOpaque(false);
    	baixo.add(new JLabel("<html><span style='color: white'>teste</span></html>"));
    	/* ************ */
    	
    	//BRZLauncher.formularioLogin.add(baixo);
    	janelaJogo.layout.add(BRZLauncher.formularioLogin);
    	
    	BRZLauncher.frame.add(janelaJogo.layout);
    	BRZLauncher.frame.pack();
    	BRZLauncher.frame.setVisible(true);
    	
    	final BRZLauncher.buscarRegistro registro = new BRZLauncher.buscarRegistro();
    	
    	inputEnviar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	if(!enviarClicado) {
            		enviarClicado = true;
            		
            		try {
						BRZLauncher.apiRequest = new apiRequest();
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
	            		JOptionPane.showMessageDialog(BRZLauncher.frame, "Preencha todos os campos!");
	            	} else {
	            		inputEnviar.setText("Cancelar");
	            		
	            		tempLabel = new JLabel("<html><span style='color: white; font-size: 8px'>Fazendo login...</span></html>");
	            		BRZLauncher.formularioLogin.add(tempLabel, "center, wrap");
	            		BRZLauncher.formularioLogin.updateUI();
	            		
	            		loginForm.login = login;
	            		loginForm.senha	= senhaMd5;
	            		
	            		inputLogin.setEditable(false);
	            		inputSenha.setEditable(false);
	
	            		BRZLauncher.apiRequest.cmd("a=login&u="+login+"&s="+senhaMd5);
	            	}
            	} else {
            		BRZLauncher.removerRegistros();
            		
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
