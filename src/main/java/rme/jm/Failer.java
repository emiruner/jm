package rme.jm;

public class Failer extends MemoEntry {
    private boolean used;

    public Failer() {
        super(null, null);
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    public String toString() {
        return "failer[" + (used ? "used" : "unused") + "]";
    }
}
