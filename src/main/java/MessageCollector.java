import org.apache.commons.codec.binary.Hex;
import org.omg.Messaging.SYNC_WITH_TRANSPORT;
import org.pcap4j.core.*;
import org.pcap4j.packet.Packet;

import java.io.EOFException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

//NOT THREAD SAFE!!!!!!

public class MessageCollector implements Runnable {

    volatile List<Network> networks = new ArrayList<Network>();

    /*MessageCollector (List<Network> foundAddresses) {
        this.networks = foundAddresses;
    }*/

    @Override
    public void run() {
        PcapHandle.Builder bhndl = new PcapHandle.Builder("en0");
        bhndl.rfmon(true);
        bhndl.snaplen(65536);
        bhndl.timeoutMillis(50);

        try {
            PcapHandle hndl = bhndl.build();
            hndl.setFilter("type mgt subtype probe-req or type mgt subtype probe-resp", BpfProgram.BpfCompileMode.OPTIMIZE);

            final PacketListener listener = new PacketListener() {
                @Override
                public void gotPacket(Packet packet) {

                    // Override the default gotPacket() function and process packet
                    // System.out.println(hndl.getTimestamp());
                    byte[] packetData = packet.getRawData();
                    //System.out.println(packet);

                    byte[] bb = Arrays.copyOfRange(packetData,0x19,packetData.length-4);
                    Checksum checksum = new CRC32();
                    checksum.update(bb,0,bb.length);

                    byte[] crc = {packetData[packetData.length-1],packetData[packetData.length-2],packetData[packetData.length-3],packetData[packetData.length-4]};

                    if (Long.toHexString(checksum.getValue()).toUpperCase().equals(Hex.encodeHexString(crc).toUpperCase())) {

                        //Probe Req
                        if (packetData[0x19] == 0x40) {

                            //System.out.println(name + " " + packetData[0x32]);
                            //System.out.println(packet);


                            Network ap = new Network(extractSSID(0x32, packetData));
                            Node node = new Node(new byte[]{packetData[0x23], packetData[0x24], packetData[0x25], packetData[0x26], packetData[0x27], packetData[0x28]});

                            //System.out.println("Found!!");
                            //System.out.println(name + " : " + node.getMacString());

                            boolean match = false;
                            for (Network nw : networks) {
                                if (nw.equals(ap)) {
                                    match = true;
                                    if (!nw.contains(node)) {
                                        nw.addNode(node);

                                        //System.out.println("A: " + ap.getBssidString());
                                        //System.out.println("N: " + node.getMacString());
                                        //System.out.println(packet);
                                    }
                                }
                            }

                            if (!match) {
                                //System.out.println("------ ADD NEW!!!!");
                                ap.addNode(node);
                                networks.add(ap);
                            }
                        }

                        //Probe resp
                        else if (packetData[0x19] == 0x50) {

                            String name = extractSSID(0x3e, packetData);

                            for (Network nw : networks) {
                                if (nw.getName().equals(name)) {
                                    nw.setBssid(new byte[] {packetData[0x23], packetData[0x24], packetData[0x25], packetData[0x26], packetData[0x27], packetData[0x28]});
                                }
                            }

                        }
                    }

                }
            };

            hndl.loop(0, listener);


        } catch (PcapNativeException e) {
            e.printStackTrace();
        } catch (NotOpenException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            //e.printStackTrace();
            System.out.println("ERROR");
        }
    }

    public List<Network> getNetworks() {
        System.out.println(networks);
        return networks;
    }

    private String extractSSID(int start, byte[] packet) {
        String name = "";
        if (packet[start] != 0) {
            for (int i = 0; i < packet[start]; i++) {
                name += (char) packet[(start+1) + i];
            }
        }
            return name;
    }
}
