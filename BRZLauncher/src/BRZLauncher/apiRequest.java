package BRZLauncher;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import org.apache.commons.lang3.StringEscapeUtils;

import net.miginfocom.swing.MigLayout;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;

public class apiRequest extends Gaia {
	// Referência da classe principal
	private Gaia Gaia = null;
	
	// Variáveis
	public Thread request 										= null;
	public Socket sock 											= null;
    public InputStreamReader streamReader 						= null;
    public BufferedReader reader 								= null;
    public PrintWriter writer 									= null;
    public HashMap<Integer, HashMap<String, JLabel>> partidaInfo	= new HashMap<Integer, HashMap<String, JLabel>>();
    public boolean scrollPress									= false;
	
	public apiRequest(Gaia g) throws HeadlessException, IOException {
		this.Gaia = g;
		
		URL oracle 			= new URL(this.Gaia.brzUrlAPI+"a=masterIP");
        URLConnection yc 	= oracle.openConnection();
        BufferedReader in 	= new BufferedReader(new InputStreamReader(yc.getInputStream()));
        
        String inputLine;
        String IP = "";
        
        while ((inputLine = in.readLine()) != null) {
            IP += inputLine;
        }
        
        in.close();
        
        if(!criarConexao(IP)) {
        	IP = InetAddress.getByName("localhost").getHostAddress();
        	
        	if(!criarConexao(IP)) {
        		this.Gaia.Init.janela.setVisible(false);
            	JOptionPane.showMessageDialog(this.Gaia.frame, "Ocorreu um erro ao conectar no servidor principal, por favor tente mais tarde.");
            	System.exit(0);
        	}
        }
        
        request = new Thread(new Request(this.Gaia));
		request.start();
	}
	
	public boolean criarConexao(String IP) {
		try {	        
        	sock = new Socket(IP, 1961);
        	System.out.println("Conexão estabelecida.");
        	
        	return true;
        } catch (IOException e) {
        	return false;
        }
	}
	
	public Exception cmd(String options) {
		try {
			writer.println(options+"&c="+this.Gaia.chaveAuth+"&t=cliente");
			writer.flush();
			
			System.out.println("Comando enviado para o servidor: "+ options+"&c="+this.Gaia.chaveAuth);
			
			return null;
		} catch(Exception e) {
			if(writer == null) {
				cmd(options);
			}
			return e;
		}
	}
	
	private void apiResp(apiResp resp) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, IOException {
		if(resp != null) {
			if(resp.funcao != null) {
				String prefixo 	= "";
				String cor		= null;
				
				switch(resp.funcao) {
					case "login":
						switch(resp.CODIGO) {
						 	case 1: // Login efetuado com sucesso.
					     		String regUser = WinRegistry.readString(WinRegistry.HKEY_CURRENT_USER, "SOFTWARE\\BRZLauncher", "loginData_user");
					            String regPass = WinRegistry.readString(WinRegistry.HKEY_CURRENT_USER, "SOFTWARE\\BRZLauncher", "loginData_pass");
					                
				                try {
				                	if(regUser == null || regPass == null) {
				                		WinRegistry.createKey(WinRegistry.HKEY_CURRENT_USER, "SOFTWARE\\BRZLauncher");
				                	}
				                	
				                	WinRegistry.writeStringValue(WinRegistry.HKEY_CURRENT_USER, "SOFTWARE\\BRZLauncher", "loginData_user", this.Gaia.loginForm.login);
				                	WinRegistry.writeStringValue(WinRegistry.HKEY_CURRENT_USER, "SOFTWARE\\BRZLauncher", "loginData_pass", this.Gaia.loginForm.senha);
				                	
				                	this.Gaia.chaveAuth = resp.chave;
				                	
				                	this.Gaia.registrarLogin(this.Gaia.loginForm.login, this.Gaia.loginForm.senha);
				                	this.Gaia.fecharJanela();
				                	this.Gaia.janelaJogo.abrir();
				                } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e1) {
				                	JOptionPane.showMessageDialog(this.Gaia.frame, e1.getStackTrace());
								}
					     	break;
					     	case 2: // Conta não encontrada
					     		JOptionPane.showMessageDialog(this.Gaia.frame, "Conta não encontrada, verifique se você digitou seu nick corretamente.");
					     	break;
					     	case 3: // Senha incorreta
					     		JOptionPane.showMessageDialog(this.Gaia.frame, "Login e/ou senha incorretos.");
					     	break;
					     	case 4: // Serviço indisponível (Liberado apenas para staff)
					     		JOptionPane.showMessageDialog(this.Gaia.frame, resp.html);
					     	break;
						}
						
						this.Gaia.formularioLogin.remove(this.Gaia.loginForm.tempLabel);
						this.Gaia.loginForm.inputEnviar.setText("Login");
						this.Gaia.loginForm.enviarClicado = false;
					break;
					case "deslogar":
						this.Gaia.deslogar();
					break;
					case "inicializar":
						this.Gaia.janelaJogo.layout.remove(this.Gaia.janelaJogo.temp);
						this.Gaia.janelaJogo.layout.setBorder(new EmptyBorder(0, 0, 0, 0));
						
						JPanel stats = new JPanel(new MigLayout("alignx left,width 595!,height 80!,insets 0 0 0 0,nogrid"));
				    	stats.setBorder(new EmptyBorder(0, 0, 0, 0));
				    	stats.setOpaque(false);
				    	
				    	JPanel infosSvDiv = new JPanel(new MigLayout("alignx left, aligny top,insets 0 22 0 0,height 80!,width 200!,nogrid"));
				    	infosSvDiv.setOpaque(false);
				    	infosSvDiv.setAlignmentY(Component.TOP_ALIGNMENT);
				    	infosSvDiv.setBorder(new EmptyBorder(0, 0, 0, 0));
					    	infosSvDiv.add(new JLabel(new ImageIcon(this.Gaia.imgUser)), "alignx left,aligny center"); // Jogadores online
					    	this.Gaia.janelaJogo.jogadoresOnline 	= new JLabel("<html><span style='color: white; font-size: 8px'>0</span></html>");
					    	this.Gaia.janelaJogo.jogadoresOnline.setLayout(new MigLayout("alignx left,aligny center,insets 0 0 0 0"));
					    	infosSvDiv.add(this.Gaia.janelaJogo.jogadoresOnline);
					    	infosSvDiv.add(new JLabel("<html><span style='color: white; font-size: 8px'>Jogadores online</span></html>"), "wrap");
					    	
					    	infosSvDiv.add(new JLabel(new ImageIcon(this.Gaia.imgServer)), "alignx left,top"); // Servidores online
					    	this.Gaia.janelaJogo.servidoresDisponiveis 	= new JLabel("<html><span style='color: green; font-size: 8px'>0</span></html>");
					    	this.Gaia.janelaJogo.servidoresDisponiveis.setLayout(new MigLayout("alignx left,aligny center,insets 0 0 0 0"));
					    	this.Gaia.janelaJogo.servidoresTotais 		= new JLabel("<html><span style='color: white; font-size: 8px'>0</span></html>");
					    	this.Gaia.janelaJogo.servidoresTotais.setLayout(new MigLayout("alignx left,aligny center,insets 0 0 0 0"));
					    	infosSvDiv.add(this.Gaia.janelaJogo.servidoresDisponiveis);
					    	infosSvDiv.add(new JLabel("<html><span style='color: white; font-size: 8px'>/</span></html>"));
					    	infosSvDiv.add(this.Gaia.janelaJogo.servidoresTotais);
					    	infosSvDiv.add(new JLabel("<html><span style='color: white; font-size: 8px'><b>Servidores disponíveis</b></span></html>"), "wrap");
					    	
					    	infosSvDiv.add(new JLabel(new ImageIcon(this.Gaia.imgClock)), "alignx left,top"); // Tempo de espera para começar partida
					    	this.Gaia.janelaJogo.tempoAproximado = new JLabel("<html><span style='color: white; font-size: 8px'>0</span></html>");
					    	this.Gaia.janelaJogo.tempoAproximado.setLayout(new MigLayout("alignx left,aligny center,insets 0 0 0 0"));
					    	this.Gaia.janelaJogo.filaStatus.setVisible(false);
					    	infosSvDiv.add(this.Gaia.janelaJogo.filaStatus, "wrap");
				    	JPanel infosPlDiv = new JPanel(new MigLayout("alignx right,top,insets 0 0 0 0,height 80!,nogrid"));
				    	infosPlDiv.setOpaque(false);
				    	infosPlDiv.setBorder(new EmptyBorder(0, 0, 0, 0));	    	
			    			infosPlDiv.add(new JLabel(new ImageIcon(this.Gaia.imgWeapon50)), "alignx left,top"); // Partidas disputadas
			    			this.Gaia.janelaJogo.plPartidasDisputadas = new JLabel("<html><span style='color: white; font-size: 8px'>0</span></html>");
			    			this.Gaia.janelaJogo.plPartidasDisputadas.setLayout(new MigLayout("alignx left,aligny top,insets 0 0 0 0"));
					    	infosPlDiv.add(this.Gaia.janelaJogo.plPartidasDisputadas);
					    	infosPlDiv.add(new JLabel("<html><span style='color: white; font-size: 8px'><b>Partidas disputadas</b></span></html>"), "wrap");
					    	
			    			infosPlDiv.add(new JLabel(new ImageIcon(this.Gaia.imgWeapon51)), "alignx left,top"); // Vitórias
			    			this.Gaia.janelaJogo.plVitorias = new JLabel("<html><span style='color: white; font-size: 8px'>0</span></html>");
			    			this.Gaia.janelaJogo.plVitorias.setLayout(new MigLayout("align left,aligny center,insets 0 0 0 0"));
					    	infosPlDiv.add(this.Gaia.janelaJogo.plVitorias);
					    	infosPlDiv.add(new JLabel("<html><span style='color: white; font-size: 8px'><b>Vitórias</b></span></html>"), "wrap");
					    	
					    	infosPlDiv.add(new JLabel(new ImageIcon(this.Gaia.imgWeapon53)), "alignx left,top"); // Matou
					    	this.Gaia.janelaJogo.plMatou = new JLabel("<html><span style='color: white; font-size: 8px'>0</span></html>");
					    	this.Gaia.janelaJogo.plMatou.setLayout(new MigLayout("align left,aligny center,insets 0 0 0 0"));
					    	infosPlDiv.add(this.Gaia.janelaJogo.plMatou);
					    	infosPlDiv.add(new JLabel("<html><span style='color: white; font-size: 8px'><b>Kills</b></span></html>"), "wrap");
					    	
					    	infosPlDiv.add(new JLabel(new ImageIcon(this.Gaia.imgWeapon49)), "alignx left,top"); // Morreu
					    	this.Gaia.janelaJogo.plMorreu = new JLabel("<html><span style='color: white; font-size: 8px'>0</span></html>");
					    	this.Gaia.janelaJogo.plMorreu.setLayout(new MigLayout("align left,aligny center,insets 0 0 0 0"));
					    	infosPlDiv.add(this.Gaia.janelaJogo.plMorreu);
					    	infosPlDiv.add(new JLabel("<html><span style='color: white; font-size: 8px'><b>Morreu</b></span></html>"), "wrap");
					    	
					    	infosPlDiv.add(new JLabel("<html><span style='color: white; font-size: 8px'>a</span></html>"), "alignx right,top"); // Ranking
					    	this.Gaia.janelaJogo.plRanking = new JLabel("<html><span style='color: white; font-size: 8px'>0</span></html>");
					    	this.Gaia.janelaJogo.plRanking.setLayout(new MigLayout("align left,aligny center,insets 0 0 0 0"));
					    	infosPlDiv.add(this.Gaia.janelaJogo.plRanking);
					    	infosPlDiv.add(new JLabel("<html><span style='color: white; font-size: 8px'><b>Ranking</b></span></html>"), "wrap");
					    	
					    	stats.add(infosSvDiv);
					    	stats.add(infosPlDiv, "wrap");
					    	
					    	this.Gaia.janelaJogo.layout.add(stats, "wrap");
					    	
					    	JPanel chat = new JPanel(new MigLayout("alignx center,aligny top,width 600!,height 200!,insets 0 0 0 0,nogrid"));
					    	chat.setOpaque(false);
					    	chat.setAlignmentY(JPanel.TOP_ALIGNMENT);
					    	chat.setAlignmentX(JPanel.LEFT_ALIGNMENT);
					    	chat.setBorder(new EmptyBorder(0, 0, 0, 0));
					    	
					    	JPanel formulario = new JPanel(new MigLayout("alignx left,aligny top,height 200!,nogrid"));
					    	formulario.setOpaque(false);
					    	formulario.setAlignmentY(Component.TOP_ALIGNMENT);
					    	formulario.setBorder(new EmptyBorder(0, 0, 0, 0));
					    	
					    	this.Gaia.janelaJogo.mensagens 		= new JPanel(new MigLayout("alignx left,aligny top,insets 0 0 0 0,nogrid"));
					    	this.Gaia.janelaJogo.mensagens.setBorder(new EmptyBorder(0, 0, 0, 0));
					    	this.Gaia.janelaJogo.mensagens.setBackground(Color.WHITE);
					    	this.Gaia.janelaJogo.mensagens.setAlignmentY(Component.TOP_ALIGNMENT);
					    	
					    		JScrollPane editorScroll 	= new JScrollPane(this.Gaia.janelaJogo.mensagens, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
					    		
					    		editorScroll.getVerticalScrollBar().addMouseListener(new MouseAdapter() {
						    		  public void mousePressed(MouseEvent e) {
						    			  if(e.getButton() == MouseEvent.BUTTON1) {
						    				  scrollPress = true;
						    			  }	  
						    		  }
						    		  
						    		  public void mouseReleased(MouseEvent e) {
						    			  scrollPress = false;
						    		  }
						    	});
					    		editorScroll.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.GRAY));
					    		editorScroll.setAlignmentY(Component.TOP_ALIGNMENT);
					    		formulario.add(editorScroll, "width 400!,height 153!,wrap");
					    		
					    		editorScroll.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {  
					    	        public void adjustmentValueChanged(AdjustmentEvent e) {
					    	        	if(!scrollPress) {
					    	        		e.getAdjustable().setValue(e.getAdjustable().getMaximum());
					    	        	}
					    	        }
					    	    });
					    	
						    	JTextField linha = new JTextField();
						    	linha.setAlignmentY(Component.BOTTOM_ALIGNMENT);
	
						    	final JTextField msg = linha;
						    	linha.addKeyListener(new KeyAdapter() {
						    	    public void keyPressed(java.awt.event.KeyEvent e) {
						    	        if(e.getKeyCode() == KeyEvent.VK_ENTER) {
						    	        	if((new Date().getTime()) - Gaia.chatUltimaMsg > 2000) {
						    	        		if(msg.getText().length() > 0) {
							    	        		if(msg.getText().length() > 128) {
							    	        			JOptionPane.showMessageDialog(Gaia.frame, "Mensagem muito grande, máximo de 128 caracteres.");
							    	        		} else {
							    	        			Gaia.apiRequest.cmd("a=chat&msg="+Gaia.encodeURIComponent(msg.getText()));
									    	        	msg.setText("");
									    	        	Gaia.chatUltimaMsg = new Date().getTime();
							    	        		}
						    	        		}
						    	        	}
						    	        }
						    	    }
						    	});
						    	
						    	linha.setBorder(new EmptyBorder(5, 5, 5, 5));
						    	formulario.add(linha, "width 400!,aligny bottom");
						    	chat.add(formulario);
						    	
						    	JPanel ladoDireito = new JPanel(new MigLayout("alignx left,top,height 188!,width 150!,insets 0 0 0 0,nogrid"));
						    	ladoDireito.setOpaque(false);
						    	
					    		this.Gaia.janelaJogo.logados = new JPanel(new MigLayout("alignx left,top,insets 0 0 0 0,nogrid"));
					    		this.Gaia.janelaJogo.logados.setBackground(Color.WHITE);
					    		this.Gaia.janelaJogo.logados.setAlignmentX(JPanel.LEFT_ALIGNMENT);
					    		this.Gaia.janelaJogo.logados.setBorder(new EmptyBorder(0, 19, 0, 1));
						    	JScrollPane logadosScroll 	= new JScrollPane(this.Gaia.janelaJogo.logados, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
						    	logadosScroll.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.GRAY));
						    	ladoDireito.add(logadosScroll,"width 150!,height 120!,wrap");
						    	
						    	JRadioButton vs1 = new JRadioButton("<html><span style='color: white; font-size: 8px'>1x1</span></html>");
						    	vs1.setOpaque(false);
						        vs1.setBackground(Color.WHITE);
						        vs1.setActionCommand("x1");
						        
						    	JRadioButton vs3 = new JRadioButton("<html><span style='color: white; font-size: 8px'>3x3</span></html>");
						    	vs3.setOpaque(false);
						        vs3.setBackground(Color.WHITE);
						        vs3.setSelected(true);
						        vs3.setActionCommand("x3");
						        
						        JRadioButton vs5 = new JRadioButton("<html><span style='color: white; font-size: 8px'>5x5</span></html>");
						        vs5.setOpaque(false);
						        vs5.setBackground(Color.WHITE);
						        vs5.setActionCommand("x5");
	
						        final ButtonGroup modosJogo = new ButtonGroup();
						        modosJogo.add(vs1);
						        modosJogo.add(vs3);
						        modosJogo.add(vs5);
						        
						        ladoDireito.add(vs1, "center");
						        ladoDireito.add(vs3, "center");
						        ladoDireito.add(vs5, "center, wrap");
						    	
						        this.Gaia.janelaJogo.jogar = new JButton("<html><span style='text-align: center; font-size: 12px;'>Jogar!</span></html>");
						        this.Gaia.janelaJogo.jogar.setHorizontalAlignment(JButton.CENTER);
						        this.Gaia.janelaJogo.jogar.setPreferredSize(new Dimension(150, 45));
						    	ladoDireito.add(this.Gaia.janelaJogo.jogar, "bottom, wrap");
						    	
						    	this.Gaia.janelaJogo.jogar.addMouseListener(new MouseAdapter() {
						    		  public void mouseClicked(MouseEvent e) {
						    			  if (e.getButton() == MouseEvent.BUTTON1) {
						    				  if(!Gaia.estaEmFila) {
							    				  Gaia.estaEmFila = true;
							    				  Gaia.janelaJogo.jogar.setText("<html><span style='text-align: center; font-size: 12px;'>Cancelar</span></html>");
							    				  Gaia.janelaJogo.filaStatus.setVisible(true);
	
							    				  Gaia.apiRequest.cmd("a=entrarFila&modo="+modosJogo.getSelection().getActionCommand());
						    				  } else {
						    					  Gaia.estaEmFila = false;
						    					  Gaia.janelaJogo.jogar.setText("<html><span style='text-align: center; font-size: 12px;'>Jogar!</span></html>");
						    					  Gaia.janelaJogo.filaStatus.setVisible(false);
						    					  Gaia.janelaJogo.filaStatus.setText("<html><span style='color: white; font-size: 8px'>enviando requisição...</span></html>");
						    					  Gaia.apiRequest.cmd("a=sairFila");
						    				  }
						    			  }
						    		  }
						    	});
						    	
						    chat.add(ladoDireito);
						this.Gaia.janelaJogo.layout.add(chat, "wrap");
				    	this.Gaia.inicializado = true;
				    	this.Gaia.apiRequest.cmd("a=sincronizar");
				    	
				    	/*
				    	BRZLauncher.loopAtul.scheduleAtFixedRate(new TimerTask() {
				            public void run() {
				            	BRZLauncher.apiRequest.cmd("a=atualizar&ua="+BRZLauncher.chatUltimaAtul);
				            }
				        }, 0, 5000);
				        */
					break;
					case "chatMsg":
						if(this.Gaia.inicializado) {
							++this.Gaia.chatMsgs;
							
							if(this.Gaia.chatMsgs%2 == 0) {
								cor = "#DDDDDD";
							} else {
								cor = "#FFFFFF";
							}
							
							this.Gaia.chatUltimaAtul = resp.UNIX_TIMESTAMP;
							resp.MENSAGEM = StringEscapeUtils.escapeHtml4(this.Gaia.decodeURIComponent(resp.MENSAGEM));
							
							JLabel inf = null;
							switch(resp.TIPO) {
								case 2:
									inf = new JLabel(new ImageIcon(this.Gaia.imgWeapon200));
									inf.setBorder(new EmptyBorder(0, 5, 0, 0));
									this.Gaia.janelaJogo.mensagens.add(inf);
									resp.MENSAGEM = "<i>"+resp.MENSAGEM+"</i>";
								break;
								case 3:
									inf = new JLabel(new ImageIcon(this.Gaia.imgWeapon201));
									inf.setBorder(new EmptyBorder(0, 5, 0, 0));
									this.Gaia.janelaJogo.mensagens.add(inf);
									resp.MENSAGEM = "<i>"+resp.MENSAGEM+"</i>";
								break;
								default:
									prefixo = "<span style='color: #888; font-size: 8px; font-family: verdana'>&nbsp;&nbsp;"+resp.NICK+":</span> ";
							}
							
							JTextPane nMsg = new JTextPane();
							nMsg.setContentType("text/html");
							nMsg.setText("<html><div style='background-color: "+cor+"; width: 400;padding-top: 5; padding-bottom: 5'>"+prefixo+"<span style='color: #222; font-size: 8px; font-family: verdana'>"+resp.MENSAGEM+"</span></span></html>");
							nMsg.setEditable(false);
							nMsg.setBorder(null);
							nMsg.setForeground(UIManager.getColor("Label.foreground"));
							
							this.Gaia.janelaJogo.mensagens.add(nMsg, "width 400!,left, wrap");
						
							this.Gaia.novaMsgSom.play();
						}
					break;
					case "filaStatus":
						this.Gaia.janelaJogo.filaStatus.setText("<html><span style='color: white; font-size: 8px'>"+resp.MENSAGEM+"</span></html>");
					break;
					case "atulLogados":
						if(this.Gaia.inicializado) {
							switch(resp.ACAO) {
								case "inserir":
									String nick = "";
									
									switch(resp.STATUS) {
										case "em_fila":
											cor 	= "#8B5742";
											nick 	= "<b>"+resp.NICK+"</b>";
										break;
										case "em_jogo":
											cor 	= "#2E8B57";
											nick 	= "<b>"+resp.NICK+"</b>";
										break;
										case "logado":
										default:
											cor 	= "#222";
											nick 	= resp.NICK;
									}

									if(this.Gaia.janelaJogo.logadosLabels.get(resp.NICK) == null) {
										this.Gaia.janelaJogo.logadosLabels.put(resp.NICK, new JLabel());
										this.Gaia.janelaJogo.logados.add(this.Gaia.janelaJogo.logadosLabels.get(resp.NICK), "alignx left, wrap");
									}

									this.Gaia.janelaJogo.logadosLabels.get(resp.NICK).setText("<html><span style='color: "+cor+"; font-size: 8px;text-align: left'>"+nick+"</span></html>");
								break;
								case "remover":
									if(this.Gaia.janelaJogo.logadosLabels.get(resp.NICK) != null) {
										this.Gaia.janelaJogo.logados.remove(this.Gaia.janelaJogo.logadosLabels.get(resp.NICK));
										this.Gaia.janelaJogo.logadosLabels.remove(resp.NICK);
									}
								break;
							}

							this.Gaia.janelaJogo.jogadoresOnline.setText("<html><span style='color: white; font-size: 8px'>"+this.Gaia.janelaJogo.logadosLabels.size()+"</span></html>");
						}
					break;
					case "atulServers":
						if(this.Gaia.inicializado) {
							this.Gaia.janelaJogo.servidoresDisponiveis.setText("<html><span style='color: green; font-size: 8px'>"+resp.disponiveis+"</span></html>");
							this.Gaia.janelaJogo.servidoresTotais.setText("<html><span style='color: white; font-size: 8px'>"+resp.totais+"</span></html>");
						}
					break;
					case "atualizar":
						apiResp.infosResp infos = (new Gson()).fromJson((new Gson()).toJson(resp.infos), apiResp.infosResp.class);
						
						if(infos.atualizacoes != null) {
							Iterator<apiResp.atualizacoesResp> i = infos.atualizacoes.iterator();
							while(i.hasNext()) {
								apiResp.atualizacoesResp atualizacao = i.next();
	
								switch(atualizacao.ELEMENTO) {
									case "mensagem":
										JOptionPane.showMessageDialog(this.Gaia.frame, atualizacao.VALOR);
									break;
								}
							}
						}
					break;
					case "partidaFormada":
						if(partidaInfo.get(resp.partidaid) == null) {
							partidaInfo.put(resp.partidaid, new HashMap<String, JLabel>());
						}
						
						this.Gaia.janelaJogo.filaStatus.setText("<html><span style='color: green; font-size: 8px'>Partida encontrada!</span></html>");
						this.Gaia.janelaJogo.jogar.setEnabled(false);
						this.Gaia.janelaJogo.jogar.setText("Em jogo");
						
						this.Gaia.janelaPronto.abrir();
						
						JPanel corpo = new JPanel(new MigLayout("insets 0 20 0 0"));
						corpo.setOpaque(false);
						
						String[] jogadores = resp.jogadores.split(",");
						
						for(String e : jogadores) {
							corpo.add(new JLabel("<html><span style='color: white; font-size: 14px'>"+e+"</span></html>"), "left");
							partidaInfo.get(resp.partidaid).put(e, new JLabel("<html><span style='color: red; font-size: 8px; padding-left: 40px;'>Aguardando...</span></html>"));
							corpo.add(partidaInfo.get(resp.partidaid).get(e), "right, wrap");
						}
						
						janelaPronto.botaoPronto = new JButton("<html><span style='text-align: center; font-size: 15px;'>Pronto</span></html>");
						janelaPronto.botaoPronto.setHorizontalAlignment(JButton.CENTER);
						
						janelaPronto.botaoPronto.addMouseListener(new MouseAdapter() {
				    		  public void mouseClicked(MouseEvent e) {
				    			  if (e.getButton() == MouseEvent.BUTTON1) {
				    				  if(!Gaia.estaPronto) {
				    					  Gaia.estaPronto = true;
					    				  janelaPronto.botaoPronto.setText("<html><span style='text-align: center; font-size: 15px;'>Aguarde</span></html>");
					    				  janelaPronto.botaoPronto.setEnabled(false);

					    				  Gaia.apiRequest.cmd("a=confirmarPronto");
				    				  } else {
				    					  Gaia.estaPronto = false;
					    				  janelaPronto.botaoPronto.setEnabled(true);
					    				  
					    				  Gaia.apiRequest.cmd("a=cancelarPronto");
				    				  }
				    			  }
				    		  }
				    	});
						
						corpo.add(janelaPronto.botaoPronto, "bottom");
						
						janelaPronto.layout.add(corpo);						
						janelaPronto.layout.updateUI();
					break;
					case "cancelarPartida":
						try {
							this.Gaia.killProcess(this.Gaia.gtaProcNome);
						} catch (Exception e1) {
							//JOptionPane.showMessageDialog(BRZLauncher.frame, e1.getStackTrace());
						}
						
						janelaPronto.sair();
					break;
					case "mensagem":
						JOptionPane.showMessageDialog(this.Gaia.frame, resp.MENSAGEM);
					break;
					case "setarPronto":
						partidaInfo.get(resp.partidaid).get(resp.NICK).setText("<html><span style='color: green; font-size: 12px; padding-left: 40px;'>Pronto!</span></html>");
					break;
					case "entrarServer":
						String serverIP 	= resp.IP;
						String serverSenha 	= resp.SENHA;
						
						this.Gaia.frame.setVisible(true);
						janelaPronto.frame.setVisible(false);
						
						if(!this.Gaia.loginForm.login.equals(this.Gaia.obterSAMPNickRegistro())) {
							this.Gaia.setarSAMPNickRegistro(this.Gaia.loginForm.login);
						}

						String[] comando = {this.Gaia.SAMPPath, serverIP, serverSenha};
						ProcessBuilder builder = new ProcessBuilder(comando);
						builder.redirectErrorStream(true);
						builder.start();
						this.Gaia.threadJogo = new Thread(new monitorarJogo(this.Gaia));
						this.Gaia.threadJogo.start(); 
					break;
					case "abrirServidor":
						int res = JOptionPane.showConfirmDialog(this.Gaia.frame, "Não há servidores disponíveis no momento para o modo de jogo escolhido.\nvocê pode abrir o seu próprio servidor clicando em 'Sim' nesta janela de confirmação.\n\nTenha em mente que modificar ou fechar o servidor no meio de uma partida resulta em punição, podendo levar até o banimento de sua conta.", "Abrir novo servidor", JOptionPane.YES_NO_OPTION);
			        	if(res == JOptionPane.YES_OPTION) {
			        		new Thread(new Gaia.serverSAMPSocket(this.Gaia)).start();
			        		cmd("a=testarServidorSAMP&porta="+ this.Gaia.serverPort);
			        	}
					break;
					case "testarServidorSAMPSucesso":
						System.out.println("Abrindo servidor...");
						new Thread(new Gaia.inicializarServidor(this.Gaia)).start();
					break;
					case "testarServidorSAMPFalha":
						System.out.println("A porta "+this.Gaia.serverPort+" não está aberta no roteador.");
						JOptionPane.showMessageDialog(this.Gaia.frame, "Para abrir um servidor você deve ter a porta "+this.Gaia.serverPort+" aberta em seu roteador ou firewall.");
					break;
					case "emPunicao":
						this.Gaia.estaEmFila = false;
						this.Gaia.janelaJogo.jogar.setText("<html><span style='text-align: center; font-size: 12px;'>Jogar!</span></html>");
						this.Gaia.janelaJogo.filaStatus.setVisible(false);
						this.Gaia.janelaJogo.filaStatus.setText("<html><span style='color: white; font-size: 8px'>enviando requisição...</span></html>");
						JOptionPane.showMessageDialog(this.Gaia.frame, "Você foi punido recentemente e deve aguardar "+formatarTempo(resp.puniAte)+" para voltar a jogar.");
					break;
				}
				
				this.Gaia.janelaJogo.layout.updateUI();
			}
		}
	}
	
	public String formatarTempo(int segundos) {
		String output = null;
		
		if(segundos < 60) {
			output = segundos + " segundo(s)";
		} else if(segundos < 3600) {
			output = (segundos / 60) + " minuto(s)";
		} else if(segundos < 86400) {
			output = (segundos / 3600) + " hora(s)";
		} else if(segundos < 2592000) {
			output = (segundos / 86400) + " dia(s)";
		} else if(segundos < 946080000) {
			output = (segundos / 2592000) + " ano(s)";
		}
		
		return output;
	}
	
	public class monitorarJogo implements Runnable {
		// Referência da classe principal
		private Gaia Gaia = null;
		
		public monitorarJogo(Gaia g) {
			this.Gaia = g;
		}
		
		public void run() {			
			try {
				Thread.sleep(5000);
				
				String processos = this.Gaia.isProcessRunning(this.Gaia.gtaProcNome);
				Matcher m = (Pattern.compile("exe.*?(\\d+)")).matcher(processos);

				if(m.find()) {
					Gaia.gtaPid = new Integer(m.group(1));
					
					while(this.Gaia.isProcessRunning(this.Gaia.gtaProcNome) != null) {
						Thread.sleep(1000);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			this.Gaia.gtaPid = -1;
			this.Gaia.estaEmFila = false;
			
			this.Gaia.janelaJogo.jogar.setText("<html><span style='text-align: center; font-size: 12px;'>Jogar!</span></html>");
			this.Gaia.janelaJogo.jogar.setEnabled(true);
			this.Gaia.janelaJogo.filaStatus.setVisible(false);
			this.Gaia.janelaJogo.filaStatus.setText("<html><span style='color: white; font-size: 8px'>enviando requisição...</span></html>");
			this.Gaia.apiRequest.cmd("a=sairPartida");
			
			this.Gaia.frame.setVisible(true);
			this.Gaia.estaEmFila = false;
			this.Gaia.estaPronto = false;
		}
	}
	
	public class Request implements Runnable {
		// Referência da classe principal
		private Gaia Gaia = null;
		
		public Request(Gaia g) {
			this.Gaia = g;
		}
		
		public void run() {
			try {               
				streamReader 	= new InputStreamReader(sock.getInputStream());
				reader 			= new BufferedReader(streamReader);
				writer 			= new PrintWriter(sock.getOutputStream());

				Thread readerThread = new Thread(new IncomingReader(this.Gaia));
				readerThread.start();
        	} catch (IOException E) {
        		JOptionPane.showMessageDialog(this.Gaia.frame, E.getStackTrace());
			}
		}
	}
	
	public class IncomingReader implements Runnable {
		// Referência da classe principal
		private Gaia Gaia = null;
		
		public IncomingReader(Gaia g) {
			this.Gaia = g;
		}
		
		public void run() {
			String resposta	= "";
			
			try {
				System.out.println("ouvindo o servidor...");
				while((resposta = reader.readLine()) != null) {
	                Gson gson = new Gson();
	                System.out.println("Comando recebido pelo servidor: "+resposta);
	                try {
		                apiResp resp = gson.fromJson(resposta, apiResp.class);
		                apiResp(resp);
	                } catch(JsonParseException e) {
	                	
	                }
				}
			} catch(Exception e) {
				JOptionPane.showMessageDialog(this.Gaia.frame, "A comunicação com o servidor foi perdida.\n\nPara resolver, reinicie o programa. Caso o erro persista, crie um tópico sobre o erro em:\nhttp://samp.brazucas-server.com/forum");
				this.Gaia.deslogar();
			}
		}
	}
}
