import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.HashMap;
import java.util.Scanner;
//TODO: Sende beskjed til alle connected clients
public class ServerReader extends Thread {

    Scanner stdIn;
    UDPBaseServer server;
    private boolean running;
    DatagramSocket socket;
    byte[] buffer = new byte[65535];
    DatagramPacket sendPacket;
    Connection parentConnection;
    HashMap<String, ClientHandler> clientConnections;

    public ServerReader(String name, UDPBaseServer server, DatagramSocket socket, Scanner scanner, HashMap<String, ClientHandler> clientConnections) {
        super(name);
        this.server = server;
        this.socket = socket;
        this.stdIn = scanner;
        this.clientConnections = clientConnections;
    }

    @Override
    public synchronized void start() {
        running = true;
        super.start();
    }

    public synchronized void closeReader() {
        System.out.println("[ServerReader]closing thread...");
        running = false;
    }

    public synchronized void sendMessageToUser(String user, String message) {
        if(clientConnections.containsKey(user)) {
            clientConnections.get(user).sendPacket(message);
        } else {
            System.out.println("[ServerReader]user " + user + " not found");
        }
    }

    @Override
    public void run() {
        //System.out.println("[ServerReader]run()");
        while(running) {
            String input = stdIn.nextLine();
            String[] inputParts = input.split(":");
            System.out.println("[ServerReader]input: " + input);

            // Shut down the server if the server issues the "stop server" command
            if(inputParts.length > 1) {
                sendMessageToUser(inputParts[0], inputParts[1]);
            } else if(input.trim().toLowerCase().equals("stop server")) {
                server.stopServer();
                closeReader();
            } else {
                System.out.println("[ServerReader]unknown command");
            }
        }

        System.out.println("[ServerReader]thread stopped.");
    }
}
