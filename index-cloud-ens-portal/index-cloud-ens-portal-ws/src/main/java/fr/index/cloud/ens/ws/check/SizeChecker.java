package fr.index.cloud.ens.ws.check;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SizeChecker {

    private static final int MAX_PERCENTAGE = 60;
    private final String host;
    private final Map<String, String> envVar;    
    private static Pattern pattern = Pattern.compile("[ *]([0-9]*)(%)");


    public SizeChecker(Map<String, String> envVar,String host) {
        super();
        this.host = host;
        this.envVar = envVar;
    }

    public String check() {

        try {
            List<String> commands = Arrays.asList("cd /data/mnt-exports/exports", "df > /tmp/supervise.html");
            String response = new SSHConnection().run(envVar, host, commands);

/*
            Filesystem                            1K-blocks     Used Available Use% Mounted on
            devtmpfs                                8121396        0   8121396   0% /dev
            tmpfs                                   8133228        0   8133228   0% /dev/shm
            tmpfs                                   8133228    33688   8099540   1% /run
            tmpfs                                   8133228        0   8133228   0% /sys/fs/cgroup
            /dev/mapper/rhel-root                  36805060  4256060  32549000  12% /
            /dev/sda1                               1038336   201080    837256  20% /boot
*/              

            Matcher m = pattern.matcher(response);

            while (m.find()) {
                if (m.groupCount() == 2) {
                    if (Integer.parseInt(m.group(1)) > MAX_PERCENTAGE) {
                        return "ko:\n" + response;
                    }
                }
            }

            return ("ok");


        } catch (SSHException e) {
            return "ko:" + e.getMessage();
        }


    }

}
