//### This file created by BYACC 1.8(/Java extension  1.15)
//### Java capabilities added 7 Jan 97, Bob Jamison
//### Updated : 27 Nov 97  -- Bob Jamison, Joe Nieten
//###           01 Jan 98  -- Bob Jamison -- fixed generic semantic constructor
//###           01 Jun 99  -- Bob Jamison -- added Runnable support
//###           06 Aug 00  -- Bob Jamison -- made state variables class-global
//###           03 Jan 01  -- Bob Jamison -- improved flags, tracing
//###           16 May 01  -- Bob Jamison -- added custom stack sizing
//###           04 Mar 02  -- Yuval Oren  -- improved java performance, added options
//###           14 Mar 02  -- Tomas Hurka -- -d support, static initializer workaround
//### Please send bug reports to tom@hukatronic.cz
//### static char yysccsid[] = "@(#)yaccpar	1.8 (Berkeley) 01/20/90";


//#line 3 "yacc.y"
package compiler;

import java.util.*;
//#line 20 "Parser.java"


public class Parser {

    boolean yydebug;        //do I want debug output?
    int yynerrs;            //number of errors so far
    int yyerrflag;          //was there an error?
    int yychar;             //the current working character

    //########## MESSAGES ##########
//###############################################################
// method: debug
//###############################################################
    void debug(String msg) {
        if (yydebug)
            System.out.println(msg);
    }

    //########## STATE STACK ##########
    final static int YYSTACKSIZE = 500;  //maximum stack size
    int statestk[] = new int[YYSTACKSIZE]; //state stack
    int stateptr;
    int stateptrmax;                     //highest index of stackptr
    int statemax;                        //state when highest index reached

    //###############################################################
// methods: state stack push,pop,drop,peek
//###############################################################
    final void state_push(int state) {
        try {
            stateptr++;
            statestk[stateptr] = state;
        } catch (ArrayIndexOutOfBoundsException e) {
            int oldsize = statestk.length;
            int newsize = oldsize * 2;
            int[] newstack = new int[newsize];
            System.arraycopy(statestk, 0, newstack, 0, oldsize);
            statestk = newstack;
            statestk[stateptr] = state;
        }
    }

    final int state_pop() {
        return statestk[stateptr--];
    }

    final void state_drop(int cnt) {
        stateptr -= cnt;
    }

    final int state_peek(int relative) {
        return statestk[stateptr - relative];
    }

    //###############################################################
// method: init_stacks : allocate and prepare stacks
//###############################################################
    final boolean init_stacks() {
        stateptr = -1;
        val_init();
        return true;
    }

    //###############################################################
// method: dump_stacks : show n levels of the stacks
//###############################################################
    void dump_stacks(int count) {
        int i;
        System.out.println("=index==state====value=     s:" + stateptr + "  v:" + valptr);
        for (i = 0; i < count; i++)
            System.out.println(" " + i + "    " + statestk[i] + "      " + valstk[i]);
        System.out.println("======================");
    }


//########## SEMANTIC VALUES ##########
//public class ParserVal is defined in ParserVal.java


    String yytext;//user variable to return contextual strings
    ParserVal yyval; //used to return semantic vals from action routines
    ParserVal yylval;//the 'lval' (result) I got from yylex()
    ParserVal valstk[];
    int valptr;

    //###############################################################
// methods: value stack push,pop,drop,peek.
//###############################################################
    void val_init() {
        valstk = new ParserVal[YYSTACKSIZE];
        yyval = new ParserVal();
        yylval = new ParserVal();
        valptr = -1;
    }

    void val_push(ParserVal val) {
        if (valptr >= YYSTACKSIZE)
            return;
        valstk[++valptr] = val;
    }

    ParserVal val_pop() {
        if (valptr < 0)
            return new ParserVal();
        return valstk[valptr--];
    }

    void val_drop(int cnt) {
        int ptr;
        ptr = valptr - cnt;
        if (ptr < 0)
            return;
        valptr = ptr;
    }

    ParserVal val_peek(int relative) {
        int ptr;
        ptr = valptr - relative;
        if (ptr < 0)
            return new ParserVal();
        return valstk[ptr];
    }

    final ParserVal dup_yyval(ParserVal val) {
        ParserVal dup = new ParserVal();
        dup.ival = val.ival;
        dup.dval = val.dval;
        dup.sval = val.sval;
        dup.obj = val.obj;
        return dup;
    }

    //#### end semantic value section ####
    public final static short LEFT_PAREN = 257;
    public final static short RIGHT_PAREN = 258;
    public final static short LEFT_BRACE = 259;
    public final static short LEFT_SQUARE_BRACE = 260;
    public final static short RIGHT_SQUARE_BRACE = 261;
    public final static short RIGHT_BRACE = 262;
    public final static short COMMA = 263;
    public final static short DOT = 264;
    public final static short MINUS = 265;
    public final static short PLUS = 266;
    public final static short SLASH = 267;
    public final static short STAR = 268;
    public final static short PERCENT = 269;
    public final static short EQUAL = 270;
    public final static short EQUAL_EQUAL = 271;
    public final static short SLASH_EQUAL = 272;
    public final static short GREATER = 273;
    public final static short GREATER_EQUAL = 274;
    public final static short LESS = 275;
    public final static short LESS_EQUAL = 276;
    public final static short DOT_DOT = 277;
    public final static short IDENTIFIER = 278;
    public final static short SEMICOLON = 279;
    public final static short COLON = 280;
    public final static short TYPE_BOOLEAN = 281;
    public final static short TYPE_INTEGER = 282;
    public final static short TYPE_REAL = 283;
    public final static short TYPE_RECORD = 284;
    public final static short TYPE_ARRAY = 285;
    public final static short TYPE_STRING = 286;
    public final static short AND = 287;
    public final static short ARRAY = 288;
    public final static short BOOLEAN = 289;
    public final static short ELSE = 290;
    public final static short END = 291;
    public final static short FALSE = 292;
    public final static short FOR = 293;
    public final static short IF = 294;
    public final static short IN = 295;
    public final static short INTEGER = 296;
    public final static short IS = 297;
    public final static short LOOP = 298;
    public final static short NOT = 299;
    public final static short OR = 300;
    public final static short PRINT = 301;
    public final static short REAL = 302;
    public final static short RECORD = 303;
    public final static short RETURN = 304;
    public final static short REVERSE = 305;
    public final static short ROUTINE = 306;
    public final static short THEN = 307;
    public final static short TRUE = 308;
    public final static short TYPE = 309;
    public final static short VAR = 310;
    public final static short WHILE = 311;
    public final static short XOR = 312;
    public final static short STRING = 313;
    public final static short WALRUS = 314;
    public final static short EOF = 315;
    public final static short YYERRCODE = 256;
    final static short yylhs[] = {-1,
            0, 0, 1, 1, 1, 2, 2, 3, 3, 3,
            4, 4, 9, 9, 9, 11, 6, 6, 6, 10,
            12, 12, 13, 13, 13, 13, 13, 13, 14, 15,
            19, 19, 16, 20, 21, 22, 23, 8, 17, 24,
            24, 18, 18, 7, 7, 7, 7, 25, 25, 25,
            25, 25, 25, 25, 26, 26, 26, 26, 27, 27,
            27, 28, 28, 29, 29, 29, 29, 29, 5,
    };
    final static short yylen[] = {2,
            2, 0, 2, 1, 0, 1, 1, 4, 6, 4,
            8, 10, 1, 3, 0, 3, 1, 1, 1, 1,
            2, 0, 1, 1, 1, 1, 1, 1, 3, 4,
            1, 3, 5, 1, 1, 1, 1, 1, 6, 4,
            5, 5, 7, 3, 3, 3, 1, 3, 3, 3,
            3, 3, 3, 1, 3, 3, 3, 1, 3, 3,
            1, 1, 3, 1, 1, 1, 1, 1, 1,
    };
    final static short yydefred[] = {0,
            38, 0, 0, 0, 0, 6, 7, 0, 69, 0,
            1, 3, 0, 0, 0, 0, 19, 17, 18, 0,
            0, 65, 64, 67, 66, 68, 10, 0, 0, 0,
            0, 62, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 9, 63, 44, 45, 46, 52,
            53, 50, 51, 48, 49, 56, 55, 57, 60, 59,
            16, 0, 0, 14, 0, 35, 36, 34, 28, 0,
            0, 20, 0, 23, 24, 25, 26, 27, 0, 0,
            0, 0, 0, 0, 11, 21, 0, 0, 0, 0,
            0, 0, 29, 0, 0, 0, 0, 12, 0, 30,
            0, 0, 0, 0, 0, 32, 33, 0, 0, 0,
            37, 42, 0, 0, 40, 39, 0, 41, 43,
    };
    final static short yydgoto[] = {3,
            4, 5, 79, 7, 26, 20, 101, 8, 34, 81,
            35, 82, 83, 84, 85, 86, 87, 88, 102, 89,
            90, 91, 123, 106, 28, 29, 30, 31, 32,
    };
    final static short yysindex[] = {-295,
            0, -273, 0, -285, -295, 0, 0, -273, 0, -271,
            0, 0, -218, -205, -172, -273, 0, 0, 0, -240,
            -172, 0, 0, 0, 0, 0, 0, -223, -135, -152,
            -246, 0, -237, -195, -196, -172, -189, -172, -172, -172,
            -172, -172, -172, -172, -172, -172, -172, -172, -172, -172,
            -172, -205, -222, -273, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, -205, -270, 0, -221, 0, 0, 0, 0, -254,
            -217, 0, -270, 0, 0, 0, 0, 0, -172, -273,
            -172, -270, -172, -172, 0, 0, -219, -199, -203, -188,
            -156, -149, 0, -270, -210, -180, -270, 0, -172, 0,
            -169, -172, -150, -270, -204, 0, 0, -133, -172, -146,
            0, 0, -270, -172, 0, 0, -143, 0, 0,
    };
    final static short yyrindex[] = {1,
            0, 0, 0, 0, -173, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, -103, 0, 0, 0, -87,
            0, 0, 0, 0, 0, 0, 0, -245, -256, -98,
            -144, 0, 0, 0, -101, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, -103, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, -132, 0, 0, 0, 0, 0, 0, 0,
            0, 0, -198, 0, 0, 0, 0, 0, 0, 0,
            0, -132, 0, 0, 0, 0, 0, 0, 0, 0,
            -100, 0, 0, -132, 0, 0, -198, 0, 0, 0,
            0, 0, 0, -132, 0, 0, 0, 0, 0, 0,
            0, 0, -132, 0, 0, 0, 0, 0, 0,
    };
    final static short yygindex[] = {0,
            156, 0, 12, 0, -2, -36, -11, 0, 110, 118,
            0, 86, 0, 0, 0, 0, 0, 0, 61, 0,
            0, 0, 0, 0, 140, 104, 49, 0, 0,
    };
    final static int YYTABLESIZE = 316;
    static short yytable[];

    static {
        yytable();
    }

    static void yytable() {
        yytable = new short[]{10,
                2, 54, 93, 27, 9, 13, 54, 9, 14, 37,
                1, 6, 47, 33, 2, 71, 6, 47, 50, 51,
                54, 54, 76, 77, 55, 15, 57, 58, 59, 11,
                54, 47, 47, 54, 54, 75, 54, 54, 16, 2,
                78, 54, 52, 54, 47, 47, 21, 47, 47, 54,
                54, 33, 47, 54, 54, 54, 36, 72, 54, 94,
                47, 47, 53, 38, 47, 47, 54, 9, 56, 47,
                80, 22, 23, 95, 73, 92, 39, 97, 104, 99,
                80, 24, 103, 17, 21, 121, 122, 98, 40, 80,
                18, 22, 22, 113, 112, 105, 19, 25, 69, 70,
                118, 80, 108, 107, 80, 9, 109, 125, 110, 22,
                23, 80, 128, 61, 47, 48, 49, 114, 61, 24,
                80, 117, 61, 61, 61, 61, 119, 61, 61, 61,
                61, 61, 61, 61, 41, 25, 42, 43, 44, 45,
                46, 4, 61, 124, 126, 61, 61, 129, 61, 61,
                66, 67, 68, 61, 15, 61, 13, 31, 22, 58,
                12, 61, 61, 74, 58, 61, 61, 61, 96, 116,
                61, 58, 0, 58, 58, 58, 58, 58, 58, 58,
                60, 61, 62, 63, 64, 65, 0, 0, 58, 0,
                8, 58, 58, 0, 58, 58, 0, 0, 0, 58,
                0, 58, 8, 8, 0, 8, 8, 58, 58, 100,
                0, 58, 58, 58, 0, 0, 58, 0, 8, 0,
                0, 111, 8, 8, 115, 0, 0, 8, 0, 0,
                0, 120, 0, 0, 0, 0, 0, 0, 0, 0,
                127, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 5,
        };
    }

    static short yycheck[];

    static {
        yycheck();
    }

    static void yycheck() {
        yycheck = new short[]{2,
                0, 258, 257, 15, 278, 8, 263, 278, 280, 21,
                306, 0, 258, 16, 310, 52, 5, 263, 265, 266,
                277, 278, 293, 294, 36, 297, 38, 39, 40, 315,
                287, 277, 278, 290, 291, 72, 293, 294, 257, 310,
                311, 298, 280, 300, 290, 291, 257, 293, 294, 306,
                307, 54, 298, 310, 311, 312, 297, 280, 315, 314,
                306, 307, 258, 287, 310, 311, 263, 278, 258, 315,
                73, 282, 283, 291, 297, 297, 300, 89, 298, 91,
                83, 292, 94, 289, 257, 290, 291, 90, 312, 92,
                296, 290, 291, 105, 305, 295, 302, 308, 50, 51,
                112, 104, 291, 307, 107, 278, 263, 119, 258, 282,
                283, 114, 124, 258, 267, 268, 269, 298, 263, 292,
                123, 291, 267, 268, 269, 270, 277, 272, 273, 274,
                275, 276, 277, 278, 270, 308, 272, 273, 274, 275,
                276, 315, 287, 277, 291, 290, 291, 291, 293, 294,
                47, 48, 49, 298, 258, 300, 258, 258, 291, 258,
                5, 306, 307, 54, 263, 310, 311, 312, 83, 109,
                315, 270, -1, 272, 273, 274, 275, 276, 277, 278,
                41, 42, 43, 44, 45, 46, -1, -1, 287, -1,
                278, 290, 291, -1, 293, 294, -1, -1, -1, 298,
                -1, 300, 290, 291, -1, 293, 294, 306, 307, 92,
                -1, 310, 311, 312, -1, -1, 315, -1, 306, -1,
                -1, 104, 310, 311, 107, -1, -1, 315, -1, -1,
                -1, 114, -1, -1, -1, -1, -1, -1, -1, -1,
                123, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                -1, -1, -1, -1, -1, 315,
        };
    }

    final static short YYFINAL = 3;
    final static short YYMAXTOKEN = 315;
    final static String yyname[] = {
            "end-of-file", null, null, null, null, null, null, null, null, null, null, null, null, null,
            null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
            null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
            null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
            null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
            null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
            null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
            null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
            null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
            null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
            null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
            null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
            null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
            null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
            null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
            null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
            null, null, null, "LEFT_PAREN", "RIGHT_PAREN", "LEFT_BRACE", "LEFT_SQUARE_BRACE",
            "RIGHT_SQUARE_BRACE", "RIGHT_BRACE", "COMMA", "DOT", "MINUS", "PLUS", "SLASH", "STAR",
            "PERCENT", "EQUAL", "EQUAL_EQUAL", "SLASH_EQUAL", "GREATER", "GREATER_EQUAL", "LESS",
            "LESS_EQUAL", "DOT_DOT", "IDENTIFIER", "SEMICOLON", "COLON", "TYPE_BOOLEAN",
            "TYPE_INTEGER", "TYPE_REAL", "TYPE_RECORD", "TYPE_ARRAY", "TYPE_STRING", "AND",
            "ARRAY", "BOOLEAN", "ELSE", "END", "FALSE", "FOR", "IF", "IN", "INTEGER", "IS", "LOOP",
            "NOT", "OR", "PRINT", "REAL", "RECORD", "RETURN", "REVERSE", "ROUTINE", "THEN", "TRUE",
            "TYPE", "VAR", "WHILE", "XOR", "STRING", "WALRUS", "EOF",
    };
    final static String yyrule[] = {
            "$accept : Program",
            "Program : Lines EOF",
            "Program :",
            "Lines : Line Lines",
            "Lines : Line",
            "Lines :",
            "Line : VariableDeclaration",
            "Line : RoutineDeclaration",
            "VariableDeclaration : VAR ModifiablePrimary COLON Type",
            "VariableDeclaration : VAR ModifiablePrimary COLON Type IS Expression",
            "VariableDeclaration : VAR ModifiablePrimary IS Expression",
            "RoutineDeclaration : RoutineKeyword ModifiablePrimary LEFT_PAREN Parameters RIGHT_PAREN IS Body END",
            "RoutineDeclaration : RoutineKeyword ModifiablePrimary LEFT_PAREN Parameters RIGHT_PAREN COLON Type IS Body END",
            "Parameters : ParameterDeclaration",
            "Parameters : ParameterDeclaration COMMA Parameters",
            "Parameters :",
            "ParameterDeclaration : ModifiablePrimary COLON Type",
            "Type : INTEGER",
            "Type : REAL",
            "Type : BOOLEAN",
            "Body : Statements",
            "Statements : Statement Statements",
            "Statements :",
            "Statement : Assignment",
            "Statement : RoutineCall",
            "Statement : WhileLoop",
            "Statement : ForLoop",
            "Statement : IfStatement",
            "Statement : VariableDeclaration",
            "Assignment : ModifiablePrimary WALRUS Expression",
            "RoutineCall : ModifiablePrimary LEFT_PAREN Expressions RIGHT_PAREN",
            "Expressions : Expression",
            "Expressions : Expression COMMA Expressions",
            "WhileLoop : WhileKeyword Expression LOOP Body END",
            "WhileKeyword : WHILE",
            "ForKeyword : FOR",
            "IfKeyword : IF",
            "ElseKeyword : ELSE",
            "RoutineKeyword : ROUTINE",
            "ForLoop : ForKeyword ModifiablePrimary Range LOOP Body END",
            "Range : IN Expression DOT_DOT Expression",
            "Range : IN REVERSE Expression DOT_DOT Expression",
            "IfStatement : IfKeyword Expression THEN Body END",
            "IfStatement : IfKeyword Expression THEN Body ElseKeyword Body END",
            "Expression : Relation AND Expression",
            "Expression : Relation OR Expression",
            "Expression : Relation XOR Expression",
            "Expression : Relation",
            "Relation : Simple LESS Relation",
            "Relation : Simple LESS_EQUAL Relation",
            "Relation : Simple GREATER Relation",
            "Relation : Simple GREATER_EQUAL Relation",
            "Relation : Simple EQUAL Relation",
            "Relation : Simple SLASH_EQUAL Relation",
            "Relation : Simple",
            "Simple : Factor STAR Simple",
            "Simple : Factor SLASH Simple",
            "Simple : Factor PERCENT Simple",
            "Simple : Factor",
            "Factor : Summand PLUS Factor",
            "Factor : Summand MINUS Factor",
            "Factor : Summand",
            "Summand : Primary",
            "Summand : LEFT_PAREN Expression RIGHT_PAREN",
            "Primary : TYPE_REAL",
            "Primary : TYPE_INTEGER",
            "Primary : TRUE",
            "Primary : FALSE",
            "Primary : ModifiablePrimary",
            "ModifiablePrimary : IDENTIFIER",
    };

    //#line 206 "yacc.y"
    Stack<List<Node>> blockStack = new Stack<>();


    List<Token> tokens;
    List<Float> floatValues = new ArrayList<>();
    int tokenPointer = 0;
    Node root = new Node("root", null, new LinkedList<>());


    void setTokens(List<Token> tokens) {
        this.tokens = tokens;
        tokenPointer = 0;
    }

    private void yyerror(String syntax_error) {
        System.out.println("Error: " + syntax_error);
    }

    private int yylex() {
        if (tokenPointer == tokens.size())
            return -1;
        Token token = tokens.get(tokenPointer);
        switch (token.type) {
            case TYPE_INTEGER -> this.yylval = new ParserVal(Integer.valueOf(token.lexeme).intValue());
            case TYPE_REAL ->  {
                this.yylval = new ParserVal(Double.valueOf(token.lexeme).doubleValue());
                floatValues.add(this.yylval.dval.floatValue());
            }
            case TYPE_BOOLEAN -> this.yylval = new ParserVal(Boolean.valueOf(token.lexeme).booleanValue());
            case IDENTIFIER -> this.yylval = new ParserVal(token.lexeme);
            default -> this.yylval = new ParserVal(token.lexeme);
        }
        tokenPointer++;
        return tokens.get(tokenPointer - 1).TokenTypeToInt();
    }

    //#line 431 "Parser.java"
//###############################################################
// method: yylexdebug : check lexer state
//###############################################################
    void yylexdebug(int state, int ch) {
        String s = null;
        if (ch < 0) ch = 0;
        if (ch <= YYMAXTOKEN) //check index bounds
            s = yyname[ch];    //now get it
        if (s == null)
            s = "illegal-symbol";
        debug("state " + state + ", reading " + ch + " (" + s + ")");
    }


    //The following are now global, to aid in error reporting
    int yyn;       //next next thing to do
    int yym;       //
    int yystate;   //current parsing state from state table
    String yys;    //current token string


    //###############################################################
// method: yyparse : parse input and execute indicated items
//###############################################################
    int yyparse() {
        boolean doaction;
        init_stacks();
        yynerrs = 0;
        yyerrflag = 0;
        yychar = -1;          //impossible char forces a read
        yystate = 0;            //initial state
        state_push(yystate);  //save it
        val_push(yylval);     //save empty value
        while (true) //until parsing is done, either correctly, or w/error
        {
            doaction = true;
            if (yydebug) debug("loop");
            //#### NEXT ACTION (from reduction table)
            for (yyn = yydefred[yystate]; yyn == 0; yyn = yydefred[yystate]) {
                if (yydebug) debug("yyn:" + yyn + "  state:" + yystate + "  yychar:" + yychar);
                if (yychar < 0)      //we want a char?
                {
                    yychar = yylex();  //get next token
                    if (yydebug) debug(" next yychar:" + yychar);
                    //#### ERROR CHECK ####
                    if (yychar < 0)    //it it didn't work/error
                    {
                        yychar = 0;      //change it to default string (no -1!)
                        if (yydebug)
                            yylexdebug(yystate, yychar);
                    }
                }//yychar<0
                yyn = yysindex[yystate];  //get amount to shift by (shift index)
                if ((yyn != 0) && (yyn += yychar) >= 0 &&
                        yyn <= YYTABLESIZE && yycheck[yyn] == yychar) {
                    if (yydebug)
                        debug("state " + yystate + ", shifting to state " + yytable[yyn]);
                    //#### NEXT STATE ####
                    yystate = yytable[yyn];//we are in a new state
                    state_push(yystate);   //save it
                    val_push(yylval);      //push our lval as the input for next rule
                    yychar = -1;           //since we have 'eaten' a token, say we need another
                    if (yyerrflag > 0)     //have we recovered an error?
                        --yyerrflag;        //give ourselves credit
                    doaction = false;        //but don't process yet
                    break;   //quit the yyn=0 loop
                }

                yyn = yyrindex[yystate];  //reduce
                if ((yyn != 0) && (yyn += yychar) >= 0 &&
                        yyn <= YYTABLESIZE && yycheck[yyn] == yychar) {   //we reduced!
                    if (yydebug) debug("reduce");
                    yyn = yytable[yyn];
                    doaction = true; //get ready to execute
                    break;         //drop down to actions
                } else //ERROR RECOVERY
                {
                    if (yyerrflag == 0) {
                        yyerror("syntax error");
                        yynerrs++;
                    }
                    if (yyerrflag < 3) //low error count?
                    {
                        yyerrflag = 3;
                        while (true)   //do until break
                        {
                            if (stateptr < 0)   //check for under & overflow here
                            {
                                yyerror("stack underflow. aborting...");  //note lower case 's'
                                return 1;
                            }
                            yyn = yysindex[state_peek(0)];
                            if ((yyn != 0) && (yyn += YYERRCODE) >= 0 &&
                                    yyn <= YYTABLESIZE && yycheck[yyn] == YYERRCODE) {
                                if (yydebug)
                                    debug("state " + state_peek(0) + ", error recovery shifting to state " + yytable[yyn] + " ");
                                yystate = yytable[yyn];
                                state_push(yystate);
                                val_push(yylval);
                                doaction = false;
                                break;
                            } else {
                                if (yydebug)
                                    debug("error recovery discarding state " + state_peek(0) + " ");
                                if (stateptr < 0)   //check for under & overflow here
                                {
                                    yyerror("Stack underflow. aborting...");  //capital 'S'
                                    return 1;
                                }
                                state_pop();
                                val_pop();
                            }
                        }
                    } else            //discard this token
                    {
                        if (yychar == 0)
                            return 1; //yyabort
                        if (yydebug) {
                            yys = null;
                            if (yychar <= YYMAXTOKEN) yys = yyname[yychar];
                            if (yys == null) yys = "illegal-symbol";
                            debug("state " + yystate + ", error recovery discards token " + yychar + " (" + yys + ")");
                        }
                        yychar = -1;  //read another
                    }
                }//end error recovery
            }//yyn=0 loop
            if (!doaction)   //any reason not to proceed?
                continue;      //skip action
            yym = yylen[yyn];          //get count of terminals on rhs
            if (yydebug)
                debug("state " + yystate + ", reducing " + yym + " by rule " + yyn + " (" + yyrule[yyn] + ")");
            if (yym > 0)                 //if count of rhs not 'nil'
                yyval = val_peek(yym - 1); //get current semantic value
            yyval = dup_yyval(yyval); //duplicate yyval if ParserVal is used as semantic value
            switch (yyn) {
//########## USER-SUPPLIED ACTIONS ##########
                case 6:
//#line 76 "yacc.y"
                {
                    root.descendants.add(val_peek(0).obj);
                }
                break;
                case 7:
//#line 77 "yacc.y"
                {
                    root.descendants.add(val_peek(0).obj);
                }
                break;
                case 8:
//#line 81 "yacc.y"
                {
                    yyval = new ParserVal(new Node("variable-declaration", null, Arrays.asList(val_peek(2).obj, val_peek(0).obj)));
                }
                break;
                case 9:
//#line 82 "yacc.y"
                {
                    yyval = new ParserVal(new Node("variable-declaration", null, Arrays.asList(val_peek(4).obj, val_peek(2).obj, val_peek(0).obj)));
                }
                break;
                case 10:
//#line 83 "yacc.y"
                {
                    yyval = new ParserVal(new Node("variable-declaration", null, Arrays.asList(val_peek(2).obj, val_peek(0).obj)));
                }
                break;
                case 11:
//#line 87 "yacc.y"
                {
                    yyval = new ParserVal(new Node("routine-declaration", null, Arrays.asList(val_peek(6).obj, val_peek(4).obj, val_peek(1).obj)));
                }
                break;
                case 12:
//#line 88 "yacc.y"
                {
                    yyval = new ParserVal(new Node("routine-declaration", null, Arrays.asList(val_peek(8).obj, val_peek(6).obj, val_peek(3).obj, val_peek(1).obj)));
                }
                break;
                case 13:
//#line 92 "yacc.y"
                {
                    yyval = val_peek(0);
                }
                break;
                case 14:
//#line 93 "yacc.y"
                {
                    ((Node) val_peek(0).obj).descendants.add(val_peek(2).obj);
                    yyval = val_peek(0);
                }
                break;
                case 15:
//#line 94 "yacc.y"
                {
                    yyval = new ParserVal(new Node("parameters", null));
                }
                break;
                case 16:
//#line 98 "yacc.y"
                {
                    yyval = new ParserVal(new Node("parameter-declaration", null, Arrays.asList(val_peek(2).obj, val_peek(0).obj)));
                }
                break;
                case 17:
//#line 102 "yacc.y"
                {
                    yyval = new ParserVal(new Node("type-integer", null));
                }
                break;
                case 18:
//#line 103 "yacc.y"
                {
                    yyval = new ParserVal(new Node("type-real", null));
                }
                break;
                case 19:
//#line 104 "yacc.y"
                {
                    yyval = new ParserVal(new Node("type-boolean", null));
                }
                break;
                case 20:
//#line 108 "yacc.y"
                {
                    yyval = new ParserVal(new Node("body", null, blockStack.peek()));
                    blockStack.pop();
                }
                break;
                case 23:
//#line 116 "yacc.y"
                {
                    yyval = val_peek(0);
                    blockStack.peek().add(val_peek(0).obj);
                }
                break;
                case 24:
//#line 117 "yacc.y"
                {
                    yyval = val_peek(0);
                    blockStack.peek().add(val_peek(0).obj);
                }
                break;
                case 25:
//#line 118 "yacc.y"
                {
                    yyval = val_peek(0);
                    blockStack.peek().add(val_peek(0).obj);
                }
                break;
                case 26:
//#line 119 "yacc.y"
                {
                    yyval = val_peek(0);
                    blockStack.peek().add(val_peek(0).obj);
                }
                break;
                case 27:
//#line 120 "yacc.y"
                {
                    yyval = val_peek(0);
                    blockStack.peek().add(val_peek(0).obj);
                }
                break;
                case 28:
//#line 121 "yacc.y"
                {
                    yyval = val_peek(0);
                    blockStack.peek().add(val_peek(0).obj);
                }
                break;
                case 29:
//#line 125 "yacc.y"
                {
                    yyval = new ParserVal(new Node("assignment", null, Arrays.asList(val_peek(2).obj, val_peek(0).obj)));
                }
                break;
                case 30:
//#line 129 "yacc.y"
                {
                    yyval = new ParserVal(new Node("routine-call", null, Arrays.asList(val_peek(3).obj, val_peek(1).obj)));
                }
                break;
                case 31:
//#line 132 "yacc.y"
                {
                    yyval = val_peek(0);
                }
                break;
                case 32:
//#line 133 "yacc.y"
                {
                    ((Node) val_peek(0).obj).descendants.add(val_peek(2).obj);
                    yyval = val_peek(0);
                }
                break;
                case 33:
//#line 137 "yacc.y"
                {
                    yyval = new ParserVal(new Node("while", null, Arrays.asList(val_peek(3).obj, val_peek(1).obj)));
                }
                break;
                case 34:
//#line 140 "yacc.y"
                {
                    blockStack.push(new LinkedList<>());
                }
                break;
                case 35:
//#line 141 "yacc.y"
                {
                    blockStack.push(new LinkedList<>());
                }
                break;
                case 36:
//#line 142 "yacc.y"
                {
                    blockStack.push(new LinkedList<>());
                }
                break;
                case 37:
//#line 143 "yacc.y"
                {
                    blockStack.push(new LinkedList<>());
                }
                break;
                case 38:
//#line 144 "yacc.y"
                {
                    blockStack.push(new LinkedList<>());
                }
                break;
                case 39:
//#line 147 "yacc.y"
                {
                    yyval = new ParserVal(new Node("for", null, Arrays.asList(val_peek(4).obj, val_peek(3).obj, val_peek(1).obj)));
                }
                break;
                case 40:
//#line 151 "yacc.y"
                {
                    yyval = new ParserVal(new Node("range", null, Arrays.asList(val_peek(2).obj, val_peek(0).obj)));
                }
                break;
                case 41:
//#line 152 "yacc.y"
                {
                    yyval = new ParserVal(new Node("range-reverse", null, Arrays.asList(val_peek(2).obj, val_peek(0).obj)));
                }
                break;
                case 42:
//#line 156 "yacc.y"
                {
                    yyval = new ParserVal(new Node("if", null, Arrays.asList(val_peek(3).obj, val_peek(1).obj)));
                }
                break;
                case 43:
//#line 157 "yacc.y"
                {
                    yyval = new ParserVal(new Node("if", null, Arrays.asList(val_peek(5).obj, val_peek(3).obj, val_peek(1).obj)));
                }
                break;
                case 44:
//#line 161 "yacc.y"
                {
                    yyval = new ParserVal(new Node("and", null, Arrays.asList(val_peek(2).obj, val_peek(0).obj)));
                }
                break;
                case 45:
//#line 162 "yacc.y"
                {
                    yyval = new ParserVal(new Node("or", null, Arrays.asList(val_peek(2).obj, val_peek(0).obj)));
                }
                break;
                case 46:
//#line 163 "yacc.y"
                {
                    yyval = new ParserVal(new Node("xor", null, Arrays.asList(val_peek(2).obj, val_peek(0).obj)));
                }
                break;
                case 47:
//#line 164 "yacc.y"
                {
                    yyval = val_peek(0);
                }
                break;
                case 48:
//#line 168 "yacc.y"
                {
                    yyval = new ParserVal(new Node("less", null, Arrays.asList(val_peek(2).obj, val_peek(0).obj)));
                }
                break;
                case 49:
//#line 169 "yacc.y"
                {
                    yyval = new ParserVal(new Node("less or equal", null, Arrays.asList(val_peek(2).obj, val_peek(0).obj)));
                }
                break;
                case 50:
//#line 170 "yacc.y"
                {
                    yyval = new ParserVal(new Node("more", null, Arrays.asList(val_peek(2).obj, val_peek(0).obj)));
                }
                break;
                case 51:
//#line 171 "yacc.y"
                {
                    yyval = new ParserVal(new Node("more or equal", null, Arrays.asList(val_peek(2).obj, val_peek(0).obj)));
                }
                break;
                case 52:
//#line 172 "yacc.y"
                {
                    yyval = new ParserVal(new Node("equal", null, Arrays.asList(val_peek(2).obj, val_peek(0).obj)));
                }
                break;
                case 53:
//#line 173 "yacc.y"
                {
                    yyval = new ParserVal(new Node("not equal", null, Arrays.asList(val_peek(2).obj, val_peek(0).obj)));
                }
                break;
                case 54:
//#line 174 "yacc.y"
                {
                    yyval = val_peek(0);
                }
                break;
                case 55:
//#line 178 "yacc.y"
                {
                    yyval = new ParserVal(new Node("multiply", null, Arrays.asList(val_peek(2).obj, val_peek(0).obj)));
                }
                break;
                case 56:
//#line 179 "yacc.y"
                {
                    yyval = new ParserVal(new Node("divide", null, Arrays.asList(val_peek(2).obj, val_peek(0).obj)));
                }
                break;
                case 57:
//#line 180 "yacc.y"
                {
                    yyval = new ParserVal(new Node("percent", null, Arrays.asList(val_peek(2).obj, val_peek(0).obj)));
                }
                break;
                case 58:
//#line 181 "yacc.y"
                {
                    yyval = val_peek(0);
                }
                break;
                case 59:
//#line 185 "yacc.y"
                {
                    yyval = new ParserVal(new Node("plus", null, Arrays.asList(val_peek(2).obj, val_peek(0).obj)));
                }
                break;
                case 60:
//#line 186 "yacc.y"
                {
                    yyval = new ParserVal(new Node("minus", null, Arrays.asList(val_peek(2).obj, val_peek(0).obj)));
                }
                break;
                case 61:
//#line 187 "yacc.y"
                {
                    yyval = val_peek(0);
                }
                break;
                case 62:
//#line 190 "yacc.y"
                {
                    yyval = val_peek(0);
                }
                break;
                case 63:
//#line 191 "yacc.y"
                {
                    yyval = new ParserVal(new Node("summand", null, Arrays.asList(val_peek(1).obj)));
                }
                break;
                case 64:
//#line 195 "yacc.y"
                {
                    yyval = new ParserVal(new Node("realValue", Double.valueOf(val_peek(0).dval).floatValue()));
                }
                break;
                case 65:
//#line 196 "yacc.y"
                {
                    yyval = new ParserVal(new Node("intValue", Integer.valueOf(val_peek(0).ival)));
                }
                break;
                case 66:
//#line 197 "yacc.y"
                {
                    yyval = new ParserVal(new Node("boolValue", Boolean.valueOf(true)));
                }
                break;
                case 67:
//#line 198 "yacc.y"
                {
                    yyval = new ParserVal(new Node("boolValue", Boolean.valueOf(false)));
                }
                break;
                case 68:
//#line 199 "yacc.y"
                {
                    yyval = val_peek(0);
                }
                break;
                case 69:
//#line 202 "yacc.y"
                {
                    yyval = new ParserVal(new Node(yylval.sval, null));
                }
                break;
//#line 828 "Parser.java"
//########## END OF USER-SUPPLIED ACTIONS ##########
            }//switch
            //#### Now let's reduce... ####
            if (yydebug) debug("reduce");
            state_drop(yym);             //we just reduced yylen states
            yystate = state_peek(0);     //get new state
            val_drop(yym);               //corresponding value drop
            yym = yylhs[yyn];            //select next TERMINAL(on lhs)
            if (yystate == 0 && yym == 0)//done? 'rest' state and at first TERMINAL
            {
                if (yydebug) debug("After reduction, shifting from state 0 to state " + YYFINAL + "");
                yystate = YYFINAL;         //explicitly say we're done
                state_push(YYFINAL);       //and save it
                val_push(yyval);           //also save the semantic value of parsing
                if (yychar < 0)            //we want another character?
                {
                    yychar = yylex();        //get next character
                    if (yychar < 0) yychar = 0;  //clean, if necessary
                    if (yydebug)
                        yylexdebug(yystate, yychar);
                }
                if (yychar == 0)          //Good exit (if lex returns 0 ;-)
                    break;                 //quit the loop--all DONE
            }//if yystate
            else                        //else not done yet
            {                         //get next state and push, for next yydefred[]
                yyn = yygindex[yym];      //find out where to go
                if ((yyn != 0) && (yyn += yystate) >= 0 &&
                        yyn <= YYTABLESIZE && yycheck[yyn] == yystate)
                    yystate = yytable[yyn]; //get new state
                else
                    yystate = yydgoto[yym]; //else go to new defred
                if (yydebug)
                    debug("after reduction, shifting from state " + state_peek(0) + " to state " + yystate + "");
                state_push(yystate);     //going again, so push state & val...
                val_push(yyval);         //for next action
            }
        }//main loop
        return 0;//yyaccept!!
    }
//## end of method parse() ######################################


//## run() --- for Thread #######################################

    /**
     * A default run method, used for operating this parser
     * object in the background.  It is intended for extending Thread
     * or implementing Runnable.  Turn off with -Jnorun .
     */
    public void run() {
        yyparse();
        System.out.println(root.toString());
    }
//## end of method run() ########################################


//## Constructors ###############################################

    /**
     * Default constructor.  Turn off with -Jnoconstruct .
     */
    public Parser() {
        //nothing to do
    }


    /**
     * Create a parser, setting the debug to true or false.
     *
     * @param debugMe true for debugging, false for no debug.
     */
    public Parser(boolean debugMe) {
        yydebug = debugMe;
    }
//###############################################################


}
//################### END OF CLASS ##############################
