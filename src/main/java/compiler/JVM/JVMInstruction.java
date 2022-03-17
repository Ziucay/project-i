package compiler.JVM;

public class JVMInstruction {
    public static int instructionOffset(String instruction) {
        return switch (instruction) {
            case "iconst_", "istore_", "iload_", "iadd", "istore", "iload", "imul", "iduv", "isub", "iconst" -> 1;
            case "bipush" -> 2;
            case "goto", "if_icmp", "if", "fcmp", "iinc" -> 3;
            default -> 0;
        };
    }
}
