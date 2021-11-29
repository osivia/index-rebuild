package fr.index.cloud.ens.ws.check;


import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;


public class SSHConnection {

    /**
     * JSch Example Tutorial
     * Java SSH Connection Program
     */
    public  String run(Map<String, String> envVar, String host, List<String> commands) throws SSHException {
        String user = "root";
        String password = envVar.get("supervisor.genericPassword");
        if( System.getenv("supervisor.genericPassword") != null)
            password = System.getenv("supervisor.genericPassword");
        
        StringBuffer res = new StringBuffer();

        try {

            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            JSch jsch = new JSch();
            Session session = jsch.getSession(user, host, 22);
            session.setPassword(password);
            session.setConfig(config);
            session.connect();

            
            ChannelShell channelShell = (ChannelShell) session.openChannel("shell");
            InputStream inputStream = channelShell.getInputStream();//The data arriving from the far end can be read from this stream.
            channelShell.setPty(true);
            channelShell.connect();
            
            
            OutputStream outputStream = channelShell.getOutputStream();
            
            
            PrintWriter printWriter = new PrintWriter(outputStream);
            printWriter.println("rm -f /tmp/supervise.html");
            for(String command : commands)
                printWriter.println(command);
            printWriter.println("echo -BEGIN- > /tmp/test2.html");
            printWriter.println("cat /tmp/supervise.html >> /tmp/test2.html");
            printWriter.println("echo $'\\n'-END- >> /tmp/test2.html");  
            printWriter.println("cat  /tmp/test2.html");  
            printWriter.println("exit");//To end this interaction
            printWriter.flush () ;// force the buffer data output
            
            
            
            byte[] tmp = new byte[1024];
            
            while (true) {
                while (inputStream.available() > 0) {
                    int i = inputStream.read(tmp, 0, 1024);
                    if (i < 0)
                        break;
                    res.append(new String(tmp, 0, i));
                }
                if (channelShell.isClosed()) {
                    res.append("--exit-status: " + channelShell.getExitStatus()+"\n");
                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (Exception ee) {

                }
            }


            channelShell.disconnect();
            session.disconnect();


        } catch (Exception e) {
               throw new SSHException(e.getMessage());
        }
        
        String toAnalyse = res.toString();
        int iBeg = toAnalyse.lastIndexOf("-BEGIN-");
        int iEnd = toAnalyse.lastIndexOf("-END-");
        
        if( iBeg > 0 && iEnd > 0) {
            toAnalyse = toAnalyse.substring(iBeg +9,iEnd -2);
        }   else
            throw new SSHException("/tmp/supervise.html cannot be parsed");
        
       
        return toAnalyse;
    }


}