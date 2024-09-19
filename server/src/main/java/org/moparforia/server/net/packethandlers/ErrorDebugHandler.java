package org.moparforia.server.net.packethandlers;

import org.moparforia.server.Server;
import org.moparforia.server.game.Player;
import org.moparforia.server.net.Packet;
import org.moparforia.server.net.PacketHandler;
import org.moparforia.server.net.PacketType;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ErrorDebugHandler implements PacketHandler {
    @Override
    public PacketType getType() {
        return PacketType.DATA;
    }

    @Override
    // error-debug\t4\tjava.lang.StringIndexOutOfBoundsException: Index 1 out of bounds for length 1\tgame\starttrack\t\0\V 1\A Dewlor\N Witch\T BAQQ48DEBGAQB3ADBAHA15DBWJHBAJA6DEB3ABAKA10DBABB3DBAKADB3AEEB3ADDE15DEE6D3E10DE3DED4ECBAEBLAQBAQQ7DE6DEE6D3E10DE3DED4E9DBHAQEE6DEE6D3E10DEDDBKABED3EBFAQE9DEE6DEE6D3E10DEBIBAB3ADED3E7DBNAQEDDEE6DEE6D3E10DEB3ADDED3EBGAQH9DEE6DBAQQE6D3E10DEEDDED3EB3A10DEE6DEE6D3E10DEEDDED4EDDBIAQBAQQDDBJAQEDDEE6DEE6D3E10DBOABEDDED4EDDBLAQEDDBKAQEDDEE6DEE6D3E10DB3A3DED4E10DEE6DEE6D3E10DE3DED4E9DBKQAEE6DEE6D3E10DE3DED3EBAIADDBPIQBAQQ7DI6DEG6D3E10DE3DED4E9DBJQIEE6DEE6D3E10DE3DED4E10DEE6DEE6D3E10DBMAMEDDED4E10DEE6DEE6D3E10DBAMMEDDED4E10DEE6DEE6D3E10DEEDDED4E10DBLIQE6DEE6DBLKQF7DBNKQEDDEEDDED4E11DE6DEE6DBAKA12DEBLMAEDED4E11DE6DEE6DE12DEDDBJAMED4E11DE6DEE6DE12DE3DEDCAA3E10DBKAIE6DEE6DE12DE3DEDHEEB3A17DBKQAEBLQAE5DE12DE3DED3E48D,Ads:B2220\I 64508,788552,2,66\B pot-shot,1138226400000\R 286,75,80,115,127,505,377,368,266,166,1108\tgame\resetvoteskip\tgame\voteskip
    public Pattern getPattern() {
        return Pattern.compile("error-debug\\t(\\d)\\t(.*)\\t(.*)\\t(.*)\\t(.*)");
    }

    @Override
    public boolean handle(Server server, Packet packet, Matcher message) {
        Player player = packet.getChannel().attr(Player.PLAYER_ATTRIBUTE_KEY).get();
        int activePanel = Integer.parseInt(message.group(1));
        String errorMessage = message.group(2);
        String lastPacketSentToClient = message.group(3);
        String secondLastPacketSentToClient = message.group(4);
        String lastPacketReceivedFromClient = message.group(5);
        System.out.println("Fatal error for player '" + player.getNick() + "' (id " + player.getId() + "): " + errorMessage + ", activePanel: " + activePanel + ", lastPacketSentToClient: " + lastPacketSentToClient + ", secondLastPacketSentToClient: " + secondLastPacketSentToClient + ", lastPacketReceivedFromClient: " + lastPacketReceivedFromClient);
        return true;
    }
}
