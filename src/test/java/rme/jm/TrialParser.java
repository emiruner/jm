package rme.jm;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

// ometa SimpleExpressionParser
@SuppressWarnings({"UnusedDeclaration"})
public class TrialParser {
    // exp = exp:e spaces '+' num:n -> new Object[]{"Add", e, n}
    //     | exp:e spaces '-' num:n -> new Object[]{"Sub", e, n}
    //     | num:n                  -> n
    public static final Rule exp = new Rule() {
        public Object execute(Parser parser) {
            return parser._or(
                    parser1 -> {
                        Object e = parser1.apply(exp);
                        parser1.apply(TextRules.spaces);

                        parser1.applyWithArgs(BaseRules.exactly, '+');

                        Object n = parser1.apply(TextRules.num);

                        return asList("Add", e, n);
                    },

                    parser2 -> {
                        Object e = parser2.apply(exp);
                        parser2.apply(TextRules.spaces);

                        parser2.applyWithArgs(BaseRules.exactly, '-');

                        Object n = parser2.apply(TextRules.num);

                        return asList("Sub", e, n);
                    },

                    TextRules.num
            );
        }
    };

    // notx = ~'x' :a -> a
    public static final Rule notx = parser -> {
        parser._not(parser1 -> parser1.applyWithArgs(BaseRules.exactly, 'x'));

        return parser.apply(BaseRules.anything);
    };

    // abAndEnd = 'a' 'b' end
    public static final Rule abAndEnd = parser -> {
        parser.applyWithArgs(BaseRules.exactly, 'a');
        parser.applyWithArgs(BaseRules.exactly, 'b');

        return parser.apply(BaseRules.end);
    };

    // helloAnd3 = ['hello' 3]
    public static final Rule helloAnd3 = parser -> {
        parser._form(parser1 -> {
            parser1.applyWithArgs(BaseRules.exactly, "hello");
            return parser1.applyWithArgs(BaseRules.exactly, 3);
        });

        return null;
    };

    // hello3AndSomething = ['hello' 3 [:a 8]] -> a
    public static final Rule hello3AndSomething = parser -> {
        final Object[] aHolder = new Object[1];

        parser._form(parser1 -> {
            parser1.applyWithArgs(BaseRules.exactly, "hello");
            parser1.applyWithArgs(BaseRules.exactly, 3);

            return parser1._form(parser11 -> {
                aHolder[0] = parser11.apply(BaseRules.anything);
                return parser11.applyWithArgs(BaseRules.exactly, 8);
            });
        });

        return aHolder[0];
    };

    // complexList = [1 [3 5]+:a 7] -> a
    public static final Rule complexList = parser -> {
        final Object[] aHolder = new Object[1];

        parser._form(parser12 -> {
            parser12.applyWithArgs(BaseRules.exactly, 1);

            aHolder[0] = parser12._many1(parser1 -> parser1._form(parser11 -> {
                parser11.applyWithArgs(BaseRules.exactly, 3);
                return parser11.applyWithArgs(BaseRules.exactly, 5);
            }));

            return parser12.applyWithArgs(BaseRules.exactly, 7);
        });

        return aHolder[0];
    };

    // listOf :p = apply(p):first ("," apply(p))*:rest -> {List result = list(first); result.addAll(rest)); result}
    public static final Rule listOf = parser -> {
        final Rule p = (Rule) parser.apply(BaseRules.anything);

        final Object first = parser.apply(p);
        final List<Object> rest = parser._many(parser1 -> {
            parser1.applyWithArgs(TextRules.token, ",");
            return parser1.apply(p);
        });

        final ArrayList<Object> result = new ArrayList<>();

        result.add(first);
        result.addAll(rest);

        return result;
    };

    // helloWorld = "hello" "world"
    public static final Rule helloWorld = parser -> {
        parser.applyWithArgs(TextRules.token, "hello");
        return parser.applyWithArgs(TextRules.token, "world");
    };
}
