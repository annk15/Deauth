import org.apache.commons.codec.binary.Hex;

import java.util.Arrays;
import java.util.Objects;

public class Node {
    private byte[] mac = null;
    private boolean selected = false;

    public Node (byte[] mac) {
        this.mac = mac;
    }

    public String getMacString() {
        return macToString(mac);
    }

    public byte[] getMac() {return this.mac; }

    public boolean getSelected() { return selected; }

    public void setSelected(boolean selected) { this.selected = selected; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return Arrays.equals(mac, node.mac);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(mac);
    }

    static String macToString(byte[] addr) {
        String toReturn;
        toReturn = Hex.encodeHexString(addr);
        StringBuilder stringBuilder = new StringBuilder(toReturn);
        for (int i = 0; i < 5; i++) {
            toReturn = stringBuilder.insert(2+3*i, ":").toString();
        }

        return toReturn;
    }
}
