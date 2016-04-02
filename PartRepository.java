import java.util.List; 
import java.rmi.Remote; 
import java.rmi.RemoteException; 
import java.util.Map; 

public interface PartRepository extends Remote{
	public Part inserePeca(String nome, String descricao) throws RemoteException;
	public Part inserePeca(String nome, String descricao, Map<Part,Integer> subcomponentes) throws RemoteException;
	public Part recuperaPeca(int id) throws RemoteException; 
	public List<Part> recuperaTodasPecas() throws RemoteException; 
	public String getNomeServidor() throws RemoteException; 
	public int getNumeroPecas() throws RemoteException; 
}