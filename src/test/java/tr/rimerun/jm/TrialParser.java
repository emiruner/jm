package tr.rimerun.jm;

import java.util.List;

import static tr.rimerun.jm.BaseRules.anything;
import static tr.rimerun.jm.BaseRules.end;
import static tr.rimerun.jm.BaseRules.exactly;
import static tr.rimerun.jm.TextRules.num;
import static tr.rimerun.jm.TextRules.spaces;
import static tr.rimerun.jm.TextRules.token;
import static tr.rimerun.jm.Util.list;

// ometa SimpleExpressionParser
@SuppressWarnings({"UnusedDeclaration"})
public class TrialParser {
    // exp = exp:e spaces '+' num:n -> new Object[]{"Add", e, n}
    //     | exp:e spaces '-' num:n -> new Object[]{"Sub", e, n}
    //     | num:n                  -> n
    public static final Rule exp = new Rule() {
        public Object execute(Parser parser) {
            return parser._or(
                    new Rule() {
                        public Object execute(Parser parser) {
                            Object e = parser.apply(exp);
                            parser.apply(spaces);

                            parser.applyWithArgs(exactly, '+');

                            Object n = parser.apply(num);

                            return list("Add", e, n);
                        }
                    },

                    new Rule() {
                        public Object execute(Parser parser) {
                            Object e = parser.apply(exp);
                            parser.apply(spaces);

                            parser.applyWithArgs(exactly, '-');

                            Object n = parser.apply(num);

                            return list("Sub", e, n);
                        }
                    },

                    num
            );
        }
    };

    // notx = ~'x' :a -> a
    public static final Rule notx = new Rule() {
        public Object execute(Parser parser) {
            parser._not(new Rule() {
                public Object execute(Parser parser) {
                    return parser.applyWithArgs(exactly, 'x');
                }
            });

            return parser.apply(anything);
        }
    };

    // abAndEnd = 'a' 'b' end
    public static final Rule abAndEnd = new Rule() {
        public Object execute(Parser parser) {
            parser.applyWithArgs(exactly, 'a');
            parser.applyWithArgs(exactly, 'b');

            return parser.apply(end);
        }
    };

    // helloAnd3 = ['hello' 3]
    public static final Rule helloAnd3 = new Rule() {
        public Object execute(Parser parser) {
            parser._form(new Rule() {
                public Object execute(Parser parser) {
                    parser.applyWithArgs(exactly, "hello");
                    return parser.applyWithArgs(exactly, 3);
                }
            });

            return null;
        }
    };

    // hello3AndSomething = ['hello' 3 [:a 8]] -> a
    public static final Rule hello3AndSomething = new Rule() {
        public Object execute(Parser parser) {
            final Object[] aHolder = new Object[1];

            parser._form(new Rule() {
                public Object execute(Parser parser) {
                    parser.applyWithArgs(exactly, "hello");
                    parser.applyWithArgs(exactly, 3);

                    return parser._form(new Rule() {
                        public Object execute(Parser parser) {
                            aHolder[0] = parser.apply(anything);
                            return parser.applyWithArgs(exactly, 8);
                        }
                    });
                }
            });

            return aHolder[0];
        }
    };

    // complexList = [1 [3 5]+:a 7] -> a
    public static final Rule complexList = new Rule() {
        public Object execute(Parser parser) {
            final Object[] aHolder = new Object[1];

            parser._form(new Rule() {
                public Object execute(Parser parser) {
                    parser.applyWithArgs(exactly, 1);

                    aHolder[0] = parser._many1(new Rule() {
                        public Object execute(Parser parser) {
                            return parser._form(new Rule() {
                                public Object execute(Parser parser) {
                                    parser.applyWithArgs(exactly, 3);
                                    return parser.applyWithArgs(exactly, 5);
                                }
                            });
                        }
                    });

                    return parser.applyWithArgs(exactly, 7);
                }
            });

            return aHolder[0];
        }
    };

    // listOf :p = apply(p):first ("," apply(p))*:rest -> {List result = list(first); result.addAll(rest)); result}
    public static final Rule listOf = new Rule() {
        public Object execute(Parser parser) {
            final Rule p = (Rule) parser.apply(anything);

            final Object first = parser.apply(p);
            final List<Object> rest = parser._many(new Rule() {
                public Object execute(Parser parser) {
                    parser.applyWithArgs(token, ",");
                    return parser.apply(p);
                }
            });

            final List<Object> result = list(first);
            result.addAll(rest);

            return result;
        }
    };

    // helloWorld = "hello" "world"
    public static final Rule helloWorld = new Rule() {
        public Object execute(Parser parser) {
            parser.applyWithArgs(token, "hello");
            return parser.applyWithArgs(token, "world");
        }
    };
}
