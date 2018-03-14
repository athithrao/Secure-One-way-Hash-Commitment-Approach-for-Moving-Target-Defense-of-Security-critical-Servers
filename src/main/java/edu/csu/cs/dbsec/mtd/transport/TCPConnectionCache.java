package edu.csu.cs.dbsec.mtd.transport;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;

public class TCPConnectionCache {

    private static HashMap<String,TCPSenderThread> connectionMap = null;

    private int totalConnections = 0;

    public TCPConnectionCache()
    {
        connectionMap = new HashMap<String,TCPSenderThread>(); // connectionIdentifier, TCPNode
    }

    public void addToConnectionMap(String key,  TCPSenderThread sender)
    {
        if(!connectionMap.containsKey(key)) {
            connectionMap.put(key, sender);
        }
    }

    public TCPSenderThread getTCPSender(String key)
    {
        if(connectionMap.containsKey(key))
            return connectionMap.get(key);
        return null;
    }

    public void removeFromConnectionMap(String key)
    {
        if(connectionMap.containsKey(key))
        {
            connectionMap.remove(key);
            --totalConnections;
        }
    }

    public void closeAllConnections() throws IOException
    {
        Iterator<String> keySetIterator = connectionMap.keySet().iterator();

        while (keySetIterator.hasNext()) {
            String key = keySetIterator.next();
            connectionMap.get(key).close();
            this.removeFromConnectionMap(key);
        }
    }

    public int getTotalConnections() {
        return totalConnections;
    }
}
