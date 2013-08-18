package BRZLauncher;

import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Timer;
import org.imgscalr.Scalr;
import javax.imageio.ImageIO;
import javax.swing.*;
import Variaveis.ArquivoVars;

import BRZLauncher.Utils.AESencrp;
import BRZLauncher.Gui.JanelaCliente;
import BRZLauncher.Gui.JanelaConfirma;
import BRZLauncher.Gui.JanelaLogin;
import BRZLauncher.Gui.Gui;

/**
 * TODO
 * 		O BRZLauncher não funcionar no windows xp porque o comando TASKLIST do prompt de comando não é pré instalado neste sistema operacional
 */

public class Gaia {
	// Referências
	public Api Api 							= null;
	public InicializarCliente Init 				= null;
	
	// GUI - Design do programa
	public JanelaLogin guiJanelaLogin		= null;
	public JanelaCliente guiJanelaCliente	= null;
	public JanelaConfirma guiJanelaConfirma	= null;
	public Gui Gui							= null;
	public Utils Utils						= null;
	public AESencrp C 						= null;
	
	// Variáveis
	public String brzUrlAPI					= "http://samp.brazucas-server.com/api.php?";
	public String brzLauncherUrl			= "http://samp.brazucas-server.com/BRZLauncher.php";
	public BufferedImage BRZLogo			= null;
	public BufferedImage BRZLogoLarge		= null;
	public WinRegistry winRegistry 			= null;
	private String userLogin				= null;
	private String userPass					= null;
	private static boolean userLogado		= false;//
	public String versao					= "Alpha1";
	public static String versaoNome			= "Pre Alpha RC 1";
	public String jogoLocal					= null;
	public String GTAPath					= null;
	public String SAMPPath					= null;
	public String chaveAuth					= null;
	public boolean estaEmFila				= false;
	public Timer loopAtul					= new Timer();
	public Thread threadJogo				= null;
	public long chatUltimaMsg				= 0;
	public long chatUltimaAtul				= 0;
	public BufferedImage frameBg			= null;
	public BufferedImage BRZLogo2			= null;
	public BufferedImage imgUser			= null;
	public BufferedImage imgServer			= null;
	public BufferedImage imgClock			= null;
	public BufferedImage imgWeapon0			= null;
	public BufferedImage imgWeapon200		= null;
	public BufferedImage imgWeapon201		= null;
	public BufferedImage imgWeapon49		= null;
	public BufferedImage imgWeapon50		= null;
	public BufferedImage imgWeapon51		= null;
	public BufferedImage imgWeapon53		= null;
	public BufferedImage imagemMinimizar	= null;
	public BufferedImage imagemFechar		= null;
	public int chatMsgs						= 0;
	public BufferedImage bgimage			= null;
	public JPanel formularioLogin 			= null;
	public AudioClip novaMsgSom				= null;
	public Api apiRequest					= null;
	public boolean inicializado				= false;
	public boolean estaPronto				= false;
	public String gtaProcNome				= "gta_sa.exe";
	public int gtaPid						= -1;
	public boolean servidorLiberado 		= false;
	public String tempFolder				= null;
	public int serverPort					= 7777;
	
    public void init() throws IOException, URISyntaxException {
    	tempFolder 	= System.getProperty("java.io.tmpdir");
    	
    	Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {

            }
        });
    	
    	/**
    	 * TODO
    	 * Extrai o arquivo zipado do servidor para uma pasta temporária.
    	 */
    	this.Utils.extrairServidor();
    	
    	BufferedImage imagem;
    	
    	winRegistry = new WinRegistry();
    	
    	try {
			imagem = ImageIO.read(BRZLauncher.class.getResource("resources/brzlogo.png"));
		} catch (IOException e1) {
			imagem = null;
		}
    	BRZLogo = imagem;
    	
    	try {
			imagem = ImageIO.read(BRZLauncher.class.getResource("resources/brzlogolarge.png"));
		} catch (IOException e1) {
			imagem = null;
		}
    	BRZLogoLarge = imagem;
    	
    	try {
			imagem = ImageIO.read(BRZLauncher.class.getResource("resources/brz_logo.png"));
		} catch (IOException e1) {
			imagem = null;
		}
    	BRZLogo2 = imagem;
    	
    	try {
			imagem = ImageIO.read(BRZLauncher.class.getResource("resources/user.png"));
			imagem = Scalr.resize(imagem, 15, 15);
		} catch (IOException e1) {
			imagem = null;
		}
    	imgUser =imagem;
    	
    	try {
			imagem = ImageIO.read(BRZLauncher.class.getResource("resources/server.png"));
			imagem = Scalr.resize(imagem, 15, 15);
		} catch (IOException e1) {
			imagem = null;
		}
    	imgServer = imagem;
    	
    	try {
			imagem = ImageIO.read(BRZLauncher.class.getResource("resources/clock.gif"));
			imagem = Scalr.resize(imagem, 15, 15);
		} catch (IOException e1) {
			imagem = null;
		}
    	imgClock = imagem;
    	
    	try {
			imagem = ImageIO.read(BRZLauncher.class.getResource("resources/Weapon-0.gif"));
			imagem = Scalr.resize(imagem, 15, 15);
		} catch (IOException e1) {
			imagem = null;
		}
    	imgWeapon0 = imagem;
    	
    	try {
			imgWeapon200 = ImageIO.read(BRZLauncher.class.getResource("resources/Weapon-200.gif"));
			imgWeapon200 = Scalr.resize(imgWeapon200, 15, 15);
		} catch (IOException e1) {
			imgWeapon200 = null;
		}
    	
    	try {
    		imgWeapon201 = ImageIO.read(BRZLauncher.class.getResource("resources/Weapon-201.gif"));
    		imgWeapon201 = Scalr.resize(imgWeapon201, 15, 15);
		} catch (IOException e1) {
			imgWeapon201 = null;
		}
    	
    	try {
			imagem = ImageIO.read(BRZLauncher.class.getResource("resources/Weapon-49.gif"));
			imagem = Scalr.resize(imagem, 15, 15);
		} catch (IOException e1) {
			imagem = null;
		}
    	imgWeapon49 = imagem;
    	
    	try {
			imagem = ImageIO.read(BRZLauncher.class.getResource("resources/Weapon-50.gif"));
			imagem = Scalr.resize(imagem, 15, 15);
		} catch (IOException e1) {
			imagem = null;
		}
    	imgWeapon50 = imagem;
    	
    	try {
			imagem = ImageIO.read(BRZLauncher.class.getResource("resources/Weapon-51.gif"));
			imagem = Scalr.resize(imagem, 15, 15);
		} catch (IOException e1) {
			imagem = null;
		}
    	imgWeapon51 = imagem;
    	
    	try {
			imagem = ImageIO.read(BRZLauncher.class.getResource("resources/Weapon-53.gif"));
			imagem = Scalr.resize(imagem, 15, 15);
		} catch (IOException e1) {
			imagem = null;
		}
    	imgWeapon53 = imagem;
    	
    	try {
    		bgimage = ImageIO.read(BRZLauncher.class.getResource("resources/background.png"));
		} catch (IOException e1) {
			bgimage = null;
		}
    	
    	try {
			imagem = ImageIO.read(BRZLauncher.class.getResource("resources/close.png"));
		} catch (IOException e1) {
			imagem = null;
		}
    	imagemFechar = imagem;
    	
    	try {
			imagem = ImageIO.read(BRZLauncher.class.getResource("resources/minimize.png"));
		} catch (IOException e1) {
			imagem = null;
		}
    	imagemMinimizar = imagem;

    	novaMsgSom = Applet.newAudioClip(BRZLauncher.class.getResource("resources/novaMsg.ogg"));
    	
    	this.Init.abrir();
    }

    public void registrarLogin(String user, String pass) {
    	userLogin 	= user;
    	userPass 	= pass;
    	userLogado 	= true;
    }
    
    public void deslogar() {
    	userLogin 	= null;
    	userPass 	= null;
    	userLogado 	= false;
    	
    	if(this.Api != null) {
    		this.Api.cmd("a=deslogar");
    	}

    	if(this.Api != null && this.Api.sock != null) {
	    	try {
				apiRequest.sock.close();
			} catch (IOException e) {
				JOptionPane.showMessageDialog(this.Gui, e.getStackTrace());
			}
    	}
    	
    	this.fecharJanela();
    	System.exit(0);
    }
    
    public void removerRegistros() {
    	try {
    		WinRegistry.deleteValue(WinRegistry.HKEY_CURRENT_USER, "SOFTWARE\\BRZLauncher", "loginData_user");
    		WinRegistry.deleteValue(WinRegistry.HKEY_CURRENT_USER, "SOFTWARE\\BRZLauncher", "loginData_pass");
		} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
			
		}
    }
    
    public String obterSAMPNickRegistro() {
    	String nick	= null;

		try {
    		nick = WinRegistry.readString(WinRegistry.HKEY_CURRENT_USER, "SOFTWARE\\SAMP", "PlayerName");
		} catch(IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
			nick = null;
			JOptionPane.showMessageDialog(this.Gui, e.getStackTrace());
		}
		
		return nick;
    }
    
    public String setarSAMPNickRegistro(String novoNick) {
    	String nick	= null;

		try {
    		WinRegistry.writeStringValue(WinRegistry.HKEY_CURRENT_USER, "SOFTWARE\\SAMP", "PlayerName", novoNick);
		} catch(IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
			nick = null;
			JOptionPane.showMessageDialog(this.Gui, e.getStackTrace());
		}
		
		return nick;
    }
    
    public boolean estaLogado() {
    	return userLogado;
    }
    
    public class buscarRegistro {
    	public String registroLogin = null;
    	public String registroSenha = null;
    	
    	public buscarRegistro() {
    		try {
	    		registroLogin = WinRegistry.readString(WinRegistry.HKEY_CURRENT_USER, "SOFTWARE\\BRZLauncher", "loginData_user");
	            registroSenha = WinRegistry.readString(WinRegistry.HKEY_CURRENT_USER, "SOFTWARE\\BRZLauncher", "loginData_pass");
    		} catch(IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
    			registroLogin = null;
    			registroSenha = null;
    		}
    	}
    }

    public void fecharJanela() {
		this.Gui.setVisible(false);
		this.Gui.dispose();
	}
    
    /**
     * TODO
     * Função chamada quando o servidor é fechado.
     * Caso o servidor tenha sido fechado no meio de uma partida, o cliente deve ser punido com 15 minutos sem  poder jogar (verificação esta feita pelo servidor mestre).
     */
    public void quandoServidorFechar() {
    	JOptionPane.showMessageDialog(this.Gui, "O servidor foi fechado de forma inesperada.\n\nFechar o servidor durante uma partida resulta em punição. Caso tenha ocorrido algum crash, copie e envie o código retornado via fórum para que nossos desenvolvedores possam analisá-lo.");
    }
    
    public HashMap<String, ArquivoVars> obterServidorVars(String arquivo) {
    	HashMap<String, ArquivoVars> vars = new HashMap<String, ArquivoVars>();
    	
	    File file = new File("\""+tempFolder + "servidor SA-MP/"+arquivo+"\"");
	    int len = (int) file.length();
	    byte[] bytes = new byte[len];
	    FileInputStream fis = null;
	    
	    try {
	        fis = new FileInputStream(file);
	        assert len == fis.read(bytes);
	        
	        String texto = new String(bytes, "UTF-8");
	        String[] linhas = texto.split("\n");
	        
	        for(String linha : linhas) {
	        	String[] var 	= linha.split("=");
	        	String ind 		= var[0];
	        	String val 		= var[1];
	        	
	        	vars.put(ind, new ArquivoVars(val));
	        }
	    } catch (IOException e) {
    	    try {
    	        fis.close();
    	    } catch(IOException ignored) {
    	    	JOptionPane.showMessageDialog(this.Gui, e.getStackTrace());
    	    }
	    }
    	
    	return vars;
    }
}
