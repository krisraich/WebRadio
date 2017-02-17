package palyer.state;

import java.util.Observable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import radio3.Util;

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
    


    private PlayerState() {}
    
    private static PlayerState instance = null;
    public synchronized static PlayerState getInstacnce(){
        if(PlayerState.instance == null){
            PlayerState.instance = new PlayerState();
        }
        return PlayerState.instance;
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
    
    public byte[] getStateForWebRequest(){
        return (" { \"title\": " + Util.sanitizeJson(this.streamTitle) + ", " + 
                "\"volume\": " + this.volume + ", " + 
                "\"mute\": " + this.mute + ", " + 
                "\"playing\": " + this.playing + "}").getBytes();
    }

    @Override
    public String toString() {
        return "Current Title: " + this.streamTitle + 
                "\r\nVolume: " + this.volume +
                "\r\nMute: " + this.mute +
                "\r\nPlaying: " + this.playing;
    }
    

}
