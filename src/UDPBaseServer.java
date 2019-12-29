import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;

public class UDPBaseServer extends Thread {
    private InetAddress ip;
    private int port;
    DatagramSocket serverSocket;
    byte[] receivedBytes = new byte[65535];
    DatagramPacket receivedPacket;
    boolean running = false;

    HashMap<String, ClientHandler> clientListeners;

    public UDPBaseServer(int port) throws IOException {
        // Step 1 : Create a socket to listen at port 1234
        this.ip = InetAddress.getLocalHost();
        this.port = port;
        serverSocket = new DatagramSocket(port, ip);
        clientListeners = new HashMap<>();
        System.out.println("[UDPBaseServer]Server created at port: " + port + " with ip address: " + ip);
        System.out.println("[UDPBaseServer]Waiting for client connection...");
    }

    @Override
    public void run() {
        //System.out.println("[UDPBaseServer]run() started");
        while (running) {
            try {
                receivedPacket = new DatagramPacket(receivedBytes, receivedBytes.length);
                serverSocket.receive(receivedPacket);

                String request = Utility.dataToString(receivedBytes);
                String[] requestParts = request.split(":");
                System.out.println("[UDPBaseServer]From client: " + request);

                if(request.trim().toLowerCase().equals("connect")) {
                    Connection clientConnection = new Connection(receivedPacket.getAddress(), receivedPacket.getPort());
                    String id = Utility.getRandomString(16);

                    // Generate a new id if the current id already exists in the HashMap (very unlikely)
                    while(clientListeners.containsKey(id)) {
                        id = Utility.getRandomString(16);
                    }

                    clientListeners.put(id, new ClientHandler(id, serverSocket, clientConnection));
                    clientListeners.get(id).start();
                }

                if(requestParts.length > 1) {
                    if (requestParts[1].trim().toLowerCase().equals("exit")) {
                        String clientToPop = requestParts[0];
                        System.out.println("[UDPBaseServer]" + clientToPop);
                        clientListeners.get(clientToPop).stopThread();
                        clientListeners.remove(clientToPop);
                    }
                }

                receivedBytes = new byte[65535];
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void startServer() {
        running = true;
        this.start();
    }

    private void close() {
        running = false;
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
