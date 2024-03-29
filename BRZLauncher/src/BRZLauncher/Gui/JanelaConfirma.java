package BRZLauncher.Gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.MediaTracker;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import net.miginfocom.swing.MigLayout;
import BRZLauncher.Gaia;

public class JanelaConfirma extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7028585119550107847L;

	// Refer�ncia da classe principal
	private Gaia Gaia = null;
	
	// Vari�veis	
	public JFrame frame 					= null;
	public JPanel layout					= null;
	public Point mouseDownScreenCoords 	= null;
	public Point mouseDownCompCoords 	= null;
	public boolean cimaClicado			= false;
	public JButton botaoPronto			= null;

	public JanelaConfirma() {

	}
	
	public void criar(Gaia g) {
		this.Gaia = g;
		
		this.Gaia.Gui.setVisible(false);
		
		frame = new JFrame("BRZLauncher - Forma��o de Partida");

		frame.setSize(400, 550);
		frame.setMaximumSize(new Dimension(400, 550));
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
		frame.setUndecorated(true);
		frame.setBackground(new Color(0,0,0,0));
		
		/*
		frame.addWindowListener(new WindowAdapter(){
			  public void windowClosing(WindowEvent we){
				  BRZLauncher.deslogar();
			  }
		});
		*/
		
		// Layout
    	layout = new JPanel(new MigLayout("width 400!,height 550!,insets 0 00 0 0,novisualpadding,nogrid")) {
			private static final long serialVersionUID = 1L;
			protected void paintComponent(Graphics g) {
    		    super.paintComponent(g);
    		    g.drawImage(Gaia.bgimage, 1, 1, null);
    		  }
    	};
    	
    	MediaTracker mt = new MediaTracker(layout);
	    mt.addImage(this.Gaia.bgimage, 0);
	    try {
	    	mt.waitForAll();
	    } catch (InterruptedException e) {
	    	e.printStackTrace();
	    }
    	
	    layout.setBackground(new Color (0, 0, 0, 0));
    	/* **************** */
    	
    	// Cima
	    JLabel imagemFechar = new JLabel(new ImageIcon(this.Gaia.imagemFechar));
    	imagemFechar.setBorder(new EmptyBorder(5, 0, 5, 5));
    	imagemFechar.addMouseListener(new MouseAdapter()  {
    	    public void mouseClicked(MouseEvent e) {
    	    	int key = e.getModifiers();
    	    	
    	        if(key == MouseEvent.BUTTON1_MASK) {
    	        	sair();
    	        } 
    	    }
    	});
    	
    	JLabel imagemMinimizar = new JLabel(new ImageIcon(this.Gaia.imagemMinimizar));
    	imagemMinimizar.setBorder(new EmptyBorder(3, 2, 5, 5));
    	imagemMinimizar.addMouseListener(new MouseAdapter()  {  
    	    public void mouseClicked(MouseEvent e) {
    	    	int key = e.getModifiers();
    	    	
    	        if(key == MouseEvent.BUTTON1_MASK) {  
    	        	frame.setState(Frame.ICONIFIED);
    	        } 
    	    }
    	});
    	
	    JLabel BRZLogo2 = new JLabel(new ImageIcon(this.Gaia.BRZLogo2));
    	BRZLogo2.setBorder(new EmptyBorder(15, 15, 15, 15));
    	
    	JPanel janelaProntoCima = new JPanel(new MigLayout("top, width 400!, height 52!, insets 0 0 0 0"));
    	janelaProntoCima.setOpaque(false);
    	
    	janelaProntoCima.add(BRZLogo2, "left, top");
    	janelaProntoCima.add(imagemMinimizar, "pushx, alignx right, aligny top");
    	janelaProntoCima.add(imagemFechar, "alignx right, aligny top");
    	
    	janelaProntoCima.addMouseListener(new MouseAdapter()  {  
    	    public void mousePressed(MouseEvent e) {
    	    	int key = e.getModifiers();
    	    	
    	        if(key == MouseEvent.BUTTON1_MASK) {  
    	        	Gaia.guiJanelaConfirma.cimaClicado 				= true;
    	        	Gaia.guiJanelaConfirma.mouseDownScreenCoords 		= e.getLocationOnScreen();
    	        	Gaia.guiJanelaConfirma.mouseDownCompCoords 		= e.getPoint();
    	        } 
    	    }

    	    public void mouseReleased(MouseEvent e) {
    	    	int key = e.getModifiers();
    	    	
    	        if(key == MouseEvent.BUTTON1_MASK) {  
    	        	Gaia.guiJanelaConfirma.cimaClicado 				= false;
    	        	Gaia.guiJanelaConfirma.mouseDownScreenCoords 		= null;
    	        	Gaia.guiJanelaConfirma.mouseDownCompCoords 		= null;
    	        } 
    	    }
    	});
    	
    	janelaProntoCima.addMouseMotionListener(new MouseMotionListener() {
    		public void mouseDragged(MouseEvent e) {
    			if(Gaia.guiJanelaConfirma.cimaClicado) {
    	    		Point currCoords = e.getLocationOnScreen();
    	    		Gaia.guiJanelaConfirma.frame.setLocation(Gaia.guiJanelaConfirma.mouseDownScreenCoords.x + (currCoords.x - Gaia.guiJanelaConfirma.mouseDownScreenCoords.x) - Gaia.guiJanelaConfirma.mouseDownCompCoords.x, Gaia.guiJanelaConfirma.mouseDownScreenCoords.y + (currCoords.y - Gaia.guiJanelaConfirma.mouseDownScreenCoords.y) - Gaia.guiJanelaConfirma.mouseDownCompCoords.y);
    	    	}
    		}
    		
    		public void mouseMoved(MouseEvent e) {
    			
    		}
    	});
    	
    	layout.add(janelaProntoCima, "top, wrap");
    	/* **************** */
    	
    	frame.add(layout);
    	frame.pack();
    	frame.setVisible(true);
	}
	
	public void sair() {
		if(frame.isVisible()) {
			this.Gaia.Gui.setVisible(true);
			this.Gaia.estaEmFila = false;
			this.Gaia.estaPronto = false;
			
			this.Gaia.guiJanelaCliente.jogar.setText("<html><span style='text-align: center; font-size: 12px;'>Jogar!</span></html>");
			this.Gaia.guiJanelaCliente.jogar.setEnabled(true);
			this.Gaia.guiJanelaCliente.filaStatus.setVisible(false);
			this.Gaia.guiJanelaCliente.filaStatus.setText("<html><span style='color: white; font-size: 8px'>enviando requisi��o...</span></html>");
			this.Gaia.apiRequest.cmd("a=sairPartida");
	    	frame.dispose();
		}
	}
}
