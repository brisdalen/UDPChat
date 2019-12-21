import javax.xml.crypto.Data;
import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.Scanner;

public class UDPBaseClient {

    public static void main(String[] args) throws IOException {

        String name = "John";
        Scanner stdIn = new Scanner(System.in);

        // Step 1 : Create the socket object for carrying the data.
        DatagramSocket socket = new DatagramSocket();
        InetAddress ip = InetAddress.getLocalHost();
        System.out.println(ip.toString());
        byte[] nameBytes, ipBytes = null;
        byte[] buffer = null;

        socket.connect(ip, 1234);

        nameBytes = name.getBytes();
        DatagramPacket sendName = new DatagramPacket(nameBytes, nameBytes.length);

        ipBytes = ip.toString().split("/")[1].getBytes();
        DatagramPacket sendIP = new DatagramPacket(ipBytes, ipBytes.length);

        socket.send(sendName);
        socket.send(sendIP);

        byte[] receivedBytes = new byte[65535];

        DatagramPacket receivedPacket = new DatagramPacket(receivedBytes, receivedBytes.length);
        socket.receive(receivedPacket);
        String receivedString = dataToString(receivedBytes);
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