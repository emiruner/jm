package tr.rimerun.jm;

import java.util.HashMap;
import java.util.Map;

public abstract class LinkedInputStreamBase implements LinkedInputStream {
    protected Map<String, MemoEntry> memo;

    protected LinkedInputStreamBase() {
        this.memo = new HashMap<String, MemoEntry>();
    }

    public MemoEntry memo(String rule) {
        return memo.get(rule);
    }

    public MemoEntry memoize(String rule, MemoEntry newMemo) {
        memo.put(rule, newMemo);
        return newMemo;
    }
}
