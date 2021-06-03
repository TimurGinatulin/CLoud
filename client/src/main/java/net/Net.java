package net;

import java.io.*;
import java.net.Socket;

public class Net implements Closeable {

    private int port;
    private Socket socket;
    private DataInputStream is;
    private DataOutputStream os;
    private InputStream in;
    private OutputStream ou;

    private Net(int port) throws IOException {
        socket = new Socket("localhost", port);
        is = new DataInputStream(socket.getInputStream());
        os = new DataOutputStream(socket.getOutputStream());
        in= socket.getInputStream();
    }

    public static Net start(int port) throws IOException {
        return new Net(port);
    }

    public void sendMessage(String msg) throws IOException {
        os.writeUTF(msg);
        os.flush();
    }
    public InputStream getIO(){
        return in;
    }
    public OutputStream getOu(){
        return ou;
    }

    public String getMessage() throws IOException {
        return is.readUTF();
    }
    public int readBuf(byte[] buffer) throws IOException {
        return is.read(buffer);
    }
    @Override
    public void close() throws IOException {
        os.close();
        is.close();
        socket.close();
    }
}
