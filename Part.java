import java.util.Map; 
import java.rmi.Remote; 
import java.rmi.RemoteException; 

/*
* Interface Part
* Comentario: representa cada uma das pecas. 
*/

public interface Part extends Remote {
	public int getCodigo() throws RemoteException; //retorna o codigo da peca que esta sendo referenciada
	public String getNome() throws RemoteException; //retorna o nome da peca que esta sendo referenciada 
	public String getDescricao() throws RemoteException; //retorna a descricao da peca que esta sendo referenciada
	public Map<Part,Integer> getComponentes() throws RemoteException; /*retorna os subcomponentes da peca que esta 
	sendo referenciada. Tais subcomponentes sao representados por um mapa, em que cada par contem uma peca e a
	correspondente quantidade. */
	public void setNome(String nome) throws RemoteException; //modifica o nome da peca que esta sendo referenciada
	public void setDescricao(String descricao) throws RemoteException; /*modifica a descricao da peca que esta
	sendo referenciada.*/ 
	public void adicionaSubComponente(Part subpeca, int quantidade) throws RemoteException; /*adiciona um
	subcomponente a peca que esta sendo referenciada*/
	public boolean ehPrimitiva() throws RemoteException; /*retorna se a peca eh primitiva (se nao possui
	subcomponentes) ou nao */
}
