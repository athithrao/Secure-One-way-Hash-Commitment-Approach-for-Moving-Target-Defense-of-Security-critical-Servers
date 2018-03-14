package edu.csu.cs.dbsec.mtd.transport;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class TCPServerThread implements Runnable {

    private ServerSocket serverSocket;
    private boolean exitFlag = false;
    private TCPConnection connection = null;

    public TCPServerThread(TCPConnection connect, int portNumber) throws IOException
    {
        connection = connect;
        serverSocket = new ServerSocket(portNumber);
        serverSocket.setReuseAddress(true);
    }

    public void run()
    {
        while(!exitFlag && !serverSocket.isClosed())
        {
            try
            {
                Socket receiveSocket = serverSocket.accept();
                //send the socket to TCPConnection
                connection.addToCache(receiveSocket);
                Thread receiveThread = new Thread(new TCPReceiverThread(receiveSocket),"ReceiverThread");
                receiveThread.start();
            }
            catch(IOException e)
            {
                System.out.println("Server socket closed.");
                break;
            }

        }
    }

    public void close() throws IOException
    {
            serverSocket.close();
            exitFlag = true;
    }

}
