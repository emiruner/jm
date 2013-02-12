package tr.rimerun.jm;

public interface LinkedInputStream {
    Object head();

    LinkedInputStream tail();

    MemoEntry memo(String rule);

    MemoEntry memo(String rule, MemoEntry newMemo);
}
