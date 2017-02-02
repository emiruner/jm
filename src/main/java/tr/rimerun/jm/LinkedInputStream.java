package tr.rimerun.jm;

public interface LinkedInputStream {
    Object head();

    LinkedInputStream tail();

    MemoEntry memo(Rule rule);

    MemoEntry memoize(Rule rule, MemoEntry newMemo);
}
