package BRZLauncher;

import java.applet.Applet;
import java.applet.AudioClip;
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
import org.imgscalr.Scalr;
import javax.imageio.ImageIO;
import javax.swing.*;

import org.teleal.cling.UpnpService;
import org.teleal.cling.UpnpServiceImpl;
import org.teleal.cling.model.message.header.STAllHeader;
import org.teleal.cling.support.igd.PortMappingListener;
import org.teleal.cling.support.model.PortMapping;

/**
 * TODO
 * 		O BRZLauncher não irá funcionar no windows xp porque o comando TASKLIST do prompt de comando não é pré instalado neste sistema operacional
 */

public class BRZLauncher {
	public static JFrame frame 					= null;
	public static String brzUrlAPI				= "http://samp.brazucas-server.com/api.php?";
	public static String brzLauncherUrl			= "http://samp.brazucas-server.com/BRZLauncher.php";
	public static BufferedImage BRZLogo			= null;
	public static BufferedImage BRZLogoLarge	= null;
	public static WinRegistry winRegistry 		= null;
	private static String userLogin				= null;
	private static String userPass				= null;
	private static boolean userLogado			= false;
	public static String versao					= "Alpha1";
	public static String versaoNome				= "Pre Alpha RC 1";
	public static String jogoLocal				= null;
	public static String GTAPath				= null;
	public static String SAMPPath				= null;
	public static String chaveAuth				= null;
	public static boolean estaEmFila			= false;
	public static Timer loopAtul				= new Timer();
	public static Thread threadJogo				= null;
	public static long chatUltimaMsg			= 0;
	public static long chatUltimaAtul			= 0;
	public static BufferedImage frameBg			= null;
	public static BufferedImage BRZLogo2		= null;
	public static BufferedImage imgUser			= null;
	public static BufferedImage imgServer		= null;
	public static BufferedImage imgClock		= null;
	public static BufferedImage imgWeapon0		= null;
	public static BufferedImage imgWeapon200	= null;
	public static BufferedImage imgWeapon201	= null;
	public static BufferedImage imgWeapon49		= null;
	public static BufferedImage imgWeapon50		= null;
	public static BufferedImage imgWeapon51		= null;
	public static BufferedImage imgWeapon53		= null;
	public static BufferedImage imagemMinimizar	= null;
	public static BufferedImage imagemFechar	= null;
	public static int chatMsgs					= 0;
	public static BufferedImage bgimage			= null;
	public static JPanel formularioLogin 		= null;
	public static AudioClip novaMsgSom			= null;
	public static apiRequest apiRequest			= null;
	public static boolean inicializado			= false;
	public static boolean estaPronto			= false;
	public static String gtaProcNome			= "gta_sa.exe";
	public static int gtaPid					= -1;
	public static boolean servidorLiberado 		= false;
	public static String tempFolder				= null;
	public static int serverPort				= 7777;
	
    public static void main (String args[]) throws IOException, URISyntaxException {
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
    	
    	new inicializar();
    }

    public static void registrarLogin(String user, String pass) {
    	userLogin 	= user;
    	userPass 	= pass;
    	userLogado 	= true;
    }
    
    public static void deslogar() {
    	userLogin 	= null;
    	userPass 	= null;
    	userLogado 	= false;
    	
    	if(BRZLauncher.apiRequest != null) {
    		BRZLauncher.apiRequest.cmd("a=deslogar");
    	}

    	if(apiRequest.sock != null) {
	    	try {
				apiRequest.sock.close();
			} catch (IOException e) {
				JOptionPane.showMessageDialog(BRZLauncher.frame, e.getStackTrace());
			}
    	}
    	
    	BRZLauncher.fecharJanela();
    	System.exit(0);
    }
    
    public static void removerRegistros() {
    	try {
    		WinRegistry.deleteValue(WinRegistry.HKEY_CURRENT_USER, "SOFTWARE\\BRZLauncher", "loginData_user");
    		WinRegistry.deleteValue(WinRegistry.HKEY_CURRENT_USER, "SOFTWARE\\BRZLauncher", "loginData_pass");
		} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
			
		}
    }
    
    public static String obterSAMPNickRegistro() {
    	String nick	= null;

		try {
    		nick = WinRegistry.readString(WinRegistry.HKEY_CURRENT_USER, "SOFTWARE\\SAMP", "PlayerName");
		} catch(IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
			nick = null;
			JOptionPane.showMessageDialog(BRZLauncher.frame, e.getStackTrace());
		}
		
		return nick;
    }
    
    public static String setarSAMPNickRegistro(String novoNick) {
    	String nick	= null;

		try {
    		WinRegistry.writeStringValue(WinRegistry.HKEY_CURRENT_USER, "SOFTWARE\\SAMP", "PlayerName", novoNick);
		} catch(IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
			nick = null;
			JOptionPane.showMessageDialog(BRZLauncher.frame, e.getStackTrace());
		}
		
		return nick;
    }
    
    public static boolean estaLogado() {
    	return userLogado;
    }
    
    public static class buscarRegistro {
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

    public static void fecharJanela() {
		BRZLauncher.frame.setVisible(false);
		BRZLauncher.frame.dispose();
	}
    
    public static String decodeURIComponent(String s) {
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
    
    public static String encodeURIComponent(String component)   {     
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
    
    @SuppressWarnings("resource")
	public static boolean extrairServidor() {
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
	    	//portforward();
	    	//JOptionPane.showMessageDialog(BRZLauncher.frame, "Servidor extraído.");
    	} catch (IOException e) {
    		JOptionPane.showMessageDialog(BRZLauncher.frame, e.getStackTrace());
    	}
    	
    	return true;
    }
    
    /**
     * TODO
     * Função chamada quando o servidor é fechado.
     * Caso o servidor tenha sido fechado no meio de uma partida, o cliente deve ser punido com 15 minutos sem  poder jogar (verificação esta feita pelo servidor mestre).
     */
    public static void quandoServidorFechar() {
    	JOptionPane.showMessageDialog(BRZLauncher.frame, "O servidor foi fechado de forma inesperada.\n\nFechar o servidor durante uma partida resulta em punição. Caso tenha ocorrido algum crash, copie e envie o código retornado via fórum para que nossos desenvolvedores possam analisá-lo.");
    }
    
    public static HashMap<String, arquivoVars> obterServidorVars(String arquivo) {
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
    	    	JOptionPane.showMessageDialog(BRZLauncher.frame, e.getStackTrace());
    	    }
	    }
    	
    	return vars;
    }
    
    public static class serverSAMPSocket implements Runnable {
    	public void run() {
    		ServerSocket serverSock = null;
    		
    		try {
    			serverSock = new ServerSocket(BRZLauncher.serverPort);
    			
    			System.out.println("[SA-MP Server] Escutando porta "+BRZLauncher.serverPort+"...");
    			
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
    
    public static String arquivoVarsEmStr(HashMap<String, arquivoVars> vars) {
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
    
    public static class arquivoVars {
    	public String valor = null;
    	
    	public arquivoVars(String val) {
    		valor = val;
    	}
    }
    
    public static class inicializarServidor implements Runnable {
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
    			JOptionPane.showMessageDialog(BRZLauncher.frame, e.getStackTrace());
			}
    	}
    }
    
    public static class SyncPipe implements Runnable {
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
    
    public static String isProcessRunning(String serviceName) throws Exception {
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

    public static void killProcess(String serviceName) throws Exception {
    	Runtime.getRuntime().exec("taskkill /IM " + serviceName);
    }
    
    public static void portforward() throws UnknownHostException {
    	InetAddress i = InetAddress.getLocalHost();
        System.out.println(i.getHostAddress());
        
    	UpnpService upnpServiceTCP = new UpnpServiceImpl(new PortMappingListener(new PortMapping(7777, i.getHostAddress(), PortMapping.Protocol.TCP)));
    	upnpServiceTCP.getControlPoint().search(new STAllHeader());
    	
    	UpnpService upnpServiceUDP = new UpnpServiceImpl(new PortMappingListener(new PortMapping(7777, i.getHostAddress(), PortMapping.Protocol.UDP)));
    	upnpServiceUDP.getControlPoint().search(new STAllHeader());
    }
}