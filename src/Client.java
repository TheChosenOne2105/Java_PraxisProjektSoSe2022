import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

//Basierend auf dem Code von diesem Tutorial: https://www.youtube.com/watch?v=gLfuZrrfKes
public class Client {
    private JButton Send;
    private JTextArea textArea2;
    private JTextField typeMessage;
    private JPanel Panel;

    static ImageIcon image= new ImageIcon("src/default/SpeedchatIcon.jpeg");
    static JFrame frame= new JFrame();
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;


    public Client(Socket socket){
        try {
            this.socket = socket;
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter= new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            textArea2.setEditable(false);
            typeMessage.addKeyListener(new KeyListener() {
                @Override
                public void keyTyped(KeyEvent e) {

                }
                @Override
                public void keyPressed(KeyEvent e) {

                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        textArea2.append(typeMessage.getText());
                        String messageToSend = typeMessage.getText();
                        try {
                            textArea2.append("\n");
                            bufferedWriter.write(messageToSend);
                            bufferedWriter.newLine();
                            bufferedWriter.flush();
                            typeMessage.setText("");
                        } catch (IOException ex) {
                            CloseClient(socket, bufferedReader, bufferedWriter);
                        }
                    }
                }

                @Override
                public void keyReleased(KeyEvent e) {

                }
            });
            Send.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (e.getSource() == Send) {
                        textArea2.append(typeMessage.getText());
                        String messageToSend = typeMessage.getText();
                        try {
                            textArea2.append("\n");
                            bufferedWriter.write(messageToSend);
                            bufferedWriter.newLine();
                            bufferedWriter.flush();
                            typeMessage.setText("");
                        } catch (IOException ex) {
                            CloseClient(socket, bufferedReader, bufferedWriter);
                        }
                    }


                }
            });
        } catch (IOException e) {
            CloseClient(socket, bufferedReader, bufferedWriter);
        }
    }
    public void Frame() {
        frame.setSize(500, 500);
        frame.setContentPane(Panel);
        frame.setIconImage(image.getImage());
        frame.setVisible(true);
        frame.setTitle("SpeedChat");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        if(frame == null){
            CloseClient(socket, bufferedReader, bufferedWriter);
        }

    }


    public void ListenForMessages() {
        new Thread(new Runnable() {
            @Override
            public void run() {


                while (socket.isConnected()) {
                    try {
                        textArea2.append(bufferedReader.readLine());
                        textArea2.append("\n");
                        }
                    catch (IOException e) {

                        CloseClient(socket, bufferedReader, bufferedWriter);
                    }
                }
                CloseClient(socket, bufferedReader, bufferedWriter);
            }
        }).start();
    }


    public void CloseClient(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {

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

            Client client = new Client(new Socket("localhost", 1833));
            client.Frame();
            client.ListenForMessages();
        } catch (UnknownHostException e) {
            System.out.println("Server is unreachable");
        } catch (IOException e) {
            System.out.println("An Error occurred, please try it again later! Maybe check if the Server is online!");
        }
    }
}