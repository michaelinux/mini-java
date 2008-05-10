import lex.LexConfig;
import lex.TextLexConfig;
import lex.TokenRevamped;
import lex.TokenType;
import lex.TokenizerIF;
import lex.TokenizerAdapter;
import lex.legacy.Tokenizer;
import lex.legacy.RegexCompiler;
import fa.legacy.DFA;
import fa.legacy.SimpleFA;
import java.util.List;

public class Test02 {
    public static void main(String[] args) throws Exception {
        Tokenizer tokenizer = new Tokenizer();
        LexConfig lexConfig = TextLexConfig.getInstance();
        for (TokenType type : lexConfig.getTokenTypes()) {
            String tokenSpec = lexConfig.getTokenSpec(type);
            //try {
                //DFA dfa = RegexCompiler.compile(tokenSpec).toDFA().toDFA0();
                SimpleFA fa = RegexCompiler.compile(tokenSpec).toDFA();
                //System.out.println("********");
                //fa.dump();
                //System.out.println("********");
                DFA dfa = fa.toDFA0();
                
                tokenizer.addDFA(dfa, type.toString());
            //} catch (Exception e) {
            //    throw new RuntimeException(e);
            //}
        }
        TokenizerIF newTokenizer = new TokenizerAdapter(tokenizer);
        List<TokenRevamped> tokens = newTokenizer.tokenize("class Main { public static void main(String[] args) { }} ");
        for (TokenRevamped token : tokens) {
            System.out.println(token);
        }
    }
}
