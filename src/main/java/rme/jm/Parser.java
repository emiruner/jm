package rme.jm;

import java.util.ArrayList;
import java.util.List;

public class Parser {
    protected LinkedInputStream input;

    public Parser(LinkedInputStream input) {
        this.input = input;
    }

    public Object apply(Rule rule) {
        MemoEntry memoRec = input.memo(rule);

        if (memoRec == null) {
            final LinkedInputStream origPos = input;
            final Failer failer = new Failer();

            origPos.memoize(rule, failer);
            memoRec = origPos.memoize(rule, new MemoEntry(rule.execute(this), input));

            if (failer.isUsed()) {
                final LinkedInputStream lastPos = input;

                while (true) {
                    try {
                        input = origPos;

                        final Object answer = rule.execute(this);

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

    public void ensure(boolean pred) {
        if(!pred) {
            throw new ParseFailure();
        }
    }

    public void ensure(boolean pred, String errorMessage) {
        if(!pred) {
            throw new ParseFailure(errorMessage);
        }
    }

    protected void prependInput(Object v) {
        this.input = new SimpleLinkedInputStream(v, input);
    }

    public Object applyWithArgs(Rule rule, Object arg) {
        prependInput(arg);
        return apply(rule);
    }

    public List<Object> _many(Rule rule) {
        ArrayList<Object> result = new ArrayList<>();

        while (true) {
            final LinkedInputStream lastPos = input;

            try {
                result.add(rule.execute(this));
            } catch (ParseFailure ex) {
                input = lastPos;
                break;
            }
        }

        return result;
    }

    public List<Object> _many1(Rule rule) {
        ArrayList<Object> result = new ArrayList<>();

        result.add(rule.execute(this));
        result.addAll(_many(rule));

        return result;
    }

    public Object _or(Rule... rules) {
        LinkedInputStream origPos = input;

        for (Rule rule : rules) {
            try {
                return rule.execute(this);
            } catch (ParseFailure ex) {
                input = origPos;
            }
        }

        throw new ParseFailure();
    }

    public Object _not(Rule rule) {
        final LinkedInputStream origPos = input;

        try {
            rule.execute(this);
        } catch (ParseFailure ex) {
            input = origPos;
            return null;
        }

        throw new ParseFailure();
    }

    public Object _opt(Rule rule) {
        LinkedInputStream origPos = input;

        try {
            return rule.execute(this);
        } catch (ParseFailure ex) {
            input = origPos;
        }

        return null;
    }

    public Object _anything() {
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

        List<?> list = (List<?>) obj;
        input = SimpleLinkedInputStream.fromList(list);

        rule.execute(this);

        if(!(input instanceof EndLinkedInputStream)) {
            input = origInput;
            throw new ParseFailure();
        }

        input = origInput.tail();
        return list;
    }
}
