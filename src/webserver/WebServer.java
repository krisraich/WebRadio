package webserver;

import com.sun.net.httpserver.*;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import radio3.Radio3;


/**
 *
 * @author Kris
 */
public class WebServer {
    
     private HttpServer server;
     private final int port;

    public WebServer() {
        //default
        this(80);
    }

    public WebServer(int port) {
        this.port = port;
    }
    
    public void start() throws IOException{
        server = HttpServer.create(new InetSocketAddress(port), 0);
        addContext("/", new RequestHandler());
        server.setExecutor(Executors.newCachedThreadPool());
        server.start();

    }
    
     public void addContext(String context, AbstractRequestHandler abstractRequestHandler){
        server.createContext(context, abstractRequestHandler);
        abstractRequestHandler.setContext(context);
     }
    
    public void stop(){
        if(Radio3.DEV_MODE) System.out.println("Stopping webserver");
        server.stop(0);
    }

    public int getPort() {
        return port;
    }

    public InetSocketAddress getSocketAddress() {
        return server.getAddress();
    }
    

}
