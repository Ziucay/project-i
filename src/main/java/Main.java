import Lexer

public class Main {
    public static void main(String[] args) {
        byte[] bytes = Files.readAllBytes(Paths.get("test.txt"));
        String source = new String(bytes, Charset.defaultCharset()));
        Lexer lexer = new Lexer(source);
    }
}