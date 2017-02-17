package stations;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.SAXException;

/**
 *
 * @author Kris
 */
public class StationManager {

    private static StationManager instance;

    public static synchronized StationManager getInstance() {
        if (instance == null) {
            instance = new StationManager();
        }
        return instance;
    }

    private SortedMap<Integer, StationBean> stationBeans; 
    
    private StationManager() {
        InputStream is = getClass().getResourceAsStream("stations.xml");

        SAXParserFactory factory = SAXParserFactory.newInstance();
        try {

            SAXParser saxParser = factory.newSAXParser();
            StationHandler handler = new StationHandler();
            saxParser.parse(is, handler);
            stationBeans = handler.getStationBeans();
            
        } catch (IOException err) {
            System.err.println("Cant read stations xml");
        } catch (ParserConfigurationException | SAXException ex) {
             System.err.println("Cant parse stations xml");
        } 

    }

    public synchronized byte[] getBeansAsJsonArray(){
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        
        
        for (Map.Entry<Integer, StationBean> entry : stationBeans.entrySet()) {
            sb.append(entry.getValue().toJsonString());
            sb.append(",");
        }
        sb.deleteCharAt(sb.length()-1);
        sb.append("]");
        return sb.toString().getBytes();
    }
    
    public synchronized StationBean getStationByID(int stationid){
        return stationBeans.get(stationid);
    }
    
    public synchronized String getFirstEntryPath(){
        return stationBeans.get(stationBeans.firstKey()).getPath();
    }
    
}
