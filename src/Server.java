
import java.io.*;
import java.net.*;

public class Server {
    //Basierend auf dem Code von diesem Tutorial: https://www.youtube.com/watch?v=cRfsUrU3RjE
    int port;


    public Server(int port) {
        this.port = port;
    }



    public void start() {
        try {
            System.out.println("Server is starting... ");
            ServerSocket serverSocket = new ServerSocket(port);
            while (true) {
                System.out.println("Waiting for new connections");
                Socket clientsocket = serverSocket.accept();
                System.out.println("Connection accepted from " + clientsocket);
                Clienthandler clienthandler = new Clienthandler(clientsocket);
                clienthandler.start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}




