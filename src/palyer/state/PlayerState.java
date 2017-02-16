package palyer.state;

import java.util.Observable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 *
 * @author Kris
 */
public class PlayerState extends Observable{

    public static final long LAST_UPDATE_THRESHOLD = 5 * 1000; //5sek
    
    private final AtomicBoolean isWatching = new AtomicBoolean(false);
    
    private String streamTitle;
    private byte volume;
    private boolean mute;
    private boolean playing;
    
    private AtomicLong lastUpdate = new AtomicLong();
    private Thread isPlayingWatcherThread;
    private final Runnable isPlayingWatcher = new Runnable() {
        @Override
        public void run() {
            lastUpdate.set(System.currentTimeMillis());
            while(isWatching.get()){
                try {
                    Thread.sleep(1000);
                    setPlaying(
                            System.currentTimeMillis() -  lastUpdate.get() <  LAST_UPDATE_THRESHOLD
                    );
                } catch (InterruptedException ex) {}
            }
        }
    };
    
    private static PlayerState instance = null;

    public PlayerState() {
        if(PlayerState.instance == null){
            PlayerState.instance = this;
        }
    }
    public static byte[] GetStateForWebRequest(){
        return instance.getStateForWebRequest();
    }
    
    
    private void checkedNotifyObserver(){
        if(isWatching.get()){
            this.notifyObservers(getStateForWebRequest());
        }
    }
    
    public void startWatching(){
        if( ! isWatching.getAndSet(true)){
            isPlayingWatcherThread = new Thread(isPlayingWatcher);
            isPlayingWatcherThread.setName("Is Playing status Watcher");
            isPlayingWatcherThread.start();
        }
    }
    public void stopWatching(){
        if( isWatching.getAndSet(false)){
            isPlayingWatcherThread.interrupt();
            isPlayingWatcherThread = null;
        }
    }
    
    
    public String getStreamTitle() {
        return streamTitle;
    }

    public void setStreamTitle(String streamTitle) {
        if(! streamTitle.equals(this.streamTitle)){
            this.streamTitle = streamTitle;
            checkedNotifyObserver();
        }
    }

    public byte getVolume() {
        return volume;
    }

    public void setVolume(byte volume) {
        if(this.volume != volume){
            this.volume = volume;
            checkedNotifyObserver();
        }
    }

    public boolean isMute() {
        return mute;
    }

    public void setMute(boolean mute) {
        if(this.mute != mute){
            this.mute = mute;
            checkedNotifyObserver();
        }
    }

    public boolean isPlaying() {
        return playing;
    }

    private void setPlaying(boolean playing) {
        if(this.playing != playing){
            this.playing = playing;
            checkedNotifyObserver();
        }
    }
    

    public void updateIsPlaying() {
        lastUpdate.set(System.currentTimeMillis());
    }
    
    private byte[] getStateForWebRequest(){
        return "test state".getBytes();
    }

}
