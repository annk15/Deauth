import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Network {
    private String name = null;
    private byte[] bssid = null;
    private List<Node> nodes = new ArrayList<Node>();
    private boolean selected = false;

    Network(String name) {
        this.name = name;
    }

    public void addNode(Node node) {
        if (!contains(node))
            this.nodes.add(node);
    }

    public void setName(String name) {
        name = name;
    }

    public boolean contains(Node node) {
        for ( Node k : nodes) {
            if (k.equals(node))
                return true;
        }
        return false;
    }

    public String getBssidString() {
        return Node.macToString(bssid);
    }
    public byte[] getBssid() { return this.bssid; }
    public void setBssid(byte[] bssid) { this.bssid = bssid; }

    public List<Node> getNodes() {
        return nodes;
    }

    public String getName() {
        return name;
    }

    public boolean getSelected() { return selected; }
    public void setSelected(boolean selected) { this.selected = selected; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Network network = (Network) o;
        return Objects.equals(name, network.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
