package tr.rimerun.jm;

import java.util.ArrayList;

@SuppressWarnings({"UnusedDeclaration"})
public class CharacterOrientedBaseParser extends SimpleParser {
    public CharacterOrientedBaseParser(LinkedInputStream input) {
        super(input);

        // token :t = spaces seq(t) (space+ | end) -> t
        addRule("token", new Rule() {
            public Object execute() {
                final String value = (String) apply("anything");

                apply("spaces");

                final ArrayList<Character> chars = new ArrayList<Character>(value.length());

                for (char ch : value.toCharArray()) {
                    chars.add(ch);
                }

                applyWithArgs("seq", chars);

                _or(new Rule() {
                        public Object execute() {
                            return _many1("space");
                        }
                    }, "end"
                );

                return value;
            }
        });

        // chr = :o ?(o instanceof Character) -> o
        addRule("chr", new Rule() {
            public Object execute() {
                Object o = apply("anything");
                ensure(o instanceof Character);
                return o;
            }
        });

        // space = chr:ch ?(Character.isWhitespace(ch)) -> ch
        addRule("space", new Rule() {
            public Object execute() {
                Character chr = (Character) apply("chr");
                ensure(Character.isWhitespace(chr));
                return chr;
            }
        });

        // spaces = space*
        addRule("spaces", new Rule() {
            public Object execute() {
                return _many("space");
            }
        });

        // digit = chr:d ?(Character.isDigit(d)) -> d
        addRule("digit", new Rule() {
            public Object execute() {
                Character chr = (Character) apply("chr");
                ensure(Character.isDigit(chr));
                return chr;
            }
        });
    }
}
