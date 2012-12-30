package cryptocast.client;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static cryptocast.util.ByteUtils.str2bytes;

public class SimpleHttpStreamServer implements Runnable {
    private static final int TIMEOUT_MS = 100;

    private static final Logger log = LoggerFactory
            .getLogger(SimpleHttpStreamServer.class);
    
    private InputStream in;
    private ServerSocket sock;
    private String contentType;
    private int bufsize;
    private SocketAddress addr;

    public SimpleHttpStreamServer(InputStream in, 
                                  SocketAddress addr, 
                                  String contentType,
                                  int bufsize) {
        this.in = in;
        this.contentType = contentType;
        this.bufsize = bufsize;
        this.addr = addr;
    }

    private static Socket acceptInterruptable(ServerSocket sock) 
                throws InterruptedException, IOException {
        for (;;) {
            try {
                return sock.accept();
            } catch (InterruptedIOException e) { }
            if (Thread.interrupted()) {
                throw new InterruptedException("Interrupted during accept()");
            }
        }
    }
    
    @Override
    public void run() {
        ServerSocket sock;
        try {
            sock = new ServerSocket();
        } catch (IOException e) {
            log.error("Cannot bind to port!", e);
            return;
        }
        try {
            sock.setSoTimeout(TIMEOUT_MS);
            sock.bind(addr);
            for (;;) {
                handleNextClient(sock);
            }
        } catch (InterruptedException e) {
        } catch (Exception e) {
            log.error("Error while accepting or handling client", e);
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
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }
        }
        out.write("0\r\n\r\n".getBytes());
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
