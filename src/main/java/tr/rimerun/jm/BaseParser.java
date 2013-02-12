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

            origPos.memo(ruleName, failer);
            memoRec = origPos.memo(ruleName, new MemoEntry(evalRule(ruleName), input));

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

    @SuppressWarnings({"unchecked"})
    public Object applyWithPred(String ruleName, Predicate pred) {
        Object answer = apply(ruleName);

        if (pred.eval(answer)) {
            return answer;
        }

        throw new ParseFailure();
    }

    public Object applyWithArgs(String ruleName, Object... params) {
        for (int i = params.length - 1; i >= 0; --i) {
            prependInput(params[i]);
        }

        return evalRule(ruleName);
    }

    private void prependInput(Object v) {
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

    protected List<Object> _many(SimpleFn fun) {
        ArrayList<Object> result = new ArrayList<Object>();

        while (true) {
            final LinkedInputStream lastPos = input;

            try {
                result.add(fun.call());
            } catch (ParseFailure ex) {
                input = lastPos;
                break;
            }
        }

        return result;
    }

    protected List<Object> _many(final String ruleName) {
        return _many(new SimpleFn() {
            public Object call() {
                return apply(ruleName);
            }
        });
    }

    protected List<Object> _many1(SimpleFn fun) {
        ArrayList<Object> result = new ArrayList<Object>();

        result.add(fun.call());
        result.addAll(_many(fun));

        return result;
    }

    protected List<Object> _many1(final String ruleName) {
        return _many1(new SimpleFn() {
            public Object call() {
                return apply(ruleName);
            }
        });
    }

    protected Object _or(SimpleFn... funs) {
        LinkedInputStream origPos = input;

        for (SimpleFn fun : funs) {
            try {
                return fun.call();
            } catch (ParseFailure ex) {
                input = origPos;
            }
        }

        throw new ParseFailure();
    }

    protected Object _not(SimpleFn simpleFn) {
        final LinkedInputStream origPos = input;

        try {
            simpleFn.call();
        } catch (ParseFailure ex) {
            input = origPos;
            return true;
        }

        throw new ParseFailure();
    }

    protected Object _opt(SimpleFn simpleFn) {
        LinkedInputStream origPos = input;

        try {
            return simpleFn.call();
        } catch (ParseFailure ex) {
            input = origPos;
        }

        return null;
    }

    @SuppressWarnings({"unchecked"})
    protected Object _form(SimpleFn simpleFn) {
        List<Object> list = (List<Object>) applyWithPred("anything", new Predicate() {
            public boolean eval(Object o) {
                return o instanceof List;
            }
        });

        LinkedInputStream origInput = input;
        input = BasicLinkedInputStream.fromList(list);

        simpleFn.call();
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
        return applyWithPred("anything", new Predicate() {
            public boolean eval(Object parsed) {
                return o.equals(parsed);
            }
        });
    }

    protected Object seq() {
        final Collection items = (Collection) apply("anything");

        for (Object o : items) {
            applyWithArgs("exactly", o);
        }

        return items;
    }

    // end = ~anything
    protected Object end() {
        return _not(new SimpleFn() {
            public Object call() {
                return apply("anything");
            }
        });
    }
}
