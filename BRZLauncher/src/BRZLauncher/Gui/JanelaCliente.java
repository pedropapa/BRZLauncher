package BRZLauncher.Gui;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import BRZLauncher.Gaia;
import BRZLauncher.WinRegistry;

public class JanelaCliente extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	// Referência da classe principal
	private Gaia Gaia = null;
	
	
	// Variáveis
	//public JPanel layout 						= new JPanel();
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
	public JLabel filaStatus					= new JLabel("<html><span style='color: white; font-size: 8px'>Enviando requisição...</span></html>");
	public JPanel logados						= null;
	public JPanel mensagens						= null;
	public HashMap<String, JLabel> logadosLabels = new HashMap<String, JLabel>();

	public JanelaCliente() {
		
	}
	
	public void criar(Gaia g) throws IOException {
		this.Gaia = g;

		if(this.Gaia.estaLogado()) {
			this.verificarJogoLocal();
			this.Gaia.Gui.layout.remove(this.Gaia.formularioLogin);
			
	    	temp 	= new JLabel("<html><span style='color: white'>Obtendo informações da API...</span></html>");
	    	temp.setBorder(new EmptyBorder(15, 22, 15, 15));
	    	
	    	this.Gaia.apiRequest.cmd("a=inicializar");

	    	this.Gaia.Gui.layout.add(temp, "left");
	    	this.Gaia.Gui.Core.pack();
	    	this.Gaia.Gui.Core.setVisible(true);

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
			JOptionPane.showMessageDialog(this.Gaia.Gui, "O GTA não foi encontrado em seu computador!\n\nPor favor selecione manualmente o local do executável do jogo.", "Jogo não encontrado", JOptionPane.ERROR_MESSAGE);

			this.Gaia.fecharJanela();
			
			try {
				selecionarJogoLocal();
			} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
				JOptionPane.showMessageDialog(this.Gaia.Gui, "Erro encontrado!\n\n"+e.getStackTrace()+"\n\nTente fechar e abrir o programa novamente.", "Caminho incorreto.", JOptionPane.ERROR_MESSAGE);
			}
		} else {
			this.Gaia.GTAPath = local;
			
			diretorio 	= local.replace("gta_sa.exe", "");
        	local 		= diretorio + "samp.exe";
        	
        	File arquivo = new File(local);
        	if(arquivo.exists()) {
        		this.Gaia.SAMPPath = local;
        	} else {
        		JOptionPane.showMessageDialog(this.Gaia.Gui, "Não foi encontrado uma instalação do SA-MP em seu computador.\n\nEle deve ser instalado no mesmo diretório do jogo.", "samp.exe não encontrado", JOptionPane.ERROR_MESSAGE);
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
	        	JOptionPane.showMessageDialog(this.Gaia.Gui, "Este não é um caminho válido para o executável do jogo.", "Caminho incorreto.", JOptionPane.ERROR_MESSAGE);
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
	        		this.Gaia.guiJanelaCliente.criar(this.Gaia);
	        	} else {
	        		JOptionPane.showMessageDialog(this.Gaia.Gui, "Não foi encontrado uma instalação do SA-MP em seu computador.\n\nEle deve ser instalado no mesmo diretório do jogo.", "samp.exe não encontrado", JOptionPane.ERROR_MESSAGE);
	        	}
	        }
	    } else if(returnVal == JFileChooser.CANCEL_OPTION) {
	    	this.Gaia.deslogar();
	    }
	}
}
