import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GUI {
    private JButton Send;
    private JTextArea textArea2;
    private JTextField typeMessage;
    private JPanel Panel;

    static ImageIcon image= new ImageIcon("src/default/SpeedchatIcon.jpeg");
    static JFrame frame= new JFrame();
    private String message;

    public static void main(String[] args) {
        frame();
    }

    GUI(){
        final String[] message = {""};
    textArea2.setEditable(false);
        Send.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource()== Send){
                    message[0] = typeMessage.toString();
                    textArea2.append(typeMessage.getText());
                    typeMessage.setText("");
                    textArea2.append("\n");
                }
            }
        });
    }

        static void frame(){
        frame.setSize(500,500);
        frame.setContentPane(new GUI().Panel);
        frame.setIconImage(image.getImage());
        frame.setVisible(true);
        frame.setTitle("SpeedChat");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
