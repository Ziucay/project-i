package compiler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.Charset;

public class Main {
    public static void main(String[] args) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get("test.txt"));
        String source = new String(bytes, Charset.defaultCharset());
        Lexer lexer = new Lexer(source);
        lexer.scanTokens();
        System.out.println(lexer.toString());
    }
}