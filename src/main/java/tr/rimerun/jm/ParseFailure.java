package tr.rimerun.jm;

public class ParseFailure extends RuntimeException {
    public ParseFailure() {
    }

    public ParseFailure(String message) {
        super(message);
    }
}
