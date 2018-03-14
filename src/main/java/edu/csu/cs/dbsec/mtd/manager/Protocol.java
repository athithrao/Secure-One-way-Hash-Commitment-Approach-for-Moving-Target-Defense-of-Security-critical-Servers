package edu.csu.cs.dbsec.mtd.manager;

public class Protocol {

    public static final int DEBUG = 1;

    public static final int COMMIT = 2;
    public static final int ESTIMATE = 3;
    public static final int CONFIRM = 4;
    public static final int SUSPECT = 5;
    public static final int WALK = 6;
    public static final int KEYS = 6;
    public static final int SERVERUP = 6;

    public enum State {
        COMMIT, ESTIMATE, CONFIRM, READY, NREADY, LEADER, SUSPECT, IDLE, RESET
    }

}
