package webserver;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;

/**
 *
 * @author Kris
 */
public abstract class AbstractRequestHandler implements HttpHandler{
    
    public static final String FILE_NOT_FOUND = "File not found: ";
    public static final String ERROR_OCCURRED = "An Error has occurred: ";
    
    protected String context;
    
    protected void sendData(HttpExchange he, byte[] data) throws IOException{
        he.sendResponseHeaders(200, data.length);
        send(he, data);
    }
     protected void sendTrue(HttpExchange he) throws IOException{
        he.sendResponseHeaders(200, "true".length());
        send(he, "true".getBytes());
     }
    
    protected void sendNotFound(HttpExchange he) throws IOException{
        String response = FILE_NOT_FOUND + he.getRequestURI().toString();
        he.sendResponseHeaders(404, response.length());
        send(he, response.getBytes());
    }
    
    protected void sendError(HttpExchange he, String msg) throws IOException{
//        String response = ERROR_OCCURRED + msg + " " + he.getRequestURI().toString();
        he.sendResponseHeaders(500, "false".length());
        send(he, "false".getBytes());
    }

    private void send(HttpExchange he, byte[] data) throws IOException{
        try (OutputStream os = he.getResponseBody()) {
            os.write(data);
            os.flush();
        }
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }
    
    
}
