package compiler.JVM;

import compiler.ASTNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class JVMCompiler {
    HashMap<Float, Integer> constantPool;
    public ArrayList<String> code;
    String name;

    public JVMCompiler(ArrayList<Float> constants, String name) {
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

    public void traverseTree (ASTNode root) {
        this.code.add("{");
        for (int i = 0; i < root.childs.size(); i++) {
            ASTNode routine = root.childs.get(i);
            if (routine.childs.size() == 3) {
                JVMFrame frame = new JVMFrame(this.constantPool, routine.childs.get(1).childs, routine.childs.get(0).op);
                code.addAll(frame.startFrame());
                visitNode (routine.childs.get(2), frame);
                code.addAll(frame.endFrame());
            } else {
                JVMFrame frame = new JVMFrame(this.constantPool, routine.childs.get(1).childs, routine.childs.get(2).op, routine.childs.get(0).op);
                code.addAll(frame.startFrame());
                visitNode(routine.childs.get(3), frame);
                code.addAll(frame.endFrame());
            }
        }
        this.code.remove(this.code.size() - 1);
        this.code.add("}");
    }

    String visitNode (ASTNode node, JVMFrame frame) {
        String left, right;
        switch (node.op) {
            case "+":
                left = visitNode(node.childs.get(0), frame);
                right = visitNode(node.childs.get(1), frame);
                return performAdd(node.childs.get(0), node.childs.get(1), left, right, frame);
            case "-":
                left = visitNode(node.childs.get(0), frame);
                right = visitNode(node.childs.get(1), frame);
                return performSubtract(node.childs.get(0), node.childs.get(1), left, right, frame);
            case "*":
                left = visitNode(node.childs.get(0), frame);
                right = visitNode(node.childs.get(1), frame);
                return performMultiply(node.childs.get(0), node.childs.get(1), left, right, frame);
            case "/":
                left = visitNode(node.childs.get(0), frame);
                right = visitNode(node.childs.get(1), frame);
                return performDivision(node.childs.get(0), node.childs.get(1), left, right, frame);
            case ">":
                left = visitNode(node.childs.get(0), frame);
                right = visitNode(node.childs.get(1), frame);
                return performMore(node.childs.get(0), node.childs.get(1), left, right, frame);
            case ">=":
                left = visitNode(node.childs.get(0), frame);
                right = visitNode(node.childs.get(1), frame);
                return performMoreEqual(node.childs.get(0), node.childs.get(1), left, right, frame);
            case "=":
                left = visitNode(node.childs.get(0), frame);
                right = visitNode(node.childs.get(1), frame);
                return performEqual(node.childs.get(0), node.childs.get(1), left, right, frame);
            case "<":
                left = visitNode(node.childs.get(0), frame);
                right = visitNode(node.childs.get(1), frame);
                return performLess(node.childs.get(0), node.childs.get(1), left, right, frame);
            case "<=":
                left = visitNode(node.childs.get(0), frame);
                right = visitNode(node.childs.get(1), frame);
                return performLessEqual(node.childs.get(0), node.childs.get(1), left, right, frame);
            case "/=":
                left = visitNode(node.childs.get(0), frame);
                right = visitNode(node.childs.get(1), frame);
                return performNotEqual(node.childs.get(0), node.childs.get(1), left, right, frame);
            case "or":
                left = visitNode(node.childs.get(0), frame);
                right = visitNode(node.childs.get(1), frame);
                return performOr(node.childs.get(0), node.childs.get(1), left, right, frame);
            case "and":
                left = visitNode(node.childs.get(0), frame);
                right = visitNode(node.childs.get(1), frame);
                return performAnd(node.childs.get(0), node.childs.get(1), left, right, frame);
            case "xor":
                left = visitNode(node.childs.get(0), frame);
                right = visitNode(node.childs.get(1), frame);
                return performXor(node.childs.get(0), node.childs.get(1), left, right, frame);
            case "assign":
                left = visitNode(node.childs.get(0), frame);
                right = visitNode(node.childs.get(1), frame);
                frame.table.addVariableEntry(node.childs.get(0).op, checkType(right));
                return performAssign(node.childs.get(0), node.childs.get(1), left, right, frame);
            case "body":
                for (ASTNode child:
                        node.childs) {
                    visitNode(child, frame);
                }
                return "";
            case "if":
                visitNode(node.childs.get(0), frame);
                ArrayList<Integer> offset = new ArrayList<>();
                ArrayList<Integer> index = new ArrayList<>();
                if (node.childs.size() == 2) {
                    offset.add(frame.offset);
                    code.add("\t\t" + frame.offset + ": ifgt " + frame.offset + JVMInstruction.instructionOffset("goto") + JVMInstruction.instructionOffset("if"));
                    frame.offset += JVMInstruction.instructionOffset("goto") + JVMInstruction.instructionOffset("if");
                    index.add(code.size());
                    visitNode(node.childs.get(1), frame);
                    code.add(index.get(0), "\t\t" + offset.get(0) + ": goto " + frame.offset);
                } else {
                    offset.add(frame.offset);
                    index.add(code.size());
                    frame.offset += JVMInstruction.instructionOffset("if");
                    visitNode(node.childs.get(2), frame);
                    code.add(index.get(0), "\t\t" + offset.get(0) + ": ifgt " + frame.offset);
                    offset.add(frame.offset);
                    index.add(code.size());
                    frame.offset += JVMInstruction.instructionOffset("goto");
                    visitNode(node.childs.get(1), frame);
                    code.add(index.get(1), "\t\t" + offset.get(1) + ": goto " + frame.offset);
                }
                return "";
            default:
                if (Objects.equals(frame.table.variableEntries.get(node.op), "boolean")) {
                    return "boolId";
                } else if (Objects.equals(frame.table.variableEntries.get(node.op), "int")) {
                    return "intId";
                } else if (Objects.equals(frame.table.variableEntries.get(node.op), "real")) {
                    return "realId";
                }
                return node.op;
        }
    }

    String checkType (String ret) {
        if (Objects.equals(ret, "boolId") || Objects.equals(ret, "boolStack") || Objects.equals(ret, "boolValue")) {
            return "boolean";
        } else if (Objects.equals(ret, "intId") || Objects.equals(ret, "intValue") || Objects.equals(ret, "intStack")) {
            return "int";
        } else if (Objects.equals(ret, "realId") || Objects.equals(ret, "realValue") || Objects.equals(ret, "realStack")) {
            return "real";
        }
        return "Error";
    }

    String performOr (ASTNode leftNode, ASTNode rightNode, String left, String right, JVMFrame frame) {
        if (Objects.equals(left, "boolId")) {
            if (Objects.equals(right, "boolId")) {
                this.code.addAll(frame.orId(leftNode.op, rightNode.op));
            } else if (Objects.equals(right, "boolValue")) {
                this.code.addAll(frame.orValueId(leftNode.op, rightNode.boolValue));
            } else if (Objects.equals(right, "boolStack")) {
                this.code.addAll(frame.orStackId(leftNode.op));
            }
            return "boolStack";
        } else if (Objects.equals(left, "boolValue")) {
            if (Objects.equals(right, "boolId")) {
                this.code.addAll(frame.orValueId(rightNode.op, leftNode.boolValue));
            } else if (Objects.equals(right, "boolValue")) {
                this.code.addAll(frame.orValue(leftNode.boolValue, rightNode.boolValue));
            } else if (Objects.equals(right, "boolStack")) {
                this.code.addAll(frame.orValueStack(leftNode.boolValue));
            }
            return "boolStack";
        } else if (Objects.equals(left, "boolStack")) {
            if (Objects.equals(right, "boolId")) {
                this.code.addAll(frame.orStackId(rightNode.op));
            } else if (Objects.equals(right, "boolValue")) {
                this.code.addAll(frame.orValueStack(rightNode.boolValue));
            } else if (Objects.equals(right, "boolStack")) {
                this.code.addAll(frame.orStack());
            }
            return "boolStack";
        }
        return "Error";
    }

    String performAnd (ASTNode leftNode, ASTNode rightNode, String left, String right, JVMFrame frame) {
        if (Objects.equals(left, "boolId")) {
            if (Objects.equals(right, "boolId")) {
                this.code.addAll(frame.andId(leftNode.op, rightNode.op));
            } else if (Objects.equals(right, "boolValue")) {
                this.code.addAll(frame.andValueId(leftNode.op, rightNode.boolValue));
            } else if (Objects.equals(right, "boolStack")) {
                this.code.addAll(frame.andStackId(leftNode.op));
            }
            return "boolStack";
        } else if (Objects.equals(left, "boolValue")) {
            if (Objects.equals(right, "boolId")) {
                this.code.addAll(frame.andValueId(rightNode.op, leftNode.boolValue));
            } else if (Objects.equals(right, "boolValue")) {
                this.code.addAll(frame.andValue(leftNode.boolValue, rightNode.boolValue));
            } else if (Objects.equals(right, "boolStack")) {
                this.code.addAll(frame.andValueStack(leftNode.boolValue));
            }
            return "boolStack";
        } else if (Objects.equals(left, "boolStack")) {
            if (Objects.equals(right, "boolId")) {
                this.code.addAll(frame.andStackId(rightNode.op));
            } else if (Objects.equals(right, "boolValue")) {
                this.code.addAll(frame.andValueStack(rightNode.boolValue));
            } else if (Objects.equals(right, "boolStack")) {
                this.code.addAll(frame.andStack());
            }
            return "boolStack";
        }
        return "Error";
    }

    String performXor (ASTNode leftNode, ASTNode rightNode, String left, String right, JVMFrame frame) {
        if (Objects.equals(left, "boolId")) {
            if (Objects.equals(right, "boolId")) {
                this.code.addAll(frame.xorId(leftNode.op, rightNode.op));
            } else if (Objects.equals(right, "boolValue")) {
                this.code.addAll(frame.xorValueId(leftNode.op, rightNode.boolValue));
            } else if (Objects.equals(right, "boolStack")) {
                this.code.addAll(frame.xorStackId(leftNode.op));
            }
            return "boolStack";
        } else if (Objects.equals(left, "boolValue")) {
            if (Objects.equals(right, "boolId")) {
                this.code.addAll(frame.xorValueId(rightNode.op, leftNode.boolValue));
            } else if (Objects.equals(right, "boolValue")) {
                this.code.addAll(frame.xorValue(leftNode.boolValue, rightNode.boolValue));
            } else if (Objects.equals(right, "boolStack")) {
                this.code.addAll(frame.xorValueStack(leftNode.boolValue));
            }
            return "boolStack";
        } else if (Objects.equals(left, "boolStack")) {
            if (Objects.equals(right, "boolId")) {
                this.code.addAll(frame.xorStackId(rightNode.op));
            } else if (Objects.equals(right, "boolValue")) {
                this.code.addAll(frame.xorValueStack(rightNode.boolValue));
            } else if (Objects.equals(right, "boolStack")) {
                this.code.addAll(frame.xorStack());
            }
            return "boolStack";
        }
        return "Error";
    }

    String performAssign (ASTNode leftNode, ASTNode rightNode, String left, String right, JVMFrame frame) {
        if (Objects.equals(frame.table.checkVariableType(leftNode.op), "boolean")) {
            if (Objects.equals(right, "boolId")) {
                this.code.addAll(frame.assignBoolId(leftNode.op, rightNode.op));
            } else if (Objects.equals(right, "boolValue")) {
                this.code.addAll(frame.assignBoolValue(leftNode.op, rightNode.boolValue));
            } else if (Objects.equals(right, "boolStack")) {
                this.code.addAll(frame.assignBoolStack(leftNode.op));
            }
            return "";
        } else if (Objects.equals(frame.table.checkVariableType(leftNode.op), "int")) {
            if (Objects.equals(right, "intId")) {
                this.code.addAll(frame.assignIntId(leftNode.op, rightNode.op));
            } else if (Objects.equals(right, "intValue")) {
                this.code.addAll(frame.assignIntValue(rightNode.intValue, leftNode.op));
            } else if (Objects.equals(right, "intStack")) {
                this.code.addAll(frame.assignIntStack(leftNode.op));
            }
            return "";
        } else if (Objects.equals(frame.table.checkVariableType(leftNode.op), "real")) {
            if (Objects.equals(right, "realId")) {
                this.code.addAll(frame.assignRealId(leftNode.op, rightNode.op));
            } else if (Objects.equals(right, "realValue")) {
                this.code.addAll(frame.assignRealValue(rightNode.realValue, leftNode.op));
            } else if (Objects.equals(right, "realStack")) {
                this.code.addAll(frame.assignRealStack(leftNode.op));
            }
            return "";
        }
        return "Error";
    }

    String performMore (ASTNode leftNode, ASTNode rightNode, String left, String right, JVMFrame frame) {
        if (Objects.equals(left, "intId")) {
            if (Objects.equals(right, "intId")) {
                this.code.addAll(frame.moreIntId(leftNode.op, rightNode.op));
            } else if (Objects.equals(right, "intValue")) {
                this.code.addAll(frame.moreIntIdValue(leftNode.op, rightNode.intValue, true));
            } else if (Objects.equals(right, "intStack")) {
                this.code.addAll(frame.moreIntIdStack(leftNode.op, true));
            }
            return "boolStack";
        } else if (Objects.equals(left, "intValue")) {
            if (Objects.equals(right, "intId")) {
                this.code.addAll(frame.moreIntIdValue(rightNode.op, leftNode.intValue, false));
            } else if (Objects.equals(right, "intValue")) {
                this.code.addAll(frame.moreIntValue(leftNode.intValue, rightNode.intValue));
            } else if (Objects.equals(right, "intStack")) {
                this.code.addAll(frame.moreIntValueStack(leftNode.intValue, false));
            }
            return "boolStack";
        } else if (Objects.equals(left, "intStack")) {
            if (Objects.equals(right, "intId")) {
                this.code.addAll(frame.moreIntIdStack(rightNode.op, false));
            } else if (Objects.equals(right, "intValue")) {
                this.code.addAll(frame.moreIntValueStack(rightNode.intValue, true));
            } else if (Objects.equals(right, "intStack")) {
                this.code.addAll(frame.moreIntStack());
            }
            return "boolStack";
        } else if (Objects.equals(left, "realId")) {
            if (Objects.equals(right, "realId")) {
                this.code.addAll(frame.moreRealId(leftNode.op, rightNode.op));
            } else if (Objects.equals(right, "realValue")) {
                this.code.addAll(frame.moreRealIdValue(leftNode.op, rightNode.intValue, true));
            } else if (Objects.equals(right, "realStack")) {
                this.code.addAll(frame.moreRealIdStack(leftNode.op, true));
            }
            return "boolStack";
        } else if (Objects.equals(left, "realValue")) {
            if (Objects.equals(right, "realId")) {
                this.code.addAll(frame.moreRealIdValue(rightNode.op, leftNode.intValue, false));
            } else if (Objects.equals(right, "realValue")) {
                this.code.addAll(frame.moreRealValue(leftNode.intValue, rightNode.intValue));
            } else if (Objects.equals(right, "realStack")) {
                this.code.addAll(frame.moreRealStackValue(leftNode.intValue, false));
            }
            return "boolStack";
        } else if (Objects.equals(left, "realStack")) {
            if (Objects.equals(right, "realId")) {
                this.code.addAll(frame.moreRealIdStack(rightNode.op, false));
            } else if (Objects.equals(right, "realValue")) {
                this.code.addAll(frame.moreRealStackValue(rightNode.intValue, true));
            } else if (Objects.equals(right, "realStack")) {
                this.code.addAll(frame.moreRealStack());
            }
            return "boolStack";
        }
        return "TypeError";
    }

    String performMoreEqual (ASTNode leftNode, ASTNode rightNode, String left, String right, JVMFrame frame) {
        if (Objects.equals(left, "intId")) {
            if (Objects.equals(right, "intId")) {
                this.code.addAll(frame.moreEqualIntId(leftNode.op, rightNode.op));
            } else if (Objects.equals(right, "intValue")) {
                this.code.addAll(frame.moreEqualIntIdValue(leftNode.op, rightNode.intValue, true));
            } else if (Objects.equals(right, "intStack")) {
                this.code.addAll(frame.moreEqualIntIdStack(leftNode.op, true));
            }
            return "boolStack";
        } else if (Objects.equals(left, "intValue")) {
            if (Objects.equals(right, "intId")) {
                this.code.addAll(frame.moreEqualIntIdValue(rightNode.op, leftNode.intValue, false));
            } else if (Objects.equals(right, "intValue")) {
                this.code.addAll(frame.moreEqualIntValue(leftNode.intValue, rightNode.intValue));
            } else if (Objects.equals(right, "intStack")) {
                this.code.addAll(frame.moreEqualIntValueStack(leftNode.intValue, false));
            }
            return "boolStack";
        } else if (Objects.equals(left, "intStack")) {
            if (Objects.equals(right, "intId")) {
                this.code.addAll(frame.moreEqualIntIdStack(rightNode.op, false));
            } else if (Objects.equals(right, "intValue")) {
                this.code.addAll(frame.moreEqualIntValueStack(rightNode.intValue, true));
            } else if (Objects.equals(right, "intStack")) {
                this.code.addAll(frame.moreEqualIntStack());
            }
            return "boolStack";
        } else if (Objects.equals(left, "realId")) {
            if (Objects.equals(right, "realId")) {
                this.code.addAll(frame.moreEqualRealId(leftNode.op, rightNode.op));
            } else if (Objects.equals(right, "realValue")) {
                this.code.addAll(frame.moreEqualRealIdValue(leftNode.op, rightNode.intValue, true));
            } else if (Objects.equals(right, "realStack")) {
                this.code.addAll(frame.moreEqualRealIdStack(leftNode.op, true));
            }
            return "boolStack";
        } else if (Objects.equals(left, "realValue")) {
            if (Objects.equals(right, "realId")) {
                this.code.addAll(frame.moreEqualRealIdValue(rightNode.op, leftNode.intValue, false));
            } else if (Objects.equals(right, "realValue")) {
                this.code.addAll(frame.moreEqualRealValue(leftNode.intValue, rightNode.intValue));
            } else if (Objects.equals(right, "realStack")) {
                this.code.addAll(frame.moreEqualRealStackValue(leftNode.intValue, false));
            }
            return "boolStack";
        } else if (Objects.equals(left, "realStack")) {
            if (Objects.equals(right, "realId")) {
                this.code.addAll(frame.moreEqualRealIdStack(rightNode.op, false));
            } else if (Objects.equals(right, "realValue")) {
                this.code.addAll(frame.moreEqualRealStackValue(rightNode.intValue, true));
            } else if (Objects.equals(right, "realStack")) {
                this.code.addAll(frame.moreEqualRealStack());
            }
            return "boolStack";
        }
        return "TypeError";
    }

    String performEqual (ASTNode leftNode, ASTNode rightNode, String left, String right, JVMFrame frame) {
        if (Objects.equals(left, "intId")) {
            if (Objects.equals(right, "intId")) {
                this.code.addAll(frame.equalIntId(leftNode.op, rightNode.op));
            } else if (Objects.equals(right, "intValue")) {
                this.code.addAll(frame.equalIntIdValue(leftNode.op, rightNode.intValue, true));
            } else if (Objects.equals(right, "intStack")) {
                this.code.addAll(frame.equalIntIdStack(leftNode.op, true));
            }
            return "boolStack";
        } else if (Objects.equals(left, "intValue")) {
            if (Objects.equals(right, "intId")) {
                this.code.addAll(frame.equalIntIdValue(rightNode.op, leftNode.intValue, false));
            } else if (Objects.equals(right, "intValue")) {
                this.code.addAll(frame.equalIntValue(leftNode.intValue, rightNode.intValue));
            } else if (Objects.equals(right, "intStack")) {
                this.code.addAll(frame.equalIntValueStack(leftNode.intValue, false));
            }
            return "boolStack";
        } else if (Objects.equals(left, "intStack")) {
            if (Objects.equals(right, "intId")) {
                this.code.addAll(frame.equalIntIdStack(rightNode.op, false));
            } else if (Objects.equals(right, "intValue")) {
                this.code.addAll(frame.equalIntValueStack(rightNode.intValue, true));
            } else if (Objects.equals(right, "intStack")) {
                this.code.addAll(frame.equalIntStack());
            }
            return "boolStack";
        } else if (Objects.equals(left, "realId")) {
            if (Objects.equals(right, "realId")) {
                this.code.addAll(frame.equalRealId(leftNode.op, rightNode.op));
            } else if (Objects.equals(right, "realValue")) {
                this.code.addAll(frame.equalRealIdValue(leftNode.op, rightNode.intValue, true));
            } else if (Objects.equals(right, "realStack")) {
                this.code.addAll(frame.equalRealIdStack(leftNode.op, true));
            }
            return "boolStack";
        } else if (Objects.equals(left, "realValue")) {
            if (Objects.equals(right, "realId")) {
                this.code.addAll(frame.equalRealIdValue(rightNode.op, leftNode.intValue, false));
            } else if (Objects.equals(right, "realValue")) {
                this.code.addAll(frame.equalRealValue(leftNode.intValue, rightNode.intValue));
            } else if (Objects.equals(right, "realStack")) {
                this.code.addAll(frame.equalRealStackValue(leftNode.intValue, false));
            }
            return "boolStack";
        } else if (Objects.equals(left, "realStack")) {
            if (Objects.equals(right, "realId")) {
                this.code.addAll(frame.equalRealIdStack(rightNode.op, false));
            } else if (Objects.equals(right, "realValue")) {
                this.code.addAll(frame.equalRealStackValue(rightNode.intValue, true));
            } else if (Objects.equals(right, "realStack")) {
                this.code.addAll(frame.equalRealStack());
            }
            return "boolStack";
        }
        return "TypeError";
    }

    String performLess (ASTNode leftNode, ASTNode rightNode, String left, String right, JVMFrame frame) {
        if (Objects.equals(left, "intId")) {
            if (Objects.equals(right, "intId")) {
                this.code.addAll(frame.lessIntId(leftNode.op, rightNode.op));
            } else if (Objects.equals(right, "intValue")) {
                this.code.addAll(frame.lessIntIdValue(leftNode.op, rightNode.intValue, true));
            } else if (Objects.equals(right, "intStack")) {
                this.code.addAll(frame.lessIntIdStack(leftNode.op, true));
            }
            return "boolStack";
        } else if (Objects.equals(left, "intValue")) {
            if (Objects.equals(right, "intId")) {
                this.code.addAll(frame.lessIntIdValue(rightNode.op, leftNode.intValue, false));
            } else if (Objects.equals(right, "intValue")) {
                this.code.addAll(frame.lessIntValue(leftNode.intValue, rightNode.intValue));
            } else if (Objects.equals(right, "intStack")) {
                this.code.addAll(frame.lessIntValueStack(leftNode.intValue, false));
            }
            return "boolStack";
        } else if (Objects.equals(left, "intStack")) {
            if (Objects.equals(right, "intId")) {
                this.code.addAll(frame.lessIntIdStack(rightNode.op, false));
            } else if (Objects.equals(right, "intValue")) {
                this.code.addAll(frame.lessIntValueStack(rightNode.intValue, true));
            } else if (Objects.equals(right, "intStack")) {
                this.code.addAll(frame.lessIntStack());
            }
            return "boolStack";
        } else if (Objects.equals(left, "realId")) {
            if (Objects.equals(right, "realId")) {
                this.code.addAll(frame.lessRealId(leftNode.op, rightNode.op));
            } else if (Objects.equals(right, "realValue")) {
                this.code.addAll(frame.lessRealIdValue(leftNode.op, rightNode.intValue, true));
            } else if (Objects.equals(right, "realStack")) {
                this.code.addAll(frame.lessRealIdStack(leftNode.op, true));
            }
            return "boolStack";
        } else if (Objects.equals(left, "realValue")) {
            if (Objects.equals(right, "realId")) {
                this.code.addAll(frame.lessRealIdValue(rightNode.op, leftNode.intValue, false));
            } else if (Objects.equals(right, "realValue")) {
                this.code.addAll(frame.lessRealValue(leftNode.intValue, rightNode.intValue));
            } else if (Objects.equals(right, "realStack")) {
                this.code.addAll(frame.lessRealStackValue(leftNode.intValue, false));
            }
            return "boolStack";
        } else if (Objects.equals(left, "realStack")) {
            if (Objects.equals(right, "realId")) {
                this.code.addAll(frame.lessRealIdStack(rightNode.op, false));
            } else if (Objects.equals(right, "realValue")) {
                this.code.addAll(frame.lessRealStackValue(rightNode.intValue, true));
            } else if (Objects.equals(right, "realStack")) {
                this.code.addAll(frame.lessRealStack());
            }
            return "boolStack";
        }
        return "TypeError";
    }

    String performLessEqual (ASTNode leftNode, ASTNode rightNode, String left, String right, JVMFrame frame) {
        if (Objects.equals(left, "intId")) {
            if (Objects.equals(right, "intId")) {
                this.code.addAll(frame.lessEqualIntId(leftNode.op, rightNode.op));
            } else if (Objects.equals(right, "intValue")) {
                this.code.addAll(frame.lessEqualIntIdValue(leftNode.op, rightNode.intValue, true));
            } else if (Objects.equals(right, "intStack")) {
                this.code.addAll(frame.lessEqualIntIdStack(leftNode.op, true));
            }
            return "boolStack";
        } else if (Objects.equals(left, "intValue")) {
            if (Objects.equals(right, "intId")) {
                this.code.addAll(frame.lessEqualIntIdValue(rightNode.op, leftNode.intValue, false));
            } else if (Objects.equals(right, "intValue")) {
                this.code.addAll(frame.lessEqualIntValue(leftNode.intValue, rightNode.intValue));
            } else if (Objects.equals(right, "intStack")) {
                this.code.addAll(frame.lessEqualIntValueStack(leftNode.intValue, false));
            }
            return "boolStack";
        } else if (Objects.equals(left, "intStack")) {
            if (Objects.equals(right, "intId")) {
                this.code.addAll(frame.lessEqualIntIdStack(rightNode.op, false));
            } else if (Objects.equals(right, "intValue")) {
                this.code.addAll(frame.lessEqualIntValueStack(rightNode.intValue, true));
            } else if (Objects.equals(right, "intStack")) {
                this.code.addAll(frame.lessEqualIntStack());
            }
            return "boolStack";
        } else if (Objects.equals(left, "realId")) {
            if (Objects.equals(right, "realId")) {
                this.code.addAll(frame.lessEqualRealId(leftNode.op, rightNode.op));
            } else if (Objects.equals(right, "realValue")) {
                this.code.addAll(frame.lessEqualRealIdValue(leftNode.op, rightNode.intValue, true));
            } else if (Objects.equals(right, "realStack")) {
                this.code.addAll(frame.lessEqualRealIdStack(leftNode.op, true));
            }
            return "boolStack";
        } else if (Objects.equals(left, "realValue")) {
            if (Objects.equals(right, "realId")) {
                this.code.addAll(frame.lessEqualRealIdValue(rightNode.op, leftNode.intValue, false));
            } else if (Objects.equals(right, "realValue")) {
                this.code.addAll(frame.lessEqualRealValue(leftNode.intValue, rightNode.intValue));
            } else if (Objects.equals(right, "realStack")) {
                this.code.addAll(frame.lessEqualRealStackValue(leftNode.intValue, false));
            }
            return "boolStack";
        } else if (Objects.equals(left, "realStack")) {
            if (Objects.equals(right, "realId")) {
                this.code.addAll(frame.lessEqualRealIdStack(rightNode.op, false));
            } else if (Objects.equals(right, "realValue")) {
                this.code.addAll(frame.lessEqualRealStackValue(rightNode.intValue, true));
            } else if (Objects.equals(right, "realStack")) {
                this.code.addAll(frame.lessEqualRealStack());
            }
            return "boolStack";
        }
        return "TypeError";
    }

    String performNotEqual (ASTNode leftNode, ASTNode rightNode, String left, String right, JVMFrame frame) {
        if (Objects.equals(left, "intId")) {
            if (Objects.equals(right, "intId")) {
                this.code.addAll(frame.notEqualIntId(leftNode.op, rightNode.op));
            } else if (Objects.equals(right, "intValue")) {
                this.code.addAll(frame.notEqualIntIdValue(leftNode.op, rightNode.intValue, true));
            } else if (Objects.equals(right, "intStack")) {
                this.code.addAll(frame.notEqualIntIdStack(leftNode.op, true));
            }
            return "boolStack";
        } else if (Objects.equals(left, "intValue")) {
            if (Objects.equals(right, "intId")) {
                this.code.addAll(frame.notEqualIntIdValue(rightNode.op, leftNode.intValue, false));
            } else if (Objects.equals(right, "intValue")) {
                this.code.addAll(frame.notEqualIntValue(leftNode.intValue, rightNode.intValue));
            } else if (Objects.equals(right, "intStack")) {
                this.code.addAll(frame.notEqualIntValueStack(leftNode.intValue, false));
            }
            return "boolStack";
        } else if (Objects.equals(left, "intStack")) {
            if (Objects.equals(right, "intId")) {
                this.code.addAll(frame.notEqualIntIdStack(rightNode.op, false));
            } else if (Objects.equals(right, "intValue")) {
                this.code.addAll(frame.notEqualIntValueStack(rightNode.intValue, true));
            } else if (Objects.equals(right, "intStack")) {
                this.code.addAll(frame.notEqualIntStack());
            }
            return "boolStack";
        } else if (Objects.equals(left, "realId")) {
            if (Objects.equals(right, "realId")) {
                this.code.addAll(frame.notEqualRealId(leftNode.op, rightNode.op));
            } else if (Objects.equals(right, "realValue")) {
                this.code.addAll(frame.notEqualRealIdValue(leftNode.op, rightNode.intValue, true));
            } else if (Objects.equals(right, "realStack")) {
                this.code.addAll(frame.notEqualRealIdStack(leftNode.op, true));
            }
            return "boolStack";
        } else if (Objects.equals(left, "realValue")) {
            if (Objects.equals(right, "realId")) {
                this.code.addAll(frame.notEqualRealIdValue(rightNode.op, leftNode.intValue, false));
            } else if (Objects.equals(right, "realValue")) {
                this.code.addAll(frame.notEqualRealValue(leftNode.intValue, rightNode.intValue));
            } else if (Objects.equals(right, "realStack")) {
                this.code.addAll(frame.notEqualRealStackValue(leftNode.intValue, false));
            }
            return "boolStack";
        } else if (Objects.equals(left, "realStack")) {
            if (Objects.equals(right, "realId")) {
                this.code.addAll(frame.notEqualRealIdStack(rightNode.op, false));
            } else if (Objects.equals(right, "realValue")) {
                this.code.addAll(frame.notEqualRealStackValue(rightNode.intValue, true));
            } else if (Objects.equals(right, "realStack")) {
                this.code.addAll(frame.notEqualRealStack());
            }
            return "boolStack";
        }
        return "TypeError";
    }

    String performAdd (ASTNode leftNode, ASTNode rightNode, String left, String right, JVMFrame frame) {
        if (Objects.equals(left, "intId")) {
            if (Objects.equals(right, "intId")) {
                this.code.addAll(frame.addIntId(leftNode.op, rightNode.op));
            } else if (Objects.equals(right, "intValue")) {

                this.code.addAll(frame.addIntIdValue(leftNode.op, rightNode.intValue));
            } else if (Objects.equals(right, "intStack")) {
                this.code.addAll(frame.addIntStackId(true, leftNode.op));
            }
            return "intStack";
        } else if (Objects.equals(left, "intValue")) {
            if (Objects.equals(right, "intId")) {
                this.code.addAll(frame.addIntIdValue(rightNode.op, leftNode.intValue));
            } else if (Objects.equals(right, "intValue")) {
                this.code.addAll(frame.addIntValue(leftNode.intValue, rightNode.intValue));
            } else if (Objects.equals(right, "intStack")) {
                this.code.addAll(frame.addIntStackValue(true, leftNode.intValue));
            }
            return "intStack";
        } else if (Objects.equals(left, "intStack")) {
            if (Objects.equals(right, "intId")) {
                this.code.addAll(frame.addIntStackId(false, rightNode.op));
            } else if (Objects.equals(right, "intValue")) {
                this.code.addAll(frame.addIntStackValue(false, rightNode.intValue));
            } else if (Objects.equals(right, "intStack")) {
                this.code.addAll(frame.addIntStack());
            }
            return "intStack";
        } else if (Objects.equals(left, "realId")) {
            if (Objects.equals(right, "realId")) {
                this.code.addAll(frame.addRealId(leftNode.op, rightNode.op));
            } else if (Objects.equals(right, "realValue")) {
                this.code.addAll(frame.addRealIdValue(leftNode.op, rightNode.realValue));
            } else if (Objects.equals(right, "realStack")) {
                this.code.addAll(frame.addRealStackId(true, leftNode.op));
            }
            return "realStack";
        } else if (Objects.equals(left, "realValue")) {
            if (Objects.equals(right, "realId")) {
                this.code.addAll(frame.addRealValueId(leftNode.realValue, rightNode.op));
            } else if (Objects.equals(right, "realValue")) {
                this.code.addAll(frame.addRealValue(leftNode.realValue, rightNode.realValue));
            } else if (Objects.equals(right, "realStack")) {
                this.code.addAll(frame.addRealStackValue(true, leftNode.realValue));
            }
            return "realStack";
        } else if (Objects.equals(left, "realStack")) {
            if (Objects.equals(right, "realId")) {
                this.code.addAll(frame.addRealStackId(false, rightNode.op));
            } else if (Objects.equals(right, "realValue")) {
                this.code.addAll(frame.addRealStackValue(false, rightNode.realValue));
            } else if (Objects.equals(right, "realStack")) {
                this.code.addAll(frame.addRealStack());
            }
            return "realStack";
        }
        return "Error";
    }

    String performSubtract (ASTNode leftNode, ASTNode rightNode, String left, String right, JVMFrame frame) {
        if (Objects.equals(left, "intId")) {
            if (Objects.equals(right, "intId")) {
                this.code.addAll(frame.subtractIntId(leftNode.op, rightNode.op));
            } else if (Objects.equals(right, "intValue")) {
                this.code.addAll(frame.subtractIntIdValue(leftNode.op, rightNode.intValue));
            } else if (Objects.equals(right, "intStack")) {
                this.code.addAll(frame.subtractIntStackId(true, leftNode.op));
            }
            return "intStack";
        } else if (Objects.equals(left, "intValue")) {
            if (Objects.equals(right, "intId")) {
                this.code.addAll(frame.subtractIntIdValue(rightNode.op, leftNode.intValue));
            } else if (Objects.equals(right, "intValue")) {
                this.code.addAll(frame.subtractIntValue(leftNode.intValue, rightNode.intValue));
            } else if (Objects.equals(right, "intStack")) {
                this.code.addAll(frame.subtractIntStackValue(true, leftNode.intValue));
            }
            return "intStack";
        } else if (Objects.equals(left, "intStack")) {
            if (Objects.equals(right, "intId")) {
                this.code.addAll(frame.subtractIntStackId(false, rightNode.op));
            } else if (Objects.equals(right, "intValue")) {
                this.code.addAll(frame.subtractIntStackValue(false, rightNode.intValue));
            } else if (Objects.equals(right, "intStack")) {
                this.code.addAll(frame.subtractIntStack());
            }
            return "intStack";
        } else if (Objects.equals(left, "realId")) {
            if (Objects.equals(right, "realId")) {
                this.code.addAll(frame.subtractRealId(leftNode.op, rightNode.op));
            } else if (Objects.equals(right, "realValue")) {
                this.code.addAll(frame.subtractRealIdValue(leftNode.op, rightNode.realValue));
            } else if (Objects.equals(right, "realStack")) {
                this.code.addAll(frame.subtractRealStackId(true, leftNode.op));
            }
            return "realStack";
        } else if (Objects.equals(left, "realValue")) {
            if (Objects.equals(right, "realId")) {
                this.code.addAll(frame.subtractRealValueId(leftNode.realValue, rightNode.op));
            } else if (Objects.equals(right, "realValue")) {
                this.code.addAll(frame.subtractRealValue(leftNode.realValue, rightNode.realValue));
            } else if (Objects.equals(right, "realStack")) {
                this.code.addAll(frame.subtractRealStackValue(true, leftNode.realValue));
            }
            return "realStack";
        } else if (Objects.equals(left, "realStack")) {
            if (Objects.equals(right, "realId")) {
                this.code.addAll(frame.subtractRealStackId(false, rightNode.op));
            } else if (Objects.equals(right, "realValue")) {
                this.code.addAll(frame.subtractRealStackValue(false, rightNode.realValue));
            } else if (Objects.equals(right, "realStack")) {
                this.code.addAll(frame.subtractRealStack());
            }
            return "realStack";
        }
        return "Error";
    }

    String performMultiply (ASTNode leftNode, ASTNode rightNode, String left, String right, JVMFrame frame) {
        if (Objects.equals(left, "intId")) {
            if (Objects.equals(right, "intId")) {
                this.code.addAll(frame.multiplyIntId(leftNode.op, rightNode.op));
            } else if (Objects.equals(right, "intValue")) {
                this.code.addAll(frame.multiplyIntIdValue(leftNode.op, rightNode.intValue));
            } else if (Objects.equals(right, "intStack")) {
                this.code.addAll(frame.multiplyIntStackId(true, leftNode.op));
            }
            return "intStack";
        } else if (Objects.equals(left, "intValue")) {
            if (Objects.equals(right, "intId")) {
                this.code.addAll(frame.multiplyIntIdValue(rightNode.op, leftNode.intValue));
            } else if (Objects.equals(right, "intValue")) {
                this.code.addAll(frame.multiplyIntValue(leftNode.intValue, rightNode.intValue));
            } else if (Objects.equals(right, "intStack")) {
                this.code.addAll(frame.multiplyIntStackValue(true, leftNode.intValue));
            }
            return "intStack";
        } else if (Objects.equals(left, "intStack")) {
            if (Objects.equals(right, "intId")) {
                this.code.addAll(frame.multiplyIntStackId(false, rightNode.op));
            } else if (Objects.equals(right, "intValue")) {
                this.code.addAll(frame.multiplyIntStackValue(false, rightNode.intValue));
            } else if (Objects.equals(right, "intStack")) {
                this.code.addAll(frame.multiplyIntStack());
            }
            return "intStack";
        } else if (Objects.equals(left, "realId")) {
            if (Objects.equals(right, "realId")) {
                this.code.addAll(frame.multiplyRealId(leftNode.op, rightNode.op));
            } else if (Objects.equals(right, "realValue")) {
                this.code.addAll(frame.multiplyRealIdValue(leftNode.op, rightNode.realValue));
            } else if (Objects.equals(right, "realStack")) {
                this.code.addAll(frame.multiplyRealStackId(true, leftNode.op));
            }
            return "realStack";
        } else if (Objects.equals(left, "realValue")) {
            if (Objects.equals(right, "realId")) {
                this.code.addAll(frame.multiplyRealValueId(leftNode.realValue, rightNode.op));
            } else if (Objects.equals(right, "realValue")) {
                this.code.addAll(frame.multiplyRealValue(leftNode.realValue, rightNode.realValue));
            } else if (Objects.equals(right, "realStack")) {
                this.code.addAll(frame.multiplyRealStackValue(true, leftNode.realValue));
            }
            return "realStack";
        } else if (Objects.equals(left, "realStack")) {
            if (Objects.equals(right, "realId")) {
                this.code.addAll(frame.multiplyRealStackId(false, rightNode.op));
            } else if (Objects.equals(right, "realValue")) {
                this.code.addAll(frame.multiplyRealStackValue(false, rightNode.realValue));
            } else if (Objects.equals(right, "realStack")) {
                this.code.addAll(frame.multiplyRealStack());
            }
            return "realStack";
        }
        return "Error";
    }

    String performDivision (ASTNode leftNode, ASTNode rightNode, String left, String right, JVMFrame frame) {
        if (Objects.equals(left, "intId")) {
            if (Objects.equals(right, "intId")) {
                this.code.addAll(frame.divisionIntId(leftNode.op, rightNode.op));
            } else if (Objects.equals(right, "intValue")) {
                this.code.addAll(frame.divisionIntIdValue(leftNode.op, rightNode.intValue));
            } else if (Objects.equals(right, "intStack")) {
                this.code.addAll(frame.divisionIntStackId(true, leftNode.op));
            }
            return "intStack";
        } else if (Objects.equals(left, "intValue")) {
            if (Objects.equals(right, "intId")) {
                this.code.addAll(frame.divisionIntIdValue(rightNode.op, leftNode.intValue));
            } else if (Objects.equals(right, "intValue")) {
                this.code.addAll(frame.divisionIntValue(leftNode.intValue, rightNode.intValue));
            } else if (Objects.equals(right, "intStack")) {
                this.code.addAll(frame.divisionIntStackValue(true, leftNode.intValue));
            }
            return "intStack";
        } else if (Objects.equals(left, "intStack")) {
            if (Objects.equals(right, "intId")) {
                this.code.addAll(frame.divisionIntStackId(false, rightNode.op));
            } else if (Objects.equals(right, "intValue")) {
                this.code.addAll(frame.divisionIntStackValue(false, rightNode.intValue));
            } else if (Objects.equals(right, "intStack")) {
                this.code.addAll(frame.divisionIntStack());
            }
            return "intStack";
        } else if (Objects.equals(left, "realId")) {
            if (Objects.equals(right, "realId")) {
                this.code.addAll(frame.divisionRealId(leftNode.op, rightNode.op));
            } else if (Objects.equals(right, "realValue")) {
                this.code.addAll(frame.divisionRealIdValue(leftNode.op, rightNode.realValue));
            } else if (Objects.equals(right, "realStack")) {
                this.code.addAll(frame.divisionRealStackId(true, leftNode.op));
            }
            return "realStack";
        } else if (Objects.equals(left, "realValue")) {
            if (Objects.equals(right, "realId")) {
                this.code.addAll(frame.divisionRealValueId(leftNode.realValue, rightNode.op));
            } else if (Objects.equals(right, "realValue")) {
                this.code.addAll(frame.divisionRealValue(leftNode.realValue, rightNode.realValue));
            } else if (Objects.equals(right, "realStack")) {
                this.code.addAll(frame.divisionRealStackValue(true, leftNode.realValue));
            }
            return "realStack";
        } else if (Objects.equals(left, "realStack")) {
            if (Objects.equals(right, "realId")) {
                this.code.addAll(frame.divisionRealStackId(false, rightNode.op));
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
