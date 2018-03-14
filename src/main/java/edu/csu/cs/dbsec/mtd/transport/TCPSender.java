package edu.csu.cs.dbsec.mtd.transport;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class TCPSender {

    private Socket socket;
    private DataOutputStream dout = null;


    public TCPSender(Socket socket) throws IOException{
        this.socket = socket;
        this.dout = new DataOutputStream(socket.getOutputStream());
    }

    public void sendData(byte[] dataToSend) throws IOException {

        int dataLength = dataToSend.length;
        dout.writeInt(dataLength);
        dout.write(dataToSend, 0, dataLength);
        dout.flush();
    }

    public boolean isClosed()
    {
        if(socket.isClosed())
            return true;
        return false;
    }

    public void close() throws IOException
    {
        dout.close();
        socket.close();
    }

    public int getLocalPort()
    {
        return socket.getLocalPort();
    }
}