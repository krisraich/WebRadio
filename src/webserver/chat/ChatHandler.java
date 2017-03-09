package webserver.chat;

import com.sun.net.httpserver.Headers;
import webserver.*;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import radio3.Radio3;

/**
 *
 * @author Kris
 */
public class ChatHandler extends AbstractRequestHandler {

    public static final String CMD_ADD = "/add";
    public static final String CMD_CLEAR = "/clear";
    public static final String CMD_BLOCK = "/blocking";
    public static final String CMD_INSTANT = "/instant";

    private final ChatStack chatStack = new ChatStack(30);

    @Override
    public void handle(HttpExchange he) throws IOException {

        String reqURI = he.getRequestURI().toString();

        if (reqURI.equals(context + CMD_ADD) && he.getRequestMethod().equalsIgnoreCase("POST")) {
            
            he.getResponseHeaders().add("Content-Type", "application/json");
            
            byte[] buffer;
            try {
                
                Headers requestHeaders = he.getRequestHeaders();
                Set<Map.Entry<String, List<String>>> entries = requestHeaders.entrySet();
                int contentLength = Integer.parseInt(requestHeaders.getFirst("Content-length"));

                if(contentLength > 5000){
                    contentLength = 5000;
                }
                
                buffer = new byte[contentLength]; 
            
                int read = he.getRequestBody().read(buffer);
                if(read == 0) throw new IOException("Message null");
                
            } catch (IOException e) {
                this.sendError(he);
                return;
            }
            
             synchronized(this){
                chatStack.add(new ChatMessage(new String(buffer), he.getRemoteAddress().getAddress()));
                notifyAll();
            }
            
             this.sendTrue(he);
            

        } else if (reqURI.equals(context + CMD_CLEAR)) {
            he.getResponseHeaders().add("Content-Type", "application/json");
            chatStack.clear();  
            this.sendTrue(he);
        } else if (reqURI.equals(context + CMD_INSTANT)) {
            
            he.getResponseHeaders().add("Content-Type", "text/html");
            this.sendData(he, chatStack.getOutCache());
            
        } else if (reqURI.startsWith(context + CMD_BLOCK)) {
            he.getResponseHeaders().add("Content-Type", "text/html");
            try {
                synchronized (this) {
                    wait();
                    if (Radio3.DEV_MODE) System.out.println("Sending chat to: " + he.getRemoteAddress());
                    this.sendData(he, chatStack.getOutCache());
                }
            } catch (InterruptedException ex) {
                System.err.println("Timeout while waiting [chat]");
                this.sendError(he);
            }

        } else {
            //default
            this.sendNotFound(he);
        }

    }


}
