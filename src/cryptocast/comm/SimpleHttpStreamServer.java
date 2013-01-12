package cryptocast.comm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static cryptocast.util.ByteUtils.str2bytes;
/**
 * A simple tcp based http server
 */
public class SimpleHttpStreamServer implements Runnable {
    private static final int TIMEOUT_MS = 100;

    private static final Logger log = LoggerFactory
            .getLogger(SimpleHttpStreamServer.class);
    
    private InputStream in;
    private String contentType;
    private int bufsize;
    private SocketAddress addr;
    private ServerSocket sock;
    private CountDownLatch listeningEvent = new CountDownLatch(1);
    
    /**
     * Creates SimpleHttpStreamServer with the given parameter.
     * 
     * @param in The input stream.
     * @param addr the socket address.
     * @param contentType The type of the content.
     * @param bufsize The buffer size.
     */
    public SimpleHttpStreamServer(InputStream in, 
                                  SocketAddress addr, 
                                  String contentType,
                                  int bufsize) {
        this.in = in;
        this.contentType = contentType;
        this.bufsize = bufsize;
        this.addr = addr;
    }
    /**
     * Waits for a client to send a request. 
     * 
     * @return The local port of the server.
     * @throws InterruptedException
     */
    public int waitForListener() throws InterruptedException {
        listeningEvent.await();
        return sock.getLocalPort();
    }
    
    @Override
    public void run() {
        try {
            sock = new ServerSocket();
        } catch (IOException e) {
            log.error("Cannot bind to port!", e);
            return;
        }
        try {
            sock.setSoTimeout(TIMEOUT_MS);
            sock.bind(addr);
        } catch (Exception e) {
            log.error("Error while binding to socket", e);
        }
        // inform a client sticking at waitForListener()
        // that we are ready for a connection
        listeningEvent.countDown();
        try {
            for (;;) {
                try {
                    handleNextClient(sock);
                } catch (InterruptedException e) {
                    break;
                } catch (Exception e) {
                    log.error("Error while accepting or handling client", e);
                }
            }
        } finally {
            try {
                sock.close();
            } catch (Throwable e) { /* ignore errors */ }
        }
    }
    
    private void handleNextClient(ServerSocket sock) 
                   throws InterruptedException, IOException {
        Socket clientSock = acceptInterruptable(sock);
        BufferedReader clientIn = new BufferedReader(
                    new InputStreamReader(clientSock.getInputStream()));
        while (clientIn.readLine().length() > 1) {
            // fetch all request headers
        }
        OutputStream clientOut = clientSock.getOutputStream();
        clientOut.write(getChunkedResponseHeader());
        sendChunked(clientOut, in);
        clientSock.close();
    }

    private static Socket acceptInterruptable(ServerSocket sock) 
                throws InterruptedException, IOException {
        for (;;) {
            try {
                return sock.accept();
            } catch (InterruptedIOException e) { }
            checkInterrupt();
        }
    }
    
    private void sendChunked(OutputStream out, InputStream in) 
                throws InterruptedException, IOException {
        int recv;
        byte[] buffer = new byte[bufsize];
        // TODO make this interruptable?
        while ((recv = in.read(buffer)) >= 0) {
            if (recv == 0) {
                Thread.sleep(10); 
                continue; 
            }
            out.write((Integer.toHexString(recv) + "\r\n").getBytes());
            out.write(buffer, 0, recv);
            out.write("\r\n".getBytes());
            checkInterrupt();
        }
        out.write("0\r\n\r\n".getBytes());
    }

    private static void checkInterrupt() throws InterruptedException {
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }
    }
    
    private byte[] getChunkedResponseHeader() {
        return str2bytes(
             "HTTP/1.1 200 OK\r\n" +
             "Transfer-Encoding: chunked\r\n" +
             "Content-Type: " + contentType + "\r\n" +
             "Connection: keep-alive\r\n" +
             "\r\n"
        );
    }
}
