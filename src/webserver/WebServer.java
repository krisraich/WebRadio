package webserver;

import com.sun.net.httpserver.*;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;


/**
 *
 * @author Kris
 */
public class WebServer {
    
     private HttpServer server;
     private final int port;

    public WebServer() {
        this(80);
    }

    public WebServer(int port) {
        this.port = port;
    }
    
    public void start() throws IOException{
        server = HttpServer.create(new InetSocketAddress(port), 0);
        addContext("/", new RequestHandler());
        server.setExecutor(Executors.newCachedThreadPool()); // creates a default executor
        server.start();

    }
    
     public void addContext(String context, AbstractRequestHandler abstractRequestHandler){
        server.createContext(context, abstractRequestHandler);
        abstractRequestHandler.setContext(context);
     }
    
    public void stop(){
        server.stop(0);
    }

    public int getPort() {
        return port;
    }

    public InetSocketAddress getSocketAddress() {
        return server.getAddress();
    }
    

}
