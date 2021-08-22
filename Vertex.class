package trabalho;
import java.util.HashMap;

public class Vertex implements Comparable<Vertex> {
    protected Integer id;
    protected HashMap<Integer, Vertex> nbhood; // outgoing neighbors
    protected Vertex parent, root;
    protected Integer dist, d, f;
    protected int size;

    //Construtor
    public Vertex(int id) {
        // id >= 1
        this.id = id;
        nbhood = new HashMap<Integer, Vertex>();
        parent = null;
        dist = d = null;
    }

    protected void reset() {
        parent = null;
        d = null;
        f = null;
        dist = null;
    }

    public void add_neighbor(Vertex viz) {
    	nbhood.put(viz.id, viz);
    }

    public int degree() {
        return nbhood.size();
    }

    public void discover(Vertex parent) {
        this.parent = parent;
        this.dist = parent.dist + 1;
    }

    protected Vertex get_root() {
        if (parent == null)
            root = this;
        else
            root = parent.get_root();
        return root;
    }

    public void print() {
        System.out.print("\n Id do vertice " + id + ", Vizinhança: ");
        for (Vertex v : nbhood.values())
            System.out.print(" " + v.id);
    }

    @Override
    public int compareTo(Vertex otherVertex) {
        if (otherVertex.size > this.size)
            return 1;
        else
            return -1;
    }
}