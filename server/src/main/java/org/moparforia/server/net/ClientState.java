package org.moparforia.server.net;


import io.netty.util.AttributeKey;


public final class ClientState {

    public static final AttributeKey<ClientState> CLIENT_STATE_ATTRIBUTE_KEY = AttributeKey.valueOf("MESSAGE_COUNTS");

    private long sentCount;
    private long receivedCount;
    private long lastActivityTime;

    public ClientState() {
        this.sentCount = 0;
        this.receivedCount = 0;
        this.lastActivityTime = System.currentTimeMillis();
    }

    public long getSentCount() {
        return sentCount;
    }

    public void setSentCount(long sentCount) {
        this.sentCount = sentCount;
    }

    public long getReceivedCount() {
        return receivedCount;
    }

    public void setReceivedCount(long receivedCount) {
        this.receivedCount = receivedCount;
    }

    public long getLastActivityTime() {
        return lastActivityTime;
    }

    public void setLastActivityTime(long lastActivityTime) {
        this.lastActivityTime = lastActivityTime;
    }
}
