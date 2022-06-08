import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class Clienthandler extends Thread {
    //Inspiration durch dieses YouTube Video von Jim Liao : https://www.youtube.com/watch?v=cRfsUrU3RjE
    //Inspiration durch dieses YouTube Video: https://www.youtube.com/watch?v=gLfuZrrfKes
    final Socket client;
    public static ArrayList<Clienthandler> serverliste = new ArrayList<>();
    public static ArrayList<Clienthandler> Chatroom1 = new ArrayList<>();
    public static ArrayList<Clienthandler> Chatroom2 = new ArrayList<>();
    public static ArrayList<Clienthandler> Chatroom3 = new ArrayList<>();
    public static ArrayList<Clienthandler> Chatroom4 = new ArrayList<>();
    public static ArrayList<Clienthandler> Chatroom5 = new ArrayList<>();
    public static ArrayList<ArrayList<Clienthandler>> Chatrooms = new ArrayList<>();


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
        String formatedMessage = clientUsername + ": " + Message;
        ArrayList<Clienthandler> sendingroom = serverliste;
        for (Clienthandler clienthandler : serverliste) {
            if (clienthandler.clientUsername.equals(clientUsername)) {
                if (Chatroom1.contains(clienthandler)) {
                    sendingroom = Chatroom1;
                } else if (Chatroom2.contains(clienthandler)) {
                    sendingroom = Chatroom2;
                } else if (Chatroom3.contains(clienthandler)) {
                    sendingroom = Chatroom3;
                } else if (Chatroom4.contains(clienthandler)) {
                    sendingroom = Chatroom4;
                } else if (Chatroom5.contains(clienthandler)) {
                    sendingroom = Chatroom5;
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
        boolean CR1 = false;
        boolean CR2 = false;
        boolean CR3 = false;
        boolean CR4 = false;
        boolean CR5 = false;
        try {
            for (Clienthandler clienthandler : serverliste) {
                if (clienthandler.clientUsername.equals(clientUsername)) {
                    bufferedWriter.write("Please enter Number of the Chatroom(1-5) that you want to go in: ");
                    bufferedWriter.newLine();
                    bufferedWriter.flush();
                    String insert = clienthandler.bufferedReader.readLine();
                    if (insert.equals("1")) {
                        if (Chatroom1.contains(clienthandler)) {
                            bufferedWriter.write("You are already in Chatroom 1!");
                            bufferedWriter.newLine();
                            bufferedWriter.flush();
                            break;
                        } else {
                            Chatroom1.add(clienthandler);
                            broadcastServerMessage(clienthandler.clientUsername + " joined Chatroom 1");
                            CR1 = true;
                        }
                    } else if (insert.equals("2")) {
                        if (Chatroom2.contains(clienthandler)) {
                            bufferedWriter.write("You are already in Chatroom 2!");
                            bufferedWriter.newLine();
                            bufferedWriter.flush();
                            break;
                        } else {
                            Chatroom2.add(clienthandler);
                            broadcastServerMessage(clienthandler.clientUsername + " joined Chatroom 2");
                            CR2 = true;
                        }
                    } else if (insert.equals("3")) {
                        if (Chatroom3.contains(clienthandler)) {
                            bufferedWriter.write("You are already in Chatroom 3!");
                            bufferedWriter.newLine();
                            bufferedWriter.flush();
                            break;
                        } else {
                            Chatroom3.add(clienthandler);
                            broadcastServerMessage(clienthandler.clientUsername + " joined Chatroom 3");
                            CR3 = true;
                        }
                        ;
                    } else if (insert.equals("4")) {
                        if (Chatroom4.contains(clienthandler)) {
                            bufferedWriter.write("You are already in Chatroom 4!");
                            bufferedWriter.newLine();
                            bufferedWriter.flush();
                            break;
                        } else {
                            Chatroom4.add(clienthandler);
                            broadcastServerMessage(clienthandler.clientUsername + " joined Chatroom 4");
                            CR4 = true;
                        }
                    } else if (insert.equals("5")) {
                        if (Chatroom5.contains(clienthandler)) {
                            bufferedWriter.write("You are already in Chatroom 5!");
                            bufferedWriter.newLine();
                            bufferedWriter.flush();
                            break;
                        } else {
                            Chatroom5.add(clienthandler);
                            broadcastServerMessage(clienthandler.clientUsername + " joined Chatroom 5");
                            CR5 = true;
                        }
                    } else if (insert.equalsIgnoreCase("x")) {
                        bufferedWriter.write("You choosed to stay in your current room! Please type /changeChatroom if you changed your Mind!");
                        bufferedWriter.newLine();
                        bufferedWriter.flush();
                        break;
                    } else {
                        bufferedWriter.write("Error! A Chatroom like that don't exist on this Server. We only have: Chatroom 1, Chatroom 2, Chatroom 3, Chatroom 4, Chatroom 5");
                        bufferedWriter.newLine();
                        bufferedWriter.flush();
                        break;
                    }
                    if (CR1) {
                        if (Chatroom2.contains(clienthandler)) {
                            broadcastServerMessage(clienthandler.clientUsername + " has left Chatroom 2");
                            Chatroom2.remove(clienthandler);
                        } else if (Chatroom3.contains(clienthandler)) {
                            broadcastServerMessage(clienthandler.clientUsername + " has left Chatroom 3");
                            Chatroom3.remove(clienthandler);
                        } else if (Chatroom4.contains(clienthandler)) {
                            broadcastServerMessage(clienthandler.clientUsername + " has left Chatroom 4");
                            Chatroom4.remove(clienthandler);
                        } else if (Chatroom5.contains(clienthandler)) {
                            broadcastServerMessage(clienthandler.clientUsername + " has left Chatroom 5");
                            Chatroom5.remove(clienthandler);
                        }
                    }
                    if (CR2) {
                        if (Chatroom1.contains(clienthandler)) {
                            broadcastServerMessage(clienthandler.clientUsername + " has left Chatroom 1");
                            Chatroom1.remove(clienthandler);
                        } else if (Chatroom3.contains(clienthandler)) {
                            broadcastServerMessage(clienthandler.clientUsername + " has left Chatroom 3");
                            Chatroom3.remove(clienthandler);
                        } else if (Chatroom4.contains(clienthandler)) {
                            broadcastServerMessage(clienthandler.clientUsername + " has left Chatroom 4");
                            Chatroom4.remove(clienthandler);
                        } else if (Chatroom5.contains(clienthandler)) {
                            broadcastServerMessage(clienthandler.clientUsername + " has left Chatroom 5");
                            Chatroom5.remove(clienthandler);
                        }
                    }
                    if (CR3) {
                        if (Chatroom2.contains(clienthandler)) {
                            broadcastServerMessage(clienthandler.clientUsername + " has left Chatroom 2");
                            Chatroom2.remove(clienthandler);
                        } else if (Chatroom1.contains(clienthandler)) {
                            broadcastServerMessage(clienthandler.clientUsername + " has left Chatroom 1");
                            Chatroom1.remove(clienthandler);
                        } else if (Chatroom4.contains(clienthandler)) {
                            broadcastServerMessage(clienthandler.clientUsername + " has left Chatroom 4");
                            Chatroom4.remove(clienthandler);
                        } else if (Chatroom5.contains(clienthandler)) {
                            broadcastServerMessage(clienthandler.clientUsername + " has left Chatroom 5");
                            Chatroom5.remove(clienthandler);
                        }
                    }
                    if (CR4) {
                        if (Chatroom2.contains(clienthandler)) {
                            broadcastServerMessage(clienthandler.clientUsername + " has left Chatroom 2");
                            Chatroom2.remove(clienthandler);
                        } else if (Chatroom3.contains(clienthandler)) {
                            broadcastServerMessage(clienthandler.clientUsername + " has left Chatroom 3");
                            Chatroom3.remove(clienthandler);
                        } else if (Chatroom1.contains(clienthandler)) {
                            broadcastServerMessage(clienthandler.clientUsername + " has left Chatroom 1");
                            Chatroom1.remove(clienthandler);
                        } else if (Chatroom5.contains(clienthandler)) {
                            broadcastServerMessage(clienthandler.clientUsername + " has left Chatroom 5");
                            Chatroom5.remove(clienthandler);
                        }
                    }
                    if (CR5) {
                        if (Chatroom2.contains(clienthandler)) {
                            broadcastServerMessage(clienthandler.clientUsername + " has left Chatroom 2");
                            Chatroom2.remove(clienthandler);
                        } else if (Chatroom3.contains(clienthandler)) {
                            broadcastServerMessage(clienthandler.clientUsername + " has left Chatroom 3");
                            Chatroom3.remove(clienthandler);
                        } else if (Chatroom4.contains(clienthandler)) {
                            broadcastServerMessage(clienthandler.clientUsername + " has left Chatroom 4");
                            Chatroom4.remove(clienthandler);
                        } else if (Chatroom1.contains(clienthandler)) {
                            broadcastServerMessage(clienthandler.clientUsername + " has left Chatroom 1");
                            Chatroom1.remove(clienthandler);

                        }

                    }


                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}


