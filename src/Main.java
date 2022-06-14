import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Main {
    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));

    public static void main(String[] args) throws IOException {
        System.out.println("Server is starting... ");
        ServerSocket serverSocket = new ServerSocket(1833);
        while (true) {
            System.out.println("Waiting for new connections");
            Socket clientsocket = serverSocket.accept();
            System.out.println("Connection accepted from " + clientsocket);
            Clienthandler clienthandler = new Clienthandler(clientsocket);
            clienthandler.start();
        }
    }
}