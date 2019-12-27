import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class ClientHandler extends Thread {

    DatagramSocket socket;
    Connection userConnection;
    private boolean running = false;
    private boolean first = true;

    public ClientHandler(String name, DatagramSocket socket, Connection connection) throws IOException {
        super(name);
        this.socket = socket;
        this.userConnection = connection;
        System.out.println("Client id: " + getName());
        DatagramPacket sendVerification = Utility.createPacket(getName().getBytes(), connection);
        socket.send(sendVerification);
    }

    @Override
    public synchronized void start() {
        running = true;
        super.start();
    }

    @Override
    public void run() {
        System.out.println("ClientHandler thread started");
        byte[] sendData = new byte[65535];
        DatagramPacket sendPacket = null;

        String message1 = "Hello";
        String message2 = "darkness";
        String message3 = "my old friend";

        // The main server should receive the packets, and only be responsible for sending packets to clients
        while(running) {
            try {
                sendData = message1.getBytes();
                sendPacket = createPacket(sendData, userConnection);
                socket.send(sendPacket);
                sendData = new byte[65535];

                sendData = message2.getBytes();
                sendPacket = createPacket(sendData, userConnection);
                socket.send(sendPacket);
                sendData = new byte[65535];

                sendData = message3.getBytes();
                sendPacket = createPacket(sendData, userConnection);
                sendData = new byte[65535];

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public DatagramPacket createPacket(byte[] message, Connection connection) {
        return new DatagramPacket(message, message.length, connection.getIp(), connection.getPort());
    }
}
