package tr.rimerun.jm;

import java.util.ArrayList;
import java.util.List;

public abstract class Parser {
    protected LinkedInputStream input;

    public Parser(LinkedInputStream input) {
        this.input = input;
    }

    public Object apply(String ruleName) {
        MemoEntry memoRec = input.memo(ruleName);

        if (memoRec == null) {
            final LinkedInputStream origPos = input;
            final Failer failer = new Failer();

            origPos.memoize(ruleName, failer);
            memoRec = origPos.memoize(ruleName, new MemoEntry(eval(ruleName), input));

            if (failer.isUsed()) {
                final LinkedInputStream lastPos = input;

                while (true) {
                    try {
                        input = origPos;

                        final Object answer = eval(ruleName);

                        if (input == lastPos) {
                            break;
                        }

                        memoRec.setAnswer(answer);
                        memoRec.setNextPos(input);
                    } catch (ParseFailure ex) {
                        break;
                    }
                }
            }
        } else if (memoRec instanceof Failer) {
            ((Failer) memoRec).setUsed(true);
            throw new ParseFailure();
        }

        input = memoRec.getNextPos();
        return memoRec.getAnswer();
    }

    protected abstract Object eval(String ruleName);

    protected void ensure(boolean pred) {
        if(!pred) {
            throw new ParseFailure();
        }
    }

    protected void prependInput(Object v) {
        this.input = new SimpleLinkedInputStream(v, input);
    }

    protected Object applyWithArgs(String rule, Object arg) {
        prependInput(arg);
        return apply(rule);
    }

    protected List<Object> _many(Rule rule) {
        ArrayList<Object> result = new ArrayList<Object>();

        while (true) {
            final LinkedInputStream lastPos = input;

            try {
                result.add(rule.execute());
            } catch (ParseFailure ex) {
                input = lastPos;
                break;
            }
        }

        return result;
    }

    protected List<Object> _many1(Rule rule) {
        ArrayList<Object> result = new ArrayList<Object>();

        result.add(rule.execute());
        result.addAll(_many(rule));

        return result;
    }

    protected Object _or(Rule... rules) {
        LinkedInputStream origPos = input;

        for (Rule rule : rules) {
            try {
                return rule.execute();
            } catch (ParseFailure ex) {
                input = origPos;
            }
        }

        throw new ParseFailure();
    }

    protected Object _not(Rule rule) {
        final LinkedInputStream origPos = input;

        try {
            rule.execute();
        } catch (ParseFailure ex) {
            input = origPos;
            return null;
        }

        throw new ParseFailure();
    }

    protected Object _opt(Rule rule) {
        LinkedInputStream origPos = input;

        try {
            return rule.execute();
        } catch (ParseFailure ex) {
            input = origPos;
        }

        return null;
    }

    protected Object _anything() {
        Object answer = input.head();
        input = input.tail();
        return answer;
    }

    protected Object _form(Rule rule) {
        LinkedInputStream origInput = input;

        Object obj = _anything();

        if(!(obj instanceof List)) {
            input = origInput;
            throw new ParseFailure();
        }

        List list = (List) obj;
        input = SimpleLinkedInputStream.fromList(list);

        rule.execute();

        if(!(input instanceof EndLinkedInputStream)) {
            input = origInput;
            throw new ParseFailure();
        }

        input = origInput.tail();
        return list;
    }
}
