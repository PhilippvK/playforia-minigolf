/*
 */

package phi.net;

import java.net.InetAddress;
import java.net.SocketAddress;
import java.net.InetSocketAddress;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.PipedOutputStream;
import java.io.PipedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;

import java.lang.Thread;

import com.leaningtech.client.Global;

//    public static int receiveJS(String s) {
//        System.out.println("PRINT TEST S="+s);
//        return 42;
//    }
/**
 */
public final class CheerpJSocket {

    /**
     * The IP address of the remote end of this socket.
     */
    static InetAddress address;

    /**
     * The port number on the remote host to which this socket is connected.
     */
    static int port;

    /**
     */
    static volatile boolean connected = false, closed = false;

    /**
     * The timeout in ms.
     */
    final static int DEFAULT_TIMEOUT = 250;
    static int timeout;
    
    /**
     * ???
     */
    static PipedOutputStream pipeIn;
    static PipedInputStream pipeOut;
    static PipedOutputStream reverseIn;
    static PipedInputStream reverseOut;

    private static Inner inner;

    private static class Inner extends Thread {
        int countDown = 10;
        PipedInputStream reverseOut;
        Inner(String name) {
            super(name);
        }

        public void setStream (PipedInputStream stream) {
            reverseOut = stream;
        }

        public void run() {
            reverseRead();
        }
        /**
         */
        void reverseRead() {
            try {
                System.out.println("Opening in");
                DataInputStream in = new DataInputStream(reverseOut);
                BufferedReader d = new BufferedReader(new InputStreamReader(in));
                System.out.println("Opened in. Reading Chars");
                for (;;) {
                    try {
                        String c = d.readLine();
                        System.out.println("IN="+c);
                        Global.jsCall("clientNotifyData", c);
                    } catch (IOException ex) {
                        System.out.println("REACHED END");
                        break;
                    }
                }
                System.out.println("Read Chars. Closing in");
                d.close();
                System.out.println("Closed in");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        /*public void run() {
            System.out.println("MyThread running");
            while (true) {
                System.out.println(this);
                if (--countDown == 0)
                    return;
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }*/

        public String toString() {
            return getName() + ": " + countDown;
        }
    }
    /**
     * Creates a socket stream
     */
    static void create() throws IOException {
        System.out.println("Creating Streams");
        pipeIn = new PipedOutputStream();
        pipeOut = new PipedInputStream(pipeIn);
        reverseIn = new PipedOutputStream();
        reverseOut = new PipedInputStream(reverseIn);
        System.out.println("Created Streams. Starting Thread");
        inner = new Inner("name");
        inner.setStream(reverseOut);
        inner.start();
        System.out.println("Started Thread");
    }

    /**
     */
    static DataOutputStream out = null;
    public static void pipeWrite(String str) {
        boolean success = false;
        while (!success && !closed) {
            try {
                System.out.println("Opening out");
                if (out == null) {
                    out = new DataOutputStream(pipeIn);
                }
                BufferedWriter d = new BufferedWriter(new OutputStreamWriter(out));
                System.out.println("Opened out. Writing Chars: "+str);
                d.write(str,0,str.length());
                System.out.println("Written Chars. Closing out");
                d.flush();
                //d.close();
                success = true;
                System.out.println("Closed out");
            } catch (IOException e) {
                e.printStackTrace();
                try {
                    Thread.sleep(5);
                } catch (InterruptedException e2) {
                    e2.printStackTrace();
                }
            }
        }
    }
    
    /**
     */
    static void reverseRead() {
        try {
            System.out.println("Opening in");
            DataInputStream in = new DataInputStream(reverseOut);
            BufferedReader d = new BufferedReader(new InputStreamReader(in));
            System.out.println("Opened in. Reading Chars");
            for (;;) {
                try {
                    String c = d.readLine();
                    System.out.println("IN="+c);
                    Global.jsCall("clientNotifyData", c);
                } catch (IOException ex) {
                    System.out.println("REACHED END");
                    break;
                }
            }
            System.out.println("Read Chars. Closing in");
            d.close();
            System.out.println("Closed in");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     */
    public static void cheerpJNotify(String msg) {
        System.out.println("NOTIFY:"+msg);
        if (msg.equals("connected")) {
            connected = true;
        } else if (msg.equals("closed")) {
            closed = true;
        } else {
        }
    }

    /**
     */
    static boolean cheerpJConnect(String host, int port, int timeout) {
        boolean success = true;
        System.out.println("Trying to connect to CheerpJ");
        Global.jsCall("clientConnect", host, port, timeout);
        try {
        while(!connected) {
          System.out.println("connected="+connected);
          Thread.sleep(5);
        }// TODO: timeout
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        System.out.println("Connected to CheerpJ");
        return success;
    }

    static void cheerpJClose() {
        System.out.println("Closing Connection to CheerpJ");
        Global.jsCall("clientClose");
        //while(!closed); // TODO: timeout
        System.out.println("Closed Connection to CheerpJ");
    }
    
    static void cheerpJReset() {
        System.out.println("Resetting Connection to CheerpJ");
        Global.jsCall("clientReset");
        System.out.println("Resetted Connection to CheerpJ");
    }
    
    static void cheerpJSetTimeout(int timeout) {
        System.out.println("Setting CheerpJ Timeout");
        Global.jsCall("clientSetTimeout", timeout);
        System.out.println("Setted CheerpJ Timeout");
    }
    
    static int cheerpJGetTimeout() {
        System.out.println("Getting CheerpJ Timeout");
        int t = Global.jsCallI("clientGetTimeout");
        System.out.println("Got CheerpJ Timeout");
        return t;
    }

    /**
     */
    static void connect(SocketAddress address, int timeout) throws IOException {
        InetSocketAddress inet = (InetSocketAddress) address;
        CheerpJSocket.address = inet.getAddress();
        CheerpJSocket.port = inet.getPort();
        CheerpJSocket.timeout = timeout;
        if (!cheerpJConnect(inet.getHostString(), inet.getPort(), timeout)) {
            throw new IOException("cheerpJConnect failed");
        }
    }

    /**
     * Returns an input stream for this socket.
    */
    static InputStream getInputStream() throws IOException {
        return (InputStream) pipeOut;
    }

    /**
     * Returns an output stream for this socket.
     */
    static OutputStream getOutputStream() throws IOException {
        return (OutputStream) reverseIn;
    }

    /**
     * Closes this socket.
     */
    static void close() throws IOException {
        cheerpJClose();
    }

    /**
     * Places the input stream for this socket at "end of stream".
     */
    static void shutdownInput() throws IOException {
      throw new IOException("Method not implemented!");
    }

    /**
     * Disables the output stream for this socket.
     */
    static void shutdownOutput() throws IOException {
      throw new IOException("Method not implemented!");
    }

    /**
     * Returns the value of this socket's {@code address} field.
     */
    static InetAddress getInetAddress() {
        return address;
    }

    /**
     * Returns the value of this socket's {@code port} field.
     */
    static int getPort() {
        return port;
    }

    static void reset() throws IOException {
        address = null;
        port = 0;
        timeout = DEFAULT_TIMEOUT;
        connected = false;
        closed = false;
        cheerpJReset();
    }

    /**
     */
    static void setTimeout(int ms) {
        timeout = ms; // TODO
        cheerpJSetTimeout(ms);
    }
    
    static int getTimeout() {
        return cheerpJGetTimeout();
    }
}
