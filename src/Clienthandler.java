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



    }

    @Override
    public void run() {
        try {
            this.bufferedReader = new BufferedReader(new InputStreamReader(client.getInputStream()));
            this.outputStream = client.getOutputStream();
            outputStream.write("Enter your Username: ".getBytes());
            this.clientUsername = bufferedReader.readLine();
            broadcastServerMessage("Server: " + clientUsername + " has entered the Chat! \n \r");
            clienthandlers.add(this);
            handler(client);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void handler(Socket clientSocket) throws IOException {
        String clientMessage;

        while (clientSocket.isConnected()){
            try {
                clientMessage =  bufferedReader.readLine();
                sendingMessages(clientMessage);
                if (clientMessage.equalsIgnoreCase("/quit")){
                    removeClient();
                }
                if (clientMessage.equalsIgnoreCase("/showMembers")){
                    showClients();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }


        }
        removeClient();

    }


    public void sendingMessages(String Message){
        String formatedMessage = clientUsername+ ": " + Message;
    for (Clienthandler clienthandler : clienthandlers){
        try{
            if (!clienthandler.clientUsername.equals(clientUsername) && !Message.contains("/")) {
                clienthandler.outputStream.write(formatedMessage.getBytes());
                clienthandler.outputStream.write("\n \r".getBytes());
            } else if (Message.equalsIgnoreCase("/showmembers") || Message.equalsIgnoreCase("/quit")) {

            } else if (clienthandler.clientUsername.equals(clientUsername) && Message.contains("/")){
                clienthandler.outputStream.write("Dieser Befehl ist uns leider unbekannt! Bitte versuchen Sie es erneut.\n \r".getBytes());
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    }
    public void removeClient() throws IOException {
        outputStream.write("You disconnected from the Server!".getBytes());
        clienthandlers.remove(this);
        outputStream.close();
        bufferedReader.close();
        client.close();
        broadcastServerMessage("Server: " + clientUsername + " has left the chat\n\r");
    }
    public void showClients() throws IOException {
        for (Clienthandler clienthandler : clienthandlers){
            if(clienthandler.clientUsername.equals(clientUsername)){
                for (Clienthandler clienthandler1 : clienthandlers){
                    outputStream.write(clienthandler1.clientUsername.getBytes());
                    outputStream.write("\n \r".getBytes());
                }
            }
        }
    }
    public void broadcastServerMessage(String Message){
        for (Clienthandler clienthandler : clienthandlers){
            try {
                clienthandler.outputStream.write(Message.getBytes());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    }

