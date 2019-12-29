import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class ClientListener extends Thread {

    private DatagramSocket clientSocket;
    private byte[] receivedBytes = new byte[65535];
    private DatagramPacket receivedPacket;
    private boolean running;

    public ClientListener(String name, DatagramSocket clientSocket) {
        super(name);
        this.clientSocket = clientSocket;
    }

    @Override
    public synchronized void start() {
        running = true;
        super.start();
    }

    public synchronized void closeListener() {
        System.out.println("[ClientListener]closing thread...");
        running = false;
    }

    @Override
    public void run() {
        //System.out.println("[ClientListener]run() started");
        while (running) {
            try {
                receivedPacket = new DatagramPacket(receivedBytes, receivedBytes.length);
                clientSocket.receive(receivedPacket);
                String message = Utility.dataToString(receivedBytes);
                String[] messageParts = message.split(":");
                System.out.println("[ClientListener]From server: " + message);

                if(messageParts.length > 1) {
                    if(!messageParts[0].equals("Client:" + getName())) {
                        System.out.println("This is not meant for me?");
                    } else {
                        System.out.println(messageParts[1]);
                    }
                }

                receivedBytes = new byte[65535];
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        System.out.println("[ClientListener]thread stopped.");
    }
}
