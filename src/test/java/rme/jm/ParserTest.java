package rme.jm;

import org.junit.Test;

import java.io.StringReader;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static rme.jm.TextRules.num;
import static rme.jm.TrialParser.listOf;

public class ParserTest {
    @Test
    public void leftRecursion() {
        final Parser parser = new Parser(streamFromString("1-2-3"));
        assertEquals(asList("Expr", asList("Expr", 1, '-', 2), '-', 3), parser.apply(LeftRecursionTestParser.expr));
    }

    @Test
    public void basicParse() {
        //[Add, [Add, [Sub, -123, -45], 76], 1]
        final Parser parser = new Parser(streamFromString("    -123  -  -45   +76+1 "));
        assertEquals(asList("Add", asList("Add", asList("Sub", -123, -45), 76), 1), parser.apply(TrialParser.exp));
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
        final Parser parser = new Parser(SimpleLinkedInputStream.singleElementList(asList("hello", 3)));
        assertNull(parser.apply(TrialParser.helloAnd3));
    }

    @Test
    public void listMatch() {
        final Parser parser = new Parser(SimpleLinkedInputStream.singleElementList(asList("hello", 3, asList(7, 8))));
        assertEquals(7, parser.apply(TrialParser.hello3AndSomething));
    }

    @Test(expected = ParseFailure.class)
    public void listNotMatch() {
        final Parser parser = new Parser(SimpleLinkedInputStream.singleElementList(asList("hello", 3, asList(7, 1))));
        assertEquals(7, parser.apply(TrialParser.hello3AndSomething));
    }

    @Test
    public void complexList() {
        final Parser parser = new Parser(SimpleLinkedInputStream.singleElementList(asList(1, asList(3, 5), asList(3, 5), 7)));
        assertEquals(asList(asList(3, 5), asList(3, 5)), parser.apply(TrialParser.complexList));
    }

    @Test
    public void higherOrder() {
        final Parser parser = new Parser(streamFromString("45, 787, 997"));
        assertEquals(asList(45, 787, 997), parser.applyWithArgs(listOf, num));
    }

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
