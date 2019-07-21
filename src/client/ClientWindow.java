package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ClientWindow extends JFrame {
    private static final String HOST = "localhost";
    private static final int PORT = 1234;
    private Socket clientSocket;        // client socket
    private Scanner in;                 // for input stream
    private PrintWriter out;            // for output stream

    private JTextField jtfMessage;      // field for message
    private JTextField jtfName;         // field for client's name
    private JTextArea jtaAreaMessage;   // area for messages

    private String clientName = "";     // client's name

    /*public String getClientName() {
        return this.clientName;
    }*/

    public ClientWindow() {
        try {
            clientSocket = new Socket(HOST, PORT);                  // creating socket
            in = new Scanner(clientSocket.getInputStream());        // creating input stream
            out = new PrintWriter(clientSocket.getOutputStream());  // creating output stream
        } catch (IOException e) {
            e.printStackTrace();
        }

        setBounds(600, 300, 600, 500);          // size of window
        setTitle("Client");                                         // name of window
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        jtaAreaMessage = new JTextArea();
        jtaAreaMessage.setEditable(false);
        jtaAreaMessage.setLineWrap(true);

        JScrollPane jsp = new JScrollPane(jtaAreaMessage);          // scroll for messages' area
        add(jsp, BorderLayout.CENTER);

        JLabel jlNumberOfClients = new JLabel("Number of clients: "); // label for counting number of clients
        add(jlNumberOfClients, BorderLayout.NORTH);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        add(bottomPanel, BorderLayout.SOUTH);

        JButton jbSendMessage = new JButton("Send");           // button for sending messages
        bottomPanel.add(jbSendMessage, BorderLayout.EAST);

        jtfMessage = new JTextField("Enter your message: ");        // area for entering message
        bottomPanel.add(jtfMessage, BorderLayout.CENTER);

        jtfName = new JTextField("Enter your name:");               // area for entering name of client
        bottomPanel.add(jtfName, BorderLayout.WEST);

        jbSendMessage.addActionListener(new ActionListener() {      // listener for button "send"
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!jtfMessage.getText().isEmpty() && !jtfName.getText().isEmpty()) {
                    clientName = jtfName.getText();
                    sendMsg();
                    jtfMessage.grabFocus();
                }
            }
        });

        jtfMessage.addFocusListener(new FocusAdapter() {            // set message area empty
            @Override
            public void focusGained(FocusEvent e) {
                jtfMessage.setText("");
            }
        });

        jtfName.addFocusListener(new FocusAdapter() {               // set name area empty
            @Override
            public void focusGained(FocusEvent e) {
                if (jtfName.getText().equals("Enter your name:")) {
                    jtfName.setText("");
                }
            }
        });

        new Thread(new Runnable() {                                 // creating new Thread for client
            @Override
            public void run() {
                try{
                    while(true) {
                        if (in.hasNext()) {
                            String msg = in.nextLine();
                            String numberOfClients = "Number of clients: ";
                            if (msg.indexOf(numberOfClients) == 0) {
                                jlNumberOfClients.setText(msg);
                            } else {
                                jtaAreaMessage.append(msg);
                                jtaAreaMessage.append("\n");
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

        addWindowListener(new WindowAdapter() {                     // listener for closing window
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                try {
                    if (!clientName.isEmpty() && !clientName.equals("Enter your name:")){
                        out.println(clientName + " leave the chat.");
                    } else {
                        out.println("Anonym leave the chat.");
                    }
                    out.println("exit");
                    out.flush();
                    out.close();
                    in.close();
                    clientSocket.close();

                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        setVisible(true);
    }

    private void sendMsg() {
        String msg = jtfName.getText() + ": " + jtfMessage.getText();
        out.println(msg);
        out.flush();
        jtfMessage.setText("");
    }
}
