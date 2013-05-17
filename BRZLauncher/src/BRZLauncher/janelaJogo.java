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

public class janelaJogo {
	public static JPanel layout 						= null;
	public static JPanel cima							= null;
	public static JLabel temp							= null;
	public static JLabel jogadoresOnline 				= null;
	public static JLabel servidoresDisponiveis 			= null;
	public static JLabel servidoresTotais 				= null;
	public static JLabel tempoAproximado	 			= null;
	public static JLabel plPartidasDisputadas			= null;
	public static JLabel plVitorias						= null;
	public static JLabel plVezesPunido					= null;
	public static JLabel plMatou						= null;
	public static JLabel plMorreu						= null;
	public static JLabel plRanking						= null;
	public static JButton jogar							= null;
	public static JLabel filaStatus						= new JLabel("<html><span style='color: white; font-size: 8px'>enviando requisição...</span></html>");
	public static JPanel logados						= null;
	public static JPanel mensagens						= null;
	public static Point mouseDownScreenCoords 			= null;
	public static Point mouseDownCompCoords 			= null;
	public static HashMap<String, JLabel> logadosLabels = new HashMap<String, JLabel>();
	
	public static boolean cimaClicado			= false;

	public janelaJogo() throws IOException {
		if(BRZLauncher.estaLogado()) {
			verificarJogoLocal();
			
			layout.remove(BRZLauncher.formularioLogin);

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
	    	    	BRZLauncher.removerRegistros();
	    	    	BRZLauncher.deslogar();
	    	    }
	    	});

	    	
	    	menuBarLabel1.add(menuBarLabelItem1);
	    	menuBarLabel2.add(menuBarLabelItem2);
	    	/* **************************** */

	    	temp 	= new JLabel("<html><span style='color: white'>Obtendo informações da API...</span></html>");
	    	temp.setBorder(new EmptyBorder(15, 22, 15, 15));
	    	layout.add(temp, "left");
	    	
	    	BRZLauncher.apiRequest.cmd("a=inicializar");
	    	/* **************************** */
	    	
	    	BRZLauncher.frame.add(layout);
	    	BRZLauncher.frame.pack();
	    	BRZLauncher.frame.setVisible(true);
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
			JOptionPane.showMessageDialog(BRZLauncher.frame, "O GTA não foi encontrado em seu computador!\n\nPor favor selecione manualmente o local do executável do jogo.", "Jogo não encontrado", JOptionPane.ERROR_MESSAGE);

			BRZLauncher.fecharJanela();
			
			try {
				selecionarJogoLocal();
			} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
				JOptionPane.showMessageDialog(BRZLauncher.frame, "Erro encontrado!\n\n"+e.getStackTrace()+"\n\nTente fechar e abrir o programa novamente.", "Caminho incorreto.", JOptionPane.ERROR_MESSAGE);
			}
		} else {
			BRZLauncher.GTAPath = local;
			
			diretorio 	= local.replace("gta_sa.exe", "");
        	local 		= diretorio + "samp.exe";
        	
        	File arquivo = new File(local);
        	if(arquivo.exists()) {
        		BRZLauncher.SAMPPath = local;
        	} else {
        		JOptionPane.showMessageDialog(BRZLauncher.frame, "Não foi encontrado uma instalação do SA-MP em seu computador.\n\nEle deve ser instalado no mesmo diretório do jogo.", "samp.exe não encontrado", JOptionPane.ERROR_MESSAGE);
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
	        	JOptionPane.showMessageDialog(BRZLauncher.frame, "Este não é um caminho válido para o executável do jogo.", "Caminho incorreto.", JOptionPane.ERROR_MESSAGE);
	        	selecionarJogoLocal();
	        } else {
	        	BRZLauncher.GTAPath = local;
	        	
	        	WinRegistry.createKey(WinRegistry.HKEY_CURRENT_USER, "SOFTWARE\\SAMP");
	        	WinRegistry.writeStringValue(WinRegistry.HKEY_CURRENT_USER, "SOFTWARE\\SAMP", "gta_sa_exe", BRZLauncher.GTAPath);
	        	
	        	diretorio 	= local.replace("gta_sa.exe", "");
	        	local 		= diretorio + "samp.exe";
	        	
	        	File arquivo = new File(local);
	        	if(arquivo.exists()) {
	        		BRZLauncher.SAMPPath = local;
	        		new janelaJogo();
	        	} else {
	        		JOptionPane.showMessageDialog(BRZLauncher.frame, "Não foi encontrado uma instalação do SA-MP em seu computador.\n\nEle deve ser instalado no mesmo diretório do jogo.", "samp.exe não encontrado", JOptionPane.ERROR_MESSAGE);
	        	}
	        }
	    } else if(returnVal == JFileChooser.CANCEL_OPTION) {
	    	BRZLauncher.deslogar();
	    }
	}
}
