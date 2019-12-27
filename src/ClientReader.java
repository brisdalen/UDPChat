import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Scanner;

public class ClientReader extends Thread {

    Scanner stdIn;
    DatagramSocket socket;
    private boolean running;
    byte[] buffer = new byte[65535];
    DatagramPacket sendPacket;
    Connection parentConnection;

    public ClientReader(String name, Scanner scanner, DatagramSocket socket, Connection connection) {
        super(name);
        this.stdIn = scanner;
        this.socket = socket;
        this.parentConnection = connection;
    }

    @Override
    public synchronized void start() {
        running = true;
        super.start();
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
                    break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}