package compiler.JVM;

import compiler.ASTNode;
import compiler.Node;
import compiler.SymbolTable;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class JVMFrame {
    int offset = 0;
    HashMap<Float, Integer> constantPool;
    int varId = 1;
    char[] tabs;
    HashMap<String, Integer> varIds;
    public SymbolTable table;
    String parameters;
    String parameterString;
    String returnType;
    String identifier;

    public JVMFrame(HashMap<Float, Integer> constantPool, List<Node> parameters, String identifier) {
        this.varIds = new HashMap<>();
        this.constantPool = constantPool;
        this.varId += this.constantPool.size();
        this.table = new SymbolTable();
        this.identifier = identifier;
        this.parameters = "";
        this.parameterString = "";

        if (parameters != null) {
            for (Node parameter :
                    parameters) {
                this.parameterString = this.parameters + parameter.descendants.get(0).identifier + ", ";
                this.parameters += parameter.descendants.get(0).identifier.substring(0, 1).toUpperCase(Locale.ROOT);
                this.varIds.put(parameter.descendants.get(1).identifier, this.varId);
                this.varId++;
            }
        }

        this.returnType = "void";
    }

    public JVMFrame(HashMap<Float, Integer> constantPool, List<Node> parameters, String returnType, String identifier) {
        this.varIds = new HashMap<>();
        this.constantPool = constantPool;
        this.varId += this.constantPool.size();
        this.table = new SymbolTable();
        this.identifier = identifier;
        this.parameters = "";
        this.parameterString = "";

        if (parameters != null) {
            for (Node parameter :
                    parameters) {
                this.parameterString = this.parameters + parameter.descendants.get(0).identifier + ", ";
                this.parameters += parameter.descendants.get(0).identifier.substring(0, 1).toUpperCase(Locale.ROOT);
                this.varIds.put(parameter.descendants.get(1).identifier, this.varId);
                this.varId++;
            }
        }

        this.parameterString = this.parameterString.substring(0, this.parameterString.length() - 3);
        this.returnType = returnType;
    }

    public ArrayList<String> startFrame() {
        ArrayList<String> result = new ArrayList<>();

        String declare = " public " + this.returnType + " " + this.identifier + "(" + this.parameters + ");";
        result.add(declare);
        result.add("\tdescriptor: (" + this.parameters + ")" + this.returnType.substring(0, 1).toUpperCase(Locale.ROOT));
        result.add("\tflags: ACC_PUBLIC");
        result.add("\tCode:");
        return result;
    }

    public ArrayList<String> endFrame() {
        ArrayList<String> result = new ArrayList<>();

        result.add("");
        return result;
    }

    void variableDeclaration(String identifier) {
        this.varIds.put(identifier, 0);
    }

    ArrayList<String> incrementPositive(String identifier) {
        int id = this.varIds.get(identifier);
        ArrayList<String> result = new ArrayList<>();

        result.add("\t\t" + offset + ": iinc " + id + " " + 1);
        this.offset += JVMInstruction.instructionOffset("iinc");
        return result;
    }

    ArrayList<String> incrementNegative(String identifier) {
        int id = this.varIds.get(identifier);
        ArrayList<String> result = new ArrayList<>();

        result.add("\t\t" + offset + ": iinc " + id + " " + -1);
        this.offset += JVMInstruction.instructionOffset("iinc");
        return result;
    }
;
    ArrayList<String> assignIntValue(int value, String identifier) {
        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": bipush " + value);
        this.offset += JVMInstruction.instructionOffset("bipush");
        if (this.varIds.containsKey(identifier)) {
            if (this.varIds.get(identifier) == 0) {
                this.varIds.put(identifier, this.varId);
                this.varId++;
            }
            result.add("\t\t" + offset + ": istore_" + this.varIds.get(identifier));
            this.offset += JVMInstruction.instructionOffset("istore");
        } else {
            this.varIds.put(identifier, this.varId);
            this.varId++;
            result.add("\t\t" + offset + ": istore");
            this.offset += JVMInstruction.instructionOffset("istore");
        }
        return result;
    }

    ArrayList<String> assignIntId(String identifier1, String identifier2) {
        int id = (int) this.varIds.get(identifier2);

        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": iload_" + id);
        this.offset += JVMInstruction.instructionOffset("iload");
        if (this.varIds.containsKey(identifier1)) {
            if (this.varIds.get(identifier1) == 0) {
                this.varIds.put(identifier1, this.varId);
                this.varId++;
            }
            result.add("\t\t" + offset + ": istore_" + this.varIds.get(identifier1));
            this.offset += JVMInstruction.instructionOffset("istore");
        } else {
            this.varIds.put(identifier1, this.varId);
            this.varId++;
            result.add("\t\t" + offset + ": istore");
            this.offset += JVMInstruction.instructionOffset("istore");
        }
        return result;
    }

    ArrayList<String> assignIntStack(String identifier) {
        ArrayList<String> result = new ArrayList<>();
        if (this.varIds.containsKey(identifier)) {
            if (this.varIds.get(identifier) == 0) {
                this.varIds.put(identifier, this.varId);
                this.varId++;
            }
            result.add("\t\t" + offset + ": istore_" + this.varIds.get(identifier));
            this.offset += JVMInstruction.instructionOffset("istore");
        } else {
            this.varIds.put(identifier, this.varId);
            this.varId++;
            result.add("\t\t" + offset + ": istore");
            this.offset += JVMInstruction.instructionOffset("istore");
        }
        return result;
    }

    ArrayList<String> intBinaryOp(String identifier1, String identifier2) {
        int id1 = (int) this.varIds.get(identifier1);
        int id2 = (int) this.varIds.get(identifier2);

        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": iload_" + id1);
        this.offset += JVMInstruction.instructionOffset("iload");
        result.add("\t\t" + offset + ": iload_" + id2);
        this.offset += JVMInstruction.instructionOffset("iload");
        return result;
    }

    ArrayList<String> negateIntStack () {
        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": ineg");
        this.offset += JVMInstruction.instructionOffset("ineg");
        return result;
    }

    ArrayList<String> negateIntId (String identifier) {
        int id = (int) this.varIds.get(identifier);
        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": iload_" + id);
        this.offset += JVMInstruction.instructionOffset("iload");
        result.add("\t\t" + offset + ": ineg");
        this.offset += JVMInstruction.instructionOffset("ineg");
        return result;
    }

    ArrayList<String> addIntId(String identifier1, String identifier2)  {
        ArrayList<String> result = intBinaryOp(identifier1, identifier2);
        result.add("\t\t" + offset + ": iadd");
        this.offset += JVMInstruction.instructionOffset("iadd");
        return result;
    }

    ArrayList<String> addIntStackId(boolean stack, String identifier) {
        int id = (int) this.varIds.get(identifier);

        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": iload_" + id);
        this.offset += JVMInstruction.instructionOffset("iload");
        if (stack) {
            result.add("\t\t" + offset + ": swap");
            this.offset += JVMInstruction.instructionOffset("swap");
        }
        result.add("\t\t" + offset + ": iadd");
        this.offset += JVMInstruction.instructionOffset("iadd");
        return result;
    }

    ArrayList<String> addIntStack() {
        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": iadd");
        this.offset += JVMInstruction.instructionOffset("iadd");
        return result;
    }

    ArrayList<String> addIntStackValue(boolean stack, int value) {
        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": bipush " + value);
        this.offset += JVMInstruction.instructionOffset("bipush");
        if (stack) {
            result.add("\t\t" + offset + ": swap");
            this.offset += JVMInstruction.instructionOffset("swap");
        }
        result.add("\t\t" + offset + ": iadd");
        this.offset += JVMInstruction.instructionOffset("iadd");
        return result;
    }

    ArrayList<String> addIntValue(int value1, int value2) {
        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": bipush " + value1);
        this.offset += JVMInstruction.instructionOffset("bipush");
        result.add("\t\t" + offset + ": bipush " + value2);
        this.offset += JVMInstruction.instructionOffset("bipush");
        result.add("\t\t" + offset + ": iadd");
        this.offset += JVMInstruction.instructionOffset("iadd");
        return result;
    }

    ArrayList<String> addIntValueId(int value, String identifier) {
        int id = (int) this.varIds.get(identifier);

        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": bipush " + value);
        this.offset += JVMInstruction.instructionOffset("bipush");
        result.add("\t\t" + offset + ": iload_" + id);
        this.offset += JVMInstruction.instructionOffset("iload");
        result.add("\t\t" + offset + ": iadd");
        this.offset += JVMInstruction.instructionOffset("iadd");
        return result;
    }

    ArrayList<String> addIntIdValue(String identifier, int value) {
        int id = this.varIds.get(identifier);

        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": iload_" + id);
        this.offset += JVMInstruction.instructionOffset("iload");
        result.add("\t\t" + offset + ": bipush " + value);
        this.offset += JVMInstruction.instructionOffset("bipush");
        result.add("\t\t" + offset + ": iadd");
        this.offset += JVMInstruction.instructionOffset("iadd");
        return result;
    }

    ArrayList<String> subtractIntId(String identifier1, String identifier2) {
        ArrayList<String> result = intBinaryOp(identifier1, identifier2);
        result.add("\t\t" + offset + ": isub");
        this.offset += JVMInstruction.instructionOffset("isub");
        return result;
    }

    ArrayList<String> subtractIntStackId(boolean stack, String identifier) {
        int id = (int) this.varIds.get(identifier);

        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": iload_" + id);
        this.offset += JVMInstruction.instructionOffset("iload");
        if (stack) {
            result.add("\t\t" + offset + ": swap");
            this.offset += JVMInstruction.instructionOffset("swap");
        }
        result.add("\t\t" + offset + ": isub");
        this.offset += JVMInstruction.instructionOffset("isub");
        return result;
    }

    ArrayList<String> subtractIntStack() {
        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": isub");
        this.offset += JVMInstruction.instructionOffset("isub");
        return result;
    }

    ArrayList<String> subtractIntStackValue(boolean stack, int value) {
        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": bipush " + value);
        this.offset += JVMInstruction.instructionOffset("bipush");
        if (stack) {
            result.add("\t\t" + offset + ": swap");
            this.offset += JVMInstruction.instructionOffset("swap");
        }
        result.add("\t\t" + offset + ": isub");
        this.offset += JVMInstruction.instructionOffset("isub");
        return result;
    }

    ArrayList<String> subtractIntValue(int value1, int value2) {
        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": bipush " + value1);
        this.offset += JVMInstruction.instructionOffset("bipush");
        result.add("\t\t" + offset + ": bipush " + value2);
        this.offset += JVMInstruction.instructionOffset("bipush");
        result.add("\t\t" + offset + ": isub");
        this.offset += JVMInstruction.instructionOffset("isub");
        return result;
    }

    ArrayList<String> subtractIntValueId(int value, String identifier) {
        int id = (int) this.varIds.get(identifier);

        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": bipush " + value);
        this.offset += JVMInstruction.instructionOffset("bipush");
        result.add("\t\t" + offset + ": iload_" + id);
        this.offset += JVMInstruction.instructionOffset("iload");
        result.add("\t\t" + offset + ": isub");
        this.offset += JVMInstruction.instructionOffset("isub");
        return result;
    }

    ArrayList<String> subtractIntIdValue(String identifier, int value) {
        int id = (int) this.varIds.get(identifier);

        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": iload_" + id);
        this.offset += JVMInstruction.instructionOffset("iload");
        result.add("\t\t" + offset + ": bipush " + value);
        this.offset += JVMInstruction.instructionOffset("bipush");
        result.add("\t\t" + offset + ": isub");
        this.offset += JVMInstruction.instructionOffset("isub");
        return result;
    }

    ArrayList<String> multiplyIntId(String identifier1, String identifier2) {
        ArrayList<String> result = intBinaryOp(identifier1, identifier2);
        result.add("\t\t" + offset + ": imul");
        this.offset += JVMInstruction.instructionOffset("imul");
        return result;
    }

    ArrayList<String> multiplyIntStackId(boolean stack, String identifier) {
        int id = (int) this.varIds.get(identifier);

        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": iload_" + id);
        this.offset += JVMInstruction.instructionOffset("iload");
        if (stack) {
            result.add("\t\t" + offset + ": swap");
            this.offset += JVMInstruction.instructionOffset("swap");
        }
        result.add("\t\t" + offset + ": imul");
        this.offset += JVMInstruction.instructionOffset("imul");
        return result;
    }

    ArrayList<String> multiplyIntStack() {
        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": imul");
        this.offset += JVMInstruction.instructionOffset("imul");
        return result;
    }

    ArrayList<String> multiplyIntStackValue(boolean stack, int value) {
        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": bipush " + value);
        this.offset += JVMInstruction.instructionOffset("bipush");
        if (stack) {
            result.add("\t\t" + offset + ": swap");
            this.offset += JVMInstruction.instructionOffset("swap");
        }
        result.add("\t\t" + offset + ": imul");
        this.offset += JVMInstruction.instructionOffset("imul");
        return result;
    }

    ArrayList<String> multiplyIntValue(int value1, int value2) {
        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": bipush " + value1);
        this.offset += JVMInstruction.instructionOffset("bipush");
        result.add("\t\t" + offset + ": bipush " + value2);
        this.offset += JVMInstruction.instructionOffset("bipush");
        result.add("\t\t" + offset + ": imul");
        this.offset += JVMInstruction.instructionOffset("imul");
        return result;
    }

    ArrayList<String> multiplyIntValueId(int value, String identifier) {
        int id = (int) this.varIds.get(identifier);

        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": bipush " + value);
        this.offset += JVMInstruction.instructionOffset("bipush");
        result.add("\t\t" + offset + ": iload_" + id);
        this.offset += JVMInstruction.instructionOffset("iload");
        result.add("\t\t" + offset + ": imul");
        this.offset += JVMInstruction.instructionOffset("imul");
        return result;
    }

    ArrayList<String> multiplyIntIdValue(String identifier, int value) {
        int id = (int) this.varIds.get(identifier);

        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": iload_" + id);
        this.offset += JVMInstruction.instructionOffset("iload");
        result.add("\t\t" + offset + ": bipush " + value);
        this.offset += JVMInstruction.instructionOffset("bipush");
        result.add("\t\t" + offset + ": imul");
        this.offset += JVMInstruction.instructionOffset("imul");
        return result;
    }

    ArrayList<String> divisionIntId(String identifier1, String identifier2) {
        ArrayList<String> result = intBinaryOp(identifier1, identifier2);
        result.add("\t\t" + offset + ": idiv");
        this.offset += JVMInstruction.instructionOffset("idiv");
        return result;
    }

    ArrayList<String> divisionIntStackId(boolean stack, String identifier) {
        int id = (int) this.varIds.get(identifier);

        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": iload_" + id);
        this.offset += JVMInstruction.instructionOffset("iload");
        if (stack) {
            result.add("\t\t" + offset + ": swap");
            this.offset += JVMInstruction.instructionOffset("swap");
        }
        result.add("\t\t" + offset + ": idiv");
        this.offset += JVMInstruction.instructionOffset("idiv");
        return result;
    }

    ArrayList<String> divisionIntStack() {
        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": idiv");
        this.offset += JVMInstruction.instructionOffset("idiv");
        return result;
    }

    ArrayList<String> divisionIntStackValue(boolean stack, int value) {
        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": bipush " + value);
        this.offset += JVMInstruction.instructionOffset("bipush");
        if (stack) {
            result.add("\t\t" + offset + ": swap");
            this.offset += JVMInstruction.instructionOffset("swap");
        }
        result.add("\t\t" + offset + ": idiv");
        this.offset += JVMInstruction.instructionOffset("idiv");
        return result;
    }

    ArrayList<String> divisionIntValue(int value1, int value2) {
        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": bipush " + value1);
        this.offset += JVMInstruction.instructionOffset("bipush");
        result.add("\t\t" + offset + ": bipush " + value2);
        this.offset += JVMInstruction.instructionOffset("bipush");
        result.add("\t\t" + offset + ": idiv");
        this.offset += JVMInstruction.instructionOffset("idiv");
        return result;
    }

    ArrayList<String> divisionIntValueId(int value, String identifier) {
        int id = (int) this.varIds.get(identifier);

        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": bipush " + value);
        this.offset += JVMInstruction.instructionOffset("bipush");
        result.add("\t\t" + offset + ": iload_" + id);
        this.offset += JVMInstruction.instructionOffset("iload");
        result.add("\t\t" + offset + ": idiv");
        this.offset += JVMInstruction.instructionOffset("idiv");
        return result;
    }

    ArrayList<String> divisionIntIdValue(String identifier, int value) {
        int id = (int) this.varIds.get(identifier);

        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": iload_" + id);
        this.offset += JVMInstruction.instructionOffset("iload");
        result.add("\t\t" + offset + ": bipush " + value);
        this.offset += JVMInstruction.instructionOffset("bipush");
        result.add("\t\t" + offset + ": idiv");
        this.offset += JVMInstruction.instructionOffset("idiv");
        return result;
    }

    ArrayList<String> assignRealValue(float value, String identifier) {
        int id = this.constantPool.get(value);
        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": ldc " + id);
        this.offset += JVMInstruction.instructionOffset("ldc");
        if (this.varIds.containsKey(identifier)) {
            if (this.varIds.get(identifier) == 0) {
                this.varIds.put(identifier, this.varId);
                this.varId++;
            }
            result.add("\t\t" + offset + ": fstore_" + this.varIds.get(identifier));
            this.offset += JVMInstruction.instructionOffset("fstore");
        } else {
            this.varIds.put(identifier, this.varId);
            this.varId++;
            result.add("\t\t" + offset + ": fstore");
            this.offset += JVMInstruction.instructionOffset("fstore");
        }
        return result;
    }

    ArrayList<String> assignRealId(String identifier1, String identifier2) {
        int id = (int) this.varIds.get(identifier2);

        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": fload_" + id);
        this.offset += JVMInstruction.instructionOffset("fload");
        if (this.varIds.containsKey(identifier1)) {
            if (this.varIds.get(identifier1) == 0) {
                this.varIds.put(identifier1, this.varId);
                this.varId++;
            }
            result.add("\t\t" + offset + ": fstore_" + this.varIds.get(identifier1));
            this.offset += JVMInstruction.instructionOffset("fstore");
        } else {
            this.varIds.put(identifier1, this.varId);
            this.varId++;
            result.add("\t\t" + offset + ": fstore");
            this.offset += JVMInstruction.instructionOffset("fstore");
        }
        return result;
    }

    ArrayList<String> assignRealStack(String identifier) {
        ArrayList<String> result = new ArrayList<>();
        if (this.varIds.containsKey(identifier)) {
            if (this.varIds.get(identifier) == 0) {
                this.varIds.put(identifier, this.varId);
                this.varId++;
            }
            result.add("\t\t" + offset + ": fstore_" + this.varIds.get(identifier));
            this.offset += JVMInstruction.instructionOffset("fstore");
        } else {
            this.varIds.put(identifier, this.varId);
            this.varId++;
            result.add("\t\t" + offset + ": fstore");
            this.offset += JVMInstruction.instructionOffset("fstore");
        }
        return result;
    }

    ArrayList<String> realBinaryOp(String identifier1, String identifier2) {
        int id1 = (int) this.varIds.get(identifier1);
        int id2 = (int) this.varIds.get(identifier2);

        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": fload_" + id1);
        this.offset += JVMInstruction.instructionOffset("fload");
        result.add("\t\t" + offset + ": fload_" + id2);
        this.offset += JVMInstruction.instructionOffset("fload");
        return result;
    }

    ArrayList<String> negateRealStack () {
        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": fneg");
        this.offset += JVMInstruction.instructionOffset("fneg");
        return result;
    }

    ArrayList<String> negateRealId (String identifier) {
        int id = (int) this.varIds.get(identifier);
        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": fload_" + id);
        this.offset += JVMInstruction.instructionOffset("fload");
        result.add("\t\t" + offset + ": fneg");
        this.offset += JVMInstruction.instructionOffset("fneg");
        return result;
    }

    ArrayList<String> addRealId(String identifier1, String identifier2)  {
        ArrayList<String> result = realBinaryOp(identifier1, identifier2);
        result.add("\t\t" + offset + ": fadd");
        this.offset += JVMInstruction.instructionOffset("fadd");
        return result;
    }

    ArrayList<String> addRealStackId(boolean stack, String identifier) {
        int id = (int) this.varIds.get(identifier);

        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": fload_" + id);
        this.offset += JVMInstruction.instructionOffset("fload");
        if (stack) {
            result.add("\t\t" + offset + ": swap");
            this.offset += JVMInstruction.instructionOffset("swap");
        }
        result.add("\t\t" + offset + ": fadd");
        this.offset += JVMInstruction.instructionOffset("fadd");
        return result;
    }

    ArrayList<String> addRealStack() {
        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": fadd");
        this.offset += JVMInstruction.instructionOffset("fadd");
        return result;
    }

    ArrayList<String> addRealStackValue(boolean stack, float value) {
        int id = this.constantPool.get(value);

        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": fload_" + id);
        this.offset += JVMInstruction.instructionOffset("fload");
        if (stack) {
            result.add("\t\t" + offset + ": swap");
            this.offset += JVMInstruction.instructionOffset("swap");
        }
        result.add("\t\t" + offset + ": fadd");
        this.offset += JVMInstruction.instructionOffset("fadd");
        return result;
    }

    ArrayList<String> addRealValue(float value1, float value2) {
        int id1 = this.constantPool.get(value1);
        int id2 = this.constantPool.get(value2);

        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": ldc " + id1);
        this.offset += JVMInstruction.instructionOffset("ldc");
        result.add("\t\t" + offset + ": ldc " + id2);
        this.offset += JVMInstruction.instructionOffset("ldc");
        result.add("\t\t" + offset + ": fadd");
        this.offset += JVMInstruction.instructionOffset("fadd");
        return result;
    }

    ArrayList<String> addRealValueId(float value, String identifier) {
        int id1 = (int) this.varIds.get(identifier);
        int id2 = this.constantPool.get(value);

        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": ldc " + id2);
        this.offset += JVMInstruction.instructionOffset("ldc");
        result.add("\t\t" + offset + ": fload_" + id1);
        this.offset += JVMInstruction.instructionOffset("fload");
        result.add("\t\t" + offset + ": fadd");
        this.offset += JVMInstruction.instructionOffset("fadd");
        return result;
    }

    ArrayList<String> addRealIdValue(String identifier, float value) {
        int id1 = (int) this.varIds.get(identifier);
        int id2 = this.constantPool.get(value);

        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": fload_" + id1);
        this.offset += JVMInstruction.instructionOffset("fload");
        result.add("\t\t" + offset + ": ldc " + id2);
        this.offset += JVMInstruction.instructionOffset("ldc");
        result.add("\t\t" + offset + ": fadd");
        this.offset += JVMInstruction.instructionOffset("fadd");
        return result;
    }

    ArrayList<String> subtractRealId(String identifier1, String identifier2) {
        ArrayList<String> result = realBinaryOp(identifier1, identifier2);
        result.add("\t\t" + offset + ": fsub");
        this.offset += JVMInstruction.instructionOffset("fsub");
        return result;
    }

    ArrayList<String> subtractRealStackId(boolean stack, String identifier) {
        int id = (int) this.varIds.get(identifier);

        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": fload_" + id);
        this.offset += JVMInstruction.instructionOffset("fload");
        if (stack) {
            result.add("\t\t" + offset + ": swap");
            this.offset += JVMInstruction.instructionOffset("swap");
        }
        result.add("\t\t" + offset + ": fsub");
        this.offset += JVMInstruction.instructionOffset("fsub");
        return result;
    }

    ArrayList<String> subtractRealStack() {
        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": fsub");
        this.offset += JVMInstruction.instructionOffset("fsub");
        return result;
    }

    ArrayList<String> subtractRealStackValue(boolean stack, float value) {
        int id = this.constantPool.get(value);

        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": ldc " + id);
        this.offset += JVMInstruction.instructionOffset("ldc");
        if (stack) {
            result.add("\t\t" + offset + ": swap");
            this.offset += JVMInstruction.instructionOffset("swap");
        }
        result.add("\t\t" + offset + ": fsub");
        this.offset += JVMInstruction.instructionOffset("fsub");
        return result;
    }

    ArrayList<String> subtractRealValue(float value1, float value2) {
        int id1 = this.constantPool.get(value1);
        int id2 = this.constantPool.get(value2);

        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": ldc " + id1);
        this.offset += JVMInstruction.instructionOffset("ldc");
        result.add("\t\t" + offset + ": ldc " + id2);
        this.offset += JVMInstruction.instructionOffset("ldc");
        result.add("\t\t" + offset + ": fsub");
        this.offset += JVMInstruction.instructionOffset("fsub");
        return result;
    }

    ArrayList<String> subtractRealValueId(float value, String identifier) {
        int id1 = (int) this.varIds.get(identifier);
        int id2 = this.constantPool.get(value);

        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": ldc " + id2);
        this.offset += JVMInstruction.instructionOffset("ldc");
        result.add("\t\t" + offset + ": fload_" + id1);
        this.offset += JVMInstruction.instructionOffset("fload");
        result.add("\t\t" + offset + ": fsub");
        this.offset += JVMInstruction.instructionOffset("fsub");
        return result;
    }

    ArrayList<String> subtractRealIdValue(String identifier, float value) {
        int id1 = (int) this.varIds.get(identifier);
        int id2 = this.constantPool.get(value);

        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": fload_" + id1);
        this.offset += JVMInstruction.instructionOffset("fload");
        result.add("\t\t" + offset + ": ldc " + id2);
        this.offset += JVMInstruction.instructionOffset("ldc");
        result.add("\t\t" + offset + ": fsub");
        this.offset += JVMInstruction.instructionOffset("fsub");
        return result;
    }

    ArrayList<String> multiplyRealId(String identifier1, String identifier2) {
        ArrayList<String> result = realBinaryOp(identifier1, identifier2);
        result.add("\t\t" + offset + ": fmul");
        this.offset += JVMInstruction.instructionOffset("fmul");
        return result;
    }

    ArrayList<String> multiplyRealStackId(boolean stack, String identifier) {
        int id = (int) this.varIds.get(identifier);

        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": fload_" + id);
        this.offset += JVMInstruction.instructionOffset("fload");
        if (stack) {
            result.add("\t\t" + offset + ": swap");
            this.offset += JVMInstruction.instructionOffset("swap");
        }
        result.add("\t\t" + offset + ": fmul");
        this.offset += JVMInstruction.instructionOffset("fmul");
        return result;
    }

    ArrayList<String> multiplyRealStack() {
        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": fmul");
        this.offset += JVMInstruction.instructionOffset("fmul");
        return result;
    }

    ArrayList<String> multiplyRealStackValue(boolean stack, float value) {
        int id = this.constantPool.get(value);

        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": fload_" + id);
        this.offset += JVMInstruction.instructionOffset("fload");
        if (stack) {
            result.add("\t\t" + offset + ": swap");
            this.offset += JVMInstruction.instructionOffset("swap");
        }
        result.add("\t\t" + offset + ": fmul");
        this.offset += JVMInstruction.instructionOffset("fmul");
        return result;
    }

    ArrayList<String> multiplyRealValue(float value1, float value2) {
        int id1 = this.constantPool.get(value1);
        int id2 = this.constantPool.get(value2);

        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": ldc " + id1);
        this.offset += JVMInstruction.instructionOffset("ldc");
        result.add("\t\t" + offset + ": ldc " + id2);
        this.offset += JVMInstruction.instructionOffset("ldc");
        result.add("\t\t" + offset + ": fmul");
        this.offset += JVMInstruction.instructionOffset("fmul");
        return result;
    }

    ArrayList<String> multiplyRealValueId(float value, String identifier) {
        int id1 = (int) this.varIds.get(identifier);
        int id2 = this.constantPool.get(value);

        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": ldc " + id2);
        this.offset += JVMInstruction.instructionOffset("ldc");
        result.add("\t\t" + offset + ": fload_" + id1);
        this.offset += JVMInstruction.instructionOffset("fload");
        result.add("\t\t" + offset + ": fmul");
        this.offset += JVMInstruction.instructionOffset("fmul");
        return result;
    }

    ArrayList<String> multiplyRealIdValue(String identifier, float value) {
        int id1 = (int) this.varIds.get(identifier);
        int id2 = this.constantPool.get(value);

        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": fload_" + id1);
        this.offset += JVMInstruction.instructionOffset("fload");
        result.add("\t\t" + offset + ": ldc " + id2);
        this.offset += JVMInstruction.instructionOffset("ldc");
        result.add("\t\t" + offset + ": fmul");
        this.offset += JVMInstruction.instructionOffset("fmul");
        return result;
    }

    ArrayList<String> divisionRealId(String identifier1, String identifier2) {
        ArrayList<String> result = realBinaryOp(identifier1, identifier2);
        result.add("\t\t" + offset + ": fdiv");
        this.offset += JVMInstruction.instructionOffset("fdiv");
        return result;
    }

    ArrayList<String> divisionRealStackId(boolean stack, String identifier) {
        int id = (int) this.varIds.get(identifier);

        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": fload_" + id);
        this.offset += JVMInstruction.instructionOffset("fload");
        if (stack) {
            result.add("\t\t" + offset + ": swap");
            this.offset += JVMInstruction.instructionOffset("swap");
        }
        result.add("\t\t" + offset + ": fdiv");
        this.offset += JVMInstruction.instructionOffset("fdiv");
        return result;
    }

    ArrayList<String> divisionRealStack() {
        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": fdiv");
        this.offset += JVMInstruction.instructionOffset("fdiv");
        return result;
    }

    ArrayList<String> divisionRealStackValue(boolean stack, float value) {
        int id = this.constantPool.get(value);

        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": ldc " + id);
        this.offset += JVMInstruction.instructionOffset("ldc");
        if (stack) {
            result.add("\t\t" + offset + ": swap");
            this.offset += JVMInstruction.instructionOffset("swap");
        }
        result.add("\t\t" + offset + ": fdiv");
        this.offset += JVMInstruction.instructionOffset("fdiv");
        return result;
    }

    ArrayList<String> divisionRealValue(float value1, float value2) {
        int id1 = this.constantPool.get(value1);
        int id2 = this.constantPool.get(value2);

        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": ldc " + id1);
        this.offset += JVMInstruction.instructionOffset("ldc");
        result.add("\t\t" + offset + ": ldc " + id2);
        this.offset += JVMInstruction.instructionOffset("ldc");
        result.add("\t\t" + offset + ": fdiv");
        this.offset += JVMInstruction.instructionOffset("fdiv");
        return result;
    }

    ArrayList<String> divisionRealValueId(float value, String identifier) {
        int id1 = (int) this.varIds.get(identifier);
        int id2 = this.constantPool.get(value);

        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": ldc " + id2);
        this.offset += JVMInstruction.instructionOffset("ldc");
        result.add("\t\t" + offset + ": fload_" + id1);
        this.offset += JVMInstruction.instructionOffset("fload");
        result.add("\t\t" + offset + ": fdiv");
        this.offset += JVMInstruction.instructionOffset("fdiv");
        return result;
    }

    ArrayList<String> divisionRealIdValue(String identifier, float value) {
        int id1 = (int) this.varIds.get(identifier);
        int id2 = this.constantPool.get(value);

        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": fload_" + id1);
        this.offset += JVMInstruction.instructionOffset("fload");
        result.add("\t\t" + offset + ": ldc " + id2);
        this.offset += JVMInstruction.instructionOffset("ldc");
        result.add("\t\t" + offset + ": fdiv");
        this.offset += JVMInstruction.instructionOffset("fdiv");
        return result;
    }

    ArrayList<String> assignBoolValue (String identifier, boolean value) {
        ArrayList<String> result = new ArrayList<>();
        int valued;
        if (value) {
            valued = 1;
        } else {
            valued = 0;
        }
        result.add("\t\t" + offset + ": bipush " + valued);
        this.offset += JVMInstruction.instructionOffset("bipush");
        if (this.varIds.containsKey(identifier)) {
            result.add("\t\t" + offset + ": istore_" + this.varIds.get(identifier));
            this.offset += JVMInstruction.instructionOffset("istore");
        } else {
            this.varIds.put(identifier, this.varId);
            this.varId++;
            result.add("\t\t" + offset + ": istore");
            this.offset += JVMInstruction.instructionOffset("istore");
        }
        return result;
    }

    ArrayList<String> assignBoolStack (String identifier) {
        ArrayList<String> result = new ArrayList<>();

        if (this.varIds.containsKey(identifier)) {
            result.add("\t\t" + offset + ": istore_" + this.varIds.get(identifier));
            this.offset += JVMInstruction.instructionOffset("istore");
        } else {
            this.varIds.put(identifier, this.varId);
            this.varId++;
            result.add("\t\t" + offset + ": istore");
            this.offset += JVMInstruction.instructionOffset("istore");
        }
        return result;
    }

    ArrayList<String> assignBoolId (String identifier1, String identifier2) {
        int id = (int) this.varIds.get(identifier2);

        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": iload_" + id);
        this.offset += JVMInstruction.instructionOffset("iload");
        if (this.varIds.containsKey(identifier1)) {
            result.add("\t\t" + offset + ": istore_" + this.varIds.get(identifier1));
            this.offset += JVMInstruction.instructionOffset("istore");
        } else {
            this.varIds.put(identifier1, this.varId);
            this.varId++;
            result.add("\t\t" + offset + ": istore");
            this.offset += JVMInstruction.instructionOffset("istore");
        }
        return result;
    }

    ArrayList<String> orId (String identifier1, String identifier2) {
        int id1 = (int) this.varIds.get(identifier1);
        int id2 = (int) this.varIds.get(identifier2);

        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": iload_" + id1);
        this.offset += JVMInstruction.instructionOffset("iload");
        result.add("\t\t" + offset + ": iload_" + id2);
        this.offset += JVMInstruction.instructionOffset("iload");
        result.add("\t\t" + offset + ": ior");
        this.offset += JVMInstruction.instructionOffset("ior");
        return result;
    }

    ArrayList<String> orStackId (String identifier) {
        int id = (int) this.varIds.get(identifier);

        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": iload_" + id);
        result.add("\t\t" + offset + ": ior");
        this.offset += JVMInstruction.instructionOffset("ior");
        return result;
    }

    ArrayList<String> orStack () {
        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": ior");
        this.offset += JVMInstruction.instructionOffset("ior");
        return result;
    }

    ArrayList<String> orValueId (String identifier, boolean value) {
        int id = (int) this.varIds.get(identifier);

        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": iload_" + id);
        if (value) {
            result.add("\t\t" + offset + ": iconst_" + 1);
        } else {
            result.add("\t\t" + offset + ": iconst_" + 0);
        }
        this.offset += JVMInstruction.instructionOffset("iconst");
        result.add("\t\t" + offset + ": ior");
        this.offset += JVMInstruction.instructionOffset("ior");
        return result;
    }

    ArrayList<String> orValueStack (boolean value) {
        ArrayList<String> result = new ArrayList<>();
        if (value) {
            result.add("\t\t" + offset + ": iconst_" + 1);
        } else {
            result.add("\t\t" + offset + ": iconst_" + 0);
        }
        this.offset += JVMInstruction.instructionOffset("iconst");
        result.add("\t\t" + offset + ": ior");
        this.offset += JVMInstruction.instructionOffset("ior");
        return result;
    }

    ArrayList<String> orValue (boolean value1, boolean value2) {
        ArrayList<String> result = new ArrayList<>();
        if (value1) {
            result.add("\t\t" + offset + ": iconst_" + 1);
        } else {
            result.add("\t\t" + offset + ": iconst_" + 0);
        }
        this.offset += JVMInstruction.instructionOffset("iconst");
        if (value2) {
            result.add("\t\t" + offset + ": iconst_" + 1);
        } else {
            result.add("\t\t" + offset + ": iconst_" + 0);
        }
        this.offset += JVMInstruction.instructionOffset("iconst");
        result.add("\t\t" + offset + ": ior");
        this.offset += JVMInstruction.instructionOffset("ior");
        return result;
    }

    ArrayList<String> andId (String identifier1, String identifier2) {
        int id1 = (int) this.varIds.get(identifier1);
        int id2 = (int) this.varIds.get(identifier2);

        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": iload_" + id1);
        this.offset += JVMInstruction.instructionOffset("iload");
        result.add("\t\t" + offset + ": iload_" + id2);
        this.offset += JVMInstruction.instructionOffset("iload");
        result.add("\t\t" + offset + ": iand");
        this.offset += JVMInstruction.instructionOffset("iand");
        return result;
    }

    ArrayList<String> andStackId (String identifier) {
        int id = (int) this.varIds.get(identifier);

        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": iload_" + id);
        result.add("\t\t" + offset + ": iand");
        this.offset += JVMInstruction.instructionOffset("iand");
        return result;
    }

    ArrayList<String> andStack () {
        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": iand");
        this.offset += JVMInstruction.instructionOffset("iand");
        return result;
    }

    ArrayList<String> andValueStack (boolean value) {
        ArrayList<String> result = new ArrayList<>();
        if (value) {
            result.add("\t\t" + offset + ": iconst_" + 1);
        } else {
            result.add("\t\t" + offset + ": iconst_" + 0);
        }
        this.offset += JVMInstruction.instructionOffset("iconst");
        result.add("\t\t" + offset + ": iand");
        this.offset += JVMInstruction.instructionOffset("iand");
        return result;
    }

    ArrayList<String> andValueId (String identifier, boolean value) {
        int id = (int) this.varIds.get(identifier);

        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": iload_" + id);
        if (value) {
            result.add("\t\t" + offset + ": iconst_" + 1);
        } else {
            result.add("\t\t" + offset + ": iconst_" + 0);
        }
        this.offset += JVMInstruction.instructionOffset("iconst");
        result.add("\t\t" + offset + ": iand");
        this.offset += JVMInstruction.instructionOffset("iand");
        return result;
    }

    ArrayList<String> andValue (boolean value1, boolean value2) {
        ArrayList<String> result = new ArrayList<>();
        if (value1) {
            result.add("\t\t" + offset + ": iconst_" + 1);
        } else {
            result.add("\t\t" + offset + ": iconst_" + 0);
        }
        this.offset += JVMInstruction.instructionOffset("iconst");
        if (value2) {
            result.add("\t\t" + offset + ": iconst_" + 1);
        } else {
            result.add("\t\t" + offset + ": iconst_" + 0);
        }
        this.offset += JVMInstruction.instructionOffset("iconst");
        result.add("\t\t" + offset + ": iand");
        this.offset += JVMInstruction.instructionOffset("iand");
        return result;
    }

    ArrayList<String> xorId (String identifier1, String identifier2) {
        int id1 = (int) this.varIds.get(identifier1);
        int id2 = (int) this.varIds.get(identifier2);

        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": iload_" + id1);
        this.offset += JVMInstruction.instructionOffset("iload");
        result.add("\t\t" + offset + ": iload_" + id2);
        this.offset += JVMInstruction.instructionOffset("iload");
        result.add("\t\t" + offset + ": ixor");
        this.offset += JVMInstruction.instructionOffset("ixor");
        return result;
    }

    ArrayList<String> xorStackId (String identifier) {
        int id = (int) this.varIds.get(identifier);

        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": iload_" + id);
        result.add("\t\t" + offset + ": ixor");
        this.offset += JVMInstruction.instructionOffset("ixor");
        return result;
    }

    ArrayList<String> xorStack () {
        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": ixor");
        this.offset += JVMInstruction.instructionOffset("ixor");
        return result;
    }

    ArrayList<String> xorValueStack (boolean value) {
        ArrayList<String> result = new ArrayList<>();
        if (value) {
            result.add("\t\t" + offset + ": iconst_" + 1);
        } else {
            result.add("\t\t" + offset + ": iconst_" + 0);
        }
        this.offset += JVMInstruction.instructionOffset("iconst");
        result.add("\t\t" + offset + ": ixor");
        this.offset += JVMInstruction.instructionOffset("ixor");
        return result;
    }

    ArrayList<String> xorValueId (String identifier, boolean value) {
        int id = (int) this.varIds.get(identifier);

        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": iload_" + id);
        this.offset += JVMInstruction.instructionOffset("iload");
        if (value) {
            result.add("\t\t" + offset + ": iconst_" + 1);
        } else {
            result.add("\t\t" + offset + ": iconst_" + 0);
        }
        this.offset += JVMInstruction.instructionOffset("iconst");
        result.add("\t\t" + offset + ": ixor");
        this.offset += JVMInstruction.instructionOffset("ixor");
        return result;
    }

    ArrayList<String> xorValue (boolean value1, boolean value2) {
        ArrayList<String> result = new ArrayList<>();
        if (value1) {
            result.add("\t\t" + offset + ": iconst_" + 1);
        } else {
            result.add("\t\t" + offset + ": iconst_" + 0);
        }
        this.offset += JVMInstruction.instructionOffset("iconst");
        if (value2) {
            result.add("\t\t" + offset + ": iconst_" + 1);
        } else {
            result.add("\t\t" + offset + ": iconst_" + 0);
        }
        this.offset += JVMInstruction.instructionOffset("iconst");
        result.add("\t\t" + offset + ": ixor");
        this.offset += JVMInstruction.instructionOffset("ixor");
        return result;
    }

    private ArrayList<String> getIfShift(ArrayList<String> result) {
        this.offset += JVMInstruction.instructionOffset("if_icmp");
        result.add("\t\t" + offset + ": iconst_" + 0);
        this.offset += JVMInstruction.instructionOffset("iconst");
        int val = this.offset + JVMInstruction.instructionOffset("iconst") + JVMInstruction.instructionOffset("goto");
        result.add("\t\t" + offset + ": goto " + val);
        this.offset += JVMInstruction.instructionOffset("goto");
        result.add("\t\t" + offset + ": iconst_" + 1);
        this.offset += JVMInstruction.instructionOffset("iconst");
        return result;
    }

    private ArrayList<String> getStringsGt(ArrayList<String> result) {
        int val = this.offset + JVMInstruction.instructionOffset("if_icmp") + JVMInstruction.instructionOffset("iconst") + JVMInstruction.instructionOffset("goto");
        result.add("\t\t" + offset + ": if_icmpgt " + val);
        return getIfShift(result);
    }

    ArrayList<String> moreIntId (String identifier1, String identifier2) {
        int id1 = (int) this.varIds.get(identifier1);
        int id2 = (int) this.varIds.get(identifier2);

        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": iload_" + id1);
        this.offset += JVMInstruction.instructionOffset("iload");
        result.add("\t\t" + offset + ": iload_" + id2);
        this.offset += JVMInstruction.instructionOffset("iload");
        return getStringsGt(result);
    }

    ArrayList<String> moreIntIdStack (String identifier, boolean rightValue) {
        int id = (int) this.varIds.get(identifier);

        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": iload_" + id);
        this.offset += JVMInstruction.instructionOffset("iload");
        if (rightValue) {
            result.add("\t\t" + offset + ": swap");
            this.offset += JVMInstruction.instructionOffset("swap");
        }
        return getStringsGt(result);
    }

    ArrayList<String> moreIntIdValue (String identifier, int value, boolean rightValue) {
        int id = (int) this.varIds.get(identifier);

        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": iload_" + id);
        this.offset += JVMInstruction.instructionOffset("iload");
        result.add("\t\t" + offset + ": bipush " + value);
        this.offset += JVMInstruction.instructionOffset("bipush");
        if (!rightValue) {
            result.add("\t\t" + offset + ": swap");
            this.offset += JVMInstruction.instructionOffset("swap");
        }
        return getStringsGt(result);
    }

    ArrayList<String> moreIntValueStack (int value, boolean rightValue) {
        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": bipush " + value);
        this.offset += JVMInstruction.instructionOffset("bipush");
        if (!rightValue) {
            result.add("\t\t" + offset + ": swap");
            this.offset += JVMInstruction.instructionOffset("swap");
        }
        return getStringsGt(result);
    }

    ArrayList<String> moreIntStack () {
        ArrayList<String> result = new ArrayList<>();
        return getStringsGt(result);
    }

    ArrayList<String> moreIntValue (int value1, int value2) {
        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": bipush " + value1);
        this.offset += JVMInstruction.instructionOffset("bipush");
        result.add("\t\t" + offset + ": bipush " + value2);
        this.offset += JVMInstruction.instructionOffset("bipush");
        return getStringsGt(result);
    }

    private ArrayList<String> getStringsGe(ArrayList<String> result) {
        int val = this.offset + JVMInstruction.instructionOffset("if_icmp") + JVMInstruction.instructionOffset("iconst") + JVMInstruction.instructionOffset("goto");
        result.add("\t\t" + offset + ": if_icmpge " + val);
        return getIfShift(result);
    }

    ArrayList<String> moreEqualIntId (String identifier1, String identifier2) {
        int id1 = (int) this.varIds.get(identifier1);
        int id2 = (int) this.varIds.get(identifier2);

        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": iload_" + id1);
        this.offset += JVMInstruction.instructionOffset("iload");
        result.add("\t\t" + offset + ": iload_" + id2);
        this.offset += JVMInstruction.instructionOffset("iload");
        return getStringsGe(result);
    }

    ArrayList<String> moreEqualIntIdStack (String identifier, boolean rightValue) {
        int id = (int) this.varIds.get(identifier);

        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": iload_" + id);
        this.offset += JVMInstruction.instructionOffset("iload");
        if (rightValue) {
            result.add("\t\t" + offset + ": swap");
            this.offset += JVMInstruction.instructionOffset("swap");
        }
        return getStringsGe(result);
    }

    ArrayList<String> moreEqualIntIdValue (String identifier, int value, boolean rightValue) {
        int id = (int) this.varIds.get(identifier);

        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": iload_" + id);
        this.offset += JVMInstruction.instructionOffset("iload");
        result.add("\t\t" + offset + ": bipush " + value);
        this.offset += JVMInstruction.instructionOffset("bipush");
        if (!rightValue) {
            result.add("\t\t" + offset + ": swap");
            this.offset += JVMInstruction.instructionOffset("swap");
        }
        return getStringsGe(result);
    }

    ArrayList<String> moreEqualIntValueStack (int value, boolean rightValue) {
        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": bipush " + value);
        this.offset += JVMInstruction.instructionOffset("bipush");
        if (!rightValue) {
            result.add("\t\t" + offset + ": swap");
            this.offset += JVMInstruction.instructionOffset("swap");
        }
        return getStringsGe(result);
    }

    ArrayList<String> moreEqualIntStack () {
        ArrayList<String> result = new ArrayList<>();
        return getStringsGe(result);
    }

    ArrayList<String> moreEqualIntValue (int value1, int value2) {
        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": bipush " + value1);
        this.offset += JVMInstruction.instructionOffset("bipush");
        result.add("\t\t" + offset + ": bipush " + value2);
        this.offset += JVMInstruction.instructionOffset("bipush");
        return getStringsGe(result);
    }

    private ArrayList<String> getStringsEq(ArrayList<String> result) {
        int val = this.offset + JVMInstruction.instructionOffset("if_icmp") + JVMInstruction.instructionOffset("iconst") + JVMInstruction.instructionOffset("goto");
        result.add("\t\t" + offset + ": if_icmpeq " + val);
        return getIfShift(result);
    }

    ArrayList<String> equalIntId (String identifier1, String identifier2) {
        int id1 = (int) this.varIds.get(identifier1);
        int id2 = (int) this.varIds.get(identifier2);

        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": iload_" + id1);
        this.offset += JVMInstruction.instructionOffset("iload");
        result.add("\t\t" + offset + ": iload_" + id2);
        this.offset += JVMInstruction.instructionOffset("iload");
        return getStringsEq(result);
    }

    ArrayList<String> equalIntIdStack (String identifier, boolean rightValue) {
        int id = (int) this.varIds.get(identifier);

        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": iload_" + id);
        this.offset += JVMInstruction.instructionOffset("iload");
        if (rightValue) {
            result.add("\t\t" + offset + ": swap");
            this.offset += JVMInstruction.instructionOffset("swap");
        }
        return getStringsEq(result);
    }

    ArrayList<String> equalIntIdValue (String identifier, int value, boolean rightValue) {
        int id = (int) this.varIds.get(identifier);

        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": iload_" + id);
        this.offset += JVMInstruction.instructionOffset("iload");
        result.add("\t\t" + offset + ": bipush " + value);
        this.offset += JVMInstruction.instructionOffset("bipush");
        if (!rightValue) {
            result.add("\t\t" + offset + ": swap");
            this.offset += JVMInstruction.instructionOffset("swap");
        }
        return getStringsEq(result);
    }

    ArrayList<String> equalIntValueStack (int value, boolean rightValue) {
        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": bipush " + value);
        this.offset += JVMInstruction.instructionOffset("bipush");
        if (!rightValue) {
            result.add("\t\t" + offset + ": swap");
            this.offset += JVMInstruction.instructionOffset("swap");
        }
        return getStringsEq(result);
    }

    ArrayList<String> equalIntStack () {
        ArrayList<String> result = new ArrayList<>();
        return getStringsEq(result);
    }

    ArrayList<String> equalIntValue (int value1, int value2) {
        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": bipush " + value1);
        this.offset += JVMInstruction.instructionOffset("bipush");
        result.add("\t\t" + offset + ": bipush " + value2);
        this.offset += JVMInstruction.instructionOffset("bipush");
        return getStringsEq(result);
    }

    private ArrayList<String> getStringsLt(ArrayList<String> result) {
        int val = this.offset + JVMInstruction.instructionOffset("if_icmp") + JVMInstruction.instructionOffset("iconst") + JVMInstruction.instructionOffset("goto");
        result.add("\t\t" + offset + ": if_icmplt " + val);
        return getIfShift(result);
    }

    ArrayList<String> lessIntId (String identifier1, String identifier2) {
        int id1 = (int) this.varIds.get(identifier1);
        int id2 = (int) this.varIds.get(identifier2);

        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": iload_" + id1);
        this.offset += JVMInstruction.instructionOffset("iload");
        result.add("\t\t" + offset + ": iload_" + id2);
        this.offset += JVMInstruction.instructionOffset("iload");
        return getStringsLt(result);
    }

    ArrayList<String> lessIntIdStack (String identifier, boolean rightValue) {
        int id = (int) this.varIds.get(identifier);

        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": iload_" + id);
        this.offset += JVMInstruction.instructionOffset("iload");
        if (rightValue) {
            result.add("\t\t" + offset + ": swap");
            this.offset += JVMInstruction.instructionOffset("swap");
        }
        return getStringsLt(result);
    }

    ArrayList<String> lessIntIdValue (String identifier, int value, boolean rightValue) {
        int id = (int) this.varIds.get(identifier);

        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": iload_" + id);
        this.offset += JVMInstruction.instructionOffset("iload");
        result.add("\t\t" + offset + ": bipush " + value);
        this.offset += JVMInstruction.instructionOffset("bipush");
        if (!rightValue) {
            result.add("\t\t" + offset + ": swap");
            this.offset += JVMInstruction.instructionOffset("swap");
        }
        return getStringsLt(result);
    }

    ArrayList<String> lessIntValueStack (int value, boolean rightValue) {
        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": bipush " + value);
        this.offset += JVMInstruction.instructionOffset("bipush");
        if (!rightValue) {
            result.add("\t\t" + offset + ": swap");
            this.offset += JVMInstruction.instructionOffset("swap");
        }
        return getStringsLt(result);
    }

    ArrayList<String> lessIntStack () {
        ArrayList<String> result = new ArrayList<>();
        return getStringsLt(result);
    }

    ArrayList<String> lessIntValue (int value1, int value2) {
        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": bipush " + value1);
        this.offset += JVMInstruction.instructionOffset("bipush");
        result.add("\t\t" + offset + ": bipush " + value2);
        this.offset += JVMInstruction.instructionOffset("bipush");
        return getStringsLt(result);
    }

    private ArrayList<String> getStringsLe(ArrayList<String> result) {
        int val = this.offset + JVMInstruction.instructionOffset("if_icmp") + JVMInstruction.instructionOffset("iconst") + JVMInstruction.instructionOffset("goto");
        result.add("\t\t" + offset + ": if_icmple " + val);
        return getIfShift(result);
    }

    ArrayList<String> lessEqualIntId (String identifier1, String identifier2) {
        int id1 = (int) this.varIds.get(identifier1);
        int id2 = (int) this.varIds.get(identifier2);

        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": iload_" + id1);
        this.offset += JVMInstruction.instructionOffset("iload");
        result.add("\t\t" + offset + ": iload_" + id2);
        this.offset += JVMInstruction.instructionOffset("iload");
        return getStringsLe(result);
    }

    ArrayList<String> lessEqualIntIdStack (String identifier, boolean rightValue) {
        int id = (int) this.varIds.get(identifier);

        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": iload_" + id);
        this.offset += JVMInstruction.instructionOffset("iload");
        if (rightValue) {
            result.add("\t\t" + offset + ": swap");
            this.offset += JVMInstruction.instructionOffset("swap");
        }
        return getStringsLe(result);
    }

    ArrayList<String> lessEqualIntIdValue (String identifier, int value, boolean rightValue) {
        int id = (int) this.varIds.get(identifier);

        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": iload_" + id);
        this.offset += JVMInstruction.instructionOffset("iload");
        result.add("\t\t" + offset + ": bipush " + value);
        this.offset += JVMInstruction.instructionOffset("bipush");
        if (!rightValue) {
            result.add("\t\t" + offset + ": swap");
            this.offset += JVMInstruction.instructionOffset("swap");
        }
        return getStringsLe(result);
    }

    ArrayList<String> lessEqualIntValueStack (int value, boolean rightValue) {
        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": bipush " + value);
        this.offset += JVMInstruction.instructionOffset("bipush");
        if (!rightValue) {
            result.add("\t\t" + offset + ": swap");
            this.offset += JVMInstruction.instructionOffset("swap");
        }
        return getStringsLe(result);
    }

    ArrayList<String> lessEqualIntStack () {
        ArrayList<String> result = new ArrayList<>();
        return getStringsLe(result);
    }

    ArrayList<String> lessEqualIntValue (int value1, int value2) {
        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": bipush " + value1);
        this.offset += JVMInstruction.instructionOffset("bipush");
        result.add("\t\t" + offset + ": bipush " + value2);
        this.offset += JVMInstruction.instructionOffset("bipush");
        return getStringsLe(result);
    }

    private ArrayList<String> getStringsNe(ArrayList<String> result) {
        int val = this.offset + JVMInstruction.instructionOffset("if_icmp") + JVMInstruction.instructionOffset("iconst") + JVMInstruction.instructionOffset("goto");
        result.add("\t\t" + offset + ": if_icmpne " + val);
        return getIfShift(result);
    }

    ArrayList<String> notEqualIntId (String identifier1, String identifier2) {
        int id1 = (int) this.varIds.get(identifier1);
        int id2 = (int) this.varIds.get(identifier2);

        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": iload_" + id1);
        this.offset += JVMInstruction.instructionOffset("iload");
        result.add("\t\t" + offset + ": iload_" + id2);
        this.offset += JVMInstruction.instructionOffset("iload");
        return getStringsNe(result);
    }

    ArrayList<String> notEqualIntIdStack (String identifier, boolean rightValue) {
        int id = (int) this.varIds.get(identifier);

        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": iload_" + id);
        this.offset += JVMInstruction.instructionOffset("iload");
        if (rightValue) {
            result.add("\t\t" + offset + ": swap");
            this.offset += JVMInstruction.instructionOffset("swap");
        }
        return getStringsNe(result);
    }

    ArrayList<String> notEqualIntIdValue (String identifier, int value, boolean rightValue) {
        int id = (int) this.varIds.get(identifier);

        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": iload_" + id);
        this.offset += JVMInstruction.instructionOffset("iload");
        result.add("\t\t" + offset + ": bipush " + value);
        this.offset += JVMInstruction.instructionOffset("bipush");
        if (!rightValue) {
            result.add("\t\t" + offset + ": swap");
            this.offset += JVMInstruction.instructionOffset("swap");
        }
        return getStringsNe(result);
    }

    ArrayList<String> notEqualIntValueStack (int value, boolean rightValue) {
        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": bipush " + value);
        this.offset += JVMInstruction.instructionOffset("bipush");
        if (!rightValue) {
            result.add("\t\t" + offset + ": swap");
            this.offset += JVMInstruction.instructionOffset("swap");
        }
        return getStringsNe(result);
    }

    ArrayList<String> notEqualIntStack () {
        ArrayList<String> result = new ArrayList<>();
        return getStringsNe(result);
    }

    ArrayList<String> notEqualIntValue (int value1, int value2) {
        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": bipush " + value1);
        this.offset += JVMInstruction.instructionOffset("bipush");
        result.add("\t\t" + offset + ": bipush " + value2);
        this.offset += JVMInstruction.instructionOffset("bipush");
        return getStringsNe(result);
    }

    private ArrayList<String> getIfIfShift(ArrayList<String> result) {
        this.offset += JVMInstruction.instructionOffset("if");
        result.add("\t\t" + offset + ": iconst_" + 0);
        this.offset += JVMInstruction.instructionOffset("iconst");
        result.add("\t\t" + offset + ": goto " + this.offset + JVMInstruction.instructionOffset("iconst") + JVMInstruction.instructionOffset("goto"));
        this.offset += JVMInstruction.instructionOffset("goto");
        result.add("\t\t" + offset + ": iconst_" + 1);
        this.offset += JVMInstruction.instructionOffset("iconst");
        return result;
    }

    ArrayList<String> moreRealId (String identifier1, String identifier2) {
        int id1 = (int) this.varIds.get(identifier1);
        int id2 = (int) this.varIds.get(identifier2);

        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": iload_" + id1);
        this.offset += JVMInstruction.instructionOffset("iload");
        result.add("\t\t" + offset + ": iload_" + id2);
        this.offset += JVMInstruction.instructionOffset("iload");
        result.add("\t\t" + offset + ": fcmp");
        this.offset += JVMInstruction.instructionOffset("fcmp");
        result.add("\t\t" + offset + ": ifgt");
        return getIfIfShift(result);
    }

    ArrayList<String> moreRealIdValue (String identifier, float value, boolean rightValue) {
        int id1 = (int) this.varIds.get(identifier);
        int id2 = this.constantPool.get(value);

        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": fload_" + id1);
        this.offset += JVMInstruction.instructionOffset("fload");
        result.add("\t\t" + offset + ": ldc " + id2);
        this.offset += JVMInstruction.instructionOffset("ldc");
        if (!rightValue) {
            result.add("\t\t" + offset + ": swap");
            this.offset += JVMInstruction.instructionOffset("swap");
        }
        result.add("\t\t" + offset + ": ifgt");
        return getIfIfShift(result);
    }

    ArrayList<String> moreRealIdStack (String identifier, boolean rightValue) {
        int id = (int) this.varIds.get(identifier);

        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": fload_" + id);
        this.offset += JVMInstruction.instructionOffset("fload");
        if (rightValue) {
            result.add("\t\t" + offset + ": swap");
            this.offset += JVMInstruction.instructionOffset("swap");
        }
        result.add("\t\t" + offset + ": ifgt");
        return getIfIfShift(result);
    }

    ArrayList<String> moreRealStack () {
        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": ifgt");
        return getIfIfShift(result);
    }

    ArrayList<String> moreRealStackValue (float value, boolean rightValue) {
        int id = this.constantPool.get(value);

        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": ldc " + id);
        this.offset += JVMInstruction.instructionOffset("ldc");
        if (!rightValue) {
            result.add("\t\t" + offset + ": swap");
            this.offset += JVMInstruction.instructionOffset("swap");
        }
        result.add("\t\t" + offset + ": ifgt");
        return getIfIfShift(result);
    }

    ArrayList<String> moreRealValue (float value1, float value2) {
        int id1 = this.constantPool.get(value1);
        int id2 = this.constantPool.get(value2);

        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": ldc " + id1);
        this.offset += JVMInstruction.instructionOffset("ldc");
        result.add("\t\t" + offset + ": ldc " + id2);
        this.offset += JVMInstruction.instructionOffset("ldc");
        result.add("\t\t" + offset + ": ifgt");
        return getIfIfShift(result);
    }

    ArrayList<String> moreEqualRealId (String identifier1, String identifier2) {
        int id1 = (int) this.varIds.get(identifier1);
        int id2 = (int) this.varIds.get(identifier2);

        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": iload_" + id1);
        this.offset += JVMInstruction.instructionOffset("iload");
        result.add("\t\t" + offset + ": iload_" + id2);
        this.offset += JVMInstruction.instructionOffset("iload");
        result.add("\t\t" + offset + ": fcmp");
        this.offset += JVMInstruction.instructionOffset("fcmp");
        result.add("\t\t" + offset + ": ifge");
        return getIfIfShift(result);
    }

    ArrayList<String> moreEqualRealIdValue (String identifier, float value, boolean rightValue) {
        int id1 = (int) this.varIds.get(identifier);
        int id2 = this.constantPool.get(value);

        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": fload_" + id1);
        this.offset += JVMInstruction.instructionOffset("fload");
        result.add("\t\t" + offset + ": ldc " + id2);
        this.offset += JVMInstruction.instructionOffset("ldc");
        if (!rightValue) {
            result.add("\t\t" + offset + ": swap");
            this.offset += JVMInstruction.instructionOffset("swap");
        }
        result.add("\t\t" + offset + ": ifge");
        return getIfIfShift(result);
    }

    ArrayList<String> moreEqualRealIdStack (String identifier, boolean rightValue) {
        int id = (int) this.varIds.get(identifier);

        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": fload_" + id);
        this.offset += JVMInstruction.instructionOffset("fload");
        if (rightValue) {
            result.add("\t\t" + offset + ": swap");
            this.offset += JVMInstruction.instructionOffset("swap");
        }
        result.add("\t\t" + offset + ": ifge");
        return getIfIfShift(result);
    }

    ArrayList<String> moreEqualRealStack () {
        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": ifge");
        return getIfIfShift(result);
    }

    ArrayList<String> moreEqualRealStackValue (float value, boolean rightValue) {
        int id = this.constantPool.get(value);

        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": ldc " + id);
        this.offset += JVMInstruction.instructionOffset("ldc");
        if (!rightValue) {
            result.add("\t\t" + offset + ": swap");
            this.offset += JVMInstruction.instructionOffset("swap");
        }
        result.add("\t\t" + offset + ": ifge");
        return getIfIfShift(result);
    }

    ArrayList<String> moreEqualRealValue (float value1, float value2) {
        int id1 = this.constantPool.get(value1);
        int id2 = this.constantPool.get(value2);

        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": ldc " + id1);
        this.offset += JVMInstruction.instructionOffset("ldc");
        result.add("\t\t" + offset + ": ldc " + id2);
        this.offset += JVMInstruction.instructionOffset("ldc");
        result.add("\t\t" + offset + ": ifge");
        return getIfIfShift(result);
    }

    ArrayList<String> equalRealId (String identifier1, String identifier2) {
        int id1 = (int) this.varIds.get(identifier1);
        int id2 = (int) this.varIds.get(identifier2);

        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": iload_" + id1);
        this.offset += JVMInstruction.instructionOffset("iload");
        result.add("\t\t" + offset + ": iload_" + id2);
        this.offset += JVMInstruction.instructionOffset("iload");
        result.add("\t\t" + offset + ": fcmp");
        this.offset += JVMInstruction.instructionOffset("fcmp");
        result.add("\t\t" + offset + ": ifeq");
        return getIfIfShift(result);
    }

    ArrayList<String> equalRealIdValue (String identifier, float value, boolean rightValue) {
        int id1 = (int) this.varIds.get(identifier);
        int id2 = this.constantPool.get(value);

        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": fload_" + id1);
        this.offset += JVMInstruction.instructionOffset("fload");
        result.add("\t\t" + offset + ": ldc " + id2);
        this.offset += JVMInstruction.instructionOffset("ldc");
        if (!rightValue) {
            result.add("\t\t" + offset + ": swap");
            this.offset += JVMInstruction.instructionOffset("swap");
        }
        result.add("\t\t" + offset + ": ifeq");
        return getIfIfShift(result);
    }

    ArrayList<String> equalRealIdStack (String identifier, boolean rightValue) {
        int id = (int) this.varIds.get(identifier);

        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": fload_" + id);
        this.offset += JVMInstruction.instructionOffset("fload");
        if (rightValue) {
            result.add("\t\t" + offset + ": swap");
            this.offset += JVMInstruction.instructionOffset("swap");
        }
        result.add("\t\t" + offset + ": ifeq");
        return getIfIfShift(result);
    }

    ArrayList<String> equalRealStack () {
        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": ifeq");
        return getIfIfShift(result);
    }

    ArrayList<String> equalRealStackValue (float value, boolean rightValue) {
        int id = this.constantPool.get(value);

        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": ldc " + id);
        this.offset += JVMInstruction.instructionOffset("ldc");
        if (!rightValue) {
            result.add("\t\t" + offset + ": swap");
            this.offset += JVMInstruction.instructionOffset("swap");
        }
        result.add("\t\t" + offset + ": ifeq");
        return getIfIfShift(result);
    }

    ArrayList<String> equalRealValue (float value1, float value2) {
        int id1 = this.constantPool.get(value1);
        int id2 = this.constantPool.get(value2);

        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": ldc " + id1);
        this.offset += JVMInstruction.instructionOffset("ldc");
        result.add("\t\t" + offset + ": ldc " + id2);
        this.offset += JVMInstruction.instructionOffset("ldc");
        result.add("\t\t" + offset + ": ifeq");
        return getIfIfShift(result);
    }

    ArrayList<String> lessRealId (String identifier1, String identifier2) {
        int id1 = (int) this.varIds.get(identifier1);
        int id2 = (int) this.varIds.get(identifier2);

        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": iload_" + id1);
        this.offset += JVMInstruction.instructionOffset("iload");
        result.add("\t\t" + offset + ": iload_" + id2);
        this.offset += JVMInstruction.instructionOffset("iload");
        result.add("\t\t" + offset + ": fcmp");
        this.offset += JVMInstruction.instructionOffset("fcmp");
        result.add("\t\t" + offset + ": iflt");
        return getIfIfShift(result);
    }

    ArrayList<String> lessRealIdValue (String identifier, float value, boolean rightValue) {
        int id1 = (int) this.varIds.get(identifier);
        int id2 = this.constantPool.get(value);

        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": fload_" + id1);
        this.offset += JVMInstruction.instructionOffset("fload");
        result.add("\t\t" + offset + ": ldc " + id2);
        this.offset += JVMInstruction.instructionOffset("ldc");
        if (!rightValue) {
            result.add("\t\t" + offset + ": swap");
            this.offset += JVMInstruction.instructionOffset("swap");
        }
        result.add("\t\t" + offset + ": iflt");
        return getIfIfShift(result);
    }

    ArrayList<String> lessRealIdStack (String identifier, boolean rightValue) {
        int id = (int) this.varIds.get(identifier);

        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": fload_" + id);
        this.offset += JVMInstruction.instructionOffset("fload");
        if (rightValue) {
            result.add("\t\t" + offset + ": swap");
            this.offset += JVMInstruction.instructionOffset("swap");
        }
        result.add("\t\t" + offset + ": iflt");
        return getIfIfShift(result);
    }

    ArrayList<String> lessRealStack () {
        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": iflt");
        return getIfIfShift(result);
    }

    ArrayList<String> lessRealStackValue (float value, boolean rightValue) {
        int id = this.constantPool.get(value);

        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": ldc " + id);
        this.offset += JVMInstruction.instructionOffset("ldc");
        if (!rightValue) {
            result.add("\t\t" + offset + ": swap");
            this.offset += JVMInstruction.instructionOffset("swap");
        }
        result.add("\t\t" + offset + ": iflt");
        return getIfIfShift(result);
    }

    ArrayList<String> lessRealValue (float value1, float value2) {
        int id1 = this.constantPool.get(value1);
        int id2 = this.constantPool.get(value2);

        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": ldc " + id1);
        this.offset += JVMInstruction.instructionOffset("ldc");
        result.add("\t\t" + offset + ": ldc " + id2);
        this.offset += JVMInstruction.instructionOffset("ldc");
        result.add("\t\t" + offset + ": iflt");
        return getIfIfShift(result);
    }

    ArrayList<String> lessEqualRealId (String identifier1, String identifier2) {
        int id1 = (int) this.varIds.get(identifier1);
        int id2 = (int) this.varIds.get(identifier2);

        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": iload_" + id1);
        this.offset += JVMInstruction.instructionOffset("iload");
        result.add("\t\t" + offset + ": iload_" + id2);
        this.offset += JVMInstruction.instructionOffset("iload");
        result.add("\t\t" + offset + ": fcmp");
        this.offset += JVMInstruction.instructionOffset("fcmp");
        result.add("\t\t" + offset + ": ifle");
        return getIfIfShift(result);
    }

    ArrayList<String> lessEqualRealIdValue (String identifier, float value, boolean rightValue) {
        int id1 = (int) this.varIds.get(identifier);
        int id2 = this.constantPool.get(value);

        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": fload_" + id1);
        this.offset += JVMInstruction.instructionOffset("fload");
        result.add("\t\t" + offset + ": ldc " + id2);
        this.offset += JVMInstruction.instructionOffset("ldc");
        if (!rightValue) {
            result.add("\t\t" + offset + ": swap");
            this.offset += JVMInstruction.instructionOffset("swap");
        }
        result.add("\t\t" + offset + ": ifle");
        return getIfIfShift(result);
    }

    ArrayList<String> lessEqualRealIdStack (String identifier, boolean rightValue) {
        int id = (int) this.varIds.get(identifier);

        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": fload_" + id);
        this.offset += JVMInstruction.instructionOffset("fload");
        if (rightValue) {
            result.add("\t\t" + offset + ": swap");
            this.offset += JVMInstruction.instructionOffset("swap");
        }
        result.add("\t\t" + offset + ": ifle");
        return getIfIfShift(result);
    }

    ArrayList<String> lessEqualRealStack () {
        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": ifle");
        return getIfIfShift(result);
    }

    ArrayList<String> lessEqualRealStackValue (float value, boolean rightValue) {
        int id = this.constantPool.get(value);

        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": ldc " + id);
        this.offset += JVMInstruction.instructionOffset("ldc");
        if (!rightValue) {
            result.add("\t\t" + offset + ": swap");
            this.offset += JVMInstruction.instructionOffset("swap");
        }
        result.add("\t\t" + offset + ": ifle");
        return getIfIfShift(result);
    }

    ArrayList<String> lessEqualRealValue (float value1, float value2) {
        int id1 = this.constantPool.get(value1);
        int id2 = this.constantPool.get(value2);

        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": ldc " + id1);
        this.offset += JVMInstruction.instructionOffset("ldc");
        result.add("\t\t" + offset + ": ldc " + id2);
        this.offset += JVMInstruction.instructionOffset("ldc");
        result.add("\t\t" + offset + ": ifle");
        return getIfIfShift(result);
    }

    ArrayList<String> notEqualRealId (String identifier1, String identifier2) {
        int id1 = (int) this.varIds.get(identifier1);
        int id2 = (int) this.varIds.get(identifier2);

        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": iload_" + id1);
        this.offset += JVMInstruction.instructionOffset("iload");
        result.add("\t\t" + offset + ": iload_" + id2);
        this.offset += JVMInstruction.instructionOffset("iload");
        result.add("\t\t" + offset + ": fcmp");
        this.offset += JVMInstruction.instructionOffset("fcmp");
        result.add("\t\t" + offset + ": ifne");
        return getIfIfShift(result);
    }

    ArrayList<String> notEqualRealIdValue (String identifier, float value, boolean rightValue) {
        int id1 = (int) this.varIds.get(identifier);
        int id2 = this.constantPool.get(value);

        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": fload_" + id1);
        this.offset += JVMInstruction.instructionOffset("fload");
        result.add("\t\t" + offset + ": ldc " + id2);
        this.offset += JVMInstruction.instructionOffset("ldc");
        if (!rightValue) {
            result.add("\t\t" + offset + ": swap");
            this.offset += JVMInstruction.instructionOffset("swap");
        }
        result.add("\t\t" + offset + ": ifne");
        return getIfIfShift(result);
    }

    ArrayList<String> notEqualRealIdStack (String identifier, boolean rightValue) {
        int id = (int) this.varIds.get(identifier);

        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": fload_" + id);
        this.offset += JVMInstruction.instructionOffset("fload");
        if (rightValue) {
            result.add("\t\t" + offset + ": swap");
            this.offset += JVMInstruction.instructionOffset("swap");
        }
        result.add("\t\t" + offset + ": ifne");
        return getIfIfShift(result);
    }

    ArrayList<String> notEqualRealStack () {
        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": ifne");
        return getIfIfShift(result);
    }

    ArrayList<String> notEqualRealStackValue (float value, boolean rightValue) {
        int id = this.constantPool.get(value);

        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": ldc " + id);
        this.offset += JVMInstruction.instructionOffset("ldc");
        if (!rightValue) {
            result.add("\t\t" + offset + ": swap");
            this.offset += JVMInstruction.instructionOffset("swap");
        }
        result.add("\t\t" + offset + ": ifne");
        return getIfIfShift(result);
    }

    ArrayList<String> notEqualRealValue (float value1, float value2) {
        int id1 = this.constantPool.get(value1);
        int id2 = this.constantPool.get(value2);

        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": ldc " + id1);
        this.offset += JVMInstruction.instructionOffset("ldc");
        result.add("\t\t" + offset + ": ldc " + id2);
        this.offset += JVMInstruction.instructionOffset("ldc");
        result.add("\t\t" + offset + ": ifne");
        return getIfIfShift(result);
    }

    ArrayList<String> returnVoid() {
        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": return");
        this.offset += JVMInstruction.instructionOffset("return");
        return result;
    }

    ArrayList<String> returnInt() {
        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": ireturn");
        this.offset += JVMInstruction.instructionOffset("ireturn");
        return result;
    }

    ArrayList<String> returnReal() {
        ArrayList<String> result = new ArrayList<>();
        result.add("\t\t" + offset + ": dreturn");
        this.offset += JVMInstruction.instructionOffset("dreturn");
        return result;
    }
}