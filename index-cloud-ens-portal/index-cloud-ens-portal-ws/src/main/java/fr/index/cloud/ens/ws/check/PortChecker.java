package fr.index.cloud.ens.ws.check;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.util.StringUtils;


public class PortChecker {

    private static final int MAX_PERCENTAGE = 20;
    private final String host;
    private final String remoteHost;
    private final String remotePort;
    private final Map<String, String> envVar;


    public PortChecker(Map<String, String> envVar, String host, String remoteHost, String remotePort) {
        super();
        this.host = host;
        this.remoteHost = remoteHost;
        this.remotePort = remotePort;
        this.envVar = envVar;
    }

    public String check() {

        try {
            /*
             * echo quit | timeout --signal=9 2 telnet cldens-web-d11 8080 > /tmp/supervise.html
             */
            List<String> commands = Arrays.asList("echo quit | timeout --signal=9 2 telnet " + remoteHost + " " + remotePort + " > /tmp/supervise.html");
            String response = new SSHConnection().run(envVar, host, commands);

            /*
             * Trying 192.168.207.132...
             * Connected to dev-fs-d01.dev.index.france.
             */


            Pattern pattern = Pattern.compile("Connected");

            Matcher m = pattern.matcher(response);

            if (!m.find()) {
                return "ko:\n"+response;
            }

            return ("ok");


        } catch (SSHException e) {
            return "ko:" + e.getMessage();
        }


    }

}
