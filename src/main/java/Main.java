import nsu.networks.server.Server;

import static java.lang.Integer.parseInt;

public class Main {
    public static void main(String[] args) {
        if(args.length != 1){
            System.err.println("Server needs port in the Program arguments\nExample: java -jar ServerFileReceiverTCP.jar 8080");
        }
        else {
            Server server = new Server(parseInt(args[0]));
        }
    }
}