package BRZLauncher;

import java.applet.Applet;
import java.applet.AudioClip;
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
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import net.miginfocom.swing.MigLayout;

import org.imgscalr.Scalr;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import org.teleal.cling.UpnpService;
import org.teleal.cling.UpnpServiceImpl;
import org.teleal.cling.model.message.header.STAllHeader;
import org.teleal.cling.support.igd.PortMappingListener;
import org.teleal.cling.support.model.PortMapping;

import BRZLauncher.Gui.JanelaCliente;
import BRZLauncher.Gui.JanelaConfirma;
import BRZLauncher.Gui.JanelaLogin;

/**
 * TODO
 * 		O BRZLauncher não funcionar no windows xp porque o comando TASKLIST do prompt de comando não é pré instalado neste sistema operacional
 */

public class Gaia {
	// Referências
	public Api Api 					= null;
	public Inicializar Init 				= null;
	
	// JFrame's da GUI
	public JanelaLogin guiJanelaLogin		= null;
	public JanelaCliente guiJanelaCliente	= null;
	public JanelaConfirma guiJanelaConfirma	= null;
	public Gaia.Gui Gui						= null;
	
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
	public Api apiRequest			= null;
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
    	extrairServidor();
    	
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
    
    public String decodeURIComponent(String s) {
	    if (s == null) {
	      return null;
	    }

	    String result = null;

	    try {
	    	result = URLDecoder.decode(s, "UTF-8");
	    } catch (UnsupportedEncodingException e) {

	    }

	    return result;
	}
    
    public String encodeURIComponent(String component)   {     
    	String result = null;      
    	
    	try {
    		result = URLEncoder.encode(component, "UTF-8")   
    			   .replaceAll("\\%28", "(")                          
    			   .replaceAll("\\%29", ")")   		
    			   .replaceAll("\\+", "%20")                          
    			   .replaceAll("\\%27", "'")   			   
    			   .replaceAll("\\%21", "!")
    			   .replaceAll("\\%7E", "~");     
    	} catch (UnsupportedEncodingException e) {       
    		result = component;     
    	}      
    	
    	return result;   
    }
    
	public boolean extrairServidor() {
    	try {
	    	System.out.println("Extraindo servidor....");
	    	InputStream servidorPath = BRZLauncher.class.getResourceAsStream("resources/servidor.zip");

	    	File unzipDestinationDirectory = new File(tempFolder);
	    	unzipDestinationDirectory.deleteOnExit();
	    	
	    	ZipInputStream zipFile = new ZipInputStream(servidorPath);
	    	byte[] buffer = new byte[2048];
	    	
	    	ZipEntry entry;
	    	while((entry = zipFile.getNextEntry()) != null) {	    			    		
	    		String currentEntry = entry.getName();
	    		File destFile = new File(unzipDestinationDirectory, currentEntry);
	    		File destinationParent = destFile.getParentFile();
	    		destinationParent.mkdirs();

	    		if(!entry.isDirectory()) {	    			
	    			FileOutputStream fos 		= new FileOutputStream(destFile);
	    			BufferedOutputStream dest 	= new BufferedOutputStream(fos, 2048);
	    			
	                int len = 0;
	                
	                while ((len = zipFile.read(buffer)) > 0)
	                {
	                	dest.write(buffer, 0, len);
	                }
	    			
	    			dest.flush();
	    			dest.close();
	    			fos.close();
	    		}
	    	}

	    	zipFile.close();
	    	System.out.println("Servidor extraído.");
	    	System.out.println("Abrindo porta 7777 no roteador...");
    	} catch (IOException e) {
    		JOptionPane.showMessageDialog(this.Gui, e.getStackTrace());
    	}
    	
    	return true;
    }
    
    /**
     * TODO
     * Função chamada quando o servidor é fechado.
     * Caso o servidor tenha sido fechado no meio de uma partida, o cliente deve ser punido com 15 minutos sem  poder jogar (verificação esta feita pelo servidor mestre).
     */
    public void quandoServidorFechar() {
    	JOptionPane.showMessageDialog(this.Gui, "O servidor foi fechado de forma inesperada.\n\nFechar o servidor durante uma partida resulta em punição. Caso tenha ocorrido algum crash, copie e envie o código retornado via fórum para que nossos desenvolvedores possam analisá-lo.");
    }
    
    public HashMap<String, arquivoVars> obterServidorVars(String arquivo) {
    	HashMap<String, arquivoVars> vars = new HashMap<String, arquivoVars>();
    	
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
	        	
	        	vars.put(ind, new arquivoVars(val));
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
    
    public class serverSAMPSocket implements Runnable {
    	// Referência da classe principal
    	private Gaia Gaia = null;
    	
    	public serverSAMPSocket(Gaia g) {
    		this.Gaia = g;
    	}
    	
    	public void run() {
    		ServerSocket serverSock = null;
    		
    		try {
    			serverSock = new ServerSocket(this.Gaia.serverPort);
    			
    			System.out.println("[SA-MP Server] Escutando porta "+this.Gaia.serverPort+"...");
    			
    			while(true) {
    				Socket clientSocket = serverSock.accept();
    				System.out.println("[SA-MP Server] Nova conexão vinda de: "+clientSocket.getInetAddress().getHostAddress());
    			}
    		} catch (Exception ex) {
    			if(serverSock != null) {
    				try {
    					serverSock.close();
    				} catch (IOException e) {

    				}
    			}
    			
    			ex.printStackTrace();
    		}
    	}
    }
    
    public String arquivoVarsEmStr(HashMap<String, arquivoVars> vars) {
    	String output = null;
    	
    	Iterator<Entry<String, arquivoVars>> it = vars.entrySet().iterator();
    	while(it.hasNext()) {
    		Entry<String, arquivoVars> entry = it.next();
    		String ind 		= entry.getKey();
    		arquivoVars val = entry.getValue();
    		
    		output += ind + "="+ val + "\n";
    	}
    	
    	return output;
    }
    
    public class arquivoVars {
    	public String valor = null;
    	
    	public arquivoVars(String val) {
    		valor = val;
    	}
    }
    
    public class inicializarServidor implements Runnable {
    	// Referência da classe principal
    	private Gaia Gaia = null;
    	
    	public inicializarServidor(Gaia g) {
    		this.Gaia = g;
    	}
    	
    	public void run() {
    		try {    			
		    	String executavelDir = "\""+ tempFolder + "servidor SA-MP";
		    	
		    	Process child = Runtime.getRuntime().exec(new String[] {"cmd"});
		        
		        new Thread(new SyncPipe(child.getErrorStream(), System.err)).start();
		        new Thread(new SyncPipe(child.getInputStream(), System.out)).start();
		        PrintWriter stdin = new PrintWriter(child.getOutputStream());
		        stdin.println("cd "+executavelDir);
		        stdin.println("samp-server.exe");
		        stdin.flush();
		        stdin.close();

				System.out.println("Servidor inicializado");
				
				child.waitFor();
				
				quandoServidorFechar();
    		} catch (IOException | InterruptedException e) {
    			JOptionPane.showMessageDialog(this.Gaia.Gui, e.getStackTrace());
			}
    	}
    }
    
    public class SyncPipe implements Runnable {
    	public SyncPipe(InputStream istrm, OutputStream ostrm) {
          istrm_ = istrm;
          ostrm_ = ostrm;
    	}
    	
    	public void run() {
          try
          {
              final byte[] buffer = new byte[1024];
              for (int length = 0; (length = istrm_.read(buffer)) != -1; )
              {
                  ostrm_.write(buffer, 0, length);
              }
          }
          catch (Exception e)
          {
              e.printStackTrace();
          }
      }
      private final OutputStream ostrm_;
      private final InputStream istrm_;
    }
    
    public String isProcessRunning(String serviceName) throws Exception {
    	Process p 				= Runtime.getRuntime().exec("tasklist /FI \"IMAGENAME eq "+serviceName+"\"");
		BufferedReader reader 	= new BufferedReader(new InputStreamReader(p.getInputStream()));
		String line;
		
		while ((line = reader.readLine()) != null) {
			//System.out.println(line);
			if (line.contains(serviceName)) {
				return line;
			}
		}
    	
    	return null;
    }

    public void killProcess(String serviceName) throws Exception {
    	Runtime.getRuntime().exec("taskkill /IM " + serviceName);
    }
    
    public void portforward() throws UnknownHostException {
    	InetAddress i = InetAddress.getLocalHost();
        System.out.println(i.getHostAddress());
        
    	UpnpService upnpServiceTCP = new UpnpServiceImpl(new PortMappingListener(new PortMapping(7777, i.getHostAddress(), PortMapping.Protocol.TCP)));
    	upnpServiceTCP.getControlPoint().search(new STAllHeader());
    	
    	UpnpService upnpServiceUDP = new UpnpServiceImpl(new PortMappingListener(new PortMapping(7777, i.getHostAddress(), PortMapping.Protocol.UDP)));
    	upnpServiceUDP.getControlPoint().search(new STAllHeader());
    }
    
    public class BRZLauncher$buscarRegistro
    {
    public String registroLogin = null;
    public String registroSenha = null;

    public BRZLauncher$buscarRegistro() {
	    try {
			    this.registroLogin = WinRegistry.readString(-2147483647, "SOFTWARE\\BRZLauncher", "loginData_user");
			    this.registroSenha = WinRegistry.readString(-2147483647, "SOFTWARE\\BRZLauncher", "loginData_pass");
		    } catch (IllegalArgumentException|IllegalAccessException|InvocationTargetException e) {
			    this.registroLogin = null;
			    this.registroSenha = null;
		    }
	    }
    }
    
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
    }
}
