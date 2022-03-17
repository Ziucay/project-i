package compiler;

import compiler.JVM.JVMCompiler;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.Charset;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        String filename = args[0];
        byte[] bytes = Files.readAllBytes(Paths.get(filename + ".txt"));
        String source = new String(bytes, Charset.defaultCharset());
        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.scanTokens();
        System.out.println(tokens);
        Parser parser = new Parser(false);
        parser.setTokens(tokens);
        parser.run();
        JVMCompiler compiler = new JVMCompiler(parser.floatValues, filename);
        if (parser.root.descendants.size() != 0) {
            compiler.traverseTree(parser.root);
            System.out.println();
            FileWriter writer = new FileWriter(filename + ".class");
            for (String line :
                    compiler.code) {
                writer.write(line + "\n");
            }
            writer.flush();
        }
    }
}

