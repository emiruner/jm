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
                    public Object execute() {
                        Object e = apply("exp");
                        apply("spaces");

                        prependInput('+');
                        apply("exactly");

                        Object n = apply("num");

                        return list("Add", e, n);
                    }
                },

                new Rule() {
                    public Object execute() {
                        Object e = apply("exp");
                        apply("spaces");

                        prependInput('-');
                        apply("exactly");

                        Object n = apply("num");

                        return list("Sub", e, n);
                    }
                },

                new Rule() {
                    public Object execute() {
                        return apply("num");
                    }
                }
        );
    }

    // notx = ~'x' :a -> a
    private Object notx() {
        _not(new Rule() {
            public Object execute() {
                prependInput('x');
                return apply("exactly");
            }
        });

        return apply("anything");
    }

    // abAndEnd = 'a' 'b' end
    private Object abAndEnd() {
        prependInput('a');
        apply("exactly");

        prependInput('b');
        apply("exactly");

        return apply("end");
    }

    // helloAnd3 = ['hello' 3]
    private Object helloAnd3() {
        _form(new Rule() {
            public Object execute() {
                prependInput("hello");
                apply("exactly");

                prependInput(3);
                return apply("exactly");
            }
        });

        return null;
    };

    // hello3AndSomething = ['hello' 3 [:a 8]] -> a
    private Object hello3AndSomething() {
        final Object[] aHolder = new Object[1];

        _form(new Rule() {
            public Object execute() {
                prependInput("hello");
                apply("exactly");

                prependInput(3);
                apply("exactly");

                return _form(new Rule() {
                    public Object execute() {
                        aHolder[0] = apply("anything");

                        prependInput(8);
                        return apply("exactly");
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
            public Object execute() {
                prependInput(1);
                apply("exactly");

                aHolder[0] = _many1(new Rule() {
                    public Object execute() {
                        return _form(new Rule() {
                            public Object execute() {
                                prependInput(3);
                                apply("exactly");

                                prependInput(5);
                                return apply("exactly");
                            }
                        });
                    }
                });

                prependInput(7);
                return apply("exactly");
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
            public Object execute() {
                prependInput(",");
                apply("token");

                return apply(p);
            }
        });

        final List result = list(first);
        result.addAll(rest);

        return result;
    }

    // helloWorld = "hello" "world"
    private Object helloWorld() {
        prependInput("hello");
        apply("token");

        prependInput("world");
        return apply("token");
    }
}
