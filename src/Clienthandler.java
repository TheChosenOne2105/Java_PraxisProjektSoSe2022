import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class Clienthandler extends Thread{
    //Inspiration durch dieses YouTube Video von Jim Liao : https://www.youtube.com/watch?v=cRfsUrU3RjE
    //Inspiration durch dieses YouTube Video: https://www.youtube.com/watch?v=gLfuZrrfKes
    final Socket client;
    public static ArrayList<Clienthandler> clienthandlers = new ArrayList<>();
    private String clientUsername;
    private BufferedWriter bufferedWriter;
    private BufferedReader bufferedReader;

    public Clienthandler(Socket clientsocket) throws IOException {
        this.client = clientsocket;



    }

    @Override
    public void run() {
        try {
            this.bufferedReader = new BufferedReader(new InputStreamReader(client.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new PrintWriter(client.getOutputStream()));
            this.clientUsername = bufferedReader.readLine();
            broadcastServerMessage("Server: " + clientUsername + " has entered the Chat!");
            clienthandlers.add(this);
            handler(client);
        } catch (IOException e) {
            removeClient();
        }
    }
    public void handler(Socket clientSocket){
        String clientMessage;

        while (clientSocket.isConnected()){
            try {
                clientMessage =  bufferedReader.readLine();
                sendingMessages(clientMessage);
                if (clientMessage.equalsIgnoreCase("/quit")){
                    removeClient();
                    break;
                }
                if (clientMessage.equalsIgnoreCase("/showmembers")){
                    showClients();
                }
            } catch (IOException e) {
                removeClient();
                break;
            }


        }
        removeClient();

    }


    public void sendingMessages(String Message){
        String formatedMessage = clientUsername+ ": " + Message;
    for (Clienthandler clienthandler : clienthandlers){
        try{
            if (Message.equalsIgnoreCase("")){

            }
            else if (!clienthandler.clientUsername.equals(clientUsername) && !Message.contains("/")) {
                clienthandler.bufferedWriter.write(formatedMessage);
                clienthandler.bufferedWriter.newLine();
                clienthandler.bufferedWriter.flush();

            } else if (Message.equalsIgnoreCase("/showmembers") || Message.equalsIgnoreCase("/quit")) {

            } else if (clienthandler.clientUsername.equals(clientUsername) && Message.contains("/")){
                clienthandler.bufferedWriter.write("Dieser Befehl ist uns leider unbekannt! Bitte versuchen Sie es erneut.");
                clienthandler.bufferedWriter.newLine();
                clienthandler.bufferedWriter.flush();
            }

        } catch (IOException e) {
            removeClient();
            break;
        }
    }
    }
    public void removeClient() {
       try {
           this.bufferedWriter.write("You disconnected from the Server!");
           clienthandlers.remove(this);
           broadcastServerMessage("Server: " + clientUsername + " has left the chat");
           if (bufferedWriter != null) {
               this.bufferedWriter.close();
           }
           if (bufferedReader != null) {
               this.bufferedReader.close();
           }
           if (client != null) {
               this.client.close();
           }
       } catch (IOException e) {
           e.printStackTrace();
       }
    }
    public void showClients() {
        try {
            for (Clienthandler clienthandler : clienthandlers) {
                if (clienthandler.clientUsername.equals(clientUsername)) {
                    bufferedWriter.write("Current Users on this Server: ");
                    bufferedWriter.newLine();
                    bufferedWriter.flush();
                    for (Clienthandler clienthandler1 : clienthandlers) {
                        bufferedWriter.write(clienthandler1.clientUsername);
                        bufferedWriter.newLine();
                        bufferedWriter.flush();

                    }
                }
            }
        } catch (IOException e) {
            removeClient();
        }
    }
    public void broadcastServerMessage(String Message){
        for (Clienthandler clienthandler : clienthandlers){
            try {
                clienthandler.bufferedWriter.write(Message);
                clienthandler.bufferedWriter.newLine();
                clienthandler.bufferedWriter.flush();
            } catch (IOException e) {
                removeClient();
                break;
            }
        }
    }
    }

