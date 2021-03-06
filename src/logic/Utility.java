package logic;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.nio.ByteBuffer;

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
}
