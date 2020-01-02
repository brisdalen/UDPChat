package logic.server;

import logic.Connection;
import logic.CustomThread;
import logic.Utility;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
//TODO: Legg til sjekk for time-outs
public class UDPBaseServer extends CustomThread {

    private ArrayList<CustomThread> threads;

    private Scanner stdIn;

    private InetAddress ip;
    private int port;
    private DatagramSocket serverSocket;
    private byte[] receivedBytes = new byte[65535];
    private DatagramPacket receivedPacket;

    private GameUpdater gameUpdater;

    private ServerReader serverReader;
    private HashMap<String, ClientHandler> clientListeners;

    public UDPBaseServer(int port) throws IOException {
        threads = new ArrayList<>();
        stdIn = new Scanner(System.in);
        // Step 1 : Create a socket to listen at port 1234
        this.ip = InetAddress.getByName("0.0.0.0");
        System.out.println(ip);
        this.port = port;
        serverSocket = new DatagramSocket(port, ip);
        clientListeners = new HashMap<>();
        System.out.println("[logic.server.UDPBaseServer]Server created at port: " + port + " with ip address: " + ip);
        System.out.println("[logic.server.UDPBaseServer]Waiting for client connection...");

        gameUpdater = new GameUpdater();
        threads.add(gameUpdater);
        gameUpdater.setPriority(9);
        //gameUpdater.start();

        serverReader = new ServerReader("Server", this, serverSocket, stdIn, clientListeners);
        threads.add(serverReader);
        //serverReader.start();

    }

    public synchronized void sendMessageToUser(String user, String message) {
        serverReader.sendMessageToUser(user, message);
    }

    public synchronized void sendMessageToAllUsers(String message) {
        serverReader.sendMessageToAllUsers(message);
    }

    // Method that updates all clients
    public void updateClients(HashMap<String, ClientHandler> clientListeners) {
        for(String cl : clientListeners.keySet()) {
            // Handle updates to every client. On it's own thread.
        }
    }

    @Override
    public void run() {
        //System.out.println("[logic.server.UDPBaseServer]run() started");
        while (running) {
            try {
                // The Server's "receiver"
                receivedPacket = new DatagramPacket(receivedBytes, receivedBytes.length);
                serverSocket.receive(receivedPacket);
                String request = Utility.dataToString(receivedBytes);
                String[] requestParts = request.split(":");
                System.out.println("[logic.server.UDPBaseServer]From client: " + request);

                if(request.trim().toLowerCase().equals("connect")) {
                    Connection clientConnection = new Connection(receivedPacket.getAddress(), receivedPacket.getPort());
                    String id = Utility.getRandomString(4);

                    // Generate a new id if the current id already exists in the HashMap (very unlikely)
                    while(clientListeners.containsKey(id)) {
                        id = Utility.getRandomString(16);
                    }
                    // Store the handler with it's unique id in the hashmap
                    clientListeners.put(id, new ClientHandler(id, serverSocket, clientConnection));
                    //clientListeners.get(id).start();
                }

                if(requestParts.length > 1) {
                    // Stop and remove the handler responsible for the client exiting
                    if (requestParts[1].trim().toLowerCase().equals("exit")) {
                        String clientToPop = requestParts[0];
                        clientListeners.get(clientToPop).stopThread();
                        clientListeners.remove(clientToPop);
                    }
                }

                receivedBytes = new byte[65535];
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        System.out.println("[logic.server.UDPBaseServer]server stopped.");
    }

    public void startServer() {
        System.out.println("[logic.server.UDPBaseServer]Server starting...");
        gameUpdater.start();
        serverReader.start();
        this.start();
        System.out.println("[logic.server.UDPBaseServer]Server started.");
    }

    public void stopServer() {
        System.out.println("[logic.server.UDPBaseServer]stopping server...");
        serverReader.sendMessageToAllUsers("Server is closing...");
        running = false;

        for(String client : clientListeners.keySet()) {
            clientListeners.get(client).stopThread();
        }

        for(CustomThread ct : threads) {
            ct.stopThread();
        }

        serverReader.sendMessageToAllUsers("Server is closed.");
        System.out.println("[logic.server.UDPBaseServer]server stopped.");
    }

    public static void main(String[] args) {
        try {
            UDPBaseServer server = new UDPBaseServer(1234);
            server.setPriority(10);
            server.startServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
