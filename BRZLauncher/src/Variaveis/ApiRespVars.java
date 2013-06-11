package Variaveis;

import java.util.List;

public class ApiRespVars {
	public int 		status			= 0;
	public String 	target			= null;
	public String 	html 			= null;
	public int 		CODIGO			= 0;
	public String 	funcao			= null;
	public String 	chave 			= null;
	public Object 	infos 			= null;
	
	public String 	NICK 			= null;
	public String 	MENSAGEM  		= null;
	public long		UNIX_TIMESTAMP 	= 0;
	public int 		TIPO			= 0;
	public String 	STATUS			= null;
	public String 	ACAO			= null;
	
	public int 		NUMERO			= 0;
	public int 		disponiveis		= 0;
	public int 		totais			= 0;
	public int 		partidaid		= -1;
	
	public String	IP				= null;
	public String 	SENHA			= null;
	
	public String jogadores			= null;
	public int puniAte				= 0;
	
	public static class infosResp {
		public int logados 										= 0;
		public List<servidoresResp> 		servidores 			= null;
		public List<atualizacoesResp> 		atualizacoes 		= null;
	}
	
	public static class servidoresResp {
		public String IP 	= null;
		public String Senha = null;
	}
	
	public static class atualizacoesResp {
		public String 	NICK 		= null;
		public String 	ELEMENTO 	= null;
		public String 	VALOR		= null;
		public String 	CALLBACK	= null;
		public String 	EXTRA1 		= null;
		public String 	EXTRA2 		= null;
		public String 	EXTRA3 		= null;
		public String 	EXTRA4 		= null;
		public String 	EXTRA5 		= null;
		public String 	TIMESTAMP 	= null;
	}
}
