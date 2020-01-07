package ui.server;

import logic.server.UDPBaseServer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ServerUI extends JFrame {

    private UDPBaseServer server;

    public ServerUI(UDPBaseServer server) {
        this(server, 600, 400);
    }

    public ServerUI(UDPBaseServer server, int width, int height) {
        super("GUI test");
        this.setSize(width, height);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.server = server;

        JPanel mainPanel = new JPanel(new BorderLayout());

        JTextField targetField = new JTextField("Target (specific user, $self, or $all)");
        targetField.setToolTipText("Target (specific user, $self, or $all)");
        targetField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                targetField.setText("");
            }
        });
        JTextField messageField = new JTextField("Message");
        messageField.setToolTipText("Enter the message here");
        messageField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                messageField.setText("");
            }
        });
        JButton sendMessage = new JButton("Click to send message");
        sendMessage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String target = targetField.getText();
                String message = messageField.getText();

                // TODO: Bytte til kommandoer istedenfor String-sjekking
                // Sjekker først om det er en $self kommando (og like godt om det er stop server, siden det er den eneste kommandoen som finnes).
                if(target.equals("$self") && message.equals("stop server")) {
                    server.stopServer();
                    System.exit(0);
                    // Så lenge target ikke er all eller self, så sender vi beskjeden til brukeren "target"
                } else if(!(target.equals("$all") || target.equals("$self"))) {
                    server.sendMessageToUser(target, message);
                } else if(target.equals("$all")) {
                    server.sendMessageToAllUsers(message);
                }
            }
        });
        this.getRootPane().setDefaultButton(sendMessage);

        String[] temp = new String[100];
        for(int i = 0; i < 100; i++) {
            temp[i] = "" + i;
        }
        // TODO: Liste over alle clients som er tilkoblet
        JList list = new JList();
        JScrollPane scrollPane = new JScrollPane(list);
        scrollPane.setPreferredSize(new Dimension(120, 0));

        mainPanel.add(targetField, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.EAST);
        mainPanel.add(messageField, BorderLayout.CENTER);
        mainPanel.add(sendMessage, BorderLayout.SOUTH);

        this.add(mainPanel);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        this.requestFocus();
    }

}
