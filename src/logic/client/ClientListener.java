package logic;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
//TODO: Akkurat nå venter listener på et siste svar fra server før den stenges; tenke på hvordan alt skal avsluttes
public class ClientListener extends CustomThread {

    private DatagramSocket clientSocket;
    private byte[] receivedBytes = new byte[65535];
    private DatagramPacket receivedPacket;

    public ClientListener(String name, DatagramSocket clientSocket) {
        super(name);
        this.clientSocket = clientSocket;
    }

    public synchronized void stopListener() {
        System.out.println("[logic.ClientListener]closing thread...");
        super.stopThread();
    }

    @Override
    public void run() {
        //System.out.println("[logic.ClientListener]run() started");
        while (running) {
            try {
                receivedPacket = new DatagramPacket(receivedBytes, receivedBytes.length);
                clientSocket.receive(receivedPacket);
                String message = Utility.dataToString(receivedBytes);
                // Endre 2 strings, på samme måte som logic.ServerReader
                String[] messageParts = message.split(":");
                System.out.println("[logic.ClientListener]From server: " + message);

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

        System.out.println("[logic.ClientListener]thread stopped.");
    }
}
