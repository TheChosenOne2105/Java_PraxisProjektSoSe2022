import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class Clienthandler extends Thread{
    //Inspiration durch dieses YouTube Video von Jim Liao : https://www.youtube.com/watch?v=cRfsUrU3RjE
    //Inspiration durch dieses YouTube Video: https://www.youtube.com/watch?v=gLfuZrrfKes
    final Socket client;
    public static ArrayList<Clienthandler> clienthandlers = new ArrayList<>();
    private String clientUsername;

    private OutputStream outputStream;
    private BufferedReader bufferedReader;

    public Clienthandler(Socket clientsocket) throws IOException {
        this.client = clientsocket;
        this.outputStream = clientsocket.getOutputStream();
        this.bufferedReader = new BufferedReader(new InputStreamReader(clientsocket.getInputStream()));
        outputStream.write("Enter your Username: ".getBytes());
        this.clientUsername = bufferedReader.readLine();
        clienthandlers.add(this);
        sendingMessages("Server: " + clientUsername + " has entered the Chat!");

    }

    @Override
    public void run() {
        handler(client);
    }
    public void handler(Socket clientSocket){
        String clientMessage;

        while (clientSocket.isConnected()){
            try {
                clientMessage = bufferedReader.readLine();
                sendingMessages(clientMessage);
                if (clientMessage.equalsIgnoreCase("quit")){
                    removeClientHandler();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }


        }

    }


    public void sendingMessages(String Message){
        String formatedMessage = clientUsername+ ": " + Message;
    for (Clienthandler clienthandler : clienthandlers){
        try{
            if(!clienthandler.clientUsername.equals(clientUsername)){
                clienthandler.outputStream.write(formatedMessage.getBytes());
                clienthandler.outputStream.write("\n \r".getBytes());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    }
    public void removeClientHandler() throws IOException {
        clienthandlers.remove(this);
        sendingMessages("Server: " + clientUsername + "has left the chat");
        outputStream.close();
        bufferedReader.close();
        client.close();
    }
    }

