package webserver.chat;

import com.sun.net.httpserver.Headers;
import webserver.*;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
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

    public static final int MAX_CHAT_LENGTH = 1000;
    
    private final ChatStack chatStack = new ChatStack(30);

    @Override
    public void handle(HttpExchange he) throws IOException {

        String reqURI = he.getRequestURI().toString();

        if (reqURI.equals(context + CMD_ADD) && he.getRequestMethod().equalsIgnoreCase("POST")) {
            
            he.getResponseHeaders().add("Content-Type", "application/json");
            String msg;
           
            try {
                byte[] buffer;
                Headers requestHeaders = he.getRequestHeaders();
//                Set<Map.Entry<String, List<String>>> entries = requestHeaders.entrySet();
                if(requestHeaders.containsKey("Content-length")){
                    int contentLength = 
                            Math.max(
                                MAX_CHAT_LENGTH,
                                Integer.parseInt(requestHeaders.getFirst("Content-length"))
                            );
                    if (Radio3.DEV_MODE) System.out.println("Received Chat message. Length: " + contentLength);
                    buffer = new byte[contentLength]; 
                    he.getRequestBody().read(buffer);
                    msg = new String(buffer).trim();
                     
                }else{
                    buffer = new byte[MAX_CHAT_LENGTH * 2]; //chars doppelt
                    int len = he.getRequestBody().read(buffer,0, buffer.length);
                    msg = new String(buffer, 0, len).trim();
                }

                if(msg.length() == 0){
                    throw new IOException("Message empty");
                }
                
                
            } catch (IOException e) {
                this.sendFalse(he);
                if (Radio3.DEV_MODE) System.err.println("Error receiving Chat message. " + e.getMessage());
                return;
            }
            
            synchronized(this){
                chatStack.add(new ChatMessage(msg, he.getRemoteAddress().getAddress()));
                notifyAll();
                this.sendTrue(he);
            }

        } else if (reqURI.equals(context + CMD_CLEAR)) {
            he.getResponseHeaders().add("Content-Type", "application/json");
            if (Radio3.DEV_MODE) System.out.println("Clearing chat");
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
