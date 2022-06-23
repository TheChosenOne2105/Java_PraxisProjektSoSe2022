
import java.io.*;
import java.net.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.UUID;

public class ClientHandler extends Thread {
    //Basierend auf dem Code von diesem Tutorial: https://www.youtube.com/watch?v=cRfsUrU3RjE
    //Basierend auf dem Code von diesem Tutorial: https://www.youtube.com/watch?v=gLfuZrrfKes
    final Socket client;
    public static ArrayList<ClientHandler> serverliste = new ArrayList<>();
    public static ArrayList<ClientHandler> Chatroom1 = new ArrayList<>();
    public static ArrayList<ClientHandler> Chatroom2 = new ArrayList<>();
    public static ArrayList<ClientHandler> Chatroom3 = new ArrayList<>();
    public static ArrayList<ClientHandler> Chatroom4 = new ArrayList<>();
    public static ArrayList<ClientHandler> Chatroom5 = new ArrayList<>();
    public static HashMap <Integer , ArrayList<ClientHandler>> Chatrooms = new HashMap<>();


    private String clientUsername;
    private String UniqueID;
    Database database = new Database();
    private BufferedWriter bufferedWriter;
    private BufferedReader bufferedReader;

    public ClientHandler(Socket ClientSocket) {
        this.client = ClientSocket;


    }

    @Override
    public void run() {
        try {
            database.DbStart();
            this.bufferedReader = new BufferedReader(new InputStreamReader(client.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new PrintWriter(client.getOutputStream()));
            LoginOrRegister();
            this.UniqueID = CreatingUniqueID();
            AssigningChatrooms();
            if (CheckIfUserInstanceIsAlreadyOnServer(clientUsername)){
                BroadcastServerMessage(clientUsername + " has entered the Chat!");
            }
            serverliste.add(this);
            changeChatroom();
            Handler(client);
        } catch (IOException e) {
            RemoveClient();
        }
    }

    public void Handler(Socket clientSocket) {
        String clientMessage;

        while (clientSocket.isConnected()) {
            try {
                clientMessage = bufferedReader.readLine();
                sendingMessages(clientMessage);

                switch (clientMessage.toLowerCase()) {
                    case "/showmembers" -> ShowClients();
                    case "/changechatroom" -> changeChatroom();
                    case "/emojis" -> {
                        bufferedWriter.write("Server: You can send Emojis if you press the Windows-Key and the dot-key at the same time 😊");
                        bufferedWriter.newLine();
                        bufferedWriter.flush();
                    }
                    case "/help" -> {
                        bufferedWriter.write("Server :/changeChatroom to change the Chatroom, you are current in!  You can send Emojis if you press the Windows-Key and the dot-key at the same time \uD83D\uDE0A\" ");
                        bufferedWriter.newLine();
                        bufferedWriter.flush();
                        bufferedWriter.write("Server: /showmembers to display all users on the Server and in which Chatroom they are!");
                        bufferedWriter.newLine();
                        bufferedWriter.flush();
                        bufferedWriter.write("Server: You can send Emojis if you press the Windows-Key and the dot-key at the same time 😊 ");
                        bufferedWriter.newLine();
                        bufferedWriter.flush();
                    }
                    default -> {
                    }
                }


            } catch (IOException e) {
                RemoveClient();
                break;
            }


        }
        BroadcastServerMessage(clientUsername + " has left the chat");
        RemoveClient();

    }


    public void sendingMessages(String Message) {
        DateTimeFormatter DateTimeFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        String time = LocalDateTime.now().format(DateTimeFormat);
        Database database = new Database();
        String FormattedMessage = clientUsername + ": " + Message;
        String FinalizedFormattedMessage = "[" + time+ "]"+ clientUsername + ": " + Message;
        ArrayList<ClientHandler> SendingRoom = serverliste;
        for (ClientHandler clienthandler : serverliste) {
            if (clienthandler.UniqueID.equals(UniqueID)) {
                for(int i=1; i<Chatrooms.size()+1; i++){
                    if (Chatrooms.get(i).contains(clienthandler)) {
                    SendingRoom = Chatrooms.get(i);
                    if(!Message.contains("/") && !Message.equals("")) {
                        database.insertIntoOldMessages(FormattedMessage, i);
                    }
                }
                }
            }
        }
        try {
            for (ClientHandler clienthandler : SendingRoom) {
                if (Message.equalsIgnoreCase("")) {
                } else if (!clienthandler.UniqueID.equals(UniqueID) && !Message.contains("/")) {
                    clienthandler.bufferedWriter.write(FinalizedFormattedMessage);
                    clienthandler.bufferedWriter.newLine();
                    clienthandler.bufferedWriter.flush();
                } else if (Message.equalsIgnoreCase("/showmembers") || Message.equalsIgnoreCase("/quit") || Message.equalsIgnoreCase("/changeChatroom") || Message.equalsIgnoreCase("/emojis") || Message.equalsIgnoreCase("/help")) {
                } else if (clienthandler.UniqueID.equals(UniqueID) && Message.contains("/")) {
                    clienthandler.bufferedWriter.write("Dieser Befehl ist uns leider unbekannt! Bitte versuchen Sie es erneut.");
                    clienthandler.bufferedWriter.newLine();
                    clienthandler.bufferedWriter.flush();
                }
            }
        } catch (IOException e) {
            RemoveClient();

        }

    }

    public void RemoveClient() {
        try {
            serverliste.remove(this);
            for (int i = 1; i < Chatrooms.size()+1; i++) {
                if (Chatrooms.get(i).contains(this)){
                    Chatrooms.get(i).remove(this);
                    BroadcastServerMessage(clientUsername + " has left Chatroom " + i + "!");
                    break;
                }
            }
            if (this.bufferedWriter != null) {
                this.bufferedWriter.close();
            }
            if (this.bufferedReader != null) {
                this.bufferedReader.close();
            }
            if (this.client != null) {
                this.client.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void ShowClients() {
        try {
            for (ClientHandler clienthandler : serverliste) {
                if (clienthandler.UniqueID.equals(UniqueID)) {
                    bufferedWriter.write("Current Users on this Server: ");
                    bufferedWriter.newLine();
                    bufferedWriter.flush();
                    bufferedWriter.write(clientUsername);
                    bufferedWriter.newLine();
                    bufferedWriter.flush();
                    for (ClientHandler clientHandler1 : serverliste) {
                        if(!clientHandler1.clientUsername.equals(clientUsername)) {
                            bufferedWriter.write(clientHandler1.clientUsername);
                            bufferedWriter.newLine();
                            bufferedWriter.flush();
                        }
                    }
                    for(int i=1; i < Chatrooms.size()+1; i++){
                        bufferedWriter.write("Current Users in Chatroom " + i);
                        bufferedWriter.newLine();
                        bufferedWriter.flush();
                        for(ClientHandler clientHandler2 : Chatrooms.get(i)){
                            bufferedWriter.write(clientHandler2.clientUsername);
                            bufferedWriter.newLine();
                            bufferedWriter.flush();
                        }
                    }
                }

            }
        } catch (IOException e) {
            RemoveClient();
        }
    }

    public void BroadcastServerMessage(String Message) {
        String FormattedMessage = "Server: " + Message;
        for (ClientHandler clienthandler : serverliste) {
            try {
                clienthandler.bufferedWriter.write(FormattedMessage);
                clienthandler.bufferedWriter.newLine();
                clienthandler.bufferedWriter.flush();
            } catch (IOException e) {
                RemoveClient();
                break;
            }
        }
    }

    public void changeChatroom() {
        Database database = new Database();
        boolean Check = false;
        int auswahl = 0;
        try {
            for (ClientHandler clienthandler : serverliste) {
                if( clienthandler.clientUsername == null){
                    RemoveClient();
                    break;
                }
                if (clienthandler.UniqueID.equals(UniqueID)) {
                    bufferedWriter.write("Please enter Number of the Chatroom(1-5) that you want to go in or x if you want to remain in your current chatroom: " );
                    bufferedWriter.newLine();
                    bufferedWriter.flush();
                    String insert = clienthandler.bufferedReader.readLine();
                    for (int i = 1; i < Chatrooms.size()+1; i++) {
                        if (insert.equals(Integer.toString(i))) {
                            if (Chatrooms.get(i).contains(clienthandler) ||CheckIfUsernameIsAlreadyInChatroom(i, clientUsername)) {
                                bufferedWriter.write("You or another instance of your Account are already in Chatroom "+ i + " !");
                                bufferedWriter.newLine();
                                bufferedWriter.flush();
                                Check = true;
                                break;
                            } else {
                                Chatrooms.get(i).add(clienthandler);
                                BroadcastServerMessage(clienthandler.clientUsername + " joined Chatroom " + i);
                                ArrayList<String> LoadedMessages = database.LoadingOldMessages(i);

                                Collections.reverse(LoadedMessages);

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
                                Check = true;
                            }
                        } }
                        if (insert.equalsIgnoreCase("x")) {
                            bufferedWriter.write("You choosed to stay in your current room! Please type /changeChatroom if you changed your Mind!");
                            bufferedWriter.newLine();
                            bufferedWriter.flush();
                            break;
                        } else if (!Check && !insert.equalsIgnoreCase("x") ) {
                            bufferedWriter.write("Error! A Chatroom like that don't exist on this Server. We only have: Chatroom 1, Chatroom 2, Chatroom 3, Chatroom 4, Chatroom 5");
                            bufferedWriter.newLine();
                            bufferedWriter.flush();
                            break;
                        }

                    if(auswahl > 0){
                        for (int i = 1; i < Chatrooms.size()+1; i++){
                            if(i != auswahl && Chatrooms.get(i).contains(clienthandler)){
                                BroadcastServerMessage(clienthandler.clientUsername + " has left Chatroom " + i);
                                Chatrooms.get(i).remove(clienthandler);
                                break;
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            RemoveClient();
        }

    }
    public void AssigningChatrooms() throws IOException {
        if(!(Chatrooms.containsValue(Chatroom1) || Chatrooms.containsValue(Chatroom2) || Chatrooms.containsValue(Chatroom3) || Chatrooms.containsValue(Chatroom4) || Chatrooms.containsValue(Chatroom5) || Chatrooms.containsValue(serverliste))){
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
            RemoveClient();
            return null;
        }

    }
    public String Login(){
        try {
            String Username;
            String Password;
            while (true) {
                bufferedWriter.write("Please type in your Username: ");
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
                bufferedWriter.write("Please type in your Password: ");
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
            RemoveClient();
            return null;
        }
    }
    public void LoginOrRegister(){
        String auswahl;
        try {
            bufferedWriter.write("Successfully connected to Speedchat!");
            bufferedWriter.newLine();
            bufferedWriter.flush();
            while (true) {
                bufferedWriter.write("Please choose if you want to login or register! For Login 1 and for Register 2");
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
                    RemoveClient();
                    break;
                }
            }
        } catch (IOException e) {
            RemoveClient();
        }
    }
    private String CreatingUniqueID(){
        return UUID.randomUUID().toString();
    }
    private boolean CheckIfUsernameIsAlreadyInChatroom(Integer NumberOfTheChatroom, String UserNameToCheck){
        boolean Check = false;
        for (ClientHandler clientHandler:Chatrooms.get(NumberOfTheChatroom)){
            if (clientHandler.clientUsername.equals(UserNameToCheck)){
                Check = true;
                break;
            }
        }
        return Check;
    }
    private boolean CheckIfUserInstanceIsAlreadyOnServer(String UserNameToCheck){
        boolean Check = true;
        for(ClientHandler clientHandler : serverliste){
            if (clientHandler.clientUsername.equals(UserNameToCheck)){
                Check = false;
                break;
            }
        }
        return Check;
    }
}


