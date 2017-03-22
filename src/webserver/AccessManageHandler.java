package webserver;

import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author Kris
 */
public class AccessManageHandler extends AbstractRequestHandler {

    private static final String ADD_IP = "/add?ip=";
    private static final String REMOVE_IP = "/remove?ip=";
    
    
    
    
    @Override
    public void handle(HttpExchange he) throws IOException {
       
        String reqURI = he.getRequestURI().toString();
        String requestAddress = he.getRemoteAddress().getAddress().toString().substring(1);
        he.getResponseHeaders().add("Content-Type", "application/json");
        
        List<String> allowedIPAddresses = ControlHandler.getAllowedIPAddresses();
        
        
         if(! allowedIPAddresses.isEmpty() && ! allowedIPAddresses.contains(requestAddress)){
            System.out.println("Blocked access request from: " + requestAddress);
            this.sendFalse(he);
            return;
        }
        
        String ip = reqURI.substring(reqURI.lastIndexOf("=") + 1);
        if(reqURI.startsWith(context + ADD_IP)){
            allowedIPAddresses.add(ip);
        }else if(reqURI.startsWith(context + REMOVE_IP)){
            allowedIPAddresses.remove(ip);
        }
        this.sendTrue(he);

    }

}
