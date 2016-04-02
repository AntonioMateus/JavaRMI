import java.util.Map; 
import java.rmi.Remote; 
import java.rmi.RemoteException; 

public interface Part extends Remote {
	public int getCodigo() throws RemoteException;
	public String getNome() throws RemoteException; 
	public String getDescricao() throws RemoteException;
	public Map<Part,Integer> getComponentes() throws RemoteException;
	public void setNome(String nome) throws RemoteException;
	public void setDescricao(String descricao) throws RemoteException; 
	public void adicionaSubComponente(Part subpeca, int quantidade) throws RemoteException;
	public boolean ehPrimitiva() throws RemoteException;
}