package tr.rimerun.jm;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class SimpleParser extends Parser {
    private HashMap<String, Rule> rules;

    public SimpleParser(LinkedInputStream input) {
        super(input);

        this.rules = new HashMap<String, Rule>();

        addRule("anything", new Rule() {
            public Object execute() {
                return _anything();
            }
        });

        addRule("exactly", new Rule() {
            public Object execute() {
                final Object o = apply("anything");
                final Object p = apply("anything");

                ensure(o.equals(p));
                return p;
            }
        });

        addRule("seq", new Rule() {
            public Object execute() {
                final Collection items = (Collection) apply("anything");

                for (Object item : items) {
                    prependInput(item);
                    apply("exactly");
                }

                return items;
            }
        });

        addRule("end", new Rule() {
            public Object execute() {
                return _not(new Rule() {
                    public Object execute() {
                        return apply("anything");
                    }
                });
            }
        });
    }

    @Override
    protected Object eval(String ruleName) {
        if (!rules.containsKey(ruleName)) {
            throw new RuntimeException("unknown rule: " + ruleName);
        }

        return rules.get(ruleName).execute();
    }

    protected void addRule(String name, Rule rule) {
        rules.put(name, rule);
    }

    protected List<Object> _many(String ruleName) {
        return _many(rules.get(ruleName));
    }

    protected List<Object> _many1(String ruleName) {
        return _many1(rules.get(ruleName));
    }

    protected Object _or(Object... rulesOrRuleNames) {
        Rule[] rules = new Rule[rulesOrRuleNames.length];

        for (int i = 0; i < rulesOrRuleNames.length; ++i) {
            if (rulesOrRuleNames[i] instanceof Rule) {
                rules[i] = (Rule) rulesOrRuleNames[i];
            } else if (rulesOrRuleNames[i] instanceof String) {
                rules[i] = this.rules.get((String) rulesOrRuleNames[i]);
            } else {
                throw new RuntimeException("must be either a rule or a rule name");
            }
        }

        return _or(rules);
    }
}
