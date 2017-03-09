package webserver.chat;

import java.net.InetAddress;
import java.util.Date;

/**
 *
 * @author Kris
 */
public class ChatMessage {

    private final String message;
    private final InetAddress address;
    private final long created;

    public ChatMessage(String message, InetAddress address) {
        this.message = message;
        this.address = address;
        this.created = System.currentTimeMillis();
    }

    public String getMessage() {
        return message;
    }

    public InetAddress getAddress() {
        return address;
    }

    public Date getCreated() {
        return new Date(created);
    }
    
    
    @Override
    public String toString() {
        return address.toString().substring(1) + ": " + message;
    }

    

}
