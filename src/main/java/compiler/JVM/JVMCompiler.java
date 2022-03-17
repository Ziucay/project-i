package compiler.JVM;

import compiler.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class JVMCompiler {
    HashMap<Float, Integer> constantPool;
    public ArrayList<String> code;
    String name;

    public JVMCompiler(List<Float> constants, String name) {
        this.constantPool = new HashMap<>();
        this.code = new ArrayList<>();
        this.name = name;
        int k = 1;
        this.code.add("public class " + this.name);
        this.code.add(" minor version: 0");
        this.code.add(" minor version: 55");
        this.code.add(" flags: ACC_PUBLIC");
        this.code.add(" this_class: #0");
        this.code.add(" super_class: #0");

        if (constants.size() != 0) {
            this.code.add("Constant pool:");
        }
        for (int i = 1; i <= constants.size(); i++) {
            if (!this.constantPool.containsKey(constants.get(i))) {
                this.code.add("\t#" + k + " = Float\t\t" + constants.get(i));
                this.constantPool.put(constants.get(i), k);
                k++;
            }
        }
    }

    public void traverseTree (Node root) {
        this.code.add("{");
        for (int i = 0; i < root.descendants.size(); i++) {
            Node routine = root.descendants.get(i);
            if (routine.descendants.size() == 3) {
                JVMFrame frame = new JVMFrame(this.constantPool, routine.descendants.get(1).descendants, routine.descendants.get(0).identifier);
                code.addAll(frame.startFrame());
                visitNode (routine.descendants.get(2), frame);
                code.addAll(frame.endFrame());
            } else {
                JVMFrame frame = new JVMFrame(this.constantPool, routine.descendants.get(1).descendants, routine.descendants.get(2).identifier, routine.descendants.get(0).identifier);
                code.addAll(frame.startFrame());
                visitNode(routine.descendants.get(3), frame);
                code.addAll(frame.endFrame());
            }
        }
        this.code.remove(this.code.size() - 1);
        this.code.add("}");
    }

    String visitNode (Node node, JVMFrame frame) {
        String left, right;
        String start, end;
        String identifier;
        ArrayList<Integer> offset = new ArrayList<>();
        ArrayList<Integer> index = new ArrayList<>();
        switch (node.identifier) {
            case "plus":
                left = visitNode(node.descendants.get(0), frame);
                right = visitNode(node.descendants.get(1), frame);
                return performAdd(node.descendants.get(0), node.descendants.get(1), left, right, frame);
            case "minus":
                left = visitNode(node.descendants.get(0), frame);
                right = visitNode(node.descendants.get(1), frame);
                return performSubtract(node.descendants.get(0), node.descendants.get(1), left, right, frame);
            case "multiply":
                left = visitNode(node.descendants.get(0), frame);
                right = visitNode(node.descendants.get(1), frame);
                return performMultiply(node.descendants.get(0), node.descendants.get(1), left, right, frame);
            case "divide":
                left = visitNode(node.descendants.get(0), frame);
                right = visitNode(node.descendants.get(1), frame);
                return performDivision(node.descendants.get(0), node.descendants.get(1), left, right, frame);
            case "more":
                left = visitNode(node.descendants.get(0), frame);
                right = visitNode(node.descendants.get(1), frame);
                return performMore(node.descendants.get(0), node.descendants.get(1), left, right, frame);
            case "more or equal":
                left = visitNode(node.descendants.get(0), frame);
                right = visitNode(node.descendants.get(1), frame);
                return performMoreEqual(node.descendants.get(0), node.descendants.get(1), left, right, frame);
            case "equal":
                left = visitNode(node.descendants.get(0), frame);
                right = visitNode(node.descendants.get(1), frame);
                return performEqual(node.descendants.get(0), node.descendants.get(1), left, right, frame);
            case "less":
                left = visitNode(node.descendants.get(0), frame);
                right = visitNode(node.descendants.get(1), frame);
                return performLess(node.descendants.get(0), node.descendants.get(1), left, right, frame);
            case "less or equal":
                left = visitNode(node.descendants.get(0), frame);
                right = visitNode(node.descendants.get(1), frame);
                return performLessEqual(node.descendants.get(0), node.descendants.get(1), left, right, frame);
            case "not equal":
                left = visitNode(node.descendants.get(0), frame);
                right = visitNode(node.descendants.get(1), frame);
                return performNotEqual(node.descendants.get(0), node.descendants.get(1), left, right, frame);
            case "or":
                left = visitNode(node.descendants.get(0), frame);
                right = visitNode(node.descendants.get(1), frame);
                return performOr(node.descendants.get(0), node.descendants.get(1), left, right, frame);
            case "and":
                left = visitNode(node.descendants.get(0), frame);
                right = visitNode(node.descendants.get(1), frame);
                return performAnd(node.descendants.get(0), node.descendants.get(1), left, right, frame);
            case "xor":
                left = visitNode(node.descendants.get(0), frame);
                right = visitNode(node.descendants.get(1), frame);
                return performXor(node.descendants.get(0), node.descendants.get(1), left, right, frame);
            case "assignment":
                left = visitNode(node.descendants.get(0), frame);
                right = visitNode(node.descendants.get(1), frame);
                frame.table.addVariableEntry(node.descendants.get(0).identifier, checkType(right));
                return performAssign(node.descendants.get(0), node.descendants.get(1), left, right, frame);
            case "body":
                for (Node child:
                        node.descendants) {
                    visitNode(child, frame);
                }
                return "";
            case "if":
                visitNode(node.descendants.get(0), frame);
                if (node.descendants.size() == 2) {
                    offset.add(frame.offset);
                    code.add("\t\t" + frame.offset + ": ifgt " + frame.offset + JVMInstruction.instructionOffset("goto") + JVMInstruction.instructionOffset("if"));
                    frame.offset += JVMInstruction.instructionOffset("goto") + JVMInstruction.instructionOffset("if");
                    index.add(code.size());
                    visitNode(node.descendants.get(1), frame);
                    code.add(index.get(0), "\t\t" + offset.get(0) + ": goto " + frame.offset);
                } else {
                    offset.add(frame.offset);
                    index.add(code.size());
                    frame.offset += JVMInstruction.instructionOffset("if");
                    visitNode(node.descendants.get(2), frame);
                    code.add(index.get(0), "\t\t" + offset.get(0) + ": ifgt " + frame.offset);
                    offset.add(frame.offset);
                    index.add(code.size());
                    frame.offset += JVMInstruction.instructionOffset("goto");
                    visitNode(node.descendants.get(1), frame);
                    code.add(index.get(1), "\t\t" + offset.get(1) + ": goto " + frame.offset);
                }
                return "";
            case "while":
                offset.add(frame.offset);
                visitNode(node.descendants.get(0), frame);
                index.add(this.code.size());
                offset.add(frame.offset);
                frame.offset += JVMInstruction.instructionOffset("if");
                visitNode(node.descendants.get(1), frame);
                code.add("\t\t" + frame.offset + ": goto " + offset.get(0));
                frame.offset += JVMInstruction.instructionOffset("goto");
                code.add(index.get(0), "\t\t" + offset.get(1) + ": ifeq " + frame.offset);
                return "";
            case "for":
                Node range = node.descendants.get(1);
                start = visitNode(range.descendants.get(0), frame);
                identifier = visitNode(node.descendants.get(0), frame);
                performAssign(node.descendants.get(0), range.descendants.get(0), identifier, start, frame);
                offset.add(frame.offset);
                if (Objects.equals(node.descendants.get(1).identifier, "range")) {
                    performLessEqual(node.descendants.get(0), range.descendants.get(1), identifier, visitNode(range.descendants.get(1), frame), frame);
                } else {
                    performMoreEqual(node.descendants.get(0), range.descendants.get(1), identifier, visitNode(range.descendants.get(1), frame), frame);
                }
                int value = frame.offset + JVMInstruction.instructionOffset("if") + JVMInstruction.instructionOffset("goto");
                code.add("\t\t" + frame.offset + ": ifgt " + value);
                frame.offset += JVMInstruction.instructionOffset("if");
                index.add(code.size());
                offset.add(frame.offset);
                frame.offset += JVMInstruction.instructionOffset("goto");
                visitNode(node.descendants.get(2), frame);
                if (Objects.equals(node.descendants.get(1).identifier, "range")) {
                    code.addAll(frame.incrementPositive(node.descendants.get(0).identifier));
                } else {
                    code.addAll(frame.incrementNegative(node.descendants.get(0).identifier));
                }
                code.add("\t\t" + frame.offset + ": goto " + offset.get(0));
                code.add(index.get(0), "\t\t" + offset.get(1) + ": goto " + frame.offset);
                frame.offset += JVMInstruction.instructionOffset("goto");
                return "";
            case "variable-declaration":
                if (Objects.equals(node.descendants.get(1).identifier, "type-integer")) {
                    if (node.descendants.size() == 2) {
                        frame.table.addVariableEntry(node.descendants.get(0).identifier, checkType(node.descendants.get(1).identifier));
                        frame.variableDeclaration(node.descendants.get(0).identifier);
                        return "";
                    } else {
                        frame.table.addVariableEntry(node.descendants.get(0).identifier, checkType(node.descendants.get(1).identifier));
                        left = visitNode(node.descendants.get(0), frame);
                        right = visitNode(node.descendants.get(2), frame);
                        return performAssign(node.descendants.get(0), node.descendants.get(1), left, right, frame);
                    }
                } else if (Objects.equals(node.descendants.get(1).identifier, "type-real")) {
                    if (node.descendants.size() == 2) {
                        frame.table.addVariableEntry(node.descendants.get(0).identifier, checkType(node.descendants.get(1).identifier));
                        frame.variableDeclaration(node.descendants.get(0).identifier);
                        return "";
                    } else {
                        frame.table.addVariableEntry(node.descendants.get(0).identifier, checkType(node.descendants.get(1).identifier));
                        left = visitNode(node.descendants.get(0), frame);
                        right = visitNode(node.descendants.get(2), frame);
                        return performAssign(node.descendants.get(0), node.descendants.get(1), left, right, frame);
                    }
                } else if (Objects.equals(node.descendants.get(1).identifier, "type-boolean")) {
                    if (node.descendants.size() == 2) {
                        frame.table.addVariableEntry(node.descendants.get(0).identifier, checkType(node.descendants.get(1).identifier));
                        frame.variableDeclaration(node.descendants.get(0).identifier);
                        return "";
                    } else {
                        frame.table.addVariableEntry(node.descendants.get(0).identifier, checkType(node.descendants.get(1).identifier));
                        left = visitNode(node.descendants.get(0), frame);
                        right = visitNode(node.descendants.get(2), frame);
                        return performAssign(node.descendants.get(0), node.descendants.get(1), left, right, frame);
                    }
                } else {
                    left = visitNode(node.descendants.get(0), frame);
                    right = visitNode(node.descendants.get(1), frame);
                    frame.table.addVariableEntry(node.descendants.get(0).identifier, checkType(right));
                    return performAssign(node.descendants.get(0), node.descendants.get(1), left, right, frame);
                }
            default:
                if (Objects.equals(frame.table.variableEntries.get(node.identifier), "boolean")) {
                    return "boolId";
                } else if (Objects.equals(frame.table.variableEntries.get(node.identifier), "integer")) {
                    return "intId";
                } else if (Objects.equals(frame.table.variableEntries.get(node.identifier), "real")) {
                    return "realId";
                }
                return node.identifier;
        }
    }

    String checkType (String ret) {
        if (Objects.equals(ret, "boolId") || Objects.equals(ret, "boolStack") || Objects.equals(ret, "boolValue") || Objects.equals(ret, "type-boolean")) {
            return "boolean";
        } else if (Objects.equals(ret, "intId") || Objects.equals(ret, "intValue") || Objects.equals(ret, "intStack") || Objects.equals(ret, "type-integer")) {
            return "integer";
        } else if (Objects.equals(ret, "realId") || Objects.equals(ret, "realValue") || Objects.equals(ret, "realStack") || Objects.equals(ret, "type-real")) {
            return "real";
        }
        return "Error";
    }

    String performOr (Node leftNode, Node rightNode, String left, String right, JVMFrame frame) {
        if (Objects.equals(left, "boolId")) {
            if (Objects.equals(right, "boolId")) {
                this.code.addAll(frame.orId(leftNode.identifier, rightNode.identifier));
            } else if (Objects.equals(right, "boolValue")) {
                this.code.addAll(frame.orValueId(leftNode.identifier, rightNode.boolValue));
            } else if (Objects.equals(right, "boolStack")) {
                this.code.addAll(frame.orStackId(leftNode.identifier));
            }
            return "boolStack";
        } else if (Objects.equals(left, "boolValue")) {
            if (Objects.equals(right, "boolId")) {
                this.code.addAll(frame.orValueId(rightNode.identifier, leftNode.boolValue));
            } else if (Objects.equals(right, "boolValue")) {
                this.code.addAll(frame.orValue(leftNode.boolValue, rightNode.boolValue));
            } else if (Objects.equals(right, "boolStack")) {
                this.code.addAll(frame.orValueStack(leftNode.boolValue));
            }
            return "boolStack";
        } else if (Objects.equals(left, "boolStack")) {
            if (Objects.equals(right, "boolId")) {
                this.code.addAll(frame.orStackId(rightNode.identifier));
            } else if (Objects.equals(right, "boolValue")) {
                this.code.addAll(frame.orValueStack(rightNode.boolValue));
            } else if (Objects.equals(right, "boolStack")) {
                this.code.addAll(frame.orStack());
            }
            return "boolStack";
        }
        return "Error";
    }

    String performAnd (Node leftNode, Node rightNode, String left, String right, JVMFrame frame) {
        if (Objects.equals(left, "boolId")) {
            if (Objects.equals(right, "boolId")) {
                this.code.addAll(frame.andId(leftNode.identifier, rightNode.identifier));
            } else if (Objects.equals(right, "boolValue")) {
                this.code.addAll(frame.andValueId(leftNode.identifier, rightNode.boolValue));
            } else if (Objects.equals(right, "boolStack")) {
                this.code.addAll(frame.andStackId(leftNode.identifier));
            }
            return "boolStack";
        } else if (Objects.equals(left, "boolValue")) {
            if (Objects.equals(right, "boolId")) {
                this.code.addAll(frame.andValueId(rightNode.identifier, leftNode.boolValue));
            } else if (Objects.equals(right, "boolValue")) {
                this.code.addAll(frame.andValue(leftNode.boolValue, rightNode.boolValue));
            } else if (Objects.equals(right, "boolStack")) {
                this.code.addAll(frame.andValueStack(leftNode.boolValue));
            }
            return "boolStack";
        } else if (Objects.equals(left, "boolStack")) {
            if (Objects.equals(right, "boolId")) {
                this.code.addAll(frame.andStackId(rightNode.identifier));
            } else if (Objects.equals(right, "boolValue")) {
                this.code.addAll(frame.andValueStack(rightNode.boolValue));
            } else if (Objects.equals(right, "boolStack")) {
                this.code.addAll(frame.andStack());
            }
            return "boolStack";
        }
        return "Error";
    }

    String performXor (Node leftNode, Node rightNode, String left, String right, JVMFrame frame) {
        if (Objects.equals(left, "boolId")) {
            if (Objects.equals(right, "boolId")) {
                this.code.addAll(frame.xorId(leftNode.identifier, rightNode.identifier));
            } else if (Objects.equals(right, "boolValue")) {
                this.code.addAll(frame.xorValueId(leftNode.identifier, rightNode.boolValue));
            } else if (Objects.equals(right, "boolStack")) {
                this.code.addAll(frame.xorStackId(leftNode.identifier));
            }
            return "boolStack";
        } else if (Objects.equals(left, "boolValue")) {
            if (Objects.equals(right, "boolId")) {
                this.code.addAll(frame.xorValueId(rightNode.identifier, leftNode.boolValue));
            } else if (Objects.equals(right, "boolValue")) {
                this.code.addAll(frame.xorValue(leftNode.boolValue, rightNode.boolValue));
            } else if (Objects.equals(right, "boolStack")) {
                this.code.addAll(frame.xorValueStack(leftNode.boolValue));
            }
            return "boolStack";
        } else if (Objects.equals(left, "boolStack")) {
            if (Objects.equals(right, "boolId")) {
                this.code.addAll(frame.xorStackId(rightNode.identifier));
            } else if (Objects.equals(right, "boolValue")) {
                this.code.addAll(frame.xorValueStack(rightNode.boolValue));
            } else if (Objects.equals(right, "boolStack")) {
                this.code.addAll(frame.xorStack());
            }
            return "boolStack";
        }
        return "Error";
    }

    String performAssign (Node leftNode, Node rightNode, String left, String right, JVMFrame frame) {
        if (Objects.equals(frame.table.checkVariableType(leftNode.identifier), "boolean")) {
            if (Objects.equals(right, "boolId")) {
                this.code.addAll(frame.assignBoolId(leftNode.identifier, rightNode.identifier));
            } else if (Objects.equals(right, "boolValue")) {
                this.code.addAll(frame.assignBoolValue(leftNode.identifier, rightNode.boolValue));
            } else if (Objects.equals(right, "boolStack")) {
                this.code.addAll(frame.assignBoolStack(leftNode.identifier));
            }
            return "";
        } else if (Objects.equals(frame.table.checkVariableType(leftNode.identifier), "integer")) {
            if (Objects.equals(right, "intId")) {
                this.code.addAll(frame.assignIntId(leftNode.identifier, rightNode.identifier));
            } else if (Objects.equals(right, "intValue")) {
                this.code.addAll(frame.assignIntValue(rightNode.intValue, leftNode.identifier));
            } else if (Objects.equals(right, "intStack")) {
                this.code.addAll(frame.assignIntStack(leftNode.identifier));
            }
            return "";
        } else if (Objects.equals(frame.table.checkVariableType(leftNode.identifier), "real")) {
            if (Objects.equals(right, "realId")) {
                this.code.addAll(frame.assignRealId(leftNode.identifier, rightNode.identifier));
            } else if (Objects.equals(right, "realValue")) {
                this.code.addAll(frame.assignRealValue(rightNode.realValue, leftNode.identifier));
            } else if (Objects.equals(right, "realStack")) {
                this.code.addAll(frame.assignRealStack(leftNode.identifier));
            }
            return "";
        }
        return "Error";
    }

    String performMore (Node leftNode, Node rightNode, String left, String right, JVMFrame frame) {
        if (Objects.equals(left, "intId")) {
            if (Objects.equals(right, "intId")) {
                this.code.addAll(frame.moreIntId(leftNode.identifier, rightNode.identifier));
            } else if (Objects.equals(right, "intValue")) {
                this.code.addAll(frame.moreIntIdValue(leftNode.identifier, rightNode.intValue, true));
            } else if (Objects.equals(right, "intStack")) {
                this.code.addAll(frame.moreIntIdStack(leftNode.identifier, true));
            }
            return "boolStack";
        } else if (Objects.equals(left, "intValue")) {
            if (Objects.equals(right, "intId")) {
                this.code.addAll(frame.moreIntIdValue(rightNode.identifier, leftNode.intValue, false));
            } else if (Objects.equals(right, "intValue")) {
                this.code.addAll(frame.moreIntValue(leftNode.intValue, rightNode.intValue));
            } else if (Objects.equals(right, "intStack")) {
                this.code.addAll(frame.moreIntValueStack(leftNode.intValue, false));
            }
            return "boolStack";
        } else if (Objects.equals(left, "intStack")) {
            if (Objects.equals(right, "intId")) {
                this.code.addAll(frame.moreIntIdStack(rightNode.identifier, false));
            } else if (Objects.equals(right, "intValue")) {
                this.code.addAll(frame.moreIntValueStack(rightNode.intValue, true));
            } else if (Objects.equals(right, "intStack")) {
                this.code.addAll(frame.moreIntStack());
            }
            return "boolStack";
        } else if (Objects.equals(left, "realId")) {
            if (Objects.equals(right, "realId")) {
                this.code.addAll(frame.moreRealId(leftNode.identifier, rightNode.identifier));
            } else if (Objects.equals(right, "realValue")) {
                this.code.addAll(frame.moreRealIdValue(leftNode.identifier, rightNode.intValue, true));
            } else if (Objects.equals(right, "realStack")) {
                this.code.addAll(frame.moreRealIdStack(leftNode.identifier, true));
            }
            return "boolStack";
        } else if (Objects.equals(left, "realValue")) {
            if (Objects.equals(right, "realId")) {
                this.code.addAll(frame.moreRealIdValue(rightNode.identifier, leftNode.intValue, false));
            } else if (Objects.equals(right, "realValue")) {
                this.code.addAll(frame.moreRealValue(leftNode.intValue, rightNode.intValue));
            } else if (Objects.equals(right, "realStack")) {
                this.code.addAll(frame.moreRealStackValue(leftNode.intValue, false));
            }
            return "boolStack";
        } else if (Objects.equals(left, "realStack")) {
            if (Objects.equals(right, "realId")) {
                this.code.addAll(frame.moreRealIdStack(rightNode.identifier, false));
            } else if (Objects.equals(right, "realValue")) {
                this.code.addAll(frame.moreRealStackValue(rightNode.intValue, true));
            } else if (Objects.equals(right, "realStack")) {
                this.code.addAll(frame.moreRealStack());
            }
            return "boolStack";
        }
        return "TypeError";
    }

    String performMoreEqual (Node leftNode, Node rightNode, String left, String right, JVMFrame frame) {
        if (Objects.equals(left, "intId")) {
            if (Objects.equals(right, "intId")) {
                this.code.addAll(frame.moreEqualIntId(leftNode.identifier, rightNode.identifier));
            } else if (Objects.equals(right, "intValue")) {
                this.code.addAll(frame.moreEqualIntIdValue(leftNode.identifier, rightNode.intValue, true));
            } else if (Objects.equals(right, "intStack")) {
                this.code.addAll(frame.moreEqualIntIdStack(leftNode.identifier, true));
            }
            return "boolStack";
        } else if (Objects.equals(left, "intValue")) {
            if (Objects.equals(right, "intId")) {
                this.code.addAll(frame.moreEqualIntIdValue(rightNode.identifier, leftNode.intValue, false));
            } else if (Objects.equals(right, "intValue")) {
                this.code.addAll(frame.moreEqualIntValue(leftNode.intValue, rightNode.intValue));
            } else if (Objects.equals(right, "intStack")) {
                this.code.addAll(frame.moreEqualIntValueStack(leftNode.intValue, false));
            }
            return "boolStack";
        } else if (Objects.equals(left, "intStack")) {
            if (Objects.equals(right, "intId")) {
                this.code.addAll(frame.moreEqualIntIdStack(rightNode.identifier, false));
            } else if (Objects.equals(right, "intValue")) {
                this.code.addAll(frame.moreEqualIntValueStack(rightNode.intValue, true));
            } else if (Objects.equals(right, "intStack")) {
                this.code.addAll(frame.moreEqualIntStack());
            }
            return "boolStack";
        } else if (Objects.equals(left, "realId")) {
            if (Objects.equals(right, "realId")) {
                this.code.addAll(frame.moreEqualRealId(leftNode.identifier, rightNode.identifier));
            } else if (Objects.equals(right, "realValue")) {
                this.code.addAll(frame.moreEqualRealIdValue(leftNode.identifier, rightNode.intValue, true));
            } else if (Objects.equals(right, "realStack")) {
                this.code.addAll(frame.moreEqualRealIdStack(leftNode.identifier, true));
            }
            return "boolStack";
        } else if (Objects.equals(left, "realValue")) {
            if (Objects.equals(right, "realId")) {
                this.code.addAll(frame.moreEqualRealIdValue(rightNode.identifier, leftNode.intValue, false));
            } else if (Objects.equals(right, "realValue")) {
                this.code.addAll(frame.moreEqualRealValue(leftNode.intValue, rightNode.intValue));
            } else if (Objects.equals(right, "realStack")) {
                this.code.addAll(frame.moreEqualRealStackValue(leftNode.intValue, false));
            }
            return "boolStack";
        } else if (Objects.equals(left, "realStack")) {
            if (Objects.equals(right, "realId")) {
                this.code.addAll(frame.moreEqualRealIdStack(rightNode.identifier, false));
            } else if (Objects.equals(right, "realValue")) {
                this.code.addAll(frame.moreEqualRealStackValue(rightNode.intValue, true));
            } else if (Objects.equals(right, "realStack")) {
                this.code.addAll(frame.moreEqualRealStack());
            }
            return "boolStack";
        }
        return "TypeError";
    }

    String performEqual (Node leftNode, Node rightNode, String left, String right, JVMFrame frame) {
        if (Objects.equals(left, "intId")) {
            if (Objects.equals(right, "intId")) {
                this.code.addAll(frame.equalIntId(leftNode.identifier, rightNode.identifier));
            } else if (Objects.equals(right, "intValue")) {
                this.code.addAll(frame.equalIntIdValue(leftNode.identifier, rightNode.intValue, true));
            } else if (Objects.equals(right, "intStack")) {
                this.code.addAll(frame.equalIntIdStack(leftNode.identifier, true));
            }
            return "boolStack";
        } else if (Objects.equals(left, "intValue")) {
            if (Objects.equals(right, "intId")) {
                this.code.addAll(frame.equalIntIdValue(rightNode.identifier, leftNode.intValue, false));
            } else if (Objects.equals(right, "intValue")) {
                this.code.addAll(frame.equalIntValue(leftNode.intValue, rightNode.intValue));
            } else if (Objects.equals(right, "intStack")) {
                this.code.addAll(frame.equalIntValueStack(leftNode.intValue, false));
            }
            return "boolStack";
        } else if (Objects.equals(left, "intStack")) {
            if (Objects.equals(right, "intId")) {
                this.code.addAll(frame.equalIntIdStack(rightNode.identifier, false));
            } else if (Objects.equals(right, "intValue")) {
                this.code.addAll(frame.equalIntValueStack(rightNode.intValue, true));
            } else if (Objects.equals(right, "intStack")) {
                this.code.addAll(frame.equalIntStack());
            }
            return "boolStack";
        } else if (Objects.equals(left, "realId")) {
            if (Objects.equals(right, "realId")) {
                this.code.addAll(frame.equalRealId(leftNode.identifier, rightNode.identifier));
            } else if (Objects.equals(right, "realValue")) {
                this.code.addAll(frame.equalRealIdValue(leftNode.identifier, rightNode.intValue, true));
            } else if (Objects.equals(right, "realStack")) {
                this.code.addAll(frame.equalRealIdStack(leftNode.identifier, true));
            }
            return "boolStack";
        } else if (Objects.equals(left, "realValue")) {
            if (Objects.equals(right, "realId")) {
                this.code.addAll(frame.equalRealIdValue(rightNode.identifier, leftNode.intValue, false));
            } else if (Objects.equals(right, "realValue")) {
                this.code.addAll(frame.equalRealValue(leftNode.intValue, rightNode.intValue));
            } else if (Objects.equals(right, "realStack")) {
                this.code.addAll(frame.equalRealStackValue(leftNode.intValue, false));
            }
            return "boolStack";
        } else if (Objects.equals(left, "realStack")) {
            if (Objects.equals(right, "realId")) {
                this.code.addAll(frame.equalRealIdStack(rightNode.identifier, false));
            } else if (Objects.equals(right, "realValue")) {
                this.code.addAll(frame.equalRealStackValue(rightNode.intValue, true));
            } else if (Objects.equals(right, "realStack")) {
                this.code.addAll(frame.equalRealStack());
            }
            return "boolStack";
        }
        return "TypeError";
    }

    String performLess (Node leftNode, Node rightNode, String left, String right, JVMFrame frame) {
        if (Objects.equals(left, "intId")) {
            if (Objects.equals(right, "intId")) {
                this.code.addAll(frame.lessIntId(leftNode.identifier, rightNode.identifier));
            } else if (Objects.equals(right, "intValue")) {
                this.code.addAll(frame.lessIntIdValue(leftNode.identifier, rightNode.intValue, true));
            } else if (Objects.equals(right, "intStack")) {
                this.code.addAll(frame.lessIntIdStack(leftNode.identifier, true));
            }
            return "boolStack";
        } else if (Objects.equals(left, "intValue")) {
            if (Objects.equals(right, "intId")) {
                this.code.addAll(frame.lessIntIdValue(rightNode.identifier, leftNode.intValue, false));
            } else if (Objects.equals(right, "intValue")) {
                this.code.addAll(frame.lessIntValue(leftNode.intValue, rightNode.intValue));
            } else if (Objects.equals(right, "intStack")) {
                this.code.addAll(frame.lessIntValueStack(leftNode.intValue, false));
            }
            return "boolStack";
        } else if (Objects.equals(left, "intStack")) {
            if (Objects.equals(right, "intId")) {
                this.code.addAll(frame.lessIntIdStack(rightNode.identifier, false));
            } else if (Objects.equals(right, "intValue")) {
                this.code.addAll(frame.lessIntValueStack(rightNode.intValue, true));
            } else if (Objects.equals(right, "intStack")) {
                this.code.addAll(frame.lessIntStack());
            }
            return "boolStack";
        } else if (Objects.equals(left, "realId")) {
            if (Objects.equals(right, "realId")) {
                this.code.addAll(frame.lessRealId(leftNode.identifier, rightNode.identifier));
            } else if (Objects.equals(right, "realValue")) {
                this.code.addAll(frame.lessRealIdValue(leftNode.identifier, rightNode.intValue, true));
            } else if (Objects.equals(right, "realStack")) {
                this.code.addAll(frame.lessRealIdStack(leftNode.identifier, true));
            }
            return "boolStack";
        } else if (Objects.equals(left, "realValue")) {
            if (Objects.equals(right, "realId")) {
                this.code.addAll(frame.lessRealIdValue(rightNode.identifier, leftNode.intValue, false));
            } else if (Objects.equals(right, "realValue")) {
                this.code.addAll(frame.lessRealValue(leftNode.intValue, rightNode.intValue));
            } else if (Objects.equals(right, "realStack")) {
                this.code.addAll(frame.lessRealStackValue(leftNode.intValue, false));
            }
            return "boolStack";
        } else if (Objects.equals(left, "realStack")) {
            if (Objects.equals(right, "realId")) {
                this.code.addAll(frame.lessRealIdStack(rightNode.identifier, false));
            } else if (Objects.equals(right, "realValue")) {
                this.code.addAll(frame.lessRealStackValue(rightNode.intValue, true));
            } else if (Objects.equals(right, "realStack")) {
                this.code.addAll(frame.lessRealStack());
            }
            return "boolStack";
        }
        return "TypeError";
    }

    String performLessEqual (Node leftNode, Node rightNode, String left, String right, JVMFrame frame) {
        if (Objects.equals(left, "intId")) {
            if (Objects.equals(right, "intId")) {
                this.code.addAll(frame.lessEqualIntId(leftNode.identifier, rightNode.identifier));
            } else if (Objects.equals(right, "intValue")) {
                this.code.addAll(frame.lessEqualIntIdValue(leftNode.identifier, rightNode.intValue, true));
            } else if (Objects.equals(right, "intStack")) {
                this.code.addAll(frame.lessEqualIntIdStack(leftNode.identifier, true));
            }
            return "boolStack";
        } else if (Objects.equals(left, "intValue")) {
            if (Objects.equals(right, "intId")) {
                this.code.addAll(frame.lessEqualIntIdValue(rightNode.identifier, leftNode.intValue, false));
            } else if (Objects.equals(right, "intValue")) {
                this.code.addAll(frame.lessEqualIntValue(leftNode.intValue, rightNode.intValue));
            } else if (Objects.equals(right, "intStack")) {
                this.code.addAll(frame.lessEqualIntValueStack(leftNode.intValue, false));
            }
            return "boolStack";
        } else if (Objects.equals(left, "intStack")) {
            if (Objects.equals(right, "intId")) {
                this.code.addAll(frame.lessEqualIntIdStack(rightNode.identifier, false));
            } else if (Objects.equals(right, "intValue")) {
                this.code.addAll(frame.lessEqualIntValueStack(rightNode.intValue, true));
            } else if (Objects.equals(right, "intStack")) {
                this.code.addAll(frame.lessEqualIntStack());
            }
            return "boolStack";
        } else if (Objects.equals(left, "realId")) {
            if (Objects.equals(right, "realId")) {
                this.code.addAll(frame.lessEqualRealId(leftNode.identifier, rightNode.identifier));
            } else if (Objects.equals(right, "realValue")) {
                this.code.addAll(frame.lessEqualRealIdValue(leftNode.identifier, rightNode.intValue, true));
            } else if (Objects.equals(right, "realStack")) {
                this.code.addAll(frame.lessEqualRealIdStack(leftNode.identifier, true));
            }
            return "boolStack";
        } else if (Objects.equals(left, "realValue")) {
            if (Objects.equals(right, "realId")) {
                this.code.addAll(frame.lessEqualRealIdValue(rightNode.identifier, leftNode.intValue, false));
            } else if (Objects.equals(right, "realValue")) {
                this.code.addAll(frame.lessEqualRealValue(leftNode.intValue, rightNode.intValue));
            } else if (Objects.equals(right, "realStack")) {
                this.code.addAll(frame.lessEqualRealStackValue(leftNode.intValue, false));
            }
            return "boolStack";
        } else if (Objects.equals(left, "realStack")) {
            if (Objects.equals(right, "realId")) {
                this.code.addAll(frame.lessEqualRealIdStack(rightNode.identifier, false));
            } else if (Objects.equals(right, "realValue")) {
                this.code.addAll(frame.lessEqualRealStackValue(rightNode.intValue, true));
            } else if (Objects.equals(right, "realStack")) {
                this.code.addAll(frame.lessEqualRealStack());
            }
            return "boolStack";
        }
        return "TypeError";
    }

    String performNotEqual (Node leftNode, Node rightNode, String left, String right, JVMFrame frame) {
        if (Objects.equals(left, "intId")) {
            if (Objects.equals(right, "intId")) {
                this.code.addAll(frame.notEqualIntId(leftNode.identifier, rightNode.identifier));
            } else if (Objects.equals(right, "intValue")) {
                this.code.addAll(frame.notEqualIntIdValue(leftNode.identifier, rightNode.intValue, true));
            } else if (Objects.equals(right, "intStack")) {
                this.code.addAll(frame.notEqualIntIdStack(leftNode.identifier, true));
            }
            return "boolStack";
        } else if (Objects.equals(left, "intValue")) {
            if (Objects.equals(right, "intId")) {
                this.code.addAll(frame.notEqualIntIdValue(rightNode.identifier, leftNode.intValue, false));
            } else if (Objects.equals(right, "intValue")) {
                this.code.addAll(frame.notEqualIntValue(leftNode.intValue, rightNode.intValue));
            } else if (Objects.equals(right, "intStack")) {
                this.code.addAll(frame.notEqualIntValueStack(leftNode.intValue, false));
            }
            return "boolStack";
        } else if (Objects.equals(left, "intStack")) {
            if (Objects.equals(right, "intId")) {
                this.code.addAll(frame.notEqualIntIdStack(rightNode.identifier, false));
            } else if (Objects.equals(right, "intValue")) {
                this.code.addAll(frame.notEqualIntValueStack(rightNode.intValue, true));
            } else if (Objects.equals(right, "intStack")) {
                this.code.addAll(frame.notEqualIntStack());
            }
            return "boolStack";
        } else if (Objects.equals(left, "realId")) {
            if (Objects.equals(right, "realId")) {
                this.code.addAll(frame.notEqualRealId(leftNode.identifier, rightNode.identifier));
            } else if (Objects.equals(right, "realValue")) {
                this.code.addAll(frame.notEqualRealIdValue(leftNode.identifier, rightNode.intValue, true));
            } else if (Objects.equals(right, "realStack")) {
                this.code.addAll(frame.notEqualRealIdStack(leftNode.identifier, true));
            }
            return "boolStack";
        } else if (Objects.equals(left, "realValue")) {
            if (Objects.equals(right, "realId")) {
                this.code.addAll(frame.notEqualRealIdValue(rightNode.identifier, leftNode.intValue, false));
            } else if (Objects.equals(right, "realValue")) {
                this.code.addAll(frame.notEqualRealValue(leftNode.intValue, rightNode.intValue));
            } else if (Objects.equals(right, "realStack")) {
                this.code.addAll(frame.notEqualRealStackValue(leftNode.intValue, false));
            }
            return "boolStack";
        } else if (Objects.equals(left, "realStack")) {
            if (Objects.equals(right, "realId")) {
                this.code.addAll(frame.notEqualRealIdStack(rightNode.identifier, false));
            } else if (Objects.equals(right, "realValue")) {
                this.code.addAll(frame.notEqualRealStackValue(rightNode.intValue, true));
            } else if (Objects.equals(right, "realStack")) {
                this.code.addAll(frame.notEqualRealStack());
            }
            return "boolStack";
        }
        return "TypeError";
    }

    String performAdd (Node leftNode, Node rightNode, String left, String right, JVMFrame frame) {
        if (Objects.equals(left, "intId")) {
            if (Objects.equals(right, "intId")) {
                this.code.addAll(frame.addIntId(leftNode.identifier, rightNode.identifier));
            } else if (Objects.equals(right, "intValue")) {
                this.code.addAll(frame.addIntIdValue(leftNode.identifier, rightNode.intValue));
            } else if (Objects.equals(right, "intStack")) {
                this.code.addAll(frame.addIntStackId(true, leftNode.identifier));
            }
            return "intStack";
        } else if (Objects.equals(left, "intValue")) {
            if (Objects.equals(right, "intId")) {
                this.code.addAll(frame.addIntIdValue(rightNode.identifier, leftNode.intValue));
            } else if (Objects.equals(right, "intValue")) {
                this.code.addAll(frame.addIntValue(leftNode.intValue, rightNode.intValue));
            } else if (Objects.equals(right, "intStack")) {
                this.code.addAll(frame.addIntStackValue(true, leftNode.intValue));
            }
            return "intStack";
        } else if (Objects.equals(left, "intStack")) {
            if (Objects.equals(right, "intId")) {
                this.code.addAll(frame.addIntStackId(false, rightNode.identifier));
            } else if (Objects.equals(right, "intValue")) {
                this.code.addAll(frame.addIntStackValue(false, rightNode.intValue));
            } else if (Objects.equals(right, "intStack")) {
                this.code.addAll(frame.addIntStack());
            }
            return "intStack";
        } else if (Objects.equals(left, "realId")) {
            if (Objects.equals(right, "realId")) {
                this.code.addAll(frame.addRealId(leftNode.identifier, rightNode.identifier));
            } else if (Objects.equals(right, "realValue")) {
                this.code.addAll(frame.addRealIdValue(leftNode.identifier, rightNode.realValue));
            } else if (Objects.equals(right, "realStack")) {
                this.code.addAll(frame.addRealStackId(true, leftNode.identifier));
            }
            return "realStack";
        } else if (Objects.equals(left, "realValue")) {
            if (Objects.equals(right, "realId")) {
                this.code.addAll(frame.addRealValueId(leftNode.realValue, rightNode.identifier));
            } else if (Objects.equals(right, "realValue")) {
                this.code.addAll(frame.addRealValue(leftNode.realValue, rightNode.realValue));
            } else if (Objects.equals(right, "realStack")) {
                this.code.addAll(frame.addRealStackValue(true, leftNode.realValue));
            }
            return "realStack";
        } else if (Objects.equals(left, "realStack")) {
            if (Objects.equals(right, "realId")) {
                this.code.addAll(frame.addRealStackId(false, rightNode.identifier));
            } else if (Objects.equals(right, "realValue")) {
                this.code.addAll(frame.addRealStackValue(false, rightNode.realValue));
            } else if (Objects.equals(right, "realStack")) {
                this.code.addAll(frame.addRealStack());
            }
            return "realStack";
        }
        return "Error";
    }

    String performSubtract (Node leftNode, Node rightNode, String left, String right, JVMFrame frame) {
        if (Objects.equals(left, "intId")) {
            if (Objects.equals(right, "intId")) {
                this.code.addAll(frame.subtractIntId(leftNode.identifier, rightNode.identifier));
            } else if (Objects.equals(right, "intValue")) {
                this.code.addAll(frame.subtractIntIdValue(leftNode.identifier, rightNode.intValue));
            } else if (Objects.equals(right, "intStack")) {
                this.code.addAll(frame.subtractIntStackId(true, leftNode.identifier));
            }
            return "intStack";
        } else if (Objects.equals(left, "intValue")) {
            if (Objects.equals(right, "intId")) {
                this.code.addAll(frame.subtractIntIdValue(rightNode.identifier, leftNode.intValue));
            } else if (Objects.equals(right, "intValue")) {
                this.code.addAll(frame.subtractIntValue(leftNode.intValue, rightNode.intValue));
            } else if (Objects.equals(right, "intStack")) {
                this.code.addAll(frame.subtractIntStackValue(true, leftNode.intValue));
            }
            return "intStack";
        } else if (Objects.equals(left, "intStack")) {
            if (Objects.equals(right, "intId")) {
                this.code.addAll(frame.subtractIntStackId(false, rightNode.identifier));
            } else if (Objects.equals(right, "intValue")) {
                this.code.addAll(frame.subtractIntStackValue(false, rightNode.intValue));
            } else if (Objects.equals(right, "intStack")) {
                this.code.addAll(frame.subtractIntStack());
            }
            return "intStack";
        } else if (Objects.equals(left, "realId")) {
            if (Objects.equals(right, "realId")) {
                this.code.addAll(frame.subtractRealId(leftNode.identifier, rightNode.identifier));
            } else if (Objects.equals(right, "realValue")) {
                this.code.addAll(frame.subtractRealIdValue(leftNode.identifier, rightNode.realValue));
            } else if (Objects.equals(right, "realStack")) {
                this.code.addAll(frame.subtractRealStackId(true, leftNode.identifier));
            }
            return "realStack";
        } else if (Objects.equals(left, "realValue")) {
            if (Objects.equals(right, "realId")) {
                this.code.addAll(frame.subtractRealValueId(leftNode.realValue, rightNode.identifier));
            } else if (Objects.equals(right, "realValue")) {
                this.code.addAll(frame.subtractRealValue(leftNode.realValue, rightNode.realValue));
            } else if (Objects.equals(right, "realStack")) {
                this.code.addAll(frame.subtractRealStackValue(true, leftNode.realValue));
            }
            return "realStack";
        } else if (Objects.equals(left, "realStack")) {
            if (Objects.equals(right, "realId")) {
                this.code.addAll(frame.subtractRealStackId(false, rightNode.identifier));
            } else if (Objects.equals(right, "realValue")) {
                this.code.addAll(frame.subtractRealStackValue(false, rightNode.realValue));
            } else if (Objects.equals(right, "realStack")) {
                this.code.addAll(frame.subtractRealStack());
            }
            return "realStack";
        }
        return "Error";
    }

    String performMultiply (Node leftNode, Node rightNode, String left, String right, JVMFrame frame) {
        if (Objects.equals(left, "intId")) {
            if (Objects.equals(right, "intId")) {
                this.code.addAll(frame.multiplyIntId(leftNode.identifier, rightNode.identifier));
            } else if (Objects.equals(right, "intValue")) {
                this.code.addAll(frame.multiplyIntIdValue(leftNode.identifier, rightNode.intValue));
            } else if (Objects.equals(right, "intStack")) {
                this.code.addAll(frame.multiplyIntStackId(true, leftNode.identifier));
            }
            return "intStack";
        } else if (Objects.equals(left, "intValue")) {
            if (Objects.equals(right, "intId")) {
                this.code.addAll(frame.multiplyIntIdValue(rightNode.identifier, leftNode.intValue));
            } else if (Objects.equals(right, "intValue")) {
                this.code.addAll(frame.multiplyIntValue(leftNode.intValue, rightNode.intValue));
            } else if (Objects.equals(right, "intStack")) {
                this.code.addAll(frame.multiplyIntStackValue(true, leftNode.intValue));
            }
            return "intStack";
        } else if (Objects.equals(left, "intStack")) {
            if (Objects.equals(right, "intId")) {
                this.code.addAll(frame.multiplyIntStackId(false, rightNode.identifier));
            } else if (Objects.equals(right, "intValue")) {
                this.code.addAll(frame.multiplyIntStackValue(false, rightNode.intValue));
            } else if (Objects.equals(right, "intStack")) {
                this.code.addAll(frame.multiplyIntStack());
            }
            return "intStack";
        } else if (Objects.equals(left, "realId")) {
            if (Objects.equals(right, "realId")) {
                this.code.addAll(frame.multiplyRealId(leftNode.identifier, rightNode.identifier));
            } else if (Objects.equals(right, "realValue")) {
                this.code.addAll(frame.multiplyRealIdValue(leftNode.identifier, rightNode.realValue));
            } else if (Objects.equals(right, "realStack")) {
                this.code.addAll(frame.multiplyRealStackId(true, leftNode.identifier));
            }
            return "realStack";
        } else if (Objects.equals(left, "realValue")) {
            if (Objects.equals(right, "realId")) {
                this.code.addAll(frame.multiplyRealValueId(leftNode.realValue, rightNode.identifier));
            } else if (Objects.equals(right, "realValue")) {
                this.code.addAll(frame.multiplyRealValue(leftNode.realValue, rightNode.realValue));
            } else if (Objects.equals(right, "realStack")) {
                this.code.addAll(frame.multiplyRealStackValue(true, leftNode.realValue));
            }
            return "realStack";
        } else if (Objects.equals(left, "realStack")) {
            if (Objects.equals(right, "realId")) {
                this.code.addAll(frame.multiplyRealStackId(false, rightNode.identifier));
            } else if (Objects.equals(right, "realValue")) {
                this.code.addAll(frame.multiplyRealStackValue(false, rightNode.realValue));
            } else if (Objects.equals(right, "realStack")) {
                this.code.addAll(frame.multiplyRealStack());
            }
            return "realStack";
        }
        return "Error";
    }

    String performDivision (Node leftNode, Node rightNode, String left, String right, JVMFrame frame) {
        if (Objects.equals(left, "intId")) {
            if (Objects.equals(right, "intId")) {
                this.code.addAll(frame.divisionIntId(leftNode.identifier, rightNode.identifier));
            } else if (Objects.equals(right, "intValue")) {
                this.code.addAll(frame.divisionIntIdValue(leftNode.identifier, rightNode.intValue));
            } else if (Objects.equals(right, "intStack")) {
                this.code.addAll(frame.divisionIntStackId(true, leftNode.identifier));
            }
            return "intStack";
        } else if (Objects.equals(left, "intValue")) {
            if (Objects.equals(right, "intId")) {
                this.code.addAll(frame.divisionIntIdValue(rightNode.identifier, leftNode.intValue));
            } else if (Objects.equals(right, "intValue")) {
                this.code.addAll(frame.divisionIntValue(leftNode.intValue, rightNode.intValue));
            } else if (Objects.equals(right, "intStack")) {
                this.code.addAll(frame.divisionIntStackValue(true, leftNode.intValue));
            }
            return "intStack";
        } else if (Objects.equals(left, "intStack")) {
            if (Objects.equals(right, "intId")) {
                this.code.addAll(frame.divisionIntStackId(false, rightNode.identifier));
            } else if (Objects.equals(right, "intValue")) {
                this.code.addAll(frame.divisionIntStackValue(false, rightNode.intValue));
            } else if (Objects.equals(right, "intStack")) {
                this.code.addAll(frame.divisionIntStack());
            }
            return "intStack";
        } else if (Objects.equals(left, "realId")) {
            if (Objects.equals(right, "realId")) {
                this.code.addAll(frame.divisionRealId(leftNode.identifier, rightNode.identifier));
            } else if (Objects.equals(right, "realValue")) {
                this.code.addAll(frame.divisionRealIdValue(leftNode.identifier, rightNode.realValue));
            } else if (Objects.equals(right, "realStack")) {
                this.code.addAll(frame.divisionRealStackId(true, leftNode.identifier));
            }
            return "realStack";
        } else if (Objects.equals(left, "realValue")) {
            if (Objects.equals(right, "realId")) {
                this.code.addAll(frame.divisionRealValueId(leftNode.realValue, rightNode.identifier));
            } else if (Objects.equals(right, "realValue")) {
                this.code.addAll(frame.divisionRealValue(leftNode.realValue, rightNode.realValue));
            } else if (Objects.equals(right, "realStack")) {
                this.code.addAll(frame.divisionRealStackValue(true, leftNode.realValue));
            }
            return "realStack";
        } else if (Objects.equals(left, "realStack")) {
            if (Objects.equals(right, "realId")) {
                this.code.addAll(frame.divisionRealStackId(false, rightNode.identifier));
            } else if (Objects.equals(right, "realValue")) {
                this.code.addAll(frame.divisionRealStackValue(false, rightNode.realValue));
            } else if (Objects.equals(right, "realStack")) {
                this.code.addAll(frame.divisionRealStack());
            }
            return "realStack";
        }
        return "Error";
    }
}
