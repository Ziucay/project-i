import compiler.Lexer;
import compiler.Token;
import org.junit.jupiter.api.Test;

import java.util.List;

public class LexerTest {

    private static Lexer lexer;

    @Test
    void checkVariableDeclarationWithoutInitialization()
    {
        String source = """
                var a : boolean
                var b : integer
                var c : real
                """;

        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.scanTokens();
        System.out.println(tokens.size());
        System.out.println(tokens);
        assert tokens.size() == 13;
    }

    @Test
    void checkVariableDeclarationWithInitialization()
    {
        String source = """
                var a : boolean is true
                var b : integer is 10
                var c : real is 1.0
                """;

        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.scanTokens();
        System.out.println(tokens.size());
        System.out.println(tokens);
        assert tokens.size() == 19;
    }

    @Test
    void checkVariableDeclarationWithInitializationOmittingType()
    {
        String source = """
                var a : is true
                var b : is 10
                var c : is 1.0
                """;

        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.scanTokens();
        System.out.println(tokens.size());
        System.out.println(tokens);
        assert tokens.size() == 16;
    }

    @Test
    void checkTypeDeclaration()
    {
        String source = """
                type kilometer is real
                type second is integer
                """;

        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.scanTokens();
        System.out.println(tokens.size());
        System.out.println(tokens);
        assert tokens.size() == 9;
    }

    @Test
    void checkVariableAssignment()
    {
        String source = """
                someVariable := 5 + 6
                """;

        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.scanTokens();
        System.out.println(tokens.size());
        System.out.println(tokens);
        assert tokens.size() == 6;
    }

    @Test
    void checkRoutineCall()
    {
        String source = """
                someFunction( 5, true, 4.3)
                """;

        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.scanTokens();
        System.out.println(tokens.size());
        System.out.println(tokens);
        assert tokens.size() == 9;
    }

    @Test
    void checkRoutineDeclaration()
    {
        String source = """
                routine someRoutine (someIntegerParameter : real, someBooleanParameter : boolean) : boolean
                is
                return 10
                end
                """;

        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.scanTokens();
        System.out.println(tokens.size());
        System.out.println(tokens);
        assert tokens.size() == 18;
    }

    @Test
    void checkExpression()
    {
        String source = """
                5 + 3 * (6 * 6) / 2
                """;

        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.scanTokens();
        System.out.println(tokens.size());
        System.out.println(tokens);
        assert tokens.size() == 12;
    }


}
