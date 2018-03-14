package edu.csu.cs.dbsec.mtd.transport;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

public class TCPReceiverThread implements Runnable {

    private Socket socket;
    private DataInputStream din;
    private EventFactory eventFactory = null;
    private volatile boolean exitFlag = false;

    public TCPReceiverThread(Socket socket) throws IOException{
        this.socket = socket;
        this.socket.setSendBufferSize(63999);
        eventFactory = EventFactory.getInstance();
        din = new DataInputStream(socket.getInputStream());
    }

    public void run()
    {
        int dataLength;
        while (!exitFlag && !socket.isClosed()) {
            try {

                    dataLength = din.readInt();
                    if(dataLength == -1) {
                        break;
                    }
                    byte[] data = new byte[dataLength];
                    din.readFully(data, 0, dataLength);
                    //Event needs to be processed here!

                    //synchronized (eventFactory)
                    {
                        eventFactory.processMessage(data);
                    }

                } catch (SocketException se) {
                    //System.out.println(se.getMessage());
                    System.out.println("Receiver socket and thread closed! - Socket Exception");
                    //se.printStackTrace();
                    break;
                }
                catch (IOException ioe) {
                    //System.out.println(ioe.getMessage());
                    System.out.println("Receiver socket and thread closed! - one node exited the overlay.");
                    //ioe.printStackTrace();
                    break;
                }

         }
    }

    public void close()throws IOException
    {
        this.socket.close();
        this.din.close();
        exitFlag = true;
    }
}
