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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
//#line 23 "Parser.java"




public class Parser
{

boolean yydebug;        //do I want debug output?
int yynerrs;            //number of errors so far
int yyerrflag;          //was there an error?
int yychar;             //the current working character

//########## MESSAGES ##########
//###############################################################
// method: debug
//###############################################################
void debug(String msg)
{
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
final void state_push(int state)
{
  try {
		stateptr++;
		statestk[stateptr]=state;
	 }
	 catch (ArrayIndexOutOfBoundsException e) {
     int oldsize = statestk.length;
     int newsize = oldsize * 2;
     int[] newstack = new int[newsize];
     System.arraycopy(statestk,0,newstack,0,oldsize);
     statestk = newstack;
     statestk[stateptr]=state;
  }
}
final int state_pop()
{
  return statestk[stateptr--];
}
final void state_drop(int cnt)
{
  stateptr -= cnt; 
}
final int state_peek(int relative)
{
  return statestk[stateptr-relative];
}
//###############################################################
// method: init_stacks : allocate and prepare stacks
//###############################################################
final boolean init_stacks()
{
  stateptr = -1;
  val_init();
  return true;
}
//###############################################################
// method: dump_stacks : show n levels of the stacks
//###############################################################
void dump_stacks(int count)
{
int i;
  System.out.println("=index==state====value=     s:"+stateptr+"  v:"+valptr);
  for (i=0;i<count;i++)
    System.out.println(" "+i+"    "+statestk[i]+"      "+valstk[i]);
  System.out.println("======================");
}


//########## SEMANTIC VALUES ##########
//public class ParserVal is defined in ParserVal.java


String   yytext;//user variable to return contextual strings
ParserVal yyval; //used to return semantic vals from action routines
ParserVal yylval;//the 'lval' (result) I got from yylex()
ParserVal valstk[];
int valptr;
//###############################################################
// methods: value stack push,pop,drop,peek.
//###############################################################
void val_init()
{
  valstk=new ParserVal[YYSTACKSIZE];
  yyval=new ParserVal();
  yylval=new ParserVal();
  valptr=-1;
}
void val_push(ParserVal val)
{
  if (valptr>=YYSTACKSIZE)
    return;
  valstk[++valptr]=val;
}
ParserVal val_pop()
{
  if (valptr<0)
    return new ParserVal();
  return valstk[valptr--];
}
void val_drop(int cnt)
{
int ptr;
  ptr=valptr-cnt;
  if (ptr<0)
    return;
  valptr = ptr;
}
ParserVal val_peek(int relative)
{
int ptr;
  ptr=valptr-relative;
  if (ptr<0)
    return new ParserVal();
  return valstk[ptr];
}
final ParserVal dup_yyval(ParserVal val)
{
  ParserVal dup = new ParserVal();
  dup.ival = val.ival;
  dup.dval = val.dval;
  dup.sval = val.sval;
  dup.obj = val.obj;
  return dup;
}
//#### end semantic value section ####
public final static short LEFT_PAREN=257;
public final static short RIGHT_PAREN=258;
public final static short LEFT_BRACE=259;
public final static short LEFT_SQUARE_BRACE=260;
public final static short RIGHT_SQUARE_BRACE=261;
public final static short RIGHT_BRACE=262;
public final static short COMMA=263;
public final static short DOT=264;
public final static short MINUS=265;
public final static short PLUS=266;
public final static short SLASH=267;
public final static short STAR=268;
public final static short PERCENT=269;
public final static short EQUAL=270;
public final static short EQUAL_EQUAL=271;
public final static short SLASH_EQUAL=272;
public final static short GREATER=273;
public final static short GREATER_EQUAL=274;
public final static short LESS=275;
public final static short LESS_EQUAL=276;
public final static short DOT_DOT=277;
public final static short IDENTIFIER=278;
public final static short SEMICOLON=279;
public final static short COLON=280;
public final static short TYPE_BOOLEAN=281;
public final static short TYPE_INTEGER=282;
public final static short TYPE_REAL=283;
public final static short TYPE_RECORD=284;
public final static short TYPE_ARRAY=285;
public final static short TYPE_STRING=286;
public final static short AND=287;
public final static short ARRAY=288;
public final static short BOOLEAN=289;
public final static short ELSE=290;
public final static short END=291;
public final static short FALSE=292;
public final static short FOR=293;
public final static short IF=294;
public final static short IN=295;
public final static short INTEGER=296;
public final static short IS=297;
public final static short LOOP=298;
public final static short NOT=299;
public final static short OR=300;
public final static short PRINT=301;
public final static short REAL=302;
public final static short RECORD=303;
public final static short RETURN=304;
public final static short REVERSE=305;
public final static short ROUTINE=306;
public final static short THEN=307;
public final static short TRUE=308;
public final static short TYPE=309;
public final static short VAR=310;
public final static short WHILE=311;
public final static short XOR=312;
public final static short STRING=313;
public final static short WALRUS=314;
public final static short EOF=315;
public final static short YYERRCODE=256;
final static short yylhs[] = {                           -1,
    0,    0,    1,    1,    1,    2,    2,    4,    4,    8,
    5,    5,    5,    5,   10,    6,    6,   11,   11,   11,
    9,    9,   13,   13,   13,   13,   13,   13,   13,   13,
   13,    3,    3,    3,    7,    7,    7,   12,   14,
};
final static short yylen[] = {                            2,
    0,    2,    0,    1,    2,    1,    1,    8,   10,    4,
    0,    1,    3,    3,    3,    0,    2,    1,    1,    1,
    0,    2,    1,    1,    1,    3,    1,    3,    3,    3,
    3,    4,    6,    4,    1,    1,    1,    3,    1,
};
final static short yydefred[] = {                         0,
    0,    0,    0,    0,    0,    6,    7,    0,    0,    2,
    5,    0,    0,    0,    0,    0,    0,    0,   35,   36,
   37,    0,    0,   39,   24,   23,   25,    0,   27,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,   14,
   15,    0,    0,   13,    0,   31,    0,    0,   30,    0,
    0,    0,   18,    0,   20,    0,   19,    0,    0,    0,
    8,   17,    0,    0,    0,    0,    0,    9,   10,   22,
};
final static short yydgoto[] = {                          3,
    4,    5,   53,    7,   17,   54,   22,   55,   65,   18,
   56,   57,   66,   29,
};
final static short yysindex[] = {                      -261,
 -278, -272,    0, -295, -261,    0,    0, -227, -209,    0,
    0, -211, -194, -200, -211, -224, -190, -210,    0,    0,
    0, -222, -200,    0,    0,    0,    0, -145,    0, -174,
 -194, -191, -211, -200, -149, -200, -200, -200, -200,    0,
    0, -194, -266,    0, -145,    0, -141, -168,    0, -175,
 -186, -152,    0, -167,    0, -266,    0, -183, -266, -200,
    0,    0, -200, -159, -129, -153, -145,    0,    0,    0,
};
final static short yyrindex[] = {                         1,
    0,    0,    0,    0, -181,    0,    0,    0,    0,    0,
    0, -123,    0,    0, -123,    0,    0, -122,    0,    0,
    0, -230,    0,    0,    0,    0,    0, -212,    0,    0,
    0,    0, -123,    0,    0,    0,    0,    0,    0,    0,
    0,    0, -154,    0, -205,    0, -241, -219,    0, -255,
    0, -176,    0,    0,    0, -154,    0,    0, -154, -121,
    0,    0,    0,    0,    0, -121, -256,    0,    0,    0,
};
final static short yygindex[] = {                         0,
  134,    0,   14,    0,   37,  -51,  -27,    0,   74,    0,
    0,    0,   -5,   34,
};
final static int YYTABLESIZE=316;
static short yytable[];
static { yytable();}
static void yytable(){
yytable = new short[]{                          8,
    1,   29,   29,   41,   62,    9,   29,   64,   28,   29,
   29,   52,   29,    6,   51,   28,   28,   35,    6,   10,
   28,   38,   29,   28,   28,   29,   29,   29,   45,   12,
   47,   48,   49,   50,   38,   29,   28,   26,   26,   28,
   28,   28,   26,    2,    1,   15,   26,   32,    2,   28,
   29,   30,   33,   38,   29,   31,   23,   67,   26,   29,
   32,   26,   26,   26,   28,   34,   16,   32,   28,   44,
   13,   26,   33,   28,   34,   32,   58,   24,   34,   32,
   25,   26,   27,   40,   32,   33,   26,   14,   42,   58,
   26,   38,   58,   34,   19,   26,   36,   34,   38,   39,
   33,   20,   34,   23,   33,   43,   60,   21,   46,   33,
   59,   36,   37,   38,   39,   36,   37,   38,   39,   36,
   37,   38,   39,   61,   24,   38,   39,   25,   26,   27,
   63,   68,   69,    3,   11,   12,   16,   39,   11,   70,
   21,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    3,
};
}
static short yycheck[];
static { yycheck(); }
static void yycheck() {
yycheck = new short[] {                        278,
    0,  257,  258,   31,   56,  278,  262,   59,   14,  265,
  266,  278,  268,    0,   42,  257,  258,   23,    5,  315,
  262,  278,  278,  265,  266,  281,  282,  283,   34,  257,
   36,   37,   38,   39,  291,  291,  278,  257,  258,  281,
  282,  283,  262,  310,  306,  257,  266,  278,  310,  291,
  306,   15,  263,  310,  310,  280,  257,   63,  278,  315,
  291,  281,  282,  283,  306,  278,  278,  258,  310,   33,
  280,  291,  278,  315,  297,  306,   43,  278,  291,  310,
  281,  282,  283,  258,  315,  291,  306,  297,  280,   56,
  310,  267,   59,  306,  289,  315,  265,  310,  267,  268,
  306,  296,  315,  257,  310,  297,  259,  302,  258,  315,
  297,  265,  266,  267,  268,  265,  266,  267,  268,  265,
  266,  267,  268,  291,  278,  267,  268,  281,  282,  283,
  314,  291,  262,  315,  258,  258,  291,  314,    5,   66,
  262,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,  315,
};
}
final static short YYFINAL=3;
final static short YYMAXTOKEN=315;
final static String yyname[] = {
"end-of-file",null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,"LEFT_PAREN","RIGHT_PAREN","LEFT_BRACE","LEFT_SQUARE_BRACE",
"RIGHT_SQUARE_BRACE","RIGHT_BRACE","COMMA","DOT","MINUS","PLUS","SLASH","STAR",
"PERCENT","EQUAL","EQUAL_EQUAL","SLASH_EQUAL","GREATER","GREATER_EQUAL","LESS",
"LESS_EQUAL","DOT_DOT","IDENTIFIER","SEMICOLON","COLON","TYPE_BOOLEAN",
"TYPE_INTEGER","TYPE_REAL","TYPE_RECORD","TYPE_ARRAY","TYPE_STRING","AND",
"ARRAY","BOOLEAN","ELSE","END","FALSE","FOR","IF","IN","INTEGER","IS","LOOP",
"NOT","OR","PRINT","REAL","RECORD","RETURN","REVERSE","ROUTINE","THEN","TRUE",
"TYPE","VAR","WHILE","XOR","STRING","WALRUS","EOF",
};
final static String yyrule[] = {
"$accept : Program",
"Program :",
"Program : Lines EOF",
"Lines :",
"Lines : Line",
"Lines : Line Lines",
"Line : VariableDeclaration",
"Line : RoutineDeclaration",
"RoutineDeclaration : ROUTINE IDENTIFIER LEFT_PAREN Parameters RIGHT_PAREN IS Body END",
"RoutineDeclaration : ROUTINE IDENTIFIER LEFT_PAREN Parameters RIGHT_PAREN COLON Type IS Body END",
"RoutineCall : IDENTIFIER LEFT_BRACE Expressions RIGHT_BRACE",
"Parameters :",
"Parameters : Parameter",
"Parameters : Parameter COMMA Parameters",
"Parameters : LEFT_PAREN Parameters RIGHT_PAREN",
"Parameter : IDENTIFIER COLON Type",
"Body :",
"Body : Statement Body",
"Statement : VariableDeclaration",
"Statement : Assignment",
"Statement : RoutineCall",
"Expressions :",
"Expressions : Expression Expressions",
"Expression : TYPE_INTEGER",
"Expression : TYPE_BOOLEAN",
"Expression : TYPE_REAL",
"Expression : Expression PLUS Expression",
"Expression : ModifiablePrimary",
"Expression : Expression MINUS Expression",
"Expression : Expression STAR Expression",
"Expression : Expression SLASH Expression",
"Expression : LEFT_PAREN Expression RIGHT_PAREN",
"VariableDeclaration : VAR IDENTIFIER COLON Type",
"VariableDeclaration : VAR IDENTIFIER COLON Type IS Expression",
"VariableDeclaration : VAR IDENTIFIER IS Expression",
"Type : BOOLEAN",
"Type : INTEGER",
"Type : REAL",
"Assignment : ModifiablePrimary WALRUS Expression",
"ModifiablePrimary : IDENTIFIER",
};

//#line 177 "yacc.y"

String currentString = "";

    List<Parameter> parameters = new LinkedList<>();
    List<Statement> statements = new LinkedList<>();
    Statement currentStatement = null;
    ParserValType currentType = ParserValType.DOUBLE;
    Map<String, Routine> routines = new HashMap<String, Routine>();
    Map<String, ParserVal> states = new HashMap<String, ParserVal>();

    List<Token> tokens;
    int tokenPointer = 0;

    public void setCurrentType(ParserValType type) {
        currentType = type;
    }

    public void declareRoutine(String identifier, List<Parameter> parameters, List<Statement> body) {
        System.out.println("Declare routine \"" + identifier + "\" and parameters: " + parameters);
        routines.put(identifier, new Routine(identifier, body));
    }

    public void declareRoutine(String identifier, List<Parameter> parameters, List<Statement> body, Routine.ReturnType returnType) {
        System.out.println("Declare routine \"" + identifier + "\" and parameters: " + parameters + " and " + returnType);
        routines.put(identifier, new Routine(identifier,body,returnType));
    }

    enum StatementType {
        VAR_DECLARATION,
        VAR_ASSIGNMENT,
        ROUTINE_CALL,
        WHILE_LOOP,
        FOR_LOOP,
        IF
    }

    class Statement {
        StatementType type;
        String identifier;
        List<String> params;

        public Statement(StatementType type, String identifier, List<String> params) {
            this.type = type;
            this.identifier = identifier;
            this.params = params;
        }
    }

    class Parameter {
        String identifier;
        ParserValType type;

        public Parameter(String identifier, ParserValType type)
        {
            this.identifier = identifier;
            this.type = type;
        }
    }

    class Routine
    {
        String identifier;
        List<Statement> statements;
        ReturnType returnType;

        public Routine(String identifier, List<Statement> statements, ReturnType returnType)
        {
            this.identifier = identifier;
            this.statements = statements;
            this.returnType = returnType;
        }

        public Routine(String identifier, List<Statement> statements)
        {
            this.identifier = identifier;
            this.statements = statements;
            this.returnType = ReturnType.VOID;
        }

        enum ReturnType
        {
            VOID,
            DOUBLE,
            INT,
            BOOL
        }

        public static ReturnType typeToReturnType(ParserValType type)
        {
            switch (type)
            {
                case INT -> { return ReturnType.INT;}
                case BOOL -> { return ReturnType.BOOL;}
                case DOUBLE -> { return ReturnType.DOUBLE;}
            }

            throw new IllegalStateException("Incorrect argument");
        }

    }

    public void initializeVariable(String name, ParserVal value) {
        states.put(name, value);
    }

    public void assignValue(String name, ParserVal newValue) {
        states.replace(name, newValue);
    }

    public ParserVal getValue(String name) {
        return states.get(name);
    }

    void setTokens(List<Token> tokens) {
        this.tokens = tokens;
        tokenPointer = 0;
    }

    private void yyerror(String syntax_error) {
    }

    public ParserVal convertTypes(ParserVal left, ParserVal right) {
        ParserValType leftType = left.type;
        ParserValType rightType = right.type;
        if (leftType == ParserValType.INT && rightType == ParserValType.INT) {
            return new ParserVal(right.ival.intValue());
        }


        if (leftType == ParserValType.INT && rightType == ParserValType.DOUBLE)
            return new ParserVal(Math.round(right.dval));

        if (leftType == ParserValType.INT && rightType == ParserValType.BOOL)
            return new ParserVal(boolToInt(right.bval));

        if (leftType == ParserValType.DOUBLE && rightType == ParserValType.DOUBLE)
            return new ParserVal(right.dval.doubleValue());

        if (leftType == ParserValType.DOUBLE && rightType == ParserValType.INT)
            return new ParserVal(Double.valueOf(right.ival));

        if (leftType == ParserValType.DOUBLE && rightType == ParserValType.BOOL)
            return new ParserVal(boolToDouble(right.bval));

        if (leftType == ParserValType.BOOL && rightType == ParserValType.BOOL)
            return new ParserVal(right.bval.booleanValue());

        if (leftType == ParserValType.BOOL && rightType == ParserValType.INT) {
            if (right.ival == 1)
                return new ParserVal(true);
            else if (right.ival == 0)
                return new ParserVal(false);
            else
                throw new IllegalArgumentException("Integer which is not 0 or 1 cannot be converted to boolean");
        }

        if (leftType == ParserValType.BOOL && rightType == ParserValType.DOUBLE) {
            throw new IllegalArgumentException("Real cannot be converted to boolean");
        }

        throw new IllegalStateException("One or both arguments have incorrect value");
    }

    public int boolToInt(boolean val) {
        return val == true ? 1 : 0;
    }

    public double boolToDouble(boolean val) {
        return val == true ? 1.0 : 0.0;
    }

    public ParserVal sum(ParserVal left, ParserVal right) {
        ParserVal rightConverted = convertTypes(left, right);
        if (left.type == ParserValType.INT)
            return new ParserVal(left.ival + rightConverted.ival);
        if (left.type == ParserValType.DOUBLE)
            return new ParserVal(left.dval + rightConverted.dval);
        throw new IllegalArgumentException("Summation allowed only for integers and doubles");
    }

    public ParserVal substract(ParserVal left, ParserVal right) {
        ParserVal rightConverted = convertTypes(left, right);
        if (left.type == ParserValType.INT)
            return new ParserVal(left.ival - rightConverted.ival);
        if (left.type == ParserValType.DOUBLE)
            return new ParserVal(left.dval - rightConverted.dval);
        throw new IllegalArgumentException("Substraction allowed only for integers and doubles");
    }

    public ParserVal divide(ParserVal left, ParserVal right) {
        ParserVal rightConverted = convertTypes(left, right);
        if (left.type == ParserValType.INT)
            return new ParserVal(left.ival / rightConverted.ival);
        if (left.type == ParserValType.DOUBLE)
            return new ParserVal(left.dval / rightConverted.dval);
        throw new IllegalArgumentException("Division allowed only for integers and doubles");
    }

    public ParserVal multiply(ParserVal left, ParserVal right) {
        ParserVal rightConverted = convertTypes(left, right);
        if (left.type == ParserValType.INT)
            return new ParserVal(left.ival * rightConverted.ival);
        if (left.type == ParserValType.DOUBLE)
            return new ParserVal(left.dval * rightConverted.dval);
        throw new IllegalArgumentException("Multiplication allowed only for integers and doubles");
    }

    private int yylex() {
        if (tokenPointer == tokens.size())
            return -1;
        Token token = tokens.get(tokenPointer);
        switch (token.type) {
            case TYPE_INTEGER -> this.yylval = new ParserVal(Integer.valueOf(token.lexeme).intValue());
            case TYPE_REAL -> this.yylval = new ParserVal(Double.valueOf(token.lexeme).doubleValue());
            case TYPE_BOOLEAN -> this.yylval = new ParserVal(Boolean.valueOf(token.lexeme).booleanValue());
            case IDENTIFIER -> this.yylval = new ParserVal(token.lexeme);
            default -> this.yylval = new ParserVal(token.lexeme);
        }
        tokenPointer++;
        return tokens.get(tokenPointer - 1).TokenTypeToInt();
    }
//#line 569 "Parser.java"
//###############################################################
// method: yylexdebug : check lexer state
//###############################################################
void yylexdebug(int state,int ch)
{
String s=null;
  if (ch < 0) ch=0;
  if (ch <= YYMAXTOKEN) //check index bounds
     s = yyname[ch];    //now get it
  if (s==null)
    s = "illegal-symbol";
  debug("state "+state+", reading "+ch+" ("+s+")");
}





//The following are now global, to aid in error reporting
int yyn;       //next next thing to do
int yym;       //
int yystate;   //current parsing state from state table
String yys;    //current token string


//###############################################################
// method: yyparse : parse input and execute indicated items
//###############################################################
int yyparse()
{
boolean doaction;
  init_stacks();
  yynerrs = 0;
  yyerrflag = 0;
  yychar = -1;          //impossible char forces a read
  yystate=0;            //initial state
  state_push(yystate);  //save it
  val_push(yylval);     //save empty value
  while (true) //until parsing is done, either correctly, or w/error
    {
    doaction=true;
    if (yydebug) debug("loop"); 
    //#### NEXT ACTION (from reduction table)
    for (yyn=yydefred[yystate];yyn==0;yyn=yydefred[yystate])
      {
      if (yydebug) debug("yyn:"+yyn+"  state:"+yystate+"  yychar:"+yychar);
      if (yychar < 0)      //we want a char?
        {
        yychar = yylex();  //get next token
        if (yydebug) debug(" next yychar:"+yychar);
        //#### ERROR CHECK ####
        if (yychar < 0)    //it it didn't work/error
          {
          yychar = 0;      //change it to default string (no -1!)
          if (yydebug)
            yylexdebug(yystate,yychar);
          }
        }//yychar<0
      yyn = yysindex[yystate];  //get amount to shift by (shift index)
      if ((yyn != 0) && (yyn += yychar) >= 0 &&
          yyn <= YYTABLESIZE && yycheck[yyn] == yychar)
        {
        if (yydebug)
          debug("state "+yystate+", shifting to state "+yytable[yyn]);
        //#### NEXT STATE ####
        yystate = yytable[yyn];//we are in a new state
        state_push(yystate);   //save it
        val_push(yylval);      //push our lval as the input for next rule
        yychar = -1;           //since we have 'eaten' a token, say we need another
        if (yyerrflag > 0)     //have we recovered an error?
           --yyerrflag;        //give ourselves credit
        doaction=false;        //but don't process yet
        break;   //quit the yyn=0 loop
        }

    yyn = yyrindex[yystate];  //reduce
    if ((yyn !=0 ) && (yyn += yychar) >= 0 &&
            yyn <= YYTABLESIZE && yycheck[yyn] == yychar)
      {   //we reduced!
      if (yydebug) debug("reduce");
      yyn = yytable[yyn];
      doaction=true; //get ready to execute
      break;         //drop down to actions
      }
    else //ERROR RECOVERY
      {
      if (yyerrflag==0)
        {
        yyerror("syntax error");
        yynerrs++;
        }
      if (yyerrflag < 3) //low error count?
        {
        yyerrflag = 3;
        while (true)   //do until break
          {
          if (stateptr<0)   //check for under & overflow here
            {
            yyerror("stack underflow. aborting...");  //note lower case 's'
            return 1;
            }
          yyn = yysindex[state_peek(0)];
          if ((yyn != 0) && (yyn += YYERRCODE) >= 0 &&
                    yyn <= YYTABLESIZE && yycheck[yyn] == YYERRCODE)
            {
            if (yydebug)
              debug("state "+state_peek(0)+", error recovery shifting to state "+yytable[yyn]+" ");
            yystate = yytable[yyn];
            state_push(yystate);
            val_push(yylval);
            doaction=false;
            break;
            }
          else
            {
            if (yydebug)
              debug("error recovery discarding state "+state_peek(0)+" ");
            if (stateptr<0)   //check for under & overflow here
              {
              yyerror("Stack underflow. aborting...");  //capital 'S'
              return 1;
              }
            state_pop();
            val_pop();
            }
          }
        }
      else            //discard this token
        {
        if (yychar == 0)
          return 1; //yyabort
        if (yydebug)
          {
          yys = null;
          if (yychar <= YYMAXTOKEN) yys = yyname[yychar];
          if (yys == null) yys = "illegal-symbol";
          debug("state "+yystate+", error recovery discards token "+yychar+" ("+yys+")");
          }
        yychar = -1;  //read another
        }
      }//end error recovery
    }//yyn=0 loop
    if (!doaction)   //any reason not to proceed?
      continue;      //skip action
    yym = yylen[yyn];          //get count of terminals on rhs
    if (yydebug)
      debug("state "+yystate+", reducing "+yym+" by rule "+yyn+" ("+yyrule[yyn]+")");
    if (yym>0)                 //if count of rhs not 'nil'
      yyval = val_peek(yym-1); //get current semantic value
    yyval = dup_yyval(yyval); //duplicate yyval if ParserVal is used as semantic value
    switch(yyn)
      {
//########## USER-SUPPLIED ACTIONS ##########
case 2:
//#line 80 "yacc.y"
{System.out.println("END");}
break;
case 6:
//#line 88 "yacc.y"
{System.out.println("End declaring variable.");}
break;
case 8:
//#line 93 "yacc.y"
{declareRoutine(val_peek(6).sval, parameters, statements);
										statements = new LinkedList<>();
										parameters = new LinkedList<>();}
break;
case 9:
//#line 96 "yacc.y"
{declareRoutine(val_peek(8).sval, parameters, statements, Routine.typeToReturnType(currentType));
										statements = new LinkedList<>();
										parameters = new LinkedList<>();}
break;
case 10:
//#line 102 "yacc.y"
{	List<String> params = new LinkedList<>();
								params.add(val_peek(1).sval);
								currentStatement = new Statement(StatementType.ROUTINE_CALL,val_peek(3).sval, params);}
break;
case 15:
//#line 115 "yacc.y"
{parameters.add(new Parameter(val_peek(2).sval, currentType)); System.out.println("Decklare parameter");}
break;
case 17:
//#line 119 "yacc.y"
{statements.add(currentStatement); System.out.println("Add current statement to statements");}
break;
case 23:
//#line 133 "yacc.y"
{yyval = val_peek(0); System.out.println("int literal is: " + val_peek(0).ival);}
break;
case 24:
//#line 134 "yacc.y"
{yyval = new ParserVal(val_peek(0)); System.out.println("boolean literal is: " + val_peek(0).bval);}
break;
case 25:
//#line 135 "yacc.y"
{yyval = val_peek(0); System.out.println("real literal is: " + val_peek(0).dval);}
break;
case 26:
//#line 136 "yacc.y"
{yyval = sum(val_peek(2), val_peek(0)); System.out.println("Summation");}
break;
case 27:
//#line 137 "yacc.y"
{yyval = getValue(this.yylval.sval); System.out.println("Get value");}
break;
case 28:
//#line 138 "yacc.y"
{yyval = substract(val_peek(2),val_peek(0)); System.out.println("Substraction");}
break;
case 29:
//#line 139 "yacc.y"
{yyval = multiply(val_peek(2),val_peek(0)); System.out.println("Multiplication");}
break;
case 30:
//#line 140 "yacc.y"
{yyval = divide(val_peek(2),val_peek(0)); System.out.println("Division");}
break;
case 31:
//#line 141 "yacc.y"
{yyval = val_peek(1); System.out.println("Unpacking");}
break;
case 32:
//#line 148 "yacc.y"
{currentStatement = new Statement(StatementType.VAR_DECLARATION,val_peek(2).sval, new LinkedList<>() );}
break;
case 33:
//#line 149 "yacc.y"
{	System.out.println("Declaring variable with type and expression");
							List<String> params = new LinkedList<>();
							params.add(val_peek(0).sval);
							currentStatement = new Statement(StatementType.VAR_ASSIGNMENT,val_peek(4).sval, params);}
break;
case 34:
//#line 153 "yacc.y"
{		System.out.println("Declaring variable with expression");
							List<String> params = new LinkedList<>();
							params.add(val_peek(0).sval);	
							currentStatement = new Statement(StatementType.VAR_ASSIGNMENT,val_peek(2).sval, params);}
break;
case 35:
//#line 160 "yacc.y"
{ setCurrentType(ParserValType.BOOL);}
break;
case 36:
//#line 161 "yacc.y"
{ setCurrentType(ParserValType.INT);}
break;
case 37:
//#line 162 "yacc.y"
{setCurrentType(ParserValType.DOUBLE);}
break;
case 38:
//#line 167 "yacc.y"
{ List<String> params = new LinkedList<>();
						params.add(val_peek(0).sval);
						currentStatement = new Statement(StatementType.VAR_ASSIGNMENT,val_peek(2).sval,params);}
break;
case 39:
//#line 173 "yacc.y"
{yyval = val_peek(0);}
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
      if (yydebug) debug("After reduction, shifting from state 0 to state "+YYFINAL+"");
      yystate = YYFINAL;         //explicitly say we're done
      state_push(YYFINAL);       //and save it
      val_push(yyval);           //also save the semantic value of parsing
      if (yychar < 0)            //we want another character?
        {
        yychar = yylex();        //get next character
        if (yychar<0) yychar=0;  //clean, if necessary
        if (yydebug)
          yylexdebug(yystate,yychar);
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
      if (yydebug) debug("after reduction, shifting from state "+state_peek(0)+" to state "+yystate+"");
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
public void run()
{
  yyparse();
}
//## end of method run() ########################################



//## Constructors ###############################################
/**
 * Default constructor.  Turn off with -Jnoconstruct .

 */
public Parser()
{
  //nothing to do
}


/**
 * Create a parser, setting the debug to true or false.
 * @param debugMe true for debugging, false for no debug.
 */
public Parser(boolean debugMe)
{
  yydebug=debugMe;
}
//###############################################################



}
//################### END OF CLASS ##############################
