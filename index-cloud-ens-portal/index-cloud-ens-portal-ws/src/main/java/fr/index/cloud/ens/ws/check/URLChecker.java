package fr.index.cloud.ens.ws.check;

import java.util.Arrays;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class URLChecker {
    
    private final String host;
    private final String command;
    private final String regexp;
    private final Map<String, String> envVar;
    

    
    public URLChecker(Map<String, String> envVar, String host, String command, String regexp) {
        super();
        this.envVar = envVar;
        this.host = host;
        this.command = command;
        this.regexp = regexp;
    }
    
    public String check()  {
        try {
            String response = new SSHConnection().run( envVar, host,  Arrays.asList(command));
/*            
            <cas:serviceResponse xmlns:cas="http://www.yale.edu/tp/cas"><cas:authenticationFailure code="INVALID_REQUEST">Le paramètre "service" est obligatoire</cas:authenticationFailure></cas:serviceResponse>
*/
            if( regexp != null) {
                Pattern p = Pattern.compile(regexp);
                Matcher m = p.matcher(response);
                if( m.matches()) {
                    return "ok";
                }
                else    {
                    return "ko:\n"+response;
                }
            }
            
            // no regexp
            return "ok";
        } catch(SSHException e)    {
            return "ko:"+ e.getMessage();
        }
        

    }

}
