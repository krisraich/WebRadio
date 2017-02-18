package webserver;

import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;
import palyer.state.PlayerState;

/**
 *
 * @author Kris
 */
public class UpdateHandler extends AbstractRequestHandler implements Observer{

    public static final String CMD_BLOCK = "/blocking";
    public static final String CMD_INSTANT = "/instant";

    
    @Override
    public void handle(HttpExchange he) throws IOException {
       
        String reqURI = he.getRequestURI().toString();
        he.getResponseHeaders().add("Content-Type", "application/json");
        
        
       if(reqURI.equals(context + CMD_INSTANT)){
           
           this.sendData(he, PlayerState.getInstacnce().getStateForWebRequest());
           
       }else if(reqURI.startsWith(context + CMD_BLOCK)){
           
           try {
                synchronized(this){
                    wait();
                    System.out.println("Sending state to: " + he.getRemoteAddress());
                    this.sendData(he, PlayerState.getInstacnce().getStateForWebRequest());
                }
            } catch (InterruptedException ex) {
                System.err.println("Timeout while waiting");
                this.sendError(he);
            }
            
       }else {
           //default
           this.sendNotFound(he);
       }

    }
    

    @Override
    public void update(Observable o, Object arg) {
//        System.out.println("State changed. sending update.");
         synchronized(this){
             notifyAll();
         }
    }
}
