package radio3;

import java.io.IOException;


public class RadioTest {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {

//        args = new String[]{"p=8080"};
        
        // -------- DEFAULTS ---------
        int port = 80; //default Port
        String fifoFilePath = null;
        
        
        // ------- PARS ARGS ---------
        for (String currentArgument : args) {
            if(currentArgument.toLowerCase().startsWith("p=")){
                try {
                    port = Integer.parseInt(currentArgument.substring(2));
                    System.out.println("Benutze port: " + port);
                } catch (Exception e) {
                    System.err.println("Portnummer konnte nicht gelsenen werden");
                }
            }
            if(currentArgument.toLowerCase().startsWith("c=")){
                fifoFilePath = currentArgument.substring(2);
                System.out.println("Benutze control fifo file: " + fifoFilePath);
            }
        }

        
        
        
        
        System.in.read();

        
    }
    
}

