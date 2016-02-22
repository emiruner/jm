package tr.rimerun.jm;

import java.util.List;

import static tr.rimerun.jm.Util.list;

// ometa SimpleExpressionParser <: BaseTestParser
@SuppressWarnings({"UnusedDeclaration"})
public class TrialParser extends BaseTestParser {
    public TrialParser(LinkedInputStream input) {
        super(input);

        // exp = exp:e spaces '+' num:n -> new Object[]{"Add", e, n}
        //     | exp:e spaces '-' num:n -> new Object[]{"Sub", e, n}
        //     | num:n                  -> n
        addRule("exp", new Rule() {
            public Object execute() {
                return _or(
                        new Rule() {
                            public Object execute() {
                                Object e = apply("exp");
                                apply("spaces");

                                applyWithArgs("exactly", '+');

                                Object n = apply("num");

                                return list("Add", e, n);
                            }
                        },

                        new Rule() {
                            public Object execute() {
                                Object e = apply("exp");
                                apply("spaces");

                                applyWithArgs("exactly", '-');

                                Object n = apply("num");

                                return list("Sub", e, n);
                            }
                        },

                        "num"
                );
            }
        });

        // notx = ~'x' :a -> a
        addRule("notx", new Rule() {
            public Object execute() {
                _not(new Rule() {
                    public Object execute() {
                        return applyWithArgs("exactly", 'x');
                    }
                });

                return apply("anything");
            }
        });

        // abAndEnd = 'a' 'b' end
        addRule("abAndEnd", new Rule() {
            public Object execute() {
                applyWithArgs("exactly", 'a');
                applyWithArgs("exactly", 'b');

                return apply("end");
            }
        });

        // helloAnd3 = ['hello' 3]
        addRule("helloAnd3", new Rule() {
            public Object execute() {
                _form(new Rule() {
                    public Object execute() {
                        applyWithArgs("exactly", "hello");
                        return applyWithArgs("exactly", 3);
                    }
                });

                return null;
            }
        });

        // hello3AndSomething = ['hello' 3 [:a 8]] -> a
        addRule("hello3AndSomething", new Rule() {
            public Object execute() {
                final Object[] aHolder = new Object[1];

                _form(new Rule() {
                    public Object execute() {
                        applyWithArgs("exactly", "hello");
                        applyWithArgs("exactly", 3);

                        return _form(new Rule() {
                            public Object execute() {
                                aHolder[0] = apply("anything");
                                return applyWithArgs("exactly", 8);
                            }
                        });
                    }
                });

                return aHolder[0];
            }
        });

        // complexList = [1 [3 5]+:a 7] -> a
        addRule("complexList", new Rule() {
            public Object execute() {
                final Object[] aHolder = new Object[1];

                _form(new Rule() {
                    public Object execute() {
                        applyWithArgs("exactly", 1);

                        aHolder[0] = _many1(new Rule() {
                            public Object execute() {
                                return _form(new Rule() {
                                    public Object execute() {
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
        });

        // listOf :p = apply(p):first ("," apply(p))*:rest -> {List result = list(first); result.addAll(rest)); result}
        addRule("listOf", new Rule() {
            public Object execute() {
                final String p = (String) apply("anything");

                final Object first = apply(p);
                final List<Object> rest = _many(new Rule() {
                    public Object execute() {
                        applyWithArgs("token", ",");
                        return apply(p);
                    }
                });

                final List<Object> result = list(first);
                result.addAll(rest);

                return result;
            }
        });

        // helloWorld = "hello" "world"
        addRule("helloWorld", new Rule() {
            public Object execute() {
                applyWithArgs("token", "hello");
                return applyWithArgs("token", "world");
            }
        });
    }
}
