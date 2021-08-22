package trabalho;

import java.util.HashMap;
import java.util.List;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class Graph {
	private HashMap<Integer, Vertex> vertices;
	private int time;

	//Construtor
	public Graph() {
		vertices = new HashMap<Integer, Vertex>();
	}

	public void add_vertex(int id) {
		if (id < 1 || this.vertices.get(id) == null) {
			Vertex v = new Vertex(id);
			vertices.put(v.id, v);
			reset();
		}
	}
	
	// Função que incrementa uma aresta
	public void add_edge(Integer id1, Integer id2) {
		Vertex v1 = vertices.get(id1);
		Vertex v2 = vertices.get(id2);
		if (v1 == null || v2 == null) {
			System.out.printf(" Vértice inexistente!");
			return;
		}
		v1.add_neighbor(v2);
		v2.add_neighbor(v1);
		reset();
	}

	// Função que deleta uma aresta
	public void delEdge(Vertex i, Vertex j) {
		if (vertices.containsKey(i.id)) {
			vertices.get(i.id).nbhood.remove(j.id);
			vertices.get(j.id).nbhood.remove(i.id);
		}
	}

	// Função que retorna uma lista de vizinhos
	public List<Vertex> getAdjacent(int i) {
		if (vertices.containsKey(i)) {
			return new ArrayList<Vertex>(Arrays.asList(vertices.get(i).nbhood.values().toArray(new Vertex[] {})));
		} else {
			return new ArrayList<Vertex>();
		}
	}

	// Função que retorna uma lista de vértices
	public List<Vertex> getNos() {
		return new ArrayList<Vertex>(Arrays.asList(vertices.values().toArray(new Vertex[] {})));
	}

	// Função que retorna o tamanho do HashMap vertices
	public int getSize() {
		return vertices.size();
	}

	//Leitura do arquivo 
	public void open_text(String arq_ent) {
		String thisLine = null;
		vertices = new HashMap<Integer, Vertex>();
		String pieces[];

		try {
			FileReader file_in = new FileReader(arq_ent);
			BufferedReader br1 = new BufferedReader(file_in);
			while ((thisLine = br1.readLine()) != null) {
				// retira excessos de espaços em branco
				thisLine = thisLine.replaceAll("\\s+", " ");
				pieces = thisLine.split(" ");
				int v1 = Integer.parseInt(pieces[0]);
				this.add_vertex(v1);
				for (int i = 2; i < pieces.length; i++) {
					int v2 = Integer.parseInt(pieces[i]);
					// pode ser a primeira ocorrencia do v2
					this.add_vertex(v2);
					this.add_edge(v1, v2);
				}
			}
			br1.close();
			System.out.println(" Arquivo lido com sucesso.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Essa funcao compacta o grafo. Por exemplo: vertices 1 2 3 4 6 vira 1 2 3 4 5
	public void compact() {
		int n = vertices.size();
		// ids utilizados de 1 a n
		int[] small = new int[n + 1];
		Vertex[] big = new Vertex[n];

		int qbig = 0;
		for (Vertex v1 : vertices.values()) {
			if (v1.id <= n)
				small[v1.id] = 1;
			else
				big[qbig++] = v1;
		}

		int i = 1;
		for (int pairs = 0; pairs < qbig; i++) {
			if (small[i] == 0)
				small[pairs++] = i;
		}

		for (i = 0; i < qbig; i++) {
			int old_id = big[i].id;
			big[i].id = small[i];

			vertices.remove(old_id);
			vertices.put(big[i].id, big[i]);

			for (Vertex v1 : vertices.values()) {
				if (v1.nbhood.get(old_id) != null) {
					v1.nbhood.remove(old_id);
					v1.nbhood.put(big[i].id, big[i]);
				}
			}
		}
	}

	private void reset() {
		time = 0;
		for (Vertex v1 : vertices.values())
			v1.reset();
	}

	// Busca em profundidade usada na funcao CFS, a qual e usada para descobrir se o grafo é fortemente conexo
	public void DFS(List<Vertex> ordering) {
		reset();
		if (ordering == null) {
			ordering = new ArrayList<Vertex>();
			ordering.addAll(vertices.values());
		}

		for (Vertex v1 : ordering)
			if (v1.d == null)
				DFS_visit(v1);
	}

	private void DFS_visit(Vertex v1) {
		v1.d = ++time;
		for (Vertex neig : v1.nbhood.values()) {
			if (neig.d == null) {
				neig.parent = v1;
				DFS_visit(neig);
			} else if (neig.d < v1.d && neig.f == null){
				//acyclic = false;
			}
		}
		v1.f = ++time;
	}

	public Graph reverse() {
		Graph d2 = new Graph();
		for (Vertex v11 : this.vertices.values()) {
			d2.add_vertex(v11.id);
		}
		for (Vertex v11 : this.vertices.values()) {
			for (Vertex v12 : v11.nbhood.values()) {
				Vertex v21 = d2.vertices.get(v11.id);
				Vertex v22 = d2.vertices.get(v12.id);
				v22.add_neighbor(v21);
			}
		}
		return d2;
	}

	// Funcao que retorna a lista de rotas do grafo
	public List<Vertex> get_list_roots() {
		List<Vertex> list_roots = new ArrayList<Vertex>();
		for (Vertex v1 : vertices.values()) {
			if (v1.parent == null)
				list_roots.add(v1);
		}
		return list_roots;
	}

	// Funcao que verifica as rotas
	public void CFC() {
		this.DFS(null);
		Graph d2 = this.reverse();

		List<Vertex> decreasing_f2 = new ArrayList<Vertex>();
		for (Vertex v1 : this.vertices.values()) {
			Vertex v2 = d2.vertices.get(v1.id);
			v2.size = v1.f;
			decreasing_f2.add(v2);
		}
		Collections.sort(decreasing_f2);

		d2.DFS(decreasing_f2);

		this.reset();
		for (Vertex v21 : d2.vertices.values()) {
			Vertex v11 = this.vertices.get(v21.id);
			if (v21.parent != null) {
				Vertex v12 = this.vertices.get(v21.parent.id);
				v11.parent = v12;
			}
		}
	}

	public void print() {
		for (Vertex v : vertices.values())
			v.print();
		// CFC
		System.out.print("\n Componentes fortemente conexas: ");
		for (Vertex v1 : vertices.values())
			v1.root = v1.get_root();
		List<Vertex> list_roots = get_list_roots();
		for (Vertex v1 : list_roots) {
			System.out.print("\n CFC: ");
			for (Vertex v2 : vertices.values()) {
				if (v2.root == v1)
					System.out.print("  " + v2.id);
			}
		}
		System.out.print("\n");

	}
}
