package logic;

import java.net.InetAddress;

public class Connection {

    private InetAddress ip;
    private int port;

    public Connection(InetAddress ip, int port) {
        this.ip = ip;
        this.port = port;
        System.out.println("[logic.Connection]new connection from: " + ip + "/" + port);
    }

    public InetAddress getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    @Override
    public String toString() {
        return ip.toString() + ":" + port;
    }
}
