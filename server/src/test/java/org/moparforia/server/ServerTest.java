package org.moparforia.server;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ServerTest {

    private void sendMessage(BufferedWriter writer, String message) throws IOException {
        writer.write(message);
        writer.newLine();
        writer.flush();
    }

    @Test
    void testSinglePlayerFlow() throws IOException, InterruptedException {
        Server server = new Server("127.0.0.1", 4243, false,Optional.empty());
        server.start();

        Socket socket = new Socket("127.0.0.1", 4243);
        InputStream in = socket.getInputStream();
        OutputStream out = socket.getOutputStream();
        InputStreamReader r = new InputStreamReader(in);
        OutputStreamWriter w = new OutputStreamWriter(out);
        BufferedReader reader = new BufferedReader(r);
        BufferedWriter writer = new BufferedWriter(w);

        // init
        String h = reader.readLine();
        String cIo = reader.readLine();
        String cCrt = reader.readLine();
        String cCtr = reader.readLine();
        assertEquals("h 1", h);
        assertTrue(cIo.matches("c io \\d+"));
        assertEquals("c crt 250", cCrt);
        assertEquals("c ctr", cCtr);

        String nickname = "nick";

        // client id
        this.sendMessage(writer, "c new");
        String cId = reader.readLine();
        assertEquals("c id 0", cId);

        // version
        this.sendMessage(writer, "d 0 version\t35");
        String statusLogin = reader.readLine();
        assertEquals("d 0 status\tlogin", statusLogin);

        // login
        this.sendMessage(writer, "d 1 ttlogin\t" + nickname + "\t");
        String basicInfo = reader.readLine();
        String statusLobbyselect = reader.readLine();
        assertEquals("d 1 basicinfo\tt\t0\tt\tf", basicInfo);
        assertEquals("d 2 status\tlobbyselect\t300", statusLobbyselect);

        // number of players
        this.sendMessage(writer, "d 2 lobbyselect\trnop");
        String lobbySelectNop = reader.readLine();
        assertEquals("d 3 lobbyselect\tnop\t0\t0\t0", lobbySelectNop);

        // select single player
        this.sendMessage(writer, "d 3 lobbyselect\tselect\t1");
        String statusLobby = reader.readLine();
        String lobbyUsers = reader.readLine();
        String lobbyOwnjoin = reader.readLine();
        assertEquals("d 4 status\tlobby\t1", statusLobby);
        assertEquals("d 5 lobby\tusers", lobbyUsers);
        assertEquals("d 6 lobby\townjoin\t3:" + nickname + "^r^0^-^-^-", lobbyOwnjoin);
        this.sendMessage(writer, "d 4 lobby\ttracksetlist");
        String tracksetlist = reader.readLine();
        assertEquals(tracksetlist, "d 7 lobby\ttracksetlist\tBirchwood\t1\t9\tNo one\t1\tNo one\t1\tNo one\t1\tNo one\t1\tOak Park\t1\t18\tNo one\t1\tNo one\t1\tNo one\t1\tNo one\t1\tOne by One\t2\t18\tNo one\t1\tNo one\t1\tNo one\t1\tNo one\t1\tScary Set\t3\t9\tNo one\t1\tNo one\t1\tNo one\t1\tNo one\t1\tSpruce Corpse\t2\t9\tNo one\t1\tNo one\t1\tNo one\t1\tNo one\t1\tThe First\t2\t18\tNo one\t1\tNo one\t1\tNo one\t1\tNo one\t1\tTorment Fields\t3\t18\tNo one\t1\tNo one\t1\tNo one\t1\tNo one\t1");

        // start championship game
        this.sendMessage(writer, "d 5 lobby\tcspc\t6");
        String statusGame = reader.readLine();
        String gameInfo = reader.readLine();
        String players = reader.readLine();
        String ownInfo = reader.readLine();
        String gameStart = reader.readLine();
        String resetVoteSkip = reader.readLine();
        String startTrack = reader.readLine();
        String startTurn = reader.readLine();
        assertEquals("d 8 status\tgame", statusGame);
        assertEquals("d 9 game\tgameinfo\tderp\tf\t0\t1\t18\t0\t0\t0\t0\t1\t0\t0\tf", gameInfo);
        assertEquals("d 10 game\tplayers", players);
        assertEquals("d 11 game\towninfo\t0\t" + nickname + "\t-", ownInfo);
        assertEquals("d 12 game\tstart", gameStart);
        assertEquals("d 13 game\tresetvoteskip", resetVoteSkip);
        assertTrue(startTrack.matches("d 14 game\tstarttrack\tt\t0\tV 1\tA Leonardo\tN Revocations\tT BAMM21DBAQQ7DBAMM18DBAQQDB3ADDBAQQ6DEDDBAQQ7DB3A11DBHAQBGAQB3ABAQQ4DB3ADDBHAMEDEB3A10DEDDB3A3DBHAQBGAQB3AEE14DBAGABAIADDBAKAE3DEDEE10DEDDE6DEE6DBAQQE6DEEDDEE3DEDEE10DEDDE4DBIAHBAIAEE6DEE6DEEDDEE3DEDEE10DEDDE4DBAGA3EDBGMABHMAEDDEE6DEEDDEE3DEDEE10DEDDE4D4EDBHAMBGAMEDDEE6DEEDDEE3DEDEE10DEDDE4D4E6DEE6DEEDDEE3DEDEE9DBGLABAEADDBHFAE3DBAQQ3E6DEE6DEEDDEE3DEDEE9DBAKAGDDBAGAE3D4E6DEEDBAQQ6DEG7DBAMMEDBEAQBFAQE5DEEDDEE3D4E6DEE5DEDDEE7DEEDBHAQBAQQDE4DEEDDEE3DEBAGADE6DEE5DEG9D3EDDEBGAQE4DEEDDEE3DEDDE6DEE5DEE9DEBAQQE9DEEDDEE5DEE6DEE5DBGAQE9D3E9DEEDDEE5DEE4DBAQQDDE16D3E5DBAQQ4DEDDBAQQ7DE4DEBAGADE5DBEAQBAQQDDBGAQEDDBGMABAMMDDEBFAQE4DEBAMM3DEDDBAMA7DE4DEBAEAGE5DBAQQBGAQH4DBGMABAMM3DEDFE3DEE3DEDDE7DE4D4E5DEG5DBHAMEBGAMB3ABAQQEDDFEDDEEBIMAB3A17D4E5DEE6DBSAMGDEE3DFEDEEB3A18DBAGA3E5DEE9DEEBIQAB3A4DEEBLMAE17D4EDDBQAMEDEE9DEEB3ACBAE3DEE3DEDDBAMA7DE4D4EDBEAMBAMMBFAM3E7DCAA3EBLQAF4DEE3DEDDE7DEDBEAQBFAQ5EBEAMBAMMDBEMA3E9DEE11DEDDBAQQ15DBAMMDDBEMAB3ADE11DBAMM10DE4DBAMM32D,Ads:C0204\tI 308939,4480252,4,2203\tB d2b,\\d+\tR 633,173,118,129,125,546,495,461,467,556,4472"));
        assertEquals("d 15 game\tstartturn\t0", startTurn);

        // leave game
        this.sendMessage(writer, "d 6 game\tback");
        statusLobby = reader.readLine();
        lobbyUsers = reader.readLine();
        lobbyOwnjoin = reader.readLine();
        assertEquals("d 16 status\tlobby\t1", statusLobby);
        assertEquals("d 17 lobby\tusers", lobbyUsers);
        assertEquals("d 18 lobby\townjoin\t3:" + nickname + "^r^0^-^-^-", lobbyOwnjoin);

        // quit game
        this.sendMessage(writer, "d 7 lobby\tquit");
        server.stop();
    }
}
