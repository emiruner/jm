package rme.jm;

import java.util.HashMap;
import java.util.Map;

public abstract class LinkedInputStreamBase implements LinkedInputStream {
    protected Map<Rule, MemoEntry> memo;

    protected LinkedInputStreamBase() {
        this.memo = new HashMap<Rule, MemoEntry>();
    }

    public MemoEntry memo(Rule rule) {
        return memo.get(rule);
    }

    public MemoEntry memoize(Rule rule, MemoEntry newMemo) {
        memo.put(rule, newMemo);
        return newMemo;
    }
}
