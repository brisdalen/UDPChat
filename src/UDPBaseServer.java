import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPBaseServer {

    public static void main(String[] args) throws IOException {

        InetAddress ip = InetAddress.getLocalHost();
        int port = 1234;
        // Step 1 : Create a socket to listen at port 1234
        DatagramSocket socket = new DatagramSocket(port, ip);
        System.out.println("Server created at port: " + port + " with ip address: " + ip);
        byte[] receivedBytes = new byte[65535];

        DatagramPacket receivedPacket = null;
        while(true) {

            // Step 2 : create a DatgramPacket to receive the data.
            receivedPacket = new DatagramPacket(receivedBytes, receivedBytes.length);

            // Step 3 : revieve the data in byte buffer.
            socket.receive(receivedPacket);
            Connection clientConnection = new Connection(receivedPacket.getAddress(), receivedPacket.getPort());

            socket.connect(clientConnection.getIp(), clientConnection.getPort());
            String message = "registered";
            DatagramPacket sendVerification = new DatagramPacket(message.getBytes(), message.getBytes().length);
            socket.send(sendVerification);

            String receivedString = dataToString(receivedBytes);
            System.out.println("From client: " + receivedString);

            // Exit the server (later, close the thread) when the user sends exit.
            if(receivedString.trim().toLowerCase().equals("exit")) {
                System.out.println("Client exitted. Closing connection.");
                break;
            }

            // Step 4 : clear the buffer after every message.
            receivedBytes = new byte[65535];
        }
    }

    // A utility method to convert the byte array data into a string representation.
    public static String dataToString(byte[] a) {
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
}
