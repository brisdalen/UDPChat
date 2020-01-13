package logic.client;

import logic.Connection;
import logic.CustomThread;
import logic.Utility;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.BitSet;
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
    private int ackID;
    private BitSet ackBitfield;

    public ClientReader(String name, UDPBaseClient client, DatagramSocket socket, Scanner scanner, Connection connection) {
        super(name);
        this.client = client;
        this.socket = socket;
        this.stdIn = scanner;
        this.parentConnection = connection;
        byteStream = new ByteArrayOutputStream();
        ackID = 0;
        ackBitfield = new BitSet(32);
    }

    public synchronized void stopClient() {
        System.out.println("[logic.client.ClientReader]stopping client...");
        client.clientListener.stopListener();
        super.stopThread();
    }

    @Override
    public void run() {
        //System.out.println("[logic.client.ClientReader]run()");
        /*

        "
        Each time we send a packet we increase the local sequence number (packetIDRaw++)

        When we receive a packet, we check the sequence number of the packet against the sequence number
        of the most recently received packet, called the remote sequence number. If the packet is more recent,
        we update the remote sequence to be equal to the sequence number of the packet.

        When we compose packet headers, the local sequence becomes the sequence number of the packet,
        and the remote sequence becomes the ack.
        "

        1. Construct packet from input, packetID and message.
        2. Send the packet x number of times. If you lose many, increase the amount sent. If all comes through, reduce to normal.
        3. (On server) Process the packet. Disregard any old packets. Send an array of acknowledged packets?

         */
        while(running) {

            String input = stdIn.nextLine();
            byte[] sender = getName().getBytes();
            byte[] packetID = String.valueOf(packetIDRaw++).getBytes();
            byte[] ack = String.valueOf(ackID).getBytes();
            byte[] message = input.getBytes();

            // 10 er byte-koden for "new line", samme som å skrive "byteStream.write("\n".getBytes())"
            final int NEW_LINE = 10;

            try {
                byteStream.write(sender);
                byteStream.write(packetID);
                byteStream.write(NEW_LINE);
                byteStream.write(ack);
                byteStream.write(NEW_LINE);
                byteStream.write(message);
                byteStream.write(NEW_LINE);
                byteStream.write("Acknowledged".getBytes());
                buffer = byteStream.toByteArray();
                byteStream.reset();
            } catch (IOException e) {
                e.printStackTrace();
            }

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

    public void setAck(int remoteSequenceNumber) {
        this.ackID = remoteSequenceNumber;
    }

}