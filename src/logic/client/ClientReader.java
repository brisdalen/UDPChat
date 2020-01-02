package logic;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Scanner;
// TODO: packet identification
public class ClientReader extends CustomThread {

    private UDPBaseClient client;
    private DatagramSocket socket;
    private Scanner stdIn;
    private byte[] buffer = new byte[65535];
    private DatagramPacket sendPacket;
    private Connection parentConnection;
    private int packetID = 0;

    public ClientReader(String name, UDPBaseClient client, DatagramSocket socket, Scanner scanner, Connection connection) {
        super(name);
        this.client = client;
        this.socket = socket;
        this.stdIn = scanner;
        this.parentConnection = connection;
    }

    public synchronized void stopClient() {
        System.out.println("[logic.ClientReader]stopping client...");
        client.clientListener.stopListener();
        super.stopThread();
    }

    @Override
    public void run() {
        //System.out.println("[logic.ClientReader]run()");
        while(running) {

            String input = stdIn.nextLine();
            String message = getName() + ":" + input;

            buffer = message.getBytes();
            sendPacket = Utility.createPacket(buffer, parentConnection);
            try {
                socket.send(sendPacket);
                // Exit the client if the user types "exit"
                if(input.trim().toLowerCase().equals("exit")) {
                    stopClient();
                }
                sleep(600);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("[logic.ClientReader]client stopped.");
    }

}