package tr.rimerun.jm;

import java.util.List;

import static tr.rimerun.jm.Util.list;

// ometa SimpleExpressionParser <: BaseTestParser
@SuppressWarnings({"UnusedDeclaration"})
public class TrialParser extends BaseTestParser {
    public TrialParser(LinkedInputStream input) {
        super(input);
    }

    // exp = exp:e spaces '+' num:n -> new Object[]{"Add", e, n}
    //     | exp:e spaces '-' num:n -> new Object[]{"Sub", e, n}
    //     | num:n                  -> n
    private Object exp() {
        return _or(
                new Rule() {
                    public Object call() {
                        Object e = apply("exp");
                        apply("spaces");
                        applyWithArgs("exactly", '+');
                        Object n = apply("num");

                        return list("Add", e, n);
                    }
                },

                new Rule() {
                    public Object call() {
                        Object e = apply("exp");
                        apply("spaces");
                        applyWithArgs("exactly", '-');
                        Object n = apply("num");

                        return list("Sub", e, n);
                    }
                },

                new Rule() {
                    public Object call() {
                        return apply("num");
                    }
                }
        );
    }

    // notx = ~'x' :a -> a
    private Object notx() {
        _not(new Rule() {
            public Object call() {
                return applyWithArgs("exactly", 'x');
            }
        });

        return apply("anything");
    }

    // abAndEnd = 'a' 'b' end
    private Object abAndEnd() {
        applyWithArgs("exactly", 'a');
        applyWithArgs("exactly", 'b');
        return apply("end");
    }

    // hello3AndSomething = ['hello' 3 [:a 8]] -> a
    private Object hello3AndSomething() {
        final Object[] aHolder = new Object[1];

        _form(new Rule() {
            public Object call() {
                applyWithArgs("exactly", "hello");
                applyWithArgs("exactly", 3);

                return _form(new Rule() {
                    public Object call() {
                        aHolder[0] = apply("anything");
                        return applyWithArgs("exactly", 8);
                    }
                });
            }
        });

        return aHolder[0];
    }

    // complexList = [1 [3 5]+:a 7] -> a
    private Object complexList() {
        final Object[] aHolder = new Object[1];

        _form(new Rule() {
            public Object call() {
                applyWithArgs("exactly", 1);

                aHolder[0] = _many1(new Rule() {
                    public Object call() {
                        return _form(new Rule() {
                            public Object call() {
                                applyWithArgs("exactly", 3);
                                return applyWithArgs("exactly", 5);
                            }
                        });
                    }
                });

                return applyWithArgs("exactly", 7);
            }
        });

        return aHolder[0];
    }

    // listOf :p = apply(p):first ("," apply(p))*:rest -> {List result = list(first); result.addAll(rest)); result}
    @SuppressWarnings({"unchecked"})
    private Object listOf() {
        final String p = (String) apply("anything");

        final Object first = apply(p);
        final List<Object> rest = _many(new Rule() {
            public Object call() {
                applyWithArgs("token", ",");
                return apply(p);
            }
        });

        final List result = list(first);
        result.addAll(rest);

        return result;
    }

    // helloWorld = "hello" "world"
    private Object helloWorld() {
        applyWithArgs("token", "hello");
        return applyWithArgs("token", "world");
    }
}
