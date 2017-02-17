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
public class NotifyHandler extends AbstractRequestHandler implements Observer{

    public static final String CMD_BLOCK = "/blocking";
    public static final String CMD_INSTANT = "/instant";

    
    @Override
    public void handle(HttpExchange he) throws IOException {
       
        String reqURI = he.getRequestURI().toString();
        
       if(reqURI.equals(context + CMD_INSTANT)){
           
           this.sendData(he, PlayerState.getInstacnce().getStateForWebRequest());
           
       }else if(reqURI.equals(context + CMD_BLOCK)){
           
            waitForUpdate(he);
            System.out.println("Sending Update - awake");
       }else {
           //default
           this.sendNotFound(he);
       }

    }
    
    private synchronized void waitForUpdate(HttpExchange he) throws IOException{
        try {
            wait();
            this.sendData(he, PlayerState.getInstacnce().getStateForWebRequest());
        } catch (InterruptedException ex) {
            this.sendError(he, "Timeout while waiting");
        }
    }

    @Override
    public synchronized void update(Observable o, Object arg) {
        System.out.println("Sending Update - notifyAll");
        notifyAll();
    }
}
