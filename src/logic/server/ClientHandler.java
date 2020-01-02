package logic;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class ClientHandler extends CustomThread {

    DatagramSocket socket;
    Connection userConnection;
    private boolean running = false;

    public ClientHandler(String name, DatagramSocket socket, Connection connection) throws IOException {
        super(name);
        this.socket = socket;
        this.userConnection = connection;
        System.out.println("[logic.ClientHandler]Client id: " + getName());
        DatagramPacket sendVerification = Utility.createPacket(getName().getBytes(), connection);
        socket.send(sendVerification);
    }

    public void sendPacket(String message) {
        //System.out.println("[logic.ClientHandler]sendPacket(): " + message);
        DatagramPacket sendMessage = Utility.createPacket(message.getBytes(), userConnection);
        try {
            socket.send(sendMessage);
            System.out.println("[logic.ClientHandler]packet sent to ip: " + sendMessage.getAddress() + " port: " + sendMessage.getPort());
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
        System.out.println("[logic.ClientHandler]logic.ClientHandler thread started");
        byte[] sendData = new byte[65535];
        DatagramPacket sendPacket = null;

        // The main server should receive the packets, and the handler should only be responsible for sending packets to clients
        while(running) {

        }
    }

    public DatagramPacket createPacket(byte[] message, Connection connection) {
        return new DatagramPacket(message, message.length, connection.getIp(), connection.getPort());
    }
}
