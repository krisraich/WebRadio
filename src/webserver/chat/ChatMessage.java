package webserver.chat;

import java.net.InetSocketAddress;

/**
 *
 * @author Kris
 */
public class ChatMessage {

    private final String message;
    private final InetSocketAddress address;

    public ChatMessage(String message, InetSocketAddress address) {
        this.message = message;
        this.address = address;
    }

    public String getMessage() {
        return message;
    }

    public InetSocketAddress getAddress() {
        return address;
    }

    @Override
    public String toString() {
        return address.toString() + ": " + message;
    }

    

}
