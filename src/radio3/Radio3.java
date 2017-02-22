package radio3;

import java.io.IOException;
import player.MPlayerController;
import player.MPlayerWrapper;
import player.state.PlayerState;
import player.state.StateReader;
import webserver.ControlHandler;
import webserver.UpdateHandler;
import webserver.WebServer;


/**
 *
 * AIO Programm zur Ansteuerung von MPlayer (linux)
 * 
 * Argumente
 *  p= ist die portnummer vom Webserver (default ist 80)
 *  c= pfad zum fifofile für die steuerung
 * 
 * startet einen Webserver
 * 
 *   Liste der stations
 *     /getStationList
 * 
 *   Status des players:
 *      /update/blocking/*random-id* (Blockiert bis sich status ändert, benötigt random-ID fürs threading)
 *      /update/instant
 * 
 *   Steuerung des Players
 *      /control/stop //startet player neu!
 *      /control/pause
 *      /control/play
 *      /control/setStream?id=2
 *      (Siehe ControlHandler.java)
 * 
 * 
 * @author Kris
 */
public class Radio3 {

//    public static final boolean IS_DEBUG = true;
    
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
        
//        StationManager stationManager = StationManager.getInstance();
        
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
        UpdateHandler notifyHandler = new UpdateHandler();
        server.addContext("/update", notifyHandler);
        
        
        // -------- Init Player state & Observer ---------
        PlayerState playerState = PlayerState.getInstacnce();
        playerState.addObserver(notifyHandler);

        
        // -------- Starte den stuff ---------
        mPlayerWrapper.start();
        
        
        
        System.in.read();
        System.out.println("stoppe player");
        
        
        // -------- Stoppe den  stuff ---------
        mPlayerWrapper.stop();
        StateReader.stopProcessing();
        server.stop();
        
    }
    
}

