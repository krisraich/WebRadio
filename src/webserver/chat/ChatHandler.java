package webserver.chat;

import webserver.*;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.util.Observable;
import player.state.PlayerState;
import radio3.Radio3;

/**
 *
 * @author Kris
 */
public class ChatHandler extends AbstractRequestHandler {

    public static final String CMD_ADD = "/add";
    public static final String CMD_BLOCK = "/blocking";
    public static final String CMD_INSTANT = "/instant";

    private final ChatStack chatStack = new ChatStack(30);
    private volatile byte[] bytesToSend = "empty chat".getBytes();

    @Override
    public void handle(HttpExchange he) throws IOException {

        String reqURI = he.getRequestURI().toString();

        if (reqURI.equals(context + CMD_ADD)) {
            he.getResponseHeaders().add("Content-Type", "application/json");

            byte[] buffer = new byte[4000]; //max 2000 chars
            int read = he.getRequestBody().read(buffer, 0, buffer.length);
            
            
             synchronized(this){
                chatStack.add(new String(buffer, 0, read));
                bytesToSend = chatStack.getChatAsHTML().getBytes();
                notifyAll();
            }
            
             this.sendTrue(he);
            

        } else if (reqURI.equals(context + CMD_INSTANT)) {
            
            he.getResponseHeaders().add("Content-Type", "text/html");
            this.sendData(he, bytesToSend);
            
        } else if (reqURI.startsWith(context + CMD_BLOCK)) {
            he.getResponseHeaders().add("Content-Type", "text/html");
            try {
                synchronized (this) {
                    wait();
                    if (Radio3.DEV_MODE) System.out.println("Sending chat to: " + he.getRemoteAddress());
                    this.sendData(he, bytesToSend);
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
