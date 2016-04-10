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
//Leitura do fluxo de entrada e saida
import java.util.Scanner; 
import java.util.NoSuchElementException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
//Classes de conexão com a rede
import java.net.MalformedURLException;

/*
* Classe RMIClient
* Comentarios: 
*/

public class RMIClient {
	static PrintWriter printWriter = null; //Caso o usuário opte pela saida ser escrita num arquivo, esse atributo não será nulo

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
					println("Código " +  peca.getCodigo() + "; Nome: "+peca.getNome() +"; descricao: "+peca.getDescricao());
				}
			}
			else {
				println("O repositorio esta vazio.");
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
		if (estaReferenciandoPeca()){
			return "Código " +  pecaCorrente.getCodigo() + "; Nome: "+pecaCorrente.getNome() +"; descricao: "+pecaCorrente.getDescricao(); 
		}
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
			print(espaco + "Código " +  peca.getCodigo() + "; NOME: " +peca.getNome() + "; DESCRICAO: " + peca.getDescricao() + ";");
			if(peca.ehPrimitiva()){
				print(" tipo: primitiva\n");
			} else{
				Iterator<Part> componentes = peca.getComponentes().keySet().iterator(); 
				while(componentes.hasNext()) {
					mostraSubpecas(componentes.next(), espaco + "-"); 
				}
			}
		}
	}
	
	/* se o cliente estiver conectado a um servidor, a peca cujo identificador eh especificado eh excluida do repositorio
	correspondente */
	static void removePecaRepositorioCorrente (int id) throws RemoteException {
		if (estaConectado()) {
			boolean removida = repositorioCorrente.removePeca(id);
			if(removida){
				println("A peca de identificador "+id +" foi removida com sucesso do repositorio corrente.");
			} else{
				println("Não ha nenhuma peca com o codigo " + id);
			}
		}
	}
	
	/* se o cliente estiver referenciando uma peca, a subpeca cujo identificador eh especificado eh excluida da lista de 
	subpecas da peca corrente */
	static void removeSubpecaPecaCorrente (int id) throws RemoteException {
		if(estaReferenciandoPeca()){
			pecaCorrente.removeSubComponente(id); 
			println("A subpeca de identificador "+id +" foi removida com sucesso da peca corrente.");
		}
	}
	
	static boolean estaConectado(){
		if( repositorioCorrente == null ){
			println ("O cliente nao esta conectado a nenhum servidor no momento.");
		}
		return repositorioCorrente != null;
	}
	
	static boolean estaReferenciandoPeca(){
		if( pecaCorrente == null ){
			println ("O cliente nao esta referenciado a nenhuma peca no momento.");
		}
		return pecaCorrente != null;
	}
	
	static void print(String linha){
		if(printWriter != null){
			printWriter.print(linha);
		} else{
			System.out.print(linha);
		}
	}
	
	static void println(String linha){
		if(printWriter != null){
			printWriter.println(linha);
		} else{
			System.out.println(linha);
		}
	}
	
	static void println(){
		if(printWriter != null){
			printWriter.println();
		} else{
			System.out.println();
		}
	}
	
	/* metodo principal da classe: ela apresenta um terminal ao usuario. Todos os comandos esperados utilizam as funcoes
	implementadas acima e tem seu funcionamento explicado pelo comando help. Qualquer informacao adicional seria 
	considerada repetitiva. */
	public static void main (String[] args) {
		String arquivoEntrada = null;
		if(args.length > 0){
			String arquivoSaida = args[0];
			try{
				FileWriter fileWriter = new FileWriter(arquivoSaida);
				printWriter = new PrintWriter(fileWriter);
			} catch(IOException ioe){
				System.out.println("O arquivo de saída " + arquivoSaida + " não pode ser aberto.");
			}
			
			if(args.length > 1){
				arquivoEntrada = args[1];
			}
		}
	
		System.out.println("Digite um dos comandos reconhecidos pelo sistema ou \'help\' para ajuda: ");
		Scanner input;
		if(arquivoEntrada == null){
			input = new Scanner(System.in);
		} else{
			try{
				input = new Scanner(new File(arquivoEntrada));
			} catch(IOException e){
				System.out.println("Não foi possível abrir o arquivo de entrada " + arquivoEntrada + ".");
				return;
			}
		}
		
		String comando = ""; 
		while (true) {
			try {
				if(arquivoEntrada == null){
					System.out.print("> ");
				}
				
				String linha = input.nextLine();
				if(printWriter != null){
					printWriter.println("> " + linha);
				}
				
				String[] parametros = linha.split(" ");
				comando = parametros[0];
				
				if(comando.length() > 0 && comando.charAt(0) != '#'){
					println();
				}
				
				if(comando.isEmpty()){
					//Comentario para pular linhas na saida
				}
				else if(comando.length() > 0 && comando.charAt(0) == '#'){
					//Comentario pular avaliação de qualquer comando
				}
				else if (comando.equals("quit")) {
					input.close();
					if(printWriter != null){
						printWriter.close();
					}
					System.exit(1);
				}
				else if (comando.equals("bind")) {
					String portaServidor = parametros[1];
					repositorioCorrente = (PartRepository) Naming.lookup("//localhost:" + portaServidor + "/PartRepositoryServer");
					println("O repositorio corrente foi alterado para o correspondente ao " + repositorioCorrente.getNomeServidor() + ".");
				}
				else if (comando.equals("listp")) {
					listaPecasRepositorio(); 
				}
				else if (comando.equals("getp")) {
					int indice = Integer.parseInt(parametros[1]);
					pecaCorrente = buscaPeca(indice); 
					if (pecaCorrente != null) { 
						println("A peca de id "+indice +" foi encontrada.");
						println("A peca de id "+indice +" eh a nova peca corrente.");
					}
					else {
						println("A peca de id "+indice +" nao foi encontrada.");
					}
				}
				else if (comando.equals("showp")) {
					if(estaReferenciandoPeca()){
						mostraSubpecas(pecaCorrente,"");
					}
				}
				else if (comando.equals("clearlist")) { 
					listaSubpecasCorrente.clear();							
					println("lista de subpecas corrente limpa");
				}
				else if (comando.equals("addsubpart")) {
					int quantidade = Integer.parseInt(parametros[1]);
					if (quantidade <= 0){
						println("Adicione uma quantidade válida (maior do que zero).");
					} else if(estaReferenciandoPeca() && quantidade > 0){
						listaSubpecasCorrente.put(pecaCorrente,quantidade);
						println("Foram adicionadas " +quantidade +" unidades da peca corrente a lista de subpecas corrente");
					}
				}
				else if (comando.equals("addpa")) { 
					String descricao = ""; 
					for (int i = 2; i < parametros.length; i++) {
						descricao = descricao + parametros[i] +" "; 
					}
					pecaCorrente = insereNovaPecaAgregada(parametros[1],descricao,listaSubpecasCorrente);
					if(pecaCorrente != null){
						println("A peca foi inserida e eh a nova peca corrente");
					}
				}
				else if (comando.equals("addpp")) { 
					String descricao = ""; 
					for (int i = 2; i < parametros.length; i++) {
						descricao = descricao + parametros[i] +" "; 
					}
					pecaCorrente = insereNovaPecaPrimitiva(parametros[1],descricao);
					if(pecaCorrente != null){
						println("A peca foi inserida e eh a nova peca corrente");
					}
				}
				else if (comando.equals("help")) {
					println("Comandos aceitos pelo sistema:");
					println("- bind: faz o cliente se conectar a outro servidor e muda o repositorio");
					println("corrente. exemplo: bind [porta] ");
					println("\t- [porta] = numero da porta a qual o servidor está escutando.");
					println("- listp: lista as pecas do repositorio corrente. Nao ha parametros.");
					println("- getp: busca uma peca por codigo. A busca eh efetuada no repositorio ");
					println("corrente. Se encontrada, a peca passa a ser a nova peca corrente.");
					println("Parametros: ");
					println("\t- codigo da peca a ser buscada (numero inteiro maior ou igual a 1);");
					println("- showp: mostra atributos da peca corrente e seus subcomponentes (caso");
					println("eles existam). Nao ha parametros.");
					println("- clearlist: esvazia a lista de subpecas corrente. Nao ha parametros.");
					println("- addsubpart: adiciona a lista de subpecas corrente n unidades da peca.");
					println("Parametros: ");
					println("\t- quantidade n de unidades da subpeca a ser inserida.");
					println("- addpa: adiciona uma peca agregada ao repositorio corrente. A lista de subpecas");
					println("corrente eh usada como lista de subcomponentes diretos da nova peca. Parametros: ");
					println("\t- nome da peca");
					println("\t- descricao da peca. A partir do segundo parametro, qualquer espaco sera considerado");
					println("\tpertencente a descricao e nao como espaco \"separador\" de parametros.");
					println("- addpp: adiciona uma peca primitiva (que nao tem subcomponentes) ao repositorio corrente.");
					println("Parametros: ");
					println("\t- nome da peca");
					println("\t- descricao da peca. A partir do segundo parametro, qualquer espaco sera considerado");
					println("\tpertencente a descricao e nao como espaco \"separador\" de parametros.");
					println("- rem: remove do repositorio corrente a peca cujo identificador eh especificado. Parametros: ");
					println("\t- identificador da peca a ser removida.");
					println("- remsublist: remove a ultima peca da lista de subpecas corrente. Nao ha parametros.");
					println("- remsubpart: dentre os subcomponentes da peca corrente, sera removida a peca cujo identificador");
					println("eh especificado. Parametros: ");
					println("\t- identificador da subpeca a ser excluida.");
					println("- quit: encerra a execucao do cliente. Nao ha parametros.");
				}
				else if (comando.equals("rem")) {
					removePecaRepositorioCorrente(Integer.parseInt(parametros[1]));
				}
				else if (comando.equals("remsublist")) {
					if(listaSubpecasCorrente.size() > 0){
						listaSubpecasCorrente.remove(listaSubpecasCorrente.size() - 1);
						println("A ultima peca da lista de subpecas corrente foi excluida.");
					} else{
						println("Não há peças na lista de subpecas corrente.");
					}
				}
				else if (comando.equals("remsubpart")) {
					removeSubpecaPecaCorrente(Integer.parseInt(parametros[1]));
				}
				else { 
					println("Comando invalido");
				}
				
				if(comando.length() > 0 && comando.charAt(0) != '#'){
					println();
				}
			}
			catch(IndexOutOfBoundsException i) {
				println("parametros invalidos\n");
			} 
			catch(ConnectException | NotBoundException | MalformedURLException e){
				//e.printStackTrace();
				if (comando.equals("bind")) {
					println("Nao ha nenhum servidor com esse nome.\n");
				}
			}
			catch (NoSuchElementException e) {
		        break;
		    } 
			catch(Exception e){
				e.printStackTrace();
			}
		}
	}
}
