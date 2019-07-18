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
    private Socket clientSocket;
    private Scanner in;
    private PrintWriter out;

    private JTextField jtfMessage;
    private JTextField jtfName;
    private JTextArea jtaAreaMessage;

    private String clientName = "";

    public String getClientName() {
        return this.clientName;
    }

    public ClientWindow() {
        try {
            clientSocket = new Socket(HOST, PORT);
            in = new Scanner(clientSocket.getInputStream());
            out = new PrintWriter(clientSocket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        setBounds(600, 300, 600, 500);
        setTitle("Client");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        jtaAreaMessage = new JTextArea();
        jtaAreaMessage.setEditable(false);
        jtaAreaMessage.setLineWrap(true);

        JScrollPane jsp = new JScrollPane(jtaAreaMessage);
        add(jsp, BorderLayout.CENTER);

        JLabel jlNumberOfClients = new JLabel("Number of clients: ");
        add(jlNumberOfClients, BorderLayout.NORTH);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        add(bottomPanel, BorderLayout.SOUTH);

        JButton jbSendMessage = new JButton("Send");
        bottomPanel.add(jbSendMessage, BorderLayout.EAST);

        jtfMessage = new JTextField("Enter your message: ");
        bottomPanel.add(jtfMessage, BorderLayout.CENTER);

        jtfName = new JTextField("Enter your name:");
        bottomPanel.add(jtfName, BorderLayout.WEST);

        jbSendMessage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // если имя клиента, и сообщение непустые, то отправляем сообщение
                if (!jtfMessage.getText().trim().isEmpty() && !jtfName.getText().trim().isEmpty()) {
                    clientName = jtfName.getText();
                    sendMsg();
                    // фокус на текстовое поле с сообщением
                    jtfMessage.grabFocus();
                }
            }
        });

        jtfMessage.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                jtfMessage.setText("");
            }
        });

        jtfName.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (jtfName.getText().equals("Enter your name:")) {
                    jtfName.setText("");
                }
            }
        });

        new Thread(new Runnable() {
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

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                try {
                    if (!clientName.isEmpty() && !clientName.equals("Enter your name:")){
                        out.println(clientName + " leave the chat.");
                    } else {
                        out.println("Anonym leave the chat.");
                    }
                    out.println("/exit");
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
