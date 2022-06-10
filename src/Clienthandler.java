import javax.xml.crypto.Data;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Clienthandler extends Thread {
    //Basierend auf dem Code von diesem Tutorial: https://www.youtube.com/watch?v=cRfsUrU3RjE
    //Basierend auf dem Code von diesem Tutorial: https://www.youtube.com/watch?v=gLfuZrrfKes
    final Socket client;
    public static ArrayList<Clienthandler> serverliste = new ArrayList<>();
    public static ArrayList<Clienthandler> Chatroom1 = new ArrayList<>();
    public static ArrayList<Clienthandler> Chatroom2 = new ArrayList<>();
    public static ArrayList<Clienthandler> Chatroom3 = new ArrayList<>();
    public static ArrayList<Clienthandler> Chatroom4 = new ArrayList<>();
    public static ArrayList<Clienthandler> Chatroom5 = new ArrayList<>();
    public static HashMap <Integer , ArrayList<Clienthandler>> Chatrooms = new HashMap<Integer, ArrayList<Clienthandler>>();


    private String clientUsername;
    private BufferedWriter bufferedWriter;
    private BufferedReader bufferedReader;

    public Clienthandler(Socket clientsocket) throws IOException {
        this.client = clientsocket;


    }

    @Override
    public void run() {
        try {
            AssigningChatrooms();
            this.bufferedReader = new BufferedReader(new InputStreamReader(client.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new PrintWriter(client.getOutputStream()));
            this.clientUsername = bufferedReader.readLine();
            broadcastServerMessage(clientUsername + " has entered the Chat!");
            serverliste.add(this);
            changeChatroom();
            handler(client);
        } catch (IOException e) {
            removeClient();
        }
    }

    public void handler(Socket clientSocket) {
        String clientMessage;

        while (clientSocket.isConnected()) {
            try {
                clientMessage = bufferedReader.readLine();
                sendingMessages(clientMessage);
                if (clientMessage.equalsIgnoreCase("/quit")) {
                    bufferedWriter.write("You disconnected from the Server!");
                    removeClient();
                    break;
                }
                if (clientMessage.equalsIgnoreCase("/showmembers")) {
                    showClients();
                }
                if (clientMessage.equalsIgnoreCase("/changeChatroom")) {
                    changeChatroom();
                }
            } catch (IOException e) {
                removeClient();
                break;
            }


        }
        removeClient();

    }


    public void sendingMessages(String Message) {
        Database database = new Database();
        int Adresse =1 ;
        String formatedMessage = clientUsername + ": " + Message;
        ArrayList<Clienthandler> sendingroom = serverliste;
        for (Clienthandler clienthandler : serverliste) {
            if (clienthandler.clientUsername.equals(clientUsername)) {
                for(int i=1; i<Chatrooms.size()+1; i++){
                    if (Chatrooms.get(i).contains(clienthandler)) {
                    sendingroom = Chatrooms.get(i);
                    if(!Message.contains("/") && !Message.equals("")) {
                        database.insertIntoDatabase(formatedMessage, i);
                    }
                }
                }
            }
        }
        try {
            for (Clienthandler clienthandler : sendingroom) {
                if (Message.equalsIgnoreCase("")) {

                } else if (!clienthandler.clientUsername.equals(clientUsername) && !Message.contains("/")) {
                    clienthandler.bufferedWriter.write(formatedMessage);
                    clienthandler.bufferedWriter.newLine();
                    clienthandler.bufferedWriter.flush();


                } else if (Message.equalsIgnoreCase("/showmembers") || Message.equalsIgnoreCase("/quit") || Message.equalsIgnoreCase("/changeChatroom")) {

                } else if (clienthandler.clientUsername.equals(clientUsername) && Message.contains("/")) {
                    clienthandler.bufferedWriter.write("Dieser Befehl ist uns leider unbekannt! Bitte versuchen Sie es erneut.");
                    clienthandler.bufferedWriter.newLine();
                    clienthandler.bufferedWriter.flush();
                }
            }
        } catch (IOException e) {
            removeClient();

        }

    }

    public void removeClient() {
        try {
            serverliste.remove(this);
            broadcastServerMessage(clientUsername + " has left the chat");
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
                    for(int i=1; i < Chatrooms.size()+1; i++){
                        bufferedWriter.write("Current Users in Chatroom " + i);
                        bufferedWriter.newLine();
                        bufferedWriter.flush();
                        for(Clienthandler clienthandler2 : Chatrooms.get(i)){
                            bufferedWriter.write(clienthandler2.clientUsername);
                            bufferedWriter.newLine();
                            bufferedWriter.flush();
                        }
                    }
                }

            }
        } catch (IOException e) {
            removeClient();
        }
    }

    public void broadcastServerMessage(String Message) {
        String formatedMessage = "Server: " + Message;
        for (Clienthandler clienthandler : serverliste) {
            try {
                clienthandler.bufferedWriter.write(formatedMessage);
                clienthandler.bufferedWriter.newLine();
                clienthandler.bufferedWriter.flush();
            } catch (IOException e) {
                removeClient();
                break;
            }
        }
    }

    public void changeChatroom() {
        Database database = new Database();
        boolean checker = true;
        int auswahl = 0;
        try {
            for (Clienthandler clienthandler : serverliste) {
                if (clienthandler.clientUsername.equals(clientUsername)) {
                    bufferedWriter.write("Please enter Number of the Chatroom(1-5) that you want to go in or x if you want to remain in your current chatroom: " );
                    bufferedWriter.newLine();
                    bufferedWriter.flush();
                    String insert = clienthandler.bufferedReader.readLine();
                    for (int i = 1; i < Chatrooms.size()+1; i++) {
                        if (insert.equals(Integer.toString(i))) {
                            if (Chatrooms.get(i).contains(clienthandler)) {
                                bufferedWriter.write("You are already in Chatroom "+ i + " !");
                                bufferedWriter.newLine();
                                bufferedWriter.flush();
                                checker = false;
                                break;
                            } else {
                                Chatrooms.get(i).add(clienthandler);
                                broadcastServerMessage(clienthandler.clientUsername + " joined Chatroom " + i);
                                ArrayList<String> LoadedMessages = database.LoadingOldMessages(i);
                                bufferedWriter.write("Old Messages of Chatroom " + i+ ":");
                                bufferedWriter.newLine();
                                bufferedWriter.flush();
                                for(String string: LoadedMessages){
                                    bufferedWriter.write(string);
                                    bufferedWriter.newLine();
                                    bufferedWriter.flush();
                                }
                                bufferedWriter.write("New Messages:");
                                bufferedWriter.newLine();
                                bufferedWriter.flush();
                                auswahl = i;
                                checker = false;
                            }
                        } }
                        if (insert.equalsIgnoreCase("x")) {
                            bufferedWriter.write("You choosed to stay in your current room! Please type /changeChatroom if you changed your Mind!");
                            bufferedWriter.newLine();
                            bufferedWriter.flush();
                            break;
                        } else if (checker && !insert.equalsIgnoreCase("x") ) {
                            bufferedWriter.write("Error! A Chatroom like that don't exist on this Server. We only have: Chatroom 1, Chatroom 2, Chatroom 3, Chatroom 4, Chatroom 5");
                            bufferedWriter.newLine();
                            bufferedWriter.flush();
                            break;
                        }

                    if(auswahl > 0){
                        for (int i = 1; i < Chatrooms.size()+1; i++){
                            if(i != auswahl && Chatrooms.get(i).contains(clienthandler)){
                                broadcastServerMessage(clienthandler.clientUsername + " has left Chatroom " + i);
                                Chatrooms.get(i).remove(clienthandler);
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
    public void AssigningChatrooms() throws IOException {
        if(!(Chatrooms.containsValue(Chatroom1) || Chatrooms.containsValue(Chatroom2) || Chatrooms.containsValue(Chatroom3) || Chatrooms.containsValue(Chatroom4) || Chatrooms.containsValue(Chatroom5))){
            Chatrooms.put(1, Chatroom1);
            Chatrooms.put(2, Chatroom2);
            Chatrooms.put(3, Chatroom3);
            Chatrooms.put(4, Chatroom4);
            Chatrooms.put(5, Chatroom5);


        }
    }
}


