package logic;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.BitSet;

public class Utility {

    public static byte[] intToByteArray(int i) {
        return ByteBuffer.allocate(4).putInt(i).array();
    }

    public static byte[] combineByteArrays(byte[][] arrays) {
        int size = 0;
        for(byte[] bytes : arrays) {
            size += bytes.length;
        }

        ByteBuffer bb = ByteBuffer.allocate(size);

        for(byte[] bytes : arrays) {
            bb.put(bytes);
        }

        return bb.array();
    }

    public static String getRandomString(int i) {
        // chose a Character random from this String
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz";

        // create StringBuffer size of AlphaNumericString
        StringBuilder sb = new StringBuilder(i);

        for (int j = 0; j < i; j++) {
            // generate a random number between 0 and length of AlphaNumericString
            int index = (int)(AlphaNumericString.length() * Math.random());

            // add Character one by one in end of sb
            sb.append(AlphaNumericString.charAt(index));
        }

        return sb.toString();
    }

    // A utility method to convert the byte array data into a string representation.
    public static String dataToString(byte[] a) {
        if (a == null)
            return null;
        StringBuilder ret = new StringBuilder();
        int i = 0;
        while (a[i] != 0)
        {
            ret.append((char) a[i]);
            i++;
        }
        return ret.toString();
    }

    public static DatagramPacket createPacket(byte[] message) {
        return new DatagramPacket(message, message.length);
    }

    public static DatagramPacket createPacket(byte[] message, Connection connection) {
        return new DatagramPacket(message, message.length, connection.getIp(), connection.getPort());
    }

    public static DatagramPacket createPacket(byte[] message, InetAddress ip, int port) {
        return new DatagramPacket(message, message.length, ip, port);
    }

    /**
     * updates a given BitSet in regards to how many packets are missed between the
     * remote ack and the new ack
     * @param original the BitSet to calculate on
     * @param remoteAck the currently stored remote ack
     * @param newAck the new ack to calculate from
     */
    public static void updateAcks(BitSet original, int remoteAck, int newAck) {
        // if missedPackets is higher than 1, then we have some packet loss
        int missedPackets = newAck - remoteAck;
        // oldAcks is used for calculating the new BitSet of acknowledged packets
        int oldAcks = 32 - missedPackets;

        // Copy the bits that will remain after the shift
        BitSet copy = new BitSet(32);
        if(!(missedPackets >= 33)) {
            copy = original.get(missedPackets, 33);
        }

        // Move the copied bits to the start of the BitSet
        for(int i = 0; i < oldAcks; i++) {
            original.set(i, copy.get(i));
        }

        // No need to set any bits to 0 if there are no missed packets
        if(oldAcks <= 31 && oldAcks > 0) {
            // otherwise set the bits between to 0
            for (int i = oldAcks; i < 31; i++) {
                original.set(i, false);
            }
        } else {
            // Set all bits to 0 if all packets have been missed
            for(int i = 0; i < 32; i++) {
                original.set(i, false);
            }
        }

        // Set the last (current) packet to true
        original.set(31, true);

        System.out.println("\n" + original);
    }
}
