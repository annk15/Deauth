import javax.imageio.IIOException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TcpDumper implements Runnable {

    public void run() {
        try {
            String[] cmdLineTCP = {"bash", "-c", "tcpdump -l -s0 -I -i en0 -e not type mgt subtype beacon"};
            ProcessBuilder pbTCP = new ProcessBuilder(cmdLineTCP);
            Process proc = pbTCP.start();
            BufferedReader inputTCP = new BufferedReader(new InputStreamReader(proc.getInputStream()));


            String tcpDump = null;
            List<Network> networks = new ArrayList<Network>();
            while ((tcpDump = inputTCP.readLine()) != null) {
                Network currentNet = null;
                currentNet = new Network(extractMac(tcpDump, "BSSID"));

                if ((currentNet.getBssid() != null) && !networks.contains(currentNet)) {
                    //currentNet.setName(netName);
                    networks.add(currentNet);
                }

                String da = extractMac(tcpDump, "DA");
                String sa = extractMac(tcpDump, "SA");

                //Add nodes
                if (da != null && sa != null) {
                    boolean daExist = networks.contains(new Network(da));
                    boolean saExist = networks.contains(new Network(sa));

                    //SLOW SEARCH!
                    for ( Network nw : networks) {
                        if (nw.getBssid().equals(da) && !saExist && !nw.contains(new Node(sa)))
                            nw.addNode(new Node(sa));
                        if (nw.getBssid().equals(sa) && !daExist && !nw.contains(new Node(da)))
                            nw.addNode(new Node(da));
                    }
                }

                /*
                System.out.println("-----------------------------------");
                for ( Network n : networks) {
                    /*if (n.getNodes().size() < 1)
                        break;

                    System.out.println("+ " + n.getBssid() + " " + n.getName());
                    for (Node nd : n.getNodes())
                        System.out.println("    - " + nd.getMac());
                }
                System.out.println("-----------------------------------");*/

            }
        } catch (IOException error) {
            System.out.println("Error");
        }
    }

    public String extractMac(String line, String tag) {
        return extract(line,String.format("\\W(%s)((?i)..:..:..:..:..:..(?-i))\\W*", tag));
    }

    public String extractWord(String line, String tag) {
        return extract(line,String.format("\\W(%s )((?i)[^\\s]*(?-i))\\W*", tag));
    }

    private String extract(String line, String crntPattern) {
        String extracted = null;
        Pattern pattern = Pattern.compile(crntPattern);
        Matcher match = pattern.matcher(line);

        if (match.find()) {
            extracted = match.group(0);
        }

        return extracted;
    }

    public void test () {
        System.out.println("HEJ");
    }
}
