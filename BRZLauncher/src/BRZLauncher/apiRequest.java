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

public class apiRequest {
	public static Thread request 										= null;
	public static Socket sock 											= null;
    public static InputStreamReader streamReader 						= null;
    public static BufferedReader reader 								= null;
    public static PrintWriter writer 									= null;
    public static HashMap<Integer, HashMap<String, JLabel>> partidaInfo	= new HashMap<Integer, HashMap<String, JLabel>>();
    public static boolean scrollPress									= false;
	
	public apiRequest() throws HeadlessException, IOException {
		URL oracle 			= new URL(BRZLauncher.brzUrlAPI+"a=masterIP");
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
        		inicializar.janela.setVisible(false);
            	JOptionPane.showMessageDialog(BRZLauncher.frame, "Ocorreu um erro ao conectar no servidor principal, por favor tente mais tarde.");
            	System.exit(0);
        	}
        }
        
        request = new Thread(new Request());
		request.start();
	}
	
	public static boolean criarConexao(String IP) {
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
			writer.println(options+"&c="+BRZLauncher.chaveAuth+"&t=cliente");
			writer.flush();
			
			System.out.println("Comando enviado para o servidor: "+ options+"&c="+BRZLauncher.chaveAuth);
			
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
				                	
				                	WinRegistry.writeStringValue(WinRegistry.HKEY_CURRENT_USER, "SOFTWARE\\BRZLauncher", "loginData_user", loginForm.login);
				                	WinRegistry.writeStringValue(WinRegistry.HKEY_CURRENT_USER, "SOFTWARE\\BRZLauncher", "loginData_pass", loginForm.senha);
				                	
				                	BRZLauncher.chaveAuth = resp.chave;
				                	
				                	BRZLauncher.registrarLogin(loginForm.login, loginForm.senha);
				                	BRZLauncher.fecharJanela();
				                	new janelaJogo();
				                } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e1) {
				                	JOptionPane.showMessageDialog(BRZLauncher.frame, e1.getStackTrace());
								}
					     	break;
					     	case 2: // Conta não encontrada
					     		JOptionPane.showMessageDialog(BRZLauncher.frame, "Conta não encontrada, verifique se você digitou seu nick corretamente.");
					     	break;
					     	case 3: // Senha incorreta
					     		JOptionPane.showMessageDialog(BRZLauncher.frame, "Login e/ou senha incorretos.");
					     	break;
					     	case 4: // Serviço indisponível (Liberado apenas para staff)
					     		JOptionPane.showMessageDialog(BRZLauncher.frame, resp.html);
					     	break;
						}
						
						BRZLauncher.formularioLogin.remove(loginForm.tempLabel);
						loginForm.inputEnviar.setText("Login");
						loginForm.enviarClicado = false;
					break;
					case "deslogar":
						BRZLauncher.deslogar();
					break;
					case "inicializar":
						janelaJogo.layout.remove(janelaJogo.temp);
						janelaJogo.layout.setBorder(new EmptyBorder(0, 0, 0, 0));
						
						JPanel stats = new JPanel(new MigLayout("alignx left,width 595!,height 80!,insets 0 0 0 0,nogrid"));
				    	stats.setBorder(new EmptyBorder(0, 0, 0, 0));
				    	stats.setOpaque(false);
				    	
				    	JPanel infosSvDiv = new JPanel(new MigLayout("alignx left, aligny top,insets 0 22 0 0,height 80!,width 200!,nogrid"));
				    	infosSvDiv.setOpaque(false);
				    	infosSvDiv.setAlignmentY(Component.TOP_ALIGNMENT);
				    	infosSvDiv.setBorder(new EmptyBorder(0, 0, 0, 0));
					    	infosSvDiv.add(new JLabel(new ImageIcon(BRZLauncher.imgUser)), "alignx left,aligny center"); // Jogadores online
					    	janelaJogo.jogadoresOnline 	= new JLabel("<html><span style='color: white; font-size: 8px'>0</span></html>");
					    	janelaJogo.jogadoresOnline.setLayout(new MigLayout("alignx left,aligny center,insets 0 0 0 0"));
					    	infosSvDiv.add(janelaJogo.jogadoresOnline);
					    	infosSvDiv.add(new JLabel("<html><span style='color: white; font-size: 8px'>Jogadores online</span></html>"), "wrap");
					    	
					    	infosSvDiv.add(new JLabel(new ImageIcon(BRZLauncher.imgServer)), "alignx left,top"); // Servidores online
					    	janelaJogo.servidoresDisponiveis 	= new JLabel("<html><span style='color: green; font-size: 8px'>0</span></html>");
					    	janelaJogo.servidoresDisponiveis.setLayout(new MigLayout("alignx left,aligny center,insets 0 0 0 0"));
					    	janelaJogo.servidoresTotais 		= new JLabel("<html><span style='color: white; font-size: 8px'>0</span></html>");
					    	janelaJogo.servidoresTotais.setLayout(new MigLayout("alignx left,aligny center,insets 0 0 0 0"));
					    	infosSvDiv.add(janelaJogo.servidoresDisponiveis);
					    	infosSvDiv.add(new JLabel("<html><span style='color: white; font-size: 8px'>/</span></html>"));
					    	infosSvDiv.add(janelaJogo.servidoresTotais);
					    	infosSvDiv.add(new JLabel("<html><span style='color: white; font-size: 8px'><b>Servidores disponíveis</b></span></html>"), "wrap");
					    	
					    	infosSvDiv.add(new JLabel(new ImageIcon(BRZLauncher.imgClock)), "alignx left,top"); // Tempo de espera para começar partida
					    	janelaJogo.tempoAproximado = new JLabel("<html><span style='color: white; font-size: 8px'>0</span></html>");
					    	janelaJogo.tempoAproximado.setLayout(new MigLayout("alignx left,aligny center,insets 0 0 0 0"));
					    	janelaJogo.filaStatus.setVisible(false);
					    	infosSvDiv.add(janelaJogo.filaStatus, "wrap");
				    	JPanel infosPlDiv = new JPanel(new MigLayout("alignx right,top,insets 0 0 0 0,height 80!,nogrid"));
				    	infosPlDiv.setOpaque(false);
				    	infosPlDiv.setBorder(new EmptyBorder(0, 0, 0, 0));	    	
			    			infosPlDiv.add(new JLabel(new ImageIcon(BRZLauncher.imgWeapon50)), "alignx left,top"); // Partidas disputadas
			    			janelaJogo.plPartidasDisputadas = new JLabel("<html><span style='color: white; font-size: 8px'>0</span></html>");
					    	janelaJogo.plPartidasDisputadas.setLayout(new MigLayout("alignx left,aligny top,insets 0 0 0 0"));
					    	infosPlDiv.add(janelaJogo.plPartidasDisputadas);
					    	infosPlDiv.add(new JLabel("<html><span style='color: white; font-size: 8px'><b>Partidas disputadas</b></span></html>"), "wrap");
					    	
			    			infosPlDiv.add(new JLabel(new ImageIcon(BRZLauncher.imgWeapon51)), "alignx left,top"); // Vitórias
			    			janelaJogo.plVitorias = new JLabel("<html><span style='color: white; font-size: 8px'>0</span></html>");
					    	janelaJogo.plVitorias.setLayout(new MigLayout("align left,aligny center,insets 0 0 0 0"));
					    	infosPlDiv.add(janelaJogo.plVitorias);
					    	infosPlDiv.add(new JLabel("<html><span style='color: white; font-size: 8px'><b>Vitórias</b></span></html>"), "wrap");
					    	
					    	infosPlDiv.add(new JLabel(new ImageIcon(BRZLauncher.imgWeapon53)), "alignx left,top"); // Matou
					    	janelaJogo.plMatou = new JLabel("<html><span style='color: white; font-size: 8px'>0</span></html>");
					    	janelaJogo.plMatou.setLayout(new MigLayout("align left,aligny center,insets 0 0 0 0"));
					    	infosPlDiv.add(janelaJogo.plMatou);
					    	infosPlDiv.add(new JLabel("<html><span style='color: white; font-size: 8px'><b>Kills</b></span></html>"), "wrap");
					    	
					    	infosPlDiv.add(new JLabel(new ImageIcon(BRZLauncher.imgWeapon49)), "alignx left,top"); // Morreu
					    	janelaJogo.plMorreu = new JLabel("<html><span style='color: white; font-size: 8px'>0</span></html>");
					    	janelaJogo.plMorreu.setLayout(new MigLayout("align left,aligny center,insets 0 0 0 0"));
					    	infosPlDiv.add(janelaJogo.plMorreu);
					    	infosPlDiv.add(new JLabel("<html><span style='color: white; font-size: 8px'><b>Morreu</b></span></html>"), "wrap");
					    	
					    	infosPlDiv.add(new JLabel("<html><span style='color: white; font-size: 8px'>a</span></html>"), "alignx right,top"); // Ranking
					    	janelaJogo.plRanking = new JLabel("<html><span style='color: white; font-size: 8px'>0</span></html>");
					    	janelaJogo.plRanking.setLayout(new MigLayout("align left,aligny center,insets 0 0 0 0"));
					    	infosPlDiv.add(janelaJogo.plRanking);
					    	infosPlDiv.add(new JLabel("<html><span style='color: white; font-size: 8px'><b>Ranking</b></span></html>"), "wrap");
					    	
				    	stats.add(infosSvDiv);
				    	stats.add(infosPlDiv, "wrap");
				    	
				    	janelaJogo.layout.add(stats, "wrap");
				    	
				    	JPanel chat = new JPanel(new MigLayout("alignx center,aligny top,width 600!,height 200!,insets 0 0 0 0,nogrid"));
				    	chat.setOpaque(false);
				    	chat.setAlignmentY(JPanel.TOP_ALIGNMENT);
				    	chat.setAlignmentX(JPanel.LEFT_ALIGNMENT);
				    	chat.setBorder(new EmptyBorder(0, 0, 0, 0));
					    	JPanel formulario = new JPanel(new MigLayout("alignx left,aligny top,height 200!,nogrid"));
					    	formulario.setOpaque(false);
					    	formulario.setAlignmentY(Component.TOP_ALIGNMENT);
					    	formulario.setBorder(new EmptyBorder(0, 0, 0, 0));
					    		janelaJogo.mensagens 		= new JPanel(new MigLayout("alignx left,aligny top,insets 0 0 0 0,nogrid"));
					    		janelaJogo.mensagens.setBorder(new EmptyBorder(0, 0, 0, 0));
					    		janelaJogo.mensagens.setBackground(Color.WHITE);
					    		janelaJogo.mensagens.setAlignmentY(Component.TOP_ALIGNMENT);
					    		JScrollPane editorScroll 	= new JScrollPane(janelaJogo.mensagens, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
					    		
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
						    	        	if((new Date().getTime()) - BRZLauncher.chatUltimaMsg > 2000) {
						    	        		if(msg.getText().length() > 0) {
							    	        		if(msg.getText().length() > 128) {
							    	        			JOptionPane.showMessageDialog(BRZLauncher.frame, "Mensagem muito grande, máximo de 128 caracteres.");
							    	        		} else {
								    	        		BRZLauncher.apiRequest.cmd("a=chat&msg="+BRZLauncher.encodeURIComponent(msg.getText()));
									    	        	msg.setText("");
									    	        	BRZLauncher.chatUltimaMsg = new Date().getTime();
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
						    	janelaJogo.logados = new JPanel(new MigLayout("alignx left,top,insets 0 0 0 0,nogrid"));
						    	janelaJogo.logados.setBackground(Color.WHITE);
						    	janelaJogo.logados.setAlignmentX(JPanel.LEFT_ALIGNMENT);
						    	janelaJogo.logados.setBorder(new EmptyBorder(0, 19, 0, 1));
						    	JScrollPane logadosScroll 	= new JScrollPane(janelaJogo.logados, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
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
						    	
						    	janelaJogo.jogar = new JButton("<html><span style='text-align: center; font-size: 12px;'>Jogar!</span></html>");
						    	janelaJogo.jogar.setHorizontalAlignment(JButton.CENTER);
						    	janelaJogo.jogar.setPreferredSize(new Dimension(150, 45));
						    	ladoDireito.add(janelaJogo.jogar, "bottom, wrap");
						    	
						    	janelaJogo.jogar.addMouseListener(new MouseAdapter() {
						    		  public void mouseClicked(MouseEvent e) {
						    			  if (e.getButton() == MouseEvent.BUTTON1) {
						    				  if(!BRZLauncher.estaEmFila) {
							    				  BRZLauncher.estaEmFila = true;
							    				  janelaJogo.jogar.setText("<html><span style='text-align: center; font-size: 12px;'>Cancelar</span></html>");
							    				  janelaJogo.filaStatus.setVisible(true);
	
							    				  BRZLauncher.apiRequest.cmd("a=entrarFila&modo="+modosJogo.getSelection().getActionCommand());
						    				  } else {
						    					  BRZLauncher.estaEmFila = false;
						    					  janelaJogo.jogar.setText("<html><span style='text-align: center; font-size: 12px;'>Jogar!</span></html>");
						    					  janelaJogo.filaStatus.setVisible(false);
						    					  janelaJogo.filaStatus.setText("<html><span style='color: white; font-size: 8px'>enviando requisição...</span></html>");
						    					  BRZLauncher.apiRequest.cmd("a=sairFila");
						    				  }
						    			  }
						    		  }
						    	});
						    	
						    chat.add(ladoDireito);
				    	janelaJogo.layout.add(chat, "wrap");
				    	BRZLauncher.inicializado = true;
				    	BRZLauncher.apiRequest.cmd("a=sincronizar");
				    	
				    	/*
				    	BRZLauncher.loopAtul.scheduleAtFixedRate(new TimerTask() {
				            public void run() {
				            	BRZLauncher.apiRequest.cmd("a=atualizar&ua="+BRZLauncher.chatUltimaAtul);
				            }
				        }, 0, 5000);
				        */
					break;
					case "chatMsg":
						if(BRZLauncher.inicializado) {
							++BRZLauncher.chatMsgs;
							
							if(BRZLauncher.chatMsgs%2 == 0) {
								cor = "#DDDDDD";
							} else {
								cor = "#FFFFFF";
							}
							
							BRZLauncher.chatUltimaAtul = resp.UNIX_TIMESTAMP;
							resp.MENSAGEM = StringEscapeUtils.escapeHtml4(BRZLauncher.decodeURIComponent(resp.MENSAGEM));
							
							JLabel inf = null;
							switch(resp.TIPO) {
								case 2:
									inf = new JLabel(new ImageIcon(BRZLauncher.imgWeapon200));
									inf.setBorder(new EmptyBorder(0, 5, 0, 0));
									janelaJogo.mensagens.add(inf);
									resp.MENSAGEM = "<i>"+resp.MENSAGEM+"</i>";
								break;
								case 3:
									inf = new JLabel(new ImageIcon(BRZLauncher.imgWeapon201));
									inf.setBorder(new EmptyBorder(0, 5, 0, 0));
									janelaJogo.mensagens.add(inf);
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
							
							janelaJogo.mensagens.add(nMsg, "width 400!,left, wrap");
						
							BRZLauncher.novaMsgSom.play();
						}
					break;
					case "filaStatus":
						janelaJogo.filaStatus.setText("<html><span style='color: white; font-size: 8px'>"+resp.MENSAGEM+"</span></html>");
					break;
					case "atulLogados":
						if(BRZLauncher.inicializado) {
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

									if(janelaJogo.logadosLabels.get(resp.NICK) == null) {
										janelaJogo.logadosLabels.put(resp.NICK, new JLabel());
										janelaJogo.logados.add(janelaJogo.logadosLabels.get(resp.NICK), "alignx left, wrap");
									}

									janelaJogo.logadosLabels.get(resp.NICK).setText("<html><span style='color: "+cor+"; font-size: 8px;text-align: left'>"+nick+"</span></html>");
								break;
								case "remover":
									if(janelaJogo.logadosLabels.get(resp.NICK) != null) {
										janelaJogo.logados.remove(janelaJogo.logadosLabels.get(resp.NICK));
										janelaJogo.logadosLabels.remove(resp.NICK);
									}
								break;
							}

							janelaJogo.jogadoresOnline.setText("<html><span style='color: white; font-size: 8px'>"+janelaJogo.logadosLabels.size()+"</span></html>");
						}
					break;
					case "atulServers":
						if(BRZLauncher.inicializado) {
							janelaJogo.servidoresDisponiveis.setText("<html><span style='color: green; font-size: 8px'>"+resp.disponiveis+"</span></html>");
							janelaJogo.servidoresTotais.setText("<html><span style='color: white; font-size: 8px'>"+resp.totais+"</span></html>");
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
										JOptionPane.showMessageDialog(BRZLauncher.frame, atualizacao.VALOR);
									break;
								}
							}
						}
					break;
					case "partidaFormada":
						if(partidaInfo.get(resp.partidaid) == null) {
							partidaInfo.put(resp.partidaid, new HashMap<String, JLabel>());
						}
						
						janelaJogo.filaStatus.setText("<html><span style='color: green; font-size: 8px'>Partida encontrada!</span></html>");
						janelaJogo.jogar.setEnabled(false);
						janelaJogo.jogar.setText("Em jogo");
						
						new janelaPronto();
						
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
				    				  if(!BRZLauncher.estaPronto) {
					    				  BRZLauncher.estaPronto = true;
					    				  janelaPronto.botaoPronto.setText("<html><span style='text-align: center; font-size: 15px;'>Aguarde</span></html>");
					    				  janelaPronto.botaoPronto.setEnabled(false);

					    				  BRZLauncher.apiRequest.cmd("a=confirmarPronto");
				    				  } else {
				    					  BRZLauncher.estaPronto = false;
					    				  janelaPronto.botaoPronto.setEnabled(true);
					    				  
					    				  BRZLauncher.apiRequest.cmd("a=cancelarPronto");
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
							BRZLauncher.killProcess(BRZLauncher.gtaProcNome);
						} catch (Exception e1) {
							//JOptionPane.showMessageDialog(BRZLauncher.frame, e1.getStackTrace());
						}
						
						janelaPronto.sair();
					break;
					case "mensagem":
						JOptionPane.showMessageDialog(BRZLauncher.frame, resp.MENSAGEM);
					break;
					case "setarPronto":
						partidaInfo.get(resp.partidaid).get(resp.NICK).setText("<html><span style='color: green; font-size: 12px; padding-left: 40px;'>Pronto!</span></html>");
					break;
					case "entrarServer":
						String serverIP 	= resp.IP;
						String serverSenha 	= resp.SENHA;
						
						BRZLauncher.frame.setVisible(true);
						janelaPronto.frame.setVisible(false);
						
						if(!loginForm.login.equals(BRZLauncher.obterSAMPNickRegistro())) {
							BRZLauncher.setarSAMPNickRegistro(loginForm.login);
						}

						String[] comando = {BRZLauncher.SAMPPath, serverIP, serverSenha};
						ProcessBuilder builder = new ProcessBuilder(comando);
						builder.redirectErrorStream(true);
						builder.start();
						BRZLauncher.threadJogo = new Thread(new monitorarJogo());
						BRZLauncher.threadJogo.start(); 
					break;
					case "abrirServidor":
						int res = JOptionPane.showConfirmDialog(BRZLauncher.frame, "Não há servidores disponíveis no momento para o modo de jogo escolhido.\nvocê pode abrir o seu próprio servidor clicando em 'Sim' nesta janela de confirmação.\n\nTenha em mente que modificar ou fechar o servidor no meio de uma partida resulta em punição, podendo levar até o banimento de sua conta.", "Abrir novo servidor", JOptionPane.YES_NO_OPTION);
			        	if(res == JOptionPane.YES_OPTION) {
			        		new Thread(new BRZLauncher.serverSAMPSocket()).start();
			        		cmd("a=testarServidorSAMP&porta="+ BRZLauncher.serverPort);
			        	}
					break;
					case "testarServidorSAMPSucesso":
						System.out.println("Abrindo servidor...");
						new Thread(new BRZLauncher.inicializarServidor()).start();
					break;
					case "testarServidorSAMPFalha":
						System.out.println("A porta "+BRZLauncher.serverPort+" não está aberta no roteador.");
						JOptionPane.showMessageDialog(BRZLauncher.frame, "Para abrir um servidor você deve ter a porta "+BRZLauncher.serverPort+" aberta em seu roteador ou firewall.");
					break;
					case "emPunicao":
						BRZLauncher.estaEmFila = false;
  					  	janelaJogo.jogar.setText("<html><span style='text-align: center; font-size: 12px;'>Jogar!</span></html>");
  					  	janelaJogo.filaStatus.setVisible(false);
  					  	janelaJogo.filaStatus.setText("<html><span style='color: white; font-size: 8px'>enviando requisição...</span></html>");
						JOptionPane.showMessageDialog(BRZLauncher.frame, "Você foi punido recentemente e deve aguardar "+formatarTempo(resp.puniAte)+" para voltar a jogar.");
					break;
				}
				
				janelaJogo.layout.updateUI();
			}
		}
	}
	
	public static String formatarTempo(int segundos) {
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
		public void run() {			
			try {
				Thread.sleep(5000);
				
				String processos = BRZLauncher.isProcessRunning(BRZLauncher.gtaProcNome);
				Matcher m = (Pattern.compile("exe.*?(\\d+)")).matcher(processos);

				if(m.find()) {
					BRZLauncher.gtaPid = new Integer(m.group(1));
					
					while(BRZLauncher.isProcessRunning(BRZLauncher.gtaProcNome) != null) {
						Thread.sleep(1000);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			BRZLauncher.gtaPid = -1;
			BRZLauncher.estaEmFila = false;
			
			janelaJogo.jogar.setText("<html><span style='text-align: center; font-size: 12px;'>Jogar!</span></html>");
			janelaJogo.jogar.setEnabled(true);
			janelaJogo.filaStatus.setVisible(false);
			janelaJogo.filaStatus.setText("<html><span style='color: white; font-size: 8px'>enviando requisição...</span></html>");
			BRZLauncher.apiRequest.cmd("a=sairPartida");
			
			BRZLauncher.frame.setVisible(true);
			BRZLauncher.estaEmFila = false;
			BRZLauncher.estaPronto = false;
		}
	}
	
	public class Request implements Runnable {
		public void run() {
			try {               
				streamReader 	= new InputStreamReader(sock.getInputStream());
				reader 			= new BufferedReader(streamReader);
				writer 			= new PrintWriter(sock.getOutputStream());

				Thread readerThread = new Thread(new IncomingReader());
				readerThread.start();
        	} catch (IOException E) {
        		JOptionPane.showMessageDialog(BRZLauncher.frame, E.getStackTrace());
			}
		}
	}
	
	public class IncomingReader implements Runnable {
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
				JOptionPane.showMessageDialog(BRZLauncher.frame, "A comunicação com o servidor foi perdida.\n\nPara resolver, reinicie o programa. Caso o erro persista, crie um tópico sobre o erro em:\nhttp://samp.brazucas-server.com/forum");
				BRZLauncher.deslogar();
			}
		}
	}
}
