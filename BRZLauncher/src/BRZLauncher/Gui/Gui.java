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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import Variaveis.ArquivoVars;

import net.miginfocom.swing.MigLayout;
import BRZLauncher.Gaia;

public class Gui extends JFrame {
	private static final long serialVersionUID = 1L;
	
	public JPanel layout 				= null;
	public JFrame Core					= null;
	public JPanel cima					= null;
	public boolean cimaClicado			= false;
	public Point mouseDownScreenCoords 	= null;
	public Point mouseDownCompCoords 	= null;
	
	public Gui(Gaia g) {
		this.Core = this.montar(g);
		this.Core.add(this.layout);
	}
	
	public JFrame montar(Gaia g) {
		final Gaia Gaia = g;
		
		addWindowListener(new WindowAdapter(){
			  public void windowClosing(WindowEvent we){
				  Gaia.deslogar();
			  }
		});

		setSize(600, 350);
		setMaximumSize(new Dimension(600, 350));
		setLocationRelativeTo(null);
		setResizable(false);
		setUndecorated(true);
		setBackground(new Color(0,0,0,0));
		
		addWindowListener(new WindowAdapter(){
			  public void windowClosing(WindowEvent we){
				  Gaia.deslogar();
			  }
		});
		
		this.layout = new JPanel(new MigLayout("width 600!,height 350!,insets 0 0 0 0,novisualpadding,nogrid")) {
			private static final long serialVersionUID = 1L;
			protected void paintComponent(Graphics g) {
    		    super.paintComponent(g);
    		    g.drawImage(Gaia.bgimage, 1, 1, null);
    		  }
    	};
    	
    	MediaTracker mt = new MediaTracker(this.layout);
	    mt.addImage(Gaia.bgimage, 0);
	    try {
	    	mt.waitForAll();
	    } catch (InterruptedException e) {
	    	e.printStackTrace();
	    }
    	
	    this.layout.setBackground(new Color (0, 0, 0, 0));
	    
	    JLabel imagemFechar = new JLabel(new ImageIcon(Gaia.imagemFechar));
    	imagemFechar.setBorder(new EmptyBorder(5, 0, 5, 5));
    	imagemFechar.addMouseListener(new MouseAdapter()  {  
    	    public void mouseClicked(MouseEvent e) {
    	    	int key = e.getModifiers();
    	    	
    	        if(key == MouseEvent.BUTTON1_MASK) {  
    	        	Gaia.deslogar();
    	        } 
    	    }
    	});
    	
    	JLabel imagemMinimizar = new JLabel(new ImageIcon(Gaia.imagemMinimizar));
    	imagemMinimizar.setBorder(new EmptyBorder(3, 2, 5, 5));
    	imagemMinimizar.addMouseListener(new MouseAdapter()  {  
    	    public void mouseClicked(MouseEvent e) {
    	    	int key = e.getModifiers();
    	    	
    	        if(key == MouseEvent.BUTTON1_MASK) {  
    	            setState(Frame.ICONIFIED);
    	        } 
    	    }
    	});
    	
    	JLabel BRZLogo2 = new JLabel(new ImageIcon(Gaia.BRZLogo2));
    	BRZLogo2.setBorder(new EmptyBorder(15, 15, 15, 15));
    	
    	this.cima = new JPanel(new MigLayout("top, width 600!, height 52!, insets 0 0 0 0"));
    	this.cima.setOpaque(false);
    	
    	this.cima.add(BRZLogo2, "left, top");
    	this.cima.add(imagemMinimizar, "pushx, alignx right, aligny top");
    	this.cima.add(imagemFechar, "alignx right, aligny top");
    	
    	this.cima.addMouseListener(new MouseAdapter()  {  
    	    public void mousePressed(MouseEvent e) {
    	    	int key = e.getModifiers();
    	    	
    	        if(key == MouseEvent.BUTTON1_MASK) {  
    	        	cimaClicado 									= true;
    	        	mouseDownScreenCoords 	= e.getLocationOnScreen();
    	        	mouseDownCompCoords 		= e.getPoint();
    	        } 
    	    }
    	    
    	    public void mouseReleased(MouseEvent e) {
    	    	int key = e.getModifiers();
    	    	
    	        if(key == MouseEvent.BUTTON1_MASK) {  
    	        	cimaClicado 			= false;
    	        	mouseDownScreenCoords 	= null;
    	        	mouseDownCompCoords 	= null;
    	        } 
    	    }
    	});
    	
    	this.cima.addMouseMotionListener(new MouseMotionListener() {
    		public void mouseDragged(MouseEvent e) {
    			if(cimaClicado) {
    	    		Point currCoords = e.getLocationOnScreen();
    	    		setLocation(mouseDownScreenCoords.x + (currCoords.x - mouseDownScreenCoords.x) - mouseDownCompCoords.x, mouseDownScreenCoords.y + (currCoords.y - mouseDownScreenCoords.y) - mouseDownCompCoords.y);
    	    	}
    		}

			@Override
			public void mouseMoved(MouseEvent e) {
			}
    	});
    	
    	this.layout.add(this.cima, "top, wrap");
		
		return this;
	}
	
	public String arquivoVarsEmStr(HashMap<String, ArquivoVars> vars) {
    	String output = null;
    	
    	Iterator<Entry<String, ArquivoVars>> it = vars.entrySet().iterator();
    	while(it.hasNext()) {
    		Entry<String, ArquivoVars> entry = it.next();
    		String ind 		= entry.getKey();
    		ArquivoVars val = entry.getValue();
    		
    		output += ind + "="+ val + "\n";
    	}
    	
    	return output;
    }
}