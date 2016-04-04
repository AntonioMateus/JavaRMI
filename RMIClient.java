//Java RMI
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.ConnectException;
import java.rmi.NotBoundException;
//Estruturas de dados
import java.util.List; 
import java.util.Map;
import java.util.HashMap; 
import java.util.Iterator; 
//Leitura do fluxo de entrada
import java.util.Scanner; 

/*
* Classe RMIClient
* Comentarios: 
*/

public class RMIClient {
	static PartRepository repositorioCorrente = null; //servidor (repositorio) que esta sendo referenciado 
	static Part pecaCorrente = null; //peca que esta sendo referenciada
	static Map<Part,Integer> listaSubpecasCorrente = new HashMap<>(); /* "lista" de subpecas que esta sendo
	referenciada no momento. Eh implementada como um mapa em que cada par representa uma peca e a sua
	correspondente quantidade. */
	
	/* metodo que lista todas as pecas presentes em um repositorio, apresentando apenas o nome e a descricao
	de cada um. */
	static void listaPecasRepositorio() throws RemoteException {
		if (estaConectado()) {
			List<Part> pecas = repositorioCorrente.recuperaTodasPecas();
			if (pecas.size() != 0) {
				for (Part peca: pecas) {
					System.out.println("Nome: "+peca.getNome() +"; descricao: "+peca.getDescricao());
				}
			}
			else {
				System.out.println("O repositorio esta vazio.");
			}
		}
	}	
	
	//metodo que retorna o nome de um servidor e o numero de pecas existente no repositorio correspondente
	static String getSituacaoRepositorio() throws RemoteException {
		if (!estaConectado()) return "";
		else return "nome: "+repositorioCorrente.getNomeServidor() +"; numero de pecas: "+repositorioCorrente.getNumeroPecas(); 
	}
	
	//metodo que busca uma peca no repositorio corrente, atraves de seu identificador
	static Part buscaPeca(int id) throws RemoteException {
		if (!estaConectado()) {
			return null;
		}		
		return repositorioCorrente.recuperaPeca(id);
	}
	
	//metodo que insere uma nova peca agregada (isto eh, que possui subcomponentes)
	static Part insereNovaPecaAgregada(String nome, String descricao, Map<Part,Integer> componentes) throws RemoteException {
		if (!estaConectado()) {
			return null;
		}
		return repositorioCorrente.inserePeca(nome, descricao, componentes);
	}
	
	//metodo que insere uma nova peca primitiva (ou seja, que nao possui subcomponentes)
	static Part insereNovaPecaPrimitiva(String nome, String descricao) throws RemoteException {
		if (!estaConectado()) {
			return null;
		}
		return repositorioCorrente.inserePeca(nome, descricao);
	}
	
	//metodo que retorna o nome e a desscricao da peca corrente, se esta for valida. 
	static String getCaracteristicasPecaCorrente() throws RemoteException {
		if (estaReferenciandoPeca()) return "Nome: "+pecaCorrente.getNome() +"; descricao: "+pecaCorrente.getDescricao(); 
		else return "nao ha nada a declarar";
	}
	
	// se o cliente estiver conectado a algum servidor, seu nome sera retornado pelo metodo abaixo
	static String getNomeRepositorioCorrente() throws RemoteException {
		if (!estaConectado()) return "";
		return repositorioCorrente.getNomeServidor(); 
	}
	
	// metodo que retorna se a peca corrente possui subcomponentes ou não
	static boolean pecaEhPrimitiva() throws RemoteException {
		if (!estaReferenciandoPeca()) {
			return false; 
		}
		return (pecaCorrente.getComponentes().size()==0);
	}
	
	// metodo que retorna o numero de subcomponentes da peca corrente.
	static int numeroSubcomponentes() throws RemoteException {
		if (!estaReferenciandoPeca()) {
			return 0; 
		}
		return pecaCorrente.getComponentes().size(); 
	}
	
	/* se o cliente estiver referenciado alguma peca, o metodo a seguir retornara o nome e a 
	descricao da peca corrente e, recursivamente, de seus subcomponentes. */
	static void mostraSubpecas(Part peca, String espaco) throws RemoteException {
		if (peca != null) {
			System.out.print(espaco+"Nome: "+peca.getNome() +"; descricao: "+peca.getDescricao());
			String resp = (peca.ehPrimitiva())?"sim":"nao"; 
			System.out.print("; eh primitiva: "+resp+"\n"); 
			if (!peca.ehPrimitiva()) {
				Iterator<Part> componentes = peca.getComponentes().keySet().iterator(); 
				while(componentes.hasNext()) {
					mostraSubpecas(componentes.next(),espaco+"-"); 
				}
			}
		}
	}
	
	/* se o cliente estiver conectado a um servidor, a peca cujo identificador eh especificado eh excluida do repositorio
	correspondente */
	static void removePecaRepositorioCorrente (int id) throws RemoteException {
		if (estaConectado()) {
			repositorioCorrente.removePeca(id);
			System.out.println("A peca de identificador "+id +" foi removida com sucesso do repositorio corrente.");
		}
	}
	
	/* se o cliente estiver referenciando uma peca, a subpeca cujo identificador eh especificado eh excluida da lista de 
	subpecas da peca corrente */
	static void removeSubpecaPecaCorrente (int id) throws RemoteException {
		if(estaReferenciandoPeca()){
			pecaCorrente.removeSubComponente(id); 
			System.out.println("A subpeca de identificador "+id +" foi removida com sucesso da peca corrente.");
		}
	}
	
	static boolean estaConectado(){
		if( repositorioCorrente == null ){
			System.out.println ("O cliente nao esta conectado a nenhum servidor no momento.");
		}
		return repositorioCorrente != null;
	}
	
	static boolean estaReferenciandoPeca(){
		if( pecaCorrente == null ){
			System.out.println ("O cliente nao esta referenciado a nenhuma peca no momento.");
		}
		return pecaCorrente != null;
	}
	
	/* metodo principal da classe: ela apresenta um terminal ao usuario. Todos os comandos esperados utilizam as funcoes
	implementadas acima e tem seu funcionamento explicado pelo comando help. Qualquer informacao adicional seria 
	considerada repetitiva. */
	public static void main (String[] args) {
		System.out.println("Digite um dos comandos reconhecidos pelo sistema ou \'help\' para ajuda: ");
		System.out.println();
		Scanner input = new Scanner(System.in);
		String comando = null; 
		while (true) {
			try {
				String[] parametros = input.nextLine().split(" ");	
				comando = parametros[0];
				if (comando.equals("quit")) {
					break; 
				}
				else if (comando.equals("bind")) {
					String portaServidor = parametros[1];
					repositorioCorrente = (PartRepository) Naming.lookup("//localhost:" + portaServidor + "/PartRepositoryServer");
					System.out.println("O repositorio corrente foi alterado para o correspondente ao " + repositorioCorrente.getNomeServidor() + ".");
				}
				else if (comando.equals("listp")) {
					listaPecasRepositorio(); 
				}
				else if (comando.equals("getp")) {
					int indice = Integer.parseInt(parametros[1]);
					pecaCorrente = buscaPeca(indice); 
					if (pecaCorrente != null) { 
						System.out.println("A peca de id "+indice +" foi encontrada.");
						System.out.println("A peca de id "+indice +" eh a nova peca corrente.");
					}
					else {
						System.out.println("A peca de id "+indice +" nao foi encontrada.");
					}
				}
				else if (comando.equals("showp")) {
					mostraSubpecas(pecaCorrente,""); 
				}
				else if (comando.equals("clearlist")) { 
					listaSubpecasCorrente.clear();							
					System.out.println("lista de subpecas corrente limpa");
				}
				else if (comando.equals("addsubpart")) {
					int quantidade = Integer.parseInt(parametros[1]);
					listaSubpecasCorrente.put(pecaCorrente,quantidade);
					System.out.println("Foram adicionadas " +quantidade +" unidades da peca corrente a lista de subpecas corrente");
				}
				else if (comando.equals("addpa")) { 
					String descricao = ""; 
					for (int i = 2; i < parametros.length; i++) {
						descricao = descricao + parametros[i] +" "; 
					}
					pecaCorrente = insereNovaPecaAgregada(parametros[1],descricao,listaSubpecasCorrente);
					System.out.println("A peca foi inserida e eh a nova peca corrente");
				}
				else if (comando.equals("addpp")) { 
					String descricao = ""; 
					for (int i = 2; i < parametros.length; i++) {
						descricao = descricao + parametros[i] +" "; 
					}
					pecaCorrente = insereNovaPecaPrimitiva(parametros[1],descricao);
					System.out.println("A peca foi inserida e eh a nova peca corrente");
				}
				else if (comando.equals("help")) {
					System.out.println("Comandos aceitos pelo sistema:");
					System.out.println("- bind: faz o cliente se conectar a outro servidor e muda o repositorio");
					System.out.println("corrente. exemplo: bind [porta] ");
					System.out.println("  - [porta] = numero da porta a qual o servidor está escutando.");
					System.out.println("- listp: lista as pecas do repositorio corrente. Nao ha parametros.");
					System.out.println("- getp: busca uma peca por codigo. A busca eh efetuada no repositorio ");
					System.out.println("corrente. Se encontrada, a peca passa a ser a nova peca corrente.");
					System.out.println("Parametros: ");
					System.out.println("  - codigo da peca a ser buscada (numero inteiro maior ou igual a 1);");
					System.out.println("- showp: mostra atributos da peca corrente e seus subcomponentes (caso");
					System.out.println("eles existam). Nao ha parametros.");
					System.out.println("- clearlist: esvazia a lista de subpecas corrente. Nao ha parametros.");
					System.out.println("- addsubpart: adiciona a lista de subpecas corrente n unidades da peca.");
					System.out.println("Parametros: ");
					System.out.println("  - quantidade n de unidades da subpeca a ser inserida.");
					System.out.println("- addpa: adiciona uma peca agregada ao repositorio corrente. A lista de subpecas");
					System.out.println("corrente eh usada como lista de subcomponentes diretos da nova peca. Parametros: ");
					System.out.println("  - nome da peca");
					System.out.println("  - descricao da peca. A partir do segundo parametro, qualquer espaco sera considerado");
					System.out.println("  pertencente a descricao e nao como espaco \"separador\" de parametros.");
					System.out.println("- addpp: adiciona uma peca primitiva (que nao tem subcomponentes) ao repositorio corrente.");
					System.out.println("Parametros: ");
					System.out.println("  - nome da peca");
					System.out.println("  - descricao da peca. A partir do segundo parametro, qualquer espaco sera considerado");
					System.out.println("  pertencente a descricao e nao como espaco \"separador\" de parametros.");
					System.out.println("- rem: remove do repositorio corrente a peca cujo identificador eh especificado. Parametros: ");
					System.out.println("  - identificador da peca a ser removida.");
					System.out.println("- remsublist: remove a ultima peca da lista de subpecas corrente. Nao ha parametros.");
					System.out.println("- remsubpart: dentre os subcomponentes da peca corrente, sera removida a peca cujo identificador");
					System.out.println("eh especificado. Parametros: ");
					System.out.println("  - identificador da subpeca a ser excluida.");
					System.out.println("- quit: encerra a execucao do cliente. Nao ha parametros.");
				}
				else if (comando.equals("rem")) {
					removePecaRepositorioCorrente(Integer.parseInt(parametros[1]));
				}
				else if (comando.equals("remsublist")) {
					listaSubpecasCorrente.remove(listaSubpecasCorrente.size()-1);
					System.out.println("A ultima peca da lista de subpecas corrente foi excluida.");
				}
				else if (comando.equals("remsubpart")) {
					removeSubpecaPecaCorrente(Integer.parseInt(parametros[1]));
				}
				else { 
					System.out.println("Comando invalido");
				}
				System.out.println(); 
			}
			catch(IndexOutOfBoundsException i) {
				System.out.println("parametros invalidos\n");
			} 
			catch(ConnectException | NotBoundException e){
				//e.printStackTrace();
				if (comando.equals("bind")) {
					System.out.println("Nao ha nenhum servidor com esse nome.\n");
				}
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
	}
}
