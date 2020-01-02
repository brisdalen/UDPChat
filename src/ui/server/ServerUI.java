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

        JTextField target = new JTextField("Target (specific user, $self, or $all)");
        target.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                target.setText("");
            }
        });
        JTextField message = new JTextField("Message");
        message.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                message.setText("");
            }
        });
        JButton sendMessage = new JButton("Click to send message");
        sendMessage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                server.stopServer();
                System.exit(0);
            }
        });
        this.getRootPane().setDefaultButton(sendMessage);

        mainPanel.add(target, BorderLayout.NORTH);
        mainPanel.add(message, BorderLayout.CENTER);
        mainPanel.add(sendMessage, BorderLayout.SOUTH);

        this.add(mainPanel);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

}
