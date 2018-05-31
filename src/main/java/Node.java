import java.util.Objects;

public class Node {
    private String mac = null;

    public Node (String mac) {
        this.mac = mac;
    }

    public String getMac() {
        return mac;
    }

    @Override
    public boolean equals(Object obj) {
        return mac.equals(((Node)obj).getMac());
    }
}
