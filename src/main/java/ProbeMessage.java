
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProbeMessage {

    private String BSSID = null;
    private String DA = null;
    private String SA = null;



    //Also need to ovveride hashvalue if ever used
    @Override
    public boolean equals (Object obj) {
        return this.getBSSID().equals (((ProbeMessage)obj).getBSSID()) &&
                this.getSA().equals (((ProbeMessage)obj).getSA()) &&
                this.getDA().equals (((ProbeMessage)obj).getDA());
    }

    @Override
    public int hashCode() {
        return Objects.hash(BSSID, DA, SA);
    }

    public boolean isVaild () {
        boolean isVaild = false;
        if (BSSID != null && DA != null && SA != null)
            isVaild = true;

        return isVaild;
    }

    public String getBSSID() {
        return BSSID;
    }

    public String getDA() {
        return DA;
    }

    public String getSA() {
        return SA;
    }
}
