package tr.rimerun.jm;

public class MemoEntry {
    private Object answer;
    private LinkedInputStream nextPos;

    public MemoEntry(Object answer, LinkedInputStream nextPos) {
        this.answer = answer;
        this.nextPos = nextPos;
    }

    public Object getAnswer() {
        return answer;
    }

    public void setAnswer(Object answer) {
        this.answer = answer;
    }

    public LinkedInputStream getNextPos() {
        return nextPos;
    }

    public void setNextPos(LinkedInputStream nextPos) {
        this.nextPos = nextPos;
    }

    public String toString() {
        return "memo[answer: " + answer + ", next position: " + nextPos + "]";
    }
}
