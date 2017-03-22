package webserver;

import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import radio3.Radio3;

/**
 *
 * @author Kris
 */
public class AccessManageHandler extends AbstractRequestHandler {

    private static final String ADD_IP = "/add?ip=";
    private static final String REMOVE_IP = "/remove?ip=";
    private static final String LIST_IP = "/list";
    
    private static final Set<String> ALLOWED_IP_ADDRESSES = new CopyOnWriteArraySet<>();
  
    @Override
    public void handle(HttpExchange he) throws IOException {
       
        String reqURI = he.getRequestURI().toString();
        String requestAddress = he.getRemoteAddress().getAddress().toString().substring(1);
        he.getResponseHeaders().add("Content-Type", "application/json");
        
         if(! ALLOWED_IP_ADDRESSES.contains(requestAddress)){
            System.out.println("Blocked access request from: " + requestAddress);
            this.sendFalse(he);
            return;
        }
        
        String ip = reqURI.substring(reqURI.indexOf("=") + 1);
        if(reqURI.startsWith(context + ADD_IP)){
            System.out.println("Access allowing IP: " + ip);
            ALLOWED_IP_ADDRESSES.add(ip);
        }else if(reqURI.startsWith(context + REMOVE_IP)){
            
            //cannot self terminate
            if(ALLOWED_IP_ADDRESSES.size() == 1){
                if(Radio3.DEV_MODE) System.out.println("Cannot remove last Admin... IP: " + ip);
                this.sendFalse(he);
                return;
            }
            
            ALLOWED_IP_ADDRESSES.remove(ip);
            System.out.println("Access removing IP: " + ip);
        }else if(reqURI.equals(context + LIST_IP)){
            
            StringBuilder sb = new StringBuilder();
            sb.append('[');
            ALLOWED_IP_ADDRESSES.forEach((allowedIPAddress) -> {
                sb.append('"');
                sb.append(allowedIPAddress);
                sb.append("\",");
            });
            
            sb.deleteCharAt(sb.length()-1);
            sb.append(']');
            this.sendData(he, sb.toString().getBytes());
            
        }
        this.sendTrue(he);

    }

    
    public static Set<String> getAllowedIPAddresses() {
        return ALLOWED_IP_ADDRESSES;
    }
    
    public static boolean hasPermission(String ipaddress){
        //wenn liste leer ist, gibt es keine beschränkung
        return ALLOWED_IP_ADDRESSES.isEmpty() || ALLOWED_IP_ADDRESSES.contains(ipaddress);
    }
}
