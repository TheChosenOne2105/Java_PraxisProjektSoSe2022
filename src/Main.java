import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    public static void main(String[] args) throws IOException {
        System.out.println("Server is starting... ");
        ServerSocket serverSocket = new ServerSocket(1833);
        while (true) {
            System.out.println("Waiting for new connections");
            Socket Clientsocket = serverSocket.accept();
            System.out.println("Connection accepted from " + Clientsocket);
            ClientHandler clienthandler = new ClientHandler(Clientsocket);
            clienthandler.start();
        }
    }
}