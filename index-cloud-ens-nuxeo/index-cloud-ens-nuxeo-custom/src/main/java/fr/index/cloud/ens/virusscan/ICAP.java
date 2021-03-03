package fr.index.cloud.ens.virusscan;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.Callable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;

class ICAP implements Callable<ICAPResult> {
    
    private static final int SOCKET_TIMEOUT = 2000;
    private static final Charset StandardCharsetsUTF8 = Charset.forName("UTF-8");
    private static final int BUFFER_SIZE = 32 * 1024;
    private static final int STD_RECEIVE_LENGTH = 8192;
    private static final int STD_SEND_LENGTH = 8192;
    private static final String VERSION = "1.0";
    private static final String USERAGENT = "IT-Kartellet ICAP Client/1.1";
    private static final String ICAPTERMINATOR = "\r\n\r\n";
    private static final String HTTPTERMINATOR = "0\r\n\r\n";

    private String serverIP;
    private int port;

    private Socket client;
    private DataOutputStream out;
    private DataInputStream in;

    private String icapService;

    private int stdPreviewSize;

    private String tempString;
    private String originalFilename;


    private InputStream fileInStream;
    private long fileSize;

    /** Log. */
    private static final Log log = LogFactory.getLog(ICAP.class);


    /**
     * Default constructor
     * 
     * @param serverIP The IP address to connect to.
     * @param port The port in the host to use.
     * @param icapService The service to use (fx "avscan").
     * @throws IOException
     * @throws ICAPException
     */
    public ICAP(String serverIP, int port, String icapService, String originalFilename, InputStream fileInStream, long fileSize) throws IOException, ICAPException {
        this.icapService = icapService;
        this.serverIP = serverIP;
        this.port = port;
        this.originalFilename = originalFilename;

        this.fileInStream = fileInStream;
        this.fileSize = fileSize;

    }


    /**
     * Initializes the socket connection and IO streams. It asks the server for the available options and
     * changes settings to match it.
     * 
     * @throws UnknownHostException
     * @throws IOException
     * @throws ICAPException
     */
    private void initConnection() throws UnknownHostException, IOException, ICAPException {

        if (log.isDebugEnabled())
            log.debug("initConnection" );     
        
        Socket client = new Socket();
        client.connect(new InetSocketAddress(serverIP, port), SOCKET_TIMEOUT);


        // Openening out stream
        OutputStream outToServer = client.getOutputStream();
        out = new DataOutputStream(new BufferedOutputStream(outToServer, BUFFER_SIZE));

        // Openening in stream
        InputStream inFromServer = client.getInputStream();
        in = new DataInputStream(inFromServer);

        String parseMe = getOptions();
        
        if (log.isDebugEnabled())
            log.debug("ICAP options = " +parseMe);     
        
        
        Map<String, String> responseMap = parseHeader(parseMe);
        

        if (responseMap.get("StatusCode") != null) {
            int status = Integer.parseInt(responseMap.get("StatusCode"));
            
            if (log.isDebugEnabled())
                log.debug("ICAP status = " +status);            

            switch (status) {
                case 200:
                    tempString = responseMap.get("Preview");
                    if (tempString != null) {
                        stdPreviewSize = Integer.parseInt(tempString);
                    }

                    break;
                default:
                    throw new ICAPException("Could not get preview size from server");
            }
        } else {
            throw new ICAPException("Could not get options from server");
        }
    }


    @Override
    public ICAPResult call() throws ICAPException, UnknownHostException, IOException {
        try {
            initConnection();
            
            
            if (log.isDebugEnabled())
                log.debug("call");     

            // First part of header
            String resHeader = "GET /" + originalFilename + " HTTP/1.1\r\nHost: " + serverIP + ":" + port + "\r\n\r\n";
            String resBody = resHeader + "HTTP/1.1 200 OK\r\nTransfer-Encoding: chunked\r\nContent-Length: " + fileSize + "\r\n\r\n";

            int previewSize = (int) fileSize;
            
            
            if (stdPreviewSize > 0 && previewSize > stdPreviewSize) {
                previewSize = stdPreviewSize;
            }

            String requestBuffer = "RESPMOD icap://" + serverIP + "/" + icapService + " ICAP/" + VERSION + "\r\n" + "Host: " + serverIP + "\r\n"
                    + "Connection:  close\r\n" + "User-Agent: " + USERAGENT + "\r\n" + "Allow: 204\r\n" + "Preview: " + previewSize + "\r\n"
                    + "Encapsulated: req-hdr=0, res-hdr=" + resHeader.length() + ", res-body=" + resBody.length() + "\r\n" + "\r\n" + resBody
                    + Integer.toHexString(previewSize) + "\r\n";

            sendString(requestBuffer);
            
            
            if (log.isDebugEnabled())
                log.debug("requestBuffer sent : " + new String(requestBuffer));     

            // Sending preview or, if smaller than previewSize, the whole file.
            byte[] chunk = new byte[previewSize];

            fileInStream.read(chunk);
            sendBytes(chunk);
            sendString("\r\n");
            if (fileSize <= previewSize) {
                sendString("0; ieof\r\n\r\n", true);
            } else if (previewSize != 0) {
                sendString("0\r\n\r\n", true);
            }
            
            
            if (log.isDebugEnabled())
                log.debug("preview sent");     

            // Parse the response! It might not be "100 continue"
            // if fileSize<previewSize, then this is acutally the respond
            // otherwise it is a "go" for the rest of the file.
            Map<String, String> responseMap = new HashMap<String, String>();
            int status;

            if (fileSize >= stdPreviewSize) {
                String parseMe = getHeader(ICAPTERMINATOR);
                
                
                if (log.isDebugEnabled())
                    log.debug("ICAP response(1) = " +parseMe);   
                
                responseMap = parseHeader(parseMe);

                tempString = responseMap.get("StatusCode");
                if (tempString != null) {
                    status = Integer.parseInt(tempString);

                    switch (status) {
                        case 100:
                            break; // Continue transfer
                        case 200:
                            String virusName = extractVirusName(parseMe);
                            return new ICAPResult(ICAPResult.STATE_VIRUS_FOUND, virusName);                            
                        case 204:
                            return new ICAPResult(ICAPResult.STATE_CHECKED);
                         case 404:
                            throw new ICAPException("404: ICAP Service not found");
                        default:
                            throw new ICAPException("Server returned unknown status code:" + status);
                    }
                }
            }


            // Sending remaining part of file
            if (fileSize < stdPreviewSize) {
                byte[] buffer = new byte[STD_SEND_LENGTH];
                while ((fileInStream.read(buffer)) != -1) {
                    sendString(Integer.toHexString(buffer.length) + "\r\n");
                    sendBytes(buffer);
                    sendString("\r\n");
                }
                // Closing file transfer.
                requestBuffer = "0\r\n\r\n";
                sendString(requestBuffer, true);
            }

            responseMap.clear();
            String response = getHeader(ICAPTERMINATOR);
            
            if (log.isDebugEnabled())
                log.debug("ICAP response(2) = " +response);   
            
            responseMap = parseHeader(response);

            tempString = responseMap.get("StatusCode");
            if (tempString != null) {
                status = Integer.parseInt(tempString);

                if (status == 204) {
                    return new ICAPResult(ICAPResult.STATE_CHECKED);
                } // Unmodified

                if (status == 200) { // OK - The ICAP status is ok, but the encapsulated HTTP status will likely be different
                    String virusName = extractVirusName(response);
                    return new ICAPResult(ICAPResult.STATE_VIRUS_FOUND, virusName);
                }
            }

            throw new ICAPException("Unrecognized or no status code in response header.");


        } finally {
            if( client != null)
                client.close();
        }
    }

    
    /**
     * Search virus by begin/end sequence.
     *
     * @param response the response
     * @param begin the begin 
     * @param end the end
     * @return the string
     */
    private String searchVirusBySequence( String response, String begin, String end) {
        String virus = null;
        int iBegin = response.indexOf(begin);
        if (iBegin != -1)    {
            int iEnd = response.indexOf(end, iBegin + begin.length());
            if( iEnd != -1) {
                virus = response.substring( iBegin + begin.length(), iEnd);
            }
        }
       return virus;
    }
    
    
    /**
     * Extract virus name from the response
     *
     * @param response the response
     * @return the string
     */
    private String extractVirusName(String response) {
        String virusName = searchVirusBySequence(response, "Threat=", ";");
        if( virusName == null)
            virusName = searchVirusBySequence(response, "X-FSecure-Infection-Name: \"", "\"");
        return virusName;
    }
    
    
    /**
     * Automatically asks for the servers available options and returns the raw response as a String.
     * 
     * @return String of the servers response.
     * @throws IOException
     * @throws ICAPException
     */
    private String getOptions() throws IOException, ICAPException {
        // Send OPTIONS header and receive response
        // Sending and recieving
        String requestHeader = "OPTIONS icap://" + serverIP + "/" + icapService + " ICAP/" + VERSION + "\r\n" + "Host: " + serverIP + "\r\n" + "User-Agent: "
                + USERAGENT + "\r\n" + "Encapsulated: null-body=0\r\n" + "\r\n";

        sendString(requestHeader, true);

        return getHeader(ICAPTERMINATOR);
    }

    /**
     * Receive an expected ICAP header as response of a request. The returned String should be parsed with parseHeader()
     * 
     * @param terminator
     * @return String of the raw response
     * @throws IOException
     * @throws ICAPException
     */
    private String getHeader(String terminator) throws IOException, ICAPException {
        byte[] endofheader = terminator.getBytes(StandardCharsetsUTF8);
        byte[] buffer = new byte[STD_RECEIVE_LENGTH];

        if (log.isDebugEnabled())
            log.debug("getHeader begin");     
        
        int n;
        int offset = 0;
        // STD_RECEIVE_LENGTH-offset is replaced by '1' to not receive the next (HTTP) header.
        while ((offset < STD_RECEIVE_LENGTH) && ((n = in.read(buffer, offset, 1)) != -1)) { // first part is to secure against DOS
            offset += n;

            if (offset > endofheader.length + 13) { // 13 is the smallest possible message "ICAP/1.0 xxx "
                byte[] lastBytes = Arrays.copyOfRange(buffer, offset - endofheader.length, offset);
                if (Arrays.equals(endofheader, lastBytes)) {
                    
                    if (log.isDebugEnabled())
                        log.debug("getHeader end : " + new String(buffer));     
                    
                    return new String(buffer, 0, offset, StandardCharsetsUTF8);
                      
                }
            }
        }
        throw new ICAPException("Error in getHeader() method");
    }

    /**
     * Given a raw response header as a String, it will parse through it and return a HashMap of the result
     * 
     * @param response A raw response header as a String.
     * @return HashMap of the key,value pairs of the response
     */
    private Map<String, String> parseHeader(String response) {
        Map<String, String> headers = new HashMap<>();

        /****
         * SAMPLE:****
         * ICAP/1.0 204 Unmodified
         * Server: C-ICAP/0.1.6
         * Connection: keep-alive
         * ISTag: CI0001-000-0978-6918203
         */
        // The status code is located between the first 2 whitespaces.
        // Read status code
        int x = response.indexOf(" ", 0);
        int y = response.indexOf(" ", x + 1);
        String statusCode = response.substring(x + 1, y);
        headers.put("StatusCode", statusCode);

        // Each line in the sample is ended with "\r\n".
        // When (i+2==response.length()) The end of the header have been reached.
        // The +=2 is added to skip the "\r\n".
        // Read headers
        int i = response.indexOf("\r\n", y);
        i += 2;
        while (i + 2 != response.length() && response.substring(i).contains(":")) {

            int n = response.indexOf(":", i);
            String key = response.substring(i, n);

            n += 2;
            i = response.indexOf("\r\n", n);
            String value = response.substring(n, i);

            headers.put(key, value);
            i += 2;
        }

        return headers;
    }

    /**
     * Sends a String through the socket connection. Used for sending ICAP/HTTP headers.
     * 
     * @param requestHeader
     * @throws IOException
     */
    private void sendString(String requestHeader) throws IOException {
        sendString(requestHeader, false);
    }

    /**
     * Sends a String through the socket connection. Used for sending ICAP/HTTP headers.
     * 
     * @param requestHeader
     * @param withFlush
     * @throws IOException
     */
    private void sendString(String requestHeader, boolean withFlush) throws IOException {
        out.write(requestHeader.getBytes(StandardCharsetsUTF8));
        if (withFlush) {
            out.flush();
        }
    }

    /**
     * Sends bytes of data from a byte-array through the socket connection. Used to send filedata.
     * 
     * @param chunk The byte-array to send.
     * @throws IOException
     */
    private void sendBytes(byte[] chunk) throws IOException {
        for (byte aChunk : chunk) {
            out.write(aChunk);
        }
    }


}
