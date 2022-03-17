package compiler;

import compiler.JVM.JVMCompiler;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.Charset;
import java.util.ArrayList;
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
        JVMCompiler compiler = new JVMCompiler(new ArrayList<>(), "program");
        ASTNode root = ASTNode.exampleTwo(new ASTNode("root"));
        compiler.traverseTree(root);
        FileWriter writer = new FileWriter(filename + ".class");
        for (String line:
                compiler.code) {
            writer.write(line + "\n");
        }
        writer.flush();
    }
}

