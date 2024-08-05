package org.moparforia.server.net;

import io.netty.channel.Channel;

@SuppressWarnings("SameParameterValue")
public class Packet {

    private Channel channel;
    private PacketType type;
    private int count;
    private String message;

    public Packet(PacketType type, String message) {
        this.type = type;
        this.message = message;
    }

    public Packet(Channel channel, String message) {
        this(message);
        this.channel = channel;
    }

    public Packet(String message) {
        if (message.matches("[cds] .+")) {
            switch (message.charAt(0)) {
                case 'c':
                    type = PacketType.COMMAND;
                    this.message = message.substring(2);
                    break;
                case 's':
                    type = PacketType.STRING;
                    this.message = message.substring(2);
                    break;
                case 'd':
                    type = PacketType.DATA;
                    count = Integer.valueOf(message.substring(2, message.indexOf(' ', 2)));
                    this.message = message.substring(message.indexOf(' ', 3) + 1);
                    break;
            }
        } else {
            type = PacketType.NONE;
            this.message = message;
        }
    }

    public Channel getChannel() {
        return channel;
    }

    public int getCount() {
        return count;
    }

    public PacketType getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "Packet{" +
                "channel=" + channel +
                ", type=" + type +
                ", count=" + count +
                ", message='" + message.replace("\t", "\\t") + '\'' +
                '}';
    }

}
