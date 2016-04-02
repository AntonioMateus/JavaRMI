//Estruturas de dados: 
import java.util.LinkedList;
import java.util.List;
import java.util.Map; 
import java.util.HashMap; 
//Java RMI:
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.rmi.server.ExportException; 

public class RMIServer extends UnicastRemoteObject implements PartRepository, Part {
	//***************** PartRepository ****************************
	List<Part> listaPecas; 
	String nomeServidor = null; 
	//********************** Part *********************************
	int codigoPeca = 0; /*identificador gerado automaticamente pelo
	sistema quando foram inseridas informacoes sobre a peca */
	String nomePeca = null; 
	String descricaoPeca = null; 
	Map<Part,Integer> subcomponentes; 
	//*************************************************************
	
	public RMIServer(String nome) throws RemoteException {
		listaPecas = new LinkedList<>();
		this.nomeServidor = nome; 		 
	}
	
	public RMIServer(int id, String nome, String descricao) throws RemoteException {
		this.codigoPeca = id; 
		this.nomePeca = nome; 
		this.descricaoPeca = descricao;
		this.subcomponentes = new HashMap<>();
	}
	
	//*********************************************** Part ****************************************************************
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
	//*********************************************************************************************************************
	
	//****************************************** PartRepository ***********************************************************
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
	
	
	//**********************************************************************************************************************
	
	public static void main (String args[]) {
		int porta = Integer.parseInt(args[0].split("or")[1])+1000;
		try {
			RMIServer obj = new RMIServer(args[0]); 
			Registry reg = LocateRegistry.createRegistry(porta);
			reg.bind("PartRepositoryServer",obj);
		}
		catch (ExportException e) {
			//System.out.println ("O servidor " +args[0] +" jah se encontra em uso");
			e.printStackTrace(); 
			System.exit(1); 
		}
		catch (Exception x) {
			System.out.println ("Erro ao vincular o servidor "+args[0] +" a porta " +porta); 
			System.exit(1); 
		}
	}
}