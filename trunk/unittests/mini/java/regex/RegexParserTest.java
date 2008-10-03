package mini.java.regex;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.Collection;

import mini.java.lex.DummyTokenizer;
import mini.java.syntax.Helper;
import mini.java.syntax.Symbol;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class RegexParserTest {
    private String _symbols;
    private String _symbol;
    private RegexParser _parser;
    
    // Constructor
    public RegexParserTest(String symbols_, String symbol_) {
        assert(symbols_ != null);
        assert(symbol_  != null);
        
        _symbols        = symbols_;
        _symbol         = symbol_;
        _parser         = new RegexParser();
    }
    
    @Test
    public void testParse() {
        Symbol got = _parser.parse(new DummyTokenizer().tokenize(_symbols));
        Symbol expected = Helper.createSymbol(_symbol);
        
        assertNotNull(expected);
        assertNotNull(got);        
        assertEquals(expected, got);
    }
    
    @Parameters
    public static Collection<Object[]> getParameters() {
        return Arrays.asList(new Object[][] {
                {"C",           "START(E(C))"},
                {"C STAR",      "START(E(E(C),STAR))"},
                {"C BAR C",     "START(E(E(C),BAR,E(C)))"},
                {"LP C RP",     "START(E(LP,E(C),RP))"},
                {"C C",         "START(E(E(C),E(C)))"},
                {"LP C RP STAR",       "START(E(E(LP,E(C),RP),STAR))"},
        });
    }
}