package logic.client;

import logic.Connection;
import logic.Utility;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.Scanner;

public class UDPBaseClient {

    private Scanner stdIn;
    private String id;

    private DatagramSocket socket;
    private InetAddress serverIP;
    private Connection clientConnection;

    private byte[] requestBytes;
    // Packet for requesting ID
    private DatagramPacket sendID;
    private DatagramPacket receivedPacket;

    private byte[] receivedBytes = new byte[65535];

    protected ClientListener clientListener;
    private ClientReader clientReader;

    public UDPBaseClient() throws IOException {
        stdIn = new Scanner(System.in);
        socket = new DatagramSocket();
        serverIP = InetAddress.getByName("142.93.135.21");
        clientConnection = new Connection(serverIP, 1234);

        requestBytes = "connect".getBytes();
        sendID = Utility.createPacket(requestBytes, serverIP, 1234);
        socket.send(sendID);

        receivedPacket = Utility.createPacket(receivedBytes);
        socket.receive(receivedPacket);
        String receivedString = Utility.dataToString(receivedBytes);
        System.out.println("[logic.client.UDPBaseClient]From server: " + receivedString);
        id = receivedString;

        clientReader = new ClientReader(id, this, socket, stdIn, clientConnection);
        clientReader.start();
        clientListener = new ClientListener(id, socket);
        clientListener.start();
    }

    public static void main(String[] args) throws IOException {
        new UDPBaseClient();
    }

}