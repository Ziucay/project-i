package compiler;

import java.util.ArrayList;

public class ASTNode {
    public String op;
    public ArrayList<ASTNode> childs;
    public float realValue;
    public int intValue;
    public boolean boolValue;

    ASTNode (String op) {
        this.op = op;
        this.childs = new ArrayList<>();
    }

    ASTNode (float value) {
        this.op = "realValue";
        this.realValue = value;
        this.childs = new ArrayList<>();
    }

    ASTNode (boolean value) {
        this.op = "boolValue";
        this.boolValue = value;
        this.childs = new ArrayList<>();
    }

    ASTNode (int value) {
        this.op = "intValue";
        this.intValue = value;
        this.childs = new ArrayList<>();
    }

    void addChild (ASTNode node) {
        this.childs.add(node);
    }

    static ASTNode exampleOne(ASTNode root) {
        ASTNode routineNode = new ASTNode("routine");
        root.addChild(routineNode);
        ASTNode bodyNode = new ASTNode("body");
        routineNode.addChild(new ASTNode("a"));
        routineNode.addChild(new ASTNode("parameters"));
        routineNode.addChild(bodyNode);
        ASTNode assignNode = new ASTNode("assign");
        ASTNode assignNodeSecond = new ASTNode("assign");
        bodyNode.addChild(assignNode);
        bodyNode.addChild(assignNodeSecond);
        assignNode.addChild(new ASTNode("ab"));
        ASTNode plusNode = new ASTNode("+");
        ASTNode plusNodeSecond = new ASTNode("+");
        assignNode.addChild(plusNode);
        plusNode.addChild(new ASTNode(1));
        plusNode.addChild(new ASTNode(2));
        assignNodeSecond.addChild(new ASTNode("cd"));
        assignNodeSecond.addChild(plusNodeSecond);
        plusNodeSecond.addChild(new ASTNode("ab"));
        plusNodeSecond.addChild(new ASTNode(3));
        return root;
    }

    static ASTNode exampleTwo(ASTNode root) {
        ASTNode routineNode = new ASTNode("routine");
        root.addChild(routineNode);
        ASTNode bodyNode = new ASTNode("body");
        routineNode.addChild(new ASTNode("a"));
        routineNode.addChild(new ASTNode("parameters"));
        routineNode.addChild(bodyNode);
        ASTNode assignNode = new ASTNode("assign");
        ASTNode assignNodeSecond = new ASTNode("assign");
        bodyNode.addChild(assignNode);
        bodyNode.addChild(assignNodeSecond);
        assignNode.addChild(new ASTNode("ab"));
        ASTNode plusNode = new ASTNode("+");
        ASTNode mulNode = new ASTNode("*");
        assignNode.addChild(plusNode);
        plusNode.addChild(new ASTNode(2));
        plusNode.addChild(new ASTNode(3));
        assignNodeSecond.addChild(new ASTNode("ad"));
        assignNodeSecond.addChild(mulNode);
        mulNode.addChild(new ASTNode(2));
        mulNode.addChild(new ASTNode(3));
        ASTNode ifNode = new ASTNode("if");
        bodyNode.addChild(ifNode);
        ASTNode condNode = new ASTNode(">=");
        ifNode.addChild(condNode);
        condNode.addChild(new ASTNode("ad"));
        condNode.addChild(new ASTNode("ab"));
        ASTNode thenNode = new ASTNode("assign");
        ASTNode elseNode = new ASTNode("assign");
        ifNode.addChild(thenNode);
        ifNode.addChild(elseNode);
        thenNode.addChild(new ASTNode("ad"));
        thenNode.addChild(new ASTNode("ab"));
        elseNode.addChild(new ASTNode("ad"));
        elseNode.addChild(new ASTNode("ab"));
        return root;
    }

    public String toString () {
        return this.op;
    }
}
