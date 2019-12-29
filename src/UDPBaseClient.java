import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.Scanner;

public class UDPBaseClient {

    Scanner stdIn;
    String id;

    DatagramSocket socket;
    InetAddress serverIP;
    Connection clientConnection;

    byte[] requestBytes = new byte[65535];
    // Packet for requesting ID
    DatagramPacket sendID;
    DatagramPacket receivedPacket;

    byte[] receivedBytes = new byte[65535];

    public UDPBaseClient() throws IOException {
        stdIn = new Scanner(System.in);
        socket = new DatagramSocket();
        serverIP = InetAddress.getByName("10.0.0.111");
        clientConnection = new Connection(serverIP, 1234);

        requestBytes = "connect".getBytes();
        sendID = Utility.createPacket(requestBytes, serverIP, 1234);

        socket.send(sendID);

        receivedPacket = Utility.createPacket(receivedBytes);
        socket.receive(receivedPacket);

        String receivedString = Utility.dataToString(receivedBytes);
        System.out.println("From server: " + receivedString);
        id = receivedString;

        ClientReader clientReader = new ClientReader(id, stdIn, socket, clientConnection);
        clientReader.start();
    }

    public static void main(String[] args) throws IOException {
        UDPBaseClient client = new UDPBaseClient();
    }

    private static byte[] intToByteArray(int i) {
        return ByteBuffer.allocate(4).putInt(i).array();
    }

}