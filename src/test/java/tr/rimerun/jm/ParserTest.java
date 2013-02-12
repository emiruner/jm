package tr.rimerun.jm;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static tr.rimerun.jm.Util.list;

public class ParserTest {
    @Test
    public void leftRecursion() {
        final LeftRecursionTestParser parser = new LeftRecursionTestParser(BasicLinkedInputStream.fromString("1-2-3"));
        assertEquals(list("Expr", list("Expr", 1, '-', 2), '-', 3), parser.apply("expr"));
    }

    @Test
    public void basicParse() {
        //[Add, [Add, [Sub, -123, -45], 76], 1]
        final TrialParser parser = new TrialParser(BasicLinkedInputStream.fromString("    -123  -  -45   +76+1 "));
        assertEquals(list("Add", list("Add", list("Sub", -123, -45), 76), 1), parser.apply("exp"));
    }

    @Test
    public void parameterPassingEtc() {
        final FactorialCalculatingParser parser = new FactorialCalculatingParser(BasicLinkedInputStream.fromString("11"));
        assertEquals(39916800, parser.apply("start"));
    }

    @Test
    public void not() {
        final TrialParser parser = new TrialParser(BasicLinkedInputStream.fromString("y"));
        assertEquals('y', parser.apply("notx"));
    }

    @Test
    public void endMatch() {
        final TrialParser parser = new TrialParser(BasicLinkedInputStream.fromString("ab"));
        parser.apply("abAndEnd");
    }

    @Test(expected = ParseFailure.class)
    public void endFail() {
        final TrialParser parser = new TrialParser(BasicLinkedInputStream.fromString("abc"));
        parser.apply("abAndEnd");
    }

    @Test
    public void listMatch() {
        final TrialParser parser = new TrialParser(BasicLinkedInputStream.fromList(list(list("hello", 3, list(7, 8)))));
        assertEquals(7, parser.apply("hello3AndSomething"));
    }

    @Test(expected = ParseFailure.class)
    public void listNotMatch() {
        final TrialParser parser = new TrialParser(BasicLinkedInputStream.fromList(list(list("hello", 3, list(7, 1)))));
        assertEquals(7, parser.apply("hello3AndSomething"));
    }

    @Test
    public void complexList() {
        final TrialParser parser = new TrialParser(BasicLinkedInputStream.fromList(list(list(1, list(3, 5), list(3, 5), 7))));
        assertEquals(list(list(3, 5), list(3, 5)), parser.apply("complexList"));
    }

    @Test
    public void higherOrder() {
        final TrialParser parser = new TrialParser(BasicLinkedInputStream.fromString("45, 787, 997"));
        assertEquals(list(45, 787, 997), parser.applyWithArgs("listOf", "num"));
    }

    @Test
    public void tokenRuleMatch() {
        assertEquals("world", new TrialParser(BasicLinkedInputStream.fromString("hello world")).apply("helloWorld"));
        assertEquals("world", new TrialParser(BasicLinkedInputStream.fromString("hello            world")).apply("helloWorld"));
        assertEquals("world", new TrialParser(BasicLinkedInputStream.fromString("          hello            world")).apply("helloWorld"));
        assertEquals("world", new TrialParser(BasicLinkedInputStream.fromString("hello            world          ")).apply("helloWorld"));
        assertEquals("world", new TrialParser(BasicLinkedInputStream.fromString("     hello            world          ")).apply("helloWorld"));
    }

    @Test(expected = ParseFailure.class)
    public void tokenRuleNotMatch() {
        new TrialParser(BasicLinkedInputStream.fromString("helloworld")).apply("helloWorld");
    }
}
