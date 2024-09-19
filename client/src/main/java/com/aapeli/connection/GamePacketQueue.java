package com.aapeli.connection;

import com.aapeli.tools.Tools;
import java.util.ArrayList;
import java.util.List;

class GamePacketQueue implements Runnable {

    private SocketConnection socketConnection;
    private SocketConnectionListener socketConnectionListener;
    private List<String> packets;
    private boolean running;
    private Thread thread;


    protected GamePacketQueue(SocketConnection socketConnection, SocketConnectionListener socketConnectionListener) {
        this.socketConnection = socketConnection;
        this.socketConnectionListener = socketConnectionListener;
        this.packets = new ArrayList<>();
        this.running = true;
        this.thread = new Thread(this);
        this.thread.start();
    }

    public void run() {
        while (true) {
            try {
                Tools.sleep(50L);

                String packet;
                while ((packet = this.nextGamePacket()) != null) {
                    this.socketConnectionListener.dataReceived(packet);
                }

                if (this.running) {
                    continue;
                }
            } catch (Exception e) {
                this.running = false;
                this.socketConnection.handleCrash();
            }

            return;
        }
    }

    protected synchronized void addGamePacket(String command) {
        this.packets.add(command);
    }

    protected void stop() {
        this.running = false;
    }

    private synchronized String nextGamePacket() {
        if (!this.packets.isEmpty() && this.running) {
            String packet = this.packets.getFirst();
            this.packets.removeFirst();
            return packet;
        } else {
            return null;
        }
    }
}
