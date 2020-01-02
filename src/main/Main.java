package main;

import logic.server.UDPBaseServer;
import ui.server.ServerUI;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        UDPBaseServer server = new UDPBaseServer(1234);
        ServerUI serverUI = new ServerUI(server, 600, 400);
        server.setPriority(10);
        server.startServer();
    }
}
