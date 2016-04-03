import java.util.List; 
import java.rmi.Remote; 
import java.rmi.RemoteException; 
import java.util.Map; 

/*
* Interface PartRepository
* Comentario: representa uma colecao de pecas
*/

public interface PartRepository extends Remote{
	//metodo que consiste em inserir uma peca na colecao
	public Part inserePeca(String nome, String descricao) throws RemoteException; /*se a peca a ser inserida for primitiva, nao ha
	necessidade de informar quais sao seus subcomponentes */
	public Part inserePeca(String nome, String descricao, Map<Part,Integer> subcomponentes) throws RemoteException; /*se a peca a 
	ser inserida for agregada, seus componentes serao passados como parametro */
	public Part recuperaPeca(int id) throws RemoteException; //busca a peca cujo id foi informado como parametro
	public List<Part> recuperaTodasPecas() throws RemoteException; /*retorna uma lista que contem todas as pecas armazenadas no
	repositorio. */
	public String getNomeServidor() throws RemoteException; //retorna o nome do servidor responsavel pelo repositorio 
	public int getNumeroPecas() throws RemoteException; //retorna o numero de pecas armazenadas no repositorio
	public void removePeca(int id) throws RemoteException; //remove uma peca da lista de pecas no repositorio
}
