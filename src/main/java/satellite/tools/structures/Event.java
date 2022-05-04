package satellite.tools.structures;

public class Event {

    private long time;
    private int who;
    private int whoElse;
    private String type;

    public Event() {

    }

    public Event(long time, int who) {
        this.time = time;
        this.who = who;
    }

    public Event(String type, long time, int who) {
        this.type = type;
        this.time = time;
        this.who = who;
    }

    public Event(String type, long time, int who, int whoElse) {
        this.time = time;
        this.who = who;
        this.whoElse = whoElse;
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public long getTime() {
        return time;
    }

    public int getWho() {
        return who;
    }

    public int getWhoElse() {
        return whoElse;
    }

    @Override
    public String toString() {
        return time + "," + type + "," + who + "," + whoElse;
    }
}
