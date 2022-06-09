import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

//Basierend auf dem Code von diesem Tutorial: https://www.youtube.com/watch?v=gLfuZrrfKes
public class Client {


    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;


    public Client(Socket socket){
        try {
            this.socket = socket;
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter= new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {

            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }


    public void sendMessage(Scanner scanner, String username) {
        try {
            bufferedWriter.write(username);
            bufferedWriter.newLine();
            bufferedWriter.flush();

            while (socket.isConnected()) {
                String messageToSend = scanner.nextLine();
                bufferedWriter.write(messageToSend);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
            closeEverything(socket, bufferedReader, bufferedWriter);
        } catch (IOException e) {

            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }


    public void listenForMessage() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String msgFromGroupChat;

                while (socket.isConnected()) {
                    try {


                        msgFromGroupChat = bufferedReader.readLine();
                        if(msgFromGroupChat == null){
                            closeEverything(socket,bufferedReader, bufferedWriter);
                        }
                        else{
                            System.out.println(msgFromGroupChat);
                        }
                    } catch (IOException e) {

                        closeEverything(socket, bufferedReader, bufferedWriter);
                    }
                }
                closeEverything(socket, bufferedReader, bufferedWriter);
            }
        }).start();
    }


    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {

        try {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Run the program.
    public static void main(String[] args) {
        try {
            Scanner sc = new Scanner(System.in);
            System.out.println("Enter your username for the group chat: ");
            String username = sc.nextLine();
            Client client = new Client(new Socket("localhost", 1833));
            System.out.println("Succesfully logged into SpeedChat");

            client.listenForMessage();
            client.sendMessage(sc, username);
        } catch (UnknownHostException e) {
            System.out.println("Server is unreachable");
        } catch (IOException e) {
            System.out.println("An Error occurred, please try it again later! Maybe check if the Server is online!");
        }
    }
}