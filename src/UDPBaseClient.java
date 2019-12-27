import javax.xml.crypto.Data;
import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.Scanner;

public class UDPBaseClient {

    public static void main(String[] args) throws IOException {

        Scanner stdIn = new Scanner(System.in);
        String id;

        // Step 1 : Create the socket object for carrying the data.
        DatagramSocket socket = new DatagramSocket();
        InetAddress ip = InetAddress.getLocalHost();
        System.out.println(ip.toString());
        byte[] requestBytes = null;
        byte[] buffer = null;

        requestBytes = "connect".getBytes();
        DatagramPacket sendID = new DatagramPacket(requestBytes, requestBytes.length, ip, 1234);

        socket.send(sendID);

        byte[] receivedBytes = new byte[65535];

        DatagramPacket receivedPacket = new DatagramPacket(receivedBytes, receivedBytes.length);
        socket.receive(receivedPacket);
        socket.connect(receivedPacket.getAddress(), receivedPacket.getPort());

        String receivedString = Utility.dataToString(receivedBytes);
        System.out.println("From server: " + receivedString);

        while(true) {

            String input = stdIn.nextLine();

            // convert the String input into the byte array.
            buffer = input.getBytes();

            // Step 2 : Create the datagramPacket for sending the data.
            DatagramPacket sendPacket = new DatagramPacket(buffer, buffer.length);

            // Step 3 : invoke the send call to actually send the data.
            socket.send(sendPacket);

            // Exit the client if the user types "exit"
            if(input.trim().toLowerCase().equals("exit")) {
                break;
            }
        }

    }

    private static byte[] intToByteArray(int i) {
        return ByteBuffer.allocate(4).putInt(i).array();
    }

}
