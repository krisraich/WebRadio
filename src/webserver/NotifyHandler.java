package webserver;

import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;
import palyer.state.PlayerState;

/**
 *
 * @author Kris
 */
public class NotifyHandler extends AbstractRequestHandler implements Observer{

    public static final String CMD_BLOCK = "/blocking";
    public static final String CMD_INSTANT = "/instant";



    public NotifyHandler() {
        
    }
    
    
    @Override
    public void handle(HttpExchange he) throws IOException {
       
        String reqURI = he.getRequestURI().toString();
        
       if(reqURI.equals(context + CMD_INSTANT)){
           
           this.sendData(he, PlayerState.GetStateForWebRequest());
           
       }else if(reqURI.equals(context + CMD_BLOCK)){
           
            try {
                Thread.sleep(5000);
            } catch (InterruptedException ex) {
                Logger.getLogger(NotifyHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
            this.sendData(he, PlayerState.GetStateForWebRequest());
            
       }else {
           //default
           this.sendNotFound(he);
       }

    }

    @Override
    public void update(Observable o, Object arg) {
        
    }
}
