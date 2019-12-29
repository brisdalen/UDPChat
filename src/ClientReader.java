import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Scanner;

public class ClientReader extends Thread {

    UDPBaseClient client;
    DatagramSocket socket;
    Scanner stdIn;
    private boolean running;
    byte[] buffer = new byte[65535];
    DatagramPacket sendPacket;
    Connection parentConnection;

    public ClientReader(String name, UDPBaseClient client, DatagramSocket socket, Scanner scanner, Connection connection) {
        super(name);
        this.client = client;
        this.socket = socket;
        this.stdIn = scanner;
        this.parentConnection = connection;
    }

    @Override
    public synchronized void start() {
        running = true;
        super.start();
    }

    public synchronized void stopClient() {
        System.out.println("ClientReader]stopping client...");
        client.clientListener.closeListener();
        running = false;
    }

    @Override
    public void run() {
        //System.out.println("[ClientReader]run()");
        while(running) {
            String input = stdIn.nextLine();
            String message = getName() + ":" + input;
            //System.out.println("[ClientReader]input: " + input);
            buffer = message.getBytes();
            sendPacket = Utility.createPacket(buffer, parentConnection);
            try {
                socket.send(sendPacket);
                // Exit the client if the user types "exit"
                if(input.trim().toLowerCase().equals("exit")) {
                    stopClient();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        System.out.println("ClientReader]client stopped.");
    }
}