package player;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import palyer.state.StateReader;
import stations.StationManager;

/**
 *
 * @author Kris
 */
public class MPlayerWrapper {

    private final ProcessBuilder processBuilder;
    private final AtomicBoolean isPlaying = new AtomicBoolean(false);
    private Thread playerWatcherThread = null;

    private class PlayerWatcher implements Runnable {

        private Process process;

        @Override
        public void run() {

            do {
                try {
                    process = processBuilder.start();

                    StateReader.bindInputStreamAndProcess(process.getInputStream());

                    process.waitFor();
                    if (isPlaying.get()) {
                        System.out.println("Mplayer crashed.. Restarting");
                        Thread.sleep(1000);

                    }
                } catch (InterruptedException ex) {

                } catch (IOException ex) {
                    System.err.println("Konnte MPlayer nicht starten: " + ex.getMessage());
                    break;
                }finally{
                    try {
                        process.destroyForcibly();
                    } catch (Exception e) {}
                }

            } while (isPlaying.get());
        }
    };

    public MPlayerWrapper() {
        this.processBuilder = new ProcessBuilder("mplayer", StationManager.getInstance().getFirstEntryPath());
    }

    public MPlayerWrapper(String pathToFifoFile) {
        this.processBuilder = new ProcessBuilder("mplayer", "-slave", "-input", "file=" + pathToFifoFile, StationManager.getInstance().getFirstEntryPath());
        System.out.println(processBuilder.command());
    }

    public void start() {
        if( ! isPlaying.getAndSet(true)){
            playerWatcherThread = new Thread(new PlayerWatcher());
            playerWatcherThread.setName("Mplayer Watcher");
            playerWatcherThread.start();
        }

    }

    public void stop() {
        if(isPlaying.getAndSet(false))
            playerWatcherThread.interrupt();

    }

}
