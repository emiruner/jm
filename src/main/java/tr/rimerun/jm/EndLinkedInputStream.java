package tr.rimerun.jm;

public class EndLinkedInputStream extends LinkedInputStreamBase {
    public Object head() {
        throw new ParseFailure();
    }

    public LinkedInputStream tail() {
        throw new ParseFailure();
    }
}
