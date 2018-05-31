import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Network {
    private String Name =null;
    private String bssid = null;
    private List<Node> nodes = new ArrayList<Node>();

    public Network(String bssid) {
        this.bssid = bssid;
    }

    public void addNode(Node node) {
        if (!contains(node))
            this.nodes.add(node);
    }

    public void setName(String name) {
        Name = name;
    }

    public boolean contains(Node node) {
        for ( Node k : nodes) {
            if (k.equals(node))
                return true;
        }
        return false;
    }

    public String getBssid() {
        return bssid;
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public String getName() {
        return Name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Network network = (Network) o;
        return Objects.equals(bssid, network.bssid);
    }

    @Override
    public int hashCode() {

        return Objects.hash(bssid);
    }
}
