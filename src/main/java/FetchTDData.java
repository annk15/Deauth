import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class FetchTDData implements Runnable {

    String[] cmdLineTCP = null;
    List<String> toFill = null;
    volatile boolean running = true;

    public FetchTDData (String[] cmnd, List<String> lines) {
        this.cmdLineTCP = cmnd;
        this.toFill = lines;
    }

    @Override
    public void run() {
        try {
            ProcessBuilder pbTCP = new ProcessBuilder(cmdLineTCP);
            Process proc = pbTCP.start();
            BufferedReader inputTCP = new BufferedReader(new InputStreamReader(proc.getInputStream()));

            String tcpDump = null;
            List<Network> networks = new ArrayList<Network>();
            while ((tcpDump = inputTCP.readLine()) != null && (running == true)) {
                toFill.add(tcpDump);
            }
        } catch (IOException exc) {
            exc.printStackTrace();
        }
    }

    public void killMe() {
        running = false;
    }
}
