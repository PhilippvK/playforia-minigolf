package com.aapeli.connection;


public interface SocketConnectionListener {

    void dataReceived(String data);

    void connectionLost(int var1);

    void notifyConnectionDown();

    void notifyConnectionUp();
}
