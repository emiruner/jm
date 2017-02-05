package rme.jm;

import java.io.IOException;
import java.io.Reader;

public class ReaderBackedLinkedInputStream extends LinkedInputStreamBase {
    private Reader reader;
    private int ch;
    private boolean readCh;
    private ReaderBackedLinkedInputStream tail;

    public ReaderBackedLinkedInputStream(Reader reader) {
        this.reader = reader;
        this.readCh = false;
    }

    public Object head() {
        if(!readCh) {
            readCh = true;

            try {
                ch = reader.read();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }

        if(ch == -1) {
            throw new ParseFailure();
        }

        return (char) ch;
    }

    public LinkedInputStream tail() {
        if(tail == null) {
            tail = new ReaderBackedLinkedInputStream(reader);
        }

        return tail;
    }
}
