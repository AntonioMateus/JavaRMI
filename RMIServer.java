//Entrada de usuário
import java.util.Scanner;
//Estruturas de dados: 
import java.util.LinkedList;
import java.util.List;
import java.util.Map; 
import java.util.HashMap; 
import java.util.Iterator; 
//Java RMI:
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.rmi.server.ExportException; 

/*
* Classe RMIServer 
* Comentario: classe que implementa ambas as interfaces PartRepository e Part
* e representa um servidor RMI. 
*/

public class RMIServer extends UnicastRemoteObject implements PartRepository, Part {
	//***************** Atributos de PartRepository ***************
	private List<Part> listaPecas; //lista de pecas contidas no repositorio
	private String nomeServidor = null; //nome do servidor 
	//********************* Atributos de Part *********************
	private int codigoPeca = 0; /*identificador gerado automaticamente pelo
	sistema quando foram inseridas informacoes sobre a peca */
	private String nomePeca = null; //nome da peca
	private String descricaoPeca = null; //descricao da peca
	Map<Part,Integer> subcomponentes; /*mapa de subcomponentes em 
	que cada par representa uma peca e a respectiva quantidade*/
	//*************************************************************
	
	//construtor usado quando se quer instanciar um novo servidor
	public RMIServer(String nome) throws RemoteException {
		listaPecas = new LinkedList<>();
		this.nomeServidor = nome; 		 
	}
	
	//construtor usado quando se quer instanciar uma nova peca
	public RMIServer(int id, String nome, String descricao) throws RemoteException {
		this.codigoPeca = id; 
		this.nomePeca = nome; 
		this.descricaoPeca = descricao;
		this.subcomponentes = new HashMap<>();
	}
	
	//***************** Metodos da interface Part (jah explicados) ******************
	public int getCodigo() throws RemoteException { 
		return this.codigoPeca;
	}
	
	public String getNome() throws RemoteException { 
		return this.nomePeca;
	}
	
	public String getDescricao() throws RemoteException { 
		return this.descricaoPeca;
	}
	
	public Map<Part,Integer> getComponentes() throws RemoteException { 
		return this.subcomponentes;
	}
	
	public void setNome(String nome) throws RemoteException {
		this.nomePeca = nome;
	}
	
	public void setDescricao(String descricao) throws RemoteException { 
		this.descricaoPeca = descricao;
	}
	
	public void adicionaSubComponente(Part subpeca, int quantidade) throws RemoteException {
		this.subcomponentes.put(subpeca,new Integer(quantidade)); 
	}
	
	public boolean ehPrimitiva() throws RemoteException {
		return (this.subcomponentes.size()==0);
	}
	
	public void removeSubComponente(int id) throws RemoteException {
		Iterator<Part> pecas = this.subcomponentes.keySet().iterator(); 
		Part pecaARemover = null; 
		while (pecas.hasNext()) {
			pecaARemover = pecas.next(); 
			if (pecaARemover.getCodigo()==id) break; 
		}
		if (pecaARemover != null) this.subcomponentes.remove(pecaARemover);
	}
	//***************************************************************************************
	
	//*************** Metodos da interface PartRepository (jah explicados) ******************
	public Part inserePeca(String nome, String descricao) throws RemoteException {
		Part peca = new RMIServer(this.listaPecas.size()+1,nome,descricao); 
		listaPecas.add(peca);
		return peca; 
	}
	
	public Part recuperaPeca(int id) throws RemoteException {
		for (int i = 0; i < listaPecas.size(); i++) {
			if (listaPecas.get(i).getCodigo()==id) return listaPecas.get(i); 
		}
		return null; 
	}
	
	public List<Part> recuperaTodasPecas() throws RemoteException {
		List<Part> listaARetornar = new LinkedList<>(); 
		listaARetornar.addAll(listaPecas);
		return listaARetornar; 
	}
	
	public String getNomeServidor() throws RemoteException {
		return nomeServidor; 
	}
	
	public int getNumeroPecas() throws RemoteException {
		return listaPecas.size(); 
	}
	
	public Part inserePeca(String nome, String descricao, Map<Part,Integer> subcomponentes) throws RemoteException {
		int id = this.listaPecas.size()+1;
		Part peca = new RMIServer(id,nome,descricao); 
		Map<Part,Integer> componentesAux = peca.getComponentes(); 
		componentesAux.putAll(subcomponentes);
		listaPecas.add(peca);	
		return peca; 
	}
	
	public boolean removePeca(int id) throws RemoteException {
		for (int i = 0; i < listaPecas.size(); i++) {
			if (listaPecas.get(i).getCodigo()==id){
				listaPecas.remove(i);
				return true;
			}
		}
		return false;
	}
	//****************************************************************************************
	
	// metodo que fora usado para teste do servidor.
	public static void main (String args[]) {
		if(args.length != 2){
			System.out.println("Execute: java RMIServer [nome do servidor] [porta]");
			System.out.println("\t[nome do servidor] = qualquer nome que vocc deseja dar ao seu servidor");
			System.out.println("\t[porta] = número de porta qua voce desejar registrar o seu servidor. A porta nao pode estar sendo usada por nenhum outro processo. Escolha com cuidado");
			return;
		}
		
		String servidorNome = args[0];
		int porta = Integer.parseInt(args[1]);
		
		try {
			RMIServer rmiServer = new RMIServer(servidorNome); 
			Registry reg = LocateRegistry.createRegistry(porta);
			reg.bind("PartRepositoryServer", rmiServer);
			System.out.println("Servidor inicializado com sucesso.\n" + servidorNome + " está 'escutando' a porta " + porta + ".\n");
			
			Scanner scanner = new Scanner(System.in);
			String entrada = "";
			while( !entrada.equals("exit") && !entrada.equals("quit")){
				System.out.println("Para finalizar o servidor digite 'exit' ou 'quit'.");
				entrada = scanner.nextLine();
			}
			scanner.close();
		}catch (Exception x) {
			System.out.println ("Erro ao vincular o servidor "+ servidorNome + " a porta " + porta + ". Verifique se a porta está disponível." );
			System.out.println ("Dica: tente portas acima de 1091 ;)");
		}
		System.exit(1);
	}
}
