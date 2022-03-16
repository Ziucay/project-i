package compiler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.Charset;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get("test.txt"));
        String source = new String(bytes, Charset.defaultCharset());
        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.scanTokens();
        System.out.println(tokens);
        Parser parser = new Parser(false);
        parser.setTokens(tokens);
        parser.run();
    }
}