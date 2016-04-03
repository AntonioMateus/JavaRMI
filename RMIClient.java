//Java RMI
import java.rmi.Naming; 
import java.rmi.RemoteException;
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
		if (repositorioCorrente==null) System.out.println("O programa cliente nao esta associado a nenhum servidor no momento");
		else {
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
		if (repositorioCorrente == null) return "O cliente nao esta conectado a nenhum servidor no momento";
		else return "nome: "+repositorioCorrente.getNomeServidor() +"; numero de pecas: "+repositorioCorrente.getNumeroPecas(); 
	}
	
	//metodo que busca uma peca no repositorio corrente, atraves de seu identificador
	static Part buscaPeca(int id) throws RemoteException {
		if (repositorioCorrente == null) {
			System.out.println("O cliente nao esta conectado a nenhum servidor no momento");
			return null;
		}		
		return repositorioCorrente.recuperaPeca(id);
	}
	
	//metodo que insere uma nova peca agregada (isto eh, que possui subcomponentes)
	static Part insereNovaPecaAgregada(String nome, String descricao, Map<Part,Integer> componentes) throws RemoteException {
		if (repositorioCorrente == null) {
			System.out.println("O cliente nao esta conectado a nenhum servidor no momento");
			return null;
		}
		return repositorioCorrente.inserePeca(nome, descricao, componentes);
	}
	
	//metodo que insere uma nova peca primitiva (ou seja, que nao possui subcomponentes)
	static Part insereNovaPecaPrimitiva(String nome, String descricao) throws RemoteException {
		if (repositorioCorrente == null) {
			System.out.println("O cliente nao esta conectado a nenhum servidor no momento");
			return null;
		}
		return repositorioCorrente.inserePeca(nome, descricao);
	}
	
	//metodo que retorna o nome e a desscricao da peca corrente, se esta for valida. 
	static String getCaracteristicasPecaCorrente() throws RemoteException {
		if (pecaCorrente != null) return "Nome: "+pecaCorrente.getNome() +"; descricao: "+pecaCorrente.getDescricao(); 
		else return "nao ha nada a declarar";
	}
	
	// se o cliente estiver conectado a algum servidor, seu nome sera retornado pelo metodo abaixo
	static String getNomeRepositorioCorrente() throws RemoteException {
		if (repositorioCorrente == null) return "O cliente nao esta conectado a nenhum servidor no momento";
		return repositorioCorrente.getNomeServidor(); 
	}
	
	// metodo que retorna se a peca corrente possui subcomponentes ou não
	static boolean pecaEhPrimitiva() throws RemoteException {
		if (pecaCorrente == null) {
			System.out.println("Nao ha nenhuma peca sendo referenciada no momento");
			return false; 
		}
		return (pecaCorrente.getComponentes().size()==0);
	}
	
	// metodo que retorna o numero de subcomponentes da peca corrente.
	static int numeroSubcomponentes() throws RemoteException {
		if (pecaCorrente == null) {
			System.out.println("Nao ha nenhuma peca sendo referenciada no momento");
			return 0; 
		}
		return pecaCorrente.getComponentes().size(); 
	}
	
	/* se o cliente estiver referenciado alguma peca, o metodo a seguir retornara o nome e a 
	descricao da peca corrente e, recursivamente, de seus subcomponentes. */
	static void mostraSubpecas(Part peca, String espaco) throws RemoteException {
		if (peca != null) {
			System.out.println(espaco+"Nome: "+peca.getNome() +"; descricao: "+peca.getDescricao());
			if (!peca.ehPrimitiva()) {
				Iterator<Part> componentes = peca.getComponentes().keySet().iterator(); 
				while(componentes.hasNext()) {
					mostraSubpecas(componentes.next(),espaco+"-"); 
				}
			}
		}
	}
	
	/* metodo principal da classe: ela apresenta um terminal ao usuario. Todos os comandos esperados utilizam as funcoes
	implementadas acima e tem seu funcionamento explicado pelo comando help. Qualquer informacao adicional seria 
	considerada repetitiva. */
	public static void main (String[] args) {
		try {
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
						int portaServidor = Integer.parseInt(parametros[1].split("or")[1])+1000;
						repositorioCorrente = (PartRepository) Naming.lookup("//localhost:"+portaServidor+"/PartRepositoryServer");
						System.out.println("O repositorio corrente foi alterado para o correspondente ao "+parametros[1]+".");
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
						System.out.println("corrente. Seu unico parametro eh o nome do \"servidor-alvo\".");
						System.out.println("Durante a implementacao desse sistema, definiu-se que o nome de um");
						System.out.println("servidor seria: Servidor%i, onde \'%i\' eh um numero inteiro.");
						System.out.println("- listp: lista as pecas do repositorio corrente.");
						System.out.println("- getp: busca uma peca por codigo (que eh um numero inteiro maior ou");
						System.out.println("igual a 1 e o unico parametro. A busca eh efetuada no repositorio ");
						System.out.println("corrente. Se encontrada, a peca passa a ser a nova peca corrente.");
						System.out.println("- showp: mostra atributos da peca corrente e seus subcomponentes, se");
						System.out.println("houver.");
						System.out.println("- clearlist: esvazia a lista de subpecas corrente.");
						System.out.println("- addsubpart: adiciona a lista de subpecas corrente n unidades da peca");
						System.out.println("corrente. O inteiro n eh passado por parametros.");
						System.out.println("- addp: adiciona uma peca ao repositorio corrente. A lista de subpecas");
						System.out.println("corrente eh usada como lista de subcomponentes diretos da nova peca.");
						System.out.println("- quit: encerra a execucao do cliente.");
					}
					else { 
						System.out.println("Comando invalido");
					}
					System.out.println(); 
				}
				catch(IndexOutOfBoundsException i) {
					System.out.println("parametros invalidos");
					System.out.println();
				}
			}
		}
		catch(Exception e) {
			e.printStackTrace(); 
		}
	}
}
