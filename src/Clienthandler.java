
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
    Database database = new Database();
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
            LoginOrRegister();
            AssigningChatrooms();
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
        String formatedMessage = clientUsername + ": " + Message;
        ArrayList<Clienthandler> sendingroom = serverliste;
        for (Clienthandler clienthandler : serverliste) {
            if (clienthandler.clientUsername.equals(clientUsername)) {
                for(int i=1; i<Chatrooms.size()+1; i++){
                    if (Chatrooms.get(i).contains(clienthandler)) {
                    sendingroom = Chatrooms.get(i);
                    if(!Message.contains("/") && !Message.equals("")) {
                        database.insertIntoOldMessages(formatedMessage, i);
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
            removeClient();
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
    public String register(){
        try {
            String Username;
            String Password;
            while (true) {
                bufferedWriter.write("Please type in your wished Username: ");
                bufferedWriter.newLine();
                bufferedWriter.flush();
                Username = bufferedReader.readLine();
                if (database.UsernameCheck(Username)) {
                    bufferedWriter.write("Username is available!");
                    bufferedWriter.newLine();
                    bufferedWriter.flush();
                    break;
                } else if (Username.equals("")) {
                    bufferedWriter.write("Please enter a Username!");
                    bufferedWriter.newLine();
                    bufferedWriter.flush();
                } else {
                    bufferedWriter.write("Username is already taken! Please try it again!");
                    bufferedWriter.newLine();
                    bufferedWriter.flush();
                }
            }
            while (true) {
                bufferedWriter.write("Please type in your wished Password: ");
                bufferedWriter.newLine();
                bufferedWriter.flush();
                Password = bufferedReader.readLine();
                if (Password.equals("")) {
                    bufferedWriter.write("Please enter a Password!");
                    bufferedWriter.newLine();
                    bufferedWriter.flush();
                } else {
                    break;
                }
            }
                database.insertIntoUserHandling(Username, Password);
                bufferedWriter.write("You register is completed!");
                bufferedWriter.newLine();
                bufferedWriter.flush();

            return Username;
        } catch (IOException e) {
            removeClient();
            return null;
        }

    }
    public String Login(){
        try {
            String Username;
            String Password;
            while (true) {
                bufferedWriter.write("Please type in your  Username: ");
                bufferedWriter.newLine();
                bufferedWriter.flush();
                 Username=bufferedReader.readLine();
                if(!database.UsernameCheck(Username)){
                    break;
                } else {
                    bufferedWriter.write("There is no User with a Username: " + Username + "! Please try it again!");
                    bufferedWriter.newLine();
                    bufferedWriter.flush();
                }
            }
            while (true){
                bufferedWriter.write("Please type in your  Password: ");
                bufferedWriter.newLine();
                bufferedWriter.flush();
                Password=bufferedReader.readLine();
                if (database.PasswordCheck(Username, Password)){
                    bufferedWriter.write("Login successful");
                    bufferedWriter.newLine();
                    bufferedWriter.flush();
                    break;
                } else {
                    bufferedWriter.write("Password is wrong! Please try it again");
                    bufferedWriter.newLine();
                    bufferedWriter.flush();
                }
            }
            return Username;
        } catch (IOException e) {
            removeClient();
            return null;
        }
    }
    public void LoginOrRegister(){
        String auswahl;
        try {
            while (true) {
                bufferedWriter.write("Please choose if you want to login or register! For Login 1 and for Register 2, x for leaving the ChatProgramm");
                bufferedWriter.newLine();
                bufferedWriter.flush();
                auswahl = bufferedReader.readLine();
                if(auswahl.equals("1")){
                this.clientUsername = Login();
                break;
                } else if (auswahl.equals("2")) {
                    this.clientUsername = register();
                    break;
                } else if (auswahl.equalsIgnoreCase("x")) {
                    removeClient();
                    break;
                }
            }
        } catch (IOException e) {
            removeClient();
        }
    }
}


