package net;

import connectors.databaseConnector.DBConnector;

public class NettyMain {
    public static void main(String[] args) {
        DBConnector.startDbConnection();
        new SocketServer();
    }
}
