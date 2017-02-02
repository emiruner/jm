package tr.rimerun.jm.rule.text;

import tr.rimerun.jm.Parser;
import tr.rimerun.jm.Rule;
import tr.rimerun.jm.rule.Anything;
import tr.rimerun.jm.rule.End;
import tr.rimerun.jm.rule.Seq;

import java.util.ArrayList;

// token :t = spaces seq(t) (space+ | end) -> t
public class Token implements Rule {
    public static final Token Instance = new Token();

    private Token() {
    }

    public Object execute(Parser parser) {
        final String value = (String) parser.apply(Anything.Instance);

        parser.apply(Spaces.Instance);

        final ArrayList<Character> chars = new ArrayList<Character>(value.length());

        for (char ch : value.toCharArray()) {
            chars.add(ch);
        }

        parser.applyWithArgs(Seq.Instance, chars);

        parser._or(new Rule() {
                       public Object execute(Parser parser) {
                           return parser._many1(Space.Instance);
                       }
                   }, End.Instance
        );

        return value;
    }
}
