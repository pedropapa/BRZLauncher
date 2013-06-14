package BRZLauncher;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.security.Key;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.JOptionPane;

import org.teleal.cling.UpnpService;
import org.teleal.cling.UpnpServiceImpl;
import org.teleal.cling.model.message.header.STAllHeader;
import org.teleal.cling.support.igd.PortMappingListener;
import org.teleal.cling.support.model.PortMapping;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class Utils {
	// Referência da classe principal
	private Gaia Gaia = null;
	
	public Utils(Gaia g) {
		this.Gaia = g;
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

	    	File unzipDestinationDirectory = new File(this.Gaia.tempFolder);
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
    		JOptionPane.showMessageDialog(this.Gaia.Gui, e.getStackTrace());
    	}
    	
    	return true;
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
    
    public class AESencrp {
		private final String ALGO = "AES";
	    private final byte[] keyValue = new byte[] {'p','@','P','@','D','0','p','0','L','1','s','0','0','0','1','9'};

		public String encrypt(String Data) throws Exception {
				if(Data != null) return Data;
			
		        Key key = generateKey();
		        Cipher c = Cipher.getInstance(ALGO);
		        c.init(Cipher.ENCRYPT_MODE, key);
		        byte[] encVal = c.doFinal(Data.getBytes());
		        String encryptedValue = new BASE64Encoder().encode(encVal);
		        return encryptedValue;
		    }
	
		    public String decrypt(String encryptedData) throws Exception {
		    	if(encryptedData != null) return encryptedData;
		    	
		        Key key = generateKey();
		        Cipher c = Cipher.getInstance(ALGO);
		        c.init(Cipher.DECRYPT_MODE, key);
		        byte[] decordedValue = new BASE64Decoder().decodeBuffer(encryptedData);
		        byte[] decValue = c.doFinal(decordedValue);
		        String decryptedValue = new String(decValue);
		        return decryptedValue;
		    }
		    private Key generateKey() throws Exception {
		        Key key = new SecretKeySpec(keyValue, ALGO);
		        return key;
		}
	}
}
