package webserver;

import com.sun.net.httpserver.HttpExchange;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import stations.StationManager;

/**
 *
 * @author Kris
 */
public class RequestHandler extends AbstractRequestHandler {

    
    private static final Map<String, CacheResponseHolder> FILE_CACHE = new ConcurrentHashMap<>();
    
    @Override
    public void handle(HttpExchange he) throws IOException {
        
        String reqURI = he.getRequestURI().toString();
        
        if("/".equals(reqURI)){
            reqURI = "/index.html";
        }
        

        if("/getStationList".equals(reqURI)){
            FILE_CACHE.put(reqURI, 
                    CacheResponseHolder.createCacheResponseHolderWithMimeType(
                            "application/json", 
                            StationManager.getInstance().getBeansAsJsonArray()
                    )
            );
        }
        
        if(! FILE_CACHE.containsKey(reqURI)){
            
            InputStream is = getClass().getResourceAsStream("/webserver/ressource" +  reqURI);
            
            if(is == null){
                this.sendNotFound(he);
                return;
            }
            
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();

            int nRead;
            byte[] data = new byte[1048576];

            while ((nRead = is.read(data, 0, data.length)) != -1) {
              buffer.write(data, 0, nRead);
            }

            buffer.flush();

            FILE_CACHE.put(reqURI, 
                    CacheResponseHolder.createCacheResponseHolderFromURL(reqURI, buffer.toByteArray())
            );
        }
        
        CacheResponseHolder response = FILE_CACHE.get(reqURI);
        he.getResponseHeaders().add("Content-Type", response.getMimetype());
        this.sendData(he, response.getResponseData());

    }
 
}
