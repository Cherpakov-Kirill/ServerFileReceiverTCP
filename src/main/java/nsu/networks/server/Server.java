package nsu.networks.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private ServerSocket server;

    public Server(int port) {
        Socket socket = null;

        try {
            server = new ServerSocket(port);

            System.out.println("state: Server is working");

            while (true) {
                System.out.println("state: Server is waiting for any connection");
                socket = server.accept();
                new ClientHandler(socket);
                System.out.println("state: New client connected");
            }

        } catch (IOException e) {
            System.err.println("ERROR: Server did not started");
            e.printStackTrace();
        } finally {
            try {
                if (server != null) server.close();
                if (socket != null) socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
