package webserver;

import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import player.MediaPlayerControl;
import radio3.Radio3;

/**
 *
 * @author Kris
 */
public class ControlHandler extends AbstractRequestHandler{

    public static final String CMD_PAUSE = "/pause";
    public static final String CMD_PLAY = "/play";
    public static final String CMD_STOP = "/stop";
    public static final String CMD_MUTE = "/mute";
    public static final String CMD_UNMUTE = "/unmute";
    public static final String CMD_VOLUP = "/volup";
    public static final String CMD_VOLDOWN = "/voldown";
    public static final String CMD_SETSTREAM = "/setStream?id=";
    
    
    public static List<String> allowedIPAddresses = new ArrayList<>();
    
    
    private final MediaPlayerControl mediaPlayerControl;

    public ControlHandler(MediaPlayerControl mediaPlayerControl) {
        this.mediaPlayerControl = mediaPlayerControl;
    }
    
    
    @Override
    public void handle(HttpExchange he) throws IOException {
       
        String reqURI = he.getRequestURI().toString();
        String requestAddress = he.getRemoteAddress().getAddress().toString().substring(1);
        he.getResponseHeaders().add("Content-Type", "application/json");
       
        if(! allowedIPAddresses.isEmpty() && ! allowedIPAddresses.contains(requestAddress)){
           System.out.println("Blocked control request from: " + requestAddress);
            this.sendFalse(he);
            return;
        }
        
        
        if( Radio3.DEV_MODE){
            System.out.println("Received cmd '" + reqURI + "' from: " + he.getRemoteAddress());
        }
        
       if(reqURI.equals(context + CMD_PAUSE)){
           this.mediaPlayerControl.pause();
       }else if(reqURI.equals(context + CMD_PLAY)){
           this.mediaPlayerControl.play();
       }else if(reqURI.equals(context + CMD_STOP)){
           this.mediaPlayerControl.stop();
       }else if(reqURI.equals(context + CMD_MUTE)){
           this.mediaPlayerControl.mute();
       }else if(reqURI.equals(context + CMD_UNMUTE)){
           this.mediaPlayerControl.unmute();
       }else if(reqURI.equals(context + CMD_VOLUP)){
           this.mediaPlayerControl.volumeUp();
       }else if(reqURI.equals(context + CMD_VOLDOWN)){
           this.mediaPlayerControl.volumeDown();
       }else if(reqURI.startsWith(context + CMD_SETSTREAM)){
           try{
                int stationid = Integer.parseInt(reqURI.substring(context.length() + CMD_SETSTREAM.length()));
                this.mediaPlayerControl.setStationID(stationid);
           }catch(Exception e){
               System.err.println("Cant parse station id");
               this.sendError(he);
           }
       }else{
           //default
           this.sendNotFound(he);
           return;
       }
       this.sendTrue(he);
    }
}
