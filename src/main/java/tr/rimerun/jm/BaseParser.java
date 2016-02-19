package tr.rimerun.jm;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@SuppressWarnings({"UnusedDeclaration"})
public class BaseParser {
    protected LinkedInputStream input;

    public BaseParser(LinkedInputStream input) {
        this.input = input;
    }

    public Object apply(String ruleName) {
        MemoEntry memoRec = input.memo(ruleName);

        if (memoRec == null) {
            final LinkedInputStream origPos = input;
            final Failer failer = new Failer();

            origPos.memoize(ruleName, failer);
            memoRec = origPos.memoize(ruleName, new MemoEntry(evalRule(ruleName), input));

            if (failer.isUsed()) {
                final LinkedInputStream lastPos = input;

                while (true) {
                    try {
                        input = origPos;

                        final Object answer = evalRule(ruleName);

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

    protected void ensure(boolean pred) {
        if(!pred) {
            throw new ParseFailure();
        }
    }

    protected void prependInput(Object v) {
        this.input = new BasicLinkedInputStream(v, input);
    }

    private Object evalRule(String ruleName) {
        final Method method = ClassUtils.findMethod(getClass(), ruleName);

        if (method == null) {
            throw new RuntimeException("unknown rule: " + ruleName);
        }

        method.setAccessible(true);

        try {
            return method.invoke(this);
        } catch (InvocationTargetException e) {
            throw (RuntimeException) e.getCause();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
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

    @SuppressWarnings({"unchecked"})
    protected Object _form(Rule rule) {
        Object obj = apply("anything");
        ensure(obj instanceof List);

        List<Object> list = (List<Object>) obj;

        LinkedInputStream origInput = input;
        input = BasicLinkedInputStream.fromList(list);

        rule.execute();
        apply("end");

        input = origInput;
        return list;
    }

    protected Object anything() {
        Object answer = input.head();
        input = input.tail();
        return answer;
    }

    // exactly :o = :p ? (o.equals(p)) -> p
    protected Object exactly() {
        final Object o = apply("anything");
        final Object p = apply("anything");

        ensure(o.equals(p));
        return p;
    }

    protected Object seq() {
        final Collection items = (Collection) apply("anything");

        for (Object item : items) {
            prependInput(item);
            apply("exactly");
        }

        return items;
    }

    // end = ~anything
    protected Object end() {
        return _not(new Rule() {
            public Object execute() {
                return apply("anything");
            }
        });
    }
}
