package com.aapeli.connection;

import com.aapeli.applet.AApplet;
import com.aapeli.client.Parameters;
import org.moparforia.client.Launcher;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import phi.net.Socket;
import java.util.Vector;

public final class Connection implements Runnable {

    /* DCR Constants */
    public static final int DCR_UNDEFINED = 0;
    public static final int DCR_BYUSER = 1;
    public static final int DCR_NORETRY = 2;
    public static final int DCR_RETRYFAIL = 3;
    public static final int DCR_VERSION = 4;
    public static final int DCR_HANDLEFAILED = 5;

    /* State Constants */
    public static final int STATE_OPENING = 0;
    public static final int STATE_OPEN = 1;
    public static final int STATE_CONNECTED = 2;
    public static final int STATE_DOWN = 3;
    public static final int STATE_DISCONNECTED = 4;

    /* Other Constants */
    private static final String encoding = "UTF-8";
    public static final int CIPHER_MAGIC_DEFAULT = 4;
    private AApplet gameApplet;
    private Parameters params;
    private ConnListener connListener;
    private GameCipher gameCipher;
    private int state;
    private int disconnectReason;
    private boolean closed;
    private boolean failed;
    private Socket socket;
    private BufferedReader sockIn;
    private BufferedWriter sockOut;
    private long clientId;
    private int retryTimeoutS;
    private GameQueue gameQueue;
    private GamePacketQueue gamePacketQueue;
    private Vector thriftLogs;
    private long numReceivedGamePackets;
    private long connActivityTime;
    private ConnCipher connCipher;
    private Thread thread;


    public Connection(AApplet gameApplet, ConnListener connListener, String[] gameCipherCmds) {
        this(gameApplet, gameApplet.param, connListener, gameCipherCmds);
    }

    public Connection(Parameters params, ConnListener connListener, String[] gameCipherCmds) {
        this((AApplet) null, params, connListener, gameCipherCmds);
    }

    private Connection(AApplet gameApplet, Parameters params, ConnListener connListener, String[] gameCipherCmds) {
        this.gameApplet = gameApplet;
        this.params = params;
        this.connListener = connListener;
        if (gameApplet != null) {
            gameApplet.setConnectionReference(this);
        }

        int connCipherMagic = CIPHER_MAGIC_DEFAULT;
        if(Launcher.isUsingCustomServer())
            gameCipherCmds = null;
        if (gameCipherCmds != null) {
            this.gameCipher = new GameCipher(gameCipherCmds);
            connCipherMagic = this.gameCipher.getConnCipherMagic();
        }

        this.clientId = -1L;
        this.retryTimeoutS = 25;
        this.gameQueue = new GameQueue();
        this.thriftLogs = new Vector();
        this.numReceivedGamePackets = -1L;
        this.state = STATE_OPENING;
        this.disconnectReason = DCR_UNDEFINED;
        this.closed = this.failed = false;
        this.connCipher = new ConnCipher(connCipherMagic);
    }

    public void run() {
        this.gamePacketQueue = new GamePacketQueue(this, this.connListener);

        try {
            do {
                if (this.state == STATE_OPEN) {
                    this.handleConnection();
                } else if (this.state == STATE_CONNECTED) {
                    this.handleGame();
                } else if (this.state == STATE_DOWN) {
                    this.reconnect();
                }

                if (this.closed) {
                    if (this.processGameQueue()) {
                        this.writeLineC("end");
                    }

                    this.state = STATE_DISCONNECTED;
                    this.disconnectReason = DCR_BYUSER;
                }

                if (this.failed) {
                    this.state = STATE_DISCONNECTED;
                    this.disconnectReason = DCR_HANDLEFAILED;
                }
                Thread.sleep(1);
            } while (this.state != STATE_DISCONNECTED);
        } catch (Exception ex) {
            ; // TODO: hanlde
            ex.printStackTrace();
        } catch (Error err) {
            ; // TODO: handle
            err.printStackTrace();
        }

        this.close();
        this.gamePacketQueue.stop();
        this.connListener.connectionLost(this.disconnectReason);
    }

    public boolean openConnection() {
        if (this.state != STATE_OPENING) {
            throw new IllegalStateException("Connection already opened");
        } else if (!this.connect()) {
            this.state = STATE_DISCONNECTED;
            return false;
        } else {
            this.state = STATE_OPEN;
            this.thread = new Thread(this);
            this.thread.start();
            return true;
        }
    }

    public void writeData(String data) {
        if (this.state == STATE_OPENING) {
            throw new IllegalStateException("Connection not yet open");
        } else if (this.state != STATE_DISCONNECTED) {
            if(Launcher.debug())
                System.out.println("CLIENT> WRITE \"d " + gameQueue.sendSeqNum + " " + data + "\"");
            if (this.gameCipher != null) {
                data = this.gameCipher.encrypt(data);
            }

            this.gameQueue.add(data);
        }
    }

    public void writeThriftLog(int var1, String var2, String var3) { // TODO: replace var1...var4
        String var4 = "tlog\t" + var1 + "\t" + var2;
        if (var3 != null) {
            var4 = var4 + "\t" + var3;
        }

        this.thriftLogs.addElement(var4);
    }

    public void closeConnection() {
        if (this.state == STATE_OPENING) {
            throw new IllegalStateException("Connection not yet even opened");
        } else if (this.state != STATE_DISCONNECTED || this.thread != null) {
            this.closed = true;
            this.state = STATE_DISCONNECTED;
            this.thread.interrupt();
        }
    }

    public String getLocalIP() {
        return null;
    }

    protected void handleCrash() { // TODO
        this.failed = true;
        this.state = STATE_DISCONNECTED;
        this.thread.interrupt();
    }

    private boolean connect() {
        try {
            String serverIp = this.params.getServerIp();
            int serverPort = this.params.getServerPort();
            boolean isApplet = Boolean.parseBoolean(this.params.getParameter("is_applet"));
            this.socket = new phi.net.Socket(serverIp, serverPort, isApplet);
            InputStream in = this.socket.getInputStream();
            OutputStream out = this.socket.getOutputStream();

            InputStreamReader reader;
            OutputStreamWriter writer;
            try {
                reader = new InputStreamReader(in, encoding);
                writer = new OutputStreamWriter(out, encoding);
            } catch (UnsupportedEncodingException ex) {
                reader = new InputStreamReader(in);
                writer = new OutputStreamWriter(out);
            }

            this.sockIn = new BufferedReader(reader);
            this.sockOut = new BufferedWriter(writer);
            this.socket.setSoTimeout(250);
            this.connActivityTime = System.currentTimeMillis();
            return true;
        } catch (Exception ex) {
            ex.printStackTrace(System.out);
            return false;
        }
    }

    private void handleGame() {
        this.handleGameQueue();
        if (this.state == STATE_CONNECTED) {
            this.handleConnection();
        }

    }

    private void handleConnection() {
        this.readInput();
        if (this.state == STATE_CONNECTED) {
            this.checkConnActivity();
        }

    }

    private void handleGameQueue() {
        this.processGameQueueDisconnect();
        if (this.state == STATE_CONNECTED) {
            this.processThriftLogs();
        }

    }

    private void processGameQueueDisconnect() {
        do {
            String data = this.gameQueue.pop();
            if (data == null) {
                return;
            }

            if (!this.writeLineD(data)) {
                this.disconnect();
            }
        } while (this.state == STATE_CONNECTED);

    }

    private void processThriftLogs() {
        while (true) {
            if (this.state == STATE_CONNECTED && !this.thriftLogs.isEmpty()) {
                String str = (String) ((String) this.thriftLogs.firstElement());
                this.thriftLogs.removeElementAt(0);
                if (this.writeLineS(str)) {
                    continue;
                }

                this.disconnect();
                return;
            }

            return;
        }
    }

    private boolean processGameQueue() {
        String data;
        do {
            data = this.gameQueue.pop();
            if (data == null) {
                return true;
            }
        } while (this.writeLineD(data));

        return false;
    }

    private void disconnect() {
        if (this.state == STATE_CONNECTED && this.retryTimeoutS > 0) {
            this.close();
            this.state = STATE_DOWN;
            this.connListener.notifyConnectionDown();
        } else {
            this.state = STATE_DISCONNECTED;
            this.disconnectReason = DCR_NORETRY;
        }

    }

    private boolean writeLineC(String cmd) { // C: Command
        return this.writeLine("c " + cmd);
    }

    private boolean writeLineD(String data) { // D: Data
        return this.writeLine("d " + data);
    }

    private boolean writeLineS(String str) { // S: String
        return this.writeLine("s " + str);
    }

    private boolean writeLine(String line) {
        try {
            if(!line.startsWith("d ") && Launcher.debug())
                System.out.println("CLIENT> WRITE \"" + line + "\"");
            if(!Launcher.isUsingCustomServer())
                line = this.connCipher.encrypt(line);
            this.sockOut.write(line);
            this.sockOut.newLine();
            this.sockOut.flush();
            return true;
        } catch (IOException ex) {
            return false;
        }
    }

    private void readInput() {
        String cmd = this.readLine();
        if (cmd != null) {
            this.connActivityTime = System.currentTimeMillis();
            char cmdtype = cmd.charAt(0);
            cmd = cmd.substring(2);
            int firstSpace;
            if (cmdtype == 'h') {// not sure what
                firstSpace = Integer.parseInt(cmd);// it's always 1... ALWAYS
                if (firstSpace != 1) {
                    this.state = STATE_DISCONNECTED;
                    this.disconnectReason = DCR_VERSION;
                }
            } else if (cmdtype == 'c') {// connection related
                if (cmd.startsWith("io ")) {
                    this.connCipher.initialise(Integer.parseInt(cmd.substring(3)));
                } else if (cmd.startsWith("crt ")) {
                    this.retryTimeoutS = Integer.parseInt(cmd.substring(4));
                } else if (cmd.equals("ctr")) {
                    if (this.clientId == -1L) {
                        this.writeLineC("new");
                    } else {
                        this.writeLineC("old " + this.clientId);
                    }
                } else if (cmd.startsWith("id ")) { // connected
                    long id = Long.parseLong(cmd.substring(3));
                    this.clientId = id;
                    this.state = STATE_CONNECTED;
                } else if (cmd.equals("rcok")) { // reconnect ok
                    this.state = STATE_CONNECTED;
                    this.connListener.notifyConnectionUp();
                } else if (cmd.equals("rcf")) { // reconnect ok
                    this.state = STATE_DISCONNECTED;
                    this.disconnectReason = DCR_RETRYFAIL;
                } else if (cmd.equals("ping")) {
                    this.writeLineC("pong");
                }
            } else if (cmdtype == 'p') {
                if (cmd.startsWith("kickban ") && this.gameApplet != null) {
                    firstSpace = Integer.parseInt(cmd.substring(8));
                    this.gameApplet.setEndState(firstSpace == 1 ? AApplet.END_ERROR_KICK_NOW
                            : (firstSpace == 2 ? AApplet.END_ERROR_KICKBAN_NOW
                            : (firstSpace == 3 ? AApplet.END_ERROR_BAN_INIT
                            : AApplet.END_ERROR_TOOMANYIP_INIT)));
                }
            } else if (cmdtype == 's') {
                if (cmd.startsWith("json ")) {
                    String json = cmd.substring(5);
                    this.params.callJavaScriptJSON(json);
                }
            } else if (cmdtype == 'd') {
                firstSpace = cmd.indexOf(' ');
                long numServerSentPaketz = Long.parseLong(cmd.substring(0, firstSpace));
                if (numServerSentPaketz > this.numReceivedGamePackets) {
                    if (numServerSentPaketz > this.numReceivedGamePackets + 1L) {
                        this.state = STATE_DISCONNECTED;
                        this.disconnectReason = DCR_RETRYFAIL;
                    } else {
                        cmd = cmd.substring(firstSpace + 1);
                        if (this.gameCipher != null) {
                            cmd = this.gameCipher.decrypt(cmd);
                        }

                        if(Launcher.debug())
                            System.out.println("CLIENT> READ \"d " + numServerSentPaketz + " " + cmd + "\"");
                        this.gamePacketQueue.addGamePacket(cmd);
                        ++this.numReceivedGamePackets;
                    }
                }
            }

        }
    }

    private String readLine() {
        try {
            String line = this.sockIn.readLine();
            if (line != null) {
                if(!Launcher.isUsingCustomServer())
                    line = this.connCipher.decrypt(line);
                if(!line.startsWith("d ") && Launcher.debug())
                    System.out.println("CLIENT> READ \"" + line + "\"");
                return line;
            }
        } catch (InterruptedIOException ex) {
            ex.printStackTrace();
            return null;
        } catch (IOException ex) {
            ;
            ex.printStackTrace();
        }

        this.disconnect();
        return null;
    }

    private void checkConnActivity() {
        long time = System.currentTimeMillis();
        if (time > this.connActivityTime + 20000L) {
            this.disconnect();
        }

    }

    private void reconnect() {
        long deadline = System.currentTimeMillis() + (long) ((this.retryTimeoutS + 12) * 1000);

        do {
            try {
                Thread.sleep(3000L);
            } catch (InterruptedException ex) {
                ;
            }

            if (this.state != STATE_DOWN) {
                return;
            }

            if (this.connect()) {
                this.connCipher.reset();
                this.gameQueue.clear();
                this.state = STATE_OPEN;
                return;
            }
        } while (System.currentTimeMillis() < deadline);

        this.state = STATE_DISCONNECTED;
        this.disconnectReason = DCR_RETRYFAIL;
    }

    private void close() {
        if (this.sockIn != null) {
            try {
                this.sockIn.close();
            } catch (IOException ex) {
                ;
            }

            this.sockIn = null;
        }

        if (this.sockOut != null) {
            try {
                this.sockOut.close();
            } catch (IOException ex) {
                ;
            }

            this.sockOut = null;
        }

        if (this.socket != null) {
            try {
                this.socket.close();
            } catch (IOException ex) {
                ;
            }

            this.socket = null;
        }

    }
}
