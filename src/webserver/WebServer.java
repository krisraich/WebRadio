package webserver;

import com.sun.net.httpserver.*;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 *
 * @author Kris
 */
public class WebServer {
    
     private HttpServer server;
     private ExecutorService executorService;
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
        executorService = Executors.newCachedThreadPool();
        server.setExecutor(executorService);
        server.start();

    }
    
     public void addContext(String context, AbstractRequestHandler abstractRequestHandler){
        server.createContext(context, abstractRequestHandler);
        abstractRequestHandler.setContext(context);
     }
    
    public void stop(){
        server.stop(1);
        executorService.shutdownNow();
    }

    public int getPort() {
        return port;
    }

    public InetSocketAddress getSocketAddress() {
        return server.getAddress();
    }
    

}
