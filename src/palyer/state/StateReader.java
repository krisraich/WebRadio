package palyer.state;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;

/**
 *
 * TODO Regex by BOB implementieren
 * 
 * @author Kris
 */
public class StateReader extends Thread{

    private final static PlayerState playerState = PlayerState.getInstacnce();
    private static StateReader currentInstance = null;
    
    
    
    /**
     * Kann mehrmals aufgerufen werden und muss in einem Thread sein...
     * @param inputStream 
     */
    public static synchronized void bindInputStreamAndProcess(InputStream inputStream){
        stopProcessing();
        currentInstance = new StateReader(inputStream);
        currentInstance.start();
    }
    
    public static void stopProcessing(){
        if(currentInstance != null){
            currentInstance.dowork.set(false);
            currentInstance.interrupt();
        }
    }
    
    private final InputStream inputStream;
    private final AtomicBoolean dowork = new AtomicBoolean(true);
    
    private StateReader(InputStream inputStream){
        this.setName("Player Outpu Streamreader");
        this.inputStream = inputStream;
    }
    
    
    @Override
    public void run() {
        
        try(BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while (dowork.get()) {
                line = br.readLine().trim();

                // process the line.
                if(line == null){
                    //suspend Thread
                    Thread.sleep(100);
                    continue;
                }else if(line.length() == 0){
                    continue;
                }
                
                System.out.println("line: " + line);
                
                //TODO: BOB
                
                
                if(line.startsWith("A:")){
                    //player is alive
                    playerState.updateIsPlaying();
                }else if(line.startsWith("ICY Info:")){
                    System.out.print("received icy info");
                    playerState.setStreamTitle(line);
                }else if(line.contains("Mute: disabled")){
                    System.out.print("received mute info");
                    playerState.setMute(false);
                }else if(line.contains("Mute:")){
                    System.out.print("received mute2 info");
                    playerState.setMute(true);
                }else if(line.contains("Volume:")){
                    System.out.print("received volume info");
                    playerState.setVolume((byte)0);
                }

            }

        } catch (IOException iOException) {
            //read line wirft IO Exception.. Stream ist geschlossen
             System.err.println("Lost Programm output");
//             ex.printStackTrace();
        }catch (InterruptedException interruptedException){
            //Programm has terminated
        }
        
    }
}
