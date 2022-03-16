package compiler;

import java.util.Iterator;
import java.util.List;

public class Node {
    String identifier;
    Object value;
    List<Object> descendants;

    public Node(String identifier, Object value, List<Object> descendants) {
        this.identifier = identifier;
        this.value = value;
        this.descendants = descendants;
    }

    public Node(String identifier, Object value) {
        this.identifier = identifier;
        this.value = value;
        this.descendants = null;
    }

    public String toString() {
        StringBuilder buffer = new StringBuilder(50);
        print(buffer, "", "");
        return buffer.toString();
    }

    // This part is modified version of code from here:
    // https://stackoverflow.com/questions/4965335/how-to-print-binary-tree-diagram-in-java
    private void print(StringBuilder buffer, String prefix, String childrenPrefix) {
        // System.out.println("Enter in node " + identifier);
        buffer.append(prefix);
        buffer.append(identifier);
        buffer.append('\n');
        if (descendants == null) {
            return;
        }

        for (Iterator<Object> it = descendants.iterator(); it.hasNext(); ) {
            Object next = it.next();
            if (next == null) {
                continue;
            }
            if (it.hasNext()) {
                ((Node) next).print(buffer, childrenPrefix + "├── ", childrenPrefix + "│   ");
            } else {
                ((Node) next).print(buffer, childrenPrefix + "└── ", childrenPrefix + "    ");
            }
        }
    }
}
