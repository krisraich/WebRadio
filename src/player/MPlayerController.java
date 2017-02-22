package player;

import java.io.FileWriter;
import java.io.IOException;
import player.state.PlayerState;
import stations.StationBean;
import stations.StationManager;

/**
 *
 * @author Kris
 */
public class MPlayerController implements MediaPlayerControl {

//    final private Path pathToFifoFile;
    private final String pathToFifoFile;

    public MPlayerController(String pathToFifoFile) {
        this.pathToFifoFile = pathToFifoFile;
    }

    private synchronized void writeToFiFo(String cmd) {
//        System.out.print("Write '" + cmd + "' to fifo file (" + pathToFifoFile + ")");
//        System.out.print("Send cmd to player");
        try (FileWriter fileWriter = new FileWriter(pathToFifoFile)) {
            fileWriter.write(cmd);
            fileWriter.write("\n"); //nicht vergessen!!
            fileWriter.flush();
//            System.out.println("... done");
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
    public void setStationID(int stationid) {
        StationBean stationBean = StationManager.getInstance().getStationByID(stationid);
        if (stationBean == null) {
            System.err.println("Unkonwn Station ID");
        } else {
            PlayerState playerState = PlayerState.getInstacnce();
            if (playerState.getId() != stationid) {
                playerState.setId(stationid);
                writeToFiFo("loadfile " + stationBean.getPath());
            }
        }

    }

}
