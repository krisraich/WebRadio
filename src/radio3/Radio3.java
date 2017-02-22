package radio3;

import java.io.IOException;
import player.MPlayerController;
import player.MPlayerWrapper;
import player.state.PlayerState;
import player.state.StateReader;
import stations.StationManager;
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
 *  s= pfad zum station XML für andere webradios
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

    public static final boolean DEV_MODE = false;    
    
    /**
     * @param args the command line arguments
     * @throws java.io.IOException
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
                    System.out.println("Set Webserver port to: " + port);
                } catch (NumberFormatException e) {
                    System.err.println("Cant parse port number");
                }
            }
            if(currentArgument.toLowerCase().startsWith("c=")){
                fifoFilePath = currentArgument.substring(2);
                System.out.println("Using Pipe: " + fifoFilePath);
            }
            if(currentArgument.toLowerCase().startsWith("s=")){
                StationManager.setFilePathToStationsXML(fifoFilePath);
                System.out.println("Using File For Stations XML: " + fifoFilePath);
            }
        }

        
        
        // -------- Starte Webserver ---------
        
        System.out.println("starting [enter to stop]");
        
        WebServer server = new WebServer(port);
        server.start();
        
        if(Radio3.DEV_MODE) System.out.println("done. Webserver adress: " + server.getSocketAddress().toString());
        
        
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
        System.out.println("stopping..");
        
        
        // -------- Stoppe den  stuff ---------
        mPlayerWrapper.stop();
        StateReader.stopProcessing();
        server.stop();
        
    }
    
}

