import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Scanner;
//TODO: Legg til sjekk for time-outs
//TODO: Akkurat nå venter server på et siste svar fra en client før den stenges; tenke på hvordan alt skal avsluttes
public class UDPBaseServer extends Thread {

    private Scanner stdIn;

    private InetAddress ip;
    private int port;
    private DatagramSocket serverSocket;
    private byte[] receivedBytes = new byte[65535];
    private DatagramPacket receivedPacket;
    private boolean running = false;

    ServerReader serverReader;
    private HashMap<String, ClientHandler> clientListeners;

    public UDPBaseServer(int port) throws IOException {
        stdIn = new Scanner(System.in);
        // Step 1 : Create a socket to listen at port 1234
        this.ip = InetAddress.getByName("0.0.0.0");
        System.out.println(ip);
        this.port = port;
        serverSocket = new DatagramSocket(port, ip);
        clientListeners = new HashMap<>();
        System.out.println("[UDPBaseServer]Server created at port: " + port + " with ip address: " + ip);
        System.out.println("[UDPBaseServer]Waiting for client connection...");

        serverReader = new ServerReader("Server", this, serverSocket, stdIn, clientListeners);
        serverReader.start();
    }

    @Override
    public void run() {
        //System.out.println("[UDPBaseServer]run() started");
        while (running) {
            try {
                // The Server's "receiver"
                receivedPacket = new DatagramPacket(receivedBytes, receivedBytes.length);
                serverSocket.receive(receivedPacket);
                String request = Utility.dataToString(receivedBytes);
                String[] requestParts = request.split(":");
                System.out.println("[UDPBaseServer]From client: " + request);

                if(request.trim().toLowerCase().equals("connect")) {
                    Connection clientConnection = new Connection(receivedPacket.getAddress(), receivedPacket.getPort());
                    String id = Utility.getRandomString(4);

                    // Generate a new id if the current id already exists in the HashMap (very unlikely)
                    while(clientListeners.containsKey(id)) {
                        id = Utility.getRandomString(16);
                    }

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

        System.out.println("[UDPBaseServer]server stopped.");
    }

    private void startServer() {
        running = true;
        this.start();
    }

    protected void stopServer() {
        System.out.println("[UDPBaseServer]stopping server...");
        serverReader.sendMessageToAllUsers("Server is closing...");
        running = false;
        serverReader.sendMessageToAllUsers("Server is closed.");
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
