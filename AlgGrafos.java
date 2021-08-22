package trabalho;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class AlgGrafos {

    public static void main(String[] args) throws Exception {
    	System.out.println("+-----------------------------------------------+");
    	System.out.println("|                     Grafo                     |");
    	System.out.println("+-----------------------------------------------+");
        Graph g1 = new Graph();
        Graph g2 = new Graph();
		String arq_ent = "myfiles/grafo01.txt"; //myfiles/grafo01.txt
		g1.open_text( arq_ent );
		if (g1.getSize() != 0) {
            g1.compact();
            g1.CFC();
            g1.print();
            if (g1.get_list_roots().size() > 1) {
                System.out.println(
                        " O grafo possui mais de uma componente fortemente conexa.Logo, n�o pode haver ciclo nem caminho euleriano");}
            else {
            	int contador = obterImparVertices(g1);
            	if (contador == 0) {
            		System.out.println(" O grafo possui ciclo eulerino");
            		printaResposta(getEulerCaminho(g1));
            	} else if (contador == 2) {
            		System.out.println(" N�o h� ciclo eulerinao, mas ha caminho euleriano");
            		printaResposta(getEulerCaminho(g1));
            	} else {
            		System.out.println(" N�o h� caminho nem ciclo euleriano nesse grafo");
            	}
        }
 } 
		System.out.print("\n");
		String line1 = "+-----------------------------------------------+\n";
		String line2 = "|                      Menu                     |\n";
		String line3 = "+-----------------------------------------------+\n";
        String line4 = "|0 Sair                                         |\n|1 Grafo padr�o                                 |\n";
        String line5 = "|2 Digite o endere�o de um outro grafo desejado |\n|3 Compactar - CFC - Euler - Print              |\n+-----------------------------------------------+\n Escolha a operacao: ";
        String menu = line1 + line2 + line3 + line4 + line5;
        Scanner scan1 = new Scanner(System.in);
        Scanner scan2 = new Scanner(System.in);
        int controle=0;
        while (controle==0) {
            System.out.printf(menu);
            int choice = scan1.nextInt();
            switch (choice) {
                case 0:
                	controle+=1;
                    scan1.close();
                    scan2.close();
                    break;
                case 1:
                	g2.add_vertex( 1 );
					g2.add_vertex( 2 );
					g2.add_vertex( 3 );
					g2.add_vertex( 4 );        
					g2.add_vertex( 5 );
					g2.add_vertex( 8 );
					g2.add_edge( 1, 2 );
					g2.add_edge( 1, 3 );
					g2.add_edge( 2, 1 );
					g2.add_edge( 2, 4 );
					g2.add_edge( 2, 5 );
					g2.add_edge( 3, 1 );
					g2.add_edge( 3, 5 );
					g2.add_edge( 5, 2 );
					System.out.println(" Grafo feito com sucesso");
                    break;
                case 2:
                	System.out.print(" Arquivo: ");
                    String arq_ent2 = scan2.nextLine();
                    g2.open_text(arq_ent2);
                    break;
                case 3:
                    if (g2.getSize() != 0) {
                        g2.compact();
                        g2.CFC();
                        g2.print();
                        if (g2.get_list_roots().size() > 1) {
                            System.out.println(
                                    " O grafo possui mais de uma componente fortemente conexa.Logo, n�o pode haver ciclo nem caminho euleriano");}
                        else {
                        	int contador = obterImparVertices(g2);
                        	if (contador == 0) {
                        		System.out.println(" O grafo possui ciclo eulerino");
                        		printaResposta(getEulerCaminho(g2));
                        	} else if (contador == 2) {
                        		System.out.println(" N�o h� ciclo eulerinao, mas ha caminho euleriano");
                        		printaResposta(getEulerCaminho(g2));
                        	} else {
                        		System.out.println(" N�o h� caminho nem ciclo euleriano nesse grafo");
                        	}
                    }
                        } else {
                        System.out.println(" Precisa primeiro ler o arquivo de entrada");
                    }
                    break;
            }
            System.out.print("\n");
        }         
    }
    
    // DFS adaptado para o algoritmo Fleury
    // Nessa vers�o, ser� necess�rio processar somente  o v�rtice corrente para encontrar a quantidade de vertices cobertos
    // Dado um vertice from, um processo recursivo � executado para cada vertice adjacente de forma a contar a quantidade de vertices cobertos
    public static int DFS(Set<Vertex> visitado, Vertex from) {
        int contador = 1;
        visitado.add(from);
        Vertex[] nos = from.nbhood.values().toArray(new Vertex[] {});
        for (Vertex to : nos) {
            if (!visitado.contains(to)) {
                contador = contador + DFS(visitado, to);}}
        return contador;
    }
    
    // Dados dois vertices from e to, a fun��o isBridge verifica se a visita da aresta(from, to) nao ira deixar o grafo disjunto
    // Essa verificao � necessaria para garantir as propriedades do caminho e ciclo euleriano
    /*
     * Inicialmente, � executada a DFS para determinar a quantidade de n�s que podem ser alcan�ados a partir do n� from n�o considerando a remo��o da aresta(from, to).
     * A aresta, ent�o, � removida e a DFS � executada para recalcular a quantidade de n�s que podem ser atingidos sem essa aresta
     * Se a remo��o da aresta reduz a cobertura dos v�rtices, essa aresta � uma ponte, separando o grafo e tornando uma das partes inacess�vel
     */
    public static boolean isBridge(Graph grafo, Vertex from, Vertex to) {
        if (from.nbhood.size() == 1) {
            return false;}
        int contagemPonte = DFS(new HashSet<Vertex>(), to);
        grafo.delEdge(from, to);
        int naoContagemPonte = DFS(new HashSet<Vertex>(), to);
        grafo.add_edge(from.id, to.id);
        return naoContagemPonte < contagemPonte;
    }

    //Retorna o n�mero de v�rtices impar
    public static int obterImparVertices(Graph grafo) {
        int contador = 0;
        for (Vertex no : grafo.getNos()) {
            if (no.degree() % 2 == 1) {
            	contador++;}
        }
        return contador;
    }

    //Se h� dois vertices de grau impar, retorna o primeiro vertice de grau impar para origem do caminho
    //Se todos os vertices forem par, � adicionado como origem o primeiro v�rtice (poderia ser qualquer vertice)
    public static Vertex getEulerPath(Graph grafo) {
        for (Vertex no : grafo.getNos()) {
            if (no.degree() % 2 == 1) {
                return no;
            }
        }
        throw new RuntimeException(" Error ao encontrar a origem do caminho de Euler");
    }
    
    // A funcao getEulerCaminho � um procedimento recursivo para encontrar um caminho/ciclo a partir de um no de origem
    // Dado um no de origem from, se uma dada aresta(from, to) n�o � definida como ponte ou � a �nica aresta para sair de from ent�o a mesma � removida do grafo e o registro de avanco para to � adicionado a lista caminho.
    // Dessa forma, � garantido que caminho reversos n�o ser�o feitos.
    public static void getEulerCaminho(Graph grafo, List<Vertex> caminho, Vertex from) {
        Vertex[] nos = from.nbhood.values().toArray(new Vertex[] {});
        for (Vertex to : nos) {
            if (!isBridge(grafo, from, to)) {
                caminho.add(to);
                grafo.delEdge(from, to);
                getEulerCaminho(grafo, caminho, to);
                break;}
        }
    }
 
    //A fun��o recebe um grafo e retorna o caminho ou o ciclo Euleriano se as condi��es foram cumpridas
    public static List<Vertex> getEulerCaminho(Graph grafo) throws Exception {
        List<Vertex> resposta = new ArrayList<Vertex>();
        int contador = obterImparVertices(grafo);
        if (contador == 0) {
        	resposta.add(grafo.getNos().get(0));
            getEulerCaminho(grafo, resposta, resposta.get(0));
        } else if (contador == 2) {
        	resposta.add(getEulerPath(grafo));
            getEulerCaminho(grafo, resposta, resposta.get(0));
        } else {
            throw new Exception(" As propriedades do Euler n�o foram cumpridas");
        }
        return resposta;
    }
    
    public static void printaResposta(List<Vertex> resposta) {
        StringBuilder rota = new StringBuilder();
        for (Vertex vertex : resposta) {
            rota.append(vertex.id).append("-");
        }
        rota.deleteCharAt(rota.length() - 1);
        System.out.println(" " + rota);
    }
    
}