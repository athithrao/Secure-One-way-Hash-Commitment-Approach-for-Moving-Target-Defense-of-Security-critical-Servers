package edu.csu.cs.dbsec.mtd.utils;

import java.util.LinkedList;
import java.util.List;

////https://gist.github.com/dougnukem/1241317/4c22f86b0f68945023e1d47070018988644321ab#file-blockingqueue-java-L15

public class BlockingQueue {

    private List<byte[]> queue = new LinkedList<>();

    public BlockingQueue() {
    }

    public synchronized void enqueue(byte[] data) {
        queue.add(data);
        // Wake up anyone waiting for something to be put on the queue.
        notifyAll();
    }

    public synchronized byte[] dequeue() throws InterruptedException {

        while (queue.isEmpty()) {
            wait();
        }
        return queue.remove(0);
    }
}
