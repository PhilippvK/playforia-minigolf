/*
 */

package phi.net;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

import java.net.SocketException;
import java.net.SocketAddress;
import java.net.InetAddress;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
/**
 */
public
class Socket extends java.net.Socket implements java.io.Closeable {
    /**
     * Various states of this socket.
     */
    private boolean created = false;
    private boolean connected = false;
    private boolean closed = false;
    private Object closeLock = new Object();
    private SocketAddress address = null;
    private boolean shutIn = false;
    private boolean shutOut = false;
    private java.net.Socket socket = null;
    private boolean isApplet = false;

    /**
     */
    public Socket(String host, int port, boolean isApplet)
        throws UnknownHostException, IOException
    {
        if (!isApplet) {
            this.socket = new java.net.Socket(host, port);
        } else {
            if (address == null)
                throw new NullPointerException();
            this.address = host != null ? new InetSocketAddress(host, port) : new InetSocketAddress(InetAddress.getByName(null), port);
            CheerpJSocket.create();
            try {
                connect(address);
            } catch (IOException e) {
                System.out.println("CLOSE OF SOCKET DUE TO EX");
                try {
                    close();
                } catch (IOException ce) {
                    e.addSuppressed(ce);
                }
                throw e;
            }
        }
    }

    /**
     */
    public void connect(SocketAddress endpoint) throws IOException {
        connect(endpoint, 0);
    }

    /**
     */
    public void connect(SocketAddress endpoint, int timeout) throws IOException {
        if (endpoint == null)
            throw new IllegalArgumentException("connect: The address can't be null");

        if (timeout < 0)
          throw new IllegalArgumentException("connect: timeout can't be negative");

        if (isClosed())
            throw new IOException("Socket is closed");

        if (isConnected())
            throw new IOException("already connected");

        if (!(endpoint instanceof InetSocketAddress))
            throw new IllegalArgumentException("Unsupported address type");

        InetSocketAddress epoint = (InetSocketAddress) endpoint;
        InetAddress addr = epoint.getAddress();
        int port = epoint.getPort();
        checkAddress(addr, "connect");

        if (!created)
            CheerpJSocket.create();
            created = true;
        if (!connected) {
            CheerpJSocket.connect(endpoint, timeout);
        }          

        connected = true;
    }

    /**
     */
    private void checkAddress (InetAddress addr, String op) {
        if (addr == null) {
            return;
        }
        if (!(addr instanceof Inet4Address || addr instanceof Inet6Address)) {
            throw new IllegalArgumentException(op + ": invalid address type");
        }
    }

    /**
     */
    public InetAddress getInetAddress() {
        if (!isConnected())
            return null;
        InetSocketAddress epoint = (InetSocketAddress) address;
        return epoint.getAddress();
    }
    
    public int getPort() {
        if (!isConnected())
            return 0;
        InetSocketAddress epoint = (InetSocketAddress) address;
        return epoint.getPort();
    }

    public SocketAddress getSocketAddress() {
        if (!isConnected())
            return null;
        return new InetSocketAddress(getInetAddress(), getPort());
    }

    /**
     */
    public InputStream getInputStream() throws IOException {
        if (this.socket != null)
            return this.socket.getInputStream();

        if (isClosed())
            throw new IOException("Socket is closed");
        if (!isConnected())
            throw new IOException("Socket is not connected");
        if (isInputShutdown())
            throw new IOException("Socket input is shutdown");
        return CheerpJSocket.getInputStream();
    }

    /**
     */
    public OutputStream getOutputStream() throws IOException {
        if (this.socket != null)
            return this.socket.getOutputStream();

        if (isClosed())
            throw new IOException("Socket is closed");
        if (!isConnected())
            throw new IOException("Socket is not connected");
        if (isOutputShutdown())
            throw new IOException("Socket output is shutdown");
        return CheerpJSocket.getOutputStream();
    }

    /**
     */
    public synchronized void close() throws IOException {
        if (this.socket != null) {
            this.socket.close();
            return;
        }
        
        System.out.println("CLOSE OF SOCKET REQUESTED");
        synchronized(closeLock) {
            if (isClosed())
                return;
            if (created) {
                CheerpJSocket.close();
            }
            closed = true;
        }
    }

    /**
     */
    public void shutdownInput() throws IOException
    {
        if (isClosed())
            throw new IOException("Socket is closed");
        if (!isConnected())
            throw new IOException("Socket is not connected");
        if (isInputShutdown())
            throw new IOException("Socket input is already shutdown");
        CheerpJSocket.shutdownInput();
        shutIn = true;
    }

    /**
     */
    public void shutdownOutput() throws IOException
    {
        if (isClosed())
            throw new IOException("Socket is closed");
        if (!isConnected())
            throw new IOException("Socket is not connected");
        if (isOutputShutdown())
            throw new IOException("Socket output is already shutdown");
        CheerpJSocket.shutdownOutput();
        shutOut = true;
    }

    /**
     */
    public String toString() {
        if (isConnected()) {
            return "Socket[addr=" + getInetAddress() +
                ",port=" + getPort() + "]";
        }
        return "Socket[unconnected]";
    }

    /**
     */
    public boolean isConnected() {
        return connected;
    }

    /**
     */
    public boolean isClosed() {
        synchronized(closeLock) {
            return closed;
        }
    }

    /**
     */
    public boolean isInputShutdown() {
        return shutIn;
    }

    /**
     */
    public boolean isOutputShutdown() {
        return shutOut;
    }

    /**
     */
    public synchronized void setSoTimeout(int timeout) throws SocketException {
        if (isClosed())
            throw new SocketException("Socket is closed");
        if (timeout < 0)
          throw new IllegalArgumentException("timeout can't be negative");

        CheerpJSocket.setTimeout(timeout);
    }
    
    /**
     */
    public synchronized int getSoTimeout() throws SocketException {
        if (isClosed())
            throw new SocketException("Socket is closed");
        return CheerpJSocket.getTimeout();
    }
}
