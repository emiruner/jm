package tr.rimerun.jm;

import org.junit.Test;

import java.io.StringReader;

import static org.junit.Assert.assertEquals;
import static tr.rimerun.jm.Util.list;

public class ParserTest {
    @Test
    public void leftRecursion() {
        final Parser parser = new Parser(streamFromString("1-2-3"));
        assertEquals(list("Expr", list("Expr", 1, '-', 2), '-', 3), parser.apply(LeftRecursionTestParser.expr));
    }

    @Test
    public void basicParse() {
        //[Add, [Add, [Sub, -123, -45], 76], 1]
        final Parser parser = new Parser(streamFromString("    -123  -  -45   +76+1 "));
        assertEquals(list("Add", list("Add", list("Sub", -123, -45), 76), 1), parser.apply(TrialParser.exp));
    }

    @Test
    public void parameterPassingEtc() {
        final Parser parser = new Parser(streamFromString("11"));
        assertEquals(39916800, parser.apply(FactorialCalculatingParser.start));
    }

    @Test
    public void not() {
        final Parser parser = new Parser(streamFromString("y"));
        assertEquals('y', parser.apply(TrialParser.notx));
    }

    @Test(expected = ParseFailure.class)
    public void notFail() {
        final Parser parser = new Parser(streamFromString("x"));
        assertEquals('y', parser.apply(TrialParser.notx));
    }

    @Test
    public void endMatch() {
        final Parser parser = new Parser(streamFromString("ab"));
        parser.apply(TrialParser.abAndEnd);
    }

    @Test(expected = ParseFailure.class)
    public void endFail() {
        final Parser parser = new Parser(streamFromString("abc"));
        parser.apply(TrialParser.abAndEnd);
    }

    @Test
    public void simpleListMatch() {
        final Parser parser = new Parser(SimpleLinkedInputStream.singleElementList(list("hello", 3)));
        assertEquals(null, parser.apply(TrialParser.helloAnd3));
    }

    @Test
    public void listMatch() {
        final Parser parser = new Parser(SimpleLinkedInputStream.singleElementList(list("hello", 3, list(7, 8))));
        assertEquals(7, parser.apply(TrialParser.hello3AndSomething));
    }

    @Test(expected = ParseFailure.class)
    public void listNotMatch() {
        final Parser parser = new Parser(SimpleLinkedInputStream.singleElementList(list("hello", 3, list(7, 1))));
        assertEquals(7, parser.apply(TrialParser.hello3AndSomething));
    }

    @Test
    public void complexList() {
        final Parser parser = new Parser(SimpleLinkedInputStream.singleElementList(list(1, list(3, 5), list(3, 5), 7)));
        assertEquals(list(list(3, 5), list(3, 5)), parser.apply(TrialParser.complexList));
    }

    // TODO: How to do this?
//    @Test
//    public void higherOrder() {
//        final TrialParser parser = new TrialParser(streamFromString("45, 787, 997"));
//        assertEquals(list(45, 787, 997), parser.applyWithArgs("listOf", "num"));
//    }

    @Test
    public void tokenRuleMatch() {
        assertEquals("world", new Parser(streamFromString("hello world")).apply(TrialParser.helloWorld));
        assertEquals("world", new Parser(streamFromString("hello            world")).apply(TrialParser.helloWorld));
        assertEquals("world", new Parser(streamFromString("          hello            world")).apply(TrialParser.helloWorld));
        assertEquals("world", new Parser(streamFromString("hello            world          ")).apply(TrialParser.helloWorld));
        assertEquals("world", new Parser(streamFromString("     hello            world          ")).apply(TrialParser.helloWorld));
    }

    @Test(expected = ParseFailure.class)
    public void tokenRuleNotMatch() {
        new Parser(streamFromString("helloworld")).apply(TrialParser.helloWorld);
    }
    
    private static LinkedInputStream streamFromString(String str) {
        return new ReaderBackedLinkedInputStream(new StringReader(str));
    }
}
