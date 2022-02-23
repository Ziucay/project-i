package compiler;//### This file created by BYACC 1.8(/Java extension  1.15)
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


import java.util.List;

public class Parser
{
  List<Token> tokens;
  int tokenPointer;

  void setTokens(List<Token> tokens)
  {
    this.tokens = tokens;
    this.tokenPointer = 0;
  }

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
    0,    0,    1,    1,    3,    3,    3,    4,    2,    2,
    7,    7,    7,    9,    5,    5,    5,   10,   10,   10,
   12,   13,   13,   11,    8,    8,   14,   14,   14,   14,
   14,   15,   16,   21,   21,   17,   18,   22,   22,   19,
   19,    6,    6,    6,    6,   23,   23,   23,   23,   24,
   24,   24,   24,   24,   24,   25,   25,   25,   26,   26,
   27,   27,   28,   28,   28,   28,   28,   20,   20,   20,
};
final static short yylen[] = {                            2,
    1,    1,    1,    1,    4,    6,    4,    4,    8,   10,
    0,    1,    3,    3,    1,    1,    1,    1,    1,    1,
    5,    1,    3,    5,    1,    1,    1,    1,    1,    1,
    1,    3,    4,    1,    3,    5,    6,    4,    5,    5,
    7,    1,    3,    3,    3,    1,    3,    3,    3,    3,
    3,    3,    3,    3,    3,    3,    3,    3,    3,    3,
    1,    3,    1,    1,    1,    1,    1,    1,    3,    4,
};
final static short yydefred[] = {                         0,
    0,    0,    0,    0,    1,    2,    3,    4,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,   20,
   18,   19,    0,    8,   15,   16,   17,    0,    0,    0,
   63,   64,   66,   65,    7,   67,   42,    0,    0,    0,
    0,   61,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,   14,    0,    0,   13,    0,
    0,    0,    6,   62,    0,   69,   43,    0,   44,    0,
   45,    0,   54,   55,   52,   53,   50,   51,   57,   56,
   58,   60,   59,    0,    0,    0,    0,    0,   25,    0,
   26,   27,   28,   29,   30,   31,    0,    0,    0,    0,
   70,    0,    0,    0,    0,    0,    0,    0,    0,    9,
    0,   24,   23,   21,   47,   48,   49,    0,    0,    0,
    0,    0,    0,    0,   32,   10,    0,   33,    0,    0,
    0,    0,    0,   35,    0,    0,    0,    0,   40,   36,
    0,   38,   37,    0,   39,   41,
};
final static short yydgoto[] = {                          4,
   99,    6,    7,    8,   24,  129,   17,  100,   18,   25,
   26,   27,   72,  101,  102,  103,  104,  105,  106,   36,
  130,  132,   37,   38,   39,   40,   41,   42,
};
final static short yysindex[] = {                      -257,
 -268, -262, -260,    0,    0,    0,    0,    0, -234, -258,
 -263, -236, -209, -209, -235, -230, -208, -205, -197,    0,
    0,    0, -185,    0,    0,    0,    0, -221, -235, -240,
    0,    0,    0,    0,    0,    0,    0, -274, -174, -207,
 -180,    0, -209, -261, -236, -235, -232, -235, -181, -235,
 -183, -235, -235, -235, -235, -235, -235, -235, -235, -235,
 -235, -235, -235, -235, -235,    0, -209, -219,    0, -146,
 -143, -137,    0,    0, -139,    0,    0, -272,    0, -272,
    0, -272,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0, -172, -251, -155, -235, -235,    0, -165,
    0,    0,    0,    0,    0,    0, -187, -209, -232, -163,
    0, -235, -235, -235, -219, -235, -166, -177, -164,    0,
 -235,    0,    0,    0,    0,    0,    0, -159, -130, -127,
 -237, -161, -219, -219,    0,    0, -235,    0, -235, -138,
 -219, -202, -153,    0, -136, -235, -151, -219,    0,    0,
 -235,    0,    0, -149,    0,    0,
};
final static short yyrindex[] = {                         0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0, -119,    0,    0,    0,    0,    0, -118,    0,    0,
    0,    0,    0,    0,    0,    0,    0,   27,    0,    1,
    0,    0,    0,    0,    0,    0,    0,   21,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
 -113,    0,    0,    0,    0,    0,    0,   21,    0,   21,
    0,   21,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0, -168,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0, -115,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,
};
final static short yygindex[] = {                         0,
  148,    0,  -45,    0,  -11,  -15,    0, -104,  104,    0,
    0,    0,   41,    0,    0,    0,    0,    0,    0,  -64,
   14,    0,   51,  -47,   52,   56,   49,    0,
};
final static int YYTABLESIZE=328;
static short yytable[];
static { yytable();}
static void yytable(){
yytable = new short[]{                         35,
   68,   71,   28,  107,   78,   80,   82,  116,   50,    9,
  128,   51,   52,   49,  112,   10,   14,   11,   67,   50,
   46,   29,   51,   29,   12,   53,    5,  113,  142,  143,
   70,   66,   73,   15,   75,   68,  147,   54,   13,  114,
   30,   16,   30,  154,   31,   32,   31,   32,    1,   43,
  107,    2,    3,   44,   33,   94,   33,   45,   95,   61,
   62,   63,   46,   71,  125,  126,  127,  139,  107,  107,
   34,   47,   34,   96,   97,   48,  107,    3,   19,   20,
   74,  118,  119,  107,   64,   65,   21,  148,  149,    2,
    3,   98,   22,   23,   76,   55,  122,   56,   57,   58,
   59,   60,   77,   79,   81,  135,   83,   84,   85,   86,
   87,   88,   92,   93,  108,  140,   89,   90,   91,  109,
  110,  111,  117,  145,  115,  120,  121,  124,  131,  133,
  152,  136,  137,  134,  138,  155,  141,  150,  146,  153,
  151,  156,   11,   12,   22,   68,   34,    5,   69,  123,
  144,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,   68,    0,
    0,   68,   68,    0,    0,   68,   68,   68,   68,   68,
   68,    0,   68,   68,   68,   68,   68,   68,   46,    0,
    0,   46,   46,   46,    5,    0,    0,   68,    0,    5,
   68,   68,    0,    0,    0,    0,    0,   46,   68,    0,
   68,    0,    0,    0,    0,    0,    0,   68,    0,    0,
   46,   46,   68,    0,    0,    0,    5,    5,   46,    0,
    0,    0,    0,    0,    0,    0,    0,   46,
};
}
static short yycheck[];
static { yycheck(); }
static void yycheck() {
yycheck = new short[] {                         15,
    0,   47,   14,   68,   52,   53,   54,  259,  260,  278,
  115,  263,  287,   29,  287,  278,  280,  278,  280,  260,
    0,  259,  263,  259,  259,  300,    0,  300,  133,  134,
   46,   43,   48,  297,   50,  297,  141,  312,  297,  312,
  278,  278,  278,  148,  282,  283,  282,  283,  306,  280,
  115,  309,  310,  262,  292,   67,  292,  263,  278,  267,
  268,  269,  260,  109,  112,  113,  114,  305,  133,  134,
  308,  257,  308,  293,  294,  297,  141,  310,  288,  289,
  262,   97,   98,  148,  265,  266,  296,  290,  291,  309,
  310,  311,  302,  303,  278,  270,  108,  272,  273,  274,
  275,  276,   52,   53,   54,  121,   55,   56,   57,   58,
   59,   60,   64,   65,  261,  131,   61,   62,   63,  263,
  258,  261,  278,  139,  297,  291,  314,  291,  295,  307,
  146,  291,  263,  298,  262,  151,  298,  291,  277,  291,
  277,  291,  262,  262,  258,  314,  262,    0,   45,  109,
  137,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  258,   -1,
   -1,  261,  262,   -1,   -1,  265,  266,  267,  268,  269,
  270,   -1,  272,  273,  274,  275,  276,  277,  258,   -1,
   -1,  261,  262,  263,  258,   -1,   -1,  287,   -1,  263,
  290,  291,   -1,   -1,   -1,   -1,   -1,  277,  298,   -1,
  300,   -1,   -1,   -1,   -1,   -1,   -1,  307,   -1,   -1,
  290,  291,  312,   -1,   -1,   -1,  290,  291,  298,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,  307,
};
}
final static short YYFINAL=4;
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
"Program : SimpleDeclaration",
"Program : RoutineDeclaration",
"SimpleDeclaration : VariableDeclaration",
"SimpleDeclaration : TypeDeclaration",
"VariableDeclaration : VAR IDENTIFIER COLON Type",
"VariableDeclaration : VAR IDENTIFIER COLON Type IS Expression",
"VariableDeclaration : VAR IDENTIFIER IS Expression",
"TypeDeclaration : TYPE IDENTIFIER IS Type",
"RoutineDeclaration : ROUTINE IDENTIFIER LEFT_BRACE Parameters RIGHT_BRACE IS Body END",
"RoutineDeclaration : ROUTINE IDENTIFIER LEFT_BRACE Parameters RIGHT_BRACE COLON Type IS Body END",
"Parameters :",
"Parameters : ParameterDeclaration",
"Parameters : ParameterDeclaration COMMA ParameterDeclaration",
"ParameterDeclaration : IDENTIFIER COLON Type",
"Type : PrimitiveType",
"Type : ArrayType",
"Type : RecordType",
"PrimitiveType : INTEGER",
"PrimitiveType : REAL",
"PrimitiveType : BOOLEAN",
"RecordType : RECORD LEFT_PAREN Variables RIGHT_PAREN END",
"Variables : VariableDeclaration",
"Variables : VariableDeclaration COMMA Variables",
"ArrayType : ARRAY LEFT_SQUARE_BRACE Expression RIGHT_SQUARE_BRACE Type",
"Body : SimpleDeclaration",
"Body : Statement",
"Statement : Assignment",
"Statement : RoutineCall",
"Statement : WhileLoop",
"Statement : ForLoop",
"Statement : IfStatement",
"Assignment : ModifiablePrimary WALRUS Expression",
"RoutineCall : IDENTIFIER LEFT_BRACE Expressions RIGHT_BRACE",
"Expressions : Expression",
"Expressions : Expression COMMA Expressions",
"WhileLoop : WHILE Expression LOOP Body END",
"ForLoop : FOR IDENTIFIER Range LOOP Body END",
"Range : IN Expression DOT_DOT Expression",
"Range : IN REVERSE Expression DOT_DOT Expression",
"IfStatement : IF Expression THEN Body END",
"IfStatement : IF Expression THEN Body ELSE Body END",
"Expression : Relations",
"Expression : Relation AND Relations",
"Expression : Relation OR Relations",
"Expression : Relation XOR Relations",
"Relations : Relation",
"Relations : Relation AND Relation",
"Relations : Relation OR Relation",
"Relations : Relation XOR Relation",
"Relation : Simple LESS Simple",
"Relation : Simple LESS_EQUAL Simple",
"Relation : Simple GREATER Simple",
"Relation : Simple GREATER_EQUAL Simple",
"Relation : Simple EQUAL Simple",
"Relation : Simple SLASH_EQUAL Simple",
"Simple : Factor STAR Factor",
"Simple : Factor SLASH Factor",
"Simple : Factor PERCENT Factor",
"Factor : Summand PLUS Summand",
"Factor : Summand MINUS Summand",
"Summand : Primary",
"Summand : LEFT_BRACE Expression RIGHT_BRACE",
"Primary : TYPE_INTEGER",
"Primary : TYPE_REAL",
"Primary : TRUE",
"Primary : FALSE",
"Primary : ModifiablePrimary",
"ModifiablePrimary : IDENTIFIER",
"ModifiablePrimary : IDENTIFIER COMMA IDENTIFIER",
"ModifiablePrimary : IDENTIFIER LEFT_SQUARE_BRACE Expression RIGHT_SQUARE_BRACE",
};

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
        debug("yystate: " + yystate);
      yyn = yysindex[yystate];  //get amount to shift by (shift index)
        debug("yyn: " + yyn);
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
        debug("yyn 2: " + yyn);
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

  private void yyerror(String syntax_error) {
  }

  private int yylex() {
    //TODO: Implement function
    this.yylval = new ParserVal(tokens.get(tokenPointer).lexeme);
    tokenPointer++;
    return tokens.get(tokenPointer - 1).TokenTypeToInt();
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
