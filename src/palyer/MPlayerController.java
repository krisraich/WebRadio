package palyer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 *
 * @author Kris
 */
public class MPlayerController implements MediaPlayerControl{

    final private Path pathToFifoFile;
    
    public MPlayerController(String pathToFifoFile) {
        this.pathToFifoFile = new File(pathToFifoFile).toPath();
    }

    private synchronized void writeToFiFo(String cmd){
        try {
            Files.write(pathToFifoFile, cmd.getBytes(), StandardOpenOption.WRITE);
        } catch (IOException ex) {
            System.err.println("FiFo File konnte nicht beschrieben werden: " + ex.getMessage());
        }
    }
    
    @Override
    public void play() {
        pause();
    }

    @Override
    public void pause() {
        writeToFiFo("pause");
    }

    @Override
    public void stop() {
        writeToFiFo("stop");
    }

    @Override
    public void mute() {
        writeToFiFo("mute");
    }

    @Override
    public void unmute() {
        mute();
    }

    @Override
    public void volumeUp() {
        writeToFiFo("volume 1");
    }

    @Override
    public void volumeDown() {
        writeToFiFo("volume 0");
    }

    @Override
    public void setStreamingURL(String target) {
        //TODO: check if target exists bzw. map target id to url
        
        writeToFiFo("loadfile " + target);
    }
    
    
  
    
}
