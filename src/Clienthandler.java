import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class Clienthandler extends Thread{
    //Inspiration durch dieses YouTube Video von Jim Liao : https://www.youtube.com/watch?v=cRfsUrU3RjE
    //Inspiration durch dieses YouTube Video: https://www.youtube.com/watch?v=gLfuZrrfKes
    final Socket client;
    public static ArrayList<Clienthandler> serverliste = new ArrayList<>();
    public static ArrayList<Clienthandler> Chatroom1 = new ArrayList<>();
    public static ArrayList<Clienthandler> Chatroom2 = new ArrayList<>();
    public static ArrayList<Clienthandler> Chatroom3 = new ArrayList<>();
    public static ArrayList<Clienthandler> Chatroom4 = new ArrayList<>();
    public static ArrayList<Clienthandler> Chatroom5 = new ArrayList<>();

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
            serverliste.add(this);
            Chatroom1.add(this);
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
                if( clientMessage.equalsIgnoreCase("/changeChatroom")){
                    changeChatroom();
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
    for (Clienthandler clienthandler : serverliste){
        try{
            if (Message.equalsIgnoreCase("")){

            }
            else if (!clienthandler.clientUsername.equals(clientUsername) && !Message.contains("/")) {
                clienthandler.bufferedWriter.write(formatedMessage);
                clienthandler.bufferedWriter.newLine();
                clienthandler.bufferedWriter.flush();

            } else if (Message.equalsIgnoreCase("/showmembers") || Message.equalsIgnoreCase("/quit") || Message.equalsIgnoreCase("/changeChatroom")) {

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
           serverliste.remove(this);
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
            for (Clienthandler clienthandler : serverliste) {
                if (clienthandler.clientUsername.equals(clientUsername)) {
                    bufferedWriter.write("Current Users on this Server: ");
                    bufferedWriter.newLine();
                    bufferedWriter.flush();
                    for (Clienthandler clienthandler1 : serverliste) {
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
        for (Clienthandler clienthandler : serverliste){
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
    public void changeChatroom(){

        try {
            for (Clienthandler clienthandler : serverliste) {
                if(clienthandler.clientUsername.equals(clientUsername)) {
                    if (Chatroom1.contains(clienthandler)) {
                        Chatroom1.remove(clienthandler);
                    } else if (Chatroom2.contains(clienthandler)) {
                        Chatroom2.remove(clienthandler);
                    } else if (Chatroom3.contains(clienthandler)) {
                        Chatroom3.remove(clienthandler);
                    } else if (Chatroom4.contains(clienthandler)) {
                        Chatroom4.remove(clienthandler);
                    } else if (Chatroom5.contains(clienthandler)) {
                        Chatroom5.remove(clienthandler);
                    }

                    String insert = clienthandler.bufferedReader.readLine();
                    if (insert == "1") {
                        Chatroom1.add(clienthandler);
                        bufferedWriter.write("You successfully joined Chatroom 1!");
                    } else if (insert == "2") {
                        Chatroom2.add(clienthandler);
                        bufferedWriter.write("You successfully joined Chatroom 2!");
                    } else if (insert == "3") {
                        Chatroom3.add(clienthandler);
                        bufferedWriter.write("You successfully joined Chatroom 3!");
                    } else if (insert == "4") {
                        Chatroom4.add(clienthandler);
                        bufferedWriter.write("You successfully joined Chatroom 4!");
                    } else if (insert == "5") {
                        Chatroom5.add(clienthandler);
                        bufferedWriter.write("You successfully joined Chatroom 5!");
                    } else if (insert == "x") {
                        bufferedWriter.write("You choosed to stay in your current room! Please type /changeChatroom if you changed your Mind!");
                    } else {
                        bufferedWriter.write("Error! A Chatroom like that don't exist on this Server. We only have: Chatroom 1, Chatroom 2, Chatroom 3, Chatroom 4, Chatroom 5");
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
    }

