// https://github.com/mgodave/Jpcap/tree/master/sample

import java.io.*;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Main {

    public static void main(String[] args) throws IOException, InterruptedException {

        String[] cmndBeacon = {"bash", "-c", "tcpdump -l -s0 -I -i en0 -e type mgt subtype beacon"};
        List beaconLines = Collections.synchronizedList(new ArrayList<String>());
        FetchTDData fetchBSSID = new FetchTDData(cmndBeacon, beaconLines);
        (new Thread(fetchBSSID)).start();

        String[] cmndManagment = {"bash", "-c", "tcpdump -l -s0 -I -i en0 -e not type mgt subtype beacon"};
        List managmentLines = Collections.synchronizedList(new ArrayList<String>());
        FetchTDData fetchManagment = new FetchTDData(cmndManagment, managmentLines);
        (new Thread(fetchManagment)).start();

        System.out.println("Scanning networks (Please Wait)");
        Thread.sleep(5000);
        fetchManagment.killMe();
        fetchBSSID.killMe();

        List<Network> networks = new ArrayList<Network>();
        for (Object line : beaconLines) {
            Network currentNet = null;
            currentNet = new Network(extractMac((String) line, "BSSID"));
            currentNet.setName(extractBeacon((String) line));
            if ((currentNet.getBssid() != null) && (currentNet.getName() != null) && !networks.contains(currentNet)) {
                //currentNet.setName(netName);
                networks.add(currentNet);
                //System.out.println(currentNet.getBssid() + " " + currentNet.getName());
            }
        }

        for (Object line : managmentLines) {
            String da = extractMac(((String)line), "DA");
            String sa = extractMac(((String)line), "SA");

            //Add nodes
            if (da != null && sa != null) {
                boolean daExist = networks.contains(new Network(da));
                boolean saExist = networks.contains(new Network(sa));

                //SLOW SEARCH!
                for (Network nw : networks) {
                    if (nw.getBssid().equals(da) && !saExist && !nw.contains(new Node(sa)))
                        nw.addNode(new Node(sa));
                    if (nw.getBssid().equals(sa) && !daExist && !nw.contains(new Node(da)))
                        nw.addNode(new Node(da));
                }
            }
        }

        for (Network nw : networks) {
            System.out.println("+ " + nw.getBssid()  + " (" + nw.getName() + ")");
            for (Node node : nw.getNodes()) {
                System.out.println("    - " + node.getMac());
            }
        }

        /*
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

    static public String extractMac(String line, String tag) {
        return extract(line,String.format("\\W(%s:)((?i)..:..:..:..:..:..(?-i))\\W*", tag));
    }

    static public String extractBeacon(String line) {
        return extract(line,String.format("\\W(Beacon )\\(((?i).*(?-i))\\)\\W*"));
    }

    static private String extract(String line, String crntPattern) {
        String extracted = null;
        Pattern pattern = Pattern.compile(crntPattern);
        Matcher match = pattern.matcher(line);

        if (match.find()) {
            extracted = match.group(2);
        }

        return extracted;
    }
}