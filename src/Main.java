import java.net.*;

public class Main {
// Öffne Cmd dann schreibe: telnet localhost 1833
    public static void main(String[] args) {
        Server server = new Server(1833);
        server.start();


    }
}