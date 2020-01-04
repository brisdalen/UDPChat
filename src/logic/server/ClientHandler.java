package logic.server;

import logic.Connection;
import logic.CustomThread;
import logic.Utility;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class ClientHandler extends CustomThread {

    DatagramSocket socket;
    Connection userConnection;

    public ClientHandler(String name, DatagramSocket socket, Connection connection) throws IOException {
        super(name);
        this.socket = socket;
        this.userConnection = connection;
        System.out.println("[logic.server.ClientHandler]Client id: " + getName());
        DatagramPacket sendVerification = Utility.createPacket(getName().getBytes(), connection);
        socket.send(sendVerification);
    }

    public synchronized void sendMessageToUser(String message) {
        if(userConnection != null) {
            sendPacket(message);
        }
    }

    public void sendPacket(String message) {
        //System.out.println("[logic.server.ClientHandler]sendPacket(): " + message);
        DatagramPacket sendMessage = Utility.createPacket(message.getBytes(), userConnection);
        try {
            socket.send(sendMessage);
            System.out.println("[logic.server.ClientHandler]packet sent to ip: " + sendMessage.getAddress() + " port: " + sendMessage.getPort());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void stopThread() {
        super.stopThread();
        System.out.println(getName() + " stopped.");
    }

    @Override
    public void run() {
        System.out.println("[logic.server.ClientHandler]logic.server.ClientHandler thread started");
        byte[] sendData = new byte[65535];
        DatagramPacket sendPacket = null;

        // The main server should receive the packets, and the handler should only be responsible for sending packets to clients
        while(running) {

        }
    }

}
