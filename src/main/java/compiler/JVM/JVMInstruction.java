package compiler.JVM;

public class JVMInstruction {
    public static int instructionOffset(String instruction) {
        return switch (instruction) {
            case "iconst_", "istore_", "iload_" -> 1;
            case "bipush" -> 2;
            case "goto", "if_icmplt" -> 3;
            default -> 0;
        };
    }
}
