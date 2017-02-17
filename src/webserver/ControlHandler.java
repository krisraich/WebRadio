package webserver;

import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import palyer.MediaPlayerControl;

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
    public static final String CMD_SETSTREAM = "/setStream?path=";
    
    
    
    private final MediaPlayerControl mediaPlayerControl;

    public ControlHandler(MediaPlayerControl mediaPlayerControl) {
        this.mediaPlayerControl = mediaPlayerControl;
    }
    
    
    @Override
    public void handle(HttpExchange he) throws IOException {
       
        String reqURI = he.getRequestURI().toString();
        
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
           this.mediaPlayerControl.setStreamingURL(reqURI.substring(context.length() + CMD_SETSTREAM.length()));
       }else{
           //default
           this.sendNotFound(he);
           return;
       }
       this.sendTrue(he);
    }
}
