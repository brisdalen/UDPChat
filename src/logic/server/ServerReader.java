package logic.server;

import logic.Connection;
import logic.CustomThread;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.HashMap;
import java.util.Scanner;

public class ServerReader extends CustomThread {

    private Scanner stdIn;
    private UDPBaseServer server;
    private DatagramSocket socket;
    private byte[] buffer = new byte[65535];
    private DatagramPacket sendPacket;
    private Connection parentConnection;
    private HashMap<String, ClientHandler> clientConnections;

    public ServerReader(String name, UDPBaseServer server, DatagramSocket socket, Scanner scanner, HashMap<String, ClientHandler> clientConnections) {
        super(name);
        this.server = server;
        this.socket = socket;
        this.stdIn = scanner;
        this.clientConnections = clientConnections;
    }

    public synchronized void closeReader() {
        System.out.println("[logic.server.ServerReader]closing thread...");
        super.stopThread();
    }

    public synchronized void sendMessageToUser(String user, String message) {
        if(!clientConnections.isEmpty()) {
            if(clientConnections.containsKey(user) || user.equals("$all")) {
                if(!user.equals("$all")) {
                    // TODO: Exception in thread "Server" java.lang.NullPointerException
                    clientConnections.get(user).sendPacket(message);
                }
            } else {
                System.out.println("[logic.server.ServerReader]user " + user + " not found");
            }
        } else {
            System.out.println("[logic.server.ServerReader]no users connected");
        }
    }

    public synchronized void sendMessageToAllUsers(String message) {
        if(!clientConnections.isEmpty()) {
            for (String s : clientConnections.keySet()) {
                sendMessageToUser(s, message);
            }
        } else {
            System.out.println("[logic.server.ServerReader]no users connected");
        }
    }

    @Override
    public void run() {
        //System.out.println("[logic.server.ServerReader]run()");
        while(running) {
            //TODO: Sjekk om target finnes før man spør etter beskjed
            System.out.println("Enter target (specific user, $self, or $all):");
            String target = stdIn.nextLine();
            System.out.println("Enter message: ");
            String message = stdIn.nextLine();
            // [0] = user, [1] = message
            //String[] inputParts = input.split(":");
            System.out.println("[logic.server.ServerReader]input: " + target + ":" + message);

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
                System.out.println("[logic.server.ServerReader]unknown command");
            }*/
        }

        System.out.println("[logic.server.ServerReader]thread stopped.");
    }
}
