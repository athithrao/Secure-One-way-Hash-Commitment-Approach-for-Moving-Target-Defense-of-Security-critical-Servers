package edu.csu.cs.dbsec.mtd.transport;

import java.util.Arrays;
import java.util.Objects;

public class TCPNode {

    private byte[] ipAddress = null;
    private int listenPortNumber = -1;
    private String socketKey;

    public TCPNode(){}

    public TCPNode(byte[] ipAddress, int listenPortNumber, int localPortNumber) {
        this.ipAddress = ipAddress;
        this.listenPortNumber = listenPortNumber;
        this.socketKey = this.ipToString() + ":" + localPortNumber;
    }

    public TCPNode(byte[] ipAddress, int listenPortNumber)
    {
        this.ipAddress = ipAddress;
        this.listenPortNumber = listenPortNumber;
    }

    public byte[] getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(byte[] ipAddress) {
        this.ipAddress = ipAddress;
    }

    public int getListenPortNumber() {
        return listenPortNumber;
    }

    public void setListenPortNumber(int listenPortNumber) {
        this.listenPortNumber = listenPortNumber;
    }

    public String getSocketKey() {
        return socketKey;
    }

    public void setSocketKey(int localPortNumber) {
        this.socketKey = this.ipToString() + ":" + localPortNumber;
    }

    public String ipToString() {
        int i = 4;
        String ipAddress = "";
        for (byte raw : this.ipAddress) {
            ipAddress += (raw & 0xFF);
            if (--i > 0) {
                ipAddress += ".";
            }
        }
        return ipAddress;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TCPNode tcpNode = (TCPNode) o;
        return listenPortNumber == tcpNode.listenPortNumber &&
                Arrays.equals(ipAddress, tcpNode.ipAddress);
    }

    @Override
    public int hashCode() {

        int result = Objects.hash(listenPortNumber);
        result = 31 * result + Arrays.hashCode(ipAddress);
        return result;
    }

    @Override
    public String toString() {
       return this.ipToString() + ":" + listenPortNumber;
    }
}
