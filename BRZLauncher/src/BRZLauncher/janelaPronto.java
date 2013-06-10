package BRZLauncher;

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

public class janelaPronto extends Gaia {
	// Referência da classe principal
	private Gaia Gaia = null;
	
	// Variáveis	
	public JFrame frame 					= null;
	public JPanel layout					= null;
	public Point mouseDownScreenCoords 	= null;
	public Point mouseDownCompCoords 	= null;
	public boolean cimaClicado			= false;
	public JButton botaoPronto			= null;
	
	public janelaPronto(Gaia g) {
		this.Gaia = g;
	}
	
	public void abrir() {
		this.Gaia.frame.setVisible(false);
		
		frame = new JFrame("BRZLauncher - Formação de Partida");

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
			/**
			 * 
			 */
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
    	        	janelaPronto.cimaClicado 				= true;
    	        	janelaPronto.mouseDownScreenCoords 		= e.getLocationOnScreen();
    	        	janelaPronto.mouseDownCompCoords 		= e.getPoint();
    	        } 
    	    }

    	    public void mouseReleased(MouseEvent e) {
    	    	int key = e.getModifiers();
    	    	
    	        if(key == MouseEvent.BUTTON1_MASK) {  
    	        	janelaPronto.cimaClicado 				= false;
    	        	janelaPronto.mouseDownScreenCoords 		= null;
    	        	janelaPronto.mouseDownCompCoords 		= null;
    	        } 
    	    }
    	});
    	
    	janelaProntoCima.addMouseMotionListener(new MouseMotionListener() {
    		public void mouseDragged(MouseEvent e) {
    			if(janelaPronto.cimaClicado) {
    	    		Point currCoords = e.getLocationOnScreen();
    	    		janelaPronto.frame.setLocation(janelaPronto.mouseDownScreenCoords.x + (currCoords.x - janelaPronto.mouseDownScreenCoords.x) - janelaPronto.mouseDownCompCoords.x, janelaPronto.mouseDownScreenCoords.y + (currCoords.y - janelaPronto.mouseDownScreenCoords.y) - janelaPronto.mouseDownCompCoords.y);
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
			this.Gaia.frame.setVisible(true);
			this.Gaia.estaEmFila = false;
			this.Gaia.estaPronto = false;
			
			janelaJogo.jogar.setText("<html><span style='text-align: center; font-size: 12px;'>Jogar!</span></html>");
			janelaJogo.jogar.setEnabled(true);
			janelaJogo.filaStatus.setVisible(false);
			janelaJogo.filaStatus.setText("<html><span style='color: white; font-size: 8px'>enviando requisição...</span></html>");
			this.Gaia.apiRequest.cmd("a=sairPartida");
	    	frame.dispose();
		}
	}
}
