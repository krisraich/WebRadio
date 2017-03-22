package radio3;

import java.io.IOException;
import player.MPlayerController;
import player.MPlayerWrapper;
import player.state.PlayerState;
import player.state.StateReader;
import stations.StationManager;
import webserver.AccessManageHandler;
import webserver.ControlHandler;
import webserver.UpdateHandler;
import webserver.WebServer;
import webserver.chat.ChatHandler;


/**
 *
 * AIO Programm zur Ansteuerung von MPlayer (linux)
 * 
 * Argumente
 *  p= ist die portnummer vom Webserver (default ist 80)
 *  c= pfad zum fifofile für die steuerung
 *  s= pfad zum station XML für andere webradios (siehe stations/stations.xml)
 *  --debug Debug Infos
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

    public static boolean DEV_MODE = false;    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
                
//        remote debugging
//        try {
//            System.in.read();
//        } catch (IOException ex) {}

        
        // -------- DEFAULTS ---------
        int port = 80; //default Port
        String fifoFilePath = null;
        boolean hasChat = true;
        boolean hasSeenAllowIP = false;
        
        // ------- PARS ARGS ---------
        for (String currentArgument : args) {
            if(currentArgument.toLowerCase().equals("--debug")){
               Radio3.DEV_MODE = true;
            }
            if(currentArgument.toLowerCase().equals("--nochat")){
               hasChat = false;
            }
            
            if(! hasSeenAllowIP && currentArgument.toLowerCase().startsWith("--allow-ip-only=")){
                hasSeenAllowIP = true;
                
                String[] ipaddesses = currentArgument.substring(16).split("#");
                
                for (String current : ipaddesses) {
                    AccessManageHandler.getAllowedIPAddresses().add(current.trim());
                    System.out.println("Allwoing IP: " + current);
                }
            }
            
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
                String pathToXML = currentArgument.substring(2);
                StationManager.setFilePathToStationsXML(pathToXML);
                System.out.println("Using File For Stations XML: " + pathToXML);
            }
        }

        
        
        // -------- Starte Webserver ---------
        
        System.out.println("starting awesome webraido");
        
        WebServer server = new WebServer(port);
        try {
            server.start();
        } catch (IOException ex) {
            System.err.println("Can't start web server. Maybe another program is running on this port? " + ex.getMessage());
            return;
        }
        
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
        
        // -------- Aktiviere chat ---------   
        if(hasChat){
            server.addContext("/chat", new ChatHandler());
        }
        
        
        // -------- Aktiviere Access Mgmt ---------   
        if(hasSeenAllowIP){
            server.addContext("/access", new AccessManageHandler());
        }
        
        
        
        // -------- Init Player state & Observer ---------
        PlayerState playerState = PlayerState.getInstacnce();
        playerState.addObserver(notifyHandler);

        
        // -------- Starte den stuff ---------
        mPlayerWrapper.start();
        
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                System.out.println("stopping...");
                
                // -------- Stoppe den stuff ---------
                mPlayerWrapper.stop();
                StateReader.stopProcessing();
                server.stop();
                
            } catch (Throwable e) {
                System.err.println("Error while cleaning up: " + e.getMessage());
                //nothing
            }
        }));
    }
}

