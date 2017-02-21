
package radio3;

/**
 *
 * @author Kris
 */
public class Util {
    
    /**
     * Erstellt einen Json gültigen String
     * @param string
     * @return 
     */
    public static String sanitizeJson(String string) {
         if (string == null || string.length() == 0) {
             return "\"\"";
         }

         char         c = 0;
         int          i;
         int          len = string.length();
         StringBuilder sb = new StringBuilder(len + 4);
         String       t;

         sb.append('"');
         for (i = 0; i < len; i += 1) {
             c = string.charAt(i);
             switch (c) {
             case '\\':
             case '"':
                 sb.append('\\');
                 sb.append(c);
                 break;
             case '/':
 //                if (b == '<') {
                     sb.append('\\');
 //                }
                 sb.append(c);
                 break;
             case '\b':
                 sb.append("\\b");
                 break;
             case '\t':
                 sb.append("\\t");
                 break;
             case '\n':
                 sb.append("\\n");
                 break;
             case '\f':
                 sb.append("\\f");
                 break;
             case '\r':
                sb.append("\\r");
                break;
             default:
                 if (c < ' ') {
                     t = "000" + Integer.toHexString(c);
                     sb.append("\\u" + t.substring(t.length() - 4));
                 } else {
                     sb.append(c);
                 }
             }
         }
         sb.append('"');
         return sb.toString();
     }
    
    /**
     * Gibt den vermuteten Mime-Type einer Datei zurück, aufgrund der Dateiendung
     * @param fileName Name der Datei
     * @return 
     */
    public static String getMimeTypeFromRessourceName(String fileName){
        int lastindex =  fileName.lastIndexOf(".");
        
        //fileextension gültig
        String fileExtension = fileName.substring(lastindex + 1).toLowerCase();

        switch(fileExtension){
            case "htm": 
            case "html": 
            case "shtml": 
                            return "text/html";
                
            case "xhtml":   return "application/xhtml+xml";

            case "xml":     return "application/xml";
                
            case "css":     return "text/css";
            case "js":      return  "application/javascript";
            case "json":    return  "application/json";

            case "png":     return  "image/png";
            case "svg":     return  "image/svg+xml";
                
            case "jpg":
            case "jpeg":
            case "jpe":
                            return  "image/jpeg";

            case "woff":    return  "application/font-woff";
            case "woff2":   return  "font/woff2";
            case "eot":     return  "application/vnd.ms-fontobject";
            case "ttf":     return  "application/font-sfnt";
        }
        return null;
    }
    
}
