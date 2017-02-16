package webserver;

import com.sun.net.httpserver.HttpExchange;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author Kris
 */
public class RequestHandler extends AbstractRequestHandler {

    
    private static final Map<String, byte[]> FILE_CACHE = new ConcurrentHashMap<>();
    
    @Override
    public void handle(HttpExchange he) throws IOException {
        
        String reqURI = he.getRequestURI().toString();
        
        if("/".equals(reqURI)){
            reqURI = "/index.html";
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

            FILE_CACHE.put(reqURI,  buffer.toByteArray());
        }
        
        this.sendData(he, FILE_CACHE.get(reqURI));

    }
 
}
