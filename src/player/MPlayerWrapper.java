package player;

import java.io.IOException;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.atomic.AtomicBoolean;
import player.state.PlayerState;
import player.state.StateReader;
import stations.StationManager;

/**
 *
 * @author Kris
 */
public class MPlayerWrapper implements Observer {

    private final ProcessBuilder processBuilder;
    private final AtomicBoolean isPlaying = new AtomicBoolean(false);
    private final MediaPlayerControl mediaPlayerControl;
    
    private volatile boolean wasPaused = false;
    private volatile boolean wasMuted = false;
    private Integer lastStation = null;
    
    private volatile boolean hasCrashed = false;
    
    
    
    private Thread playerWatcherThread = null;

    @Override
    public void update(Observable o, Object arg) {
//        if (hasCrashed && isPlaying.get() && (wasMuted || wasPaused) && playerWatcherThread != null) {
//            
//            System.out.println("Mplayer was muted / not playing.. muting /pausing again");
//            
//            if(wasMuted)
//                mediaPlayerControl.mute();
//        
//            if(wasPaused)
//                mediaPlayerControl.pause();
//    
//            hasCrashed = false;
//            
//        }
    }

    private class PlayerWatcher implements Runnable {

        private Process process;

        @Override
        public void run() {

            do {
                try {
                    
                     if(lastStation == null){
                        lastStation = StationManager.getInstance().getFirstEntryID();
                        processBuilder.command().add(StationManager.getInstance().getFirstEntryPath());
                     }else{
                        processBuilder.command().remove(processBuilder.command().size()-1);
                        processBuilder.command().add(StationManager.getInstance().getStationByID(lastStation).getPath());
                     }
                    
                    process = processBuilder.start();
                    
                    //derber Bugfix... ist zum ausbessern
                    if(hasCrashed && mediaPlayerControl != null && (wasMuted || wasPaused)){
                        Thread.sleep(3000);
                        System.out.println("Mplayer was muted / not playing.. muting /pausing again");
                        
                        if (wasMuted) {
                            mediaPlayerControl.mute();
                        }

                        if (wasPaused){
                            mediaPlayerControl.pause();
                        }
                        
                    }
                    
                    
                    StateReader.bindInputStreamAndProcess(process.getInputStream());
                    
                    hasCrashed = false;
                    process.waitFor();
                    if (isPlaying.get()) {
                        wasMuted = PlayerState.getInstacnce().isMute();
                        wasPaused = !PlayerState.getInstacnce().isPlaying();
                        lastStation = PlayerState.getInstacnce().getId();
                        hasCrashed = true;
                        System.err.println("Mplayer has stopped.. Restarting");
                        Thread.sleep(1000);

                    }
                } catch (InterruptedException ex) {

                } catch (IOException ex) {
                    System.err.println("Konnte MPlayer nicht starten: " + ex.getMessage());
                    break;
                } finally {
                    try {
                        process.destroyForcibly();
                    } catch (Exception e) {
                    }
                }

            } while (isPlaying.get());
        }

    }

    public MPlayerWrapper() {
        this.processBuilder = new ProcessBuilder("mplayer");
        this.mediaPlayerControl = null;
    }

    public MPlayerWrapper(String pathToFifoFile, MediaPlayerControl mediaPlayerControl) {
        this.mediaPlayerControl = mediaPlayerControl;
        this.processBuilder = new ProcessBuilder("mplayer", "-slave", "-input", "file=" + pathToFifoFile);
    }

    public void start() {
        if (!isPlaying.getAndSet(true)) {
            playerWatcherThread = new Thread(new PlayerWatcher());
            playerWatcherThread.setName("Mplayer Watcher");
            playerWatcherThread.start();
        }

    }

    public void stop() {
        if (isPlaying.getAndSet(false)) {
            playerWatcherThread.interrupt();
        }

    }

}
