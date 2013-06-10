package BRZLauncher;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class janelaJogo extends Gaia {
	// Referência da classe principal
	private Gaia Gaia = null;
	
	
	// Variáveis
	public JPanel layout 						= null;
	public JPanel cima							= null;
	public JLabel temp							= null;
	public JLabel jogadoresOnline 				= null;
	public JLabel servidoresDisponiveis 		= null;
	public JLabel servidoresTotais 				= null;
	public JLabel tempoAproximado	 			= null;
	public JLabel plPartidasDisputadas			= null;
	public JLabel plVitorias					= null;
	public JLabel plVezesPunido					= null;
	public JLabel plMatou						= null;
	public JLabel plMorreu						= null;
	public JLabel plRanking						= null;
	public JButton jogar						= null;
	public JLabel filaStatus					= new JLabel("<html><span style='color: white; font-size: 8px'>enviando requisição...</span></html>");
	public JPanel logados						= null;
	public JPanel mensagens						= null;
	public Point mouseDownScreenCoords 			= null;
	public Point mouseDownCompCoords 			= null;
	public HashMap<String, JLabel> logadosLabels = new HashMap<String, JLabel>();
	
	public boolean cimaClicado			= false;

	public janelaJogo(Gaia g) {
		this.Gaia = g;
	}
	
	public void abrir() throws IOException {
		if(this.Gaia.estaLogado()) {
			verificarJogoLocal();
			
			layout.remove(this.Gaia.formularioLogin);

			// Cima
	    	JMenuBar menuBar = new JMenuBar();
	    	
	    	JMenu menuBarLabel1 = new JMenu("Arquivo");
	    	JMenu menuBarLabel2 = new JMenu("Ajuda");
	    	
	    	menuBar.add(menuBarLabel1);
	    	menuBar.add(menuBarLabel2);
	    	
	    	JMenuItem menuBarLabelItem1 = new JMenuItem("Sair");
	    	JMenuItem menuBarLabelItem2 = new JMenuItem("Sobre");
	    	
	    	menuBarLabelItem1.addActionListener(new ActionListener() {
	    	    @Override
	    	    public void actionPerformed(ActionEvent arg0) {
	    	    	Gaia.removerRegistros();
	    	    	Gaia.deslogar();
	    	    }
	    	});

	    	
	    	menuBarLabel1.add(menuBarLabelItem1);
	    	menuBarLabel2.add(menuBarLabelItem2);
	    	/* **************************** */

	    	temp 	= new JLabel("<html><span style='color: white'>Obtendo informações da API...</span></html>");
	    	temp.setBorder(new EmptyBorder(15, 22, 15, 15));
	    	layout.add(temp, "left");
	    	
	    	this.Gaia.apiRequest.cmd("a=inicializar");
	    	/* **************************** */
	    	
	    	this.Gaia.frame.add(layout);
	    	this.Gaia.frame.pack();
	    	this.Gaia.frame.setVisible(true);
		}
	}
	
	private void verificarJogoLocal() throws IOException {
		String local 		= null;
		String diretorio 	= null;
		
		try {
    		local = WinRegistry.readString(WinRegistry.HKEY_CURRENT_USER, "SOFTWARE\\SAMP", "gta_sa_exe");
		} catch(IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
			local = null;
			System.out.println(e.getStackTrace());
		}

		if(local == null) {
			JOptionPane.showMessageDialog(this.Gaia.frame, "O GTA não foi encontrado em seu computador!\n\nPor favor selecione manualmente o local do executável do jogo.", "Jogo não encontrado", JOptionPane.ERROR_MESSAGE);

			this.Gaia.fecharJanela();
			
			try {
				selecionarJogoLocal();
			} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
				JOptionPane.showMessageDialog(this.Gaia.frame, "Erro encontrado!\n\n"+e.getStackTrace()+"\n\nTente fechar e abrir o programa novamente.", "Caminho incorreto.", JOptionPane.ERROR_MESSAGE);
			}
		} else {
			this.Gaia.GTAPath = local;
			
			diretorio 	= local.replace("gta_sa.exe", "");
        	local 		= diretorio + "samp.exe";
        	
        	File arquivo = new File(local);
        	if(arquivo.exists()) {
        		this.Gaia.SAMPPath = local;
        	} else {
        		JOptionPane.showMessageDialog(this.Gaia.frame, "Não foi encontrado uma instalação do SA-MP em seu computador.\n\nEle deve ser instalado no mesmo diretório do jogo.", "samp.exe não encontrado", JOptionPane.ERROR_MESSAGE);
        		System.exit(0);
        	}
		}
	}
	
	private void selecionarJogoLocal() throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, IOException {
		String local		= null;
		String diretorio	= null;
		JFileChooser fc 	= new JFileChooser();
		
		JLabel c 		= new JLabel();
		int returnVal 	= fc.showOpenDialog(c);
		
		if(returnVal == JFileChooser.APPROVE_OPTION) {
			local 		= fc.getSelectedFile().getAbsolutePath();
			diretorio 	= null;
	        
	        if(!fc.getSelectedFile().getName().equalsIgnoreCase("gta_sa.exe")) {
	        	JOptionPane.showMessageDialog(this.Gaia.frame, "Este não é um caminho válido para o executável do jogo.", "Caminho incorreto.", JOptionPane.ERROR_MESSAGE);
	        	selecionarJogoLocal();
	        } else {
	        	this.Gaia.GTAPath = local;
	        	
	        	WinRegistry.createKey(WinRegistry.HKEY_CURRENT_USER, "SOFTWARE\\SAMP");
	        	WinRegistry.writeStringValue(WinRegistry.HKEY_CURRENT_USER, "SOFTWARE\\SAMP", "gta_sa_exe", this.Gaia.GTAPath);
	        	
	        	diretorio 	= local.replace("gta_sa.exe", "");
	        	local 		= diretorio + "samp.exe";
	        	
	        	File arquivo = new File(local);
	        	if(arquivo.exists()) {
	        		this.Gaia.SAMPPath = local;
	        		new janelaJogo(this.Gaia);
	        	} else {
	        		JOptionPane.showMessageDialog(this.Gaia.frame, "Não foi encontrado uma instalação do SA-MP em seu computador.\n\nEle deve ser instalado no mesmo diretório do jogo.", "samp.exe não encontrado", JOptionPane.ERROR_MESSAGE);
	        	}
	        }
	    } else if(returnVal == JFileChooser.CANCEL_OPTION) {
	    	this.Gaia.deslogar();
	    }
	}
}
