package radio3;

import java.io.IOException;
import palyer.MPlayerController;
import palyer.MPlayerWrapper;
import palyer.state.PlayerState;
import webserver.ControlHandler;
import webserver.NotifyHandler;
import webserver.WebServer;


/**
 *
 * @author Kris
 */
public class Radio3 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {

//        args = new String[]{"p=8080"};
        
        // -------- DEFAULTS ---------
        int port = 80; //default Port
        String fifoFilePath = null;
        
        
        // ------- PARS ARGS ---------
        for (String currentArgument : args) {
            if(currentArgument.toLowerCase().startsWith("p=")){
                try {
                    port = Integer.parseInt(currentArgument.substring(2));
                    System.out.println("Benutze port: " + port);
                } catch (Exception e) {
                    System.err.println("Portnummer konnte nicht gelsenen werden");
                }
            }
            if(currentArgument.toLowerCase().startsWith("c=")){
                fifoFilePath = currentArgument.substring(2);
                System.out.println("Benutze control fifo file: " + fifoFilePath);
            }
        }

        
        // -------- init Playlist? ---------
        
        
        
        // -------- Starte Webserver ---------
        
        System.out.println("starte player [ENTER zum beenden]");
        
        WebServer server = new WebServer(port);
        server.start();
        
//        System.out.println("done. Webserver Adresse: " +server.getSocketAddress().toString());
        
        
        // -------- Starte MPlayaer Wrapper ---------
        MPlayerWrapper mPlayerWrapper;
        
        if(fifoFilePath == null){
            mPlayerWrapper = new MPlayerWrapper();
        }else{
            //player cöntrolls wenn fifoFile angegeben ist
            server.addContext("/control", new ControlHandler(new MPlayerController(fifoFilePath)));
            mPlayerWrapper = new MPlayerWrapper(fifoFilePath);
        }
        
        
        // -------- Starte MPlayaer Notifyer ---------   
        NotifyHandler notifyHandler = new NotifyHandler();
        server.addContext("/notify", notifyHandler);
        
        
        // -------- Init Player state & Observer ---------
        PlayerState playerState = new PlayerState();
        playerState.addObserver(notifyHandler);
        
        
        // -------- Bind Output reader ---------
        
        
        
        // -------- Starte den stuff ---------
        playerState.startWatching();
        mPlayerWrapper.start();
        
        
        
        System.in.read();
        System.out.println("stoppe player");
        
        
        // -------- Stoppe den  stuff ---------
        mPlayerWrapper.stop();
        playerState.stopWatching();
        server.stop();
        
    }
    
}

