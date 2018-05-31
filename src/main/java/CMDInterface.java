import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;

public class CMDInterface {

    String tField = "------------------------------------";
    String mField = "";
    String bField = "------------------------------------";
    String completeTxt = "";

    public CMDInterface() {
        completeTxt = tField + mField + bField;
    }

    public void update (String currentTxt) {
        clearAll();
        completeTxt = tField + mField + bField;
        System.out.print(completeTxt);
    }

    private boolean clearAll() {
        boolean success = false;



        return success;
    }

}
