import java.io.*;
import java.net.*;

public class Clienthandler extends Thread{
    final Socket client;
    public Clienthandler(Socket clientsocket){
        this.client = clientsocket;

    }

    @Override
    public void run() {
        try {
            clienthandler(client);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void clienthandler(Socket clientsocket) throws IOException {
        InputStream inputStream = clientsocket.getInputStream();
        OutputStream outputStream = clientsocket.getOutputStream();

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        outputStream.write("Connected to Server!\n\r".getBytes());
        outputStream.write("Please type in your commands/messages: \n\r".getBytes());
        String Userinput;
        while ((Userinput = bufferedReader.readLine()) != null){
            String msg =Userinput ;
            String output = "You Typed: " + msg + "\n\r";
            outputStream.write(output.getBytes());
            if(msg.equalsIgnoreCase("quit")){
                break;
            }

        }
        outputStream.write("\n\rYou disconnected from the Server!".getBytes());
        clientsocket.close();


    }
}
