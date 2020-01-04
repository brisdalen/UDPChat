package logic.client;

import logic.Connection;
import logic.CustomThread;
import logic.Utility;

import javax.rmi.CORBA.Util;
import java.io.ByteArrayOutputStream;
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
    private int packetIDRaw = 0;
    private ByteArrayOutputStream byteStream;

    public ClientReader(String name, UDPBaseClient client, DatagramSocket socket, Scanner scanner, Connection connection) {
        super(name);
        this.client = client;
        this.socket = socket;
        this.stdIn = scanner;
        this.parentConnection = connection;
        byteStream = new ByteArrayOutputStream();
    }

    public synchronized void stopClient() {
        System.out.println("[logic.client.ClientReader]stopping client...");
        client.clientListener.stopListener();
        super.stopThread();
    }

    @Override
    public void run() {
        //System.out.println("[logic.client.ClientReader]run()");
        while(running) {

            String input = stdIn.nextLine();
            byte[] packetID = String.valueOf(packetIDRaw++).getBytes();
            //byte[] packetID = Utility.intToByteArray(packetIDRaw++);
            byte[] sender = getName().getBytes();
            byte[] message = input.getBytes();

            try {
                byteStream.write(sender);
                byteStream.write(packetID);
                byteStream.write(10);
                byteStream.write(message);
                byteStream.write(10);
                byteStream.write("Acknowledged".getBytes());
                buffer = byteStream.toByteArray();
                byteStream.reset();
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println("TEST--------");
            for(byte b : buffer) {
                System.out.print(b + " ");
            }
            System.out.println("\n" + new String(buffer));

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

        System.out.println("[logic.client.ClientReader]client stopped.");
    }

}