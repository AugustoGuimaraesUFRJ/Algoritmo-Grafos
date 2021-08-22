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
                        " O grafo possui mais de uma componente fortemente conexa.Logo, não pode haver ciclo nem caminho euleriano");}
            else {
            	int contador = obterImparVertices(g1);
            	if (contador == 0) {
            		System.out.println(" O grafo possui ciclo eulerino");
            		printaResposta(getEulerCaminho(g1));
            	} else if (contador == 2) {
            		System.out.println(" Não há ciclo eulerinao, mas ha caminho euleriano");
            		printaResposta(getEulerCaminho(g1));
            	} else {
            		System.out.println(" Não há caminho nem ciclo euleriano nesse grafo");
            	}
        }
 } 
		System.out.print("\n");
		String line1 = "+-----------------------------------------------+\n";
		String line2 = "|                      Menu                     |\n";
		String line3 = "+-----------------------------------------------+\n";
        String line4 = "|0 Sair                                         |\n|1 Grafo padrão                                 |\n";
        String line5 = "|2 Digite o endereço de um outro grafo desejado |\n|3 Compactar - CFC - Euler - Print              |\n+-----------------------------------------------+\n Escolha a operacao: ";
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
                                    " O grafo possui mais de uma componente fortemente conexa.Logo, não pode haver ciclo nem caminho euleriano");}
                        else {
                        	int contador = obterImparVertices(g2);
                        	if (contador == 0) {
                        		System.out.println(" O grafo possui ciclo eulerino");
                        		printaResposta(getEulerCaminho(g2));
                        	} else if (contador == 2) {
                        		System.out.println(" Não há ciclo eulerinao, mas ha caminho euleriano");
                        		printaResposta(getEulerCaminho(g2));
                        	} else {
                        		System.out.println(" Não há caminho nem ciclo euleriano nesse grafo");
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
    // Nessa versão, será necessário processar somente  o vértice corrente para encontrar a quantidade de vertices cobertos
    // Dado um vertice from, um processo recursivo é executado para cada vertice adjacente de forma a contar a quantidade de vertices cobertos
    public static int DFS(Set<Vertex> visitado, Vertex from) {
        int contador = 1;
        visitado.add(from);
        Vertex[] nos = from.nbhood.values().toArray(new Vertex[] {});
        for (Vertex to : nos) {
            if (!visitado.contains(to)) {
                contador = contador + DFS(visitado, to);}}
        return contador;
    }
    
    // Dados dois vertices from e to, a função isBridge verifica se a visita da aresta(from, to) nao ira deixar o grafo disjunto
    // Essa verificao é necessaria para garantir as propriedades do caminho e ciclo euleriano
    /*
     * Inicialmente, é executada a DFS para determinar a quantidade de nós que podem ser alcançados a partir do nó from não considerando a remoção da aresta(from, to).
     * A aresta, então, é removida e a DFS é executada para recalcular a quantidade de nós que podem ser atingidos sem essa aresta
     * Se a remoção da aresta reduz a cobertura dos vértices, essa aresta é uma ponte, separando o grafo e tornando uma das partes inacessível
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

    //Retorna o número de vértices impar
    public static int obterImparVertices(Graph grafo) {
        int contador = 0;
        for (Vertex no : grafo.getNos()) {
            if (no.degree() % 2 == 1) {
            	contador++;}
        }
        return contador;
    }

    //Se há dois vertices de grau impar, retorna o primeiro vertice de grau impar para origem do caminho
    //Se todos os vertices forem par, é adicionado como origem o primeiro vértice (poderia ser qualquer vertice)
    public static Vertex getEulerPath(Graph grafo) {
        for (Vertex no : grafo.getNos()) {
            if (no.degree() % 2 == 1) {
                return no;
            }
        }
        throw new RuntimeException(" Error ao encontrar a origem do caminho de Euler");
    }
    
    // A funcao getEulerCaminho é um procedimento recursivo para encontrar um caminho/ciclo a partir de um no de origem
    // Dado um no de origem from, se uma dada aresta(from, to) não é definida como ponte ou é a única aresta para sair de from então a mesma é removida do grafo e o registro de avanco para to é adicionado a lista caminho.
    // Dessa forma, é garantido que caminho reversos não serão feitos.
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
 
    //A função recebe um grafo e retorna o caminho ou o ciclo Euleriano se as condições foram cumpridas
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
            throw new Exception(" As propriedades do Euler não foram cumpridas");
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