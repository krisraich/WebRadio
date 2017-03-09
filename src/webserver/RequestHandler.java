package webserver;

import com.sun.net.httpserver.HttpExchange;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import radio3.Radio3;
import radio3.Util;
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
        
        
        //using external files for debug
         if(Radio3.DEV_MODE ){
             File f = new File("debug" + reqURI);
             if(f.exists() && ! f.isDirectory()){
                System.out.println("File found in Debug folder: debug" + reqURI);
                he.getResponseHeaders().add("Content-Type", Util.getMimeTypeFromRessourceName(reqURI));
                this.sendData(he, Files.readAllBytes(f.toPath()));
             }
         }
 
        if(! FILE_CACHE.containsKey(reqURI)){
            if(Radio3.DEV_MODE) System.out.println("Adding to file Cache: " + reqURI);
            
            
            if("/getStationList".equals(reqURI)){
                FILE_CACHE.put(
                        reqURI, 
                        CacheResponseHolder.createCacheResponseHolderWithMimeType(
                                "application/json", 
                                StationManager.getInstance().getBeansAsJsonArray()
                        )
                );
            }else{
                
                InputStream is = getClass().getResourceAsStream("/webserver/ressource" +  reqURI);
            
                if(is == null){
                    this.sendNotFound(he);
                    if(Radio3.DEV_MODE) System.out.println("File not found: " + reqURI);
                    return;
                }

                ByteArrayOutputStream buffer = new ByteArrayOutputStream();

                int nRead;
                byte[] data = new byte[1048576];

                while ((nRead = is.read(data, 0, data.length)) != -1) {
                  buffer.write(data, 0, nRead);
                }

                buffer.flush();

                FILE_CACHE.put(
                        reqURI, 
                        CacheResponseHolder.createCacheResponseHolderFromURL(reqURI, buffer.toByteArray())
                );
            }
        }
        
        CacheResponseHolder response = FILE_CACHE.get(reqURI);
        he.getResponseHeaders().add("Content-Type", response.getMimetype());
        this.sendData(he, response.getResponseData());

    }
 
}
