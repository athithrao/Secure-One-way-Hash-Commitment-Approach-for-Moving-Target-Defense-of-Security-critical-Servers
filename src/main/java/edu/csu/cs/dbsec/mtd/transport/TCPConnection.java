package edu.csu.cs.dbsec.mtd.transport;

import java.io.IOException;
import java.net.*;
import edu.csu.cs.dbsec.mtd.manager.Protocol;

public class TCPConnection {

    private static TCPNode myTCPNode = null;
    private static TCPNode registryTCPNode = null;
    
    private static TCPConnectionCache connectionCache = null;

    private TCPSenderThread registryTCPSender = null;
    private TCPReceiverThread registryTCPReceiver;
    
    private TCPServerThread server;
    
    private Thread serverThread = null;
    private Thread registrySenderThread = null;
    private Thread registryReceiverThread = null;
    //Node node = null;

    public byte[] getMyIPAddress()
    {
        return myTCPNode.getIpAddress();
    }

    public int getMyListenPortNumber()
    {
        return myTCPNode.getListenPortNumber();
    }
    public int getRegistrySocketLocalPortNumber()
    {
        return registryTCPSender.getLocalPort();
    }

    public TCPConnection(byte[] registryIP, int registryPort)
    {
        //this.node = n;
        registryTCPNode = new TCPNode();
        registryTCPNode.setIpAddress(registryIP);
        registryTCPNode.setListenPortNumber(registryPort);

        try {
            myTCPNode = new TCPNode();
            myTCPNode.setIpAddress(InetAddress.getLocalHost().getAddress());
            myTCPNode.setListenPortNumber(findFreePort());
        }
        catch (UnknownHostException e) {
            System.out.println("Error occurred in MessagingNode()");
            e.printStackTrace();
        }
        if (Protocol.DEBUG == 1) {
            System.out.println("IP Address - " + myTCPNode.ipToString());
            System.out.println("Host Name - " + myTCPNode.getListenPortNumber());
        }
    }
    public TCPConnection(int port)
    {
        //this.node = n;
        try {
            myTCPNode = new TCPNode();
            myTCPNode.setIpAddress(InetAddress.getLocalHost().getAddress());
            myTCPNode.setListenPortNumber(port);
        }
        catch (UnknownHostException e) {
            System.out.println("Error occurred in MessagingNode()");
            e.printStackTrace();
        }
        if (Protocol.DEBUG == 1) {
            System.out.println("IP Address - " + myTCPNode.ipToString());
            System.out.println("Host Name - " + myTCPNode.getListenPortNumber());
        }
    }

    int findFreePort() {
        boolean result = false;
        int port = Protocol.START_PORT_NO;
        do {
            try {
                (new ServerSocket(port)).close();
                result = true;
            } catch (SocketException e) {
                // Could not connect.
                port++;
            } catch (IOException e) {
                System.out.println("Error occurred in findFreePort()");
                e.printStackTrace();
            }

        } while (result == false);
        return port;
    }


    public void initialize()
    {
        try {
            server = new TCPServerThread(this,myTCPNode.getListenPortNumber());
            serverThread = new Thread(server, "TCPServerThread");
            serverThread.start();
            connectionCache = new TCPConnectionCache();
        }
        catch (IOException e)
        {
            System.out.println("Error in TCPConnection.Initialize().");
            e.printStackTrace();
        }
    }

    public void close() throws IOException
    {
        server.close();
        registryTCPSender.close();
        registrySenderThread.interrupt();
        if(registryTCPReceiver != null)
            registryTCPReceiver.close();
    }

    public void closeMessgagingNodeConnections() throws IOException
    {
        connectionCache.closeAllConnections();

    }

    public int openRegistryConnection() throws IOException
    {
        Socket socket = new Socket(registryTCPNode.ipToString(),registryTCPNode.getListenPortNumber());

        registryTCPSender = new TCPSenderThread(socket);
        registryTCPReceiver = new TCPReceiverThread(socket);
        registrySenderThread = new Thread(registryTCPSender, "registryTCPSender");
        registrySenderThread.start();
        registryReceiverThread = new Thread(registryTCPReceiver,"registryTCPReceiver");
        registryReceiverThread.start();

        return registryTCPSender.getLocalPort();
    }

    public int openNodeConnection(TCPNode node) throws  IOException
    {
        Socket socket = new Socket(node.ipToString(),node.getListenPortNumber());
        //I am not creating the key with the localPort data t simplify the need to having a HashMap nodeList
        //connectionCache.addToConnectionMap(socket.getInetAddress().getHostAddress() + ":"+ socket.getLocalPort(),socket);
        TCPSenderThread sender = new TCPSenderThread(socket);
        Thread thread = new Thread(sender, "TCPSenderThread ");
        thread.start();
        connectionCache.addToConnectionMap(socket.getInetAddress().getHostAddress() + ":"+ node.getListenPortNumber(),sender);
        //Thread for receive is not required as the design is uni-directional

        //I am not returning the localPort data to simplify the need to having a HashMap nodeList
        //return socket.getLocalPort();

        return node.getListenPortNumber();

    }

    public boolean sendToRegistry(byte[] data)
    {
        registryTCPSender.sendData(data);
        return true;
    }

    public boolean sendToNode(String key, byte[] data)
    {
        connectionCache.getTCPSender(key).sendData(data);
        return true;
    }

    public boolean validateSocketAddress(String key)
    {
        if(connectionCache.getTCPSender(key) != null)
            return true;
        return false;
    }

    public void addToCache(Socket socket) throws IOException
    {
        TCPSenderThread sender = new TCPSenderThread(socket);
        Thread thread = new Thread(sender,"TCPSenderThread");
        thread.start();
        connectionCache.addToConnectionMap(socket.getInetAddress().getHostAddress() + ":"+ socket.getPort(),sender);
    }
    public void removeFromCache(String key)
    {
        connectionCache.removeFromConnectionMap(key);
    }
}
