import java.io.*;
import java.net.*;

public class Server{


    int port = 1833;





    public void serverstart() {
        try {
            System.out.println("Server is starting... ");
            ServerSocket serversocket = new ServerSocket(port);
            while (true) {
                System.out.println("Waiting for Connection");
                Socket clientsocket = serversocket.accept();
                System.out.println("Connection accepted from " + clientsocket);
                Clienthandler clienthandler = new Clienthandler(clientsocket);
                clienthandler.run();


            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



    }

