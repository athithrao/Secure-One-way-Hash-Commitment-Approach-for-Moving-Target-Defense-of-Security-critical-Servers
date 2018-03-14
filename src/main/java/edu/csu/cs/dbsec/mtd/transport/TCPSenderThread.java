package edu.csu.cs.dbsec.mtd.transport;

import cs455.overlay.util.BlockingQueue;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;


public class TCPSenderThread implements Runnable {

    private Socket socket;
    private DataOutputStream dout = null;
    private volatile BlockingQueue sendQueue = null;
    private volatile boolean exitFlag = false;

    public TCPSenderThread(Socket socket) throws IOException {
        this.socket = socket;
        this.socket.setSendBufferSize(1000000);
        this.dout = new DataOutputStream(socket.getOutputStream());
        sendQueue = new BlockingQueue();
    }

    public void sendData(byte[] dataToSend)
    {
            this.sendQueue.enqueue(dataToSend);
    }

    public boolean isClosed()
    {
        if(socket.isClosed())
            return true;
        return false;
    }

    public void close()throws IOException
    {
        this.dout.close();
        this.socket.close();
        this.exitFlag = true;
    }

    public void run()
    {
        byte[] data;
        int dataLength;

        while (!exitFlag && !socket.isClosed())
        {
            try
            {
                data = sendQueue.dequeue();
                dataLength = data.length;
                dout.writeInt(dataLength);
                dout.write(data, 0, dataLength);
                dout.flush();
            }
            catch (InterruptedException ie)
            {
                System.out.println("Exiting Sender Thread");
                Thread.currentThread().interrupt();
                //ie.printStackTrace();
                break;
            }
            catch (SocketException se) {
                //System.out.println(se.getMessage());
                System.out.println("Receiver socket and thread closed! - Socket Exception");
                se.printStackTrace();
                break;
            }
            catch (IOException ioe) {
                //System.out.println(ioe.getMessage());
                System.out.println("Receiver socket and thread closed! - IO Exception ");
                ioe.printStackTrace();
                break;
            }
        }


    }

    public int getLocalPort()
    {
        return socket.getLocalPort();
    }

}
