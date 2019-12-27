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
        String message = "Your id: " + getName();
        DatagramPacket sendVerification = new DatagramPacket(message.getBytes(), message.getBytes().length,
                connection.getIp(), connection.getPort());
        socket.send(sendVerification);
    }

    @Override
    public synchronized void start() {
        running = true;
        super.start();
    }

    @Override
    public void run() {
        System.out.println("ClientListener thread started");
        byte[] receiveData = new byte[65535];
        DatagramPacket receivePacket = null;

        while(running) {
            // Step 2 : create a DatgramPacket to receive the data.
            receivePacket = new DatagramPacket(receiveData, receiveData.length);

            // Step 3 : receive the data in byte buffer.
            try {
                socket.receive(receivePacket);
                Connection clientConnection = new Connection(receivePacket.getAddress(), receivePacket.getPort());

                if(first) {
                    socket.connect(clientConnection.getIp(), clientConnection.getPort());
                    first = false;
                }

                String message = new String(receivePacket.getData());
                System.out.println("From " + super.getName() + ": " + message);

                // Exit the server (later, close the thread) when the user sends exit.
                if(message.trim().toLowerCase().equals("exit")) {
                    System.out.println("Client exitted. Closing connection.");
                    running = false;
                    socket.disconnect();
                    break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Step 4 : clear the buffer after every message.
            receiveData = new byte[65535];
        }
    }
}
