package tr.rimerun.jm;

import tr.rimerun.jm.rule.Anything;
import tr.rimerun.jm.rule.End;
import tr.rimerun.jm.rule.Exactly;
import tr.rimerun.jm.rule.text.Num;
import tr.rimerun.jm.rule.text.Spaces;
import tr.rimerun.jm.rule.text.Token;

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
                            parser.apply(Spaces.Instance);

                            parser.applyWithArgs(Exactly.Instance, '+');

                            Object n = parser.apply(Num.Instance);

                            return list("Add", e, n);
                        }
                    },

                    new Rule() {
                        public Object execute(Parser parser) {
                            Object e = parser.apply(exp);
                            parser.apply(Spaces.Instance);

                            parser.applyWithArgs(Exactly.Instance, '-');

                            Object n = parser.apply(Num.Instance);

                            return list("Sub", e, n);
                        }
                    },

                    Num.Instance
            );
        }
    };

    // notx = ~'x' :a -> a
    public static final Rule notx = new Rule() {
        public Object execute(Parser parser) {
            parser._not(new Rule() {
                public Object execute(Parser parser) {
                    return parser.applyWithArgs(Exactly.Instance, 'x');
                }
            });

            return parser.apply(Anything.Instance);
        }
    };

    // abAndEnd = 'a' 'b' end
    public static final Rule abAndEnd = new Rule() {
        public Object execute(Parser parser) {
            parser.applyWithArgs(Exactly.Instance, 'a');
            parser.applyWithArgs(Exactly.Instance, 'b');

            return parser.apply(End.Instance);
        }
    };

    // helloAnd3 = ['hello' 3]
    public static final Rule helloAnd3 = new Rule() {
        public Object execute(Parser parser) {
            parser._form(new Rule() {
                public Object execute(Parser parser) {
                    parser.applyWithArgs(Exactly.Instance, "hello");
                    return parser.applyWithArgs(Exactly.Instance, 3);
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
                    parser.applyWithArgs(Exactly.Instance, "hello");
                    parser.applyWithArgs(Exactly.Instance, 3);

                    return parser._form(new Rule() {
                        public Object execute(Parser parser) {
                            aHolder[0] = parser.apply(Anything.Instance);
                            return parser.applyWithArgs(Exactly.Instance, 8);
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
                    parser.applyWithArgs(Exactly.Instance, 1);

                    aHolder[0] = parser._many1(new Rule() {
                        public Object execute(Parser parser) {
                            return parser._form(new Rule() {
                                public Object execute(Parser parser) {
                                    parser.applyWithArgs(Exactly.Instance, 3);
                                    return parser.applyWithArgs(Exactly.Instance, 5);
                                }
                            });
                        }
                    });

                    return parser.applyWithArgs(Exactly.Instance, 7);
                }
            });

            return aHolder[0];
        }
    };

    // TODO: How to do this?
    // listOf :p = apply(p):first ("," apply(p))*:rest -> {List result = list(first); result.addAll(rest)); result}
//    public static final Rule listOf = new Rule() {
//        public Object execute(Parser parser) {
//            final String p = (String) parser.apply(Anything.Instance);
//
//            final Object first = parser.apply(p);
//            final List<Object> rest = parser._many(new Rule() {
//                public Object execute(Parser parser) {
//                    parser.applyWithArgs(Token.Instance, ",");
//                    return parser.apply(p);
//                }
//            });
//
//            final List<Object> result = list(first);
//            result.addAll(rest);
//
//            return result;
//        }
//    };

    // helloWorld = "hello" "world"
    public static final Rule helloWorld = new Rule() {
        public Object execute(Parser parser) {
            parser.applyWithArgs(Token.Instance, "hello");
            return parser.applyWithArgs(Token.Instance, "world");
        }
    };
}
