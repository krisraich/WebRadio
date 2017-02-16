package palyer;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

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

                    process.getInputStream();

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
        this.processBuilder = new ProcessBuilder("mplayer", "http://mp3channels.webradio.rockantenne.de/classic-perlen.aac");
    }

    public MPlayerWrapper(String pathToFifoFile) {
        this.processBuilder = new ProcessBuilder("mplayer", "-slave", "-input", "file=" + pathToFifoFile, "http://mp3channels.webradio.rockantenne.de/classic-perlen.aac");
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
