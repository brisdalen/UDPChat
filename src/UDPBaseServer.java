import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class UDPBaseServer extends Thread {

    private InetAddress ip;
    private int port;
    DatagramSocket socket;
    byte[] receivedBytes = new byte[65535];
    DatagramPacket receivedPacket;
    boolean running = false;

    public UDPBaseServer(int port) throws IOException {
        // Step 1 : Create a socket to listen at port 1234
        this.ip = InetAddress.getLocalHost();
        this.port = port;
        this.socket = new DatagramSocket(port, ip);
        System.out.println("Server created at port: " + port + " with ip address: " + ip);
        System.out.println("Waiting for client connection...");
    }

    @Override
    public void run() {
        System.out.println("run() started");
        try {
            receivedPacket = new DatagramPacket(receivedBytes, receivedBytes.length);
            socket.receive(receivedPacket);
            Connection clientConnection = new Connection(receivedPacket.getAddress(), receivedPacket.getPort());
            socket.connect(clientConnection.getIp(), clientConnection.getPort());

            String request = dataToString(receivedBytes);
            if(request.trim().toLowerCase().contains("connect")) {
                String id = Utility.getRandomString(16);
                System.out.println("Client id: " + id);
                String message = "Your id: " + id;
                DatagramPacket sendVerification = new DatagramPacket(message.getBytes(), message.getBytes().length);
                socket.send(sendVerification);
            }

            receivedBytes = new byte[65535];
        } catch (IOException e) {
            e.printStackTrace();
        }

        while(running) {
            // Step 2 : create a DatgramPacket to receive the data.
            receivedPacket = new DatagramPacket(receivedBytes, receivedBytes.length);

            // Step 3 : receive the data in byte buffer.
            try {
                socket.receive(receivedPacket);
                Connection clientConnection = new Connection(receivedPacket.getAddress(), receivedPacket.getPort());

                String receivedString = dataToString(receivedBytes);
                System.out.println("From client: " + receivedString);

                // Exit the server (later, close the thread) when the user sends exit.
                if(receivedString.trim().toLowerCase().equals("exit")) {
                    System.out.println("Client exitted. Closing connection.");
                    break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Step 4 : clear the buffer after every message.
            receivedBytes = new byte[65535];
        }
    }

    private void clearBuffer(byte[] buffer) {
        buffer = new byte[65535];
    }

    private void startServer() {
        running = true;
        this.start();
    }

    private void close() {
        running = false;

        if(socket.isConnected()) {
            socket.disconnect();
        }
    }

    // A utility method to convert the byte array data into a string representation.
    public String dataToString(byte[] a) {
        if (a == null)
            return null;
        StringBuilder ret = new StringBuilder();
        int i = 0;
        while (a[i] != 0)
        {
            ret.append((char) a[i]);
            i++;
        }
        return ret.toString();
    }

    public static void main(String[] args) {
        try {
            UDPBaseServer server = new UDPBaseServer(1234);
            server.startServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
