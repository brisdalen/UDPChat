import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.HashMap;
import java.util.Scanner;

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

    public synchronized void sendMessageToAllUsers(String message) {
        if(!clientConnections.isEmpty()) {
            for (String s : clientConnections.keySet()) {
                sendMessageToUser(s, message);
            }
        } else {
            System.out.println("[ServerReader]no users connected");
        }
    }

    @Override
    public void run() {
        //System.out.println("[ServerReader]run()");
        while(running) {
            //TODO: Sjekk om target finnes før man spør etter beskjed
            System.out.println("Enter target (specific user, $self, or $all):");
            String target = stdIn.nextLine();
            System.out.println("Enter message: ");
            String message = stdIn.nextLine();
            // [0] = user, [1] = message
            //String[] inputParts = input.split(":");
            System.out.println("[ServerReader]input: " + target + ":" + message);

            // Shut down the server if the server issues the "stop server" command
            if(target.trim().toLowerCase().equals("$all")) {
                System.out.println(clientConnections.keySet().toString());
                sendMessageToAllUsers(message);
            }
            // Commands issued to the server itself is issued with $self
            if(target.trim().toLowerCase().equals("$self")) {
                if(message.trim().toLowerCase().equals("stop server")) {
                    server.stopServer();
                    closeReader();
                }
            // Otherwise commands are issued to a specific user
            } else {
                sendMessageToUser(target, message);
            }
            /*else { TODO: Lage liste med kommandoer
                System.out.println("[ServerReader]unknown command");
            }*/
        }

        System.out.println("[ServerReader]thread stopped.");
    }
}
